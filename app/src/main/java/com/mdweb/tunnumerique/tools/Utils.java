package com.mdweb.tunnumerique.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.jsonRequest.LocalFilesManager;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.ui.activitys.EnqueteActivity;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Class contain the util function used
 * created by Slama Taieb 19-12-2016, 08:34
 */

public class Utils {

    // attributes
    // context current
    private Context context;
    private AlertDialog dialogNotification;
    private com.huawei.hms.ads.InterstitialAd huaweiInterstitialHAd;


    java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

    // Constructor
    public Utils(Context context) {
        // store our context
        this.context = context;
    }

    public Utils() {
    }

    public DisplayImageOptions getImageLoaderOptionNews() {
        DisplayImageOptions defaultOptions;
        //options of universal image loader
        defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageForEmptyUri((R.drawable.default_img))
                .showImageOnFail((R.drawable.default_img))
                .showImageOnLoading((R.drawable.default_img))
                .displayer(new FadeInBitmapDisplayer(500)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .build();
        ImageLoader.getInstance().init(config);

        return defaultOptions;
    }

    public DisplayImageOptions getImageLoaderOptionTheme() {
        DisplayImageOptions defaultOptions;
        //options of universal image loader
        defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageForEmptyUri((R.drawable.img_default_theme))
                .showImageOnFail((R.drawable.img_default_theme))
                .showImageOnLoading((R.drawable.img_default_theme))
                .displayer(new FadeInBitmapDisplayer(500)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .build();
        ImageLoader.getInstance().init(config);

        return defaultOptions;
    }

    public DisplayImageOptions getImageLoaderOptionDetailsNews() {
        DisplayImageOptions defaultOptions;
        //options of universal image loader
        defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageForEmptyUri((R.drawable.img_default_int))
                .showImageOnFail((R.drawable.img_default_int))
                .showImageOnLoading((R.drawable.img_default_int))
                .displayer(new FadeInBitmapDisplayer(500)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .build();
        ImageLoader.getInstance().init(config);

        return defaultOptions;
    }

    /**
     * The options of Image loader object for pub between match in calendar
     *
     * @return the options of image loader with specific configurations for pub between match in calendar
     */
    public DisplayImageOptions getImageLoaderOptionPub() {
        return     //options of universal image loader
                new DisplayImageOptions.Builder()
                        .cacheInMemory(true)//cache image in memory
                        .cacheOnDisk(true)//cache image in disk
                        .resetViewBeforeLoading(false)
                        .showImageForEmptyUri(R.drawable.default_img)//image showing when uri is empty
                        .showImageOnFail(R.drawable.default_img)//image showing when fail to load image
                        .showImageOnLoading(R.drawable.default_img)
                        .build();//image to show when loading image
    }

    public DisplayImageOptions getImageLoaderOption() {
        return     //options of universal image loader
                new DisplayImageOptions.Builder()
                        .cacheInMemory(true)//cache image in memory
                        .cacheOnDisk(true)//cache image in disk
                        .resetViewBeforeLoading(false)
                        .build();//image to show when loading image
    }

    /////////////////////////////////////////////////////////////////////
    //                   network util function                         //
    // method to check connexion
    public boolean getNetworkState() {
        // get the connectivity manager by calling the system service
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // get the network info from the network manager
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        // return the current state
        return (networkInfo != null);
    }

    /////////////////////////////////////////////////////////////////////
    //                   date util function                            //
    // method to get the name and number of the Month
    public HashMap<String, String> getNameNumMonth(String currentDate) {
        // create the result to return
        HashMap<String, String> result = new HashMap<>();
        // get date object
        Date date = null;
        // get the array contain the name of month
        String[] monthNameArrayFr = context.getResources().getStringArray(R.array.month_arrayFr);
        String[] monthNameArrayAr = context.getResources().getStringArray(R.array.month_arrayAr);
        // define the format of the date to work with it
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.format_date);
        // try catch to hold crash
        try {
            // get and store the current date in the varible date with the format defined
            date = simpleDateFormat.parse(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // get the number of the month
        String numMonth = (String) DateFormat.format(Constant.month_MM, date);
        // test on the num month getted
        if (Integer.parseInt(numMonth) == 1) {
            numMonth = Constant.douze;
        } else {
            numMonth = String.valueOf(Integer.parseInt(numMonth) - 1);
        }
        // put the num of the month
        result.put(Constant.numMonth, numMonth);
        // put the french name of the month
        result.put(Constant.nameMonthFr, monthNameArrayFr[Integer.parseInt(numMonth)]);
        // put the arabic name month
        result.put(Constant.nameMonthAr, monthNameArrayAr[Integer.parseInt(numMonth)]);
        // return the name of month
        return result;
    }

    // method to get the year number
    public String getYearNumber(String currentDate) {
        // get current year number
        Date date = null;
        // define the format to use
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat((Constant.format_date));
        // clause try catch to hold crash
        try {
            // get date object from the string date
            date = simpleDateFormat.parse(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // get the year number
        String yearNumber = (String) DateFormat.format("yy", date);
        // return the yearNumber
        return yearNumber;
    }

    /////////////////////////////////////////////////////////////////////


    // method to get String from file
    public String getStringFromFile(String fileName) {
        // variable to return
        String result = "";
        // instance of the class who manage file
        LocalFilesManager localFilesManager = new LocalFilesManager(context);
        // check the existance of the file

        try {
            if (!localFilesManager.fileExist(fileName))
                throw new FileNotFoundException();
            else
                result = localFilesManager.getFileContentText(fileName);
        } catch (FileNotFoundException e) {
            // your message here.
        }
//
//        if (localFilesManager.fileExist(fileName)) {
//            // get the result from the file name
//            result = localFilesManager.getFileContentText(fileName);
//        }
//        // return the result
        return result;
    }

    // method to get path from the fileName
//    public String getPath(String fileName) {
//        // variable to return
//        String result = "";
//        // instance of the class who manage file
//        LocalFilesManager localFilesManager = new LocalFilesManager(context);
//        // case when the file exist
//        if (localFilesManager.fileExist(fileName)) {
//            // get the absolutly path from the file name
//            result = localFilesManager.getFile(fileName).getAbsolutePath();
//        }
//        // return result
//        return result;
//    }

    // method to get path from the fileName
    public boolean isFileExist(String fileName) {
        // variable to return

        // case when the file exist
        LocalFilesManager localFilesManager = new LocalFilesManager(context);


        try {
            if (!localFilesManager.fileExist(fileName))
                throw new FileNotFoundException();

        } catch (FileNotFoundException e) {
            // your message here.
        }

        return localFilesManager.fileExist(fileName);
        // get the absolutly path from the file name


    }


    /**
     * get current date from calendar
     *
     * @return current date
     */
    public String currentDate() {
        java.text.DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        date = calendar.getTime();
        return dateFormat.format(date);
    }

    /**
     * add one day to date
     *
     * @return date
     */
    public String addDate() {
        java.text.DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        date = calendar.getTime();
        return dateFormat.format(date);

    }

    public String getDayFromDate(String date) {
        String[] parts = date.split("/");
        return parts[0];
    }


    public String getMonthFromDate(String date) {
        String[] parts = date.split("/");
        return parts[1];
    }


    private Bitmap rotateImage(Bitmap source, float angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                    source.getHeight(), matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Copy.
     *
     * @param src the src
     * @param dst the dst
     * @param dir the dir
     * @return true, if successful
     */
    public boolean copy(File src, File dst, File dir) {
        try {

            if (!dir.exists()) {
                dir.mkdirs();
            }

            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Verify if list contain specific element
     *
     * @param newsCollection the list of ranker
     * @param idNews         the indication of news
     * @return boolean value ? contain : not contain
     */
    public boolean containNews(Collection<News> newsCollection, String idNews) {
        for (News news : newsCollection) {
            if (news != null && news.getIdNews().equals(idNews)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resave picture.
     *
     * @param file          the file
     * @param pictureBitmap the picture bitmap
     */
    private void reSavePicture(File file, Bitmap pictureBitmap) {
        OutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);

            pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving
            // the
            // Bitmap
            // to
            // a
            // file
            // compressed
            // as
            // a
            // JPEG
            // with
            // 85%
            // compression
            // rate
            fOut.flush();
            fOut.close(); // do not forget to close the stream

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPopUpNotification(String url) {

        // define builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pop_up_notification, null);
        builder.setView(view);
// instanite the web view
        WebView webView = (WebView) view.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {


            public void onPageFinished(WebView view, String url) {


            }
        });

        webView.loadUrl(url);


        dialogNotification = builder.create();
        dialogNotification.setCancelable(true);

        //dialogGouvernorat.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialogNotification.setCancelable(true);
        dialogNotification.show();


    }


    public void checkSurvey() {
        String firstTime = SessionManager.getInstance().getFirstStartTime(context);

        if ((getDiffernceTime(firstTime) > 300000)) {
            String lastTime = SessionManager.getInstance().getLastShownTime(context);
            if ((lastTime.isEmpty() || (getDiffernceTime(lastTime) > 3600000))) {

                RequestQueue queue = Volley.newRequestQueue(context);


// Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Communication.URL_SURVEY,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Log.d("Survey_DATA", "Test");
                                try {
                                    JSONObject surveyObj = new JSONObject(s);
                                    String urlSurvey = surveyObj.getString("survey_url");
                                    Log.d("Survey_DATA", urlSurvey);

                                    int idSurvey = surveyObj.getInt("survey_id");
                                    int isActive = surveyObj.getInt("isActive");

                                    if (!urlSurvey.isEmpty() && isActive != 0) {
                                        if ((SessionManager.getInstance().getStateSurvey(context) || SessionManager.getInstance().getIdSurvey(context) != idSurvey)
                                        ) {
                                            SessionManager.getInstance().setStateSurvey(context, true);

                                            //  SessionManager.getInstance().setStateSurvey(MainActivity.this,false);
                                            SessionManager.getInstance().setIdSurvey(context, idSurvey);
                                            Date date = new Date();
                                            String strDate = dateFormat.format(date);
                                            SessionManager.getInstance().setLastShownTime(context, strDate);
                                            startEnquete(urlSurvey, context);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d("Survey_DATA", s);

                            }


                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Log.d("Survey_DATA", volleyError.getMessage());

                    }


                });

// Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        }
    }

    public long getDiffernceTime(String last) {

        try {
            Date date1 = dateFormat.parse(last);
            Date date = new Date();

            long dateDiff = date.getTime() - date1.getTime();
            Log.d("DifferenceTime1", date.getTime() + "");
            Log.d("DifferenceTime2", date1.getTime() + "");
            Log.d("DifferenceTime3", last + "");
            Log.d("DifferenceTime", dateDiff + "");
            return dateDiff;


        } catch (ParseException e) {

            e.printStackTrace();
            return 0;

        }


    }

    public void startEnquete(String url, Context context) {
        Intent i = new Intent(context, EnqueteActivity.class);
        i.putExtra("urlSurvey", url);
        context.startActivity(i);
    }


   public void checkAdmobPub(Activity activity) {

            String firstTime = SessionManager.getInstance().getTimeAdmob(activity);

            if (firstTime != null && !firstTime.isEmpty()) {

                if ((getDiffernceTime(firstTime) > 600000)) {
                    loadAd(activity);
                    Date date = new Date();
                    String strDate = dateFormat.format(date);
                    SessionManager.getInstance().setTimeAdmob(activity, strDate);
                }
            }

    }

    public void loadAd(Activity activity) {
        loadHuaweiAd(activity);
   }


    private void showInterstitialAd(Activity activity) {
        // Display the ad.
        if (huaweiInterstitialHAd != null && huaweiInterstitialHAd.isLoaded()) {
            huaweiInterstitialHAd.show(activity);
        }
    }

    private class AdListenerHuawei extends AdListener   {
        private Activity activity;

        AdListenerHuawei(Activity activity){
            this.activity = activity;
        }
        @Override
        public void onAdLoaded() {
            // Called when an ad is loaded successfully.
            Log.e("TestErrorFiled","onAdLoaded");
            showInterstitialAd(activity);
        }
        @Override
        public void onAdFailed(int errorCode) {
            Log.e("TestErrorFiled",errorCode+"");
            // Called when an ad fails to be loaded.


        }
        @Override
        public void onAdClosed() {
            // Called when an ad is closed.

        }
        @Override
        public void onAdClicked() {
            // Called when an ad is clicked.

        }
        @Override
        public void onAdLeave() {
            // Called when an ad leaves an app.

        }
        @Override
        public void onAdOpened() {
            // Called when an ad is opened.

        }
        @Override
        public void onAdImpression() {
            // Called when an ad impression occurs.

        }
    };

    private void loadInterstitialAd() {

        // Load an interstitial ad.
        AdParam adParam = new AdParam.Builder().build();
        huaweiInterstitialHAd.loadAd(adParam);
    }

    public void loadHuaweiAd(Activity activity){

        huaweiInterstitialHAd = new com.huawei.hms.ads.InterstitialAd(activity);
        huaweiInterstitialHAd.setAdId("x1d7ckapea");
        huaweiInterstitialHAd.setAdListener(new AdListenerHuawei(activity));
        loadInterstitialAd();
    }


}
