package com.tuzhao.utils;

import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.tuzhao.info.Park_Info;
import com.tuzhao.publicmanager.UserManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * @param canParkList 最后可停的车位
     * @param parkSpaces  全部车位
     * @param startTime   yyyy-MM-dd HH:mm
     * @param endTime     yyyy-MM-dd HH:mm
     */
    public static void findCanParkList(List<Park_Info> canParkList, List<Park_Info> parkSpaces, String startTime, String endTime) {
        Log.e("TAG", "startDate: " + startTime + "  endDate:" + endTime);
        canParkList.clear();
        canParkList.addAll(parkSpaces);

        int maxExtentionMinute;
        for (Park_Info parkInfo : parkSpaces) {
            Log.e("TAG", "scanPark parkInfo:" + parkInfo);
            if (!parkInfo.getPark_status().equals("2")) {
                canParkList.remove(parkInfo);
                continue;
            }

            int currentExtentionMinute;
            //排除不在共享日期之内的(根据共享日期)
            if ((currentExtentionMinute = DateUtil.isInShareDate(startTime, endTime, parkInfo.getOpen_date())) == -1) {
                canParkList.remove(parkInfo);
                Log.e(TAG, "scanPark: notInShareDate");
                continue;
            } else {
                maxExtentionMinute = currentExtentionMinute;
            }

            //排除暂停时间在预定时间内的(根据暂停日期)
            if ((currentExtentionMinute = DateUtil.isInPauseDate(startTime, endTime, parkInfo.getPauseShareDate())) == -1) {
                canParkList.remove(parkInfo);
                Log.e(TAG, "scanPark: inPauseDate");
                continue;
            } else {
                maxExtentionMinute = Math.min(maxExtentionMinute, currentExtentionMinute);
            }

            //排除预定时间当天不共享的(根据共享星期)
            if ((currentExtentionMinute = DateUtil.isInShareDay(startTime, endTime, parkInfo.getShareDay())) == -1) {
                canParkList.remove(parkInfo);
                Log.e("TAG", "scanPark: notInShareDay");
                continue;
            } else {
                maxExtentionMinute = Math.min(maxExtentionMinute, currentExtentionMinute);
            }

            //排除该时间段被别人预约过的(根据车位的被预约时间)
            if ((currentExtentionMinute = DateUtil.isInOrderDate(startTime, endTime, parkInfo.getOrder_times())) == -1) {
                canParkList.remove(parkInfo);
                Log.e(TAG, "scanPark: isInOrderDate");
                continue;
            } else {
                maxExtentionMinute = Math.min(maxExtentionMinute, currentExtentionMinute);
            }

            //排除不在共享时间段内的(根据共享的时间段)
            if ((currentExtentionMinute = DateUtil.isInShareTime(startTime, endTime, parkInfo.getOpen_time())) == -1) {
                canParkList.remove(parkInfo);
                Log.e("TAG", "isNotInShareTime: " + parkInfo.getOpen_time());
            } else {
                //获取车位可共享的时间差
                int position = canParkList.indexOf(parkInfo);
                canParkList.set(position, parkInfo);
                parkInfo.setMaxExtensionMinute(Math.min(maxExtentionMinute, currentExtentionMinute));
            }
        }

        if (!canParkList.isEmpty()) {
            sortParkListByExtensionTimeWithIndicator(canParkList);
        }

    }

    /**
     * 根据车位最大可顺延时长和指标进行排序
     */
    private static void sortParkListByExtensionTimeWithIndicator(List<Park_Info> canParkList) {
        final int extensionTime = UserManager.getInstance().getUserInfo().getLeave_time();
        Collections.sort(canParkList, new Comparator<Park_Info>() {
            @Override
            public int compare(Park_Info o1, Park_Info o2) {
                int result;
                if (o1.getMaxExtensionMinute() >= extensionTime && o2.getMaxExtensionMinute() >= extensionTime) {
                    //如果都满足用户的顺延时长则按照车位指标从小到大排序
                    result = Integer.valueOf(o1.getIndicator()) - Integer.valueOf(o2.getIndicator());
                } else if (o1.getMaxExtensionMinute() >= extensionTime && o2.getMaxExtensionMinute() < extensionTime) {
                    //如果第一个车位满足用户的顺延时长，第二个车位不满足，则第一个车位排在前面
                    result = -1;
                } else if (o1.getMaxExtensionMinute() < extensionTime && o2.getMaxExtensionMinute() >= extensionTime) {
                    //如果第二个车位用户的满足顺延时长，第一个车位不满足，则第二个车位排在前面
                    result = 1;
                } else {
                    //两个车位都不满足,则车位最大可顺延时长大的排在前面
                    result = -(o1.getMaxExtensionMinute() - o2.getMaxExtensionMinute());
                    if (result == 0) {
                        //如果车位的最大可顺延时长一样，则根据车位指标排序，指标小的排在前面
                        result = Integer.valueOf(o1.getIndicator()) - Integer.valueOf(o2.getIndicator());
                    }
                }
                return result;
            }
        });
    }

    /**
     * @param canParkList 最后可停的车位
     * @param parkSpaces  全部车位
     * @param startTime   yyyy-MM-dd HH:mm
     * @param endTime     yyyy-MM-dd HH:mm
     */
    public static void findCanLongParkList(List<Park_Info> canParkList, List<Park_Info> parkSpaces, String startTime, String endTime) {
        Log.e("TAG", "startDate: " + startTime + "  endDate:" + endTime);
        canParkList.clear();
        canParkList.addAll(parkSpaces);

        for (Park_Info parkInfo : parkSpaces) {
            Log.e("TAG", "scanPark parkInfo:" + parkInfo);
            if (!parkInfo.getPark_status().equals("2")) {
                canParkList.remove(parkInfo);
                continue;
            }

            //排除不在共享日期之内的(根据共享日期)
            if (DateUtil.notInShareDate(startTime, endTime, parkInfo.getOpen_date())) {
                canParkList.remove(parkInfo);
                Log.e(TAG, "scanPark: notInShareDate");
                continue;
            }

            //排除不在共享时间段内的(根据共享的时间段),因为长租是一天起的，所以车位必须全天开放才行
            if (!"-1".equals(parkInfo.getOpen_time())) {
                canParkList.remove(parkInfo);
                Log.e("TAG", "Open_time: " + parkInfo.getOpen_time());
                continue;
            }

            //排除不是每天共享的(根据共享星期)
            if (DateUtil.isNotInShareDay(startTime, endTime, parkInfo.getShareDay())) {
                canParkList.remove(parkInfo);
                Log.e("TAG", "scanPark: notInShareDay");
                continue;
            }

            //排除暂停时间在预定时间内的(根据暂停日期)
            if (DateUtil.isInParkSpacePauseDate(startTime, endTime, parkInfo.getPauseShareDate())) {
                canParkList.remove(parkInfo);
                Log.e(TAG, "scanPark: inPauseDate");
                continue;
            }

            //排除该时间段被别人预约过的(根据车位的被预约时间)
            if (!DateUtil.isNotInOrderDate(startTime, endTime, parkInfo.getOrder_times())) {
                canParkList.remove(parkInfo);
                Log.e(TAG, "scanPark: isInOrderDate");
            }

        }

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
