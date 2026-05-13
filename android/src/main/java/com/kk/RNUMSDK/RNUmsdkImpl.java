package com.kk.rnumsdk;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import javax.annotation.Nullable;

public class RNUmsdkImpl {
    public static final String NAME = "RNUMSdkBridge"; //与NativeDDVerify.ts文件中的get<Spec>('RNDdverify') 保持一致
    public static Context reactContext;
    public static RNUMShare rn_um_share = null;
    public static KKAppResource AppResource = null;

    public RNUmsdkImpl(Context context){
        reactContext = context;
        //默认使用com.kk.rnumsdk的resource
        if (AppResource == null){
            AppResource = new KKAppResourceDefault();
        }
    }
    public static void sendEvent( WritableMap params){

        KKUMSdkEventEmitter.getInstance().sendEventWithName("userNotificationCenter", params);
    }
    /**
     * 设置制定的resource，覆盖默认的com.kk.rnumsdk的resource
     * @param nAppResource 在主程序包内新建符合协议KKAppResource的类
     */
    public static void setApplicationResource(KKAppResource nAppResource){
        AppResource = nAppResource;

    };

    public RNUMShare getShareModule(){
        if (rn_um_share == null && reactContext != null){
            rn_um_share = new RNUMShare(reactContext, AppResource);
        }
        return  rn_um_share;
    }
    //与初始化
    public void preInitUMSDK(String appkey, String channel){
        //友盟基础组件预初始化
        UMConfigure.preInit(reactContext.getApplicationContext() ,appkey, channel);
    };

    //正式初始化umsdk
    public void initUMSDK(String appkey, String channel, String secret){
        //RNUmsdkModule.initUMSDK(reactContext,"551243cefd98c5ceee00031e", channel,"75e1195e4d71f6588352ea62108106dc");
        //正式初始化友盟基础组件，必须在用户隐私协议同意之后才可以。之前先试用预初始化方法
        UMConfigure.init(reactContext.getApplicationContext(), appkey, channel, UMConfigure.DEVICE_TYPE_PHONE, secret);
    };

    //获取推送token，必须要在注册后才能获取到正确token
    public void getDeviceToken(Callback callback){
        RNUMPush.getDeviceToken(callback, reactContext.getApplicationContext());
    };
    //获取推送消息（主要是iOS需要）
    public void getNonification(Callback callback){
        RNUMPush.getNonification(callback, reactContext.getApplicationContext());
    }

    /**
     * 三方登录授权
     */
    public void auth( Activity ma, String platformType, Callback callback){
        RNUMShare sm = getShareModule();
        sm.auth(ma, Integer.parseInt(platformType),callback);
    };
    //分享
    public void shareToPlatform(Activity ma, String platformType, String shareType, ReadableMap params, @Nullable Callback callback){
        RNUMShare sm = getShareModule();
        sm.shareToPlatform(ma, Integer.parseInt(platformType),shareType,params,callback);
    };
    //判断平台是否存在
    public void isInstall(Activity ma, String platform, Callback callback){
        RNUMShare sm = getShareModule();
        sm.isInstall(ma, platform, callback);
    };

    /**
     * 统计
     * @param pageName
     */
    public void onPageStart(String pageName) {
        AnalyticsUtil.onPageStart(pageName);
    }
    public void onPageEnd(String pageName) {
        AnalyticsUtil.onPageEnd(pageName);
    }
    public void profileSignInWithPUID(String puid) {
        AnalyticsUtil.profileSignInWithPUID(puid);
    }
    public void profileSignOff() {
        AnalyticsUtil.profileSignOff();
    }
}
