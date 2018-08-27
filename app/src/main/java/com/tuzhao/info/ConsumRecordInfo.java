package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by TZL13 on 2017/6/9.
 */

public class ConsumRecordInfo extends BaseInfo {

    private String id;
    private String type;        //类型，1收入、2支出、3提现
    private String amount;      //交易金额
    private String time;        //交易时间
    private String timeSlot;    //时段
    private String parkName;//车位名称
    private String paymentMethod;//支付方式，1支付宝、2微信、3余额或者两者组合（逗号隔开）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }
}
