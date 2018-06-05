package com.tuzhao.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.publicmanager.TimeManager;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private static SimpleDateFormat sYearToSecondFormat;

    private static SimpleDateFormat sYearToDayFormat;

    private static SimpleDateFormat sHourToSecond;

    private static SimpleDateFormat sExceptSecondsFormat;

    private static Date sDate;

    private static Calendar sCalendar;

    private static Calendar sSpecialToadyStartCalendar;

    private static Calendar sSpecialTodayEndCalendar;

    private static SimpleDateFormat getYearToSecondFormat() {
        if (sYearToSecondFormat == null) {
            sYearToSecondFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        return sYearToSecondFormat;
    }

    public static SimpleDateFormat getYearToDayFormat() {
        if (sYearToDayFormat == null) {
            sYearToDayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }
        return sYearToDayFormat;
    }

    public static SimpleDateFormat getHourToSecondFormat() {
        if (sHourToSecond == null) {
            sHourToSecond = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        }
        return sHourToSecond;
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

    /**
     * 因为选择的时间没24:00的，所以当天的结束时间为23:59
     */
    public static Calendar getSpecialTodayEndCalendar() {
        if (sSpecialTodayEndCalendar == null) {
            sSpecialTodayEndCalendar = getYearToMinuteCalendar("2018-04-24 23:59");
        }
        return sSpecialTodayEndCalendar;
    }

    /**
     * @param hourWithMinute HH:mm
     */
    public static Calendar getSpecialCalendar(String hourWithMinute) {
        Calendar calendar = getSpecialTodayStartCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hourWithMinute.substring(0, hourWithMinute.indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(hourWithMinute.substring(hourWithMinute.indexOf(":") + 1, hourWithMinute.length())));
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * @param isDetail true(2018-04-19 11:15:00) false(11:15:00)
     */
    public static String getCurrentDate(boolean isDetail) {
        getDate().setTime(System.currentTimeMillis());

        if (isDetail) {
            return getYearToSecondFormat().format(getDate());
        } else {
            return getHourToSecondFormat().format(getDate());
        }
    }

    /**
     * @return 当前的yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentYearToSecond() {
        getDate().setTime(System.currentTimeMillis());
        return getYearToSecondFormat().format(getDate());
    }

    /**
     * @return 当前的yyyy-MM-dd HH:mm
     */
    public static String getCurrentYearToMinutes() {
        getDate().setTime(System.currentTimeMillis());
        return getYearToMinutesFormat().format(getDate());
    }

    /**
     * @return timeMillis对应的yyyy-MM-dd HH:mm
     */
    public static String getCurrentYearToMinutes(long timeMillis) {
        getDate().setTime(timeMillis);
        return getYearToMinutesFormat().format(getDate());
    }

    /**
     * @param date 格式yyyy-MM-dd HH:ss
     * @return HH:ss
     */
    public static String getHourWithMinutes(String date) {
        return date.substring(date.indexOf(" ") + 1, date.length());
    }

    /**
     * @return HH:ss
     */
    public static String getHourWithMinutes(Calendar calendar) {
        return thanTen(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + thanTen(calendar.get(Calendar.MINUTE));
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param shareDate 共享时间，格式为yyyy-MM-dd - yyyy-MM-dd(包括了开始共享的时间和结束共享的时间)
     * @return 0:(开始时间和结束时间之间不在在共享时间之内) 1:(开始时间和结束时间之间在共享时间之内,但是结束时间刚好是最后共享日期的第二天凌晨的)
     * 2:(开始时间和结束时间之间在共享时间之内)
     */
    public static int isInShareDate(String startDate, String endDate, String shareDate) {
        if (shareDate.equals("-1") || shareDate.equals("")) {
            return 0;
        }

        String[] date = shareDate.split(" - ");
        Calendar startCalendar = getYearToDayCalendar(startDate, true);
        Calendar endCalendar = getYearToDayCalendar(endDate, true);
        Calendar startShareCalendar = getYearToDayCalendar(date[0], false);
        Calendar endShareCalendar = getYearToDayCalendar(date[1], false);

        if (startCalendar.compareTo(startShareCalendar) >= 0 && endCalendar.compareTo(endShareCalendar) <= 0) {
            //在共享的日期内
            return 2;
        } else {
            String[] endHourWithMinutes = endDate.split(" ")[1].split(":");
            if (endHourWithMinutes[0].equals("00") && endHourWithMinutes[1].equals("00")) {
                //如果不在共享日期内，但是结束时间为凌晨的，则把共享结束时间加一天再比较
                endShareCalendar.add(Calendar.DAY_OF_MONTH, 1);
                if (endCalendar.compareTo(endShareCalendar) == 0) {
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param pauseDate 暂停时间，格式为yyyy-MM-dd
     * @return 0:(暂停时间在开始时间和结束时间之间) 1:(结束时间为暂停时间的00:00)
     * 2:(暂停时间不在开始时间和结束时间之间)
     */
    public static int isInPauseDate(String startDate, String endDate, String pauseDate) {
        if (pauseDate.equals("-1")) {
            return 2;
        }

        Calendar startCalendar = getYearToDayCalendar(startDate, true);
        Calendar endCalendar = getYearToDayCalendar(endDate, true);
        String[] pause = pauseDate.split(",");
        Calendar pauseCalendar;
        int inPauseDate = 2;
        for (int i = 0; i < pause.length; i++) {
            pauseCalendar = getYearToDayCalendar(pause[i], false);
            if (startCalendar.compareTo(pauseCalendar) <= 0 && endCalendar.compareTo(pauseCalendar) >= 0) {
                //暂停时间在停车时间内
                if (endDate.split(" ")[0].equals(pause[i]) && isInStartDay(endDate)) {
                    //判断最后一天是否是暂停时间的00:00，不直接返回是因为如果停车时间为2018-04-26 10:00 - 2018-04-29:00，如果暂停时间为2018-04-29,2018-04-28则不正确
                    inPauseDate = 1;
                } else {
                    return 0;
                }
            }
        }
        return inPauseDate;
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param shareDays 每周共享的星期，
     * @return 0:(开始时间和结束时间之间不在在共享星期之内) 1:(开始时间和结束时间之间在共享星期之内,但是结束时间刚好是最后共享日期的第二天凌晨的)
     * 2:(开始时间和结束时间之间在共享星期之内)
     */
    public static int isInShareDay(String startDate, String endDate, String shareDays) {
        //如果是同一天则只需要判断当天是否共享
        if (startDate.split(" ")[0].equals(endDate.split(" ")[0])) {
            int parkDay = getDayOfWeek(startDate, true);
            if (shareDays.split(",")[parkDay - 1].charAt(0) != '1') {
                return 0;
            }
        } else {
            String[] days = shareDays.split(",");
            if (getDateDayDistance(startDate, endDate) > 6) {
                //如果停车时间超过6天则需要判断是否整个星期都共享
                for (int i = 0; i < days.length; i++) {
                    if (days[i].charAt(0) != '1') {
                        return 0;
                    }
                }
            } else {
                //停车时间为一到六天的
                int startDay = getDayOfWeek(startDate, true);
                int endDay = getDayOfWeek(endDate, true);
                boolean isInStartDay = isInStartDay(endDate);
                if (startDay < endDay) {
                    //如果停车时间是在同一个星期内则只需要判断停车的星期是否都在共享的星期内
                    for (int i = startDay; i <= endDay; i++) {
                        if (days[i - 1].charAt(0) != '1') {
                            if (i == endDay && isInStartDay) {
                                //如果最后一天不共享但是是只停00:00的
                                return 1;
                            }
                            return 0;
                        }
                    }
                } else {
                    //如果是停车时间是在两个星期之间的，则判断停车的星期是否都在共享的星期内
                    List<Integer> list = new ArrayList<>();
                    //保存停车所在的星期
                    for (int i = startDay; i <= 7; i++) {
                        list.add(i);
                    }
                    for (int i = 1; i <= endDay; i++) {
                        list.add(i);
                    }

                    for (int i = 0; i < list.size(); i++) {
                        if (days[list.get(i) - 1].charAt(0) != '1') {
                            //如果是最后一天的凌晨的则也可以
                            if (i == list.size() - 1 && isInStartDay) {
                                return 1;
                            }
                            //否则就是不在共享星期内
                            return 0;
                        }
                    }
                }
            }
        }
        return 2;
    }

    /**
     * @param orderDate 逗号隔开(2018-04-19 17:00*2018-04-19 19:00,2018-04-21 08:00*2018-04-22 05:00)
     * @return true(停车时间在预约时间内)
     */
    public static boolean isInOrderDate(String startDate, String endDate, String orderDate) {
        if (orderDate.equals("-1") || orderDate.equals("")) {
            return false;
        }

        //保存在现在时刻以后的预约时间（既是排除掉在现在以前那些被预约的时间）
        String[] orderDates = orderDate.split(",");
        List<String> usefulDates = new ArrayList<>(orderDates.length);
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.set(Calendar.SECOND, 0);
        nowCalendar.set(Calendar.MILLISECOND, 0);

        for (String date : orderDates) {
            if (nowCalendar.compareTo(getYearToMinuteCalendar(date.substring(date.indexOf("*") + 1, date.length()))) == -1) {
                usefulDates.add(date);
            }
        }

        if (usefulDates.isEmpty()) {
            return false;
        }

        //将已被预约的时间用Calendar保存
        Calendar[] calendars = new Calendar[usefulDates.size() * 2];
        for (int i = 0; i < usefulDates.size(); i++) {
            calendars[i * 2] = getYearToMinuteCalendar(usefulDates.get(i).substring(0, usefulDates.get(i).indexOf("*")));
            calendars[i * 2 + 1] = getYearToMinuteCalendar(usefulDates.get(i).substring(usefulDates.get(i).indexOf("*") + 1, usefulDates.get(i).length()));
        }

        Calendar startCalendar = getYearToMinuteCalendar(startDate);
        Calendar endCalendar = getYearToMinuteCalendar(endDate);
        for (int i = 0; i < calendars.length; i += 2) {
            if (startCalendar.compareTo(calendars[i]) >= 0 && startCalendar.compareTo(calendars[i + 1]) < 0
                    || endCalendar.compareTo(calendars[i]) > 0 && endCalendar.compareTo(calendars[i + 1]) <= 0
                    || startCalendar.compareTo(calendars[i]) <= 0 && endCalendar.compareTo(calendars[i + 1]) >= 0
                    || startCalendar.compareTo(calendars[i]) >= 0 && endCalendar.compareTo(calendars[i + 1]) <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param date 比较的时间，格式为yyyy-MM-dd HH:mm
     * @return true(HH : mm 为00 : 00)
     */

    private static boolean isInStartDay(String date) {
        String[] hourWithMinutes = date.split(" ")[1].split(":");
        return (hourWithMinutes[0].equals("00") || hourWithMinutes[0].equals("0")) && (hourWithMinutes[1].equals("00") || hourWithMinutes[1].equals("0"));
    }

    /**
     * @param calendar 比较的时间，格式为yyyy-MM-dd HH:mm
     * @return true(HH : mm 为00 : 00或者0都行)
     */
    private static boolean isInStartDay(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0;
    }

    /**
     * @param startDate 比较的开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   比较的结束时间，格式为yyyy-MM-dd HH:mm
     * @return 返回两个时间段相差的天数，因为是根据时间戳来算的如果两个时间段相差的时间毫秒不够一天的毫秒还是回返回0
     */
    public static int getDateDayDistance(String startDate, String endDate) {
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
     * @param startDate 比较的开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   比较的结束时间，格式为yyyy-MM-dd HH:mm
     * @return 返回两个时间段相差的分钟数
     */
    public static int getDateMinutesDistance(String startDate, String endDate) {
        SimpleDateFormat dateFormat = getYearToMinutesFormat();
        Date start = new Date();
        Date end = new Date();
        try {
            start = dateFormat.parse(startDate);
            end = dateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) ((end.getTime() / 1000 - start.getTime() / 1000) / 60);
    }

    /**
     * @param startDate 比较的开始时间，格式为yyyy-MM-dd HH:mm:ss
     * @param endDate   比较的结束时间，格式为yyyy-MM-dd HH:mm:ss
     * @return 返回两个时间段相差的分钟数
     */
    public static int getMinutesDistance(String startDate, String endDate) {
        SimpleDateFormat dateFormat = getYearToSecondFormat();
        Date start = new Date();
        Date end = new Date();
        try {
            start = dateFormat.parse(startDate);
            end = dateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) ((end.getTime() / 1000 - start.getTime() / 1000) / 60);
    }

    /**
     * @param startDate 比较的开始时间，格式为yyyy-MM-dd HH:mm:ss
     * @param endDate   比较的结束时间，格式为yyyy-MM-dd HH:mm:ss
     * @return 返回两个时间段相差的分钟数(x小时x分钟)
     */
    public static String getDateDistanceForHourWithMinute(String startDate, String endDate) {
        int minutesDistance = getMinutesDistance(startDate, endDate);
        StringBuilder stringBuilder = new StringBuilder();
        if (minutesDistance / 60 > 0) {
            stringBuilder.append(minutesDistance / 60);
            stringBuilder.append("小时");
            minutesDistance -= minutesDistance / 60 * 60;
        }
        if (minutesDistance > 0) {
            stringBuilder.append(minutesDistance);
            stringBuilder.append("分钟");
        }
        return stringBuilder.toString();
    }

    /**
     * @param startDate 比较的开始时间，格式为yyyy-MM-dd HH:mm:ss
     * @param endDate   比较的结束时间，格式为yyyy-MM-dd HH:mm:ss
     * @param addSecond 为startDate加上相应的秒数
     * @return 返回两个时间段相差的分钟数(x小时x分钟)
     */
    public static String getDateDistanceForHourWithMinute(String startDate, String endDate, String addSecond) {
        Calendar startCalendar = getYearToSecondCalendar(startDate);
        Calendar endCalenar = getYearToSecondCalendar(endDate);
        startCalendar.add(Calendar.SECOND, Integer.valueOf(addSecond));

        int minutesDistance = (int) ((endCalenar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000 / 60);
        StringBuilder stringBuilder = new StringBuilder();
        if (minutesDistance / 60 > 0) {
            stringBuilder.append(minutesDistance / 60);
            stringBuilder.append("小时");
            minutesDistance -= minutesDistance / 60 * 60;
        }

        if (minutesDistance > 0) {
            stringBuilder.append(minutesDistance);
            stringBuilder.append("分钟");
        }
        return stringBuilder.toString();
    }

    /**
     * @param startDate 比较的开始时间，格式为yyyy-MM-dd HH:mm:ss
     * @param endDate   比较的结束时间，格式为yyyy-MM-dd HH:mm:ss
     * @return 返回两个时间段相差的分钟数(x天x小时x分钟)
     */
    public static String getDateDistanceForDayToMinute(String startDate, String endDate) {
        Calendar startCalendar = getYearToSecondCalendar(startDate);
        Calendar endCalenar = getYearToSecondCalendar(endDate);

        int minutesDistance = (int) ((endCalenar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000 / 60);
        StringBuilder stringBuilder = new StringBuilder();

        if (minutesDistance / 60 / 24 > 0) {
            stringBuilder.append(minutesDistance / 60 / 24);
            stringBuilder.append("天");
            minutesDistance -= minutesDistance / 60 / 24 * 60 * 24; //减去n天的分钟数
        }

        if (minutesDistance / 60 > 0) {
            stringBuilder.append(minutesDistance / 60);
            stringBuilder.append("小时");
            minutesDistance -= minutesDistance / 60 * 60;
        }

        if (minutesDistance > 0) {
            stringBuilder.append(minutesDistance);
            stringBuilder.append("分钟");
        }
        return stringBuilder.toString();
    }

    /**
     * @param startDate 比较的开始时间，格式为yyyy-MM-dd HH:mm:ss
     * @param endDate   比较的结束时间，格式为yyyy-MM-dd HH:mm:ss
     * @param addSecond 为startDate加上相应的秒数
     * @return 返回两个时间段相差的分钟数(x天x小时x分钟)
     */
    public static String getDateDistanceForDayToMinute(String startDate, String endDate, String addSecond) {
        Calendar startCalendar = getYearToSecondCalendar(startDate);
        Calendar endCalenar = getYearToSecondCalendar(endDate);
        startCalendar.add(Calendar.SECOND, Integer.valueOf(addSecond));

        int minutesDistance = (int) ((endCalenar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000 / 60);
        StringBuilder stringBuilder = new StringBuilder();

        if (minutesDistance / 60 / 24 > 0) {
            stringBuilder.append(minutesDistance / 60 / 24);
            stringBuilder.append("天");
            minutesDistance -= minutesDistance / 60 / 24 * 60 * 24; //减去n天的分钟数
        }

        if (minutesDistance / 60 > 0) {
            stringBuilder.append(minutesDistance / 60);
            stringBuilder.append("小时");
            minutesDistance -= minutesDistance / 60 * 60;
        }

        if (minutesDistance > 0) {
            stringBuilder.append(minutesDistance);
            stringBuilder.append("分钟");
        }
        return stringBuilder.toString();
    }

    /**
     * @param startDate 比较的开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   比较的结束时间，格式为yyyy-MM-dd HH:mm
     * @return 返回两个时间段相差的毫秒数
     */
    public static long getDateMillisDistance(String startDate, String endDate) {
        SimpleDateFormat dateFormat = getYearToMinutesFormat();
        Date start = new Date();
        Date end = new Date();
        try {
            start = dateFormat.parse(startDate);
            end = dateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ((end.getTime() - start.getTime()));
    }

    /**
     * @param containHourAndMinute true(格式为yyyy-MM-dd HH:mm)   false(格式为yyyy-MM-dd)
     * @return 获取当天是星期几(星期一到星期七)
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
     * @return null(共享时间段内没有一个包含开始时间和结束时间的)  Calendar[]{能停车的时间段的开始时间,结束时间}
     */
    public static Calendar[] isInShareTime(String startDate, String endDate, String shareTime, boolean isInStartDay) {
        if (shareTime.equals("-1")) {
            //如果是全天共享的那直接就可以了
            return new Calendar[]{getTodayStartCalendar(getYearToMinuteCalendar(startDate)), getTodayEndCalendar(getYearToMinuteCalendar(endDate))};
        } else if (getDateDayDistance(startDate, endDate) < 1) {
            //停车时长不大于24小时
            Calendar startTime = getYearToMinuteCalendar(startDate);
            Calendar endTime = getYearToMinuteCalendar(endDate);

            //获取停车位共享的各个时间段
            String[] times = shareTime.split(",");

            if (!isInSameDay(startDate, endDate)) {
                //停车时间跨了一天的
                Calendar[] calendars = new Calendar[times.length * 2];
                for (int i = 0; i < times.length; i++) {
                    calendars[i * 2] = getSameYearToDayCalendar(startTime, times[i].substring(0, times[i].indexOf(" - ")));
                    calendars[i * 2 + 1] = getSameYearToDayCalendar(startTime, times[i].substring(times[i].indexOf(" - ") + 3, times[i].length()));

                    //共享日期跨了一天的，则把结束的日期加一天
                    if (calendars[i * 2].compareTo(calendars[i * 2 + 1]) >= 0) {
                        calendars[i * 2 + 1].add(Calendar.DAY_OF_MONTH, 1);
                    }
                }

                for (int i = 0; i < calendars.length; i += 2) {
                    if (startTime.compareTo(calendars[i]) >= 0 && endTime.compareTo(calendars[i + 1]) <= 0) {
                        if (isInStartDay) {
                            //结束时间为00:00的，但是那天是不共享的，则返回共享开始的时间段到共享结束时间段那天的凌晨的时间差
                            return new Calendar[]{calendars[i], getTodayStartCalendar(calendars[i + 1])};
                        }
                        return new Calendar[]{calendars[i], calendars[i + 1]};
                    }
                }
            } else {
                //停车时间在同一天的
                if (times.length == 2) {
                    //如果有两个时间段，如果第二个时间段是从凌晨开始的则放在数组前面。主要是为了之后判断这个时间段是不会跨天了的
                    if (isInStartDay(getSameYearToDayCalendar(startTime, times[1].substring(0, times[1].indexOf(" - "))))) {
                        String temp = times[0];
                        times[0] = times[1];
                        times[1] = temp;
                    }
                } else if (times.length >= 3) {
                    if (!isInStartDay(getSameYearToDayCalendar(startTime, times[0].substring(0, times[0].indexOf(" - "))))) {
                        //如果第一个不是从凌晨开始的，则判断其他时间段是否是从凌晨开始的
                        for (int i = 1; i < times.length; i++) {
                            if (isInStartDay(getSameYearToDayCalendar(startTime, times[i].substring(0, times[i].indexOf(" - "))))) {
                                String temp = times[0];
                                times[0] = times[i];
                                times[i] = temp;
                                break;
                            }
                        }
                    }
                }

                List<Calendar> list = new ArrayList<>(times.length * 2);
                Calendar startCalendar;
                Calendar endCalendar;
                for (int i = 0; i < times.length; i++) {
                    startCalendar = getSameYearToDayCalendar(startTime, times[i].substring(0, times[i].indexOf(" - ")));
                    endCalendar = getSameYearToDayCalendar(startTime, times[i].substring(times[i].indexOf(" - ") + 3, times[i].length()));
                    list.add(startCalendar);
                    list.add(endCalendar);

                    //如果共享日期跨天了的，则把共享时间拆成当天共享的开始时间到当天的结束时间，当天的开始时间到当天的共享结束时间
                    if (startCalendar.compareTo(endCalendar) >= 0) {
                        list.set(list.size() - 1, getTodayEndCalendar(startCalendar));
                        list.add(0, getTodayStartCalendar(endCalendar));
                        list.add(1, (Calendar) endCalendar.clone());
                    }
                }

                for (int i = 0; i < list.size(); i += 2) {
                    if (startTime.compareTo(list.get(i)) >= 0 && endTime.compareTo(list.get(i + 1)) <= 0) {
                        if (i - 2 >= 0 && isInStartDay(list.get(i))) {
                            //如果是从凌晨开始的，并且i-2大于等于0的，则这个时间段是跨天的
                            return new Calendar[]{list.get(i + 1), list.get(i - 2)};
                        } else {
                            return new Calendar[]{list.get(i), list.get(i + 1)};
                        }
                    }
                }

            }
        }
        return null;
    }

    /**
     * @param orderDate 逗号隔开(2018-04-19 17:00*2018-04-19 19:00,2018-04-21 08:00*2018-04-22 05:00)
     * @param shareTime 共享时间段(08:00 - 12:00)只有一个
     * @return null(在共享时间段内没有预约订单)     yyyy-MM-dd HH:mm至yyyy-MM-dd HH:mm(在共享时间内有预约订单,并返回该订单的时间)
     */
    public static String isInShareTime(String orderDate, String shareTime) {
        if (!orderDate.equals("") && !orderDate.equals("-1")) {
            String[] order = orderDate.split(",");
            Calendar orderStartCalendar;
            Calendar orderEndCalendar;
            Calendar shareStartCalendar;
            Calendar shareEndCalendar;
            String shareStartTime = shareTime.substring(0, shareTime.indexOf(" - "));
            String shareEndTime = shareTime.substring(shareTime.indexOf(" - ") + 3, shareTime.length());
            String result;
            boolean isInTwoDay = false;
            for (String date : order) {
                orderStartCalendar = getYearToMinuteCalendar(date.substring(0, date.indexOf("*")));
                orderEndCalendar = getYearToMinuteCalendar(date.substring(date.indexOf("*") + 1, date.length()));

                if (getCalendarDistance(orderStartCalendar, orderEndCalendar) >= 1440) {
                    //如果有订单跨了一天的则肯定在共享时间段内有订单
                    return getYearToMinutesString(orderStartCalendar) + "至" + getYearToMinutesString(orderEndCalendar);
                }

                shareStartCalendar = getSameYearToDayCalendar(orderStartCalendar, shareStartTime);
                shareEndCalendar = getSameYearToDayCalendar(orderStartCalendar, shareEndTime);

                Log.e(TAG, "isInShareTime   shareStartCalendar0: " + printCalendar(shareStartCalendar));
                Log.e(TAG, "isInShareTime   shareEndCalendar0: " + printCalendar(shareEndCalendar));
                if (shareStartCalendar.compareTo(shareEndCalendar) > 0) {
                    //共享日期跨天的   2018-05-16 09:00 - 2018-05-17 05:00
                    shareEndCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    isInTwoDay = true;
                }
                if ((result = isInShareTime(orderStartCalendar, orderEndCalendar, shareStartCalendar, shareEndCalendar)) != null) {
                    return result;
                }

                if (isInTwoDay) {
                    //如果跨天的则还需要比较2018-05-16 00:00 - 2018-05-16 05:00
                    shareEndCalendar.add(Calendar.DAY_OF_MONTH, -1);
                    shareStartCalendar = getSameYearToDayCalendar(shareEndCalendar, "00:00");
                    if ((result = isInShareTime(orderStartCalendar, orderEndCalendar, shareStartCalendar, shareEndCalendar)) != null) {
                        return result;
                    }
                }

                isInTwoDay = false;
            }
        }

        return null;
    }

    private static String isInShareTime(Calendar startCalendar, Calendar endCalendar, Calendar shareStartCalendar, Calendar shareEndCalendar) {
        Log.e(TAG, "isInShareTime   startCalendar: " + printCalendar(startCalendar));
        Log.e(TAG, "isInShareTime   endCalendar: " + printCalendar(endCalendar));
        Log.e(TAG, "isInShareTime   shareStartCalendar: " + printCalendar(shareStartCalendar));
        Log.e(TAG, "isInShareTime   shareEndCalendar: " + printCalendar(shareEndCalendar));
        if (isIntersection(startCalendar, endCalendar, shareStartCalendar, shareEndCalendar)) {
            return getYearToMinutesString(startCalendar) + "至" + getYearToMinutesString(endCalendar);
        }
        return null;
    }

    /**
     * @param startDate 格式为yyyy-MM-dd HH:mm
     * @param endDate   格式为yyyy-MM-dd HH:mm
     * @return true(两天是在同一天)
     */
    private static boolean isInSameDay(String startDate, String endDate) {
        return startDate.split(" ")[0].endsWith(endDate.split(" ")[0]);
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
     * @return 格式为yyyy-MM-dd对应的Calendar,时分秒和毫秒都会清零,月为对应的月(0-11)
     */
    public static Calendar getYearToDayCalendar() {
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.MONTH, 1);
        initHourToMilli(calendar);
        return calendar;
    }

    /**
     * @param containHourAndMinute true(格式为yyyy-MM-dd HH:mm)   false(格式为yyyy-MM-dd)
     * @return 格式为yyyy-MM-dd对应的Calendar,时分秒和毫秒都会清零
     */
    public static Calendar getYearToDayCalendar(String date, boolean containHourAndMinute) {
        Calendar calendar = Calendar.getInstance();
        String[] yearToDay = date.split("-");
        calendar.set(Calendar.YEAR, Integer.valueOf(yearToDay[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(yearToDay[1]) - 1);
        if (containHourAndMinute) {
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(yearToDay[2].substring(0, yearToDay[2].indexOf(" "))));
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(yearToDay[2]));
        }
        initHourToMilli(calendar);
        return calendar;
    }

    /**
     * @param date 格式为yyyy-MM-dd HH:mm:ss
     * @return 格式为yyyy-MM-dd对应的Calendar
     */
    public static Calendar getYearToSecondCalendar(String date) {
        Calendar calendar = Calendar.getInstance();
        String[] yearToMinute = date.split("-");
        calendar.set(Calendar.YEAR, Integer.valueOf(yearToMinute[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(yearToMinute[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(yearToMinute[2].substring(0, yearToMinute[2].indexOf(" "))));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].indexOf(" ") + 1, yearToMinute[2].indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].indexOf(":") + 1, yearToMinute[2].lastIndexOf(":"))));
        calendar.set(Calendar.SECOND, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].lastIndexOf(":") + 1, yearToMinute[2].length())));
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * @param date      格式为yyyy-MM-dd HH:mm:ss
     * @param addSecond 添加的秒数
     * @return 格式为yyyy-MM-dd对应的Calendar
     */
    public static Calendar getYearToSecondCalendar(String date, String addSecond) {
        Calendar calendar = Calendar.getInstance();
        String[] yearToMinute = date.split("-");
        calendar.set(Calendar.YEAR, Integer.valueOf(yearToMinute[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(yearToMinute[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(yearToMinute[2].substring(0, yearToMinute[2].indexOf(" "))));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].indexOf(" ") + 1, yearToMinute[2].indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].indexOf(":") + 1, yearToMinute[2].lastIndexOf(":"))));
        calendar.set(Calendar.SECOND, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].lastIndexOf(":") + 1, yearToMinute[2].length())));
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.SECOND, Integer.valueOf(addSecond));
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
        calendar.set(Calendar.MONTH, Integer.valueOf(yearToMinute[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(yearToMinute[2].substring(0, yearToMinute[2].indexOf(" "))));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].indexOf(" ") + 1, yearToMinute[2].indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].indexOf(":") + 1, yearToMinute[2].length())));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * date格式为HH:mm
     */
    public static Calendar getHourMinuteCalendar(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(date.substring(0, date.indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(date.substring(date.indexOf(":") + 1, date.length())));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 将calendar的时分秒和毫秒都清零，否则比较的时候会因为时分秒毫秒不一致而导致结果出错
     */
    public static void initHourToMilli(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * @param day       周一至七
     * @param startDate yyyy-MM-dd
     * @param endDate   yyyy-MM-dd
     * @return 在startDate-endDate之内星期为day的Calendar
     */
    private static List<Calendar> getDayCalendar(int day, String startDate, String endDate) {
        List<Calendar> calendars = new ArrayList<>();
        Calendar calendar = getYearToDayCalendar(startDate, false);
        Calendar startCalendar = getYearToDayCalendar(startDate, false);
        Calendar endCalendar = getYearToDayCalendar(endDate, false);
        int calendarDay = getDayOfWeek(startDate, false);
        calendar.add(Calendar.DAY_OF_MONTH, day - calendarDay);

        while (calendar.compareTo(endCalendar) <= 0) {
            if (calendar.compareTo(startCalendar) >= 0) {
                calendars.add(calendar);
            }
            calendar = (Calendar) calendar.clone();
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        }
        return calendars;
    }

    /**
     * @param day       周一至七
     * @param startDate yyyy-MM-dd
     * @param endDate   yyyy-MM-dd
     * @param orderDate 订单时间
     * @return true(星期day在订单时间内)
     */
    public static boolean isInOrderDay(int day, String startDate, String endDate, String orderDate) {
        List<Calendar> list = getDayCalendar(day, startDate, endDate);
        if (!list.isEmpty()) {
            String[] order = orderDate.split(",");
            Calendar endDayCalendar;
            Calendar startOrderCalendar;
            Calendar endOrderCalendar;
            for (String date : order) {
                startOrderCalendar = getYearToMinuteCalendar(date.substring(0, date.indexOf("*")));
                if (startOrderCalendar.compareTo(DateUtil.getYearToDayCalendar()) < 0) {
                    startOrderCalendar = DateUtil.getYearToDayCalendar();
                }
                endOrderCalendar = getYearToMinuteCalendar(date.substring(date.indexOf("*") + 1, date.length()));
                for (Calendar calendar : list) {
                    endDayCalendar = (Calendar) calendar.clone();
                    endDayCalendar.set(Calendar.HOUR_OF_DAY, 24);
                    endDayCalendar.set(Calendar.MINUTE, 0);
                    if (isIntersection(calendar, endDayCalendar, startOrderCalendar, endOrderCalendar)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    /**
     * @return true(两个时间段有交集)
     */
    public static boolean isIntersection(Calendar startCalendar, Calendar endCalendar, Calendar otherStartCalendar, Calendar otherEndCalendar) {
        if (startCalendar.compareTo(otherStartCalendar) >= 0 && startCalendar.compareTo(otherEndCalendar) < 0
                || endCalendar.compareTo(otherStartCalendar) > 0 && endCalendar.compareTo(otherEndCalendar) <= 0
                || startCalendar.compareTo(otherStartCalendar) <= 0 && endCalendar.compareTo(otherEndCalendar) >= 0
                || otherStartCalendar.compareTo(startCalendar) <= 0 && otherEndCalendar.compareTo(endCalendar) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * @param date yyyy-MM-dd - yyyy-MM-dd
     * @return 当前是否在可用时间内
     */
    public static boolean isInUsefulDate(String date) {
        Calendar startCalendar = getYearToDayCalendar(date.substring(0, date.indexOf(" - ")), false);
        Calendar endCalendar = getYearToDayCalendar(date.substring(date.indexOf(" - ") + 3, date.length()), false);
        Calendar nowCalendar = Calendar.getInstance();
        return startCalendar.compareTo(nowCalendar) <= 0 && nowCalendar.compareTo(endCalendar) < 0;
    }


    /**
     * @param yearToSecond yyyy-MM-dd HH:mm:ss
     * @return MM月dd日 HH:mm
     */
    public static String getMonthToMinute(String yearToSecond) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(yearToSecond.substring(yearToSecond.indexOf("-") + 1, yearToSecond.lastIndexOf(":")));
        stringBuilder.replace(2, 3, "月");
        stringBuilder.replace(5, 6, "日 ");
        return stringBuilder.toString();
    }

    /**
     * @param yearToSecond yyyy-MM-dd HH:mm:ss
     * @return HH点mm分
     */
    public static String getHourToMinute(String yearToSecond) {
        return yearToSecond.substring(yearToSecond.indexOf(" ") + 1, yearToSecond.indexOf(":")) + "点"
                + yearToSecond.substring(yearToSecond.indexOf(":") + 1, yearToSecond.lastIndexOf(":")) + "分";
    }

    /**
     * @param yearToSecond yyyy-MM-dd HH:mm:ss
     * @return MM月dd日
     */
    public static String getMonthToDay(String yearToSecond) {
        StringBuilder stringBuilder = new StringBuilder(yearToSecond.substring(yearToSecond.indexOf("-") + 1, yearToSecond.indexOf(" ")));
        stringBuilder.replace(2, 3, "月");
        stringBuilder.append("日");
        return stringBuilder.toString();
    }

    /**
     * @return 日历对应的年月日，格式2018年05月07日
     */
    public static String getCalendarYearToDayWithText(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "年" + thanTen(calendar.get(Calendar.MONTH) + 1) + "月" + thanTen(calendar.get(Calendar.DAY_OF_MONTH)) + "日";
    }

    /**
     * @return 日历对应的月日, 格式05月07日
     */
    public static String getCalendarMonthToDayWithText(Calendar calendar) {
        return thanTen(calendar.get(Calendar.MONTH) + 1) + "月" + thanTen(calendar.get(Calendar.DAY_OF_MONTH)) + "日";
    }

    /**
     * @return 日历对应的年月日，格式2018-05-07
     */
    public static String getCalendarYearToDay(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "-" + thanTen(calendar.get(Calendar.MONTH) + 1) + "-" + thanTen(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String printCalendar(Calendar calendar) {
        if (calendar == null) {
            return "";
        }
        return (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }

    public static String printYearToMinutesCalendar(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH)
                + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }

    /**
     * @param yearToSecond yyyy-MM-dd HH:mm:ss
     * @return yyyy-MM-dd HH:mm
     */
    public static String deleteSecond(String yearToSecond) {
        return yearToSecond.substring(0, yearToSecond.lastIndexOf(":"));
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
        int parkDay = getDateDayDistance(startDate, endDate);
        if (parkDay >= 1) {
            Log.e(TAG, "caculateParkFee: 1");
            //停车时间超过一天
            int hightTotalMinutes;
            hightStartCalendar = getSameYearToDayCalendar(startDateCalendar, highDates[0]);

            //判断高峰期是否同一天判断出高峰期结束的时间
            if (highDateInSameDay) {
                Log.e(TAG, "caculateParkFee: 2");
                hightEndCalendar = getSameYearToDayCalendar(startDateCalendar, highDates[1]);
                hightTotalMinutes = getDateMinutes(hightStartCalendar, hightEndCalendar);
            } else {
                Log.e(TAG, "caculateParkFee: 3");
                hightEndCalendar = getSameYearToDayCalendar(startDateCalendar, highDates[1], 1);
                hightTotalMinutes = getDateMinutes(hightStartCalendar, getTodayEndCalendar(hightStartCalendar)) +
                        getDateMinutes(getTodayStartCalendar(hightEndCalendar), hightEndCalendar);
            }
            Log.e(TAG, "caculateParkFee hightTotalMinutes: " + hightTotalMinutes + " lowTotalMinutes:" + (1440 - hightTotalMinutes));
            result = hightTotalMinutes * highFee + (1440 - hightTotalMinutes) * lowFee;     //一天所需的费用为高峰时段加上低峰时段的全部费用
            Log.e(TAG, "caculateParkFee oneDayResult: " + result);

            result *= parkDay;  //停了parkDay那么多天所需的费用
            Log.e(TAG, "caculateParkFee: parkDayResult:" + result);

            startDateCalendar = transformYearToDay(startDateCalendar, endDateCalendar);         //把停车开始时间改到最后那天的开始时间,22号12:00
            hightStartCalendar = transformYearToDay(hightStartCalendar, startDateCalendar);     //高峰开始时间为22号xx:xx
            hightEndCalendar = transformYearToDay(hightEndCalendar, endDateCalendar);           //高峰结束时间为22号xx:xx

            if (isStartAndEndInSameDay(startDateCalendar, endDateCalendar)) {
                Log.e(TAG, "caculateParkFee: 4");
                //比如从20号12:00停到22号15:00，我们还需要计算12:00到15:00的费用
                result += calculateSameDay(startDateCalendar, endDateCalendar, hightStartCalendar, hightEndCalendar, highFee, lowFee);
            } else {
                Log.e(TAG, "caculateParkFee: 6");
                //比如从20号12:00停到22号10:00，则还需要计算21号12:00到22号10:00的费用
                startDateCalendar.add(Calendar.DAY_OF_MONTH, -1);                       //把停车开始时间改到最后那天的前一天,21号12:00
                hightStartCalendar = transformYearToDay(hightStartCalendar, startDateCalendar);     //高峰开始时间为22号xx:xx
                hightEndCalendar = transformYearToDay(hightEndCalendar, startDateCalendar);           //高峰结束时间为22号xx:xx
                result += calculateDifferentDay(startDateCalendar, endDateCalendar, hightStartCalendar, hightEndCalendar, highFee, lowFee);
            }

        } else {
            hightStartCalendar = getSameYearToDayCalendar(startDateCalendar, highDates[0]);
            hightEndCalendar = getSameYearToDayCalendar(startDateCalendar, highDates[1]);
            if (parkStartDate[0].equals(parkEndDate[0])) {
                Log.e(TAG, "caculateParkFee: 8");
                //停车时间为同一天
                result = calculateSameDay(startDateCalendar, endDateCalendar, hightStartCalendar, hightEndCalendar, highFee, lowFee);
                Log.e(TAG, "caculateParkFee result: " + result);
            } else {
                Log.e(TAG, "caculateParkFee: 11");
                //停车时间跨了一天的
                result = calculateDifferentDay(startDateCalendar, endDateCalendar, hightStartCalendar, hightEndCalendar, highFee, lowFee);
            }
        }

        return result / 60;
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
            result = calculateHighAndParkInSameDay(startDate, endDate, highStartCalendar, highEndCalendar, highFee, lowFee);
        } else {
            //高峰时段不在同一天     2018-4-23 12:00-2018-4-24 08:00
            if (highEndCalendar.compareTo(startDate) <= 0) {
                Log.e(TAG, "calculateSameDay3");
                //停车时间为 2018-4-23 10:00-14:00,不包括第二天的高峰时段的
                result = calculateHighAndParkInSameDay(startDate, endDate, highStartCalendar, getTodayEndCalendar(highStartCalendar), highFee, lowFee);
            } else if (highEndCalendar.compareTo(startDate) > 0 && highEndCalendar.compareTo(endDate) <= 0) {
                Log.e(TAG, "calculateSameDay: 4");
                //停车时间的一部分在高峰时段的第二天,    2018-4-23 06:00-13:00
                result = calculateHighAndParkInSameDay(startDate, highEndCalendar, getTodayStartCalendar(startDate), highEndCalendar, highFee, lowFee); //计算2018-4-23 06:00-2018-4-23 08:00
                result += calculateHighAndParkInSameDay(highEndCalendar, endDate, highStartCalendar, getTodayEndCalendar(highStartCalendar), highFee, lowFee);  //2018-4-23 08:00-2018-4-23 11:00
            } else {
                Log.e(TAG, "calculateSameDay: 5");
                //停车时间全部在第二天的高峰时段的  2018-4-23 06:00-07:00
                result = calculateHighAndParkInSameDay(startDate, endDate, getTodayStartCalendar(startDate), highEndCalendar, highFee, lowFee);
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
        //先计算第一天的费用
        result = calculateSameDay(startDate, getTodayEndCalendar(startDate), highStartCalendar, highEndCalendar, highFee, lowFee);
        Log.e(TAG, "calculateDifferentDay1: " + result);

        highStartCalendar.add(Calendar.DAY_OF_MONTH, 1);
        highEndCalendar.add(Calendar.DAY_OF_MONTH, 1);
        //再加上第二天的费用
        result += calculateSameDay(getTodayStartCalendar(endDate), endDate, highStartCalendar, highEndCalendar, highFee, lowFee);
        Log.e(TAG, "calculateDifferentDay2: " + result);
        return result;
    }

    /**
     * @param highDate 格式为HH:mm - HH:mm
     * @return true(两个时间在同一天)
     */
    private static boolean isHighDateInSameDay(String highDate) {
        return getHourMinuteCalendar(highDate.substring(0, highDate.indexOf(" - "))).compareTo
                (getHourMinuteCalendar(highDate.substring(highDate.indexOf(" - ") + 3, highDate.length()))) <= 0;
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
     * @param date          参照的那一天，格式为yyyy-MM-dd HH:mm
     * @param hourAndMinute 格式为HH:mm
     */
    private static Calendar getSameYearToDayCalendar(Calendar date, String hourAndMinute) {
        Calendar calendar = (Calendar) date.clone();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hourAndMinute.substring(0, hourAndMinute.indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(hourAndMinute.substring(hourAndMinute.indexOf(":") + 1, hourAndMinute.length())));
        return calendar;
    }

    /**
     * @param date     参照的那一天，格式为yyyy-MM-dd HH:mm
     * @param highDate 高峰期的时分，格式为HH:mm
     * @param nextDays 比参照的那一天往后推迟几天
     */
    private static Calendar getSameYearToDayCalendar(Calendar date, String highDate, int nextDays) {
        Calendar calendar = (Calendar) date.clone();
        calendar.add(Calendar.DAY_OF_MONTH, nextDays);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(highDate.substring(0, highDate.indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(highDate.substring(highDate.indexOf(":") + 1, highDate.length())));
        return calendar;
    }

    /**
     * @return 把originCalendar的年月日转化为targetCalendar的年月日
     */
    private static Calendar transformYearToDay(Calendar originCalendar, Calendar targetCalendar) {
        originCalendar.set(Calendar.YEAR, targetCalendar.get(Calendar.YEAR));
        originCalendar.set(Calendar.MONTH, targetCalendar.get(Calendar.MONTH));
        originCalendar.set(Calendar.DAY_OF_MONTH, targetCalendar.get(Calendar.DAY_OF_MONTH));
        return originCalendar;
    }

    /**
     * 计算高峰时段和停车时间都在同一天的费用
     */
    private static double calculateHighAndParkInSameDay(Calendar startDate, Calendar endDate, Calendar highStartCalendar, Calendar highEndCalendar,
                                                        double highFee, double lowFee) {
        double result;
        int totalParkMinutes = getDateMinutes(startDate, endDate);  //停车的总分钟数
        Log.e(TAG, "calculateHighAndParkInSameDay totalParkMinutes:" + totalParkMinutes);
        int totalHighMinutes = getDateMinutes(highStartCalendar, highEndCalendar);  //高峰时段的总分钟数
        if (startDate.compareTo(highStartCalendar) <= 0 && endDate.compareTo(highEndCalendar) >= 0) {
            //停车时间完全包括高峰期的
            result = totalHighMinutes * highFee + (totalParkMinutes - totalHighMinutes) * lowFee;
            Log.e(TAG, "calculateHighAndParkInSameDay -1: " + "totalHighMinutes * highFee:" + totalHighMinutes * highFee + "  (totalParkMinutes - highFee) * lowFee:" + (totalParkMinutes - totalHighMinutes) * lowFee);
            Log.e(TAG, "calculateHighAndParkInSameDay -2: " + "totalParkMinutes:" + totalParkMinutes + "  totalHighMinutes:" + totalHighMinutes + "  result:" + result);
        } else if (startDate.compareTo(highStartCalendar) >= 0 && endDate.compareTo(highEndCalendar) <= 0) {
            //停车时间完全在高峰期内的
            result = totalParkMinutes * highFee;
            Log.e(TAG, "calculateHighAndParkInSameDay 1:" + result);
        } else if (startDate.compareTo(highStartCalendar) < 0 && endDate.compareTo(highStartCalendar) > 0 && endDate.compareTo(highEndCalendar) <= 0) {
            //停车的开始时间不在高峰时段内，但是结束时间在高峰时段内，则高峰时段的开始时间到停车的结束时间为需要交纳高峰费用的时间
            result = getDateMinutes(highStartCalendar, endDate) * highFee + getDateMinutes(startDate, highStartCalendar) * lowFee;
            Log.e(TAG, "calculateHighAndParkInSameDay 2: " + result);
        } else if (startDate.compareTo(highStartCalendar) >= 0 && startDate.compareTo(highEndCalendar) < 0 && endDate.compareTo(highEndCalendar) > 0) {
            //停车的开始时间小于高峰时段的结束时间，但是停车的时间大于高峰时段的结束时间，则停车的开始时间到高峰时段的结束时间为需要交纳高峰费用的时间
            result = getDateMinutes(startDate, highEndCalendar) * highFee + getDateMinutes(highEndCalendar, endDate) * lowFee;
            Log.e(TAG, "calculateHighAndParkInSameDay 3: " + result);
        } else {
            result = totalParkMinutes * lowFee;
            Log.e(TAG, "calculateHighAndParkInSameDay 4:" + result);
        }

        return result;
    }

    /**
     * @param startCalendar 开始时间的日历,格式为yyyy-MM-dd HH:mm
     * @param endCalendar   结束时间的日历,格式为yyyy-MM-dd HH:mm
     * @return 两个时间相差的分钟数
     */
    private static int getDateMinutes(Calendar startCalendar, Calendar endCalendar) {
        return (int) ((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000 / 60);
    }

    /**
     * @return 返回与originCalendar同年月日但时分为23点59分Calendar
     */
    private static Calendar getTodayEndCalendar(Calendar originCalendar) {
        Calendar calendar = (Calendar) originCalendar.clone();
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        return calendar;
    }

    /**
     * @return 返回与originCalendar同年月日但时分为00点00分Calendar
     */
    private static Calendar getTodayStartCalendar(Calendar originCalendar) {
        Calendar calendar = (Calendar) originCalendar.clone();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar;
    }

    /**
     * @return 两个日历相差的分钟数
     */
    public static long getCalendarDistance(Calendar startCalendar, Calendar endCalendar) {
        return (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000 / 60;
    }

    /**
     * @return yyyy-MM-dd HH:mm - yyyy-MM-dd HH:mm
     */
    public static String getTwoYearToMinutesString(Calendar startCalendar, Calendar endCalendar) {
        return getYearToMinutesString(startCalendar) + " - " + getYearToMinutesString(endCalendar);
    }

    /**
     * @return 日历对应的yyyy-MM-dd HH:mm(如果是01则只会返回1)
     */
    private static String getYearToMinutesString(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "-" + thanTen((calendar.get(Calendar.MONTH) + 1)) + "-" + thanTen(calendar.get(Calendar.DAY_OF_MONTH))
                + " " + thanTen(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + thanTen(calendar.get(Calendar.MINUTE));
    }

    /**
     * @return 返回停车的时长xx小时xx分钟
     */
    public static String getParkTime(ParkOrderInfo parkOrderInfo) {
        if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrder_endtime()).compareTo(
                DateUtil.getYearToSecondCalendar(parkOrderInfo.getPark_end_time())) < 0) {
            //停车时长超过预约时长
            return DateUtil.getDateDistanceForHourWithMinute(parkOrderInfo.getOrder_starttime(), parkOrderInfo.getPark_end_time());
        } else {
            return DateUtil.getDateDistanceForHourWithMinute(parkOrderInfo.getOrder_starttime(), parkOrderInfo.getOrder_endtime());
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
        return getYearToMinutesFormat().parse(dateString, new ParsePosition(0));
    }

    public static String decreseOneZero(double number) {
        String result = String.valueOf(number);
        if (result.substring(result.indexOf(".") + 1, result.length()).equals("00")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static String decreseOneZero(String number) {
        if (number.substring(number.indexOf(".") + 1, number.length()).equals("00")) {
            number = number.substring(0, number.length() - 1);
        }
        return number;
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
     * 十以下的数加零
     */
    public static String thanTen(String str) {
        if (str.length() == 1) {
            return "0" + str;
        }
        return str;
    }

    /**
     * 计算相差的小时
     *
     * @param starTime
     * @param endTime
     */
    public float getTimeDifferenceHour(String starTime, String endTime) {
        float hour = 0;
        SimpleDateFormat dateFormat = getYearToMinutesFormat();
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
        SimpleDateFormat dateFormat = getYearToSecondFormat();
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
        SimpleDateFormat dateFormat = getYearToMinutesFormat();
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

    public static boolean isCarNumber(String carNumber) {
        if (TextUtils.isEmpty(carNumber.trim())) {
            return false;
        }
        Pattern pattern = Pattern.compile("^(([\\u4e00-\\u9fa5]{1}[A-Z]{1})[-]?|([wW][Jj][\\u4e00-\\u9fa5]{1}[-]?)|([a-zA-Z]{2}))([A-Za-z0-9]{5}|[DdFf][A-HJ-NP-Za-hj-np-z0-9][0-9]{4}|[0-9]{5}[DdFf])$");
        return pattern.matcher(carNumber).matches();
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
