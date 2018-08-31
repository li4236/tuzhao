package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Created by TZL12 on 2017/9/13.
 */

public class ParkOrderInfo implements Parcelable {

    private String id;//订单id

    @SerializedName(value = "parkLotId", alternate = {"parkspace_id"})
    private String parkLotId;//停车场id

    @SerializedName(value = "parkSpaceId", alternate = {"park_id"})
    private String parkSpaceId;//停车位id

    @SerializedName(value = "parkLotName", alternate = {"parkspace_name"})
    private String parkLotName;//停车场名字

    @SerializedName(value = "parkLotAddress", alternate = {"ps_address"})
    private String parkLotAddress;//停车场地址

    @SerializedName(value = "parkSpaceLocation", alternate = {"location_describe"})
    private String parkSpaceLocation;//停车位的位置描述
    private String parkNumber;//车位编号

    @SerializedName(value = "openTime", alternate = "open_time")
    private String openTime;//车位的开放时间
    private String order_starttime;//预计开始停车时间(yyyy-MM-dd HH:mm:ss)
    private String order_endtime;//预计结束停车时间
    private String extensionTime;//顺延时长，单位:秒

    @SerializedName(value = "carNumber", alternate = "car_numble")
    private String carNumber;//停放车辆车牌号

    @SerializedName(value = "username", alternate = {"userName"})
    private String username;//用户名=用户手机号码
    private String park_username;//该停车位主人的手机号码
    private String order_number;//订单编号
    private String order_status;//订单状态:1-已预约、2-停车中、3-待付款、4，5-已完成（待评论、已完成）、6-已取消（超时取消、正常手动取消）
    private String order_time;//下单时间
    private String pictures;//停车场和当前停车位的所有图片，逗号隔开
    private String order_fee;//订单总费用
    private String actual_fee;//实际支付费用(优惠后的)
    private String fine_fee;//超时费用
    private Discount_Info discount;//优惠券

    @SerializedName(value = "parkStartTime", alternate = "park_starttime")
    private String parkStartTime;//真实开始停车的时间

    @SerializedName(value = "parkEndTime", alternate = "park_endtime")
    private String parkEndTime;//真实结束停车的时间
    private String high_time;//高峰时段
    private String low_time;//低峰时段
    private String high_fee;//高峰时段单价
    private String low_fee;//低峰时段单价
    private String high_max_fee;//高峰时段封顶价格。0代表不封顶
    private String low_max_fee;//低峰时段封顶价格。0代表不封顶
    private String fine;//罚金：滞留金。每小时费用

    @SerializedName(value = "cityCode", alternate = "citycode")
    private String cityCode;//订单的城市码
    private double latitude;//停车场的纬度
    private double longitude;//停车场的经度
    private String parkingUserId;//正在停车的用户id
    private String lockId;//车位锁的id

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

    public String getParkLotId() {
        return parkLotId;
    }

    public void setBelong_park_space(String belong_park_space) {
        this.parkLotId = belong_park_space;
    }

    public String getParkSpaceId() {
        return parkSpaceId;
    }

    public void setParkSpaceId(String parkSpaceId) {
        this.parkSpaceId = parkSpaceId;
    }

    public String getParkLotName() {
        return parkLotName;
    }

    public void setPark_space_name(String park_space_name) {
        this.parkLotName = park_space_name;
    }

    public String getParkLotAddress() {
        return parkLotAddress;
    }

    public void setPark_space_address(String park_space_address) {
        this.parkLotAddress = park_space_address;
    }

    public String getParkSpaceLocationDescribe() {
        return parkSpaceLocation;
    }

    public void setAddress_memo(String address_memo) {
        this.parkSpaceLocation = address_memo;
    }

    public String getOrder_starttime() {
        return order_starttime;
    }

    public void setOrder_starttime(String order_starttime) {
        this.order_starttime = order_starttime;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
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
        return parkStartTime;
    }

    public void setPark_start_time(String park_start_time) {
        this.parkStartTime = park_start_time;
    }

    public String getOrder_fee() {
        return order_fee;
    }

    public void setOrder_fee(String order_fee) {
        this.order_fee = order_fee;
    }

    public String getPark_end_time() {
        return parkEndTime;
    }

    public void setPark_end_time(String park_end_time) {
        this.parkEndTime = park_end_time;
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

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getExtensionTime() {
        return extensionTime;
    }

    public void setExtensionTime(String extensionTime) {
        this.extensionTime = extensionTime;
    }

    public String getParkNumber() {
        return parkNumber;
    }

    public void setParkNumber(String parkNumber) {
        this.parkNumber = parkNumber;
    }

    public String getParkingUserId() {
        return parkingUserId;
    }

    public void setParkingUserId(String parkingUserId) {
        this.parkingUserId = parkingUserId;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkOrderInfo that = (ParkOrderInfo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.parkLotId);
        dest.writeString(this.parkSpaceId);
        dest.writeString(this.parkLotName);
        dest.writeString(this.parkLotAddress);
        dest.writeString(this.parkSpaceLocation);
        dest.writeString(this.parkNumber);
        dest.writeString(this.openTime);
        dest.writeString(this.order_starttime);
        dest.writeString(this.order_endtime);
        dest.writeString(this.extensionTime);
        dest.writeString(this.carNumber);
        dest.writeString(this.username);
        dest.writeString(this.park_username);
        dest.writeString(this.order_number);
        dest.writeString(this.order_status);
        dest.writeString(this.order_time);
        dest.writeString(this.pictures);
        dest.writeString(this.order_fee);
        dest.writeString(this.actual_fee);
        dest.writeString(this.fine_fee);
        dest.writeParcelable(this.discount, flags);
        dest.writeString(this.parkStartTime);
        dest.writeString(this.parkEndTime);
        dest.writeString(this.high_time);
        dest.writeString(this.low_time);
        dest.writeString(this.high_fee);
        dest.writeString(this.low_fee);
        dest.writeString(this.high_max_fee);
        dest.writeString(this.low_max_fee);
        dest.writeString(this.fine);
        dest.writeString(this.cityCode);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.parkingUserId);
        dest.writeString(this.lockId);
    }

    public ParkOrderInfo() {
    }

    protected ParkOrderInfo(Parcel in) {
        this.id = in.readString();
        this.parkLotId = in.readString();
        this.parkSpaceId = in.readString();
        this.parkLotName = in.readString();
        this.parkLotAddress = in.readString();
        this.parkSpaceLocation = in.readString();
        this.parkNumber = in.readString();
        this.openTime = in.readString();
        this.order_starttime = in.readString();
        this.order_endtime = in.readString();
        this.extensionTime = in.readString();
        this.carNumber = in.readString();
        this.username = in.readString();
        this.park_username = in.readString();
        this.order_number = in.readString();
        this.order_status = in.readString();
        this.order_time = in.readString();
        this.pictures = in.readString();
        this.order_fee = in.readString();
        this.actual_fee = in.readString();
        this.fine_fee = in.readString();
        this.discount = in.readParcelable(Discount_Info.class.getClassLoader());
        this.parkStartTime = in.readString();
        this.parkEndTime = in.readString();
        this.high_time = in.readString();
        this.low_time = in.readString();
        this.high_fee = in.readString();
        this.low_fee = in.readString();
        this.high_max_fee = in.readString();
        this.low_max_fee = in.readString();
        this.fine = in.readString();
        this.cityCode = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.parkingUserId = in.readString();
        this.lockId = in.readString();
    }

    public static final Creator<ParkOrderInfo> CREATOR = new Creator<ParkOrderInfo>() {
        @Override
        public ParkOrderInfo createFromParcel(Parcel source) {
            return new ParkOrderInfo(source);
        }

        @Override
        public ParkOrderInfo[] newArray(int size) {
            return new ParkOrderInfo[size];
        }
    };

    @Override
    public String toString() {
        return "ParkOrderInfo{" +
                "id='" + id + '\'' +
                ", parkLotId='" + parkLotId + '\'' +
                ", parkSpaceId='" + parkSpaceId + '\'' +
                ", parkLotName='" + parkLotName + '\'' +
                ", parkLotAddress='" + parkLotAddress + '\'' +
                ", parkSpaceLocation='" + parkSpaceLocation + '\'' +
                ", parkNumber='" + parkNumber + '\'' +
                ", openTime='" + openTime + '\'' +
                ", order_starttime='" + order_starttime + '\'' +
                ", order_endtime='" + order_endtime + '\'' +
                ", extensionTime='" + extensionTime + '\'' +
                ", carNumber='" + carNumber + '\'' +
                ", username='" + username + '\'' +
                ", park_username='" + park_username + '\'' +
                ", order_number='" + order_number + '\'' +
                ", order_status='" + order_status + '\'' +
                ", order_time='" + order_time + '\'' +
                ", pictures='" + pictures + '\'' +
                ", order_fee='" + order_fee + '\'' +
                ", actual_fee='" + actual_fee + '\'' +
                ", fine_fee='" + fine_fee + '\'' +
                ", discount=" + discount +
                ", parkStartTime='" + parkStartTime + '\'' +
                ", parkEndTime='" + parkEndTime + '\'' +
                ", high_time='" + high_time + '\'' +
                ", low_time='" + low_time + '\'' +
                ", high_fee='" + high_fee + '\'' +
                ", low_fee='" + low_fee + '\'' +
                ", high_max_fee='" + high_max_fee + '\'' +
                ", low_max_fee='" + low_max_fee + '\'' +
                ", fine='" + fine + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", parkingUserId='" + parkingUserId + '\'' +
                ", lockId='" + lockId + '\'' +
                '}';
    }
}
