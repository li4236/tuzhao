package com.tianzhili.www.myselfsdk.update.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.tianzhili.www.myselfsdk.update.UpdateHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UpdateSP {

    private static final String KEY_DOWN_SIZE = "update_download_size";

    public static boolean isIgnore(String nversionandtime) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        String versionandtime = sp.getString("_update_version_ignore", "");
        return !versionandtime.equals("") && versionandtime.substring(0, versionandtime.indexOf(",")).equals(nversionandtime.substring(0, nversionandtime.indexOf(","))) && !(getTimeDifferenceHour(versionandtime.substring(versionandtime.indexOf(",") + 1, versionandtime.length()), nversionandtime.substring(nversionandtime.indexOf(",") + 1, nversionandtime.length())) > 36);
    }

    public static boolean isForced() {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        return sp.getBoolean("_update_version_forced", false);
    }

    public static int getDialogLayout() {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        return sp.getInt("_update_version_layout_id", 0);
    }

    public static int getStatusBarLayout() {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        return sp.getInt("_update_version_status_layout_id", 0);
    }

    public static int getDialogDownloadLayout() {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        return sp.getInt("_update_version_download_layout_id", 0);
    }

    public static void setIgnore(String versionandtime) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("_update_version_ignore", versionandtime);
        editor.apply();
    }

    public static void setForced(boolean def) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("_update_version_forced", def);
        editor.apply();
    }

    public static void setDialogLayout(int def) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("_update_version_layout_id", def);
        editor.apply();
    }

    public static void setStatusBarLayout(int def) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("_update_version_status_layout_id", def);
        editor.apply();
    }

    public static void setDialogDownloadLayout(int def) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("_update_version_download_layout_id", def);
        editor.apply();
    }

    /**
     * 计算相差的小时
     */
    private static float getTimeDifferenceHour(String starTime, String endTime) {
        float hour = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();
            String string = Long.toString(diff);

            float parseFloat = Float.parseFloat(string);

            hour = parseFloat / (60 * 60 * 1000);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hour;
    }

}
