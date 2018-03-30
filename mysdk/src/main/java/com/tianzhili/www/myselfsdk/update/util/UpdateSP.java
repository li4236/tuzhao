package com.tianzhili.www.myselfsdk.update.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.tianzhili.www.myselfsdk.update.UpdateHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateSP {


    public static final String KEY_DOWN_SIZE = "update_download_size";

    public static boolean isIgnore(String nversionandtime) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        String versionandtime = sp.getString("_update_version_ignore", "");
        if (versionandtime == "") {
            return false;
        }else {
            if (versionandtime.substring(0, versionandtime.indexOf(",")).equals(nversionandtime.substring(0, nversionandtime.indexOf(",")))) {
                if (getTimeDifferenceHour(versionandtime.substring(versionandtime.indexOf(",") + 1, versionandtime.length()), nversionandtime.substring(nversionandtime.indexOf(",") + 1, nversionandtime.length())) > 36) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }
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
        editor.commit();
    }

    public static void setForced(boolean def) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("_update_version_forced", def);
        editor.commit();
    }

    public static void setDialogLayout(int def) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("_update_version_layout_id", def);
        editor.commit();
    }

    public static void setStatusBarLayout(int def) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("_update_version_status_layout_id", def);
        editor.commit();
    }

    public static void setDialogDownloadLayout(int def) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("_update_version_download_layout_id", def);
        editor.commit();
    }

    /**
     * 计算相差的小时
     *
     * @param starTime
     * @param endTime
     * @return
     */
    public static float getTimeDifferenceHour(String starTime, String endTime) {
        float hour = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
