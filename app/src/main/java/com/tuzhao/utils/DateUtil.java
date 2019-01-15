package com.tuzhao.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    public static SimpleDateFormat getYearToSecondFormat() {
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

    public static SimpleDateFormat getYearToMinutesFormat() {
        if (sExceptSecondsFormat == null) {
            sExceptSecondsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        }
        return sExceptSecondsFormat;
    }

    /**
     * @param yearToSecond yyyy-MM-dd HH:mm:ss
     * @return 日期对应的时间戳
     */
    public static long getTimeMillis(String yearToSecond) {
        try {
            return getYearToSecondFormat().parse(yearToSecond).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
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
            sSpecialTodayEndCalendar = getYearToMinuteCalendar("2018-04-24 24:00");
        }
        return sSpecialTodayEndCalendar;
    }

    /**
     * @param hourWithMinute HH:mm
     */
    public static Calendar getSpecialCalendar(String hourWithMinute) {
        Calendar calendar = getYearToMinuteCalendar("2018-04-24 00:00");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hourWithMinute.substring(0, hourWithMinute.indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(hourWithMinute.substring(hourWithMinute.indexOf(":") + 1, hourWithMinute.length())));
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private static void setCalendarYearToMinute(Calendar calendar, String yearToMinute) {
        String[] yearToMinutes = yearToMinute.split("-");
        calendar.set(Calendar.YEAR, Integer.valueOf(yearToMinutes[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(yearToMinutes[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(yearToMinutes[2].substring(0, yearToMinutes[2].indexOf(" "))));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(yearToMinutes[2].substring(yearToMinutes[2].indexOf(" ") + 1, yearToMinutes[2].indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(yearToMinutes[2].substring(yearToMinutes[2].indexOf(":") + 1, yearToMinutes[2].length())));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
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
        return TimeManager.getInstance().getCurrentTime();
    }

    /**
     * @return 当前的yyyy-MM-dd HH:mm:ss
     */
    public static String getSpecialtYearToSecond(long timeMillis) {
        getDate().setTime(timeMillis);
        return getYearToSecondFormat().format(getDate());
    }

    /**
     * @return 当前的yyyy-MM-dd HH:mm
     */
    public static String getCurrentYearToMinutes() {
        return TimeManager.getInstance().getCurrentYearToMinute();
    }

    /**
     * @return timeMillis对应的yyyy-MM-dd HH:mm
     */
    public static String getCurrentYearToMinutes(long timeMillis) {
        getDate().setTime(timeMillis);
        return getYearToMinutesFormat().format(getDate());
    }

    /**
     * @param date 格式yyyy-MM-dd HH:mm
     * @return HH:mm
     */
    public static String getHourWithMinutes(String date) {
        return date.substring(date.indexOf(" ") + 1, date.length());
    }

    /**
     * @param date 格式yyyy-MM-dd HH:mm:ss
     * @return HH:mm
     */
    public static String getHourWithMinutesByYearToSecond(String date) {
        return date.substring(date.indexOf(" ") + 1, date.lastIndexOf(":"));
    }

    /**
     * @return HH:mm
     */
    public static String getHourWithMinutes(Calendar calendar) {
        return thanTen(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + thanTen(calendar.get(Calendar.MINUTE));
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param shareDate 共享时间，格式为yyyy-MM-dd - yyyy-MM-dd(包括了开始共享的时间和结束共享的时间)
     * @return -1(车位的共享日期不能完全包括预约的停车日期) other(预约结束时间距离车位的共享结束日期的分钟数)
     */
    public static int isInShareDate(String startDate, String endDate, String shareDate) {
        if (shareDate.equals("-1") || shareDate.equals("")) {
            return 24 * 60;
        }

        String[] date = shareDate.split(" - ");
        Calendar startCalendar = getYearToMinuteCalendar(startDate);
        Calendar endCalendar = getYearToMinuteCalendar(endDate);
        Calendar startShareCalendar = getYearToDayCalendar(date[0], false);
        Calendar endShareCalendar = getYearToDayCalendar(date[1], false);
        endShareCalendar.set(Calendar.HOUR_OF_DAY, 24);

        if (startCalendar.compareTo(startShareCalendar) >= 0 && endCalendar.compareTo(endShareCalendar) <= 0) {
            //在共享的日期内
            return getCalendarMinuteDistance(endCalendar, endShareCalendar);
        }
        return -1;
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param shareDate 共享时间，格式为yyyy-MM-dd - yyyy-MM-dd(包括了开始共享的时间和结束共享的时间)
     * @return true(预约的停车时间不在共享日期内)
     */
    public static boolean notInShareDate(String startDate, String endDate, String shareDate) {
        if (shareDate.equals("-1") || shareDate.equals("")) {
            return false;
        }

        String[] date = shareDate.split(" - ");
        Calendar startCalendar = getYearToMinuteCalendar(startDate);
        Calendar endCalendar = getYearToMinuteCalendar(endDate);
        Calendar startShareCalendar = getYearToDayCalendar(date[0], false);
        Calendar endShareCalendar = getYearToDayCalendar(date[1], false);
        endShareCalendar.set(Calendar.HOUR_OF_DAY, 24); //结束时间为那天的晚上

        return startCalendar.compareTo(startShareCalendar) < 0 || endCalendar.compareTo(endShareCalendar) > 0;
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param pauseDate 暂停时间，格式为yyyy-MM-dd
     * @return -1(和暂停时间有冲突) other(结束时间到暂停时间的分钟数)
     */
    public static int isInPauseDate(String startDate, String endDate, String pauseDate) {
        if (pauseDate.equals("-1")) {
            return 24 * 60;
        }

        Calendar startCalendar = getYearToMinuteCalendar(startDate);
        Calendar endCalendar = getYearToMinuteCalendar(endDate);

        String[] pause = pauseDate.split(",");
        List<CalendarHolder> calendarHolders = new ArrayList<>(pause.length);
        for (String pauseString : pause) {
            calendarHolders.add(new CalendarHolder(getYearToDayCalendar(pauseString, false)));
        }
        Collections.sort(calendarHolders);

        for (int i = 0; i < calendarHolders.size(); i++) {
            CalendarHolder calendarHolder = calendarHolders.get(i);
            calendarHolder.endCalendar = getTodayEndCalendar(calendarHolder.startCalendar);
            if (endCalendar.compareTo(calendarHolder.startCalendar) <= 0) {
                //因为暂停共享日期已经按时间来排序了，如果预约的结束停车时间比当前的暂停共享时间早，则代表预约停车时间不在共享时间内
                return (int) ((calendarHolder.startCalendar.getTimeInMillis() - endCalendar.getTimeInMillis()) / 60000);
            } else if (startCalendar.compareTo(calendarHolder.startCalendar) >= 0 && startCalendar.compareTo(calendarHolder.endCalendar) <= 0) {
                //预约停车的开始时间在暂停共享时间内
                return -1;
            } else if (endCalendar.compareTo(calendarHolder.startCalendar) > 0 && endCalendar.compareTo(calendarHolder.endCalendar) <= 0) {
                //预约停车的结束时间在暂停共享时间内
                return -1;
            } else if (startCalendar.compareTo(calendarHolder.startCalendar) <= 0 && endCalendar.compareTo(calendarHolder.endCalendar) >= 0) {
                //预约停车的时间内包括暂停共享时间
                return -1;
            }
        }

        //如果暂停日期都比预约停车的开始时间早的
        return 24 * 60;
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param pauseDate 暂停时间，格式为yyyy-MM-dd
     * @return true(预约时间和暂停时间有冲突)     false(预约时间和暂停时间没冲突)
     */
    public static boolean isInParkSpacePauseDate(String startDate, String endDate, String pauseDate) {
        if (pauseDate.equals("-1")) {
            return false;
        }

        Calendar startCalendar = getYearToMinuteCalendar(startDate);
        Calendar endCalendar = getYearToMinuteCalendar(endDate);

        String[] pause = pauseDate.split(",");
        List<CalendarHolder> calendarHolders = new ArrayList<>(pause.length);
        for (String pauseString : pause) {
            calendarHolders.add(new CalendarHolder(getYearToDayCalendar(pauseString, false)));
        }
        Collections.sort(calendarHolders);

        for (int i = 0; i < calendarHolders.size(); i++) {
            CalendarHolder calendarHolder = calendarHolders.get(i);
            calendarHolder.endCalendar = getTodayEndCalendar(calendarHolder.startCalendar);
            if (endCalendar.compareTo(calendarHolder.startCalendar) <= 0) {
                //因为暂停共享日期已经按时间来排序了，如果预约的结束停车时间比当前的暂停共享时间早，则代表预约停车时间不在暂停共享时间内
                return false;
            } else if (startCalendar.compareTo(calendarHolder.startCalendar) >= 0 && startCalendar.compareTo(calendarHolder.endCalendar) <= 0) {
                //预约停车的开始时间在暂停共享时间内
                return true;
            } else if (endCalendar.compareTo(calendarHolder.startCalendar) > 0 && endCalendar.compareTo(calendarHolder.endCalendar) <= 0) {
                //预约停车的结束时间在暂停共享时间内
                return true;
            } else if (startCalendar.compareTo(calendarHolder.startCalendar) <= 0 && endCalendar.compareTo(calendarHolder.endCalendar) >= 0) {
                //预约停车的时间内包括暂停共享时间
                return true;
            }
        }

        //如果暂停日期都比预约停车的开始时间早的
        return false;
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param shareDays 每周共享的星期，
     * @return -1(开始时间和结束时间之间不在在共享星期之内) other(结束时间之间距离最近的共享星期开始时间的分钟数)
     */
    public static int isInShareDay(String startDate, String endDate, String shareDays) {
        if ("1,1,1,1,1,1,1".equals(shareDays)) {
            //每天都共享的则不用判断了
            return ConstansUtil.ONE_DAY_MINUTES;
        } else if (startDate.split(" ")[0].equals(endDate.split(" ")[0])) {
            //如果是停车时间都在同一天则只需要判断当天是否共享
            int parkDay = getDayOfWeek(startDate, true);//(1-7)
            String[] days = shareDays.split(",");
            if (days[parkDay - 1].equals("0")) {
                //停车那天不可以共享
                return -1;
            } else {
                return getMaxMinuteOfShareDay(endDate, parkDay, days);
            }
        } else {
            if (getDateDayDistance(startDate, endDate) > 6) {
                //如果停车时间超过6天则需要判断是否整个星期都共享,因为第一个if已经判断了，所以这里肯定是不行的
                return -1;
            } else {
                //停车时间为一到六天的(可能周一到周日)
                int startDay = getDayOfWeek(startDate, true);
                int endDay = getEndDayOfWeek(endDate);
                String[] days = shareDays.split(",");
                if (startDay < endDay) {
                    //如果停车时间是在同一个星期内则只需要判断停车的星期是否都在共享的星期内
                    for (int i = startDay; i <= endDay; i++) {
                        if (days[i - 1].equals("0")) {
                            //在预约停车的某一天是不共享的
                            return -1;
                        }
                    }
                    return getMaxMinuteOfShareDay(endDate, endDay, days);
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
                        if (days[list.get(i) - 1].equals("0")) {
                            //在预约停车的某一天是不共享的
                            return -1;
                        }
                    }
                    return getMaxMinuteOfShareDay(endDate, endDay, days);
                }
            }
        }
    }

    private static int getMaxMinuteOfShareDay(String endDate, int endDay, String[] days) {
        if (endDay == 7) {
            //是在星期天停车
            if (days[0].equals("1")) {
                //如果星期一可以停
                return ConstansUtil.ONE_DAY_MINUTES;
            } else {
                //星期一不可以停，则最大停车时间为到当天晚上凌晨
                Calendar calendar = getYearToMinuteCalendar(endDate);
                return getCalendarMinuteDistance(calendar, getTodayEndCalendar(calendar));
            }
        } else {
            if (days[endDay].equals("1")) {
                //第二天也可以共享
                return ConstansUtil.ONE_DAY_MINUTES;
            } else {
                //第二天不可以共享
                Calendar calendar = getYearToMinuteCalendar(endDate);
                return getCalendarMinuteDistance(calendar, getTodayEndCalendar(calendar));
            }
        }
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param shareDays 每周共享的星期，
     * @return true(预约停车时间不在共享的星期内)
     */
    public static boolean isNotInShareDay(String startDate, String endDate, String shareDays) {
        if ("1,1,1,1,1,1,1".equals(shareDays)) {
            //每天都共享的则不用判断了
            return false;
        } else if (startDate.split(" ")[0].equals(endDate.split(" ")[0])) {
            //如果是停车时间都在同一天则只需要判断当天是否共享
            int parkDay = getDayOfWeek(startDate, true);//(1-7)
            return !shareDays.split(",")[parkDay - 1].equals("1");
        } else {
            if (getDateDayDistance(startDate, endDate) > 6) {
                //如果停车时间超过6天则需要判断是否整个星期都共享,因为第一个if已经判断了，所以这里肯定是不行的
                return true;
            } else {
                //停车时间为一到六天的(可能周一到周日)
                int startDay = getDayOfWeek(startDate, true);
                int endDay = getEndDayOfWeek(endDate);
                String[] days = shareDays.split(",");
                if (startDay < endDay) {
                    //如果停车时间是在同一个星期内则只需要判断停车的星期是否都在共享的星期内
                    for (int i = startDay; i <= endDay; i++) {
                        if (days[i - 1].equals("0")) {
                            //在预约停车的某一天是不共享的
                            return true;
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
                        if (days[list.get(i) - 1].equals("0")) {
                            //在预约停车的某一天是不共享的
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param orderDate 别人在这个车位预约的订单时间，逗号隔开(2018-04-19 17:00*2018-04-19 19:00,2018-04-21 08:00*2018-04-22 05:00)
     * @return -1(预约停车时间和车位订单预约时间有冲突) other(最大可宽限的时长)
     */
    public static int isInOrderDate(String startDate, String endDate, String orderDate) {
        if (orderDate.equals("-1")) {
            return ConstansUtil.ONE_DAY_MINUTES;
        }

        //保存在现在时刻以后的预约时间（既是排除掉在现在以前那些被预约的时间）
        String[] orderDates = orderDate.split(",");
        List<String> usefulDates = new ArrayList<>(orderDates.length);
        Calendar nowCalendar = getYearToSecondCalendar(TimeManager.getInstance().getServerTime());

        for (String date : orderDates) {
            if (nowCalendar.compareTo(getYearToMinuteCalendar(date.substring(date.indexOf("*") + 1, date.length()))) < 0) {
                usefulDates.add(date);
            }
        }

        if (usefulDates.isEmpty()) {
            return ConstansUtil.ONE_DAY_MINUTES;
        }

        List<CalendarHolder> calendarHolders = new ArrayList<>(usefulDates.size());
        for (int i = 0; i < usefulDates.size(); i++) {
            calendarHolders.add(new CalendarHolder(getYearToMinuteCalendar(usefulDates.get(i).substring(0, usefulDates.get(i).indexOf("*"))),
                    getYearToMinuteCalendar(usefulDates.get(i).substring(usefulDates.get(i).indexOf("*") + 1, usefulDates.get(i).length()))));
        }

        Collections.sort(calendarHolders);

        Calendar startCalendar = getYearToMinuteCalendar(startDate);
        Calendar endCalendar = getYearToMinuteCalendar(endDate);
        for (int i = 0; i < calendarHolders.size(); i++) {
            CalendarHolder calendarHolder = calendarHolders.get(i);
            if (endCalendar.compareTo(calendarHolder.startCalendar) <= 0) {
                //预约的结束停车时间比订单中的开始时间早，则最大宽限时间为预约结束时间到订单的开始停车时间
                return getCalendarMinuteDistance(endCalendar, calendarHolder.startCalendar);
            } else if (startCalendar.compareTo(calendarHolder.startCalendar) >= 0 && startCalendar.compareTo(calendarHolder.endCalendar) <= 0) {
                //预约停车的开始时间在订单预约时间内
                return -1;
            } else if (endCalendar.compareTo(calendarHolder.startCalendar) > 0 && endCalendar.compareTo(calendarHolder.endCalendar) <= 0) {
                //预约停车的结束时间在订单预约时间内
                return -1;
            } else if (startCalendar.compareTo(calendarHolder.startCalendar) <= 0 && endCalendar.compareTo(calendarHolder.endCalendar) >= 0) {
                //预约停车的时间包括订单预约时间
                return -1;
            }
        }

        return ConstansUtil.ONE_DAY_MINUTES;
    }

    /**
     * @param orderDate 别人在这个车位预约的订单时间，逗号隔开(2018-04-19 17:00*2018-04-19 19:00,2018-04-21 08:00*2018-04-22 05:00)
     * @return true(预约停车时间和车位订单预约时间没有冲突)
     */
    public static boolean isNotInOrderDate(String startDate, String endDate, String orderDate) {
        if (orderDate.equals("-1")) {
            return true;
        }

        //保存在现在时刻以后的预约时间（既是排除掉在现在以前那些被预约的时间）
        String[] orderDates = orderDate.split(",");
        List<String> usefulDates = new ArrayList<>(orderDates.length);
        Calendar nowCalendar = getYearToSecondCalendar(TimeManager.getInstance().getServerTime());

        for (String date : orderDates) {
            if (nowCalendar.compareTo(getYearToMinuteCalendar(date.substring(date.indexOf("*") + 1, date.length()))) < 0) {
                usefulDates.add(date);
            }
        }

        if (usefulDates.isEmpty()) {
            return true;
        }

        List<CalendarHolder> calendarHolders = new ArrayList<>(usefulDates.size());
        for (int i = 0; i < usefulDates.size(); i++) {
            calendarHolders.add(new CalendarHolder(getYearToMinuteCalendar(usefulDates.get(i).substring(0, usefulDates.get(i).indexOf("*"))),
                    getYearToMinuteCalendar(usefulDates.get(i).substring(usefulDates.get(i).indexOf("*") + 1, usefulDates.get(i).length()))));
        }

        Collections.sort(calendarHolders);

        Calendar startCalendar = getYearToMinuteCalendar(startDate);
        Calendar endCalendar = getYearToMinuteCalendar(endDate);
        for (int i = 0; i < calendarHolders.size(); i++) {
            CalendarHolder calendarHolder = calendarHolders.get(i);
            if (endCalendar.compareTo(calendarHolder.startCalendar) <= 0) {
                //预约的结束停车时间比订单中的开始时间早，则肯定没冲突了
                return true;
            } else if (startCalendar.compareTo(calendarHolder.startCalendar) >= 0 && startCalendar.compareTo(calendarHolder.endCalendar) <= 0) {
                //预约停车的开始时间在订单预约时间内
                return false;
            } else if (endCalendar.compareTo(calendarHolder.startCalendar) > 0 && endCalendar.compareTo(calendarHolder.endCalendar) <= 0) {
                //预约停车的结束时间在订单预约时间内
                return false;
            } else if (startCalendar.compareTo(calendarHolder.startCalendar) <= 0 && endCalendar.compareTo(calendarHolder.endCalendar) >= 0) {
                //预约停车的时间包括订单预约时间
                return false;
            }
        }

        return true;
    }

    /**
     * @param orderDate 逗号隔开(2018-04-19 17:00*2018-04-19 19:00,2018-04-21 08:00*2018-04-22 05:00)
     * @return null(停车时间不在startDate - endDate时间内)   yyyy-MM-dd至yyyy-MM-dd订单的时间
     */
    public static String getOrderBetweenDate(String startDate, String endDate, String orderDate) {
        if (orderDate.equals("-1") || orderDate.equals("")) {
            return null;
        }

        //保存在现在时刻以后的预约时间（既是排除掉在现在以前那些被预约的时间）
        String[] orderDates = orderDate.split(",");
        List<String> usefulDates = new ArrayList<>(orderDates.length);
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.set(Calendar.SECOND, 0);
        nowCalendar.set(Calendar.MILLISECOND, 0);

        for (String date : orderDates) {
            if (nowCalendar.compareTo(getYearToMinuteCalendar(date.substring(date.indexOf("*") + 1, date.length()))) < 0) {
                usefulDates.add(date);
            }
        }

        if (usefulDates.isEmpty()) {
            return null;
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
                return getCalendarYearToDay(calendars[i]) + "至" + getCalendarYearToDay(calendars[i + 1]);
            }
        }
        return null;
    }

    /**
     * @param extendSecond 顺延时长
     * @param endDate      预约结束时间
     * @param orderDate    逗号隔开(2018-04-19 17:00*2018-04-19 19:00,2018-04-21 08:00*2018-04-22 05:00)
     * @return 距离最近一个预约订单可延长停车的分钟数
     */
    public static int getDistanceOfRecentOrder(String extendSecond, String endDate, String orderDate) {
        //保存在现在时刻以后的预约时间（既是排除掉在现在以前那些被预约的时间）
        String[] orderDates = orderDate.split(",");
        List<String> usefulDates = new ArrayList<>(orderDates.length);
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.set(Calendar.SECOND, 0);
        nowCalendar.set(Calendar.MILLISECOND, 0);

        for (String date : orderDates) {
            if (nowCalendar.compareTo(getYearToMinuteCalendar(date.substring(0, date.indexOf("*")))) < 0) {
                usefulDates.add(date);
            }
        }

        if (usefulDates.isEmpty()) {
            //如果没人预约则可延长一天的时间
            return 24 * 60 * 7;
        }

        //将已被预约的时间用Calendar保存
        List<Calendar> calendarList = new ArrayList<>();
        for (int i = 0; i < usefulDates.size(); i++) {
            calendarList.add(getYearToMinuteCalendar(usefulDates.get(i).substring(0, usefulDates.get(i).indexOf("*"))));
        }

        Collections.sort(calendarList, new Comparator<Calendar>() {
            @Override
            public int compare(Calendar o1, Calendar o2) {
                return o1.compareTo(o2);
            }
        });

        Calendar compareCalendar = getYearToSecondCalendar(endDate);
        compareCalendar.add(Calendar.SECOND, Integer.valueOf(extendSecond));
        compareCalendar.set(Calendar.SECOND, 0);
        if (compareCalendar.compareTo(nowCalendar) < 0) {
            //如果是已经超时的则按现在的时间来算可以延长多长时间
            compareCalendar = (Calendar) nowCalendar.clone();
        }

        return getDateMinutes(compareCalendar, calendarList.get(0));
    }

    private static class CalendarHolder implements Cloneable, Comparable<CalendarHolder> {

        private Calendar startCalendar;

        private Calendar endCalendar;

        public CalendarHolder(Calendar startCalendar) {
            this.startCalendar = startCalendar;
        }

        CalendarHolder(Calendar startCalendar, Calendar endCalendar) {
            this.startCalendar = startCalendar;
            this.endCalendar = endCalendar;
        }

        public Calendar getStartCalendar() {
            return startCalendar;
        }

        void setStartCalendar(Calendar startCalendar) {
            this.startCalendar = startCalendar;
        }

        public Calendar getEndCalendar() {
            return endCalendar;
        }

        void setEndCalendar(Calendar endCalendar) {
            this.endCalendar = endCalendar;
        }

        @Override
        protected Object clone() {
            return new CalendarHolder((Calendar) startCalendar.clone(), (Calendar) endCalendar.clone());
        }

        @Override
        public int compareTo(@NonNull CalendarHolder o) {
            return this.startCalendar.compareTo(o.startCalendar);
        }

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
        return (int) ((end.getTime() - start.getTime()) / 1000 / 3600 / 24);
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
        int minutes = (int) ((end.getTime() / 1000 - start.getTime() / 1000) / 60);
        if (minutes == 0) {
            if (end.getTime() / 1000 > start.getTime() / 1000) {
                minutes = 1;
            }
        }
        return minutes;
    }

    /**
     * @param seconds 秒数
     * @return 返回给定的秒数为x天x小时x分钟
     */
    public static String getDistanceForDayMinute(String seconds) {
        if (seconds == null) {
            return "0分钟";
        }

        long time = Long.valueOf(seconds);
        int minutesDistance = (int) (time / 60);
        if (minutesDistance == 0) {
            if (time > 0) {
                minutesDistance = 1;
            }
        }

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

        if (minutesDistance > 0 || stringBuilder.length() == 0) {
            stringBuilder.append(minutesDistance);
            stringBuilder.append("分钟");
        }
        return stringBuilder.toString();
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
        if (minutesDistance > 0 || stringBuilder.length() == 0) {
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
        startCalendar.add(Calendar.SECOND, addSecond.equals("-1") ? 0 : Integer.valueOf(addSecond));

        int minutesDistance = (int) ((endCalenar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000 / 60);
        if (minutesDistance == 0) {
            if (endCalenar.getTimeInMillis() / 1000 > startCalendar.getTimeInMillis() / 1000) {
                minutesDistance = 1;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (minutesDistance / 60 > 0) {
            stringBuilder.append(minutesDistance / 60);
            stringBuilder.append("小时");
            minutesDistance -= minutesDistance / 60 * 60;
        }

        if (minutesDistance > 0 || stringBuilder.length() == 0) {
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
    public static String getDistanceForDayHourMinute(String startDate, String endDate) {
        Calendar startCalendar = getYearToSecondCalendar(startDate);
        Calendar endCalenar = getYearToSecondCalendar(endDate);

        int minutesDistance = (int) getCalendarDistance(startCalendar, endCalenar);
        if (minutesDistance == 0) {
            if (endCalenar.getTimeInMillis() / 1000 > startCalendar.getTimeInMillis() / 1000) {
                minutesDistance = 1;
            }
        }

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

        if (minutesDistance > 0 || stringBuilder.length() == 0) {
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
    public static String getDistanceForDayHourMinuteAddStart(String startDate, String endDate, String addSecond) {
        Calendar startCalendar = getYearToSecondCalendar(startDate);
        Calendar endCalenar = getYearToSecondCalendar(endDate);
        startCalendar.add(Calendar.SECOND, addSecond.equals("-1") ? 0 : Integer.valueOf(addSecond));

        int minutesDistance = (int) getCalendarDistance(startCalendar, endCalenar);
        if (minutesDistance == 0) {
            if (endCalenar.getTimeInMillis() / 1000 > startCalendar.getTimeInMillis() / 1000) {
                minutesDistance = 1;
            }
        }

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

        if (minutesDistance > 0 || stringBuilder.length() == 0) {
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
    public static String getDistanceForDayHourMinuteAddEnd(String startDate, String endDate, String addSecond) {
        Calendar startCalendar = getYearToSecondCalendar(startDate);
        Calendar endCalenar = getYearToSecondCalendar(endDate);
        endCalenar.add(Calendar.SECOND, addSecond.equals("-1") ? 0 : Integer.valueOf(addSecond));

        int minutesDistance = (int) ((endCalenar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000 / 60);
        if (minutesDistance == 0) {
            if (endCalenar.getTimeInMillis() / 1000 > startCalendar.getTimeInMillis() / 1000) {
                minutesDistance = 1;
            }
        }

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

        if (minutesDistance > 0 || stringBuilder.length() == 0) {
            stringBuilder.append(minutesDistance);
            stringBuilder.append("分钟");
        }
        return stringBuilder.toString();
    }

    /**
     * @param startDate 比较的开始时间，格式为yyyy-MM-dd HH:mm:ss
     * @param endDate   比较的结束时间，格式为yyyy-MM-dd HH:mm:ss
     * @return 返回两个时间段相差的分钟数(x天x时x分)
     */
    public static String getDistanceForDayTimeMinute(String startDate, String endDate) {
        Calendar startCalendar = getYearToSecondCalendar(startDate);
        Calendar endCalenar = getYearToSecondCalendar(endDate);

        int minutesDistance = (int) ((endCalenar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000 / 60);
        if (minutesDistance == 0) {
            if (endCalenar.getTimeInMillis() / 1000 > startCalendar.getTimeInMillis() / 1000) {
                minutesDistance = 1;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (minutesDistance / 60 / 24 > 0) {
            stringBuilder.append(minutesDistance / 60 / 24);
            stringBuilder.append("天");
            minutesDistance -= minutesDistance / 60 / 24 * 60 * 24; //减去n天的分钟数
        }

        if (minutesDistance / 60 > 0) {
            stringBuilder.append(minutesDistance / 60);
            stringBuilder.append("时");
            minutesDistance -= minutesDistance / 60 * 60;
        }

        if (minutesDistance > 0 || stringBuilder.length() == 0) {
            stringBuilder.append(minutesDistance);
            stringBuilder.append("分");
        }
        return stringBuilder.toString();
    }

    /**
     * @param startDate 比较的开始时间，格式为yyyy-MM-dd HH:mm:ss
     * @param endDate   比较的结束时间，格式为yyyy-MM-dd HH:mm:ss
     * @param addSecond 为endDate加上相应的秒数
     * @return 返回两个时间段相差的分钟数(x时x分)
     */
    public static String getDistanceForHourMinuteAddEnd(String startDate, String endDate, String addSecond) {
        Calendar startCalendar = getYearToSecondCalendar(startDate);
        Calendar endCalenar = getYearToSecondCalendar(endDate);
        endCalenar.add(Calendar.SECOND, addSecond.equals("-1") ? 0 : Integer.valueOf(addSecond));

        int minutesDistance = (int) ((endCalenar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000 / 60);
        if (minutesDistance == 0) {
            if (endCalenar.getTimeInMillis() / 1000 > startCalendar.getTimeInMillis() / 1000) {
                minutesDistance = 1;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (minutesDistance / 60 > 0) {
            stringBuilder.append(minutesDistance / 60);
            stringBuilder.append("时");
            minutesDistance -= minutesDistance / 60 * 60;
        }

        if (minutesDistance > 0 || stringBuilder.length() == 0) {
            stringBuilder.append(minutesDistance);
            stringBuilder.append("分");
        }
        return stringBuilder.toString();
    }

    /**
     * @param startDate 比较的开始时间，格式为yyyy-MM-dd HH:mm:ss
     * @param endDate   比较的结束时间，格式为yyyy-MM-dd HH:mm:ss
     * @param addSecond 为startDate加上相应的秒数
     * @return 返回两个时间段相差的分钟数(x时x分)
     */
    public static String getDistanceForHourMinuteAddStart(String startDate, String endDate, String addSecond) {
        Calendar startCalendar = getYearToSecondCalendar(startDate);
        Calendar endCalenar = getYearToSecondCalendar(endDate);
        startCalendar.add(Calendar.SECOND, addSecond.equals("-1") ? 0 : Integer.valueOf(addSecond));

        int minutesDistance = (int) ((endCalenar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000 / 60);
        if (minutesDistance == 0) {
            if (endCalenar.getTimeInMillis() / 1000 > startCalendar.getTimeInMillis() / 1000) {
                minutesDistance = 1;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (minutesDistance / 60 > 0) {
            stringBuilder.append(minutesDistance / 60);
            stringBuilder.append("时");
            minutesDistance -= minutesDistance / 60 * 60;
        }

        if (minutesDistance > 0 || stringBuilder.length() == 0) {
            stringBuilder.append(minutesDistance);
            stringBuilder.append("分");
        }
        return stringBuilder.toString();
    }

    /**
     * @param date      格式为yyyy-MM-dd HH:mm:ss
     * @param addSecond 添加的秒数
     */
    public static String getYearToMinute(String date, String addSecond) {
        Calendar calendar = getYearToSecondCalendar(date);
        calendar.add(Calendar.SECOND, addSecond.equals("-1") ? 0 : Integer.valueOf(addSecond));
        return getCalenarYearToMinutes(calendar);
    }

    /**
     * @param date      格式为yyyy-MM-dd HH:mm:ss
     * @param addSecond 添加的秒数
     * @return yyyy-MM-dd HH:mm
     */
    public static String getYearToMinute(String date, int addSecond) {
        Calendar calendar = getYearToSecondCalendar(date);
        calendar.add(Calendar.SECOND, addSecond == -1 ? 0 : addSecond);
        return getCalenarYearToMinutes(calendar);
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
        //周一到周日为2到6,1(周日)
        day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            day = 7;
        }
        return day;
    }

    /**
     * @param date yyyy-MM-dd HH:mm
     * @return 获取当天是星期几(星期一到星期七)
     */
    private static int getEndDayOfWeek(String date) {
        int day;
        Calendar calendar = getCalendar();
        setCalendarYearToMinute(calendar, date);
        //周一到周日为2到6,1(周日)
        day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            day = 7;
        }
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0) {
            //因为如果时间为2018-11-13 24:00,实际上Calendar会认为是2018-11-14 00:00。
            // 所以如果时间为当天凌晨的，则判断为星期几的时候为前一天。
            // 因为当13号为星期二晚上凌晨的时候，Calendar会认为是星期三了，这样如果星期三是不共享的则会判断错误，所以要回退一天
            if (day == 1) {
                day = 7;
            } else {
                day--;
            }
        }
        return day;
    }

    /**
     * @return 获取当天是星期几(星期一到星期七)
     */
    public static int getDayOfWeek(Calendar calendar) {
        int day;
        day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            day = 7;
        }
        return day;
    }

    /**
     * @param day 天数从1-7
     * @return 一到日
     */
    public static String getDayOfNumber(@IntRange(from = 1, to = 7) int day) {
        switch (day) {
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            default:
                return "日";
        }
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param shareTime 共享时间段(08:00 - 12:00,14:00 - 18:00,20:00 - 03:00) 注：最多3个，用“,”隔开
     * @return -1(共享时间段内没有一个包含开始时间和结束时间的)  other(能停车的时间段的开始时间,结束时间)
     */
    public static int isInShareTime(String startDate, String endDate, String shareTime) {
        if (shareTime.equals("-1")) {
            //如果是全天共享的那直接就可以了
            return ConstansUtil.ONE_DAY_MINUTES;
        } else if (getDateDayDistance(startDate, endDate) < 1) {
            //停车时长不大于24小时
            Calendar startCanlendar = getYearToMinuteCalendar(startDate);
            Calendar endCanlendar = getYearToMinuteCalendar(endDate);

            //获取停车位共享的各个时间段
            String[] times = shareTime.split(",");

            List<CalendarHolder> calendarHolders = new ArrayList<>();
            for (String time : times) {
                CalendarHolder calendarHolder = new CalendarHolder(getSameYearToDayCalendar(startCanlendar, time.substring(0, time.indexOf(" - "))),
                        getSameYearToDayCalendar(startCanlendar, time.substring(time.indexOf(" - ") + 3, time.length())));
                calendarHolders.add(calendarHolder);
                if (calendarHolder.startCalendar.compareTo(calendarHolder.endCalendar) >= 0) {
                    //预约的开始时间为2018-11-13
                    CalendarHolder theDayBefore = (CalendarHolder) calendarHolder.clone();
                    //20:00 - 03:00 则添加时间为2018-11-12 20:00 - 2018-11-13 03:00
                    theDayBefore.startCalendar.add(Calendar.DAY_OF_MONTH, -1);
                    calendarHolders.add(theDayBefore);

                    //20:00 - 03:00 则添加时间为2018-11-13 20:00 - 2018-11-14 03:00
                    calendarHolder.endCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
            }

            if (!isInSameDay(startDate, endDate)) {
                for (String time : times) {
                    CalendarHolder calendarHolder = new CalendarHolder(getSameYearToDayCalendar(endCanlendar, time.substring(0, time.indexOf(" - "))),
                            getSameYearToDayCalendar(endCanlendar, time.substring(time.indexOf(" - ") + 3, time.length())));
                    calendarHolders.add(calendarHolder);
                    if (calendarHolder.startCalendar.compareTo(calendarHolder.endCalendar) >= 0) {
                        //预约的结束时间为2018-11-14
                        //20:00 - 03:00 则添加时间为2018-11-14 20:00 - 2018-11-15 03:00
                        calendarHolder.endCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                }
            }

            Collections.sort(calendarHolders);

            for (int i = 0; i < calendarHolders.size(); i++) {
                CalendarHolder calendarHolder = calendarHolders.get(i);
                if (startCanlendar.compareTo(calendarHolder.startCalendar) >= 0 && endCanlendar.compareTo(calendarHolder.endCalendar) <= 0) {
                    //预约停车时间都在共享时间内
                    return getCalendarMinuteDistance(endCanlendar, calendarHolder.endCalendar);
                } else if (startCanlendar.compareTo(calendarHolder.startCalendar) < 0) {
                    //如果预约停车的开始时间比这个共享时间的开始时间还早，就可以提前退出了
                    return -1;
                }
            }
        }
        return -1;
    }

    /**
     * @param startDate 开始时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束时间，格式为yyyy-MM-dd HH:mm
     * @param shareTime 共享时间段(08:00 - 12:00,14:00 - 18:00,20:00 - 03:00) 注：最多3个，用“,”隔开
     * @return true(预约停车时间不在共享时间段内)
     */
    public static boolean isNotInShareTime(String startDate, String endDate, String shareTime) {
        if (shareTime.equals("-1")) {
            //如果是全天共享的那直接就可以了
            return false;
        } else if (getDateDayDistance(startDate, endDate) < 1) {
            //停车时长不大于24小时
            Calendar startCanlendar = getYearToMinuteCalendar(startDate);
            Calendar endCanlendar = getYearToMinuteCalendar(endDate);

            //获取停车位共享的各个时间段
            String[] times = shareTime.split(",");

            List<CalendarHolder> calendarHolders = new ArrayList<>();
            for (String time : times) {
                CalendarHolder calendarHolder = new CalendarHolder(getSameYearToDayCalendar(startCanlendar, time.substring(0, time.indexOf(" - "))),
                        getSameYearToDayCalendar(startCanlendar, time.substring(time.indexOf(" - ") + 3, time.length())));
                calendarHolders.add(calendarHolder);
                if (calendarHolder.startCalendar.compareTo(calendarHolder.endCalendar) >= 0) {
                    //预约的开始时间为2018-11-13
                    CalendarHolder theDayBefore = (CalendarHolder) calendarHolder.clone();
                    //20:00 - 03:00 则添加时间为2018-11-12 20:00 - 2018-11-13 03:00
                    theDayBefore.startCalendar.add(Calendar.DAY_OF_MONTH, -1);
                    calendarHolders.add(theDayBefore);

                    //20:00 - 03:00 则添加时间为2018-11-13 20:00 - 2018-11-14 03:00
                    calendarHolder.endCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }
            }

            if (!isInSameDay(startDate, endDate)) {
                for (String time : times) {
                    CalendarHolder calendarHolder = new CalendarHolder(getSameYearToDayCalendar(endCanlendar, time.substring(0, time.indexOf(" - "))),
                            getSameYearToDayCalendar(endCanlendar, time.substring(time.indexOf(" - ") + 3, time.length())));
                    calendarHolders.add(calendarHolder);
                    if (calendarHolder.startCalendar.compareTo(calendarHolder.endCalendar) >= 0) {
                        //预约的结束时间为2018-11-14
                        //20:00 - 03:00 则添加时间为2018-11-14 20:00 - 2018-11-15 03:00
                        calendarHolder.endCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                }
            }

            Collections.sort(calendarHolders);

            for (int i = 0; i < calendarHolders.size(); i++) {
                CalendarHolder calendarHolder = calendarHolders.get(i);
                if (startCanlendar.compareTo(calendarHolder.startCalendar) >= 0 && endCanlendar.compareTo(calendarHolder.endCalendar) <= 0) {
                    //预约停车时间都在共享时间内
                    return false;
                } else if (startCanlendar.compareTo(calendarHolder.startCalendar) < 0) {
                    //如果预约停车的开始时间比这个共享时间的开始时间还早，就可以提前退出了
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * @param shareTime 共享时间段(08:00 - 12:00,14:00 - 18:00) 注：最多3个，用“,”隔开
     * @return 在当前停车的共享时间内能延长的分钟数
     */
    public static int getDistanceForRecentShareTime(Calendar calendar, String shareTime) {
        if (shareTime.equals("-1")) {
            //如果是全天共享的那直接就可以了
            return 24 * 60 * 7;
        } else {
            //获取停车位共享的各个时间段
            String[] times = shareTime.split(",");
            List<CalendarHolder> calendarHolders = new ArrayList<>();
            Calendar startCalendar;
            Calendar endCalendar;
            for (String time : times) {
                startCalendar = getSameYearToDayCalendar(calendar, time.substring(0, time.indexOf(" - ")));
                endCalendar = getSameYearToDayCalendar(calendar, time.substring(time.indexOf(" - ") + 3, time.length()));
                if (startCalendar.compareTo(endCalendar) > 0) {
                    //跨天06-08 22:00 - 06-09 05:00
                    //先添加06-08 00:00 - 06-08 05:00
                    calendarHolders.add(new CalendarHolder(getTodayStartCalendar(endCalendar), endCalendar));

                    //再添加06-08 22:00 - 06-09 05:00
                    endCalendar = (Calendar) endCalendar.clone();
                    endCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    calendarHolders.add(new CalendarHolder(startCalendar, endCalendar));
                } else {
                    calendarHolders.add(new CalendarHolder(startCalendar, endCalendar));
                }
            }

            for (CalendarHolder calendarHolder : calendarHolders) {
                if (isIntersection(calendar, calendarHolder.getStartCalendar(), calendarHolder.getEndCalendar())) {
                    return getDateMinutes(calendar, calendarHolder.getEndCalendar());
                }
            }
            return 24 * 60 * 7;
        }
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

                Log.e(TAG, "isInShareTime   shareStartCalendar0: " + getCalendarMonthToMinute(shareStartCalendar));
                Log.e(TAG, "isInShareTime   shareEndCalendar0: " + getCalendarMonthToMinute(shareEndCalendar));
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
        Log.e(TAG, "isInShareTime   startCalendar: " + getCalendarMonthToMinute(startCalendar));
        Log.e(TAG, "isInShareTime   endCalendar: " + getCalendarMonthToMinute(endCalendar));
        Log.e(TAG, "isInShareTime   shareStartCalendar: " + getCalendarMonthToMinute(shareStartCalendar));
        Log.e(TAG, "isInShareTime   shareEndCalendar: " + getCalendarMonthToMinute(shareEndCalendar));
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
    public static boolean isInSameDay(String startDate, String endDate) {
        return startDate.split(" ")[0].equals(endDate.split(" ")[0]);
    }

    public static boolean isInSameDay(Calendar startCalendar, Calendar endCalendar) {
        return getCalendarYearToDay(startCalendar).equals(getCalendarYearToDay(endCalendar));
    }

    public static String getParkOvertime(ParkOrderInfo parkOrderInfo) {
        if (getYearToSecondCalendar(parkOrderInfo.getOrderEndTime(), parkOrderInfo.getExtensionTime()).compareTo(
                getYearToSecondCalendar(parkOrderInfo.getParkEndTime())) <= 0) {
            //停车时长超过预约时长
            return DateUtil.getDistanceForDayHourMinuteAddStart(parkOrderInfo.getOrderEndTime(), parkOrderInfo.getPark_end_time(), parkOrderInfo.getExtensionTime());
        } else {
            return "未超时";
        }
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
        Calendar calendar = TimeManager.getInstance().getCurrentCalendar();
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

        calendar.add(Calendar.SECOND, addSecond.equals("-1") ? 0 : Integer.valueOf(addSecond));
        return calendar;
    }

    /**
     * @param date      格式为yyyy-MM-dd HH:mm:ss
     * @param addSecond 添加的秒数
     * @return 格式为yyyy-MM-dd对应的Calendar
     */
    public static Calendar getYearToSecondCalendar(String date, int addSecond) {
        Calendar calendar = Calendar.getInstance();
        String[] yearToMinute = date.split("-");
        calendar.set(Calendar.YEAR, Integer.valueOf(yearToMinute[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(yearToMinute[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(yearToMinute[2].substring(0, yearToMinute[2].indexOf(" "))));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].indexOf(" ") + 1, yearToMinute[2].indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].indexOf(":") + 1, yearToMinute[2].lastIndexOf(":"))));
        calendar.set(Calendar.SECOND, Integer.valueOf(yearToMinute[2].substring(yearToMinute[2].lastIndexOf(":") + 1, yearToMinute[2].length())));
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.SECOND, addSecond);
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
     * @param startDate 格式为yyyy-MM-dd HH:mm:ss
     * @param endDate   格式为yyyy-MM-dd HH:mm:ss
     * @param addSecond 为startDate添加的秒数
     * @return -1(开始时间加上添加的秒数比结束时间早)   0(开始时间加上添加的秒数后和结束时间一样)      1(开始时间加上添加的秒数后比结束时间长)
     */
    public static int compareYearToSecondAddStart(String startDate, String endDate, String addSecond) {
        Calendar startCalendar = getYearToSecondCalendar(startDate);
        Calendar endCalendar = getYearToSecondCalendar(endDate);
        startCalendar.add(Calendar.SECOND, addSecond.equals("-1") ? 0 : Integer.valueOf(addSecond));
        return startCalendar.compareTo(endCalendar);
    }

    /**
     * @param startDate 格式为yyyy-MM-dd HH:mm:ss
     * @param endDate   格式为yyyy-MM-dd HH:mm:ss
     * @return -1(开始时间加上添加的秒数比结束时间早)   0(开始时间加上添加的秒数后和结束时间一样)      1(开始时间加上添加的秒数后比结束时间长)
     */
    public static int compareYearToSecond(String startDate, String endDate) {
        Calendar startCalendar = getYearToSecondCalendar(startDate);
        Calendar endCalendar = getYearToSecondCalendar(endDate);
        return startCalendar.compareTo(endCalendar);
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
        if (orderDate.equals("-1")) {
            return false;
        }
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
        return startCalendar.compareTo(otherStartCalendar) >= 0 && startCalendar.compareTo(otherEndCalendar) < 0
                || endCalendar.compareTo(otherStartCalendar) > 0 && endCalendar.compareTo(otherEndCalendar) <= 0
                || startCalendar.compareTo(otherStartCalendar) <= 0 && endCalendar.compareTo(otherEndCalendar) >= 0
                || otherStartCalendar.compareTo(startCalendar) <= 0 && otherEndCalendar.compareTo(endCalendar) >= 0;
    }

    /**
     * @return true(calendar在startCalendar和endCalendar之间)
     */
    public static boolean isIntersection(Calendar calendar, Calendar startCalendar, Calendar endCalendar) {
        return startCalendar.compareTo(calendar) <= 0 && calendar.compareTo(endCalendar) <= 0;
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
     * @return HH时mm分
     */
    public static String getHourToMinute(String yearToSecond) {
        return yearToSecond.substring(yearToSecond.indexOf(" ") + 1, yearToSecond.indexOf(":")) + "时"
                + yearToSecond.substring(yearToSecond.indexOf(":") + 1, yearToSecond.lastIndexOf(":")) + "分";
    }

    /**
     * @param yearToSecond yyyy-MM-dd HH:mm:ss
     * @return HH点mm分
     */
    public static String getPointToMinute(String yearToSecond) {
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
     * @param date yyyy-MM-dd HH:mm:ss
     * @return date对应的年月日，格式2018年05月07日 15时12分
     */
    public static String getYearToMinuteWithText(String date) {
        StringBuilder stringBuilder = new StringBuilder();
        int index = date.indexOf("-");
        stringBuilder.append(date.substring(0, index));
        stringBuilder.append("年");
        stringBuilder.append(date.substring(index + 1, index = date.lastIndexOf("-")));
        stringBuilder.append("月");
        stringBuilder.append(date.substring(index + 1, index = date.indexOf(" ")));
        stringBuilder.append("日 ");
        stringBuilder.append(date.substring(index + 1, index = date.indexOf(":")));
        stringBuilder.append("时");
        stringBuilder.append(date.substring(index + 1, date.lastIndexOf(":")));
        stringBuilder.append("分");
        return stringBuilder.toString();
    }

    /**
     * @param date yyyy-MM-dd HH:mm:ss(yyyy-MM-dd也行)
     * @return date对应的年月日，格式2018.05.07
     */
    public static String getYearToDayWithPointText(String date) {
        int index = date.indexOf(" ");
        if (index > 0) {
            return date.replaceAll("-", ".").substring(0, index);
        }
        return date.replaceAll("-", ".");
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

    /**
     * @return MM-dd HH:mm
     */
    public static String getCalendarMonthToMinute(Calendar calendar) {
        if (calendar == null) {
            return "";
        }
        return thanTen((calendar.get(Calendar.MONTH) + 1)) + "-" + thanTen(calendar.get(Calendar.DAY_OF_MONTH)) + " "
                + thanTen(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + thanTen(calendar.get(Calendar.MINUTE));
    }

    /**
     * @return MM月dd日 HH时mm分
     */
    public static String getCalendarMonthToMinuteWithText(Calendar calendar) {
        if (calendar == null) {
            return "";
        }
        return thanTen((calendar.get(Calendar.MONTH) + 1)) + "月" + thanTen(calendar.get(Calendar.DAY_OF_MONTH)) + "日 "
                + thanTen(calendar.get(Calendar.HOUR_OF_DAY)) + "时" + thanTen(calendar.get(Calendar.MINUTE)) + "分";
    }

    /**
     * @return yyyy-MM-dd HH:mm
     */
    public static String getCalenarYearToMinutes(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "-" + thanTen((calendar.get(Calendar.MONTH) + 1)) + "-" + thanTen(calendar.get(Calendar.DAY_OF_MONTH))
                + " " + thanTen(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + thanTen(calendar.get(Calendar.MINUTE));
    }

    /**
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getCalenarYearToSecond(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "-" + thanTen((calendar.get(Calendar.MONTH) + 1)) + "-" + thanTen(calendar.get(Calendar.DAY_OF_MONTH))
                + " " + thanTen(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + thanTen(calendar.get(Calendar.MINUTE)) + ":" + thanTen(calendar.get(Calendar.SECOND));
    }

    /**
     * @param date yyyy-MM-dd HH:mm:ss
     * @return 如果现在和date是同一天，则返回HH点mm分,如果不是同一天则返回dd日HH点mm分
     */
    public static String getDayPointMinute(String date) {
        Calendar calendar = getYearToSecondCalendar(date);
        if (isInSameDay(calendar, getCalendar())) {
            return thanTen(calendar.get(Calendar.HOUR_OF_DAY)) + "点" + thanTen(calendar.get(Calendar.MINUTE)) + "分";
        } else {
            return thanTen(calendar.get(Calendar.DAY_OF_MONTH)) + "日" + thanTen(calendar.get(Calendar.HOUR_OF_DAY)) + "点"
                    + thanTen(calendar.get(Calendar.MINUTE)) + "分";
        }
    }

    /**
     * @param date yyyy-MM-dd HH:mm:ss
     * @return xx日 HH:mm
     */
    public static String getDayToMinute(String date) {
        return thanTen(date.substring(date.lastIndexOf("-") + 1, date.indexOf(" "))) + "日" +
                thanTen(date.substring(date.indexOf(" ") + 1, date.lastIndexOf(":")));
    }

    /**
     * @param date       yyyy-MM-dd HH:mm:ss
     * @param addSeconds 在date的基础上加上的秒数
     * @return xx日 HH:mm
     */
    public static String getDayToMinute(String date, String addSeconds) {
        Calendar calendar = getYearToSecondCalendar(date, addSeconds);
        return thanTen(calendar.get(Calendar.DAY_OF_MONTH)) + "日" +
                thanTen(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + thanTen(calendar.get(Calendar.MINUTE));
    }

    /**
     * @param yearToSecond yyyy-MM-dd HH:mm:ss
     * @return yyyy-MM-dd HH:mm
     */
    public static String deleteSecond(String yearToSecond) {
        return yearToSecond.substring(0, yearToSecond.lastIndexOf(":"));
    }

    /**
     * @return 停车时长，格式为(正常时长，高峰时长，超时时长)
     */
    public static String getParkOrderTime(ParkOrderInfo parkOrderInfo) {
        StringBuilder parkTime = new StringBuilder();
        Calendar startParkCalendar = getYearToSecondCalendar(parkOrderInfo.getParkStartTime());
        Calendar endParkCalendar = getYearToSecondCalendar(parkOrderInfo.getParkEndTime());
        Calendar appointStartParkCalendar = getYearToSecondCalendar(parkOrderInfo.getOrderStartTime());
        Calendar appointEndParkCalendar = getYearToSecondCalendar(parkOrderInfo.getOrderEndTime());
        Calendar appointMaxEndParkCalendar = getYearToSecondCalendar(parkOrderInfo.getOrderEndTime(), parkOrderInfo.getExtensionTime());
        boolean shouldAddOvertime = true;
        if (startParkCalendar.compareTo(appointStartParkCalendar) < 0) {
            //提前停车
            if (endParkCalendar.compareTo(appointMaxEndParkCalendar) > 0) {
                //超时
                caculateParkTime(parkTime, parkOrderInfo.getHigh_time(), startParkCalendar, appointMaxEndParkCalendar);
                parkTime.append(",");
                parkTime.append(getDateSeconds(appointMaxEndParkCalendar, endParkCalendar));
                shouldAddOvertime = false;
            } else {
                //没有超时
                if (parkOrderInfo.isCanEarlyLeaveTime()) {
                    //可以提前离场
                    caculateParkTime(parkTime, parkOrderInfo.getHigh_time(), startParkCalendar, endParkCalendar);
                } else {
                    //不可以提前离场
                    if (endParkCalendar.compareTo(appointEndParkCalendar) > 0) {
                        //在顺延时长内结束停车
                        caculateParkTime(parkTime, parkOrderInfo.getHigh_time(), startParkCalendar, endParkCalendar);
                    } else {
                        caculateParkTime(parkTime, parkOrderInfo.getHigh_time(), startParkCalendar, appointEndParkCalendar);
                    }
                }
            }
        } else {
            //没有提前停车
            if (endParkCalendar.compareTo(appointMaxEndParkCalendar) > 0) {
                //超时
                caculateParkTime(parkTime, parkOrderInfo.getHigh_time(), appointStartParkCalendar, appointMaxEndParkCalendar);
                parkTime.append(",");
                parkTime.append(getDateSeconds(appointMaxEndParkCalendar, endParkCalendar));
                shouldAddOvertime = false;
            } else {
                //没有超时
                if (parkOrderInfo.isCanEarlyLeaveTime()) {
                    //可以提前离场
                    caculateParkTime(parkTime, parkOrderInfo.getHigh_time(), appointStartParkCalendar, endParkCalendar);
                } else {
                    //不可以提前离场
                    if (endParkCalendar.compareTo(appointEndParkCalendar) > 0) {
                        //在顺延时长内结束停车
                        caculateParkTime(parkTime, parkOrderInfo.getHigh_time(), appointStartParkCalendar, endParkCalendar);
                    } else {
                        //提前结束停车
                        caculateParkTime(parkTime, parkOrderInfo.getHigh_time(), appointStartParkCalendar, appointEndParkCalendar);
                    }
                }
            }
        }
        if (shouldAddOvertime) {
            parkTime.append(",");
            parkTime.append("0");
        }
        return parkTime.toString();
    }

    /**
     * 计算停车时长
     *
     * @param parkTime          最后结果放在这里，格式为(正常时长，高峰时长，超时时长)
     * @param highDate          高峰时段(HH:mm - HH:mm)
     * @param startParkCalendar 停车的开始时间
     * @param endParkCalendar   停车的结束时间
     */
    private static void caculateParkTime(StringBuilder parkTime, String highDate, Calendar startParkCalendar, Calendar endParkCalendar) {
        Log.e(TAG, "caculateParkTime    startParkCalendar: " + getCalenarYearToSecond(startParkCalendar));
        Log.e(TAG, "caculateParkTime    endParkCalendar: " + getCalenarYearToSecond(endParkCalendar));
        String[] highDates = highDate.split(" - ");
        boolean highDateInSameDay = isHighDateInSameDay(highDate);  //高峰期是否在同一天
        long totalTimeSlotSeconds = getDateSeconds(startParkCalendar, endParkCalendar);
        long highTimeSlotSeconds = 0;
        Calendar hightStartCalendar;
        Calendar hightEndCalendar;
        if (highDateInSameDay) {
            //高峰期在同一天,08:00 - 15:00
            Log.e(TAG, "caculateParkTime: 高峰期在同一天");
            hightStartCalendar = getSameYearToDayCalendar(startParkCalendar, highDates[0]);
            hightEndCalendar = getSameYearToDayCalendar(startParkCalendar, highDates[1]);
            if (isIntersection(startParkCalendar, hightStartCalendar, hightEndCalendar)) {
                //开始停车时间在高峰期内,2018-10-27 08:00:00
                Log.e(TAG, "caculateParkTime: 开始停车时间在高峰期内");
                if (isIntersection(endParkCalendar, hightStartCalendar, hightEndCalendar)) {
                    //结束停车时间也在高峰期内,2018-10-27 10:00:00
                    highTimeSlotSeconds += getDateSeconds(startParkCalendar, endParkCalendar);
                } else {
                    //停车时间比当天的高峰期还晚,2018-10-27 16:00:00
                    hightStartCalendar = startParkCalendar;     //需要计算高峰期的时间为开始停车时间到高峰期的结束时间
                    highTimeSlotSeconds += calulateHighParkTime(highDates[0], hightStartCalendar, hightEndCalendar,
                            startParkCalendar, endParkCalendar);
                }
            } else if (hightEndCalendar.compareTo(startParkCalendar) < 0) {
                //开始停车时间比高峰期晚，即开始停车那天是没有高峰期的，所以算第二天的.2018-10-27 16:00
                hightStartCalendar.add(Calendar.DAY_OF_MONTH, 1);   //2018-10-28 08:00:00
                hightEndCalendar.add(Calendar.DAY_OF_MONTH, 1);     //2018-10-28 15:00:00
                if (hightStartCalendar.compareTo(endParkCalendar) < 0) {
                    //停车时间内有高峰期
                    Log.e(TAG, "caculateParkTime: 第二天有高峰期");
                    if (isIntersection(endParkCalendar, hightStartCalendar, hightEndCalendar)) {
                        //第二天停车就结束，并且停车结束时间在高峰期内，2018-10-28 10:00:00
                        Log.e(TAG, "caculateParkTime: 第二天停车就结束，并且停车结束时间在高峰期内");
                        highTimeSlotSeconds += getDateSeconds(hightStartCalendar, endParkCalendar);
                    } else {
                        highTimeSlotSeconds += calulateHighParkTime(null, hightStartCalendar, hightEndCalendar,
                                startParkCalendar, endParkCalendar);
                    }
                }
            } else {
                //开始停车时间比高峰期早，2018-10-27 07:00:00
                Log.e(TAG, "caculateParkTime: 开始停车时间比高峰期早");
                if (endParkCalendar.compareTo(hightStartCalendar) >= 0) {
                    //停车时间有在高峰期内的
                    if (isIntersection(endParkCalendar, hightStartCalendar, hightEndCalendar)) {
                        //结束停车在高峰期内，2018-10-27 10:00:00
                        highTimeSlotSeconds += getDateSeconds(hightStartCalendar, endParkCalendar);
                    } else {
                        //结束停车时间比当天的高峰期结束时间还晚的
                        highTimeSlotSeconds += calulateHighParkTime(null, hightStartCalendar, hightEndCalendar,
                                startParkCalendar, endParkCalendar);
                    }
                }
            }
        } else {
            //高峰期不在同一天，08:00 - 03:00
            Log.e(TAG, "caculateParkTime: 高峰期不在同一天");
            hightStartCalendar = getSameYearToDayCalendar(startParkCalendar, highDates[0]);
            hightEndCalendar = getSameYearToDayCalendar(startParkCalendar, highDates[1], 1);
            if (isIntersection(startParkCalendar, hightStartCalendar, hightEndCalendar)) {
                //开始停车时间在高峰期内，2018-10-26 09:00:00
                Log.e(TAG, "caculateParkTime: 开始停车时间在高峰期内");
                if (endParkCalendar.compareTo(hightEndCalendar) <= 0) {
                    //停车时间也在高峰期内,2018-10-27 02:00:00
                    highTimeSlotSeconds += getDateSeconds(startParkCalendar, endParkCalendar);
                    Log.e(TAG, "caculateParkTime: 停车时间都在高峰期内" + highTimeSlotSeconds);
                } else {
                    //结束停车时间不在高峰期内,2018-10-27 05:00:00
                    hightStartCalendar = startParkCalendar;     //设置开始算的高峰期为2018-10-26 09:00:00
                    Log.e(TAG, "caculateParkTime: 结束停车时间不在高峰期内");
                    highTimeSlotSeconds += calulateHighParkTime(highDates[0], hightStartCalendar, hightEndCalendar,
                            startParkCalendar, endParkCalendar);
                }
            } else {
                //2018-10-26 03:00:00
                hightEndCalendar.add(Calendar.DAY_OF_MONTH, -1);
                Calendar hightEndDayStartCalendar = getTodayStartCalendar(hightEndCalendar);
                if (isIntersection(startParkCalendar, hightEndDayStartCalendar, hightEndCalendar)) {
                    //开始停车时间在第二天的高峰期，2018-10-26 01:00:00
                    Log.e(TAG, "caculateParkTime: 开始停车时间在第二天的高峰期");
                    if (endParkCalendar.compareTo(hightEndCalendar) <= 0) {
                        //停车时间都在第二天高峰期内,2018-10-26 02:00:00
                        highTimeSlotSeconds += getDateSeconds(startParkCalendar, endParkCalendar);
                        Log.e(TAG, "caculateParkTime: 停车时间都在第二天高峰期内");
                    } else {
                        //结束停车时间不在高峰期内,2018-10-27 05:00:00
                        Log.e(TAG, "caculateParkTime: 结束停车时间不在第二天高峰期内");
                        highTimeSlotSeconds = getDateSeconds(startParkCalendar, hightEndCalendar);
                        hightEndCalendar.add(Calendar.DAY_OF_MONTH, 1);
                        if (endParkCalendar.compareTo(hightStartCalendar) > 0) {
                            //结束停车不在当天的高峰期，2018-10-27 05:00:00
                            Log.e(TAG, "caculateParkTime: 结束停车不在当天的高峰期");
                            while (true) {
                                if (isIntersection(endParkCalendar, hightStartCalendar, hightEndCalendar)) {
                                    //结束时间在高峰期内
                                    highTimeSlotSeconds += getDateSeconds(hightStartCalendar, endParkCalendar);
                                    Log.e(TAG, "caculateParkTime: 结束时间在高峰期内" + highTimeSlotSeconds);
                                    break;
                                } else if (hightStartCalendar.compareTo(endParkCalendar) > 0) {
                                    //结束时间比高峰期早
                                    Log.e(TAG, "caculateParkTime: 结束时间比高峰期早");
                                    break;
                                }
                                highTimeSlotSeconds += getDateSeconds(hightStartCalendar, hightEndCalendar);
                                hightStartCalendar.add(Calendar.DAY_OF_MONTH, 1);
                                hightEndCalendar.add(Calendar.DAY_OF_MONTH, 1);
                            }
                        }
                    }
                } else {
                    hightEndCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    //开始停车时间比高峰期早
                    if (endParkCalendar.compareTo(hightStartCalendar) >= 0) {
                        //停车时间有在高峰期内的
                        if (isIntersection(endParkCalendar, hightStartCalendar, hightEndCalendar)) {
                            //结束停车在高峰期内，2018-10-27 10:00:00
                            highTimeSlotSeconds += getDateSeconds(hightStartCalendar, endParkCalendar);
                        } else {
                            //结束停车时间比高峰期结束时间还晚的
                            highTimeSlotSeconds += calulateHighParkTime(null, hightStartCalendar, hightEndCalendar,
                                    startParkCalendar, endParkCalendar);
                        }
                    }
                }
            }
        }

        parkTime.append(totalTimeSlotSeconds - highTimeSlotSeconds);
        parkTime.append(",");
        parkTime.append(highTimeSlotSeconds);
    }

    private static double calulateHighParkTime(String highDate, Calendar hightStartCalendar, Calendar hightEndCalendar,
                                               Calendar startParkCalendar, Calendar endParkCalendar) {
        long highTime = 0;
        while (true) {
            highTime += getDateSeconds(hightStartCalendar, hightEndCalendar);
            if (highDate != null) {
                //设置为正确的高峰期开始时间
                hightStartCalendar = getSameYearToDayCalendar(startParkCalendar, highDate);
                highDate = null;
            }
            hightStartCalendar.add(Calendar.DAY_OF_MONTH, 1);
            hightEndCalendar.add(Calendar.DAY_OF_MONTH, 1);
            if (isIntersection(endParkCalendar, hightStartCalendar, hightEndCalendar)) {
                //结束时间在高峰期内
                highTime += getDateSeconds(hightStartCalendar, endParkCalendar);
                Log.e(TAG, "caculateParkTime: 结束时间在高峰期内" + highTime);
                break;
            } else if (hightStartCalendar.compareTo(endParkCalendar) > 0) {
                //结束时间比高峰期早
                Log.e(TAG, "caculateParkTime: 结束时间比高峰期早");
                break;
            }
        }
        return highTime;
    }

    /**
     * @return 订单的金额
     */
    public static double calulateParkFee(ParkOrderInfo parkOrderInfo) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String[] parkTime = getParkOrderTime(parkOrderInfo).split(",");
        long lowTime = Long.valueOf(parkTime[0]);
        long highTime = Long.valueOf(parkTime[1]);
        long overtime = Long.valueOf(parkTime[2]);
        double result;

        //不要先转化为xx元/秒,因为如果为30元/小时,停了1小时，计算为60*60*30，最后再除以3600结果为30
        //如果转了则为0.00833333元/秒,这样算会导致结果不准确
        double lowFee = Double.valueOf(parkOrderInfo.getLow_fee());
        double highFee = Double.valueOf(parkOrderInfo.getHigh_fee());
        double overtimeFee = Double.valueOf(parkOrderInfo.getFine());
        result = Double.valueOf(decimalFormat.format(lowTime / 3600.0)) * lowFee +
                Double.valueOf(decimalFormat.format(highTime / 3600.0)) * highFee +
                Double.valueOf(decimalFormat.format(overtime / 3600.0)) * overtimeFee;
        return result;
    }

    /**
     * @param startDate          开始停车时间yyyy-MM-dd HH:mm
     * @param endDate            结束停车时间yyyy-MM-dd HH:mm
     * @param freeTime           前xx分钟免费停车
     * @param insufficientMinute 不足xx分钟按xx分钟算
     * @param timeAndPrice       停车的时段和价格60,10;-1,5
     */
    public static double caculateParkFee(String startDate, String endDate, int freeTime, int insufficientMinute, String timeAndPrice) {
        Calendar startDateCalendar = getYearToMinuteCalendar(startDate);
        Calendar endDateCalendar = getYearToMinuteCalendar(endDate);
        long totalParkMinutes = getCalendarDistance(startDateCalendar, endDateCalendar);
        if (totalParkMinutes <= freeTime) {
            //如果是在免费时长内的则价格为0
            return 0;
        }

        double result = 0;
        if (insufficientMinute > 0) {
            if (totalParkMinutes % insufficientMinute != 0) {
                //按照不足xx分钟当xx分钟算，计算出实际应该算多少分钟
                totalParkMinutes = totalParkMinutes % insufficientMinute * insufficientMinute + insufficientMinute;
            }
        }

        if (timeAndPrice.startsWith("-1")) {
            //全部时间都是同一个价格
            double price = Double.valueOf(timeAndPrice.split(",")[1]);
            result = totalParkMinutes * price / 60;
        } else {
            //60,10;-1,5
            // 总时长为120分钟
            String[] timeWithPrice = timeAndPrice.split(";");
            int lastTime = 0;
            for (String aTimeWithPrice : timeWithPrice) {
                int dotIndex = aTimeWithPrice.indexOf(',');
                int time = Integer.valueOf(aTimeWithPrice.substring(0, dotIndex));
                double price = Double.valueOf(aTimeWithPrice.substring(dotIndex + 1, aTimeWithPrice.length()));
                if (time == -1) {
                    //(120-60)*5/60=5元
                    result += (totalParkMinutes - lastTime) * price / 60;
                    break;
                } else {
                    if (totalParkMinutes > time) {
                        //(60-0)*10/60=10元
                        result += (time - lastTime) * price / 60;
                    } else {
                        result += (totalParkMinutes - lastTime) * price / 60;
                    }
                }
                lastTime = time;
            }

        }

        return result;
    }

    /**
     * @param startDate 开始停车时间，格式为yyyy-MM-dd HH:mm
     * @param endDate   结束停车时间，格式为yyyy-MM-dd HH:mm
     * @param highDate  高峰停车时间，格式为HH:mm - HH:mm
     * @param highFee   高峰时段单价(xx.xx元/小时)
     * @param lowFee    低峰时段单价(xx.xx元/小时)
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
                (getHourMinuteCalendar(highDate.substring(highDate.indexOf(" - ") + 3, highDate.length()))) < 0;
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
     * @param date          参照的那一天，格式为yyyy-MM-dd HH:mm
     * @param hourAndMinute 格式为HH:mm
     * @param nextDays      比参照的那一天往后推迟几天
     */
    private static Calendar getSameYearToDayCalendar(Calendar date, String hourAndMinute, int nextDays) {
        Calendar calendar = (Calendar) date.clone();
        calendar.add(Calendar.DAY_OF_MONTH, nextDays);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hourAndMinute.substring(0, hourAndMinute.indexOf(":"))));
        calendar.set(Calendar.MINUTE, Integer.valueOf(hourAndMinute.substring(hourAndMinute.indexOf(":") + 1, hourAndMinute.length())));
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
     * @param startCalendar 开始时间的日历,格式为yyyy-MM-dd HH:mm
     * @param endCalendar   结束时间的日历,格式为yyyy-MM-dd HH:mm
     * @return 两个时间相差的秒数
     */
    private static int getDateSeconds(Calendar startCalendar, Calendar endCalendar) {
        return (int) ((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000);
    }

    /**
     * @return 返回与originCalendar同年月日但时分为24点00分Calendar
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
     * @return 两个日历相差的分钟数, 不足一分钟的按一分钟算
     */
    public static long getCalendarDistance(Calendar startCalendar, Calendar endCalendar) {
        long second = (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 1000;
        if (second % 60 > 0) {
            return second / 60 + 1;
        }
        return second / 60;
    }

    /**
     * @return 两个日历相差的分钟数
     */
    public static int getCalendarMinuteDistance(Calendar startCalendar, Calendar endCalendar) {
        return (int) ((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / 60000);
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
     * @return 返回停车的时长xx天xx小时xx分钟
     */
    public static String getParkTime(ParkOrderInfo parkOrderInfo) {
        if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrderEndTime()).compareTo(
                DateUtil.getYearToSecondCalendar(parkOrderInfo.getPark_end_time())) < 0) {
            //停车时长超过预约时长
            return DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrderStartTime(), parkOrderInfo.getPark_end_time());
        } else {
            return DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrderStartTime(), parkOrderInfo.getOrderEndTime());
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

    public static String deleteZero(double number) {
        return deleteZero(String.valueOf(number));
    }

    /**
     * 删掉.00
     */
    public static String deleteZero(String number) {
        if (!number.contains(".") || number.charAt(number.length() - 1) != '0') {
            return number;
        }

        int count = 0;
        for (int i = number.length() - 1; i >= 0; i--) {
            if (number.charAt(i) == '0') {
                count++;
            } else {
                break;
            }
        }

        if (number.charAt(number.length() - count - 1) == '.') {
            count++;
        }

        number = number.substring(0, number.length() - count);
        return number;
    }

    /**
     * 删掉09前面的0
     */
    public static String deleteTen(String number) {
        if (number.length() == 2 && number.charAt(0) == '0') {
            return number.substring(1, 2);
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
     * 判断time是否在from，to之内
     *
     * @param thetime   指定日期
     * @param starttime 开始日期
     * @param endtime   结束日期
     */
    public boolean betweenStartAndEnd(String thetime, String starttime, String endtime) {
        SimpleDateFormat dateFormat = getYearToMinutesFormat();
        try {
            Date time = dateFormat.parse(thetime);
            Date start = dateFormat.parse(starttime);
            Date end = dateFormat.parse(endtime);
            return (time.getTime() - start.getTime()) >= 0 && (end.getTime() - time.getTime()) > 0;
        } catch (ParseException e) {
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
     */
    public static boolean isPhoneNumble(String phone_numble) {
        if (phone_numble.length() != 11) {
            return false;
        } else {
            String regex = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";
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
        if (carNumber.length() < 6) {
            return false;
        }
        Pattern pattern = Pattern.compile("^(([\\u4e00-\\u9fa5]{1}[A-Z]{1})[-]?|([wW][Jj][\\u4e00-\\u9fa5]{1}[-]?)|([a-zA-Z]{2}))([A-Za-z0-9]{5}|[DdFf][A-HJ-NP-Za-hj-np-z0-9][0-9]{4}|[0-9]{5}[DdFf])$");
        boolean result = pattern.matcher(carNumber).matches();
        if (!result && carNumber.contains("云A-V")) {
            carNumber = carNumber.replace("云A-V", "云A");
            result = pattern.matcher(carNumber).matches();
        }
        return result;
    }

    /**
     * 验证输入的是否为正确的密码
     *
     * @param password
     * @return
     */
    public boolean isCorrectPassword(String password) {
        if (password != null && !password.equals("")) {
            return password.length() >= 8 && password.length() <= 16;
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
     * 初始化车牌选项卡数据。
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

    public static void initRecentTwoYear(ArrayList<String> mYears, ArrayList<ArrayList<String>> mMonths, ArrayList<ArrayList<ArrayList<String>>> mDays) {
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 2; i++) {
            mYears.add(String.valueOf(calendar.get(Calendar.YEAR) + i));
        }

        ArrayList<String> month;
        ArrayList<String> day;
        ArrayList<ArrayList<String>> monthWithDay;

        month = new ArrayList<>();
        monthWithDay = new ArrayList<>();

        //添加今年的月日
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        for (int i = currentMonth; i <= 12; i++) {
            month.add(String.valueOf(i));
            day = new ArrayList<>();
            if (i == currentMonth) {
                for (int k = calendar.get(Calendar.DAY_OF_MONTH); k <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); k++) {
                    //添加当月剩余的日期
                    day.add(String.valueOf(k));
                }
            } else {
                for (int k = 1; k <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); k++) {
                    day.add(String.valueOf(k));
                }
            }
            monthWithDay.add(day);
            calendar.add(Calendar.MONTH, 1);    //会加到变成下一年
        }
        mMonths.add(month);
        mDays.add(monthWithDay);

        //添加下年的月日
        month = new ArrayList<>();
        monthWithDay = new ArrayList<>();
        calendar.set(Calendar.MONTH, 0);
        for (int j = 1; j <= 12; j++) {
            month.add(String.valueOf(j));
            day = new ArrayList<>();
            for (int k = 1; k <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); k++) {
                day.add(String.valueOf(k));
            }
            monthWithDay.add(day);
            calendar.add(Calendar.MONTH, 1);
        }
        mMonths.add(month);
        mDays.add(monthWithDay);
    }

    public static void initHourWithMinute(ArrayList<String> mHours, ArrayList<ArrayList<String>> mMinutes) {
        ArrayList<String> minutes;
        for (int i = 0; i < 24; i++) {
            mHours.add(String.valueOf(i));

            minutes = new ArrayList<>(60);
            for (int j = 0; j < 60; j++) {
                minutes.add(String.valueOf(j));
            }
            mMinutes.add(minutes);
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

    public static long getActualParkTime(ParkOrderInfo parkOrderInfo) {
        Calendar orderStartCalendar = getYearToSecondCalendar(parkOrderInfo.getOrderStartTime());
        Calendar orderEndCalendar = getYearToSecondCalendar(parkOrderInfo.getOrderEndTime());
        Calendar parkStartCalendar = getYearToSecondCalendar(parkOrderInfo.getPark_start_time());
        Calendar parkEndCalendar = getYearToSecondCalendar(parkOrderInfo.getPark_end_time());
        Calendar orderExtendEndCalendar = getYearToSecondCalendar(parkOrderInfo.getOrderEndTime());
        orderExtendEndCalendar.add(Calendar.SECOND, parkOrderInfo.getExtensionTime().equals("-1") ? 0 : Integer.valueOf(parkOrderInfo.getExtensionTime()));

        long parkDuration;
        if (orderStartCalendar.compareTo(parkStartCalendar) > 0) {
            //提前停车
            if (orderExtendEndCalendar.compareTo(parkEndCalendar) < 0) {
                //超时停车
                parkDuration = getCalendarDistance(parkStartCalendar, parkEndCalendar);
            } else if (orderEndCalendar.compareTo(parkEndCalendar) < 0) {
                //还没到预约的结束时间，提前结束停车
                if (orderStartCalendar.compareTo(parkEndCalendar) > 0) {
                    //还没到预约开始停车时间就跑了的
                    parkDuration = getCalendarDistance(parkStartCalendar, parkEndCalendar);
                } else {
                    if (getCalendarDistance(parkEndCalendar, orderEndCalendar) >= getCalendarDistance(orderStartCalendar, orderEndCalendar) / 5.0) {
                        //过早离开（剩余停车时长大于总预约时长的1/5）
                        parkDuration = getCalendarDistance(parkStartCalendar, parkEndCalendar);
                    } else {
                        parkDuration = getCalendarDistance(parkStartCalendar, parkEndCalendar);
                    }
                }
            } else {
                //在顺延时长内结束停车
                parkDuration = getCalendarDistance(parkStartCalendar, parkEndCalendar);
            }
        } else {
            //在预约开始停车后开始停车
            if (orderExtendEndCalendar.compareTo(parkEndCalendar) < 0) {
                //超时停车
                parkDuration = getCalendarDistance(orderStartCalendar, parkEndCalendar);
            } else if (orderEndCalendar.compareTo(parkEndCalendar) < 0) {
                //还没到预约的结束时间，提前结束停车
                if (orderStartCalendar.compareTo(parkEndCalendar) > 0) {
                    //还没到预约开始停车时间就跑了的
                    parkDuration = getCalendarDistance(orderStartCalendar, parkEndCalendar);
                } else {
                    //在预约开始停车后再结束停车的
                    if (getCalendarDistance(parkEndCalendar, orderEndCalendar) >= getCalendarDistance(orderStartCalendar, orderEndCalendar) / 5.0) {
                        //过早离开（剩余停车时长大于总预约时长的1/5）
                        parkDuration = getCalendarDistance(orderStartCalendar, parkEndCalendar);
                    } else {
                        parkDuration = getCalendarDistance(orderStartCalendar, parkEndCalendar);
                    }
                }
            } else {
                //在顺延时长内结束停车
                parkDuration = getCalendarDistance(orderStartCalendar, parkEndCalendar);
            }
        }
        return parkDuration;
    }

    private static class TimeAndPriceHolder {

        private int minute;

        private double price;

        public TimeAndPriceHolder(int minute, double price) {
            this.minute = minute;
            this.price = price;
        }

    }

}
