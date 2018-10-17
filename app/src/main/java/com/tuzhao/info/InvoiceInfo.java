package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.tuzhao.info.base_info.BaseInfo;

import java.util.Objects;

/**
 * Created by juncoder on 2018/3/28.
 */

public class InvoiceInfo extends BaseInfo implements Parcelable {

    /**
     * 订单对应总表的id
     */
    private String orderId;

    private String parkspaceName;

    private String parkStarttime;

    private boolean check = false;

    private String locationDescribe;

    private String parkDuration;

    private String actualFee;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getParkspaceName() {
        return parkspaceName;
    }

    public void setParkspaceName(String parkspaceName) {
        this.parkspaceName = parkspaceName;
    }

    public String getParkStarttime() {
        return parkStarttime;
    }

    public void setParkStarttime(String parkStarttime) {
        this.parkStarttime = parkStarttime;
    }

    public boolean getCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getLocationDescribe() {
        return locationDescribe;
    }

    public void setLocationDescribe(String locationDescribe) {
        this.locationDescribe = locationDescribe;
    }

    public String getParkDuration() {
        return parkDuration;
    }

    public void setParkDuration(String parkDuration) {
        this.parkDuration = parkDuration;
    }

    public String getActualFee() {
        return actualFee;
    }

    public void setActualFee(String actualFee) {
        this.actualFee = actualFee;
    }

    @Override
    public String toString() {
        return "InvoiceInfo{" +
                "orderId='" + orderId + '\'' +
                ", parkspaceName='" + parkspaceName + '\'' +
                ", parkStarttime='" + parkStarttime + '\'' +
                ", check=" + check +
                ", locationDescribe='" + locationDescribe + '\'' +
                ", parkDuration='" + parkDuration + '\'' +
                ", actualFee='" + actualFee + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderId);
        dest.writeString(this.parkspaceName);
        dest.writeString(this.parkStarttime);
        dest.writeByte(this.check ? (byte) 1 : (byte) 0);
        dest.writeString(this.locationDescribe);
        dest.writeString(this.parkDuration);
        dest.writeString(this.actualFee);
    }

    public InvoiceInfo() {
    }

    protected InvoiceInfo(Parcel in) {
        this.orderId = in.readString();
        this.parkspaceName = in.readString();
        this.parkStarttime = in.readString();
        this.check = in.readByte() != 0;
        this.locationDescribe = in.readString();
        this.parkDuration = in.readString();
        this.actualFee = in.readString();
    }

    public static final Creator<InvoiceInfo> CREATOR = new Creator<InvoiceInfo>() {
        @Override
        public InvoiceInfo createFromParcel(Parcel source) {
            return new InvoiceInfo(source);
        }

        @Override
        public InvoiceInfo[] newArray(int size) {
            return new InvoiceInfo[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceInfo that = (InvoiceInfo) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

}
