
package com.kk.rnumsdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facebook.react.BaseReactPackage;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;

public class RNUmsdkPackage implements BaseReactPackage {

    @Nullable
    @Override
    public NativeModule getModule(String name, @NonNull ReactApplicationContext reactContext) {
        if (name.equals(RNUmsdkImpl.NAME)) {
            /**
             * 1.新旧架构都在com.dddverify包内
             * 2.新旧架构文件同名
             * 3.编译build.gradle时根据是否开启IS_NEW_ARCHITECTURE_ENABLED，只会加载一个文件。
             *
             * 如果新旧架构报名不同，则这里应该做一个BuildConfig.IS_NEW_ARCHITECTURE_ENABLED判断，加载不同文件名，不同getName返回值，且在JS端需要根据IS_NEW_ARCHITECTURE_ENABLED判断加载不同模块
             *  @Override
             *  public String getName() { return RNDdverifyImpl.NAME; }
             */
            return new RNUmsdk(reactContext);
        } else {
            return null;
        }
    }
    @Override
    public ReactModuleInfoProvider getReactModuleInfoProvider() {
        return new ReactModuleInfoProvider() {
            @Override
            public Map<String, ReactModuleInfo> getReactModuleInfos() {
                Map<String, ReactModuleInfo> map = new HashMap<>();
                boolean isTurboModule = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;

                map.put(RNUmsdkImpl.NAME, new ReactModuleInfo(
                        RNUmsdkImpl.NAME,       // name
                        RNUmsdkImpl.NAME,       // className
                        false, // canOverrideExistingModule
                        false, // needsEagerInit
                        false, // isCXXModule
                        isTurboModule   // isTurboModule
                ));
                return map;
            }
        };
    }

}