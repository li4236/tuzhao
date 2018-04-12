package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juncoder on 2018/3/28.
 */

public class InvoiceInfo implements Parcelable {

    private String orderId;

    private String parkspaceName;

    private String parkStarttime;

    private String pictures;

    private String check;

    private String parkspaceAddress;

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

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getParkspaceAddress() {
        return parkspaceAddress;
    }

    public void setParkspaceAddress(String parkspaceAddress) {
        this.parkspaceAddress = parkspaceAddress;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderId);
        dest.writeString(this.parkspaceName);
        dest.writeString(this.parkStarttime);
        dest.writeString(this.pictures);
        dest.writeString(this.check);
        dest.writeString(this.parkspaceAddress);
        dest.writeString(this.parkDuration);
        dest.writeString(this.actualFee);
    }

    public InvoiceInfo() {
    }

    protected InvoiceInfo(Parcel in) {
        this.orderId = in.readString();
        this.parkspaceName = in.readString();
        this.parkStarttime = in.readString();
        this.pictures = in.readString();
        this.check = in.readString();
        this.parkspaceAddress = in.readString();
        this.parkDuration = in.readString();
        this.actualFee = in.readString();
    }

    public static final Parcelable.Creator<InvoiceInfo> CREATOR = new Parcelable.Creator<InvoiceInfo>() {
        @Override
        public InvoiceInfo createFromParcel(Parcel source) {
            return new InvoiceInfo(source);
        }

        @Override
        public InvoiceInfo[] newArray(int size) {
            return new InvoiceInfo[size];
        }
    };
}
