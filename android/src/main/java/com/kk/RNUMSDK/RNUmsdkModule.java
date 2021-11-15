
package com.kk.RNUMSDK;

import android.content.Context;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.umeng.commonsdk.UMConfigure;

public class RNUmsdkModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNUmsdkModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
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
}