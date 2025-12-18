package com.kk.rnumsdk;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

import javax.annotation.Nullable;

public class RNUmsdk extends NativeUMSdkModuleSpec{

    public RNUmsdkImpl sdkImpl = null;
    public RNUmsdk(ReactApplicationContext reactContext) {

        super(reactContext);
        sdkImpl = new RNUmsdkImpl(reactContext);
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
    public void auth(String platformType, Callback callback){
        sdkImpl.auth(platformType, callback);
    };
    @Override
    public void shareToPlatform(String platformType, String shareType, ReadableMap params, @Nullable Callback callback){
        sdkImpl.shareToPlatform(platformType, shareType, params, callback);
    };
    @Override
    public void isInstall(String platformType, Callback callback){
        sdkImpl.isInstall(platformType, callback);
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
