package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by TZL12 on 2017/4/29.
 * 停车场数据实体
 */

public class Park_Space_Info extends BaseInfo {

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

    public String getPark_space_name() {
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

    @Override
    public String toString() {
        return "Park_Space_Info{" +
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
                '}';
    }
}
