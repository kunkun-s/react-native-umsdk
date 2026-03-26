package com.kk.rnumsdk;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.api.UPushRegisterCallback;
import com.umeng.message.entity.UMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RNUMPush {
    private Handler handler;
    //原旧RNUMPush模块方法
    public static void getDeviceToken(Callback callback, Context applicatioContext){

        SharedPreferences userData = applicatioContext.getSharedPreferences("userDataRN", Context.MODE_PRIVATE);
        String  deviceToken = "";
        if (userData != null){
            deviceToken = userData.getString("deviceToken","");
        }
        try {
            // 2. 缓存为空，尝试从 SDK 获取
            PushAgent pushAgent = PushAgent.getInstance(applicatioContext);
            if (pushAgent != null) {
                String registrationId = pushAgent.getRegistrationId();
                if (registrationId != null && !registrationId.isEmpty()) {
                    // 获取到 token，更新缓存
                    deviceToken = registrationId;
                }
            }
        } catch (Exception ignored) {

        }

        if (callback != null) {
            callback.invoke(deviceToken);
        }
    };
    //与iOS保持一致方法，android没有实际方法
    public static void getNonification(Callback callback, Context applicatioContext){

    };
    public static WritableMap createData(UMessage msg){
        WritableMap params = Arguments.createMap();

        if (msg == null){
            return params;
        }
        if(msg.extra == null){
            return params;
        }
        if (msg.extra.isEmpty()){
            return params;
        }
        Map<String, String> mapExtra = msg.extra;
        JSONObject json = new JSONObject(mapExtra);
        params.putString("extra",json.toString());
        if (msg.text != null){
            if (!msg.text.isEmpty()){
                params.putString("text",msg.text);
            }
        }

        if (mapExtra.get("title") != null){
            params.putString("title",mapExtra.get("title"));
        }
        if (mapExtra.get("content") != null) {
            params.putString("text",mapExtra.get("content"));
        }else if (mapExtra.get("text") != null){
            params.putString("text",mapExtra.get("text"));
        }
        return params;
    }
    public void initUpush(final Context context, final UMPUSHCallback umcallback) {

        //---------------
        PushAgent mPushAgent = PushAgent.getInstance(context);
        if ( handler == null){
            handler = new Handler(Looper.getMainLooper());
        }

        //sdk开启通知声音
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        //获取的是主程序的包名而非是，当前模块的包名
        String applictionID = context.getApplicationInfo().packageName;
//        Log.i("xxxxxx",applictionID);
        //为push 指定packageName，如果文件结构和包名不一致时必须设置，用于加载那个资源包的res下文件，可以是gradle依赖的三方包
        mPushAgent.setResourcePackageName(applictionID);
        mPushAgent.setBadgeNum(0);//清除角标
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义通知栏样式的回调方法
             */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                System.out.println("新消息消息getNotification"+msg.extra.get("audioStyle"));
//
                if (umcallback != null) {
                    Notification notf = umcallback.getNotification(context, msg);;
                    if (notf != null){
                        return  notf;
                    }
                }
                return super.getNotification(context, msg);
            }
            /**
             * 通知的回调方法（通知送达时会回调）
             */
            @Override
            public void dealWithNotificationMessage(Context context, UMessage uMessage) {
                super.dealWithNotificationMessage(context, uMessage);
                // 收到消息时的回调方法(不点击通知也会走),自定义消息和通知都会走这个回调,可以在这个回调方法中做一些预处理
                umcallback.sendDDUMessageHandler(createData(uMessage));
            }
            /**
             * 自定义消息的回调方法
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {


                        umcallback.sendDDUMessageHandler(createData(msg));
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计

                            UTrack.getInstance().trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance().trackMsgDismissed(msg);
                        }
                    }
                });
            }

        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                //统计点击率
                UTrack.getInstance().trackMsgClick(msg);
            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知，参考http://bbs.umeng.com/thread-11112-1-1.html
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new UPushRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
//                Log.i("deviceToken33", deviceToken);
                SharedPreferences userData = context.getSharedPreferences("userDataRN", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userData.edit();
                editor.putString("deviceToken",deviceToken);
                editor.apply();
                umcallback.deviceTokenBack(deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });
        // 推送权限检测
        mPushAgent.setPushCheck(true);
        // 设置免打扰时间24小时制（开始小时，开始分，结束小时，结束分）
        // 例如：晚上23:00 - 次日5:00 免打扰
        // PushAgent.getInstance(context).setNoDisturbMode(23, 0, 5, 0);
        mPushAgent.setNoDisturbMode(0, 0, 0, 0);
        /**
         * 自定义通知图标
         * 将图标防止在指定的资源包内（若 资源包 和 applicationId不一致，则要设置setResourcePackageName(实际资源包名)）），
         * 状态栏小图标：res/drawable/umeng_push_notification_default_small_icon.png
         * 通知栏大图标：res/drawable/umeng_push_notification_default_large_icon.png
         * */
        //允许震动
        mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);

        //各主要厂商推送实现


    }
}
