package com.mdweb.tunnumerique.tools.application;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.ui.activitys.NotificationActivity;
import com.mdweb.tunnumerique.ui.activitys.SplashScreenActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Mdweb on 12/07/2019.
 */

class ExampleNotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {
    Context context;

    public ExampleNotificationOpenedHandler(Context context) {
        this.context = context;
    }

    // This fires when a notification is opened by tapping on it.


    @Override
    public void notificationOpened(OSNotificationOpenedResult result) {
        OSNotificationAction.ActionType actionType = result.getAction().getType();
        JSONObject data = result.getNotification().getAdditionalData();
        String launchUrl = result.getNotification().getLaunchURL(); // update docs launchUrl
        String customKey;

        Object activityToLaunch = SplashScreenActivity.class;
        // Used to check for CURRENT example main screen
        String packageNameUi = "com.mdweb.tunnumerique.ui.activitys";
        // get a list of running processes and iterate through them
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        // get the info from the currently running task


        Log.d("LaunchUrl", "Test");

        if (SessionManager.getInstance().getForground(context).equals("Run")) {
            if (launchUrl != null && !launchUrl.equals("")) {
                Log.d("LaunchUrlA", "Notify");

                Intent intent = new Intent(context, (Class<?>) NotificationActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("openURL", launchUrl);
                context.startActivity(intent);
                //   SessionManager.getInstance().setNotificationUrl(context, launchUrl);
            } else {
            }

//}
        } else {

            Log.d("LaunchUrlA", "EmptyOrNull");

            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
            Intent intent = new Intent(context, (Class<?>) activityToLaunch);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("openURL", launchUrl);
            context.startActivity(intent);
            Runtime.getRuntime().exit(0);


        }
    }
}
