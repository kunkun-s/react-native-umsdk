package com.kk.rnumsdk;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import javax.annotation.Nullable;

public class RNUmsdk2 extends ReactContextBaseJavaModule {
    public RNUmsdkImpl sdkImpl = null;

    public RNUmsdk2(ReactApplicationContext reactContext) {
        super(reactContext);
        sdkImpl = new RNUmsdkImpl(reactContext);
    }

    @Override
    public String getName() {
        return RNUmsdkImpl.NAME;
    }
    @ReactMethod
    public void addListener(String eventName) {
        // Set up any upstream listeners or background tasks as necessary
    }
    @ReactMethod
    public void removeListeners(Integer count) {
        // Remove upstream listeners, stop unnecessary background tasks
    }

    @ReactMethod
    public void preInitUMSDK(String appkey, String channel){
        sdkImpl.preInitUMSDK(appkey, channel);
    };

    @ReactMethod
    public void initUMSDK(String appkey, String channel, String secret){
        sdkImpl.initUMSDK(appkey, channel, secret);
    };
    @ReactMethod
    public void getDeviceToken(Callback callback){
        sdkImpl.getDeviceToken(callback);
    };
    @ReactMethod
    public void getNonification(Callback callback){
        sdkImpl.getNonification(callback);
    };
    @ReactMethod
    public void auth(String platformType, Callback callback){
        sdkImpl.auth(platformType, callback);
    };
    @ReactMethod
    public void shareToPlatform(String platformType, String shareType, ReadableMap params, @Nullable Callback callback){
        sdkImpl.shareToPlatform(platformType, shareType, params, callback);
    };
    @ReactMethod
    public void isInstall(String platformType, Callback callback){
        sdkImpl.isInstall(platformType, callback);
    };
    @ReactMethod
    public void onPageStart(String pageName) {
        sdkImpl.onPageStart(pageName);
    }
    @ReactMethod
    public void onPageEnd(String pageName) {
        sdkImpl.onPageEnd(pageName);
    }
    @ReactMethod
    public void profileSignInWithPUID(String puid) {
        sdkImpl.profileSignInWithPUID(puid);
    }
    @ReactMethod
    public void profileSignOff() {
        sdkImpl.profileSignOff();
    }
}
