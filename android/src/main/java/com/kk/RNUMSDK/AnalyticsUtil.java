package com.kk.rnumsdk;

import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.umeng.analytics.MobclickAgent;

public class AnalyticsUtil {
    public static void onPageStart(String pageName) {
        MobclickAgent.onPageStart(pageName);
    }
    public static void onPageEnd(String pageName) {
        MobclickAgent.onPageEnd(pageName);

    }

    /**
     * 用户账户统计，配合onProfileSignOff
     * 账号统计，这里账户尽量不要使用手机号，或者是用户token，可以使用用户uid。因为这个用户信息会保存在UM第三番平台
     */
    public static void profileSignInWithPUID(String puid) {
        MobclickAgent.onProfileSignIn(puid);
    }
    public static void profileSignOff() {
        MobclickAgent.onProfileSignOff();
    }

}
