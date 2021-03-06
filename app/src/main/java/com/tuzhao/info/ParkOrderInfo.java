package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by TZL12 on 2017/9/13.
 */

public class ParkOrderInfo implements Parcelable {

    private String id;//订单id

    @SerializedName(value = "parkLotId", alternate = "parkspace_id")
    private String parkLotId;//停车场id

    @SerializedName(value = "parkSpaceId", alternate = "park_id")
    private String parkSpaceId;//停车位id

    @SerializedName(value = "parkLotName", alternate = "parkspace_name")
    private String parkLotName;//停车场名字

    @SerializedName(value = "parkLotAddress", alternate = "ps_address")
    private String parkLotAddress;//停车场地址

    @SerializedName(value = "parkSpaceLocation", alternate = "location_describe")
    private String parkSpaceLocation;//停车位的位置描述
    private String parkNumber;//车位编号

    private String time;

    @SerializedName(value = "orderStartTime", alternate = "order_starttime")
    private String orderStartTime;//预计开始停车时间(yyyy-MM-dd HH:mm:ss)

    @SerializedName(value = "orderEndTime", alternate = "order_endtime")
    private String orderEndTime;//预计结束停车时间
    private String extensionTime;//顺延时长，单位:秒

    @SerializedName(value = "carNumber", alternate = "car_numble")
    private String carNumber;//停放车辆车牌号

    @SerializedName(value = "userName", alternate = "username")
    private String userName;//用户名=用户手机号码
    private String userNoteName;//对车位主人的备注
    private String park_username;//该停车位主人的手机号码
    private String order_number;//订单编号

    /**
     * 订单状态:1-已预约、2-停车中、3-待付款、4，5-已完成（待评论、已完成）、6-已取消（超时取消、正常手动取消）
     */
    @SerializedName(value = "orderStatus", alternate = {"order_status", "status"})
    private String orderStatus;//如果是好友车位的则是0-已预约，1-停车中，3-已完成，4-已取消

    @SerializedName(value = "orderTime", alternate = "order_time")
    private String orderTime;//下单时间
    private String pictures;//停车场和当前停车位的所有图片，逗号隔开

    @SerializedName(value = "orderFee", alternate = "order_fee")
    private String orderFee;//订单总费用

    @SerializedName(value = "actualFee", alternate = "actual_fee")
    private String actualFee;//实际支付费用(优惠后的)

    @SerializedName(value = "fineFee", alternate = "fine_fee")
    private String fineFee;//超时费用
    private ArrayList<Discount_Info> discount;//优惠券
    private double monthlyCardDiscount; //月卡优惠的折扣（0.7）

    @SerializedName(value = "parkStartTime", alternate = "park_starttime")
    private String parkStartTime;//真实开始停车的时间

    @SerializedName(value = "parkEndTime", alternate = "park_endtime")
    private String parkEndTime;//真实结束停车的时间
    private String high_time;//高峰时段
    private String low_time;//低峰时段
    private String high_fee;//高峰时段单价
    private String low_fee;//低峰时段单价
    private String fine;//罚金：滞留金。每小时费用

    @SerializedName(value = "cityCode", alternate = "citycode")
    private String cityCode;//订单的城市码
    private double latitude;//停车场的纬度
    private double longitude;//停车场的经度
    private String parkingUserId;//正在停车的用户id
    private String lockId;//车位锁的id
    private String parkLockStatus;//车位锁状态：1（打开）2（关闭）3（离线）
    private String parkSpaceStatus;//车位状态
    private String isInvoiced;//0(未开发票)，1(已开发票)
    private String ordersId;//订单对应总表的id
    private String updateTime;//订单的更新时间
    private String isFreePark;
    private String canEarlyLeaveTime;     //是否是可以提前离场

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

    public String getParkSpaceLocation() {
        return parkSpaceLocation;
    }

    public void setParkSpaceLocation(String parkSpaceLocation) {
        this.parkSpaceLocation = parkSpaceLocation;
    }

    public String getOrderStartTime() {
        return orderStartTime;
    }

    public void setOrderStartTime(String orderStartTime) {
        this.orderStartTime = orderStartTime;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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

    public String getOrderFee() {
        return orderFee;
    }

    public void setOrderFee(String orderFee) {
        this.orderFee = orderFee;
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

    public String getFine() {
        return fine;
    }

    public void setFine(String fine) {
        this.fine = fine;
    }

    public String getActual_pay_fee() {
        return actualFee;
    }

    public void setActual_pay_fee(String actual_pay_fee) {
        this.actualFee = actual_pay_fee;
    }

    public ArrayList<Discount_Info> getDiscount() {
        return discount;
    }

    public void setDiscount(ArrayList<Discount_Info> discount) {
        this.discount = discount;
    }

    public double getMonthlyCardDiscount() {
        return monthlyCardDiscount;
    }

    public void setMonthlyCardDiscount(double monthlyCardDiscount) {
        this.monthlyCardDiscount = monthlyCardDiscount;
    }

    public String getFineFee() {
        return fineFee;
    }

    public void setFineFee(String fineFee) {
        this.fineFee = fineFee;
    }

    public String getOrderEndTime() {
        return orderEndTime;
    }

    public void setOrderEndTime(String orderEndTime) {
        this.orderEndTime = orderEndTime;
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

    public void setParkLotId(String parkLotId) {
        this.parkLotId = parkLotId;
    }

    public void setParkLotName(String parkLotName) {
        this.parkLotName = parkLotName;
    }

    public void setParkLotAddress(String parkLotAddress) {
        this.parkLotAddress = parkLotAddress;
    }

    public String getActualFee() {
        return actualFee;
    }

    public void setActualFee(String actualFee) {
        this.actualFee = actualFee;
    }

    public String getParkStartTime() {
        return parkStartTime;
    }

    public void setParkStartTime(String parkStartTime) {
        this.parkStartTime = parkStartTime;
    }

    public String getParkEndTime() {
        return parkEndTime;
    }

    public void setParkEndTime(String parkEndTime) {
        this.parkEndTime = parkEndTime;
    }

    public String getParkLockStatus() {
        return parkLockStatus;
    }

    public void setParkLockStatus(String parkLockStatus) {
        this.parkLockStatus = parkLockStatus;
    }

    public String getParkSpaceStatus() {
        return parkSpaceStatus;
    }

    public void setParkSpaceStatus(String parkSpaceStatus) {
        this.parkSpaceStatus = parkSpaceStatus;
    }

    public boolean isInvoiced() {
        return "1".equals(isInvoiced);
    }

    public void setIsInvoiced(String isInvoiced) {
        this.isInvoiced = isInvoiced;
    }

    public String getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(String ordersId) {
        this.ordersId = ordersId;
    }

    public String getUserNoteName() {
        if (userNoteName == null) {
            return userName;
        }
        return userNoteName;
    }

    public void setUserNoteName(String userNoteName) {
        this.userNoteName = userNoteName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isFreePark() {
        return !"-1".equals(isFreePark);
    }

    public String getIsFreePark() {
        return isFreePark;
    }

    public void setIsFreePark(String isFreePark) {
        this.isFreePark = isFreePark;
    }

    public boolean isCanEarlyLeaveTime() {
        return "1".equals(canEarlyLeaveTime);
    }

    public void setCanEarlyLeaveTime(String canEarlyLeaveTime) {
        this.canEarlyLeaveTime = canEarlyLeaveTime;
    }

    public void copyFrom(ParkOrderInfo parkOrderInfo) {
        this.id = parkOrderInfo.getId();
        this.cityCode = parkOrderInfo.getCityCode();
        this.carNumber = parkOrderInfo.getCarNumber();
        this.parkLotName = parkOrderInfo.getParkLotName();  //车场名字
        this.orderFee = parkOrderInfo.getOrderFee();
        this.actualFee = parkOrderInfo.getActualFee();
        this.orderTime = parkOrderInfo.getOrderTime();      //下单时间
        this.orderStatus = parkOrderInfo.getOrderStatus();
        this.isFreePark = parkOrderInfo.getIsFreePark();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkOrderInfo that = (ParkOrderInfo) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(cityCode, that.cityCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cityCode);
    }

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
                ", time='" + time + '\'' +
                ", orderStartTime='" + orderStartTime + '\'' +
                ", orderEndTime='" + orderEndTime + '\'' +
                ", extensionTime='" + extensionTime + '\'' +
                ", carNumber='" + carNumber + '\'' +
                ", userName='" + userName + '\'' +
                ", park_username='" + park_username + '\'' +
                ", order_number='" + order_number + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", pictures='" + pictures + '\'' +
                ", orderFee='" + orderFee + '\'' +
                ", actualFee='" + actualFee + '\'' +
                ", fineFee='" + fineFee + '\'' +
                ", discount=" + discount +
                ", parkStartTime='" + parkStartTime + '\'' +
                ", parkEndTime='" + parkEndTime + '\'' +
                ", high_time='" + high_time + '\'' +
                ", low_time='" + low_time + '\'' +
                ", high_fee='" + high_fee + '\'' +
                ", low_fee='" + low_fee + '\'' +
                ", fine='" + fine + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", parkingUserId='" + parkingUserId + '\'' +
                ", lockId='" + lockId + '\'' +
                ", isFreePark='" + isFreePark + '\'' +
                ", isFreePark='" + canEarlyLeaveTime + '\'' +
                '}';
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
        dest.writeString(this.time);
        dest.writeString(this.orderStartTime);
        dest.writeString(this.orderEndTime);
        dest.writeString(this.extensionTime);
        dest.writeString(this.carNumber);
        dest.writeString(this.userName);
        dest.writeString(this.userNoteName);
        dest.writeString(this.park_username);
        dest.writeString(this.order_number);
        dest.writeString(this.orderStatus);
        dest.writeString(this.orderTime);
        dest.writeString(this.pictures);
        dest.writeString(this.orderFee);
        dest.writeString(this.actualFee);
        dest.writeString(this.fineFee);
        dest.writeTypedList(this.discount);
        dest.writeDouble(this.monthlyCardDiscount);
        dest.writeString(this.parkStartTime);
        dest.writeString(this.parkEndTime);
        dest.writeString(this.high_time);
        dest.writeString(this.low_time);
        dest.writeString(this.high_fee);
        dest.writeString(this.low_fee);
        dest.writeString(this.fine);
        dest.writeString(this.cityCode);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.parkingUserId);
        dest.writeString(this.lockId);
        dest.writeString(this.parkLockStatus);
        dest.writeString(this.parkSpaceStatus);
        dest.writeString(this.isInvoiced);
        dest.writeString(this.ordersId);
        dest.writeString(this.updateTime);
        dest.writeString(this.isFreePark);
        dest.writeString(this.canEarlyLeaveTime);
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
        this.time = in.readString();
        this.orderStartTime = in.readString();
        this.orderEndTime = in.readString();
        this.extensionTime = in.readString();
        this.carNumber = in.readString();
        this.userName = in.readString();
        this.userNoteName = in.readString();
        this.park_username = in.readString();
        this.order_number = in.readString();
        this.orderStatus = in.readString();
        this.orderTime = in.readString();
        this.pictures = in.readString();
        this.orderFee = in.readString();
        this.actualFee = in.readString();
        this.fineFee = in.readString();
        this.discount = in.createTypedArrayList(Discount_Info.CREATOR);
        this.monthlyCardDiscount = in.readDouble();
        this.parkStartTime = in.readString();
        this.parkEndTime = in.readString();
        this.high_time = in.readString();
        this.low_time = in.readString();
        this.high_fee = in.readString();
        this.low_fee = in.readString();
        this.fine = in.readString();
        this.cityCode = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.parkingUserId = in.readString();
        this.lockId = in.readString();
        this.parkLockStatus = in.readString();
        this.parkSpaceStatus = in.readString();
        this.isInvoiced = in.readString();
        this.ordersId = in.readString();
        this.updateTime = in.readString();
        this.isFreePark = in.readString();
        this.canEarlyLeaveTime = in.readString();
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

    public ParkOrderInfo cloneInfo(String orderFee, String parkEndTime) {
        ParkOrderInfo parkOrderInfo = new ParkOrderInfo();
        parkOrderInfo.parkLotName = this.parkLotName;
        parkOrderInfo.orderStatus = this.orderStatus;
        parkOrderInfo.orderFee = orderFee;
        parkOrderInfo.actualFee = orderFee;
        parkOrderInfo.orderStartTime = this.orderStartTime;
        parkOrderInfo.orderEndTime = this.orderEndTime;
        parkOrderInfo.parkStartTime = this.parkStartTime;
        parkOrderInfo.parkEndTime = parkEndTime;
        parkOrderInfo.isFreePark = this.isFreePark;
        parkOrderInfo.discount = this.discount;
        parkOrderInfo.monthlyCardDiscount = this.monthlyCardDiscount;
        parkOrderInfo.extensionTime = this.extensionTime;
        parkOrderInfo.high_time = this.high_time;
        parkOrderInfo.low_time = this.low_time;
        parkOrderInfo.high_fee = this.high_fee;
        parkOrderInfo.low_fee = this.low_fee;
        parkOrderInfo.fine = this.fine;
        parkOrderInfo.canEarlyLeaveTime = this.canEarlyLeaveTime;
        return parkOrderInfo;
    }

}
