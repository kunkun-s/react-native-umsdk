package com.kk.RNUMSDK;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class RNUMPushModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public RNUMPushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }
    @Override
    public String getName() {
        return "RNUMPush";
    }

    @ReactMethod
    public void getDeviceToken( final Callback successCallback) {
        SharedPreferences userData =  reactContext.getSharedPreferences("userDataRN", Context.MODE_PRIVATE);
        String  deviceToken = "";
        if (userData != null){
            deviceToken = userData.getString("deviceToken","");
        }
        if (successCallback != null) {
            successCallback.invoke(deviceToken);
        }
    }
    @ReactMethod
    public void getNonification( final Callback successCallback) {
        //Android push 没有保存通知消息的实现
        return;
    }
    @ReactMethod
    public void addListener(String eventName) {
        // Set up any upstream listeners or background tasks as necessary
    }
    @ReactMethod
    public void removeListeners(Integer count) {
        // Remove upstream listeners, stop unnecessary background tasks
    }
}
