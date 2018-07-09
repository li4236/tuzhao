package com.tuzhao.utils;

import android.util.Log;

import com.tuzhao.info.Park_Info;
import com.tuzhao.publicmanager.UserManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

        //停车时间加上宽限时长
        final Calendar mCanParkEndCalendar = DateUtil.getYearToMinuteCalendar(endTime);
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

}
