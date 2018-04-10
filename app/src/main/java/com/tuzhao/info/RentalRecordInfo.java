package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by juncoder on 2018/3/27.
 */

public class RentalRecordInfo extends BaseInfo {

    private String parkSpaceId;

    private String parkSpaceImg;

    private String parkSpaceNumber;

    private String packSpaceStatus;

    private String voltage;

    public String getParkSpaceId() {
        return parkSpaceId;
    }

    public void setParkSpaceId(String parkSpaceId) {
        this.parkSpaceId = parkSpaceId;
    }

    public String getParkSpaceImg() {
        return parkSpaceImg;
    }

    public void setParkSpaceImg(String parkSpaceImg) {
        this.parkSpaceImg = parkSpaceImg;
    }

    public String getParkSpaceNumber() {
        return parkSpaceNumber;
    }

    public void setParkSpaceNumber(String parkSpaceNumber) {
        this.parkSpaceNumber = parkSpaceNumber;
    }

    public String getPackSpaceStatus() {
        return packSpaceStatus;
    }

    public void setPackSpaceStatus(String packSpaceStatus) {
        this.packSpaceStatus = packSpaceStatus;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }
}
