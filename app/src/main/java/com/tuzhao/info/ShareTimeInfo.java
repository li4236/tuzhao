package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by juncoder on 2018/4/11.
 */

public class ShareTimeInfo extends BaseInfo implements Parcelable {

    //车位id
    private String parkSpaceId;

    //共享日期（2018-4-11 - 2018-4-12）  注：发送一个String包含两个时间段，用“ - ”隔开
    private String shareDate;

    //每周共享的时间，（1,1,0,1,0,0,1）  注：总共7天，用“，”隔开，第一位数字为1代表该天是共享的，为0代表不共享。例子为周一二四七为共享
    private String shareDay;

    //暂停共享日期（2018-4-12,2018-4-16）  注：可有多个时间段，用“，”隔开
    private String pauseShareDate;

    //每日共享时段（10:00 - 11:00,19:00 - 23:00）  注：表示从10点到11点为共享时间，最多有三个时间段，如果没传则默认全天共享
    private String everyDayShareTime;

    private boolean isHourRent;

    private String appointmentDate;

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

    public boolean isHourRent() {
        return isHourRent;
    }

    public void setHourRent(boolean hourRent) {
        isHourRent = hourRent;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    @Override
    public String toString() {
        return "ShareTimeInfo{" +
                "parkSpaceId='" + parkSpaceId + '\'' +
                ", shareDate='" + shareDate + '\'' +
                ", shareDay='" + shareDay + '\'' +
                ", pauseShareDate='" + pauseShareDate + '\'' +
                ", everyDayShareTime='" + everyDayShareTime + '\'' +
                ", isHourRent=" + isHourRent +
                ", appointmentDate='" + appointmentDate + '\'' +
                ", orderTime='" + orderTime + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.parkSpaceId);
        dest.writeString(this.shareDate);
        dest.writeString(this.shareDay);
        dest.writeString(this.pauseShareDate);
        dest.writeString(this.everyDayShareTime);
        dest.writeByte(this.isHourRent ? (byte) 1 : (byte) 0);
        dest.writeString(this.appointmentDate);
        dest.writeString(this.orderTime);
    }

    public ShareTimeInfo() {
    }

    protected ShareTimeInfo(Parcel in) {
        this.parkSpaceId = in.readString();
        this.shareDate = in.readString();
        this.shareDay = in.readString();
        this.pauseShareDate = in.readString();
        this.everyDayShareTime = in.readString();
        this.isHourRent = in.readByte() != 0;
        this.appointmentDate = in.readString();
        this.orderTime = in.readString();
    }

    public static final Parcelable.Creator<ShareTimeInfo> CREATOR = new Parcelable.Creator<ShareTimeInfo>() {
        @Override
        public ShareTimeInfo createFromParcel(Parcel source) {
            return new ShareTimeInfo(source);
        }

        @Override
        public ShareTimeInfo[] newArray(int size) {
            return new ShareTimeInfo[size];
        }
    };
}
