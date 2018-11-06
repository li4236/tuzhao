package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

import java.util.Objects;

/**
 * Created by TZL12 on 2017/5/16.
 */

public class NearPointPCInfo extends BaseInfo {

    private String id;//停车场或充电桩的id
    private double longitude;//经度
    private double latitude;//纬度
    private String cancharge;//是否可充电（是->存放充电站id）
    private String isparkspace;//是否是停车场
    private String citycode;//城市码
    private String picture;//车场或电站图片
    private String address;//车场或电站地址
    private String name;//车场或电站名
    private double price;//车场或电站价格
    private double grade;//车场或电站评分
    private int freeNumber;//空闲车位数量
    private int freeTime;//免费停车时长

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

    public String getCancharge() {
        return cancharge;
    }

    public void setCancharge(String cancharge) {
        this.cancharge = cancharge;
    }

    public String getIsparkspace() {
        return isparkspace;
    }

    public void setIsparkspace(String isparkspace) {
        this.isparkspace = isparkspace;
    }

    public String getCity_code() {
        return citycode;
    }

    public void setCity_code(String city_code) {
        this.citycode = city_code;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public int getFreeNumber() {
        return freeNumber;
    }

    public void setFreeNumber(int freeNumber) {
        this.freeNumber = freeNumber;
    }

    public int getFreeTime() {
        return freeTime;
    }

    public void setFreeTime(int freeTime) {
        this.freeTime = freeTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NearPointPCInfo that = (NearPointPCInfo) o;
        return Double.compare(that.longitude, longitude) == 0 &&
                Double.compare(that.latitude, latitude) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(isparkspace, that.isparkspace) &&
                Objects.equals(citycode, that.citycode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, longitude, latitude, isparkspace, citycode);
    }

}
