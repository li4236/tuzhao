package com.tuzhao.info;

/**
 * Created by juncoder on 2018/3/27.
 */

public class RentalRecordItemInfo {

    /**
     * 订单状态（2：停车中，4/5：已完成）
     */
    private String orderStatus;

    /**
     * 亲友备注（如果是亲友停车的则返回亲友备注，没有备注返回亲友真实姓名，如果为null则代表是出租的订单）
     */
    private String noteName;

    //出租时长
    private String rentalTime;

    //出租的开始时间
    private String rentalStartDate;

    //租用的车牌号码
    private String rentalCarNumber;

    //获得收益
    private String rentalFee;

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

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
