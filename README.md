
# react-native-umsdk

## Getting started

`$ npm install react-native-umsdk --save`


### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-umsdk` and add `RNUmsdk.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNUmsdk.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNUmsdkPackage;` to the imports at the top of the file
  - Add `new RNUmsdkPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-umsdk'
  	project(':react-native-umsdk').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-umsdk/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-umsdk')
  	```


## Usage

#### Android
1.先预初始化，在MainApplication.java 的onCreate 中调用 `RNUmsdkModule.preInitUMSDK`  
2.正式的初始化需要在用户同意过用户隐私协议之后才能调用UMSdk.js `initUMSDK`

#### ios端
ios暂时没有隐私相关问题，无需预初始化。<br>
1.可以在appDelegate `didFinishLaunchingWithOptions`方法汇中调用 `[ReactBridgeManager initUMSD];`<br>
2.或者可以在js端使用 UMSdk.js `initUMSDK`<br>



-----
### UMShare
1. 在`initUMSD`中配置share相关的参数
2. 使用 share.js 中 `shareToPlatform`直接发起分享

----
### UMPush
#### ios 
需要配置xcode，打开推送开关`Capabilities -> capability+ ` 添加 推送开关`Push Notifiications`和 后台推送权限`Background Modes - Remote notifiications`<br>
**保存devicetoken**<br>
ios
```OC
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(nonnull NSData *)deviceToken{
  
  [RNUMPush saveDeviaceToken:deviceToken];
}
```
**获取devicetoken**
```js
RNUMPush.getDeviceToken(()=>{})
```

默认不打开前台系统弹窗`setAutoAlert`，需要自己创建提示框
使用
1. 获取`deviceToken`,并上传服务端，
2. 通过push.js 中的 `userNotificationCenter`方法可以注册推送，并监听推送消息

