package com.kk.RNUMSDK;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

public class RNUMPush {
    private Handler handler;
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
        //为push 指定applictionID，如果文件结构和包名不一致时必须设置，
        mPushAgent.setResourcePackageName(applictionID);

        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义通知栏样式的回调方法
             */
//      @Override
//      public Notification getNotification(Context context, UMessage msg) {
//        System.out.println("新消息消息getNotification");
//        sendDDUMessageHandler(msg);
//        return super.getNotification(context, msg);
//      }
            /**
             * 通知的回调方法（通知送达时会回调）
             */
            @Override
            public void dealWithNotificationMessage(Context context, UMessage uMessage) {
                super.dealWithNotificationMessage(context, uMessage);
                // 收到消息时的回调方法(不点击通知也会走),自定义消息和通知都会走这个回调,可以在这个回调方法中做一些预处理
                umcallback.sendDDUMessageHandler(uMessage);
            }
            /**
             * 自定义消息的回调方法
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        umcallback.sendDDUMessageHandler(msg);
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计

                            UTrack.getInstance(context).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(context).trackMsgDismissed(msg);
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
            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知，参考http://bbs.umeng.com/thread-11112-1-1.html
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
//                Log.i("deviceToken33", deviceToken);
                SharedPreferences userData = context.getSharedPreferences("userDataRN", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userData.edit();
                editor.putString("deviceToken",deviceToken);
                editor.commit();
                umcallback.deviceTokenBack(deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });

        //各主要厂商推送实现
        //小米通道
        //XIAOMI_ID, XIAOMI_KEY
//        if(RomUtil.isMiui()) {
//            MiPushRegistar.register(getApplicationContext(), "2882303761517300206", "5761730045206");
//        }
//        //华为通道
//        if(RomUtil.isEmui()) {
//            HuaWeiRegister.register(getApplicationContext());
//        }
//        //魅族通道
//        //MEIZU_APPID, MEIZU_APPKEY
//        if(RomUtil.isFlyme()) {
//            MeizuRegister.register(getApplicationContext(), "117222", "ea545aa766514cbbb25c5545188a6678");
//        }

        //Vivo
        //    if(RomUtil.isVivo()){
        //      initVivoPush();
        //    }
        //Oppo
        //    if(RomUtil.isOppo()){
        //
        //    }

    }
}
