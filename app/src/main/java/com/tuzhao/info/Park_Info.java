package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Created by TZL12 on 2017/5/19.
 */

public class Park_Info implements Parcelable {
    private String id;
    @SerializedName(value = "parkLotId", alternate = "parkspace_id")
    private String parkLotId;//停车位所属停车场
    private String user_id;//停车位所属用户
    private String userName;//停车位所属用户名称
    private String park_number;//停车位的编号
    private String open_date;//可开放日期
    private String open_time;//可开放时段
    private String shareDay;//每周共享的时间，（1,1,0,1,0,0,1）  注：总共7天，用“，”隔开，第一位数字为1代表该天是共享的，为0代表不共享。例子为周一二四七为共享
    private String pauseShareDate;//暂停共享日期（2018-4-12,2018-4-16）  注：可有多个时间段，用“，”隔开，如果日期已经过了就删掉
    private String location_describe;//地址备注
    //    private String lock_password;//车位锁蓝牙密钥
    private String parking_user_id;//使用者
    //    private String park_is_public;//公共或者个人
    private String park_img;//停车位图片
    private String parkspace_name;//停车场名字
    private String park_status;//1(未开放)   2(开放)     3(暂停)
    private int isLongRent;//0(不长租)    1(长租)
    private String high_time;//高峰时段
    private String low_time;//低峰时段
    private String high_fee;//高峰时段单价
    private String low_fee;//低峰时段单价
    private String high_max_fee;//高峰时段封顶价格。0代表不封顶
    private String low_max_fee;//低峰时段封顶价格。0代表不封顶
    private String order_times;//所有用户所预定的时间，逗号隔开(2018-04-19 17:00*2018-04-19 19:00,2018-04-21 08:00*2018-04-22 05:00)
    private String fine;//罚金：滞留金
    private String profit_ratio;//收益比
    private String installTime;//预计安装时间
    private String reason;//审核未通过原因
    private String type;//车位归属类型
    private String userNoteName;//好友对该车位主人的的备注

    private double latitude;    //经度
    private double longitude;   //纬度
    private float distance;     //距离目的地的距离
    private boolean haveDistination;    //有目的地
    private String carNumber;   //预定人的车牌号码
    private String parkInterval;//预定停车的开始和结束时间(yyyy-MM-dd HH:mm*yyyy-MM-dd HH:mm)

    @SerializedName(value = "cityCode", alternate = {"citycode"})
    private String cityCode;//城市码
    private String create_time;//创建时间，主要是提交为了删除车位
    private String update_time;//更新时间，主要是为了核验是否新增订单号
    private String parkLockId;
    private String parkLockStatus;//车位锁状态，1(打开状态)  2(关闭状态) 3（离线状态）
    private String voltage;//车位锁电量值
    private String indicator;//指标，代表该车位被停车的次数，用于预定车位排序
    // 最大可顺延的分钟数，代表预约停车的结束时间距离合法的结束时间的分钟数。
    // 比如结束时间为2018-11-13 11:00，该车位别的订单预约时间为2018-11-13 11:05 - xxx，则最大可顺延为5分钟。
    private int maxExtensionMinute;

    public String getPark_space_name() {
        return parkspace_name;
    }

    public void setPark_space_name(String park_space_name) {
        this.parkspace_name = park_space_name;
    }

    public String getPark_status() {
        return park_status;
    }

    public void setPark_status(String park_status) {
        this.park_status = park_status;
    }

    public String getHandle_user() {
        return parking_user_id;
    }

    public void setHandle_user(String handle_user) {
        this.parking_user_id = handle_user;
    }

    public String getBelong_parkspace() {
        return parkLotId;
    }

    public void setBelong_parkspace(String belong_parkspace) {
        this.parkLotId = belong_parkspace;
    }

    public String getBelong_user() {
        return user_id;
    }

    public void setBelong_user(String belong_user) {
        this.user_id = belong_user;
    }

    public String getPark_number() {
        return park_number;
    }

    public void setPark_number(String park_numble) {
        this.park_number = park_numble;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getLocation_describe() {
        return location_describe;
    }

    public void setLocation_describe(String location_describe) {
        this.location_describe = location_describe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPark_img() {
        return park_img;
    }

    public void setPark_img(String park_img) {
        this.park_img = park_img;
    }

    public String getOpen_date() {
        return open_date;
    }

    public void setOpen_date(String open_date) {
        this.open_date = open_date;
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

    public String getOrder_times() {
        return order_times;
    }

    public void setOrder_times(String order_times) {
        this.order_times = order_times;
    }

    public String getFine() {
        return fine;
    }

    public void setFine(String fine) {
        this.fine = fine;
    }

    public String getCity_code() {
        return cityCode;
    }

    public void setCity_code(String city_code) {
        this.cityCode = city_code;
    }

    public String getProfit_ratio() {
        return profit_ratio;
    }

    public void setProfit_ratio(String profit_ratio) {
        this.profit_ratio = profit_ratio;
    }

    public String getCreat_time() {
        return create_time;
    }

    public void setCreat_time(String creat_time) {
        this.create_time = creat_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getParkLotId() {
        return parkLotId;
    }

    public void setParkLotId(String parkLotId) {
        this.parkLotId = parkLotId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public boolean isLongRent() {
        return isLongRent == 1;
    }

    public void setIsLongRent(boolean isLongRent) {
        this.isLongRent = isLongRent ? 1 : 0;
    }

    /**
     * @return true(该车位不是长租得来的车位)
     */
    public boolean isLongRentParkSpace() {
        return !park_number.startsWith("TB");
    }

    public String getParking_user_id() {
        return parking_user_id;
    }

    public void setParking_user_id(String parking_user_id) {
        this.parking_user_id = parking_user_id;
    }

    public String getParkspace_name() {
        return parkspace_name;
    }

    public void setParkspace_name(String parkspace_name) {
        this.parkspace_name = parkspace_name;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public int getMaxExtensionMinute() {
        return maxExtensionMinute;
    }

    public void setMaxExtensionMinute(int maxExtensionMinute) {
        this.maxExtensionMinute = maxExtensionMinute;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getInstallTime() {
        return installTime;
    }

    public void setInstallTime(String installTime) {
        this.installTime = installTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getParkLockId() {
        return parkLockId;
    }

    public void setParkLockId(String parkLockId) {
        this.parkLockId = parkLockId;
    }

    public String getParkLockStatus() {
        return parkLockStatus;
    }

    public void setParkLockStatus(String parkLockStatus) {
        this.parkLockStatus = parkLockStatus;
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

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public boolean isHaveDistination() {
        return haveDistination;
    }

    public void setHaveDistination(boolean haveDistination) {
        this.haveDistination = haveDistination;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getParkInterval() {
        return parkInterval;
    }

    public void setParkInterval(String parkInterval) {
        this.parkInterval = parkInterval;
    }

    public String getUserNoteName() {
        return userNoteName == null ? userName : userNoteName;
    }

    public void setUserNoteName(String userNoteName) {
        this.userNoteName = userNoteName;
    }

    @Override
    public String toString() {
        return "Park_Info{" +
                "id='" + id + '\'' +
                ", parkLotId='" + parkLotId + '\'' +
                ", user_id='" + user_id + '\'' +
                ", userName='" + userName + '\'' +
                ", park_number='" + park_number + '\'' +
                ", open_date='" + open_date + '\'' +
                ", open_time='" + open_time + '\'' +
                ", shareDay='" + shareDay + '\'' +
                ", pauseShareDate='" + pauseShareDate + '\'' +
                ", location_describe='" + location_describe + '\'' +
                ", parking_user_id='" + parking_user_id + '\'' +
                ", park_img='" + park_img + '\'' +
                ", parkspace_name='" + parkspace_name + '\'' +
                ", park_status='" + park_status + '\'' +
                ", high_time='" + high_time + '\'' +
                ", low_time='" + low_time + '\'' +
                ", high_fee='" + high_fee + '\'' +
                ", low_fee='" + low_fee + '\'' +
                ", high_max_fee='" + high_max_fee + '\'' +
                ", low_max_fee='" + low_max_fee + '\'' +
                ", order_times='" + order_times + '\'' +
                ", fine='" + fine + '\'' +
                ", profit_ratio='" + profit_ratio + '\'' +
                ", installTime='" + installTime + '\'' +
                ", reason='" + reason + '\'' +
                ", type='" + type + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", distance=" + distance +
                ", carNumber='" + carNumber + '\'' +
                ", parkInterval='" + parkInterval + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                ", parkLockId='" + parkLockId + '\'' +
                ", parkLockStatus='" + parkLockStatus + '\'' +
                ", voltage='" + voltage + '\'' +
                ", indicator='" + indicator + '\'' +
                ", isLongRent='" + isLongRent + '\'' +
                '}';
    }

    public Park_Info() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Park_Info parkInfo = (Park_Info) o;
        return Objects.equals(id, parkInfo.id) &&
                Objects.equals(parkLotId, parkInfo.parkLotId) &&
                Objects.equals(user_id, parkInfo.user_id) &&
                Objects.equals(cityCode, parkInfo.cityCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parkLotId, user_id, cityCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.parkLotId);
        dest.writeString(this.user_id);
        dest.writeString(this.userName);
        dest.writeString(this.park_number);
        dest.writeString(this.open_date);
        dest.writeString(this.open_time);
        dest.writeString(this.shareDay);
        dest.writeString(this.pauseShareDate);
        dest.writeString(this.location_describe);
        dest.writeString(this.parking_user_id);
        dest.writeString(this.park_img);
        dest.writeString(this.parkspace_name);
        dest.writeString(this.park_status);
        dest.writeString(this.high_time);
        dest.writeString(this.low_time);
        dest.writeString(this.high_fee);
        dest.writeString(this.low_fee);
        dest.writeString(this.high_max_fee);
        dest.writeString(this.low_max_fee);
        dest.writeString(this.order_times);
        dest.writeString(this.fine);
        dest.writeString(this.profit_ratio);
        dest.writeString(this.installTime);
        dest.writeString(this.reason);
        dest.writeString(this.type);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeFloat(this.distance);
        dest.writeByte(this.haveDistination ? (byte) 1 : (byte) 0);
        dest.writeString(this.carNumber);
        dest.writeString(this.parkInterval);
        dest.writeString(this.cityCode);
        dest.writeString(this.create_time);
        dest.writeString(this.update_time);
        dest.writeString(this.parkLockId);
        dest.writeString(this.parkLockStatus);
        dest.writeString(this.voltage);
        dest.writeString(this.indicator);
        dest.writeString(this.userNoteName);
        dest.writeInt(this.isLongRent);
    }

    protected Park_Info(Parcel in) {
        this.id = in.readString();
        this.parkLotId = in.readString();
        this.user_id = in.readString();
        this.userName = in.readString();
        this.park_number = in.readString();
        this.open_date = in.readString();
        this.open_time = in.readString();
        this.shareDay = in.readString();
        this.pauseShareDate = in.readString();
        this.location_describe = in.readString();
        this.parking_user_id = in.readString();
        this.park_img = in.readString();
        this.parkspace_name = in.readString();
        this.park_status = in.readString();
        this.high_time = in.readString();
        this.low_time = in.readString();
        this.high_fee = in.readString();
        this.low_fee = in.readString();
        this.high_max_fee = in.readString();
        this.low_max_fee = in.readString();
        this.order_times = in.readString();
        this.fine = in.readString();
        this.profit_ratio = in.readString();
        this.installTime = in.readString();
        this.reason = in.readString();
        this.type = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.distance = in.readFloat();
        this.haveDistination = in.readByte() != 0;
        this.carNumber = in.readString();
        this.parkInterval = in.readString();
        this.cityCode = in.readString();
        this.create_time = in.readString();
        this.update_time = in.readString();
        this.parkLockId = in.readString();
        this.parkLockStatus = in.readString();
        this.voltage = in.readString();
        this.indicator = in.readString();
        this.userNoteName = in.readString();
        this.isLongRent = in.readInt();
    }

    public static final Creator<Park_Info> CREATOR = new Creator<Park_Info>() {
        @Override
        public Park_Info createFromParcel(Parcel source) {
            return new Park_Info(source);
        }

        @Override
        public Park_Info[] newArray(int size) {
            return new Park_Info[size];
        }
    };
}
