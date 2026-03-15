package com.mdweb.tunnumerique.services;

import android.text.TextUtils;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.onesignal.OneSignalHmsEventBridge;

public class YourHmsMessageService extends HmsMessageService {

    @Override
    public void onNewToken(String token) {
        // Forward event on to OneSignal SDK
        if (!TextUtils.isEmpty(token)) {
            OneSignalHmsEventBridge.onNewToken(this, token);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        // Forward event on to OneSignal SDK
        OneSignalHmsEventBridge.onMessageReceived(this, message);
    }
}
