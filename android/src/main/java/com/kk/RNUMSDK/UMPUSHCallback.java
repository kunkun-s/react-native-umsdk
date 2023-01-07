package com.kk.RNUMSDK;

import android.app.Notification;
import android.content.Context;

import com.umeng.message.entity.UMessage;

//interface UMPushInterface {
//    public void sendDDUMessageHandler(UMessage umessage);
//    public void deviceTokenBack(String deviceToken);
//    public Notification getNotification(Context context, UMessage msg);
//}
public abstract class UMPUSHCallback {
    public void sendDDUMessageHandler(UMessage umessage){};
    public void deviceTokenBack(String deviceToken){};
    public Notification getNotification(Context context, UMessage msg){ return null; };

}
