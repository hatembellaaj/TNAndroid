package com.mdweb.tunnumerique.tools.application;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mdweb.tunnumerique.tools.SessionManager;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by Mdweb on 12/07/2019.
 */

class ExampleNotificationReceivedHandler implements OneSignal.OSRemoteNotificationReceivedHandler {
    Context context;

    public ExampleNotificationReceivedHandler(Context context) {
        this.context = context;
    }


    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent notificationReceivedEvent) {
        Log.d("NotifReceiver", "je suis dans la methode");

        OSNotification notification = notificationReceivedEvent.getNotification();
        JSONObject data = notification.getAdditionalData();
        String notificationID = notification.getNotificationId();
        String title = notification.getTitle();
        String body = notification.getBody();
        String smallIcon = notification.getSmallIcon();
        String largeIcon = notification.getLargeIcon();
        String bigPicture = notification.getBigPicture();
        String smallIconAccentColor = notification.getSmallIconAccentColor();
        String sound = notification.getSound();
        String ledColor = notification.getLedColor();
        int lockScreenVisibility = notification.getLockScreenVisibility();
        String groupKey = notification.getGroupKey();
        String groupMessage = notification.getGroupMessage();
        String fromProjectNumber = notification.getFromProjectNumber();
        //BackgroundImageLayout backgroundImageLayout = notification.payload.backgroundImageLayout;
        String rawPayload = notification.getRawPayload();
        String launchUrl = notification.getLaunchURL();
        Log.d("NotifReceiver", "Reponse recue");
        String customKey;
        String packageNameUi = "com.mdweb.tunnumerique.ui.activitys";
        // get a list of running processes and iterate through them
        String activityOnTop = SessionManager.getInstance().getActivityOnTop(context);
      //  Log.d("LaunchUrlA", activityOnTop);
        if (activityOnTop.equals(packageNameUi + ".EnqueteActivity")||activityOnTop.equals(packageNameUi + ".MainActivity") || activityOnTop.equals(packageNameUi + ".DetailArticleFragment") || activityOnTop.equals(packageNameUi + ".DetailsVideoActivity") || activityOnTop.equals(packageNameUi + ".FilterArticleActivity") || activityOnTop.equals(packageNameUi + ".DetailsBlagueActivity") || activityOnTop.equals(packageNameUi + ".NotificationActivity")) {
            if (launchUrl != null && !launchUrl.equals("")) {
                Intent aint = new Intent(context, Application.ReceiverRequete.class);
                Log.d("NotifReceiver", "Reponse recue");
                aint.putExtra("NotifRecue", "Reponse recue");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, aint, PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
