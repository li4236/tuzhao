package com.tuzhao.info;

/**
 * Created by juncoder on 2018/3/28.
 */

public class InvoiceInfo {

    private String orderId;

    private String parkspaceName;

    private String parkStartTime;

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

    public String getParkStartTime() {
        return parkStartTime;
    }

    public void setParkStartTime(String parkStartTime) {
        this.parkStartTime = parkStartTime;
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
}
