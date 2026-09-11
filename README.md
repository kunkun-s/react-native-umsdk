# react-native-umsdk

友盟 SDK 的 React Native 封装，包含三部分能力：

| 模块 | 说明 | 友盟 SDK |
|---|---|---|
| **Push** | 离线推送、通知栏消息、点击唤起、自定义通知样式 | U-Push |
| **Share** | 分享到微信/朋友圈/QQ、第三方登录授权 | UMShare |
| **UMSdk** | 页面统计、账号统计、初始化 | U-App / UMCommon |

**同时支持新架构（TurboModule / Bridgeless）和旧架构**，通过宿主工程的 `newArchEnabled` 编译期自动切换实现，JS 侧 API 完全一致。

---

## 环境要求

| | 版本 |
|---|---|
| React Native | 0.71+（新架构需 0.73+，本项目在 0.84.1 验证） |
| iOS | 11.0+ |
| Android | minSdk 17+（推送厂商通道建议 21+） |

> 新架构下本库通过 `codegenConfig` 生成 TurboModule 代码，**修改 `specs/NativeUMSdkModule.ts` 后必须重新执行 `pod install` / `gradlew clean`**，否则 JS 与原生签名不一致会直接报错。

---

## 安装

```bash
npm install react-native-umsdk --save
# 或
yarn add react-native-umsdk
```

### iOS

```bash
cd ios && pod install
```

本库已在 podspec 中通过 `install_modules_dependencies(s)` 接入新架构 codegen，无需额外配置 Podfile。

### Android

Gradle 自动链接（autolinking），确认 `android/settings.gradle` 里有：

```gradle
apply from: file("../node_modules/@react-native-community/cli-platform-android/native_modules.gradle")
applyNativeModulesSettingsGradle(settings)
```

---

## 一、初始化

友盟要求**必须在用户同意隐私协议之后**才能正式初始化，之前只能做"预初始化"（预初始化不采集任何数据，仅加快后续初始化速度）。

### Android

**1) 预初始化** —— 在 `MainApplication.onCreate` 中调用（此时不需要用户同意）：

```java
import com.kk.rnumsdk.RNUmsdkImpl;

RNUmsdkImpl umsdk = new RNUmsdkImpl(context);
umsdk.preInitUMSDK("你的appkey", "渠道名");
```

**2) 正式初始化** —— 在用户点击"同意隐私协议"之后调用：

```java
umsdk.initUMSDK("你的appkey", "渠道名", "Umeng推送secret");
```

> `secret` 是友盟的 App Secret（不是微信/QQ 的），设备类型库内部固定为 `DEVICE_TYPE_PHONE`。
> iOS 侧此参数无意义，可传空字符串。

### iOS

iOS 没有隐私协议相关的强制要求，且初始化必须在冷启动时尽早完成（推送需要拿到 `deviceToken`）。
**推荐在 `AppDelegate` 里初始化**：

```objc
#import <RNUmsdk/RNUMSdkImpl.h>
#import <RNUmsdk/RNUMShare.h>
#import <RNUmsdk/RNUMPush.h>

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    [[RNUMSdkImpl sharedInstanceDelegate] initUMSDK:@"你的appkey" channel:@"App Store" secret:@""];
    // ... 分享配置、推送配置见下文
    return [super application:application didFinishLaunchingWithOptions:launchOptions];
}
```

`preInitUMSDK` 在 iOS 上是空实现，调用无副作用。

### JS 侧初始化（可选）

应用运行中才拿到 appkey / 渠道（比如从服务端下发），也可以在 JS 里初始化：

```js
import { UMSdk } from 'react-native-umsdk';

UMSdk.initUMSDK('你的appkey', '渠道名', '');   // 三端通用
```

> ⚠️ **iOS 首次冷启动的推送必须依赖 AppDelegate 初始化**。JS 初始化要等 bundle 加载完，在此之前到达的推送拿不到 `deviceToken`，会丢失。

---

## 二、推送（Push）

### iOS 配置

**1) 打开 Xcode 能力开关**

`Signing & Capabilities` → `+ Capability` 添加：

- `Push Notifications`
- `Background Modes` → 勾选 `Remote notifications`

**2) 启动时注册推送**

```objc
- (void)configUSharePlatforms:(NSDictionary *)launchOptions
{
    // Push 注册（必须传 launchOptions，用于冷启动时取出点击的通知）
    [RNUMPush initUpus:launchOptions delegate:self];
}
```

**3) 实现 `UNUserNotificationCenterDelegate`**

AppDelegate 声明遵循协议，并实现下面 **3 个方法**（缺一不可）：

```objc
@interface AppDelegate () <UNUserNotificationCenterDelegate>
@end

// ① 拿到 deviceToken（必须保存，否则 JS 端取不到）
- (void)application:(UIApplication *)application
    didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
    [RNUMPush saveDeviaceToken:deviceToken];
}

// ② APP 在前台时收到通知（此处由库决定是否弹系统横幅）
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler
{
    [RNUMPush userNotificationCenter:center
             willPresentNotification:notification
               withCompletionHandler:completionHandler];
}

// ③ 用户点击通知（冷启动 / 后台唤起都走这里，且是 APP 关闭时收到通知的入口）
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
    didReceiveNotificationResponse:(UNNotificationResponse *)response
             withCompletionHandler:(void (^)(void))completionHandler
{
    [RNUMPush userNotificationCenter:center
      didReceiveNotificationResponse:response
               withCompletionHandler:completionHandler];
}
```

> **注意方法 ③ 的 `completionHandler` 必须被调用**（库内部已处理）。不调用的话系统会认为这次点击没有被处理，后台唤起时进程可能被延迟回收，表现为"点击通知偶发打不开"。

### Android 配置

**1) 初始化推送** —— 必须在 `UMConfigure.init` 之后调用：

```java
import com.kk.rnumsdk.RNUMPush;
import com.kk.rnumsdk.UMPUSHCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.entity.UMessage;

RNUMPush umPush = new RNUMPush();
umPush.initUpush(context, new UMPUSHCallback() {

    // 收到自定义消息 / 通知送达（可在此做业务处理）
    @Override
    public void sendDDUMessageHandler(WritableMap uMessage) { }

    // 自定义通知栏样式，返回 null 表示用友盟默认样式
    @Override
    public Notification getNotification(Context context, UMessage msg) {
        // 例如在这里按 msg.extra.get("audioStyle") 播放不同的提示音
        return null;
    }

    // 拿到 deviceToken，需要上传给自己的服务端
    @Override
    public void deviceTokenBack(String deviceToken) { }
});

PushAgent.getInstance(context).onAppStart();
```

**2) 厂商通道**（可选，用于提升离线送达率）

华为、小米、OPPO、vivo、荣耀等厂商通道需要在对应平台申请参数，并在 `AndroidManifest.xml` 的 `<application>` 中配置：

```xml
<meta-data android:name="com.huawei.hms.client.appid" android:value="appid=你的华为appid"/>
<meta-data android:name="com.vivo.push.api_key"       android:value="你的vivo api_key"/>
<meta-data android:name="com.vivo.push.app_id"        android:value="你的vivo app_id"/>
<meta-data android:name="com.hihonor.push.app_id"     android:value="你的荣耀app_id"/>
<meta-data android:name="UMENG_APPKEY"                android:value="你的友盟appkey"/>
<meta-data android:name="UMENG_CHANNEL"               android:value="${UMENG_CHANNEL}"/>
```

小米/OPPO 的通道参数通过 `MiPushRegistar` / `OppoRegister` 等类在代码中注册，具体参考友盟官方文档。

### JS 监听推送

```js
import { Push } from 'react-native-umsdk';

// 在组件挂载时注册，卸载时务必移除，否则会重复回调
useEffect(() => {
    const listener = Push.userNotificationCenter((pushData) => {
        console.log('收到推送', pushData);
        // { title: '标题', text: '内容', extra: { url: 'https://...', hide: '0' } }
    });

    return () => listener?.remove?.();
}, []);
```

`userNotificationCenter` 返回一个 `EmitterSubscription`，**必须在合适的时机调用 `remove()`**。多个页面都注册的话，每个页面都会收到回调。

### 推送数据格式

回调收到的对象结构：

```js
{
    title: '通知标题',
    text:  '通知内容',
    extra: { /* 已解析成对象的自定义字段 */ }
}
```

**关于 `extra`：**

- 原生下发的是 **JSON 字符串**，JS 侧已自动 `JSON.parse` 成对象，直接 `pushData.extra.url` 取值即可，**不需要二次解析**。
- 解析失败时 `extra` 会是 `{}`，取值请用可选链 `pushData?.extra?.url`。
- **`extra.hide === "1"` 时该条推送不会触发回调**，用于"APP 内不显示弹窗"的场景。

### 获取 deviceToken

```js
import { Push } from 'react-native-umsdk';

Push.getDeviceToken((deviceToken) => {
    // deviceToken 为空字符串表示还没注册成功，需要稍后重试
    if (deviceToken) {
        upDateDeviceToken(deviceToken);   // 上传给自己的服务端
    }
});
```

> 启动后 `deviceToken` 需要一定时间才能从系统拿到。**建议在 `deviceTokenBack`（Android）或轮询调用 `getDeviceToken`（iOS）拿到后再上传**，不要启动后立即调一次就放弃。

### 获取冷启动暂存的通知

APP 被杀死后收到推送 → 用户点击 → 冷启动，此时推送数据会先被原生暂存，等 JS 准备好了再取：

```js
import { Push } from 'react-native-umsdk';

// 与 userNotificationCenter 一起调用，覆盖"冷启动"这一场景
Push.getNonification((notification) => {
    if (notification && Object.keys(notification).length > 0) {
        // notification 结构与 userNotificationCenter 回调一致
        // 没有暂存消息时返回空对象 {}，不会一直等待
    }
});
```

> 因为通知在**送达时**就会通过 `userNotificationCenter` 事件推给 JS，所以这份暂存数据**只服务于"推送先于 JS 加载"的冷启动场景**，两者不会重复。

---

## 三、分享（Share）

> **分享不返回结果回调。** 无论成功、失败还是取消，都不会回调到 JS 侧，失败仅原生日志留痕。这是有意为之——分享结果的可靠性在各平台差异太大，业务上通常只需要"调起"。

### iOS 配置

**1) 配置平台参数**

```objc
RNUMShareModel *shareModel = [[RNUMShareModel alloc] init];

PlatformMode *wxModel = [[PlatformMode alloc] init];
wxModel.appKey        = @"wx开头的微信appid";
wxModel.appSecret     = @"微信appsecret";
wxModel.universalLink = @"https://你的域名/";        // 微信 Universal Link

PlatformMode *qqModel = [[PlatformMode alloc] init];
qqModel.appKey        = @"纯数字的QQ appid";
qqModel.appSecret     = @"QQ appkey";
qqModel.universalLink = @"https://你的域名/qq_conn/你的QQAPPID";  // QQ 固定格式

shareModel.wxModel = wxModel;
shareModel.qqModel = qqModel;

[RNUMShare configUSharePlatforms:shareModel];
```

**2) 配置 Universal Link（最容易漏，微信分享必需）**

Universal Link 是 iOS 上 App 之间互相唤起/回跳的机制，微信和 QQ 都强制要求。
**漏配的表现是：分享能调起微信，但从微信返回时回不到本 App**，或者微信直接拒绝调起。

配置分 4 步，缺一不可：

**① Apple Developer 后台**

`Certificates, Identifiers & Profiles` → `Identifiers` → 找到本 App 的 App ID → 勾选 **Associated Domains** → 重新生成 Provisioning Profile 并下载（改了 App ID 后旧 profile 会失效，必须重新生成）。

**② Xcode 开启 Associated Domains**

`Signing & Capabilities` → `+ Capability` → 添加 `Associated Domains`，填入：

```
applinks:m.你的域名
applinks:www.你的域名
```

Xcode 会自动写入工程的 `<Target>.entitlements`：

```xml
<key>com.apple.developer.associated-domains</key>
<array>
    <string>applinks:m.你的域名</string>
    <string>applinks:www.你的域名</string>
</array>
```

> 推送依赖的 `aps-environment` 也在同一个 entitlements 文件里（由 `Push Notifications` 能力生成），两者可以共存。

**③ 服务器部署 AASA 文件**

在 `https://m.你的域名/.well-known/apple-app-site-association` 部署一个**没有扩展名**的文件：

```json
{
  "applinks": {
    "apps": [],
    "details": [
      {
        "appIDs": ["你的TeamID.com.你的bundleid"],
        "components": [{ "/": "*", "comment": "匹配该域名下所有路径" }]
      }
    ]
  }
}
```

硬性要求（任何一条不满足，iOS 都不会拉起 App）：

| 要求 | 说明 |
|---|---|
| HTTPS | 必须 HTTPS，且**不能有任何重定向** |
| 无扩展名 | 文件名就叫 `apple-app-site-association`，不是 `.json` |
| Content-Type | 必须是 `application/json` |
| 路径 | 必须在 `/.well-known/` 目录下（老版本 iOS 也认根目录，建议两处都放） |
| 匿名可访问 | 不能有登录鉴权、不能带查询参数 |

**④ 微信开放平台 / QQ 互联后台填写**

| 平台 | 填写位置 | 格式要求 |
|---|---|---|
| 微信开放平台 | 应用详情 → `Universal Links` | `https://m.你的域名/`，**必须以 `/` 结尾**，不能带路径和参数 |
| QQ 互联 | 应用详情 → `iOS Universal Link` | `https://m.你的域名/qq_conn/你的QQAPPID`（腾讯固定格式，末尾不带 `/`） |

代码里 `configUSharePlatforms` 传的 `universalLink`，**必须和后台填写的字符串完全一致**（包括末尾斜杠）。

**验证方法**

1. 在 iPhone 的 Safari 里直接访问 `https://m.你的域名/.well-known/apple-app-site-association`，能直接看到 JSON 内容，就说明部署正确。
2. 在备忘录里输入 `https://m.你的域名/任意路径`，长按应该出现"在 XXX App 中打开"的选项（Safari 地址栏直接访问不会触发）。

如果 JSON 没问题但拉不起 App，多半是 **iOS 的 AASA 缓存**——系统只在 App **首次安装**时拉取一次并长期缓存，改完 AASA 必须**删除 App 重装**。

开发阶段想跳过缓存，entitlements 里可以写 `applinks:m.你的域名?mode=developer`，同时在设备的 `设置 → 开发者 → Associated Domains Development` 里打开开关。**上架前务必把 `?mode=developer` 去掉。**

<details>
<summary>参考：本仓库宿主 App「点到」的真实配置</summary>

| 位置 | 值 |
|---|---|
| `点到.entitlements` | `applinks:www.diandao.org`、`applinks:m.diandao.org` |
| `diandao.pch` | `#define WX_UNIVERSAL_LINK @"https://m.diandao.org/"` |
| `diandao.pch` | `#define QQ_UNIVERSAL_LINK @"https://m.diandao.org/qq_conn/1104206145"` |

注意微信的 UL 末尾**带** `/`，QQ 的**不带** —— 这正是两个平台各自的格式要求，不要统一。
新增 App 接入时把域名和 QQ appid 换掉即可。

</details>

**3) 处理回调 URL**

```objc
// 微信 / QQ 通过 URL Scheme 回来
- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options
{
    return [[UMSocialManager defaultManager] handleOpenURL:url options:options];
}

// 微信 / QQ 通过 Universal Link 回来
- (BOOL)application:(UIApplication *)application
    continueUserActivity:(NSUserActivity *)userActivity
      restorationHandler:(void (^)(NSArray<id<UIUserActivityRestoring>> * _Nullable))restorationHandler
{
    [[UMSocialManager defaultManager] handleUniversalLink:userActivity options:nil];
    return YES;
}
```

**4) Info.plist**

```xml
<!-- 允许查询/跳转的 App -->
<key>LSApplicationQueriesSchemes</key>
<array>
    <string>weixin</string>
    <string>weixinULAPI</string>
    <string>mqq</string>
    <string>mqqapi</string>
    <string>mqqwpa</string>
    <string>mqqbrowser</string>
</array>

<!-- 微信/QQ 回跳本 App -->
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLSchemes</key>
        <array>
            <string>wx你的微信appid</string>
            <string>tencent你的QQappid</string>
        </array>
    </dict>
</array>
```

> `isInstall` 依赖 `LSApplicationQueriesSchemes`，**没配的话永远返回 false**。

> **iOS 微信分享走的是微信原生 SDK，不经友盟。**
> 因为友盟 6.10.x 调起微信存在问题，`platformType` 为 `1`/`2`（微信聊天/朋友圈）时内部改用 `DDWXShare` 直接调用 `WXApi sendReq:`；只有 QQ（`4`）才走 `UMSocialManager`。
> 微信能调起的前提是 `configUSharePlatforms` 里正确配置了 `wxModel.appKey` 和 `universalLink`（友盟在 `setPlaform` 时会用它们注册微信 SDK）——**`universalLink` 必须与微信开放平台上填写的一致**。
> 另外这段实现里有 `http://www.diandao.org/` 的兜底地址，宿主未传 `t_url` 时会跳到这里，接入新 App 时注意替换。

### Android 配置

在 `Application.onCreate` 或初始化时配置平台参数：

```java
import com.umeng.socialize.PlatformConfig;

PlatformConfig.setWeixin("wx你的微信appid", "微信appsecret");
PlatformConfig.setQQZone("纯数字的QQ appid", "QQ appkey");

// FileProvider 的 authority，必须和 AndroidManifest 里 provider 的 authorities 一致
PlatformConfig.setFileProvider(context.getPackageName() + ".fileprovider");
```

**Manifest 需要声明的内容：**

```xml
<!-- 微信分享回调 Activity（不声明则分享后无法回到 App） -->
<activity
    android:name="你的包名.wxapi.WXEntryActivity"
    android:exported="true"
    android:launchMode="singleTask"
    android:taskAffinity="你的包名"
    android:theme="@android:style/Theme.Translucent.NoTitleBar"/>

<!-- QQ 回调，scheme 必须换成真实的 QQ appid -->
<activity
    android:name="com.tencent.tauth.AuthActivity"
    android:launchMode="singleTask"
    android:noHistory="true"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
        <data android:scheme="tencent你的QQappid"/>
    </intent-filter>
</activity>

<!-- Android 11+ 查询其他 App 需要 -->
<queries>
    <package android:name="com.tencent.mm"/>        <!-- 微信 -->
    <package android:name="com.tencent.mobileqq"/>  <!-- QQ -->
</queries>
```

`WXEntryActivity` 的内容：

```java
package 你的包名.wxapi;

import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {
}
```

> 本库自带的 manifest 已声明了 `com.kk.rnumsdk.wxapi.WXEntryActivity` 和 `com.tencent.tauth.AuthActivity`（QQ scheme 是占位值 `tencent100424468`），宿主工程仍需按上面配置声明自己的 `WXEntryActivity` 和真实 QQ scheme，或用 `tools:replace` 覆盖库的占位值。

### JS 发起分享

```js
import { Share } from 'react-native-umsdk';

// 分享网页
Share.shareToPlatform(1, 'Web', {
    t_url:   'https://www.example.com/share?id=123',
    title:   '分享标题',
    content: '分享描述',
    img_path:'https://cdn.example.com/thumb.jpg',
});

// 分享纯图片
Share.shareToPlatform(2, 'Image', {
    title:  '测试分享',
    poster: 'https://cdn.example.com/poster.jpg',
});

// 分享微信小程序
Share.shareToPlatform(1, 'MiniProgram', {
    t_url:    'https://www.example.com/',   // 低版本微信兜底网页
    title:    '小程序标题',
    content:  '小程序描述',
    img_path: 'https://cdn.example.com/thumb.jpg',
    path:     'pages/index/index',          // 小程序页面路径
    userName: 'gh_xxxxxxxxxxxx',            // 小程序原始 id
});
```

**参数说明：**

| 参数 | 类型 | 说明 |
|---|---|---|
| `platformType` | `number` | `1` 微信聊天 · `2` 微信朋友圈 · `4` QQ |
| `shareType` | `string` | `Image` 纯图片 · `MiniProgram` 小程序 · 其它值一律按网页处理 |

**`params` 字段：**

| 字段 | 说明 |
|---|---|
| `t_url` | 网页地址（分享网页时必填） |
| `title` | 标题 |
| `content` | 描述 |
| `img_path` | 缩略图地址，不传则用宿主 App 的 `R.drawable.share` |
| `poster` | **纯图片分享**的图片地址（`shareType = 'Image'` 时使用） |
| `path` | 小程序页面路径（`shareType = 'MiniProgram'` 时使用） |
| `userName` | 小程序原始 id（`shareType = 'MiniProgram'` 时使用） |

> **图片路径注意**：分享图片如果用的是自家 CDN，且遇到 SSL 证书问题，可以像示例一样把 `https` 换成 `http`，友盟 SDK 对部分 CDN 证书兼容性较差。

---

## 四、第三方登录授权（Auth）

```js
import { UMSdk } from 'react-native-umsdk';

UMSdk.auth(1, (code, result, message) => {
    if (code === 200) {
        // result 是用户信息对象（iOS 为 object，Android 为 map 序列化结果）
        console.log(result.uid, result.name, result.iconurl);
    } else {
        console.error('授权失败', code, message);
    }
});
```

**回调参数：**

| 参数 | 说明 |
|---|---|
| `code` | `200` 成功 · `2` 用户取消（仅 Android）· 其它为失败错误码 |
| `result` | 成功时为用户信息，失败/取消时为 `{}` |
| `message` | 失败原因 |

`result` 中可能包含的字段（以微信为例）：`uid` `openid` `unionid` `accessToken` `refreshToken` `expiration` `name` `iconurl` `gender` `city` `province` `country` `headimgurl`。

> 平台参数 `1` 和 `2` 在授权场景下都会走**微信聊天**授权（朋友圈不支持登录）。

---

## 五、检测目标 App 是否安装

```js
import { UMSdk } from 'react-native-umsdk';

UMSdk.isInstall('WX_SESSION', (installed) => {
    if (!installed) {
        Toast.show('未安装微信');
        return;
    }
    doShare();
});
```

**`platformType` 取值**（字符串）：

| 值 | 含义 |
|---|---|
| `'WX_SESSION'` | 微信聊天 |
| `'WX_LINE'` | 微信朋友圈 |
| `'QQ'` | QQ |

为方便使用，也可以直接传数字，JS 侧会自动映射：`1 → WX_SESSION`、`2 → WX_LINE`、`4 → QQ`。

> ⚠️ **这里的编码和 `shareToPlatform` / `auth` 不同**，注意不要混用。iOS 的此接口依赖 `LSApplicationQueriesSchemes` 配置，未配置时恒为 `false`。

---

## 六、统计（U-App）

```js
import { UMSdk } from 'react-native-umsdk';

// 页面统计（需成对调用）
UMSdk.onPageStart('HomePage');
UMSdk.onPageEnd('HomePage');

// 账号统计：登录 / 退出登录时调用
UMSdk.profileSignInWithPUID(userId);   // 传入用户唯一标识
UMSdk.profileSignOff();
```

> `profileSignInWithPUID` 的 `puid` **不要用手机号或用户 token**，这个信息会上传到友盟后台，建议用 uid 或哈希值。

---

## API 一览

```js
import { Push, Share, UMSdk } from 'react-native-umsdk';
```

### UMSdk

| 方法 | 说明 |
|---|---|
| `preInitUMSDK(appkey, channel)` | 预初始化（**仅 Android 有效**，iOS 空实现） |
| `initUMSDK(appkey, channel, secret)` | 正式初始化，需在用户同意隐私协议后调用 |
| `auth(platformType, callback)` | 第三方登录授权 |
| `isInstall(platformType, callback)` | 检测目标 App 是否安装 |
| `onPageStart(viewName)` / `onPageEnd(viewName)` | 页面统计（成对调用） |
| `profileSignInWithPUID(puid)` / `profileSignOff()` | 账号统计 |

### Push

| 方法 | 说明 |
|---|---|
| `userNotificationCenter(callback)` | 注册推送监听，返回 `EmitterSubscription`，**记得 `remove()`** |
| `getDeviceToken(callback)` | 获取 deviceToken |
| `getNonification(callback)` | 获取冷启动暂存的推送消息，无数据时回调 `{}` |

### Share

| 方法 | 说明 |
|---|---|
| `shareToPlatform(platformType, shareType, params)` | 发起分享，**无回调** |

---

## 平台编码对照表

| 场景 | 值 |
|---|---|
| `shareToPlatform` / `auth` | `1` 微信聊天 · `2` 微信朋友圈 · `4` QQ |
| `isInstall` | `'WX_SESSION'` · `'WX_LINE'` · `'QQ'`（也接受 `1` / `2` / `4`） |

---

## 原生侧 API（宿主 App 调用）

### Android

```java
// 初始化
RNUmsdkImpl umsdk = new RNUmsdkImpl(context);
umsdk.preInitUMSDK(appkey, channel);
umsdk.initUMSDK(appkey, channel, secret);

// 推送（必须在 initUMSDK 之后）
RNUMPush umPush = new RNUMPush();
umPush.initUpush(context, umPushCallback);

// 自定义分享缩略图资源（默认使用 com.kk.rnumsdk 的 R.drawable.share）
RNUmsdkImpl.setApplicationResource(new 你的KKAppResource实现类());
```

自定义资源需要实现 `KKAppResource` 接口：

```java
public class DDAppResourceDefault implements KKAppResource {
    @Override
    public int getDrawableResourceId(String key) {
        if ("share".equals(key)) {
            return 你的包名.R.drawable.share;
        }
        return 0;
    }
}
```

### iOS

```objc
// 初始化
[[RNUMSdkImpl sharedInstanceDelegate] initUMSDK:appkey channel:channel secret:@""];

// 分享平台配置
[RNUMShare configUSharePlatforms:shareModel];

// 推送（AppDelegate 中）
[RNUMPush initUpus:launchOptions delegate:self];
[RNUMPush saveDeviaceToken:deviceToken];
[RNUMPush userNotificationCenter:center willPresentNotification:notification withCompletionHandler:completionHandler];
[RNUMPush userNotificationCenter:center didReceiveNotificationResponse:response withCompletionHandler:completionHandler];
```

---

## 常见问题

**Q：新架构下方法不执行 / 报 "method not found"**

改了 `specs/NativeUMSdkModule.ts` 后必须重新生成 codegen 产物：

```bash
# iOS
cd ios && rm -rf Pods && pod install
# Android
cd android && ./gradlew clean
```

**Q：Android 分享后点了微信，回来没有任何反应**

检查 `AndroidManifest.xml` 里的 `WXEntryActivity` 是否声明在 **`你的 applicationId.wxapi`** 包下，且 `exported="true"`。微信要求回调 Activity 必须在应用包名的 `wxapi` 子包中。

**Q：iOS 分享后无法回到 App**

按这个顺序排查（前两条占了绝大多数）：

1. **Universal Link 是否真的生效** —— 在备忘录里输入 `https://m.你的域名/test`，长按看有没有"在 XXX App 中打开"。没有就是 UL 没配好，回到「配置 Universal Link」逐条对：entitlements 里的 `applinks:`、AASA 文件是否可匿名访问且无重定向、后台填的 UL 与代码里是否完全一致。
2. **AASA 改过但没重装 App** —— iOS 只在 App 首次安装时拉取一次 AASA 并长期缓存，**必须删除 App 重装**才会重新拉取。
3. 检查 `CFBundleURLTypes` 里是否配了 `wx你的微信appid` 和 `tencent你的QQappid`
4. 检查 `openURL` 和 `continueUserActivity` 是否都转交给了 `UMSocialManager`

**Q：改了 AASA 文件，测试机还是老的行为**

见上一条第 2 点。另外可以用 Apple 的 [Associated Domains 验证工具](https://search.developer.apple.com/appsearch-validation-tool/) 或直接在 Safari 打开 AASA 地址确认服务端返回的是最新内容（注意 CDN 缓存）。

**Q：`isInstall` 永远返回 false**

- iOS：`LSApplicationQueriesSchemes` 里缺对应的 scheme（`weixin` / `mqq`）
- Android：Android 11+ 需要在 `<queries>` 里声明目标 App 的包名

**Q：iOS 冷启动点击通知，JS 拿不到数据**

按顺序排查：① `initUpus` 是否传了 `launchOptions` ② `didReceiveNotificationResponse` 是否实现并转交给 `RNUMPush` ③ 是否在 JS 准备好之后调用了 `getNonification`。

**Q：推送回调触发了两次**

`userNotificationCenter` 每次注册都会新增一个监听。同一个页面重复注册，或组件卸载时没有 `listener.remove()`，都会导致重复回调。

**Q：`pushData.extra.url` 取不到值**

`extra` 已经由 JS 侧解析成对象，不要再 `JSON.parse`。另外 `extra.hide === "1"` 的推送不会触发回调，如果推送被"吞掉"，先检查这个字段。

---

## License

MIT
