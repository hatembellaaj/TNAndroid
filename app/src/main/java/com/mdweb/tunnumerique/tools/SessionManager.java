package com.mdweb.tunnumerique.tools;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mdweb.tunnumerique.tools.shared.Constant;

public class SessionManager {

    private static SessionManager ourInstance;
    // Context
    private Context _context;
    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    /**
     * Create singleton of shared preference
     *
     * @return A shared preference representing the instance of created SharedPreference
     */
    public static SessionManager getInstance() {
        if (null == ourInstance) {
            ourInstance = new SessionManager();
        }
        return ourInstance;
    }

    /**
     * the default constructor
     */
    private SessionManager() {
    }


    public Boolean isSavedGuideInPreferences(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.contains(Constant.PREF_GUIDE);
    }


    public void setSavedGuide(Context context, String save) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.putString(Constant.PREF_GUIDE, save);
        editor.commit();
    }


    public String getSavedGuide(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(Constant.PREF_GUIDE, Constant.KEY_Guide);
    }

    public void setVibreur(Context context, boolean b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putBoolean(Constant.IS_VIBREUR, b);
            editor.commit();
        } else
            Log.d("NullPointer", "nullPointer");

    }

    public boolean IsVibreur(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(Constant.IS_VIBREUR, false);
    }

    public void setSon(Context context, boolean b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putBoolean(Constant.IS_SON, b);
            editor.commit();
        } else
            Log.d("NullPointer", "nullPointer");

    }

    public boolean IsSon(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(Constant.IS_SON, false);
    }


    //Store the  notification state
    public void setNotification(Context context, boolean b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putBoolean(Constant.IS_Notifications, b);
            editor.commit();
        } else
            Log.d("NullPointer", "nullPointer");

    }

    public boolean getNotification(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(Constant.IS_Notifications, true);
    }

    //Store the  notification state
    public void setNotificationUrl(Context context, String b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putString(Constant.Url_Notifications, b);
            editor.commit();
        } else
            Log.d("NullPointer", "nullPointer");

    }

    public String getNotificationUrl(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(Constant.Url_Notifications, "");
    }

    public void setPopNotif(Context context, boolean b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putBoolean(Constant.IS_Notification, b);
            editor.commit();
        } else
            Log.d("NullPointer", "nullPointer");

    }

    public boolean IsPopUpNotif(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(Constant.IS_Notification, true);
    }

    public int getGouvernorateId(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(Constant.idGouvernorate, 1);
    }

    //Store the  notification state
    public void setIdGouvernorate(Context context, int b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putInt(Constant.idGouvernorate, b);
            editor.commit();
        } else
            Log.d("NullPointer", "nullPointer");

    }

    public int getCountryId(Context context, int indexNativeCountry) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(Constant.idCountry, indexNativeCountry);
    }

    public void setIdCountry(Context context, int b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putInt(Constant.idCountry, b);
            editor.commit();
        } else
            Log.d("NullPointer", "nullPointer");

    }

    public void setStateSurvey(Context context, boolean b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putBoolean(Constant.IS_SURVEY, b);
            editor.commit();
        } else
            Log.d("NullPointer", "nullPointer");

    }

    public boolean getStateSurvey(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(Constant.IS_SURVEY, true);
    }


    public void setIdSurvey(Context context, int b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putInt(Constant.ID_SURVEY, b);
            editor.commit();
        } else
            Log.d("NullPointer", "nullPointer");

    }

    public int getIdSurvey(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(Constant.ID_SURVEY, 0);
    }


    public void setLastShownTime(Context context, String b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putString(Constant.TIME_SURVEY, b);
            editor.commit();
        } else
            Log.d("NullPointr", "nullPointer");

    }

    public String getLastShownTime(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(Constant.TIME_SURVEY, "");
    }

    public void firstStartTime(Context context, String b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putString(Constant.TIME_FIRST, b);
            editor.commit();
        } else
            Log.d("NullPointr", "nullPointer");

    }

    public String getFirstStartTime(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(Constant.TIME_FIRST, "");
    }

    public String getTimeAdmob(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString("time_admob", "");
    }

    public void setTimeAdmob(Context context, String b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putString("time_admob", b);
            editor.commit();
        } else
            Log.d("NullPointr", "nullPointer");

    }


    public void activity_onTop(Context context, String b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putString("ActivityTOP", b);
            editor.commit();
        } else
            Log.d("NullPointr", "nullPointer");

    }

    public String getActivityOnTop(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString("ActivityTOP", "");
    }


    public void firstLaunchApp(Context context, String b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putString("FirstLaunch", b);
            editor.commit();
        } else
            Log.d("NullPointr", "nullPointer");

    }

    public String getFirstLaunch(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString("FirstLaunch", "");
    }


    public void setForground(Context context, String b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putString("forground", b);
            editor.commit();
        } else
            Log.d("NullPointr", "nullPointer");

    }

    public String getForground(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString("forground", "back");
    }


    public void setCurrentLng(Context context, String b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putString("lang", b);
            editor.apply();
        } else
            Log.d("NullPointr", "nullPointer");

    }

    public String getCurrentLang(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString("lang", "");
    }


    public void setCurrentMode(Context context, String b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putString("mode", b);
            editor.commit();
        } else
            Log.d("NullPointr", "nullPointer");

    }

    public String getCurrentMode(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString("mode", "auto");
    }


    public void setModeAuomatique(Context context, boolean b) {

        if (context != null) {
            pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            editor.putBoolean("auto", b);
            editor.commit();
        } else
            Log.d("NullPointr", "nullPointer");

    }

    public boolean isModeAuto(Context context) {
        pref = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("auto", true);
    }

}
