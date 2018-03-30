package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by TZL12 on 2017/9/13.
 */

public class ParkOrderInfo extends BaseInfo {

    private String id;//订单id
    private String parkspace_id;//停车场id
    private String park_id;//停车位id
    private String parkspace_name;//停车场名字
    private String ps_address;//停车场地址
    private String location_describe;//停车位的位置描述
    private String open_time;//车位的开放时间
    private String order_starttime;//预计开始停车时间
    private String order_endtime;//预计结束停车时间
    private String car_numble;//停放车辆车牌号
    private String username;//用户名=用户手机号码
    private String park_username;//该停车位主人的手机号码
    private String order_number;//订单编号
    private String order_status;//订单状态
    private String order_time;//下单时间
    private String pictures;//停车场和当前停车位的所有图片，逗号隔开
    private String order_fee;//订单总费用
    private String actual_fee;//实际支付费用(优惠后的)
    private String fine_fee;//超时费用
    private Discount_Info discount;//优惠券
    private String park_starttime;//真实开始停车的时间
    private String park_endtime;//真实结束停车的时间
    private String high_time;//高峰时段
    private String low_time;//低峰时段
    private String high_fee;//高峰时段单价
    private String low_fee;//低峰时段单价
    private String high_max_fee;//高峰时段封顶价格。0代表不封顶
    private String low_max_fee;//低峰时段封顶价格。0代表不封顶
    private String fine;//罚金：滞留金。每小时费用
    private String citycode;//订单的城市码
    private double latitude;//停车场的纬度
    private double longitude;//停车场的经度

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

    public String getBelong_park_space() {
        return parkspace_id;
    }

    public void setBelong_park_space(String belong_park_space) {
        this.parkspace_id = belong_park_space;
    }

    public String getBelong_park() {
        return park_id;
    }

    public void setBelong_park(String belong_park) {
        this.park_id = belong_park;
    }

    public String getPark_space_name() {
        return parkspace_name;
    }

    public void setPark_space_name(String park_space_name) {
        this.parkspace_name = park_space_name;
    }

    public String getPark_space_address() {
        return ps_address;
    }

    public void setPark_space_address(String park_space_address) {
        this.ps_address = park_space_address;
    }

    public String getAddress_memo() {
        return location_describe;
    }

    public void setAddress_memo(String address_memo) {
        this.location_describe = address_memo;
    }

    public String getOrder_starttime() {
        return order_starttime;
    }

    public void setOrder_starttime(String order_starttime) {
        this.order_starttime = order_starttime;
    }

    public String getCar_numble() {
        return car_numble;
    }

    public void setCar_numble(String car_numble) {
        this.car_numble = car_numble;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPark_username() {
        return park_username;
    }

    public void setPark_username(String park_username) {
        this.park_username = park_username;
    }

    public String getOrder_number() {
        return order_number;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getPark_start_time() {
        return park_starttime;
    }

    public void setPark_start_time(String park_start_time) {
        this.park_starttime = park_start_time;
    }

    public String getOrder_fee() {
        return order_fee;
    }

    public void setOrder_fee(String order_fee) {
        this.order_fee = order_fee;
    }

    public String getPark_end_time() {
        return park_endtime;
    }

    public void setPark_end_time(String park_end_time) {
        this.park_endtime = park_end_time;
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

    public String getActual_pay_fee() {
        return actual_fee;
    }

    public void setActual_pay_fee(String actual_pay_fee) {
        this.actual_fee = actual_pay_fee;
    }

    public Discount_Info getDiscount() {
        return discount;
    }

    public void setDiscount(Discount_Info discount) {
        this.discount = discount;
    }

    public String getFine_fee() {
        return fine_fee;
    }

    public void setFine_fee(String fine_fee) {
        this.fine_fee = fine_fee;
    }

    public String getOrder_endtime() {
        return order_endtime;
    }

    public void setOrder_endtime(String order_endtime) {
        this.order_endtime = order_endtime;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
}
