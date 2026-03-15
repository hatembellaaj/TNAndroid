package com.mdweb.tunnumerique.ui.activitys;


import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.banner.BannerView;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Country;
import com.mdweb.tunnumerique.data.parsers.DataParser;
import com.mdweb.tunnumerique.services.DataWorker;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.application.Application;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.jsonRequest.JsonRequestHelper;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.jsonRequest.LocalFilesManager;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.jsonRequest.ResponseCompleteInterface;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.tools.upload.LocallyFiles;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * Represents a Splash Screen.
 *
 * @author SOFIENE ELBLAAZI
 * @author http://www.mdsoft-int.com
 * @author KARIM MAHARI
 * @version 1.0
 * @since 03-05-2017
 */
public class SplashScreenActivity extends BaseActivity {

    private LinearLayout splashScreenView = null;
    private TextView textLoading = null;
    private ProgressBar progressLoading = null;

    private Handler handlerMain = null;
    private Handler handlerPub = null;
    private Utils utils;
    ArrayList<Country> countriesArrayList;

    private Runnable r2 = null;
    private Runnable runPub = null;
    private Intent intent = null;
    private String url_image_pub = "";
    private String link_pub = "";
    private String nameCampagne = "";
    private int isActive = 0;
    private int pubToShow = 0;
    private ImageView pub = null;

    public boolean isLoadedAd = false;
    private Animation backgroundAnimation = null;
    private LocalFilesManager localFilesManager = null;
    private Dialog dialog;
    private View view_pub;
    private View view_pub_serveur;
    private RelativeLayout relativeLayout;
    static boolean active = false;
    static boolean pub_cliked = false;
    static int first_run;
    private String openURL;

    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;
    java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

    private com.huawei.hms.ads.InterstitialAd huaweiInterstitialHAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Date date = new Date();
        String strDate = dateFormat.format(date);
        //Store Data
        SessionManager.getInstance().firstStartTime(this, strDate);
        SessionManager.getInstance().setTimeAdmob(this, strDate);
        String firstLaunch = SessionManager.getInstance().getFirstLaunch(this);
        if (firstLaunch.isEmpty()) {
            SessionManager.getInstance().firstLaunchApp(this, strDate);
        }
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest myWorkRequest = new PeriodicWorkRequest.Builder(DataWorker.class, 15L, TimeUnit.MINUTES).setConstraints(constraints).build();

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());

        workManager.enqueueUniquePeriodicWork("DataWorker",
                ExistingPeriodicWorkPolicy.KEEP, myWorkRequest);

        openURL = getIntent().getStringExtra("openURL");
        splashScreenView = findViewById(R.id.activity_splash_screen);
        textLoading = findViewById(R.id.textLoading);
        progressLoading = findViewById(R.id.progressLoading);

        // Cacher le chargement au début
        textLoading.setVisibility(View.INVISIBLE);
        progressLoading.setVisibility(View.INVISIBLE);

        animate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String lng = SessionManager.getInstance().getCurrentLang(this);

        initData();
        getDataPubFromServeur();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lng != null && !lng.isEmpty()) {
                    startPub();
                } else {
                    Intent intentLng = new Intent(SplashScreenActivity.this, LangueActivity.class);
                    startActivityForResult(intentLng, 3251);
                }
            }
        }, 2000);
    }

    /**
     * Animation de la ProgressBar
     */
    public void animationProgressBar() {
        textLoading.setVisibility(View.VISIBLE);
        progressLoading.setVisibility(View.VISIBLE);
        progressLoading.setProgress(0);

        // Animation du texte avec alpha
        textLoading.setAlpha(0f);
        textLoading.animate()
                .alpha(1f)
                .setDuration(500)
                .start();

        // Animer la progress bar de 0 à 100 sur 5 secondes
        final Handler handler = new Handler();
        final int duration = 5000; // 5 secondes
        final int updateInterval = 50; // Mise à jour toutes les 50ms
        final int maxProgress = 100;

        new Thread(new Runnable() {
            int currentProgress = 0;
            long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                while (currentProgress < maxProgress) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    currentProgress = (int) ((elapsed * maxProgress) / duration);

                    if (currentProgress > maxProgress) {
                        currentProgress = maxProgress;
                    }

                    final int progress = currentProgress;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressLoading.setProgress(progress);

                            // Optionnel : changer le texte selon la progression
                            if (progress < 30) {
                                textLoading.setText("Chargement des données...");
                            } else if (progress < 60) {
                                textLoading.setText("Préparation du contenu...");
                            } else if (progress < 90) {
                                textLoading.setText("Finalisation...");
                            } else {
                                textLoading.setText("Prêt !");
                            }
                        }
                    });

                    try {
                        Thread.sleep(updateInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
        Log.d("onresumetest", "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onresumetest", "onPause");
    }

    public void loadHuaweiAd(){
        huaweiInterstitialHAd = new com.huawei.hms.ads.InterstitialAd(this);
        huaweiInterstitialHAd.setAdId("x1d7ckapea");
        huaweiInterstitialHAd.setAdListener(adListener);
        loadInterstitialAd();
    }

    public void initData() {
        if (first_run == 1) {
            intent = new Intent(SplashScreenActivity.this, HomeTnActivity.class);
            intent.putExtra("openURL", "");
            startActivity(intent);
            finish();
        } else {
            first_run = 1;

            //view pub
            view_pub_serveur = (SplashScreenActivity.this).getLayoutInflater().inflate(R.layout.pub_start_layout, null);
            pub = (ImageView) view_pub_serveur.findViewById(R.id.pub);

            handlerMain = new Handler();
            handlerPub = new Handler();
            //initialisation des fichier pour les données des services web vide
            localFilesManager = new LocalFilesManager(getBaseContext());
            localFilesManager.saveLocallyFile(Communication.FILE_NEWS_INIT, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NEWS_INIT_AR, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NEWS_INIT_EN, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NAME_DOSSIER, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NAME_DOSSIER_EN, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NAME_DOSSIER_AR, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NAME_PLUS_LUS, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NAME_PLUS_LUS_EN, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NAME_PLUS_LUS_AR, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NAME_CATEGORIES, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NAME_CATEGORIES_AR, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NAME_CATEGORIES_EN, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NAME_PIERE, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NAME_SUPPORTED_COUNTRIES, "");
            localFilesManager.saveLocallyFile(Communication.FILE_NAME_VIDEO, "");
            localFilesManager.saveLocallyFile(Communication.FILE_SURVEY, "");
            localFilesManager.saveLocallyFile(Communication.TEXT, "");

            r2 = new Runnable() {
                @Override
                public void run() {
                    if (active && !pub_cliked) {
                        intent = new Intent(SplashScreenActivity.this, HomeTnActivity.class);
                        intent.putExtra("openURL", openURL);
                        startActivity(intent);
                        finish();

                    }
                }
            };

            // Initialise JsonRequest
            initHorairePriereData();
            final JsonRequestHelper stringRequest = new JsonRequestHelper(this);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    //  get News json file from server
                    stringRequest.makeStringRequestGet(Communication.URL_NEWS_INIT, Communication.FILE_NEWS_INIT, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                            Intent ainte = new Intent();
                            ainte.setAction("com.mdweb.tn.refresh");
                            Log.d("testserviceIntent", "je suis dans reponse ok init data articles splsh");
                            PendingIntent pendingIntente = PendingIntent.getBroadcast(getApplicationContext(), 1, ainte, PendingIntent.FLAG_IMMUTABLE);
                            try {
                                pendingIntente.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    stringRequest.makeStringRequestGet(Communication.URL_NEWS_INIT_AR, Communication.FILE_NEWS_INIT_AR, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                            Intent ainte = new Intent();
                            ainte.setAction("com.mdweb.tn.refresh");
                            Log.d("testserviceIntent", "je suis dans reponse ok init data articles splsh");
                            PendingIntent pendingIntente = PendingIntent.getBroadcast(getApplicationContext(), 1, ainte, PendingIntent.FLAG_IMMUTABLE);
                            try {
                                pendingIntente.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    stringRequest.makeStringRequestGet(Communication.URL_NEWS_INIT_EN, Communication.FILE_NEWS_INIT_EN, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                            Intent ainte = new Intent();
                            ainte.setAction("com.mdweb.tn.refresh");
                            Log.d("testserviceIntent", "je suis dans reponse ok init data articles splsh");
                            PendingIntent pendingIntente = PendingIntent.getBroadcast(getApplicationContext(), 1, ainte, PendingIntent.FLAG_IMMUTABLE);
                            try {
                                pendingIntente.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    //  get Video json file from server
                    stringRequest.makeStringRequestGet(Communication.URL_VIDEO, Communication.FILE_NAME_VIDEO, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    //  get Dossier json file from server
                    stringRequest.makeStringRequestGet(Communication.URL_DOSSIER, Communication.FILE_NAME_DOSSIER, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    stringRequest.makeStringRequestGet(Communication.URL_DOSSIER_AR, Communication.FILE_NAME_DOSSIER_AR, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    stringRequest.makeStringRequestGet(Communication.URL_DOSSIER_EN, Communication.FILE_NAME_DOSSIER_EN, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    //  get Categories json file from server
                    stringRequest.makeStringRequestGet(Communication.URL_CATEGORIES, Communication.FILE_NAME_CATEGORIES, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    stringRequest.makeStringRequestGet(Communication.URL_CATEGORIES_AR, Communication.FILE_NAME_CATEGORIES_AR, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    stringRequest.makeStringRequestGet(Communication.URL_CATEGORIES_EN, Communication.FILE_NAME_CATEGORIES_EN, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    // get plusLus json file from server
                    stringRequest.makeStringRequestGet(Communication.URL_PLUS_LUS, Communication.FILE_NAME_PLUS_LUS, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    stringRequest.makeStringRequestGet(Communication.URL_PLUS_LUS_AR, Communication.FILE_NAME_PLUS_LUS_AR, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    stringRequest.makeStringRequestGet(Communication.URL_PLUS_LUS_EN, Communication.FILE_NAME_PLUS_LUS_EN, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    stringRequest.makeStringRequestGet(Communication.URL_BLAGUE, Communication.FILE_NAME_BLAGUES, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    stringRequest.makeStringRequestGet(Communication.URL_Pub, Communication.FILE_NAME_PUB, new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                            Log.d("responseJson", response);
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                        }
                    });

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                }
            }.execute();
        }
    }

    public void getData(int type) {
        if (type == 0) {
            if (handlerMain != null && runPub != null) {
                handlerMain.postDelayed(runPub, Constant.duration_before_show_presentation);
            }

            if (handlerPub != null && r2 != null) {
                handlerPub.postDelayed(r2, 5000);
            }
        } else {
            if (handlerPub != null && r2 != null) {
                handlerPub.postDelayed(r2, 5000);
            }
        }
    }

    public void animate() {
        // set animation to background
        backgroundAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_bounce);
        splashScreenView.startAnimation(backgroundAnimation);

        backgroundAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Démarrer l'animation de la progress bar
                animationProgressBar();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPub() {
        if (isActive != 0) {
            if (pubToShow == 2) {
                showServerPub();
                getData(0);
            } else if (pubToShow == 1) {
                showAdmobPub();
                getData(0);
            } else {
                if (!isLoadedAd) {
                    isLoadedAd = !isLoadedAd;
                    loadHuaweiAd();
                }
            }
        } else {
            getData(0);
        }
    }

    public void getDataPubFromServeur() {
        final JSONObject jsonObject1 = new LocallyFiles(SplashScreenActivity.this)
                .getFileContent(Communication.FILE_NAME_PUB);
        try {
            if (jsonObject1 != null) {
                url_image_pub = jsonObject1.getJSONObject("pub").getString("image");
                link_pub = jsonObject1.getJSONObject("pub").getString("link");
                nameCampagne = jsonObject1.getJSONObject("pub").getString("nameCampagne");
                isActive = jsonObject1.getJSONObject("pub").getInt("isActive");
                pubToShow = jsonObject1.getJSONObject("pub").getInt("pubToShow");
            } else {
                Log.d("SplashScreenTest", "pub failer");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showAdmobPub() {
        view_pub = (SplashScreenActivity.this).getLayoutInflater().inflate(R.layout.pub_start_layout_ad, null);
        relativeLayout = view_pub.findViewById(R.id.relative_ad);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        BannerView huaweiBannerView = new BannerView(this);
        huaweiBannerView.setAdId("l4yj9fiyhp");
        huaweiBannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_320_100);

        // Ajouter un listener pour débugger
        huaweiBannerView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d("DEBUG_HUAWEI_BANNER", "Banner ad LOADED!");
            }

            @Override
            public void onAdFailed(int errorCode) {
                Log.e("DEBUG_HUAWEI_BANNER", "Banner ad FAILED with code: " + errorCode);
            }
        });

        relativeLayout.addView(huaweiBannerView, params);

        // Charger l'ad
        AdParam adParam = new AdParam.Builder().build();
        huaweiBannerView.loadAd(adParam);

        runPub = new Runnable() {
            @Override
            public void run() {
                if (dialog != null)
                    dialog.dismiss();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashScreenActivity.this);
                alertDialog.setView(view_pub);
                alertDialog.setCancelable(false);

                if (!((Activity) SplashScreenActivity.this).isFinishing()) {
                    dialog = alertDialog.show();

                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setGravity(Gravity.CENTER);
                    }
                }
            }
        };
    }

    public void showServerPub() {
        runPub = new Runnable() {
            @Override
            public void run() {
                if (dialog != null)
                    dialog.dismiss();

                if (!url_image_pub.equals("")) {
                    pub.getLayoutParams().height = getScreenWidth();
                    pub.getLayoutParams().width = getScreenHeight();
                    pub.requestLayout();

                    Glide.with(getApplicationContext())
                            .load(url_image_pub)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }

                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashScreenActivity.this);
                                    alertDialog.setView(view_pub_serveur);
                                    alertDialog.setCancelable(false);

                                    if (!((Activity) SplashScreenActivity.this).isFinishing()) {
                                        dialog = alertDialog.show();
                                        Application.getInstance().trackScreenView(SplashScreenActivity.this, "Pub intro");

                                        if (dialog.getWindow() != null) {
                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                                                    WindowManager.LayoutParams.WRAP_CONTENT);
                                            dialog.getWindow().setGravity(Gravity.CENTER);
                                        }
                                    }
                                    return false;
                                }
                            })
                            .into(pub);

                    pub.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pub_cliked = true;
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link_pub));
                            startActivity(browserIntent);
                        }
                    });
                }
            }
        };
    }

    public void initSupportedCountryForHorairePriere(Utils utils, String url_supported_country, String fileName) {
        final JsonRequestHelper stringRequest = new JsonRequestHelper(this);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                stringRequest.makeStringRequestGet(url_supported_country, fileName, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {
                        Log.d("responseJson", response);
                        Intent ainte = new Intent();
                        ainte.setAction("com.mdweb.tn.refresh");
                        Log.d("testserviceIntent", "je suis dans reponse ok init data priere");
                        PendingIntent pendingIntente = PendingIntent.getBroadcast(getApplicationContext(), 1, ainte, PendingIntent.FLAG_IMMUTABLE);
                        try {
                            pendingIntente.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        }.execute();
    }

    public void initHorairePriereData() {
        final JsonRequestHelper stringRequest = new JsonRequestHelper(this);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                stringRequest.makeStringRequestGet(Communication.URL_SUPPORTED_COUNTRIES, Communication.FILE_NAME_SUPPORTED_COUNTRIES, new ResponseCompleteInterface() {
                    @Override
                    public void onResponseComplete(String response) {
                        Log.d("responseJson", response);
                        Intent ainte = new Intent();
                        ainte.setAction("com.mdweb.tn.refresh");
                        Log.d("testserviceIntent", "je suis dans reponse ok init data priere");
                        PendingIntent pendingIntente = PendingIntent.getBroadcast(getApplicationContext(), 1, ainte, PendingIntent.FLAG_IMMUTABLE);
                        try {
                            pendingIntente.send();
                            utils = new Utils(getBaseContext());
                            countriesArrayList = new DataParser().getListCountries(utils.getStringFromFile(Communication.FILE_NAME_SUPPORTED_COUNTRIES), getBaseContext());
                            for (Country country : countriesArrayList) {
                                localFilesManager.saveLocallyFile(country.getPriereFileName(), "");
                                initSupportedCountryForHorairePriere(utils, country.getPriereURL(), country.getPriereFileName());
                            }
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onResponseError(VolleyError volleyError) {
                    }
                });

                utils = new Utils(getBaseContext());
                countriesArrayList = new DataParser().getListCountries(utils.getStringFromFile(Communication.FILE_NAME_SUPPORTED_COUNTRIES), getBaseContext());
                for (Country country : countriesArrayList) {
                    localFilesManager.saveLocallyFile(country.getPriereFileName(), "");
                    stringRequest.makeStringRequestGet(country.getPriereURL(), country.getPriereFileName(), new ResponseCompleteInterface() {
                        @Override
                        public void onResponseComplete(String response) {
                            Log.d("responseJson", response);
                            Intent ainte = new Intent();
                            ainte.setAction("com.mdweb.tn.refresh");
                            Log.d("testserviceIntent", "je suis dans reponse ok init data priere");
                            PendingIntent pendingIntente = PendingIntent.getBroadcast(getApplicationContext(), 1, ainte, PendingIntent.FLAG_IMMUTABLE);
                            try {
                                pendingIntente.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onResponseError(VolleyError volleyError) {
                            Log.d("volleyError", volleyError.getMessage());
                        }
                    });
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        }.execute();
    }

    private void showInterstitialAd() {
        if (huaweiInterstitialHAd != null && huaweiInterstitialHAd.isLoaded()) {
            huaweiInterstitialHAd.show(this);
        }
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            showInterstitialAd();
        }

        @Override
        public void onAdFailed(int errorCode) {
            getData(0);
            animationProgressBar();
        }

        @Override
        public void onAdClosed() {
            getData(0);
            animationProgressBar();
        }

        @Override
        public void onAdClicked() {
        }

        @Override
        public void onAdLeave() {
        }

        @Override
        public void onAdOpened() {
        }

        @Override
        public void onAdImpression() {
        }
    };

    private void loadInterstitialAd() {
        AdParam adParam = new AdParam.Builder().build();
        huaweiInterstitialHAd.loadAd(adParam);
    }
}