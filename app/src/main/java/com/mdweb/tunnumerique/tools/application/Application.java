package com.mdweb.tunnumerique.tools.application;

// class to init element on the launch

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;

import com.android.volley.VolleyError;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.cache.ImageCacheManager;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.jsonRequest.JsonRequestHelper;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.jsonRequest.ResponseCompleteInterface;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.onesignal.OneSignal;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class Application extends MultiDexApplication implements LifecycleObserver {

    private static Application mInstance;
    // Define a variable for the Analytics Kit instance.
    HiAnalyticsInstance mHiAnalyticsInstance;
    private Intent intentService;
    private static final String ONESIGNAL_APP_ID = "47494d2b-f1df-4a09-8491-6790c2d9be83";



    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        final Context cxt = this;
        setAppMode();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            // Enable Analytics Kit logging.
            HiAnalyticsTools.enableLog();
            // Generate an Analytics Kit instance.
            mHiAnalyticsInstance = HiAnalytics.getInstance(mInstance);



//        String lng = SessionManager.getInstance().getCurrentLang(this);
//        if (lng != null && !lng.isEmpty()) {
//            LocaleHelper.setLocale(this, SessionManager.getInstance().getCurrentLang(this));
//        } else {
//            LocaleHelper.setLocale(this, "fr");
//        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ImageCacheManager.INSTANCE.initImageCache(cxt);
                //       MobileAds.initialize(getApplicationContext(), "ca-app-pub-2102118777505746~3353162866");

                // Enable verbose OneSignal logging to debug issues if needed.
                OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
                OneSignal.initWithContext(cxt);
                OneSignal.setAppId(ONESIGNAL_APP_ID);

                // OneSignal.promptLocation();
                OneSignal.unsubscribeWhenNotificationsAreDisabled(true);
                //  OneSignal.OSRemoteNotificationReceivedHandler = new ExampleNotificationOpenedHandler(cxt);
                OneSignal.setNotificationOpenedHandler(new ExampleNotificationOpenedHandler(getApplicationContext()));


                // OneSignal.OSRemoteNotificationReceivedHandler = new ExampleNotificationOpenedHandler(ctx);
//                OneSignal.setNotificationWillShowInForegroundHandler(notificationReceivedEvent -> {
//                   Log.d("OnReceiveNotif", "notif");
//
//
//                });
                //    Log.d("Init_One", Text.ONESIGNAL_SDK_INIT);


                OneSignal.disablePush(!SessionManager.getInstance().getNotification(getApplicationContext()));
                return null;
            }
        }
                .execute();


    }


    public static synchronized Application getInstance() {
        return mInstance;
    }


    public void trackScreenView(Activity activity, String screenName) {

            mHiAnalyticsInstance.setCurrentActivity(activity,screenName,screenName);

        }
    public void trackEventScreenView(String idEvent, String articleId , String title) {
        Bundle bundle = new Bundle();
        bundle.putString("article_id", articleId);
        bundle.putString("article_title", title);
        bundle.putString("content_type", "text");
        mHiAnalyticsInstance.onEvent(idEvent, bundle);
    }

    public static class ReceiverRequete extends BroadcastReceiver {
        String msg;

        @Override
        public void onReceive(final Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            msg = extras.getString("NotifRecue");

            final JsonRequestHelper stringRequest = new JsonRequestHelper(context);

            //
            String serveurUrl = Communication.URL_NEWS_INIT;
            String fileName = Communication.FILE_NEWS_INIT;

            if (SessionManager.getInstance().getCurrentLang(mInstance).equals(Constant.AR)) {
                serveurUrl = Communication.URL_NEWS_INIT_AR;
                fileName = Communication.FILE_NEWS_INIT_AR;
            } else if (SessionManager.getInstance().getCurrentLang(mInstance).equals(Constant.EN)) {
                serveurUrl = Communication.URL_NEWS_INIT_EN;
                fileName = Communication.FILE_NEWS_INIT_EN;
            }
            //  get News json file from server
            stringRequest.makeStringRequestGet(serveurUrl, fileName, new ResponseCompleteInterface() {
                @Override
                public void onResponseComplete(String response) {
                    //reponse
                    Intent ainte = new Intent();
                    ainte.setAction("com.mdweb.tn.refresh");
                    PendingIntent pendingIntente = PendingIntent.getBroadcast(context, 1, ainte, PendingIntent.FLAG_UPDATE_CURRENT);
                    try {
                        // Perform the operation associated with our pendingIntent
                        pendingIntente.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onResponseError(VolleyError volleyError) {

                }
            });


        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
//        intentService = new Intent(this, ServiceSynchronisation.class);


        SessionManager.getInstance().setForground(this, "back");
//
//        Log.d("testserviceIntent","je suis dans Lifecycle.Event.ON_STOP ");
//
//        // app went to background
//        //  get News json file from server
//        if(isMyServiceRunning(ServiceSynchronisation.class)) {
//            Log.d("testserviceIntent","je suis dans Lifecycle.Event.ON_STOP my servise is running i will stop ");
//
//            stopService(intentService);
//        }

//
//            WorkManager.getInstance(this).cancelAllWorkByTag("DataWorker");
//         //  WorkManager.getInstance(this).cancelAllWork();
//            Log.d("AppBackGround", "Job cancelled!");


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground
//        intentService = new Intent(this, ServiceSynchronisation.class);
//
//        Log.d("testserviceIntent","je suis dans Lifecycle.Event.ON_START ");
//
//        // app went to foreground
//        //start service synchronisation
//
//        startService(intentService);

        //ServiceSynchronisation.enqueueWork(this, new Intent());
        SessionManager.getInstance().setForground(this, "Run");


    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static void setAppMode() {
        switch (SessionManager.getInstance().getCurrentMode(mInstance)) {
            case "day":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "night":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;


        }
    }

    //    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        //     Log.e("DisplayedRunnableDD", getResources().getString(R.string.videos_menu));


    }
}

