package com.mdweb.tunnumerique.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.onesignal.OSMutableNotification;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import java.math.BigInteger;


public class NotificationExtenderAndroid implements OneSignal.OSRemoteNotificationReceivedHandler {

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent notificationReceivedEvent) {
        // Sets the background notification color to Green on Android 5.0+ devices.
        OSNotification notification = notificationReceivedEvent.getNotification();
        OSMutableNotification mutableNotification = notification.mutableCopy();

        mutableNotification.setExtender(builder -> {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel;
            AudioAttributes audioAttributes;
            Log.e("HelloNotification", "create");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (SessionManager.getInstance().IsVibreur(context) && SessionManager.getInstance().IsSon(context)){
                    Log.e("HelloNotification", "SonVibra");

                    //Creation d'une nouvelle Channel pour activé la vibration et le son du notification pour android >= Oreo
                    mChannel =  creationChannel(Constant.CHANNEL_SIREN_ID, "CHANNEL_SIREN_NAME", NotificationManager.IMPORTANCE_HIGH);
                    audioAttributes= creationAudioAttributes();
                    mChannel.setSound(Uri.parse("android.resource://com.mdweb.tunnumerique/" + R.raw.son), audioAttributes);
                    mChannel.setVibrationPattern(new long[]{500, 500, 500, 500, 500});

                }else if (!SessionManager.getInstance().IsVibreur(context) && !SessionManager.getInstance().IsSon(context)) {
                    Log.e("HelloNotification", "NoSonVibra");

                    //Creation d'une nouvelle Channel pour déactivé la vibration et le son du notification pour android >= Oreo
                    mChannel = creationChannel(Constant.CHANNEL_SIREN_ID2, "CHANNEL_SIREN_NAME2", NotificationManager.IMPORTANCE_HIGH);
                    mChannel.setVibrationPattern(null);
                    mChannel.setSound(null,null);

                } else if (!SessionManager.getInstance().IsSon(context)) {
                    Log.e("HelloNotification", "NoSon");

                    //Creation d'une nouvelle Channel pour activé la vibration et désactivé le son du notification pour android >= Oreo
                    mChannel = creationChannel(Constant.CHANNEL_SIREN_ID3, "CHANNEL_SIREN_NAME2", NotificationManager.IMPORTANCE_HIGH);
                    mChannel.setSound(null,null);
                    mChannel.setVibrationPattern(new long[]{500, 500, 500, 500, 500});

                }else {
                    Log.e("HelloNotification", "NoSonNoVibra");

                    //Creation d'une nouvelle Channel pour déactivé la vibration et activé le son du notification pour android >= Oreo
                    mChannel = creationChannel(Constant.CHANNEL_SIREN_ID4, "CHANNEL_SIREN_NAME", NotificationManager.IMPORTANCE_HIGH);
                    audioAttributes= creationAudioAttributes();
                    mChannel.setSound(Uri.parse("android.resource://com.mdweb.tunnumerique/" + R.raw.son), audioAttributes);
                    mChannel.setVibrationPattern(null);

                }

                if (mNotificationManager != null) {
                    mNotificationManager.createNotificationChannel( mChannel );

                }
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                if (SessionManager.getInstance().IsVibreur(context) && SessionManager.getInstance().IsSon(context)) {
                    builder.setChannelId(Constant.CHANNEL_SIREN_ID);
                }
                else if (!SessionManager.getInstance().IsVibreur(context) && !SessionManager.getInstance().IsSon(context)) {
                    builder.setChannelId(Constant.CHANNEL_SIREN_ID2);
                }
                else if (!SessionManager.getInstance().IsSon(context))
                    builder.setChannelId(Constant.CHANNEL_SIREN_ID3);

                else
                    builder.setChannelId(Constant.CHANNEL_SIREN_ID4);
            }
            builder.setColor(new BigInteger("FF36C750", 16).intValue());

            if (SessionManager.getInstance().IsVibreur(context) && SessionManager.getInstance().IsSon(context)&&Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setVibrate(new long[]{500, 500, 500, 500, 500});
                builder.setSound(Uri.parse("android.resource://com.mdweb.tunnumerique/" + R.raw.son));
                return builder;

            } else
            if (!SessionManager.getInstance().IsVibreur(context) && !SessionManager.getInstance().IsSon(context)&&Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setVibrate(null);
                builder.setSound(null);
                return builder;
            } else if (!SessionManager.getInstance().IsSon(context)&&Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setSound(null);
                builder.setVibrate(new long[]{500, 500, 500, 500, 500});
                return builder;
            } else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setVibrate(null);
                builder.setSound(null);

                // builder.setSound(Uri.parse("android.resource://com.mdweb.tunnumerique/" + R.raw.son));
                return builder;
            }else {


                return builder;}
        });

        notificationReceivedEvent.complete(mutableNotification);




    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private AudioAttributes creationAudioAttributes() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        return audioAttributes;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel creationChannel(String channelSirenId, String channel_siren_name, int importanceHigh) {
        NotificationChannel mChannel = new NotificationChannel(channelSirenId, channel_siren_name, importanceHigh);
        mChannel.setLightColor(new BigInteger("FF36C750", 16).intValue());
        mChannel.enableLights(true);
        mChannel.setDescription("");
        mChannel.setBypassDnd(true);
        return mChannel;
    }
};



