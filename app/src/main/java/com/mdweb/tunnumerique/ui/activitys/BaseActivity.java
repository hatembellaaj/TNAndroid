package com.mdweb.tunnumerique.ui.activitys;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;

import java.util.Locale;

/**
 * Created by Mdweb on 22/11/2018.
 */

public class BaseActivity extends AppCompatActivity {
    Context mCTX;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Utils().checkAdmobPub(this);

    }

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }
//        Log.e("Testlanguagechanged" , "Yes");
////        Locale locale = new Locale(SessionManager.getInstance().getCurrentLang(this));
////        Locale.setDefault(locale);
//       // LocaleHelper.setLocale(this, SessionManager.getInstance().getCurrentLang(this));
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//
//        Configuration
//         conf = getResources().getConfiguration();
//
//        Locale locale = new Locale(SessionManager.getInstance().getCurrentLang(this));
//        Locale.setDefault(locale);
//
//        conf.setLocale(locale); // API 17+ only.
//        conf.setLayoutDirection(locale);
//
//       // onConfigurationChanged(conf);
//        getResources().updateConfiguration(conf,dm);




    public void attachBaseContext(Context base) {
        Resources resources = base.getResources();

        String lng = "fr";
        if(SessionManager.getInstance().getCurrentLang(base)!=null && !SessionManager.getInstance().getCurrentLang(base).isEmpty()){
            lng = SessionManager.getInstance().getCurrentLang(base);
        }
        Locale locale = new Locale(lng);
        Locale.setDefault(locale);
        Configuration config = resources.getConfiguration();
        config.fontScale = 1.10f;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            base = base.createConfigurationContext(config);
        }
        else {
            config.locale = locale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

        super.attachBaseContext(base);
    }

}
