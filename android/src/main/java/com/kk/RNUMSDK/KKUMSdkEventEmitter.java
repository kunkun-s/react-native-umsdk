package com.kk.rnumsdk;


import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashSet;
import java.util.Set;

public class KKUMSdkEventEmitter extends ReactContextBaseJavaModule {
    private ReactApplicationContext reactContext;
    private Set<String> activeListeners = new HashSet<>();
    private static KKUMSdkEventEmitter instance;
    public static String NAME = "RNUMSdkBridge"; //与NativeDDVerify.ts文件中的get<Spec>('RNDdverify') 保持一致

    public KKUMSdkEventEmitter(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        instance = this;
    }

    public static KKUMSdkEventEmitter getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return NAME;
    }

//    // 对应 iOS 的 supportedEvents 方法
//    @Override
//    public java.util.Map<String, Object> getConstants() {
//        final java.util.Map<String, Object> constants = new java.util.HashMap<>();
//        constants.put("supportedEvents", getSupportedEvents());
//        return constants;
//    }
//
//    private String[] getSupportedEvents() {
//        return new String[] {
//                "onDataUpdate",
//                "onProgress",
//                "onContinuousEvent",
//                "onError",
//                "onStatusChange"
//        };
//    }

    // 对应 iOS 的 sendEventWithName 方法
    public void sendEventWithName(String eventName, WritableMap body) {
        if (reactContext != null && reactContext.hasActiveReactInstance()) {
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, body);
        }
    }

    public void addListener(String eventName) {
        activeListeners.add(eventName);
    }

    public void removeListener(String eventName) {
        activeListeners.remove(eventName);
    }

    public void removeAllListeners() {
        activeListeners.clear();
    }

    // 便捷方法：发送事件（对应 iOS 的 emitEvent 方法）
    public void emit(String eventName, Object body) {
        if (body instanceof WritableMap) {
            sendEventWithName(eventName, (WritableMap) body);
        } else if (body instanceof String) {
            WritableMap map = Arguments.createMap();
            map.putString("value", (String) body);
            sendEventWithName(eventName, map);
        } else if (body instanceof Integer) {
            WritableMap map = Arguments.createMap();
            map.putInt("value", (Integer) body);
            sendEventWithName(eventName, map);
        } else if (body instanceof Double) {
            WritableMap map = Arguments.createMap();
            map.putDouble("value", (Double) body);
            sendEventWithName(eventName, map);
        } else if (body instanceof Boolean) {
            WritableMap map = Arguments.createMap();
            map.putBoolean("value", (Boolean) body);
            sendEventWithName(eventName, map);
        } else {
            // 默认转为字符串
            WritableMap map = Arguments.createMap();
            map.putString("value", String.valueOf(body));
            sendEventWithName(eventName, map);
        }
    }
}