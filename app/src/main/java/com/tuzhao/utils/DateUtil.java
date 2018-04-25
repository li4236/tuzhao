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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tianzhili.www.myselfsdk.pickerview.view.WheelTime.dateFormat;

/**
 * Created by TZL12 on 2017/9/15.
 */

public class DateUtil {

    private static final String TAG = "DateUtil";

    private static SimpleDateFormat sAllDateFormat;

    private static SimpleDateFormat sYearToDayFormat;

    private static SimpleDateFormat sDayDateFormat;

    private static SimpleDateFormat sExceptSecondsFormat;

    private static Date sDate;

    private static Calendar sCalendar;

    private static Calendar sSpecialToadyStartCalendar;

    private static Calendar sSpecialTodayEndCalendar;

    private static SimpleDateFormat getAllDateFormat() {
        if (sAllDateFormat == null) {
            sAllDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        return sAllDateFormat;
    }

    public static SimpleDateFormat getYearToDayFormat() {
        if (sYearToDayFormat == null) {
            sYearToDayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }
        return sYearToDayFormat;
    }

    private static SimpleDateFormat getDayDateFormat() {
        if (sDayDateFormat == null) {
            sDayDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        }
        return sDayDateFormat;
    }

    private static SimpleDateFormat getYearToMinutesFormat() {
        if (sExceptSecondsFormat == null) {
            sExceptSecondsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        }
        return sExceptSecondsFormat;
    }

    private static Date getDate() {
        if (sDate == null) {
            sDate = new Date();
        }
        return sDate;
    }

    private static Calendar getCalendar() {
        if (sCalendar == null) {
            sCalendar = Calendar.getInstance();
        }
        return sCalendar;
    }

    public static Calendar getSpecialTodayStartCalendar() {
        if (sSpecialToadyStartCalendar == null) {
            sSpecialToadyStartCalendar = getYearToMinuteCalendar("2018-04-24 00:00");
        }
        return sSpecialToadyStartCalendar;
    }

    public static Calendar getSpecialTodayEndCalendar() {
        if (sSpecialTodayEndCalendar == null) {
            sSpecialTodayEndCalendar = getYearToMinuteCalendar("2018-04-24 23:59");
        }
        return sSpecialTodayEndCalendar;
    }

    /**
     * @param isDetail true(2018-04-19 11:15:00) false(11:15:00)
     */
    public static String getCurrentDate(boolean isDetail) {
        getDate().setTime(System.currentTimeMillis());

        if (isDetail) {
            return getAllDateFormat().format(getDate());
        } else {
            return getDayDateFormat().format(getDate());
        }
    }

    /**
     * @param date 格式yyyy-MM-dd HH:ss
     * @return HH:ss
     */
    public static String getHourWithMinutes(String date) {
        return date.substring(date.indexOf(" ") + 1, date.length());
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param pauseDate 暂停时间，格式为yyyy-MM-dd
     * @return true（暂停时间在开始时间和结束时间之间）
     */
    public static boolean isInPauseDate(String startDate, String endDate, String pauseDate) {
        if (pauseDate.equals("-1")) {
            return false;
        }

        Calendar startCalendar = getYearToDayCalendar(startDate, true);
        Calendar endCalendar = getYearToDayCalendar(endDate, true);
        String[] pause = pauseDate.split(",");
        Calendar pauseCalendar;
        for (int i = 0; i < pause.length; i++) {
            pauseCalendar = getYearToDayCalendar(pause[i], false);
            if (startCalendar.compareTo(pauseCalendar) <= 0 && endCalendar.compareTo(pauseCalendar) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param startCalendar 开始时间的日历，格式为yyyy-MM-dd HH:mm，可通过getYearToDayCalendar(startDate,true)获取
     * @param endCalendar   结束时间，格式为yyyy-MM-dd HH:mm,可通过getYearToDayCalendar(startDate,true)获取
     * @param pauseDate     暂停时间，格式为yyyy-MM-dd
     * @return true（暂停时间在开始时间和结束时间之间）
     */
    public static boolean isInPauseDate(Calendar startCalendar, Calendar endCalendar, String pauseDate) {
        boolean inPauseDate = false;
        Calendar pauseCalendar = getYearToDayCalendar(pauseDate, false);
        if (startCalendar.compareTo(pauseCalendar) <= 0 && endCalendar.compareTo(pauseCalendar) >= 0) {
            inPauseDate = true;
        }
        return inPauseDate;
    }

    /**
     * @param startCalendar 开始时间的日历，格式为yyyy-MM-dd HH:mm，可通过getYearToDayCalendar(startDate,true)获取
     * @param endCalendar   结束时间，格式为yyyy-MM-dd HH:mm,可通过getYearToDayCalendar(startDate,true)获取
     * @param shareDate     共享时间，格式为yyyy-MM-dd,yyyy-MM-dd(包括了开始共享的时间和结束共享的时间)
     * @return true（开始时间和结束时间之间在共享时间之内）
     */
    public static boolean isInShareDate(Calendar startCalendar, Calendar endCalendar, String shareDate) {
        boolean inPauseDate = false;
        String[] date = shareDate.split(",");
        Calendar startShareCalendar = getYearToDayCalendar(date[0], false);
        Calendar endShareCalendar = getYearToDayCalendar(date[1], false);

        if (startCalendar.compareTo(startShareCalendar) >= 0 && endCalendar.compareTo(endShareCalendar) <= 0) {
            inPauseDate = true;
        }
        return inPauseDate;
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param shareDate 共享时间，格式为yyyy-MM-dd - yyyy-MM-dd(包括了开始共享的时间和结束共享的时间)
     * @return true（开始时间和结束时间之间在共享时间之内）
     */
    public static boolean isInShareDate(String startDate, String endDate, String shareDate) {
        if (shareDate.equals("-1") || shareDate.equals("")) {
            return true;
        }

        String[] date = shareDate.split(" - ");
        Calendar startCalendar = getYearToDayCalendar(startDate, true);
        Calendar endCalendar = getYearToDayCalendar(endDate, true);
        Calendar startShareCalendar = getYearToDayCalendar(date[0], false);
        Calendar endShareCalendar = getYearToDayCalendar(date[1], false);

        return startCalendar.compareTo(startShareCalendar) >= 0 && endCalendar.compareTo(endShareCalendar) <= 0;
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param shareDays 每周共享的星期，
     * @return 停车时间是否在停车位的共享星期内
     */
    public static boolean isInShareDay(String startDate, String endDate, String shareDays) {
        //如果是同一天则只需要判断当天是否共享
        if (startDate.split(" ")[0].equals(endDate.split(" ")[0])) {
            int parkDay = getDayOfWeek(startDate, true);
            if (shareDays.split(",")[parkDay - 1].charAt(0) != '1') {
                return false;
            }
        } else {
            String[] days = shareDays.split(",");
            if (getDateDistance(startDate, endDate) >= 6) {
                //如果停车时间超过6天则需要判断是否整个星期都共享
                for (int i = 0; i < days.length; i++) {
                    if (days[i].charAt(0) != '1') {
                        return false;
                    }
                }
            } else {
                int startDay = getDayOfWeek(startDate, true);
                int endDay = getDayOfWeek(endDate, true);
                if (startDay < endDay) {
                    //如果停车时间是在同一个星期内则只需要判断停车的星期是否都在共享的星期内
                    for (int i = startDay; i <= endDay; i++) {
                        if (days[i - 1].charAt(0) != '1') {
                            return false;
                        }
                    }
                } else {
                    //如果是停车时间是在两个星期之间的，则判断停车的星期是否都在共享的星期内
                    HashSet<Integer> hashSet = new HashSet<>();
                    for (int i = startDay; i <= 7; i++) {
                        hashSet.add(i);
                    }
                    for (int i = 1; i <= endDay; i++) {
                        hashSet.add(i);
                    }
                    for (Integer integer : hashSet) {
                        if (days[integer - 1].charAt(0) != '1') {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * @param orderDate 逗号隔开(2018-04-19 17:00*2018-04-19 19:00,2018-04-21 08:00*2018-04-22 05:00)
     * @return true(停车时间在预约时间内)
     */
    public static boolean isInOrderDate(String startDate, String endDate, String orderDate) {
        Log.e(TAG, "isInOrderDate: " + orderDate);
        if (orderDate.equals("-1") || orderDate.equals("")) {
            return false;
        }

        String[] orderDates = orderDate.split(",");
        Calendar[] calendars = new Calendar[orderDates.length * 2];
        for (int i = 0; i < orderDates.length; i++) {
            calendars[i * 2] = getYearToMinuteCalendar(orderDates[i].substring(0, orderDates[i].indexOf("*")));
            calendars[i * 2 + 1] = getYearToMinuteCalendar(orderDates[i].substring(orderDates[i].indexOf("*") + 1, orderDates[i].length()));
        }

        Calendar startCalendar = getYearToMinuteCalendar(startDate);
        Calendar endCalendar = getYearToMinuteCalendar(endDate);
        for (int i = 0; i < calendars.length; i += 2) {
            if (startCalendar.compareTo(calendars[i]) >= 0 && startCalendar.compareTo(calendars[i + 1]) < 0
                    || endCalendar.compareTo(calendars[i]) > 0 && endCalendar.compareTo(calendars[i + 1]) <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param startDate 比较的开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   比较的结束时间，格式为yyyy-MM-dd HH:mm
     * @return 返回两个时间段相差的天数，因为是根据时间戳来算的如果两个时间段相差的时间毫秒不够一天的毫秒还是回返回0
     */
    public static int getDateDistance(String startDate, String endDate) {
        SimpleDateFormat dateFormat = getYearToMinutesFormat();
        Date start = new Date();
        Date end = new Date();
        try {
            start = dateFormat.parse(startDate);
            end = dateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) ((end.getTime() / 1000 - start.getTime() / 1000) / 3600 / 24);
    }

    /**
     * @param containHourAndMinute true(格式为yyyy-MM-dd HH:mm)   false(格式为yyyy-MM-dd)
     * @return 获取当天是星期几
     */
    private static int getDayOfWeek(String date, boolean containHourAndMinute) {
        int day;
        Calendar calendar = getCalendar();
        try {
            if (containHourAndMinute) {
                calendar.setTime(getYearToMinutesFormat().parse(date));
            } else {
                calendar.setTime(getYearToDayFormat().parse(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            day = 7;
        }
        return day;
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param shareTime 共享时间段(08:00 - 12:00,14:00 - 18:00) 注：最多3个，用“,”隔开
     * @return true(共享时间段内至少有一个包含开始时间和结束时间的)
     */
    public static long isInShareTime(String startDate, String endDate, String shareTime) {
        Log.e(TAG, "isInShareTime shareTime: " + shareTime);
        if (shareTime.equals("-1")) {
            //如果是全天共享的那直接就可以了
            return getTimeDistance(startDate, endDate);
        } else if (getDateDistance(startDate, endDate) < 1) {
            System.out.println("isInShareTime: leastOneDay");
            //停车时长不大于24小时
            Calendar startTime = getYearToMinuteCalendar(startDate);
            Calendar endTime = getYearToMinuteCalendar(endDate);

            //获取停车位共享的各个时间段
            String[] times = shareTime.split(",");

            if (!isInSameDay(startDate, endDate)) {
                Calendar[] calendars = new Calendar[times.length * 2];
                for (int i = 0; i < times.length; i++) {
                    calendars[i * 2] = getHighCalendar(startTime, times[i].substring(0, times[i].indexOf(" - ")));
                    calendars[i * 2 + 1] = getHighCalendar(startTime, times[i].substring(times[i].indexOf(" - ") + 3, times[i].length()));
                    if (calendars[i * 2].compareTo(calendars[i * 2 + 1]) >= 0) {
                        calendars[i * 2 + 1].add(Calendar.DAY_OF_MONTH, 1);
                    }
                }

                for (int i = 0; i < calendars.length; i += 2) {
                    if (startTime.compareTo(calendars[i]) >= 0 && endTime.compareTo(calendars[i + 1]) <= 0) {
                        return calendars[i + 1].getTimeInMillis() - calendars[i].getTimeInMillis();
                    }
                }
            } else {
                List<Calendar> list = new ArrayList<>(times.length * 2);
                Calendar startCalendar;
                Calendar endCalendar;
                for (int i = 0; i < times.length; i++) {
                    startCalendar = getHighCalendar(startTime, times[i].substring(0, times[i].indexOf(" - ")));
                    endCalendar = getHighCalendar(startTime, times[i].substring(times[i].indexOf(" - ") + 3, times[i].length()));
                    list.add(startCalendar);
                    list.add(endCalendar);

                    if (startCalendar.compareTo(endCalendar) >= 0) {
                        list.set(list.size() - 1, getTodayEndCalendar(startCalendar));
                        list.add(getTodayStartCalendar(endCalendar));
                        list.add((Calendar) endCalendar.clone());
                    }
                }

                for (int i = 0; i < list.size(); i += 2) {
                    if (startTime.compareTo(list.get(i)) >= 0 && endTime.compareTo(list.get(i + 1)) <= 0) {
                        return list.get(i + 1).getTimeInMillis() - list.get(i).getTimeInMillis();
                    }
                }

            }
        }
        return 0;
    }

    /**
     * @param startDate 格式为yyyy-MM-dd HH:mm
     * @param endDate   格式为yyyy-MM-dd HH:mm
     * @return true(两天是在同一天)
     */
    private static boolean isInSameDay(String startDate, String endDate) {
        return startDate.split(" ")[0].endsWith(endDate.split(" ")[1]);
    }

    /**
     * @param startTime 格式为yyyy-MM-dd HH:mm
     * @param endTime   格式为yyyy-MM-dd HH:mm
     * @return 两个时间段的时间差
     */
    private static long getTimeDistance(String startTime, String endTime) {
        try {
            return getYearToMinutesFormat().parse(endTime).getTime() - getYearToMinutesFormat().parse(startTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param containHourAndMinute true(格式为yyyy-MM-dd HH:mm)   false(格式为yyyy-MM-dd)
     * @return 格式为yyyy-MM-dd对应的Calendar,时分秒和毫秒都会清零
     */
    public static Calendar getYearToDayCalendar(String date, boolean containHourAndMinute) {
        Log.e(TAG, "getYearToDayCalendar: " + date);
        Calendar calendar = Calendar.getInstance();
        String[] yearToDay = date.split("-");
        calendar.set(Calendar.YEAR, Integer.valueOf(yearToDay[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(yearToDay[1]));
        if (containHourAndMinute) {
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(yearToDay[2].substring(0, yearToDay[2].indexOf(" "))));
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(yearToDay[2]));
        }
        initHourToMilli(calendar);
        return calendar;
    }

    /**
     * @param date 格式为yyyy-MM-dd HH:mm
     * @return 格式为yyyy-MM-dd对应的Calendar
     */
    public static Calendar getYearToMinuteCalendar(String date) {
        Calendar calendar = Calendar.getInstance();
        String[] yearToMinute = date.split("-");
        calendar.set(Calendar.YEAR, Integer.valueOf(yearToMinute[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(yearToMinute[1]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(yearToMinute[2].substring(0, yearToMinute[2].indexOf(" "))));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].indexOf(" ") + 1, yearToMinute[2].indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].indexOf(":") + 1, yearToMinute[2].length())));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * @param containYear true(date格式为yyyy-MM-dd HH:mm)   false(date格式为HH:mm)
     */
    private static Calendar getHourMinuteCalendar(String date, boolean containYear) {
        Calendar calendar = Calendar.getInstance();
        String time;
        if (containYear) {
            time = date.split(" ")[1];
        } else {
            time = date;
        }
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time.substring(0, time.indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(time.substring(time.indexOf(":") + 1, time.length())));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 将calendar的时分秒和毫秒都清零，否则比较的时候会因为毫秒不一致而导致结果出错
     */
    public static void initHourToMilli(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static String printCalendar(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }

    /**
     * @param startDate //开始停车时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   //结束停车时间，格式为yyyy-MM-dd HH:mm
     * @param highDate  //高峰停车时间，格式为HH:mm - HH:mm
     * @param highFee   //高峰时段单价(xx.xx元/分钟)
     * @param lowFee    //低峰时段单价(xx.xx元/分钟)
     * @return 预估停车应交费用
     */
    public static double caculateParkFee(String startDate, String endDate, String highDate, double highFee, double lowFee) {
        double result;
        Calendar startDateCalendar = getYearToMinuteCalendar(startDate);
        Calendar endDateCalendar = getYearToMinuteCalendar(endDate);
        String[] parkStartDate = startDate.split(" ");
        String[] parkEndDate = endDate.split(" ");
        String[] highDates = highDate.split(" - ");
        boolean highDateInSameDay = isHighDateInSameDay(highDate);
        Calendar hightStartCalendar;
        Calendar hightEndCalendar;
        int parkDay = getDateDistance(startDate, endDate);
        if (parkDay >= 1) {
            Log.e(TAG, "caculateParkFee: 1");
            //停车时间超过一天
            int hightTotalMinutes;
            hightStartCalendar = getHighCalendar(startDateCalendar, highDates[0]);

            if (highDateInSameDay) {
                Log.e(TAG, "caculateParkFee: 2");
                //判断高峰期是否同一天判断出高峰期结束的时间
                hightEndCalendar = getHighCalendar(startDateCalendar, highDates[1]);
                hightTotalMinutes = getDateMinutes(hightStartCalendar, hightEndCalendar);
            } else {
                Log.e(TAG, "caculateParkFee: 3");
                hightEndCalendar = getHighCalendar(startDateCalendar, highDates[1], 1);
                hightTotalMinutes = getDateMinutes(hightStartCalendar, getTodayEndCalendar(hightStartCalendar)) +
                        getDateMinutes(getTodayStartCalendar(hightEndCalendar), hightEndCalendar);
            }
            Log.e(TAG, "caculateParkFee hightTotalMinutes: " + hightTotalMinutes + " lowTotalMinutes:" + (1440 - hightTotalMinutes));
            result = hightTotalMinutes * highFee + (1440 - hightTotalMinutes) * lowFee;     //一天所需的费用为高峰时段加上低峰时段的全部费用
            Log.e(TAG, "caculateParkFee oneDayResult: " + result);

            result *= parkDay;  //停了parkDay那么多天所需的费用
            Log.e(TAG, "caculateParkFee: parkDayResult:" + result);

            startDateCalendar = getSpecialCalendar(startDateCalendar, endDateCalendar);         //把停车开始时间改到最后那天的开始时间,22号12:00
            hightStartCalendar = getSpecialCalendar(hightStartCalendar, startDateCalendar);     //高峰开始时间为22号xx:xx
            hightEndCalendar = getSpecialCalendar(hightEndCalendar, endDateCalendar);           //高峰结束时间为22号xx:xx

            if (isStartAndEndInSameDay(startDateCalendar, endDateCalendar)) {
                Log.e(TAG, "caculateParkFee: 4");
                //比如从20号12:00停到22号15:00，我们还需要计算12:00到15:00的费用
                if (!highDateInSameDay) {
                    Log.e(TAG, "caculateParkFee: 5");
                    //如果高峰期的时段不在同一天则把高峰期的结尾时间再推后一天
                    //hightEndCalendar.add(Calendar.DAY_OF_MONTH, 1);                     //高峰结束时间为23号xx:xx
                }
                result += calculateSameDay(startDateCalendar, endDateCalendar, hightStartCalendar, hightEndCalendar, highFee, lowFee);
                Log.e(TAG, "caculateParkFee: result5:" + result);
            } else {
                Log.e(TAG, "caculateParkFee: 6");
                //比如从20号12:00停到22号10:00，则还需要计算21号12:00到22号10:00的费用
                startDateCalendar.add(Calendar.DAY_OF_MONTH, -1);                       //把停车开始时间改到最后那天的前一天,21号12:00
                if (highDateInSameDay) {
                    Log.e(TAG, "caculateParkFee: 7");
                    hightEndCalendar.add(Calendar.DAY_OF_MONTH, -1);                     //高峰结束时间为21号xx:xx
                }
                hightStartCalendar = getSpecialCalendar(hightStartCalendar, startDateCalendar);     //高峰开始时间为22号xx:xx
                hightEndCalendar = getSpecialCalendar(hightEndCalendar, startDateCalendar);           //高峰结束时间为22号xx:xx
                result += calculateDifferentDay(startDateCalendar, endDateCalendar, hightStartCalendar, hightEndCalendar, highFee, lowFee);
            }

        } else {
            if (parkStartDate[0].equals(parkEndDate[0])) {
                Log.e(TAG, "caculateParkFee: 8");
                //停车时间为同一天
                hightStartCalendar = getHighCalendar(startDateCalendar, highDates[0]);
                hightEndCalendar = getHighCalendar(startDateCalendar, highDates[1]);
                /*if (highDateInSameDay) {
                    Log.e(TAG, "caculateParkFee: 9");
                    //判断高峰期是否同一天判断出高峰期结束的时间
                } else {
                    Log.e(TAG, "caculateParkFee: 10");
                    hightEndCalendar = getHighCalendar(startDateCalendar, highDates[1], 1);
                }*/
                result = calculateSameDay(startDateCalendar, endDateCalendar, hightStartCalendar, hightEndCalendar, highFee, lowFee);
                Log.e(TAG, "caculateParkFee result: " + result);
            } else {
                Log.e(TAG, "caculateParkFee: 11");
                //停车时间跨了一天的
                hightStartCalendar = getHighCalendar(startDateCalendar, highDates[0]);
                hightEndCalendar = getHighCalendar(startDateCalendar, highDates[1]);
                result = calculateDifferentDay(startDateCalendar, endDateCalendar, hightStartCalendar, hightEndCalendar, highFee, lowFee);
            }
        }

        return result / 60;
    }

    /**
     * @param highDate 格式为HH:mm - HH:mm
     * @return true(两个时间在同一天)
     */
    private static boolean isHighDateInSameDay(String highDate) {
        return getHourMinuteCalendar(highDate.substring(0, highDate.indexOf(" - ")), false).compareTo
                (getHourMinuteCalendar(highDate.substring(highDate.indexOf(" - ") + 3, highDate.length()), false)) <= 0;
    }

    /**
     * @return true(开始时间在结束时间之前, 仅比较时分)
     */
    private static boolean isStartAndEndInSameDay(Calendar startCalendar, Calendar endCalendar) {
        Calendar startClone = (Calendar) startCalendar.clone();
        startClone.set(Calendar.YEAR, 2018);
        startClone.set(Calendar.MONTH, 4);
        startClone.set(Calendar.DAY_OF_MONTH, 20);

        Calendar endClone = (Calendar) endCalendar.clone();
        endClone.set(Calendar.YEAR, 2018);
        endClone.set(Calendar.MONTH, 4);
        endClone.set(Calendar.DAY_OF_MONTH, 20);
        return startClone.compareTo(endClone) <= 0;
    }

    /**
     * @param date     参照的那一天，格式为yyyy-MM-dd HH:mm
     * @param highDate 高峰期的时分，格式为HH:mm
     */
    private static Calendar getHighCalendar(Calendar date, String highDate) {
        Calendar calendar = (Calendar) date.clone();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(highDate.substring(0, highDate.indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(highDate.substring(highDate.indexOf(":") + 1, highDate.length())));
        return calendar;
    }

    /**
     * @param date     参照的那一天，格式为yyyy-MM-dd HH:mm
     * @param highDate 高峰期的时分，格式为HH:mm
     * @param nextDays 比参照的那一天往后推迟几天
     */
    private static Calendar getHighCalendar(Calendar date, String highDate, int nextDays) {
        Calendar calendar = (Calendar) date.clone();
        calendar.add(Calendar.DAY_OF_MONTH, nextDays);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(highDate.substring(0, highDate.indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(highDate.substring(highDate.indexOf(":") + 1, highDate.length())));
        return calendar;
    }

    /**
     * @return 把originCalendar的年月日转化为targetCalendar的年月日
     */
    private static Calendar getSpecialCalendar(Calendar originCalendar, Calendar targetCalendar) {
        originCalendar.set(Calendar.YEAR, targetCalendar.get(Calendar.YEAR));
        originCalendar.set(Calendar.MONTH, targetCalendar.get(Calendar.MONTH));
        originCalendar.set(Calendar.DAY_OF_MONTH, targetCalendar.get(Calendar.DAY_OF_MONTH));
        return originCalendar;
    }

    /**
     * 传进来的高峰时段的年月日都为开始停车时间的年月日
     *
     * @return 计算同一天所需交纳的费用
     */
    private static double calculateSameDay(Calendar startDate, Calendar endDate, Calendar highStartCalendar, Calendar highEndCalendar, double highFee, double lowFee) {
        double result;
        if (highStartCalendar.compareTo(highEndCalendar) <= 0) {
            //高峰时段在同一天
            Log.e(TAG, "calculateSameDay1");
            result = getHighAndParkInSameDay(startDate, endDate, highStartCalendar, highEndCalendar, highFee, lowFee);
        } else {
            //高峰时段不在同一天     2018-4-23 12:00-2018-4-24 08:00
            if (highEndCalendar.compareTo(startDate) <= 0) {
                Log.e(TAG, "calculateSameDay3");
                //停车时间为 2018-4-23 10:00-14:00,不包括第二天的高峰时段的
                Calendar todayHighEndCalendar = getTodayEndCalendar(highStartCalendar);
                result = getHighAndParkInSameDay(startDate, endDate, highStartCalendar, todayHighEndCalendar, highFee, lowFee);
            } else if (highEndCalendar.compareTo(startDate) > 0 && highEndCalendar.compareTo(endDate) <= 0) {
                Log.e(TAG, "calculateSameDay: 4");
                //停车时间的一部分在高峰时段的第二天,    2018-4-23 06:00-13:00
                Calendar todayStartCalendar = getTodayStartCalendar(startDate);
                Calendar todayHighEndCalendar = getTodayEndCalendar(highStartCalendar);
                result = getHighAndParkInSameDay(startDate, highEndCalendar, todayStartCalendar, highEndCalendar, highFee, lowFee); //计算2018-4-23 06:00-2018-4-23 08:00
                result += getHighAndParkInSameDay(highEndCalendar, endDate, highStartCalendar, todayHighEndCalendar, highFee, lowFee);  //2018-4-23 08:00-2018-4-23 11:00
            } else {
                Log.e(TAG, "calculateSameDay: 5");
                //停车时间全部在第二天的高峰时段的  2018-4-23 06:00-07:00
                Calendar todayStartCalendar = getTodayStartCalendar(startDate);
                result = getHighAndParkInSameDay(startDate, endDate, todayStartCalendar, highEndCalendar, highFee, lowFee);
            }

        }
        return result;
    }

    /**
     * @param startDate         当天的开始时间
     * @param endDate           第二天的结束时间
     * @param highStartCalendar 高峰时段的开始时间，年月日为开始停车的年月日
     * @param highEndCalendar   高峰时段的结束时间，年月日为开始停车的年月日
     * @return 计算跨了一天所需交纳的费用
     */
    private static double calculateDifferentDay(Calendar startDate, Calendar endDate, Calendar highStartCalendar, Calendar highEndCalendar, double highFee, double lowFee) {
        double result;
        Calendar todayParkEndCalendar = getTodayEndCalendar(startDate);
        Calendar todayParkStartCalendar = getTodayStartCalendar(endDate);
        result = calculateSameDay(startDate, todayParkEndCalendar, highStartCalendar, highEndCalendar, highFee, lowFee);
        Log.e(TAG, "calculateDifferentDay1: " + result);
        highStartCalendar.add(Calendar.DAY_OF_MONTH, 1);
        highEndCalendar.add(Calendar.DAY_OF_MONTH, 1);
        result += calculateSameDay(todayParkStartCalendar, endDate, highStartCalendar, highEndCalendar, highFee, lowFee);
        Log.e(TAG, "calculateDifferentDay2: " + result);
        return result;
    }

    /**
     * 计算高峰时段和停车时间都在同一天的费用
     */
    private static double getHighAndParkInSameDay(Calendar startDate, Calendar endDate, Calendar highStartCalendar, Calendar highEndCalendar,
                                                  double highFee, double lowFee) {
        double result;
        //高峰时段在同一天
        int totalParkMinutes = getDateMinutes(startDate, endDate);  //停车的总分钟数
        Log.e(TAG, "getHighAndParkInSameDay totalParkMinutes:" + totalParkMinutes);
        int totalHighMinutes = getDateMinutes(highStartCalendar, highEndCalendar);  //高峰时段的总分钟数
        if (startDate.compareTo(highStartCalendar) <= 0 && endDate.compareTo(highEndCalendar) >= 0) {
            //停车时间完全包括高峰期的
            result = totalHighMinutes * highFee + (totalParkMinutes - totalHighMinutes) * lowFee;
            Log.e(TAG, "getHighAndParkInSameDay -1: " + "totalHighMinutes * highFee:" + totalHighMinutes * highFee + "  (totalParkMinutes - highFee) * lowFee:" + (totalParkMinutes - totalHighMinutes) * lowFee);
            Log.e(TAG, "getHighAndParkInSameDay -2: " + "totalParkMinutes:" + totalParkMinutes + "  totalHighMinutes:" + totalHighMinutes + "  result:" + result);
        } else if (startDate.compareTo(highStartCalendar) >= 0 && endDate.compareTo(highEndCalendar) <= 0) {
            //停车时间完全在高峰期内的
            result = totalParkMinutes * highFee;
            Log.e(TAG, "getHighAndParkInSameDay 1:" + result);
        } else if (startDate.compareTo(highStartCalendar) < 0 && endDate.compareTo(highStartCalendar) > 0 && endDate.compareTo(highEndCalendar) <= 0) {
            //停车的开始时间不在高峰时段内，但是结束时间在高峰时段内，则高峰时段的开始时间到停车的结束时间为需要交纳高峰费用的时间
            result = getDateMinutes(highStartCalendar, endDate) * highFee + getDateMinutes(startDate, highStartCalendar) * lowFee;
            Log.e(TAG, "getHighAndParkInSameDay 2: " + result);
        } else if (startDate.compareTo(highStartCalendar) >= 0 && startDate.compareTo(highEndCalendar) < 0 && endDate.compareTo(highEndCalendar) > 0) {
            //停车的开始时间小于高峰时段的结束时间，但是停车的时间大于高峰时段的结束时间，则停车的开始时间到高峰时段的结束时间为需要交纳高峰费用的时间
            result = getDateMinutes(startDate, highEndCalendar) * highFee + getDateMinutes(highEndCalendar, endDate) * lowFee;
            Log.e(TAG, "getHighAndParkInSameDay 3: " + result);
        } else {
            result = totalParkMinutes * lowFee;
            Log.e(TAG, "getHighAndParkInSameDay 4:" + result);
        }

        return result;
    }

    /**
     * @param startCalendar 开始时间的日历,格式为yyyy-MM-dd HH:mm
     * @param endCalendar   结束时间的日历,格式为yyyy-MM-dd HH:mm
     * @return 两个时间相差的分钟数
     */
    private static int getDateMinutes(Calendar startCalendar, Calendar endCalendar) {
        if (endCalendar.get(Calendar.HOUR_OF_DAY) == 23 && endCalendar.get(Calendar.MINUTE) == 59) {
            return (int) ((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000 / 60 + 1);
        }
        return (int) ((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000 / 60);
    }

    private static Calendar getTodayEndCalendar(Calendar originCalendar) {
        Calendar calendar = (Calendar) originCalendar.clone();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        return calendar;
    }

    private static Calendar getTodayStartCalendar(Calendar originCalendar) {
        Calendar calendar = (Calendar) originCalendar.clone();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar;
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
    public static String thanTen(int str) {

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
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
            dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
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
        SimpleDateFormat dateFormat = getAllDateFormat();
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
            dateFormat = getYearToMinutesFormat();
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

        SimpleDateFormat dateFormat = getYearToMinutesFormat();
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

        SimpleDateFormat dateFormat = getYearToMinutesFormat();
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
     */
    public ParkFee countCost(String startParkTime, String endParkTime, String lastLeaveTime, String highStartTime, String highDndTime, String highFee, String lowFee, String fine) {

        try {
            int highStartHour, highEndHour, highStartMin, highEndMin;
            Date date = dateFormat.parse(startParkTime);
            Long dayTime;
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
