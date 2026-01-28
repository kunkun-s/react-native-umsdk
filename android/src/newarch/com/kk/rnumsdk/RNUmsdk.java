package com.kk.rnumsdk;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

import javax.annotation.Nullable;

public class RNUmsdk extends NativeUMSdkModuleSpec{

    public RNUmsdkImpl sdkImpl = null;
    public static ReactApplicationContext reactContext;
    public RNUmsdk(ReactApplicationContext context) {

        super(context);
        reactContext = context;
        sdkImpl = new RNUmsdkImpl(context);
    }

    @Override
    public String getName() {
        return RNUmsdkImpl.NAME;
    }

    @Override
    public void preInitUMSDK(String appkey, String channel){
        sdkImpl.preInitUMSDK(appkey, channel);
    };

    @Override
    public void initUMSDK(String appkey, String channel, String secret){
        sdkImpl.initUMSDK(appkey, channel, secret);
    };
    @Override
    public void getDeviceToken(Callback callback){
        sdkImpl.getDeviceToken(callback);
    };
    @Override
    public void getNonification(Callback callback){
        sdkImpl.getNonification(callback);
    };
    @Override
    public void auth(String platformType, Callback callback){
        sdkImpl.auth(reactContext.getCurrentActivity(), platformType, callback);
    };
    @Override
    public void shareToPlatform(String platformType, String shareType, ReadableMap params, @Nullable Callback callback){
        sdkImpl.shareToPlatform(reactContext.getCurrentActivity(), platformType, shareType, params, callback);
    };
    @Override
    public void isInstall(String platformType, Callback callback){
        sdkImpl.isInstall(reactContext.getCurrentActivity(), platformType, callback);
    };
    @Override
    public void onPageStart(String pageName) {
        sdkImpl.onPageStart(pageName);
    }
    @Override
    public void onPageEnd(String pageName) {
        sdkImpl.onPageEnd(pageName);
    }
    @Override
    public void profileSignInWithPUID(String puid) {
        sdkImpl.profileSignInWithPUID(puid);
    }
    @Override
    public void profileSignOff() {
        sdkImpl.profileSignOff();
    }
}
