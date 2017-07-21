package com.audacityit.finder.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.audacityit.finder.BuildConfig;
import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;

public class SharedPref {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences prefs;


    private static final String FCM_PREF_KEY = "_.FCM_PREF_KEY";
    private static final String FIRST_LAUNCH = "_.FIRST_LAUNCH";
    private static final String INFO_DATA = "_.INFO_DATA_KEY";
    private static final String BUYER_PROFILE = "_.BUYER_PROFILE_KEY";

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MAIN_PREF", Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Preference for first launch
     */
    public void setFirstLaunch(boolean flag) {
        sharedPreferences.edit().putBoolean(FIRST_LAUNCH, flag).apply();
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true);
    }

    /**
     * Preference for Fcm register
     */
    public void setFcmRegId(String fcmRegId) {
        sharedPreferences.edit().putString(FCM_PREF_KEY, fcmRegId).apply();
    }

    public String getFcmRegId() {
        return sharedPreferences.getString(FCM_PREF_KEY, null);
    }

    public boolean isFcmRegIdEmpty() {
        return TextUtils.isEmpty(getFcmRegId());
    }




    /**
     * To save dialog permission state
     */
    public void setNeverAskAgain(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getNeverAskAgain(String key) {
        return sharedPreferences.getBoolean(key, false);
    }


    public void setOpenAppCounter(int val) {
        sharedPreferences.edit().putInt("OPEN_COUNTER_KEY", val).apply();
    }



    public void clearInfoData() {
        sharedPreferences.edit().putString(INFO_DATA, null).apply();
    }



    // info buyer profile data
    public BuyerProfile setBuyerProfile(BuyerProfile buyerProfile) {
        if (buyerProfile == null) return null;
        String json = new Gson().toJson(buyerProfile, BuyerProfile.class);
        sharedPreferences.edit().putString(BUYER_PROFILE, json).apply();
        return getBuyerProfile();
    }

    public void clearBuyerProfile() {
        sharedPreferences.edit().putString(BUYER_PROFILE, null).apply();
    }

    public BuyerProfile getBuyerProfile() {
        String data = sharedPreferences.getString(BUYER_PROFILE, null);
        if (data == null) return null;
        return new Gson().fromJson(data, BuyerProfile.class);
    }
}
