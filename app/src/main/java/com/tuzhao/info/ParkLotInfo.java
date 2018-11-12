package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by TZL12 on 2017/4/29.
 * 停车场数据实体
 */

public class ParkLotInfo implements Parcelable {

    private String id;//停车场id
    private double latitude;//经度
    private double longitude;//纬度
    private String park_space_name;//停车场名称
    private String park_address;//停车场地址
    private String grade;//评分
    private String opentime;//节假日开放时间
    private String parkspace_img;//小区的图片地址
    private String high_time;//高峰时段
    private String low_time;//低峰时段
    private String high_fee;//高峰时段单价
    private String low_fee;//低峰时段单价
    private String high_max_fee;//高峰时段封顶价格。-1代表不封顶
    private String low_max_fee;//低峰时段封顶价格。-1代表不封顶
    private String fine;//罚金：滞留金
    private String ad_img;//广告图片地址
    private String ad_web;//广告页面地址
    private String city_code;//城市码
    private String profit_ratio;//收益比
    private String isCollection;//是否收藏了该车场（0：未收藏，1：收藏了）
    private String longRentInfo;//长租的信息，由天数和折扣组成(1,1;30,1;60,0.9)代表1天和30天都是不打折，60天的打9折。
    private ArrayList<LongRentInfo> longRentInfos;
    private String longRentPrice = "-1";//长租的价格,-1代表不支持长租

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getParkLotName() {
        return park_space_name;
    }

    public void setPark_space_name(String park_space_name) {
        this.park_space_name = park_space_name;
    }

    public String getPark_address() {
        return park_address;
    }

    public void setPark_address(String park_address) {
        this.park_address = park_address;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getHigh_time() {
        return high_time;
    }

    public void setHigh_time(String high_time) {
        this.high_time = high_time;
    }

    public String getLow_time() {
        return low_time;
    }

    public void setLow_time(String low_time) {
        this.low_time = low_time;
    }

    public String getHigh_fee() {
        return high_fee;
    }

    public void setHigh_fee(String high_fee) {
        this.high_fee = high_fee;
    }

    public String getLow_fee() {
        return low_fee;
    }

    public void setLow_fee(String low_fee) {
        this.low_fee = low_fee;
    }

    public String getHigh_max_fee() {
        return high_max_fee;
    }

    public void setHigh_max_fee(String high_max_fee) {
        this.high_max_fee = high_max_fee;
    }

    public String getLow_max_fee() {
        return low_max_fee;
    }

    public void setLow_max_fee(String low_max_fee) {
        this.low_max_fee = low_max_fee;
    }

    public String getFine() {
        return fine;
    }

    public void setFine(String fine) {
        this.fine = fine;
    }

    public String getAd_img() {
        return ad_img;
    }

    public void setAd_img(String ad_img) {
        this.ad_img = ad_img;
    }

    public String getAd_web() {
        return ad_web;
    }

    public void setAd_web(String ad_web) {
        this.ad_web = ad_web;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public String getParkspace_img() {
        return parkspace_img;
    }

    public void setParkspace_img(String parkspace_img) {
        this.parkspace_img = parkspace_img;
    }

    public String getProfit_ratio() {
        return profit_ratio;
    }

    public void setProfit_ratio(String profit_ratio) {
        this.profit_ratio = profit_ratio;
    }

    public boolean isCollection() {
        return "1".equals(isCollection);
    }

    public void setIsCollection(String isCollection) {
        this.isCollection = isCollection;
    }

    /**
     * 将长租的字符串信息转化为List
     */
    public void convertLongRentInfo() {
        if (!"-1".equals(longRentPrice) && longRentInfos == null) {
            String[] infos = longRentInfo.split(";");
            longRentInfos = new ArrayList<>(infos.length);
            String[] prices = longRentPrice.split(";");
            for (int i = 0; i < infos.length; i++) {
                String[] longRent = infos[i].split(",");
                LongRentInfo longRentInfo = new LongRentInfo();
                longRentInfo.setRentDay(Integer.valueOf(longRent[0]));
                longRentInfo.setDiscount(Double.valueOf(longRent[1]));
                longRentInfo.setNormalPrice(Double.valueOf(prices[i]));
                longRentInfos.add(longRentInfo);
            }
        }
    }

    public String getLongRentPrice() {
        return longRentPrice;
    }

    public ArrayList<LongRentInfo> getLongRentInfos() {
        return longRentInfos;
    }

    public void setLongRentInfos(ArrayList<LongRentInfo> longRentInfos) {
        this.longRentInfos = longRentInfos;
    }

    @Override
    public String toString() {
        return "ParkLotInfo{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", park_space_name='" + park_space_name + '\'' +
                ", park_address='" + park_address + '\'' +
                ", grade='" + grade + '\'' +
                ", opentime='" + opentime + '\'' +
                ", parkspace_img='" + parkspace_img + '\'' +
                ", high_time='" + high_time + '\'' +
                ", low_time='" + low_time + '\'' +
                ", high_fee='" + high_fee + '\'' +
                ", low_fee='" + low_fee + '\'' +
                ", high_max_fee='" + high_max_fee + '\'' +
                ", low_max_fee='" + low_max_fee + '\'' +
                ", fine='" + fine + '\'' +
                ", ad_img='" + ad_img + '\'' +
                ", ad_web='" + ad_web + '\'' +
                ", city_code='" + city_code + '\'' +
                ", profit_ratio='" + profit_ratio + '\'' +
                ", isCollection='" + isCollection + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.park_space_name);
        dest.writeString(this.park_address);
        dest.writeString(this.grade);
        dest.writeString(this.opentime);
        dest.writeString(this.parkspace_img);
        dest.writeString(this.high_time);
        dest.writeString(this.low_time);
        dest.writeString(this.high_fee);
        dest.writeString(this.low_fee);
        dest.writeString(this.high_max_fee);
        dest.writeString(this.low_max_fee);
        dest.writeString(this.fine);
        dest.writeString(this.ad_img);
        dest.writeString(this.ad_web);
        dest.writeString(this.city_code);
        dest.writeString(this.profit_ratio);
        dest.writeString(this.isCollection);
        dest.writeTypedList(this.longRentInfos);
    }

    public ParkLotInfo() {
    }

    protected ParkLotInfo(Parcel in) {
        this.id = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.park_space_name = in.readString();
        this.park_address = in.readString();
        this.grade = in.readString();
        this.opentime = in.readString();
        this.parkspace_img = in.readString();
        this.high_time = in.readString();
        this.low_time = in.readString();
        this.high_fee = in.readString();
        this.low_fee = in.readString();
        this.high_max_fee = in.readString();
        this.low_max_fee = in.readString();
        this.fine = in.readString();
        this.ad_img = in.readString();
        this.ad_web = in.readString();
        this.city_code = in.readString();
        this.profit_ratio = in.readString();
        this.isCollection = in.readString();
        this.longRentInfos = in.createTypedArrayList(LongRentInfo.CREATOR);
    }

    public static final Creator<ParkLotInfo> CREATOR = new Creator<ParkLotInfo>() {
        @Override
        public ParkLotInfo createFromParcel(Parcel source) {
            return new ParkLotInfo(source);
        }

        @Override
        public ParkLotInfo[] newArray(int size) {
            return new ParkLotInfo[size];
        }
    };

}
