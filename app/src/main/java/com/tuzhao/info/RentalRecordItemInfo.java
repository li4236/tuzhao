package com.tuzhao.info;

/**
 * Created by juncoder on 2018/3/27.
 */

public class RentalRecordItemInfo {

    //订单id
    private String orderId;

    //出租时长
    private String rentalTime;

    //出租的开始时间
    private String rentalStartDate;

    //租用的车牌号码
    private String rentalCarNumber;

    //获得收益
    private String rentalFee;

    public String getRentalTime() {
        return rentalTime;
    }

    public void setRentalTime(String rentalTime) {
        this.rentalTime = rentalTime;
    }

    public String getRentalStartDate() {
        return rentalStartDate;
    }

    public void setRentalStartDate(String rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }

    public String getRentalCarNumber() {
        return rentalCarNumber;
    }

    public void setRentalCarNumber(String rentalCarNumber) {
        this.rentalCarNumber = rentalCarNumber;
    }

    public String getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(String rentalFee) {
        this.rentalFee = rentalFee;
    }
}
