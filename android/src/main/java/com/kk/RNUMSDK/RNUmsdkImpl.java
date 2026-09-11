package com.kk.rnumsdk;

import android.app.Activity;
import android.content.Context;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.umeng.commonsdk.UMConfigure;

public class RNUmsdkImpl {
    public static final String NAME = "RNUMSdkBridge"; //与specs/NativeUMSdkModule.ts 中的 get<Spec>('RNUMSdkBridge') 保持一致
    /**
     * 宿主可以直接赋值。注意不要长期持有 Activity：
     * 内部一律通过 {@link #getAppContext()} 取值，读到时会把这里的 Activity 换成 ApplicationContext。
     */
    public static Context reactContext;
    public static RNUMShare rn_um_share = null;
    public static KKAppResource AppResource = null;

    public RNUmsdkImpl(Context context){
        reactContext = toAppContext(context);
        //默认使用com.kk.rnumsdk的resource
        if (AppResource == null){
            AppResource = new KKAppResourceDefault();
        }
    }

    /**
     * 统一取 ApplicationContext。
     * 宿主为了分享传进来的可能是 Activity（RNUmsdkImpl.reactContext = activity），
     * 静态字段长期持有 Activity 会泄漏，这里在读到的同时把它替换成 ApplicationContext。
     */
    private static Context toAppContext(Context context){
        if (context == null){
            return null;
        }
        Context application = context.getApplicationContext();
        return application != null ? application : context;
    }
    private static Context getAppContext(){
        Context context = reactContext;
        if (context == null){
            return null;
        }
        Context application = toAppContext(context);
        if (application != context){
            //抹掉静态字段里的 Activity 引用
            reactContext = application;
        }
        return application;
    }

    public static void sendEvent( WritableMap params){
        //JS 侧模块可能还没创建（冷启动时推送先于 JS 到达），此时拿不到实例，直接丢弃即可
        KKUMSdkEventEmitter emitter = KKUMSdkEventEmitter.getInstance();
        if (emitter == null){
            return;
        }
        emitter.sendEventWithName("userNotificationCenter", params);
    }
    /**
     * 设置制定的resource，覆盖默认的com.kk.rnumsdk的resource
     * @param nAppResource 在主程序包内新建符合协议KKAppResource的类
     */
    public static void setApplicationResource(KKAppResource nAppResource){
        AppResource = nAppResource;

    };

    public RNUMShare getShareModule(){
        if (rn_um_share == null){
            rn_um_share = new RNUMShare();
        }
        return  rn_um_share;
    }
    //与初始化
    public void preInitUMSDK(String appkey, String channel){
        //友盟基础组件预初始化
        UMConfigure.preInit(getAppContext(), appkey, channel);
    };

    //正式初始化umsdk
    public void initUMSDK(String appkey, String channel, String secret){
        //正式初始化友盟基础组件，必须在用户隐私协议同意之后才可以。之前先试用预初始化方法
        UMConfigure.init(getAppContext(), appkey, channel, UMConfigure.DEVICE_TYPE_PHONE, secret);
    };

    //获取推送token，必须要在注册后才能获取到正确token
    public void getDeviceToken(Callback callback){
        RNUMPush.getDeviceToken(callback, getAppContext());
    };
    //获取推送消息（主要是iOS需要）
    public void getNonification(Callback callback){
        RNUMPush.getNonification(callback, getAppContext());
    }

    /**
     * 三方登录授权
     */
    public void auth( Activity ma, String platformType, Callback callback){
        RNUMShare sm = getShareModule();
        sm.auth(ma, Integer.parseInt(platformType),callback);
    };
    //分享（不需要回调）
    public void shareToPlatform(Activity ma, String platformType, String shareType, ReadableMap params){
        RNUMShare sm = getShareModule();
        sm.shareToPlatform(ma, Integer.parseInt(platformType),shareType,params);
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
