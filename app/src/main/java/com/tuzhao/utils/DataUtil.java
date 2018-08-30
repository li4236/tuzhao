package com.tuzhao.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.tuzhao.info.Park_Info;
import com.tuzhao.publicmanager.UserManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Created by juncoder on 2018/7/9.
 */
public class DataUtil {

    private static final String TAG = "DataUtil";

    public static void findCanParkList(List<Park_Info> canParkList, List<Park_Info> parkSpaces, String startTime, String endTime) {
        Log.e("TAG", "startDate: " + startTime + "  endDate:" + endTime);
        canParkList.clear();
        canParkList.addAll(parkSpaces);

        Calendar[] shareTimeCalendar;
        int status;
        for (Park_Info parkInfo : parkSpaces) {
            Log.e("TAG", "scanPark parkInfo:" + parkInfo);
            if (!parkInfo.getPark_status().equals("2")) {
                continue;
            }

            int currentStatus;
            //排除不在共享日期之内的(根据共享日期)
            if ((currentStatus = DateUtil.isInShareDate(startTime, endTime, parkInfo.getOpen_date())) == 0) {
                canParkList.remove(parkInfo);
                Log.e(TAG, "scanPark: notInShareDate");
                continue;
            } else {
                status = currentStatus;
            }

            //排除暂停时间在预定时间内的(根据暂停日期)
            if ((currentStatus = DateUtil.isInPauseDate(startTime, endTime, parkInfo.getPauseShareDate())) == 0) {
                canParkList.remove(parkInfo);
                Log.e(TAG, "scanPark: inPauseDate");
                continue;
            } else {
                if (status != 1) {
                    status = currentStatus;
                }
            }

            //排除预定时间当天不共享的(根据共享星期)
            if ((currentStatus = DateUtil.isInShareDay(startTime, endTime, parkInfo.getShareDay())) == 0) {
                canParkList.remove(parkInfo);
                Log.e("TAG", "scanPark: notInShareDay");
                continue;
            } else {
                if (status != 1) {
                    status = currentStatus;
                }
            }

            //排除该时间段被别人预约过的(根据车位的被预约时间)
            if (DateUtil.isInOrderDate(startTime, endTime, parkInfo.getOrder_times())) {
                canParkList.remove(parkInfo);
                Log.e(TAG, "scanPark: isInOrderDate");
                continue;
            }

            Log.e("TAG", "Open_time: " + parkInfo.getOpen_time());
            //排除不在共享时间段内的(根据共享的时间段)
            if ((shareTimeCalendar = DateUtil.isInShareTime(startTime, endTime, parkInfo.getOpen_time(), status == 1)) != null) {
                //获取车位可共享的时间差
                Log.e("TAG", "shareTimeDistance: " + DateUtil.getCalendarMonthToMinute(shareTimeCalendar[0]) + "  end:" + DateUtil.getCalendarMonthToMinute(shareTimeCalendar[1]));
                int position = canParkList.indexOf(parkInfo);
                parkInfo.setShareTimeCalendar(shareTimeCalendar);
                canParkList.set(position, parkInfo);
            } else {
                canParkList.remove(parkInfo);
            }
        }

        sortCanParkListByShareTime(canParkList, endTime);
    }

    public static void sortCanParkListByShareTime(List<Park_Info> canParkList, String parkEndTime) {
        //停车时间加上宽限时长
        final Calendar mCanParkEndCalendar = DateUtil.getYearToMinuteCalendar(parkEndTime);
        mCanParkEndCalendar.add(Calendar.MINUTE, UserManager.getInstance().getUserInfo().getLeave_time());

        Collections.sort(canParkList, new Comparator<Park_Info>() {
            @Override
            public int compare(Park_Info o1, Park_Info o2) {
                long result;

                if ((o1.getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis()) >= 0
                        && (o2.getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis()) >= 0) {
                    //如果两个车位的共享结束时段都比预约停车时间加上宽限时间长，则按照车位的可共享时段的大小从小到大排序
                    result = DateUtil.getCalendarDistance(o1.getShareTimeCalendar()[0], o1.getShareTimeCalendar()[1]) -
                            DateUtil.getCalendarDistance(o2.getShareTimeCalendar()[0], o2.getShareTimeCalendar()[1]);
                } else if ((o1.getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis()) > 0) {
                    //如果第一个车位的共享结束时段都比预约停车时间加上宽限时间长，第二个不是，则第一个排前面
                    result = -1;
                } else if ((o2.getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis()) > 0) {
                    //如果第二个车位的共享结束时段都比预约停车时间加上宽限时间长，第二个不是，则第二个排前面
                    result = -1;
                } else {
                    //如果两个车位的共享结束时段都不比预约停车时间加上宽限时间长，则停车的宽限时长大的排前面
                    result = (o2.getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis())
                            - (o1.getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis());
                }
                return (int) result;
            }
        });
    }

    public static void sortCanParkByIndicator(List<Park_Info> canParkInfo, String endTime) {
        if (canParkInfo.size() <= 1) {
            return;
        }

        //停车时间加上宽限时长
        final Calendar mCanParkEndCalendar = DateUtil.getYearToMinuteCalendar(endTime);
        mCanParkEndCalendar.add(Calendar.MINUTE, UserManager.getInstance().getUserInfo().getLeave_time());

        if (canParkInfo.get(1).getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis() <= 0) {
            //如果第二个的共享时间没有比停车时间加上宽限时间更长则不用比较了
            return;
        }

        if (canParkInfo.size() == 2 ||
                (canParkInfo.size() >= 3 && canParkInfo.get(2).getShareTimeCalendar()[1].getTimeInMillis() - mCanParkEndCalendar.getTimeInMillis() <= 0)) {
            //如果只有两个预选车位或者预选车位大于三个，但是第三个的共享时间没有比停车时间加上宽限时间更长
            Park_Info parkInfoOne = canParkInfo.get(0);
            Park_Info parkInfoTwo = canParkInfo.get(1);
            if (Integer.valueOf(parkInfoOne.getIndicator()) > Integer.valueOf(parkInfoTwo.getIndicator())) {
                canParkInfo.set(0, parkInfoTwo);
                canParkInfo.set(1, parkInfoOne);
            }
        } else if (canParkInfo.size() >= 3) {

            //预选车位大于等于三个则只需要比较前三个
            List<Park_Info> list = new ArrayList<>(3);
            for (int i = 0; i < 3; i++) {
                list.add(canParkInfo.get(i));
            }

            //把前三个预选车位按照指标从小到大排序
            Collections.sort(list, new Comparator<Park_Info>() {
                @Override
                public int compare(Park_Info o1, Park_Info o2) {
                    return Integer.valueOf(o1.getIndicator()) - Integer.valueOf(o2.getIndicator());
                }
            });

            for (int i = 0; i < 3; i++) {
                canParkInfo.set(i, list.get(i));
            }
        }

    }

    /**
     * @return 在text前面加上两个字的空位
     */
    public static SpannableString getFirstTwoTransparentSpannable(String text) {
        SpannableString spannableString = new SpannableString("月卡" + text);
        spannableString.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * @return 数字是否合法
     */
    public static boolean numberLegal(String number) {
        String regex = "^(([1-9][0-9]*)|(([0]\\.\\d{1,2}|[1-9][0-9]*\\.\\d{1,2})))$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(number);
        return m.matches();
    }

    /**
     * @return true:密码是由大于8位的字符组成，并且至少包含一个数字和字母
     */
    public static boolean passwordLegal(String password) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public static void initLicensePlateAttribution(ArrayList<String> city, ArrayList<ArrayList<String>> letter) {
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        city.add("京");
        ArrayList<String> cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("E")) {
                break;
            }
        }
        cityWithLetter.add("J");
        cityWithLetter.add("K");
        cityWithLetter.add("L");
        cityWithLetter.add("M");
        cityWithLetter.add("Y");
        letter.add(cityWithLetter);

        city.add("泸");
        cityWithLetter = new ArrayList<>();
        cityWithLetter.add("A");
        cityWithLetter.add("B");
        cityWithLetter.add("C");
        cityWithLetter.add("D");
        cityWithLetter.add("R");
        letter.add(cityWithLetter);

        city.add("津");
        cityWithLetter = new ArrayList<>();
        cityWithLetter.add("A");
        cityWithLetter.add("B");
        cityWithLetter.add("C");
        cityWithLetter.add("D");
        cityWithLetter.add("E");
        letter.add(cityWithLetter);

        city.add("渝");
        cityWithLetter = new ArrayList<>();
        cityWithLetter.add("A");
        cityWithLetter.add("B");
        cityWithLetter.add("C");
        cityWithLetter.add("F");
        cityWithLetter.add("G");
        cityWithLetter.add("H");
        letter.add(cityWithLetter);

        city.add("冀");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("J")) {
                break;
            }
        }
        cityWithLetter.add("R");
        cityWithLetter.add("S");
        cityWithLetter.add("T");
        letter.add(cityWithLetter);

        city.add("豫");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("S")) {
                break;
            }
        }
        cityWithLetter.add("U");
        letter.add(cityWithLetter);

        city.add("云");
        cityWithLetter = new ArrayList<>();
        cityWithLetter.add("A");
        cityWithLetter.add("A-V");
        cityWithLetter.add("C");
        cityWithLetter.add("D");
        cityWithLetter.add("E");
        cityWithLetter.add("F");
        cityWithLetter.add("G");
        cityWithLetter.add("H");
        cityWithLetter.add("J");
        cityWithLetter.add("K");
        cityWithLetter.add("L");
        cityWithLetter.add("M");
        cityWithLetter.add("N");
        cityWithLetter.add("P");
        cityWithLetter.add("Q");
        cityWithLetter.add("R");
        cityWithLetter.add("S");
        letter.add(cityWithLetter);

        city.add("辽");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("P")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("黑");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("P")) {
                break;
            }
        }
        cityWithLetter.add("R");
        letter.add(cityWithLetter);

        city.add("湘");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("N")) {
                break;
            }
        }
        cityWithLetter.add("U");
        letter.add(cityWithLetter);

        city.add("皖");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("S")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("鲁");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("S")) {
                break;
            }
        }
        cityWithLetter.add("U");
        cityWithLetter.add("V");
        cityWithLetter.add("Y");
        letter.add(cityWithLetter);

        city.add("新");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("R")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("苏");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("N")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("浙");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("L")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("赣");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("M")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("鄂");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("S")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("桂");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("P")) {
                break;
            }
        }
        cityWithLetter.add("R");
        letter.add(cityWithLetter);

        city.add("甘");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("P")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("晋");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            if (string.equals("G")) {
                continue;
            }
            cityWithLetter.add(string);
            if (string.equals("M")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("蒙");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("M")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("陕");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("K")) {
                break;
            }
        }
        cityWithLetter.add("V");
        letter.add(cityWithLetter);

        city.add("吉");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("K")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("闽");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("K")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("贵");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("J")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("粤");
        letter.add(new ArrayList<>(Arrays.asList(letters)));

        city.add("川");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            if (string.equals("N") || string.contains("P")) {
                continue;
            }
            cityWithLetter.add(string);
        }
        letter.add(cityWithLetter);

        city.add("青");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("H")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("藏");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("J")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("琼");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("E")) {
                break;
            }
        }
        letter.add(cityWithLetter);

        city.add("宁");
        cityWithLetter = new ArrayList<>();
        for (String string : letters) {
            cityWithLetter.add(string);
            if (string.equals("E")) {
                break;
            }
        }
        letter.add(cityWithLetter);
    }

    /**
     * @return string是否包含小写字母
     */
    public static boolean containLowerCase(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.isLowerCase(string.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    /**
     * 对double类型数据保留小数点后多少位
     * 高德地图转码返回的就是 小数点后6位，为了统一封装一下
     *
     * @param digit 位数
     * @param in    输入
     * @return 保留小数位后的数
     */
    static double dataDigit(int digit, double in) {
        return new BigDecimal(in).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();

    }

    /**
     * 将火星坐标转变成百度坐标
     *
     * @param lngLat_gd 火星坐标（高德、腾讯地图坐标等）
     * @return 百度坐标
     */

    public static LatLng bd_encrypt(LatLng lngLat_gd) {
        double x = lngLat_gd.longitude, y = lngLat_gd.latitude;
        double z = sqrt(x * x + y * y) + 0.00002 * sin(y * x_pi);
        double theta = atan2(y, x) + 0.000003 * cos(x * x_pi);
        return new LatLng(dataDigit(6, z * cos(theta) + 0.0065), dataDigit(6, z * sin(theta) + 0.006));
    }

    /**
     * 将百度坐标转变成火星坐标
     *
     * @param lngLat_bd 百度坐标（百度地图坐标）
     * @return 火星坐标(高德 、 腾讯地图等)
     */
    static LatLng bd_decrypt(LatLng lngLat_bd) {
        double x = lngLat_bd.longitude - 0.0065, y = lngLat_bd.latitude - 0.006;
        double z = sqrt(x * x + y * y) - 0.00002 * sin(y * x_pi);
        double theta = atan2(y, x) - 0.000003 * cos(x * x_pi);
        return new LatLng(dataDigit(6, z * cos(theta)), dataDigit(6, z * sin(theta)));
    }

}
