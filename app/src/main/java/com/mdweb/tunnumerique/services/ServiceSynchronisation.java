package com.mdweb.tunnumerique.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.android.volley.VolleyError;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.jsonRequest.JsonRequestHelper;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.jsonRequest.ResponseCompleteInterface;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents a Service of synchronisation local data with BO .
 *
 * @author SOFIEN ELBLAAZI
 * @author http://www.mdsoft-int.com
 * @version 1.0
 * @since 02-05-2017
 */
public class ServiceSynchronisation extends JobIntentService {

    private Timer timer;
    private JsonRequestHelper stringRequest;
    public static final int Job_id = 12;


    @Override
    public void onCreate() {
        stringRequest = new JsonRequestHelper(this);
        super.onCreate();
        Log.d("testserviceIntent", "je suis dans le service ServiceSynchronisation  onCreate");
    }


    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, ServiceSynchronisation.class, Job_id, work);
    }

    @Override
    public void onDestroy() {

        //cancel timer when service is stopped
        if (timer != null)
            timer.cancel();
        super.onDestroy();
        stopSelf();


    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        timer = new Timer();
        //
        Log.d("testserviceIntentStart", "je suis dans le service ServiceSynchronisation  onStartCommand");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Log.d("testserviceIntent", "je suis dans le service ServiceSynchronisation  onStartCommand");


                //  get News json file from server
                stringRequest.makeStringRequestGet(Communication.URL_NEWS_INIT, Communication.FILE_NEWS_INIT, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {
                        Log.d("testserviceIntent", "je suis dans reponse ok init data articles service");

                        Intent ainte = new Intent();
                        ainte.setAction("com.mdweb.tn.refresh");


                        PendingIntent pendingIntente = PendingIntent.getBroadcast(getApplicationContext(), 1, ainte, PendingIntent.FLAG_UPDATE_CURRENT);

                        try {
                            // Perform the operation associated with our pendingIntent
                            pendingIntente.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }


//                        DisplayLoggingInfo();
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {

                    }
                });

                //  get Video json file from server
                stringRequest.makeStringRequestGet(Communication.URL_VIDEO, Communication.FILE_NAME_VIDEO, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {

//                        DisplayLoggingInfo();
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {

                    }
                });

                stringRequest.makeStringRequestGet(Communication.URL_BLAGUE, Communication.FILE_NAME_BLAGUES, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {

//                        DisplayLoggingInfo();
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {

                    }
                });


                // Survey  adding

//                stringRequest.makeStringRequestGet(Communication.URL_SURVEY, Communication.FILE_SURVEY, new ResponseCompleteInterface() {
//                    @Override
//                    public void onResponseComplete(String response) {
//
////                        DisplayLoggingInfo();
//                    }
//
//                    @Override
//                    public void onResponseError(VolleyError volleyError) {
//
//                    }
//                });

                //  get Dossier json file from server
                stringRequest.makeStringRequestGet(Communication.URL_DOSSIER, Communication.FILE_NAME_DOSSIER, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {

//                        DisplayLoggingInfo();
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {

                    }
                });
                stringRequest.makeStringRequestGet(Communication.URL_DOSSIER_AR, Communication.FILE_NAME_DOSSIER_AR, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {

//                        DisplayLoggingInfo();
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {

                    }
                });

                stringRequest.makeStringRequestGet(Communication.URL_DOSSIER_EN, Communication.FILE_NAME_DOSSIER_EN, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {

//                        DisplayLoggingInfo();
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {

                    }
                });



                //  get Categories json file from server
                stringRequest.makeStringRequestGet(Communication.URL_CATEGORIES, Communication.FILE_NAME_CATEGORIES, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {

//                        DisplayLoggingInfo();
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {

                    }
                });

                stringRequest.makeStringRequestGet(Communication.URL_CATEGORIES_AR, Communication.FILE_NAME_CATEGORIES_AR, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {

//                        DisplayLoggingInfo();
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {

                    }
                });
                stringRequest.makeStringRequestGet(Communication.URL_CATEGORIES_EN, Communication.FILE_NAME_CATEGORIES_EN, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {

//                        DisplayLoggingInfo();
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {

                    }
                });

                // get plusLus json file from server
                stringRequest.makeStringRequestGet(Communication.URL_PLUS_LUS, Communication.FILE_NAME_PLUS_LUS, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {

//                        DisplayLoggingInfo();
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {

                    }
                });

                stringRequest.makeStringRequestGet(Communication.URL_PLUS_LUS_AR, Communication.FILE_NAME_PLUS_LUS_AR, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {

//                        DisplayLoggingInfo();
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {

                    }
                });
                stringRequest.makeStringRequestGet(Communication.URL_PLUS_LUS_EN, Communication.FILE_NAME_PLUS_LUS_EN, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {

//                        DisplayLoggingInfo();
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {

                    }
                });



            }
            //},Constant.durationSynchronisation, Constant.durationSynchronisation);
        }, 0, Constant.durationSynchronisation);
    }

}
