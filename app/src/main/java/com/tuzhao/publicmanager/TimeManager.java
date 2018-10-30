package com.tuzhao.publicmanager;

import com.tuzhao.utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by TZL12 on 2017/11/13.
 */

public class TimeManager {

    private static TimeManager timeManager = null;
    private long differenceTime;  //时间差值
    private boolean isServerTime = false; //是否是服务器时间

    public static TimeManager getInstance() {

        if (timeManager == null) {

            synchronized (UserManager.class) {

                if (timeManager == null) {

                    timeManager = new TimeManager();
                }
                return timeManager;
            }
        } else {

            return timeManager;
        }
    }

    public String getNowTime(boolean isDetail, boolean isMoreDetail) {
        long nowSystemtime = System.currentTimeMillis();
        SimpleDateFormat format;
        if (isMoreDetail) {
            format = DateUtil.getYearToSecondFormat();
        } else {
            if (isDetail) {
                format = DateUtil.getYearToMinutesFormat();
            } else {
                format = DateUtil.getYearToDayFormat();
            }
        }

        String nowTime;
        if (isServerTime) {
            nowSystemtime = nowSystemtime + differenceTime;
            Date d1 = new Date(nowSystemtime);
            nowTime = format.format(d1);
        } else {
            Date d1 = new Date(System.currentTimeMillis());
            nowTime = format.format(d1);
        }
        return nowTime;
    }

    public void initTime(String serverTime) {
        isServerTime = true;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long newdifferencetime;
        try {
            Date serverDate = df.parse(serverTime);
            newdifferencetime = serverDate.getTime() - System.currentTimeMillis();

            if (Math.abs(newdifferencetime) < Math.abs(differenceTime)) {
                differenceTime = newdifferencetime;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
