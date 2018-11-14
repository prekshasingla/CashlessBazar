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


    public String getName(){
        return mSharedPreferences.getString("loginName",null);
    }
    public String getEmail(){
        return mSharedPreferences.getString("loginEmail",null);
    }
    public String getPhone(){
        return mSharedPreferences.getString("loginMobile",null);
    }

    public void clear() {
        mSharedPreferencesEditor.clear().commit();
    }
}
