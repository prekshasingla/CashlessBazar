package com.example.prekshasingla.cashlessbazar;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {

    private static SharedPreferenceUtils mSharedPreferenceUtils;
    protected Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;

    private SharedPreferenceUtils(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
    }

    public static synchronized SharedPreferenceUtils getInstance(Context context) {

        if (mSharedPreferenceUtils == null) {
            mSharedPreferenceUtils = new SharedPreferenceUtils(context.getApplicationContext());
        }
        return mSharedPreferenceUtils;
    }

    public void setName(String value) {
        mSharedPreferencesEditor.putString("loginName", value);
        mSharedPreferencesEditor.commit();
    }

    public void setMobile(String value) {
        mSharedPreferencesEditor.putString("loginMobile", value);
        mSharedPreferencesEditor.commit();
    }

    public void setCId(int value) {
        mSharedPreferencesEditor.putInt("loginCId", value);
        mSharedPreferencesEditor.commit();
    }

    public void setEmail(String value) {
        mSharedPreferencesEditor.putString("loginEmail", value);
        mSharedPreferencesEditor.commit();
    }

    public void setUsername(String value) {
        mSharedPreferencesEditor.putString("loginUsername", value);
        mSharedPreferencesEditor.commit();
    }

    public void setAddress(String value) {
        mSharedPreferencesEditor.putString("loginAddress", value);
        mSharedPreferencesEditor.commit();
    }

    public void setCBTPBalance(int value) {
        mSharedPreferencesEditor.putInt("loginCBTP_Balance", value);
        mSharedPreferencesEditor.commit();
    }

    public void setRewardBalance(int value) {
        mSharedPreferencesEditor.putInt("loginReward_Balance", value);
        mSharedPreferencesEditor.commit();
    }

    public void setType(String value) {
        mSharedPreferencesEditor.putString("loginType", value);
        mSharedPreferencesEditor.commit();
    }


    public String getStringValue(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }


    public int getIntValue(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }


    public long getLongValue(String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    public boolean getBoolanValue(String keyFlag, boolean defaultValue) {
        return mSharedPreferences.getBoolean(keyFlag, defaultValue);
    }


    public void removeKey(String key) {
        if (mSharedPreferencesEditor != null) {
            mSharedPreferencesEditor.remove(key);
            mSharedPreferencesEditor.commit();
        }
    }


    public void clear() {
        mSharedPreferencesEditor.clear().commit();
    }
}
