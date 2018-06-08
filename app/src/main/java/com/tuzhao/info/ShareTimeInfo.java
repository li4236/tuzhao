package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by juncoder on 2018/4/11.
 */

public class ShareTimeInfo extends BaseInfo {

    //车位id
    private String parkSpaceId;

    //共享日期（2018-4-11 - 2018-4-12）  注：发送一个String包含两个时间段，用“ - ”隔开
    private String shareDate;

    //每周共享的时间，（11,12,03,14,05,06,17）  注：总共7天，用“，”隔开，第一位数字为1代表该天是共享的，为0代表不共享。例子为周一二四七为共享
    private String shareDay;

    //暂停共享日期（2018-4-12,2018-4-16）  注：可有多个时间段，用“，”隔开
    private String pauseShareDate;

    //每日共享时段（10:00 - 11:00,19:00 - 23:00）  注：表示从10点到11点为共享时间，最多有三个时间段，如果没传则默认全天共享
    private String everyDayShareTime;

    //用户预约的时间(2018-04-19 17:00*2018-04-19 19:00,2018-04-21 08:00*2018-04-22 05:00)
    private String orderTime;

    public String getParkSpaceId() {
        return parkSpaceId;
    }

    public void setParkSpaceId(String parkSpaceId) {
        this.parkSpaceId = parkSpaceId;
    }

    public String getShareDate() {
        return shareDate;
    }

    public void setShareDate(String shareDate) {
        this.shareDate = shareDate;
    }

    public String getShareDay() {
        return shareDay;
    }

    public void setShareDay(String shareDay) {
        this.shareDay = shareDay;
    }

    public String getPauseShareDate() {
        return pauseShareDate;
    }

    public void setPauseShareDate(String pauseShareDate) {
        this.pauseShareDate = pauseShareDate;
    }

    public String getEveryDayShareTime() {
        return everyDayShareTime;
    }

    public void setEveryDayShareTime(String everyDayShareTime) {
        this.everyDayShareTime = everyDayShareTime;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    @Override
    public String toString() {
        return "ShareTimeInfo{" +
                "parkSpaceId='" + parkSpaceId + '\'' +
                ", shareDate='" + shareDate + '\'' +
                ", shareDay='" + shareDay + '\'' +
                ", pauseShareDate='" + pauseShareDate + '\'' +
                ", everyDayShareTime='" + everyDayShareTime + '\'' +
                ", orderTime='" + orderTime + '\'' +
                '}';
    }
}
