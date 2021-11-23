
package com.kk.RNUMSDK;

import android.content.Context;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class RNUmsdkModule extends ReactContextBaseJavaModule {

  public static ReactApplicationContext rContext;

  public RNUmsdkModule(ReactApplicationContext reactContext) {
    super(reactContext);
    rContext = reactContext;
  }
  public static String doGetChannel(Context mContext) {
    //获取渠道号
//    ApplicationInfo mApplicationInfo = null;
//    try {
//      //获取 AndroidManifest.xml 中 application 标签内插入的 <meta-data android:name="UMENG_CHANNEL" android:value="umeng" />
//      //通过 UMENG_CHANNEL 获取到value：umeng
//
//      mApplicationInfo = mContext.getPackageManager().getApplicationInfo(
//              mContext.getPackageName(), PackageManager.GET_META_DATA);
//      return mApplicationInfo.metaData.getString("UMENG_CHANNEL");
//    } catch (PackageManager.NameNotFoundException e) {
//      e.printStackTrace();
//    }
    return "baseFrameAndroid";
  }
  public static void preInitUMSDK(Context context, String appkey, String channel){
    //友盟基础组件预初始化
    UMConfigure.preInit(context ,appkey, channel);

  }
  public static void initUMSDK(Context constext, String appkey, String channel, String secret){
    //正式初始化友盟基础组件，必须在用户隐私协议同意之后才可以。之前先试用预初始化方法
    UMConfigure.init(constext, appkey, channel, UMConfigure.DEVICE_TYPE_PHONE, secret);
//    // 支持在子进程中统计自定义事件 如果需要在某个子进程中统计自定义事件，则需保证在此子进程中进行SDK初始化。
//    UMConfigure.setProcessEvent(true);
  }

  @Override
  public String getName() {
    return "RNUMSdkBridge";
  }

  @ReactMethod
  public void umtest(final boolean platform, final Callback successCallback) {

    if (successCallback != null) {
      successCallback.invoke("React - call - Native - callBack - React");
    }
  }

  /**
   * 用户账户统计，配合onProfileSignOff
   * 账号统计，这里账户尽量不要使用手机号，或者是用户token，可以使用用户uid。因为这个用户信息会保存在UM第三番平台
   *
   * @param params { userID provider}
   */
  @ReactMethod
  public static void onProfileSignIn(final ReadableMap params) {

    if ( params.hasKey("userID") ) {
        if ( params.hasKey("provider")) {
          MobclickAgent.onProfileSignIn(params.getString("userID"), params.getString("provider"));
        } else {
          MobclickAgent.onProfileSignIn(params.getString("userID"));
        }
    }
  }
  /**
   * 账户统计退出登录时调用（退出账户，不是退出APP）
   */
  @ReactMethod
  public static void onProfileSignOff () {
    MobclickAgent.onProfileSignOff();
  }
  /**
   * 是否自动统计页面，默认手动
   * @param status
   */
  @ReactMethod
  public static void setPageAuto(Boolean status){
    if (status){
      // 自动采集选择（仅采集activity
      MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    } else {
      /**
       * 手动采集选择 （支持activity和非activity）
       * 由于我们使用的是RN，多数的UI页面都是使用React实现，因此我们使用手动采集
       */
      MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
    }

  }
  /**
   * 手动采集页面
   * 页面打开时调用onPageState（viewName，true）
   * 页面关闭时调用用onPageState（viewName，false）
   * @param viewName 页面名称（需要在um后台提前部署好）
   * @param status 当前页面是关闭还是打开
   */
  @ReactMethod
  public static void onPageState(String viewName, Boolean status) {
      if (status){
        MobclickAgent.onPageStart(viewName);

      } else {
        MobclickAgent.onPageEnd(viewName);
      }
  }

  /**
   * 自定义事件统计
   * @param eventID 事件ID （这个ID需要提前在um自定义事件后台创建）
   * @param params 这个事件自定义参数（例如加入描述、事件触发时间、或者统计一个网络请求事件的参数等）
   * 为方便使用者理解及使用，可通过显示名称进行重命名（支持中文），进入【应用设置-事件-编辑】进行操作
   * 参数上线是256个
   * 请不要将事件属性key以"_"开头。"_"开头的key属于SDK保留字
   * id、ts、du、ds、duration、pn、token、device_name、device_model 、device_brand、country、city、channel、province、appkey、app_version、access、launch、pre_app_version、terminate、no_first_pay、is_newpayer、first_pay_at、first_pay_level、first_pay_source、first_pay_user_level、first_pay_version、type、是保留字段，不能作为event id 及key的名称
   */
  @ReactMethod
  public static void onEventObject( String eventID, final ReadableMap params){

    MobclickAgent.onEventObject(rContext, eventID, params.toHashMap());
  }

}