package com.paymentwall.pwunifiedsdk.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by nguyen.anh on 6/1/2017.
 */

public class SharedPreferenceManager {

    private String TAG = getClass().getSimpleName();

    private final String PWSDK_PREFERENCES = "com.paymentwall.pwsdk";

    private static SharedPreferenceManager instance;

    private Context context;

    // ==============================================================

    public static final String UI_STYLE = "UI_STYLE";

    private SharedPreferenceManager() {
    }

    /**
     * Constructor
     *
     * @param context
     * @return
     */
    public static SharedPreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceManager();
            instance.context = context;
        }
        return instance;
    }

    public SharedPreferenceManager(Context context) {
        this.context = context;
    }

    //======================== MANAGING FUNCTIONS ==========================

    public void setUIStyle(String styleID) {
        putStringValue(UI_STYLE, styleID);
    }

    public String getUIStyle() {
        if (getStringValue(UI_STYLE).equalsIgnoreCase(""))
            return "saas";
        return getStringValue(UI_STYLE);
    }


    // ======================== UTILITY FUNCTIONS ========================

    /**
     * Save a long integer to SharedPreferences
     *
     * @param key
     * @param n
     */
    public void putLongValue(String key, long n) {
        SharedPreferences pref = context.getSharedPreferences(
                PWSDK_PREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, n);
        editor.commit();
    }

    /**
     * Read a long integer to SharedPreferences
     *
     * @param key
     * @return
     */
    public long getLongValue(String key) {
        SharedPreferences pref = context.getSharedPreferences(
                PWSDK_PREFERENCES, 0);
        return pref.getLong(key, 0);
    }

    /**
     * Save an integer to SharedPreferences
     *
     * @param key
     * @param n
     */
    public void putIntValue(String key, int n) {
        SharedPreferences pref = context.getSharedPreferences(
                PWSDK_PREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, n);
        editor.commit();
    }

    /**
     * Read an integer to SharedPreferences
     *
     * @param key
     * @return
     */
    public int getIntValue(String key) {
        // SmartLog.log(TAG, "Get integer value");
        SharedPreferences pref = context.getSharedPreferences(
                PWSDK_PREFERENCES, 0);
        return pref.getInt(key, 0);
    }


    /**
     * Save an string to SharedPreferences
     *
     * @param key
     * @param s
     */
    public void putStringValue(String key, String s) {
        // SmartLog.log(TAG, "Set string value");
        SharedPreferences pref = context.getSharedPreferences(
                PWSDK_PREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, s);
        editor.commit();
    }

    /**
     * Read an string to SharedPreferences
     *
     * @param key
     * @return
     */
    public String getStringValue(String key) {
        SharedPreferences pref = context.getSharedPreferences(
                PWSDK_PREFERENCES, 0);
        return pref.getString(key, "");
    }

    /**
     * Read an string to SharedPreferences
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getStringValue(String key, String defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(
                PWSDK_PREFERENCES, 0);
        return pref.getString(key, defaultValue);
    }

    /**
     * Save an boolean to SharedPreferences
     *
     * @param key
     * @param key,b
     */
    public void putBooleanValue(String key, Boolean b) {
        SharedPreferences pref = context.getSharedPreferences(
                PWSDK_PREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, b);
        editor.commit();
    }

    /**
     * Read an boolean to SharedPreferences
     *
     * @param key
     * @return
     */
    public boolean getBooleanValue(String key) {
        SharedPreferences pref = context.getSharedPreferences(
                PWSDK_PREFERENCES, 0);
        return pref.getBoolean(key, false);
    }

    /**
     * Save an float to SharedPreferences
     *
     * @param key
     * @param key,f
     */
    public void putFloatValue(String key, float f) {
        SharedPreferences pref = context.getSharedPreferences(
                PWSDK_PREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, f);
        editor.commit();
    }

    /**
     * Read an float to SharedPreferences
     *
     * @param key
     * @return
     */
    public float getFloatValue(String key) {
        SharedPreferences pref = context.getSharedPreferences(
                PWSDK_PREFERENCES, 0);
        return pref.getFloat(key, 0.0f);
    }
}
