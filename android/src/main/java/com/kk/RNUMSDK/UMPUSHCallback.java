package com.kk.RNUMSDK;

import com.umeng.message.entity.UMessage;

public interface UMPUSHCallback {
    public void sendDDUMessageHandler(UMessage umessage);
    public void deviceTokenBack(String deviceToken);
}
