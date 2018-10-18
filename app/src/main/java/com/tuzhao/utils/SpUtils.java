package com.tuzhao.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by juncoder on 2018/8/28.
 */
public class SpUtils {

    private SharedPreferences mSharedPreferences;

    private static SpUtils sSpUtils;

    /**
     * 本次启动是否已检查更新
     */
    public static final String ALREADY_CHECK_UPDATE = "AlreadyCheckUpdate";

    /**
     * 忽略更新的版本
     */
    public static final String IGNORE_VERSION = "IgnoreVersion";

    public static SpUtils getInstance(Context context) {
        if (sSpUtils == null) {
            synchronized (SpUtils.class) {
                if (sSpUtils == null) {
                    sSpUtils = new SpUtils(context.getApplicationContext());
                }
            }
        }
        return sSpUtils;
    }

    private SpUtils(Context context) {
        mSharedPreferences = context.getSharedPreferences("tuzhao", Context.MODE_PRIVATE);
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }

}
