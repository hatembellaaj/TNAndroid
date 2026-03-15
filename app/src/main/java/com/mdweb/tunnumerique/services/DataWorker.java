package com.mdweb.tunnumerique.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.VolleyError;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.jsonRequest.JsonRequestHelper;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.jsonRequest.ResponseCompleteInterface;
import com.mdweb.tunnumerique.tools.shared.Communication;

public class DataWorker extends Worker {
    private JsonRequestHelper stringRequest;

    public DataWorker(@NonNull Context context, @NonNull WorkerParameters params) {

        super(context, params);
        stringRequest = new JsonRequestHelper(context);

    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("WorksStart", "Yes i'am starting");
        //  get News json file from server
        stringRequest.makeStringRequestGet(Communication.URL_NEWS_INIT, Communication.FILE_NEWS_INIT, new ResponseCompleteInterface() {
            @Override
            public void onResponseComplete(String response) {
                Log.d("testserviceIntent", "je suis dans reponse ok init data articles service");

                Intent ainte = new Intent();
                ainte.setAction("com.mdweb.tn.refresh");
                int pendingtIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT;
                if(Build.VERSION.SDK_INT>=31) {
                    pendingtIntentFlag = PendingIntent.FLAG_IMMUTABLE;
                }
                    PendingIntent pendingIntente = PendingIntent.getBroadcast(getApplicationContext(), 1, ainte, pendingtIntentFlag);
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

        stringRequest.makeStringRequestGet(Communication.URL_NEWS_INIT_AR, Communication.FILE_NEWS_INIT_AR, new ResponseCompleteInterface() {
            @Override
            public void onResponseComplete(String response) {
                Log.d("testserviceIntent", "je suis dans reponse ok init data articles service");

                Intent ainte = new Intent();
                ainte.setAction("com.mdweb.tn.refresh");


                int pendingtIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT;
                if(Build.VERSION.SDK_INT>=31) {
                    pendingtIntentFlag = PendingIntent.FLAG_IMMUTABLE;
                }
                PendingIntent pendingIntente = PendingIntent.getBroadcast(getApplicationContext(), 1, ainte, pendingtIntentFlag);

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
        stringRequest.makeStringRequestGet(Communication.URL_NEWS_INIT_EN, Communication.FILE_NEWS_INIT_EN, new ResponseCompleteInterface() {
            @Override
            public void onResponseComplete(String response) {
                Log.d("testserviceIntent", "je suis dans reponse ok init data articles service");

                Intent ainte = new Intent();
                ainte.setAction("com.mdweb.tn.refresh");


                int pendingtIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT;
                if(Build.VERSION.SDK_INT>=31) {
                    pendingtIntentFlag = PendingIntent.FLAG_IMMUTABLE;
                }
                PendingIntent pendingIntente = PendingIntent.getBroadcast(getApplicationContext(), 1, ainte, pendingtIntentFlag);

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



        return Result.success();
    }
}
