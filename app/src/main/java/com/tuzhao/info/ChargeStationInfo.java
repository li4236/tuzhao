package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by TZL12 on 2017/4/29.
 * 充电站数据实体
 */

public class ChargeStationInfo extends BaseInfo {

    private String id;//充电桩id
    private double latitude;//经度
    private double longitude;//纬度
    private String charge_station_name;//充电站名称
    private String ispublic;//是否是公桩
    private String charge_station_address;//充电站地址
    private String grade;//评分
    private String charge_fee;//充电费：xx元/度
    private String service_fee;//服务费：xx元/度
    private String park_fee;//停车费
    private String opentime;//开放时间
    private String img_url;//充电桩图片
    private String ad_img;//广告图片地址
    private String ad_web;//广告页面地址
    private String city_code;//城市码

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

    public String getCharge_station_name() {
        return charge_station_name;
    }

    public void setCharge_station_name(String charge_station_name) {
        this.charge_station_name = charge_station_name;
    }

    public String getIspublic() {
        return ispublic;
    }

    public void setIspublic(String ispublic) {
        this.ispublic = ispublic;
    }

    public String getCharge_station_address() {
        return charge_station_address;
    }

    public void setCharge_station_address(String charge_station_address) {
        this.charge_station_address = charge_station_address;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCharge_fee() {
        return charge_fee;
    }

    public void setCharge_fee(String charge_fee) {
        this.charge_fee = charge_fee;
    }

    public String getService_fee() {
        return service_fee;
    }

    public void setService_fee(String service_fee) {
        this.service_fee = service_fee;
    }

    public String getPark_fee() {
        return park_fee;
    }

    public void setPark_fee(String park_fee) {
        this.park_fee = park_fee;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
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
}
