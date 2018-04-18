package com.tuzhao.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.tuzhao.publicmanager.TimeManager;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tianzhili.www.myselfsdk.pickerview.view.WheelTime.dateFormat;

/**
 * Created by TZL12 on 2017/9/15.
 */

public class DateUtil {

    private static SimpleDateFormat mDetailDateFormat;

    private static SimpleDateFormat mDayDateFormat;

    private static Date mDate;

    private static SimpleDateFormat getDetailDateFormat() {
        if (mDetailDateFormat == null) {
            mDetailDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        return mDetailDateFormat;
    }

    private static SimpleDateFormat getDayDateFormat() {
        if (mDayDateFormat == null) {
            mDayDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        }
        return mDayDateFormat;
    }

    private static Date getDate() {
        if (mDate == null) {
            mDate = new Date();
        }
        return mDate;
    }

    public static String getCurrentDate(boolean isDetail) {
        getDate().setTime(System.currentTimeMillis());

        if (isDetail) {
            return getDetailDateFormat().format(getDate());
        } else {
            return getDayDateFormat().format(getDate());
        }
    }

    /**
     * 获取当前时间
     */
    public String getNowTime(boolean isdetail) {
        String timeString;
        Time time = new Time();
        time.setToNow();
        String year = thanTen(time.year);
        String month = thanTen(time.month + 1);
        String monthDay = thanTen(time.monthDay);
        String hour = thanTen(time.hour);
        String minute = thanTen(time.minute);

        if (isdetail) {
            timeString = year + "-" + month + "-" + monthDay + " " + hour + ":"
                    + minute;
        } else {
            timeString = year + "-" + month + "-" + monthDay;
        }
        return timeString;
    }

    public Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return simpleDateFormat.parse(dateString, position);
    }

    /**
     * 十以下的数加零
     */
    public String thanTen(int str) {

        String string;

        if (str < 10) {
            string = "0" + str;
        } else {

            string = "" + str;

        }
        return string;
    }

    /**
     * 计算相差的小时
     *
     * @param starTime
     * @param endTime
     */
    public float getTimeDifferenceHour(String starTime, String endTime) {
        float hour = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();
            String string = Long.toString(diff);

            float parseFloat = Float.parseFloat(string);

            hour = parseFloat / (60 * 60 * 1000);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hour;
    }

    /**
     * 计算相差的分钟
     *
     * @param starTime
     * @param endTime
     * @return
     */
    public int getTimeDifferenceMinute(String starTime, String endTime, boolean issample) {
        int minute = 0;
        SimpleDateFormat dateFormat;
        if (issample) {
            dateFormat = new SimpleDateFormat("HH:mm",Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
        }
        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();
            int parseInt = (int) diff;

            minute = (int) (diff / (60 * 1000));

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return minute;
    }

    /**
     * 计算相差的秒
     *
     * @param starTime
     * @param endTime
     * @return
     */
    public int getTimeDifferenceMinuteMoreDetail(String starTime, String endTime) {
        int secend = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();

            secend = (int) (diff / 1000);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return secend;
    }

    /**
     * 与当前时间比较早晚
     *
     * @param time 需要比较的时间
     * @return 输入的时间比现在时间晚则返回true
     */
    public boolean compareNowTime(String time, boolean isDetail) {
        boolean isDayu = false;
        SimpleDateFormat dateFormat;
        if (isDetail) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }

        try {
            Date parse = dateFormat.parse(time);
            Date parse1 = dateFormat.parse(TimeManager.getInstance().getNowTime(true, false));

            if (time.equals(getNowTime(false))) {
                isDayu = true;
            } else {
                long diff = parse1.getTime() - parse.getTime();
                if (diff < 0) {
                    isDayu = true;
                } else {
                    isDayu = false;
                }
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return isDayu;
    }

    /**
     * 比较两个时间
     *
     * @param starTime  开始时间
     * @param endString 结束时间
     * @return 结束时间大于开始时间返回true，否则反之֮
     */
    public boolean compareTwoTime(String starTime, String endString, boolean isDetail) {
        boolean isDayu = false;
        SimpleDateFormat dateFormat;
        if (isDetail) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }

        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endString);

            long diff = parse1.getTime() - parse.getTime();
            if (diff >= 0) {
                isDayu = true;
            } else {
                isDayu = false;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isDayu;
    }

    /**
     * 判断time是否在from，to之内
     *
     * @param thetime   指定日期
     * @param starttime 开始日期
     * @param endtime   结束日期
     * @return
     */
    public boolean betweenStartAndEnd(String thetime, String starttime, String endtime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date time = dateFormat.parse(thetime);
            Date start = dateFormat.parse(starttime);
            Date end = dateFormat.parse(endtime);

            if ((time.getTime() - start.getTime()) >= 0 && (end.getTime() - time.getTime()) > 0) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断区间是否在开始到结束时间之内
     *
     * @param thetime1  指定开始日期
     * @param starttime 开始日期
     * @param endtime   结束日期
     * @return
     */
    public boolean isTheIntervalBeginorEnd(String thetime1, String thetime2, String starttime, String endtime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date time1 = dateFormat.parse(thetime1);
            Date time2 = dateFormat.parse(thetime2);
            Date start = dateFormat.parse(starttime);
            Date end = dateFormat.parse(endtime);

            if ((time1.getTime() - start.getTime()) >= 0 && (end.getTime() - time2.getTime()) >= 0) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断time是否在from，to之内
     *
     * @param thetime   指定日期
     * @param starttime 开始日期
     * @param endtime   结束日期
     * @return
     */
    public boolean betweenStartAndEndSimple(String thetime, String starttime, String endtime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date time = dateFormat.parse(thetime);
            Date start = dateFormat.parse(starttime);
            Date end = dateFormat.parse(endtime);

            if ((time.getTime() - start.getTime()) >= 0 && (end.getTime() - time.getTime()) > 0) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public String addTime(String thetime, int addtime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        if (!thetime.equals("") && addtime > 0) {
            try {
                Date date = dateFormat.parse(thetime);
                return dateFormat.format(new Date(date.getTime() + addtime * 60 * 1000));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 计算停车费用
     *
     * @param startParkTime 开始停车的时间 格式为yyyy-MM-dd HH:mm
     * @param endParkTime   现在结束停车的时间 格式为yyyy-MM-dd HH:mm
     * @param lastLeaveTime 所允许的最迟离开时间 格式为yyyy-MM-dd HH:mm
     * @param highStartTime 高峰的开始时间 HH:mm
     * @param highDndTime   低峰的开始时间 HH:mm
     * @param highFee       0.0
     * @param lowFee        0.0
     * @return
     */
    public ParkFee countCost(String startParkTime, String endParkTime, String lastLeaveTime, String highStartTime, String highDndTime, String highFee, String lowFee, String fine) {

        try {
            int highStartHour, highEndHour, highStartMin, highEndMin;
            Date date = dateFormat.parse(startParkTime);
            Long dayTime = null;
            highStartHour = Integer.parseInt(highStartTime.substring(0, highStartTime.indexOf(":")));
            highEndHour = Integer.parseInt(highDndTime.substring(0, highDndTime.indexOf(":")));
            highStartMin = Integer.parseInt(highStartTime.substring(highStartTime.indexOf(":") + 1, highStartTime.length()));
            highEndMin = Integer.parseInt(highDndTime.substring(highDndTime.indexOf(":") + 1, highDndTime.length()));

            String hjTongTime, hjFeiTongQTime, hjFeiTongTime;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            hjTongTime = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);//当天
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
            hjFeiTongQTime = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);//前一天
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 2);
            hjFeiTongTime = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);//后一天

            int sw = 0;
            boolean isOuttime = false;

            if (compareTwoTime(endParkTime, lastLeaveTime, true)) {
                //未超时
                Log.e("未超时", "哈哈哈");
                if (highStartHour < highEndHour) {
                    //同天
                    sw = 1;
                } else if (highStartHour > highEndHour) {
                    //正常跨天
                    sw = 2;
                } else if (highStartHour == highEndHour) {
                    if (highStartMin < highEndMin) {
                        //奇葩同天
                        sw = 1;
                    } else {
                        //奇葩跨天
                        sw = 2;
                    }
                }
            } else {
                isOuttime = true;
                if (highStartHour < highEndHour) {
                    //同天
                    sw = 1;
                } else if (highStartHour > highEndHour) {
                    //正常跨天
                    sw = 2;
                } else if (highStartHour == highEndHour) {
                    if (highStartMin < highEndMin) {
                        //奇葩同天
                        sw = 1;
                    } else {
                        //奇葩跨天
                        sw = 2;
                    }
                }
            }

            switch (sw) {
                case 1:
                    Log.e("同天", "哈哈哈");
                    if (betweenStartAndEnd(startParkTime, hjTongTime + " " + highStartTime, hjTongTime + " " + highDndTime)) {
                        //开始时间在高峰时段内
                        Log.e("开始时间在当天的高峰时段内", "哈哈哈");
                        Date dt = dateFormat.parse(hjTongTime + " 00:00");
                        dayTime = dt.getTime();
                        int hightMinCount = 0, lowMinCount = 0;
                        String time1 = startParkTime, time2 = hjTongTime + " " + highDndTime;
                        Log.e("开始时间在高峰时段内", "time1" + time1 + "   time2" + time2);
                        boolean isHigh = true;
                        while (true) {
                            if (!compareTwoTime(endParkTime, time2, true)) {
                                Log.e("开始时间在高峰时段内", "执行了while1");
                                if (isHigh) {
                                    hightMinCount = hightMinCount + getTimeDifferenceMinute(time1, time2, false);
                                    time1 = time2;
                                    dayTime += 86400000;
                                    String d = dateFormat.format(dayTime);
                                    Log.e("开始时间在高峰时段内", "执行了while1" + d);
                                    time2 = d.substring(0, d.indexOf(" ")) + " " + highStartTime;
                                } else {
                                    lowMinCount = lowMinCount + getTimeDifferenceMinute(time1, time2, false);
                                    time1 = time2;
                                    String d = dateFormat.format(dayTime);
                                    time2 = d.substring(0, d.indexOf(" ")) + " " + highDndTime;
                                }
                                Log.e("开始时间在高峰时段内", "time1 " + time1 + "   time2 " + time2);
                                isHigh = !isHigh;
                            } else {
                                Log.e("开始时间在高峰时段内", "执行了while2");
                                if (isHigh) {
                                    hightMinCount = hightMinCount + getTimeDifferenceMinute(time1, endParkTime, false);
                                } else {
                                    lowMinCount = lowMinCount + getTimeDifferenceMinute(time1, endParkTime, false);
                                }
                                break;
                            }
                        }
                        Log.e("开始时间在高峰时段内", "哈哈哈" + hightMinCount + "   " + lowMinCount);
                        float parkfee = hightMinCount * (new Float(highFee) / 60) + lowMinCount * (new Float(lowFee) / 60);
                        if (isOuttime) {
                            float outfee = getTimeDifferenceMinute(lastLeaveTime, endParkTime, false) * (new Float(fine) / 60);
                            return new ParkFee((float) (Math.round(parkfee * 100)) / 100, (float) (Math.round(outfee * 100)) / 100);
                        } else {
                            return new ParkFee((float) (Math.round(parkfee * 100)) / 100, 0);
                        }
                    } else if (betweenStartAndEnd(startParkTime, hjFeiTongQTime + " " + highDndTime, hjTongTime + " " + highStartTime)) {
                        //开始时间在前一天到当天的低峰时段内
                        Log.e("开始时间在前一天到当天的低峰时段内", "哈哈哈");
                        Date dt = dateFormat.parse(hjTongTime + " 00:00");
                        dayTime = dt.getTime();
                        int hightMinCount = 0, lowMinCount = 0;
                        String time1 = startParkTime, time2 = hjTongTime + " " + highStartTime;
                        Log.e("开始时间在前一天到当天的低峰时段内", "time1" + time1 + "   time2" + time2);
                        boolean isHigh = false;
                        while (true) {
                            if (!compareTwoTime(endParkTime, time2, true)) {
                                Log.e("开始时间在前一天到当天的低峰时段内", "执行了while1");
                                if (isHigh) {
                                    hightMinCount = hightMinCount + getTimeDifferenceMinute(time1, time2, false);
                                    time1 = time2;
                                    dayTime += 86400000;
                                    String d = dateFormat.format(dayTime);
                                    Log.e("开始时间在前一天到当天的低峰时段内", "执行了while1" + d);
                                    time2 = d.substring(0, d.indexOf(" ")) + " " + highStartTime;
                                } else {
                                    lowMinCount = lowMinCount + getTimeDifferenceMinute(time1, time2, false);
                                    time1 = time2;
                                    String d = dateFormat.format(dayTime);
                                    time2 = d.substring(0, d.indexOf(" ")) + " " + highDndTime;
                                }
                                Log.e("开始时间在前一天到当天的低峰时段内", "time1 " + time1 + "   time2 " + time2);
                                isHigh = !isHigh;
                            } else {
                                Log.e("开始时间在前一天到当天的低峰时段内", "执行了while2");
                                if (isHigh) {
                                    hightMinCount = hightMinCount + getTimeDifferenceMinute(time1, endParkTime, false);
                                } else {
                                    lowMinCount = lowMinCount + getTimeDifferenceMinute(time1, endParkTime, false);
                                }
                                break;
                            }
                        }
                        Log.e("开始时间在前一天到当天的低峰时段内", "哈哈哈" + hightMinCount + "   " + lowMinCount);
                        float parkfee = hightMinCount * (new Float(highFee) / 60) + lowMinCount * (new Float(lowFee) / 60);
                        if (isOuttime) {
                            float outfee = getTimeDifferenceMinute(lastLeaveTime, endParkTime, false) * (new Float(fine) / 60);
                            return new ParkFee((float) (Math.round(parkfee * 100)) / 100, (float) (Math.round(outfee * 100)) / 100);
                        } else {
                            return new ParkFee((float) (Math.round(parkfee * 100)) / 100, 0);
                        }
                    } else if (betweenStartAndEnd(startParkTime, hjTongTime + " " + highDndTime, hjFeiTongTime + " " + highStartTime)) {
                        //开始时间在当天到后一天的高峰时段内
                        Log.e("开始时间在当天到后一天的低峰时段内", "哈哈哈");
                        Date dt = dateFormat.parse(hjFeiTongTime + " 00:00");
                        dayTime = dt.getTime();
                        int hightMinCount = 0, lowMinCount = 0;
                        String time1 = startParkTime, time2 = hjFeiTongTime + " " + highStartTime;
                        Log.e("开始时间在当天到后一天的低峰时段内", "time1" + time1 + "   time2" + time2);
                        boolean isHigh = false;
                        while (true) {
                            if (!compareTwoTime(endParkTime, time2, true)) {
                                Log.e("开始时间在当天到后一天的低峰时段内", "执行了while1");
                                if (isHigh) {
                                    hightMinCount = hightMinCount + getTimeDifferenceMinute(time1, time2, false);
                                    time1 = time2;
                                    dayTime += 86400000;
                                    String d = dateFormat.format(dayTime);
                                    Log.e("开始时间在当天到后一天的低峰时段内", "执行了while1" + d);
                                    time2 = d.substring(0, d.indexOf(" ")) + " " + highStartTime;
                                } else {
                                    lowMinCount = lowMinCount + getTimeDifferenceMinute(time1, time2, false);
                                    time1 = time2;
                                    String d = dateFormat.format(dayTime);
                                    time2 = d.substring(0, d.indexOf(" ")) + " " + highDndTime;
                                }
                                Log.e("开始时间在当天到后一天的低峰时段内", "time1 " + time1 + "   time2 " + time2);
                                isHigh = !isHigh;
                            } else {
                                Log.e("开始时间在当天到后一天的低峰时段内", "执行了while2");
                                if (isHigh) {
                                    hightMinCount = hightMinCount + getTimeDifferenceMinute(time1, endParkTime, false);
                                } else {
                                    lowMinCount = lowMinCount + getTimeDifferenceMinute(time1, endParkTime, false);
                                }
                                break;
                            }
                        }
                        Log.e("开始时间在当天到后一天的低峰时段内", "哈哈哈" + hightMinCount + "   " + lowMinCount);
                        float parkfee = hightMinCount * (new Float(highFee) / 60) + lowMinCount * (new Float(lowFee) / 60);
                        if (isOuttime) {
                            float outfee = getTimeDifferenceMinute(lastLeaveTime, endParkTime, false) * (new Float(fine) / 60);
                            return new ParkFee((float) (Math.round(parkfee * 100)) / 100, (float) (Math.round(outfee * 100)) / 100);
                        } else {
                            return new ParkFee((float) (Math.round(parkfee * 100)) / 100, 0);
                        }
                    }
                case 2:
                    Log.e("跨天", "哈哈哈");
                    if (betweenStartAndEnd(startParkTime, hjFeiTongQTime + " " + highStartTime, hjTongTime + " " + highDndTime)) {
                        //开始时间在前一天到当天的高峰时段内
                        Log.e("开始时间在前一天到当天的高峰时段内", "哈哈哈");
                        Date dt = dateFormat.parse(hjTongTime + " 00:00");
                        dayTime = dt.getTime();
                        int hightMinCount = 0, lowMinCount = 0;
                        String time1 = startParkTime, time2 = hjTongTime + " " + highDndTime;
                        Log.e("开始时间在前一天到当天的高峰时段内", "time1" + time1 + "   time2" + time2);
                        boolean isHigh = true;
                        while (true) {
                            if (!compareTwoTime(endParkTime, time2, true)) {
                                Log.e("开始时间在前一天到当天的高峰时段内", "执行了while1");
                                if (isHigh) {
                                    hightMinCount = hightMinCount + getTimeDifferenceMinute(time1, time2, false);
                                    time1 = time2;
                                    String d = dateFormat.format(dayTime);
                                    Log.e("开始时间在前一天到当天的高峰时段内", "执行了while1" + d);
                                    time2 = d.substring(0, d.indexOf(" ")) + " " + highStartTime;
                                } else {
                                    lowMinCount = lowMinCount + getTimeDifferenceMinute(time1, time2, false);
                                    time1 = time2;
                                    dayTime += 86400000;
                                    String d = dateFormat.format(dayTime);
                                    time2 = d.substring(0, d.indexOf(" ")) + " " + highDndTime;
                                }
                                Log.e("开始时间在前一天到当天的高峰时段内", "time1 " + time1 + "   time2 " + time2);
                                isHigh = !isHigh;
                            } else {
                                Log.e("开始时间在前一天到当天的高峰时段内", "执行了while2");
                                if (isHigh) {
                                    hightMinCount = hightMinCount + getTimeDifferenceMinute(time1, endParkTime, false);
                                } else {
                                    lowMinCount = lowMinCount + getTimeDifferenceMinute(time1, endParkTime, false);
                                }
                                break;
                            }
                        }
                        Log.e("开始时间在前一天到当天的高峰时段内", "哈哈哈" + hightMinCount + "   " + lowMinCount);
                        float parkfee = hightMinCount * (new Float(highFee) / 60) + lowMinCount * (new Float(lowFee) / 60);
                        if (isOuttime) {
                            float outfee = getTimeDifferenceMinute(lastLeaveTime, endParkTime, false) * (new Float(fine) / 60);
                            return new ParkFee((float) (Math.round(parkfee * 100)) / 100, (float) (Math.round(outfee * 100)) / 100);
                        } else {
                            return new ParkFee((float) (Math.round(parkfee * 100)) / 100, 0);
                        }
                    } else if (betweenStartAndEnd(startParkTime, hjTongTime + " " + highStartTime, hjFeiTongTime + " " + highDndTime)) {
                        //开始时间在当天到后一天的高峰时段内
                        Log.e("开始时间在当天到后一天的高峰时段内", "哈哈哈");
                        Date dt = dateFormat.parse(hjFeiTongTime + " 00:00");
                        dayTime = dt.getTime();
                        int hightMinCount = 0, lowMinCount = 0;
                        String time1 = startParkTime, time2 = hjFeiTongTime + " " + highDndTime;
                        Log.e("开始时间在当天到后一天的高峰时段内", "time1" + time1 + "   time2" + time2);
                        boolean isHigh = true;
                        while (true) {
                            if (!compareTwoTime(endParkTime, time2, true)) {
                                Log.e("开始时间在当天到后一天的高峰时段内", "执行了while1");
                                if (isHigh) {
                                    hightMinCount = hightMinCount + getTimeDifferenceMinute(time1, time2, false);
                                    time1 = time2;
                                    String d = dateFormat.format(dayTime);
                                    Log.e("开始时间在当天到后一天的高峰时段内", "执行了while1" + d);
                                    time2 = d.substring(0, d.indexOf(" ")) + " " + highStartTime;
                                } else {
                                    lowMinCount = lowMinCount + getTimeDifferenceMinute(time1, time2, false);
                                    time1 = time2;
                                    dayTime += 86400000;
                                    String d = dateFormat.format(dayTime);
                                    time2 = d.substring(0, d.indexOf(" ")) + " " + highDndTime;
                                }
                                Log.e("开始时间在当天到后一天的高峰时段内", "time1 " + time1 + "   time2 " + time2);
                                isHigh = !isHigh;
                            } else {
                                Log.e("开始时间在当天到后一天的高峰时段内", "执行了while2");
                                if (isHigh) {
                                    hightMinCount = hightMinCount + getTimeDifferenceMinute(time1, endParkTime, false);
                                } else {
                                    lowMinCount = lowMinCount + getTimeDifferenceMinute(time1, endParkTime, false);
                                }
                                break;
                            }
                        }
                        Log.e("开始时间在当天到后一天的高峰时段内", "哈哈哈" + hightMinCount + "   " + lowMinCount);
                        float parkfee = hightMinCount * (new Float(highFee) / 60) + lowMinCount * (new Float(lowFee) / 60);
                        if (isOuttime) {
                            float outfee = getTimeDifferenceMinute(lastLeaveTime, endParkTime, false) * (new Float(fine) / 60);
                            return new ParkFee((float) (Math.round(parkfee * 100)) / 100, (float) (Math.round(outfee * 100)) / 100);
                        } else {
                            return new ParkFee((float) (Math.round(parkfee * 100)) / 100, 0);
                        }
                    } else if (betweenStartAndEnd(startParkTime, hjTongTime + " " + highDndTime, hjTongTime + " " + highStartTime)) {
                        //开始时间在当天的低峰时段内
                        Log.e("开始时间在当天的低峰时段内", "哈哈哈");
                        Date dt = dateFormat.parse(hjTongTime + " 00:00");
                        dayTime = dt.getTime();
                        int hightMinCount = 0, lowMinCount = 0;
                        String time1 = startParkTime, time2 = hjTongTime + " " + highStartTime;
                        Log.e("开始时间在当天的低峰时段内", "time1" + time1 + "   time2" + time2);
                        boolean isHigh = false;
                        while (true) {
                            if (!compareTwoTime(endParkTime, time2, true)) {
                                Log.e("开始时间在当天的低峰时段内", "执行了while1");
                                if (isHigh) {
                                    hightMinCount = hightMinCount + getTimeDifferenceMinute(time1, time2, false);
                                    time1 = time2;
                                    String d = dateFormat.format(dayTime);
                                    Log.e("开始时间在当天的低峰时段内", "执行了while1" + d);
                                    time2 = d.substring(0, d.indexOf(" ")) + " " + highStartTime;
                                } else {
                                    lowMinCount = lowMinCount + getTimeDifferenceMinute(time1, time2, false);
                                    time1 = time2;
                                    dayTime += 86400000;
                                    String d = dateFormat.format(dayTime);
                                    time2 = d.substring(0, d.indexOf(" ")) + " " + highDndTime;
                                }
                                Log.e("开始时间在当天的低峰时段内", "time1 " + time1 + "   time2 " + time2);
                                isHigh = !isHigh;
                            } else {
                                Log.e("开始时间在当天的低峰时段内", "执行了while2");
                                if (isHigh) {
                                    hightMinCount = hightMinCount + getTimeDifferenceMinute(time1, endParkTime, false);
                                } else {
                                    lowMinCount = lowMinCount + getTimeDifferenceMinute(time1, endParkTime, false);
                                }
                                break;
                            }
                        }
                        Log.e("开始时间在当天的低峰时段内", "哈哈哈" + hightMinCount + "   " + lowMinCount);
                        float parkfee = hightMinCount * (new Float(highFee) / 60) + lowMinCount * (new Float(lowFee) / 60);
                        if (isOuttime) {
                            float outfee = getTimeDifferenceMinute(lastLeaveTime, endParkTime, false) * (new Float(fine) / 60);
                            return new ParkFee((float) (Math.round(parkfee * 100)) / 100, (float) (Math.round(outfee * 100)) / 100);
                        } else {
                            return new ParkFee((float) (Math.round(parkfee * 100)) / 100, 0);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e("停车费用计算错误", e.toString());
        }
        return new ParkFee(0, 0);
    }

    /**
     * costfee 总费用
     * fine_fee超时费用
     * discount_fee 优惠费用
     * discount_id优惠券id
     */
    public class ParkFee {

        public float parkfee, outtimefee;

        public ParkFee(float parkfee, float outtimefee) {
            this.parkfee = parkfee;
            this.outtimefee = outtimefee;
        }
    }

    //米和千米的换算
    public DistanceAndDanwei isMoreThan1000(int i) {
        double distance;
        DistanceAndDanwei distanceAndDanwei;
        if (i >= 1000) {
            distance = ((double) i) / 1000;
            distanceAndDanwei = new DistanceAndDanwei();
            distanceAndDanwei.setDistance(new DecimalFormat("###.0").format((distance)));
            distanceAndDanwei.setDanwei("km");

        } else {
            distanceAndDanwei = new DistanceAndDanwei();
            distanceAndDanwei.setDistance(i + "");
            distanceAndDanwei.setDanwei("m");
        }
        return distanceAndDanwei;
    }

    public class DistanceAndDanwei {
        String distance;
        String danwei;

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getDanwei() {
            return danwei;
        }

        public void setDanwei(String danwei) {
            this.danwei = danwei;
        }
    }

    /**
     * 验证手机号是否正确
     *
     * @param phone_numble 手机号
     * @return
     */
    public static boolean isPhoneNumble(String phone_numble) {
//		Pattern p = Pattern.compile("^(13[0-9]|14[57]|15[0-35-9]|17[6-8]|18[0-9])[0-9]{8}$");
//		Matcher m = p.matcher(phone_numble);
//		return m.matches();
       /* String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phone_numble);*/
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone_numble.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone_numble);
            return m.matches();
        }
    }

    public static boolean isEmail(String string) {
        if (string == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        return m.matches();
    }

    /**
     * 验证输入的是否为正确的密码
     *
     * @param password
     * @return
     */
    public boolean isCorrectPassword(String password) {

        if (password != null && !password.equals("")) {
            if (password.length() >= 8 && password.length() <= 16) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 订单流水号生成
     */
    public synchronized String getOrderNumber(boolean isCharge) {
        if (isCharge) {
            String str = new SimpleDateFormat("mmssSSS").format(new Date());
            str = "CG" + str + new DecimalFormat("0000").format(new Random().nextInt(100000));
            return str;
        } else {
            String str = new SimpleDateFormat("mmssSSS").format(new Date());
            str = "PK" + str + new DecimalFormat("0000").format(new Random().nextInt(100000));
            return str;
        }
    }

    /**
     * 初始化车牌选项卡数据。
     *
     * @param options1Items
     * @param options2Items
     */
    public void initData(ArrayList<String> options1Items, ArrayList<ArrayList<String>> options2Items) {

        //选项1
        options1Items.add("粤");
        options1Items.add("渝");
        options1Items.add("沪");
        options1Items.add("津");
        options1Items.add("川");
        options1Items.add("青");
        options1Items.add("藏");
        options1Items.add("琼");
        options1Items.add("京");
        options1Items.add("贵");
        options1Items.add("闽");
        options1Items.add("吉");
        options1Items.add("晋");
        options1Items.add("蒙");
        options1Items.add("陕");
        options1Items.add("鄂");
        options1Items.add("桂");
        options1Items.add("甘");
        options1Items.add("赣");
        options1Items.add("浙");
        options1Items.add("苏");
        options1Items.add("新");
        options1Items.add("鲁");
        options1Items.add("皖");
        options1Items.add("湘");
        options1Items.add("黑");
        options1Items.add("辽");
        options1Items.add("云");
        options1Items.add("豫");
        options1Items.add("冀");
        options1Items.add("宁");

        //选项2
        ArrayList<String> options2Items_01 = new ArrayList<>();
        options2Items_01.add("A");
        options2Items_01.add("B");
        options2Items_01.add("C");
        options2Items_01.add("D");
        options2Items_01.add("E");
        options2Items_01.add("F");
        options2Items_01.add("G");
        options2Items_01.add("H");
        options2Items_01.add("I");
        options2Items_01.add("J");
        options2Items_01.add("K");
        options2Items_01.add("L");
        options2Items_01.add("M");
        options2Items_01.add("N");
        options2Items_01.add("O");
        options2Items_01.add("P");
        options2Items_01.add("Q");
        options2Items_01.add("R");
        options2Items_01.add("S");
        options2Items_01.add("T");
        options2Items_01.add("U");
        options2Items_01.add("V");
        options2Items_01.add("W");
        options2Items_01.add("X");
        options2Items_01.add("Y");
        options2Items_01.add("Z");


        for (int i = 0; i < options1Items.size(); i++) {
            options2Items.add(options2Items_01);
        }

    }

    /**
     * 初始化停车时长选项卡数据。
     *
     * @param options1Items
     * @param options2Items
     */
    public void initParktimeData(ArrayList<String> options1Items, ArrayList<ArrayList<String>> options2Items, ArrayList<ArrayList<ArrayList<String>>> options3Items) {

        //选项1
        options1Items.add("0");
        options1Items.add("1");
        options1Items.add("2");
        options1Items.add("3");
        options1Items.add("4");
        options1Items.add("5");
        options1Items.add("6");
        options1Items.add("7");

        //选项2
        ArrayList<String> options2Items_01 = new ArrayList<>();
        options2Items_01.add("0");
        options2Items_01.add("1");
        options2Items_01.add("2");
        options2Items_01.add("3");
        options2Items_01.add("4");
        options2Items_01.add("5");
        options2Items_01.add("6");
        options2Items_01.add("7");
        options2Items_01.add("8");
        options2Items_01.add("9");
        options2Items_01.add("10");
        options2Items_01.add("11");
        options2Items_01.add("12");
        options2Items_01.add("13");
        options2Items_01.add("14");
        options2Items_01.add("15");
        options2Items_01.add("16");
        options2Items_01.add("17");
        options2Items_01.add("18");
        options2Items_01.add("19");
        options2Items_01.add("20");
        options2Items_01.add("21");
        options2Items_01.add("22");
        options2Items_01.add("23");

        for (int i = 0; i < options1Items.size(); i++) {
            options2Items.add(options2Items_01);
        }

        ArrayList<ArrayList<String>> options3Items_01 = new ArrayList<>();
        ArrayList<String> options3Items_01_01 = new ArrayList<>();
        //选项3
        options3Items_01_01.add("0");
        options3Items_01_01.add("5");
        options3Items_01_01.add("10");
        options3Items_01_01.add("15");
        options3Items_01_01.add("20");
        options3Items_01_01.add("25");
        options3Items_01_01.add("30");
        options3Items_01_01.add("35");
        options3Items_01_01.add("40");
        options3Items_01_01.add("45");
        options3Items_01_01.add("50");
        options3Items_01_01.add("55");

        for (int i = 0; i < options2Items_01.size(); i++) {
            options3Items_01.add(options3Items_01_01);
        }

        for (int i = 0; i < options3Items_01.size(); i++) {
            options3Items.add(options3Items_01);
        }
    }

    /**
     * 初始化开始停车时间选项卡数据。
     *
     * @param options1Items
     * @param options2Items
     */
    public void initStartParkTimeData(ArrayList<String> options1Items, ArrayList<ArrayList<String>> options2Items, ArrayList<ArrayList<ArrayList<String>>> options3Items) {

        //选项1
        options1Items.add("今天");
        options1Items.add("明天");
        options1Items.add("后天");
        for (int i = 3; i < 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + i);//让日期加1
            options1Items.add((calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日");
        }

        //选项2
        ArrayList<String> options2Items_01 = new ArrayList<>();
        options2Items_01.add("0");
        options2Items_01.add("1");
        options2Items_01.add("2");
        options2Items_01.add("3");
        options2Items_01.add("4");
        options2Items_01.add("5");
        options2Items_01.add("6");
        options2Items_01.add("7");
        options2Items_01.add("8");
        options2Items_01.add("9");
        options2Items_01.add("10");
        options2Items_01.add("11");
        options2Items_01.add("12");
        options2Items_01.add("13");
        options2Items_01.add("14");
        options2Items_01.add("15");
        options2Items_01.add("16");
        options2Items_01.add("17");
        options2Items_01.add("18");
        options2Items_01.add("19");
        options2Items_01.add("20");
        options2Items_01.add("21");
        options2Items_01.add("22");
        options2Items_01.add("23");

        for (int i = 0; i < options1Items.size(); i++) {
            options2Items.add(options2Items_01);
        }

        ArrayList<ArrayList<String>> options3Items_01 = new ArrayList<>();
        ArrayList<String> options3Items_01_01 = new ArrayList<>();
        //选项3
        options3Items_01_01.add("0");
        options3Items_01_01.add("1");
        options3Items_01_01.add("2");
        options3Items_01_01.add("3");
        options3Items_01_01.add("4");
        options3Items_01_01.add("5");
        options3Items_01_01.add("6");
        options3Items_01_01.add("7");
        options3Items_01_01.add("8");
        options3Items_01_01.add("9");
        options3Items_01_01.add("10");
        options3Items_01_01.add("11");
        options3Items_01_01.add("12");
        options3Items_01_01.add("13");
        options3Items_01_01.add("14");
        options3Items_01_01.add("15");
        options3Items_01_01.add("16");
        options3Items_01_01.add("17");
        options3Items_01_01.add("18");
        options3Items_01_01.add("19");
        options3Items_01_01.add("20");
        options3Items_01_01.add("21");
        options3Items_01_01.add("22");
        options3Items_01_01.add("23");
        options3Items_01_01.add("24");
        options3Items_01_01.add("25");
        options3Items_01_01.add("26");
        options3Items_01_01.add("27");
        options3Items_01_01.add("28");
        options3Items_01_01.add("29");
        options3Items_01_01.add("30");
        options3Items_01_01.add("31");
        options3Items_01_01.add("32");
        options3Items_01_01.add("33");
        options3Items_01_01.add("34");
        options3Items_01_01.add("35");
        options3Items_01_01.add("36");
        options3Items_01_01.add("37");
        options3Items_01_01.add("38");
        options3Items_01_01.add("39");
        options3Items_01_01.add("40");
        options3Items_01_01.add("41");
        options3Items_01_01.add("42");
        options3Items_01_01.add("43");
        options3Items_01_01.add("44");
        options3Items_01_01.add("45");
        options3Items_01_01.add("46");
        options3Items_01_01.add("47");
        options3Items_01_01.add("48");
        options3Items_01_01.add("49");
        options3Items_01_01.add("50");
        options3Items_01_01.add("51");
        options3Items_01_01.add("52");
        options3Items_01_01.add("53");
        options3Items_01_01.add("54");
        options3Items_01_01.add("55");
        options3Items_01_01.add("56");
        options3Items_01_01.add("57");
        options3Items_01_01.add("58");
        options3Items_01_01.add("59");

        for (int i = 0; i < options2Items_01.size(); i++) {
            options3Items_01.add(options3Items_01_01);
        }

        for (int i = 0; i < options3Items_01.size(); i++) {
            options3Items.add(options3Items_01);
        }
    }

    public String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
