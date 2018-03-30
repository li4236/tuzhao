package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by TZL13 on 2017/6/9.
 */

public class ConsumRecordInfo extends BaseInfo {

    private String id;
    private String type;//类型，1收入、2支出、3提现
    private String income_amount;//收益金额
    private String income_time;//收益时间
    private String park_name;//车位名称
    private String park_time;//停车时段

    private String consume_describe;//消费描述（停车场名）
    private String pay_way;//支付方式，1支付宝、2微信、3余额或者两者组合（逗号隔开）
    private String pay_time;//付款时间
    private String actual_pay_fee;//支付的费用

    private String amount;//提现金额
    private String applymoney_time;//提现时间
    private String getmoney_time;//到账时间



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

    public String getIncome_amount() {
        return income_amount;
    }

    public void setIncome_amount(String income_amount) {
        this.income_amount = income_amount;
    }

    public String getIncome_time() {
        return income_time;
    }

    public void setIncome_time(String income_time) {
        this.income_time = income_time;
    }

    public String getPark_name() {
        return park_name;
    }

    public void setPark_name(String park_name) {
        this.park_name = park_name;
    }

    public String getPark_time() {
        return park_time;
    }

    public void setPark_time(String park_time) {
        this.park_time = park_time;
    }

    public String getConsume_describe() {
        return consume_describe;
    }

    public void setConsume_describe(String consume_describe) {
        this.consume_describe = consume_describe;
    }

    public String getPay_way() {
        return pay_way;
    }

    public void setPay_way(String pay_way) {
        this.pay_way = pay_way;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getActual_pay_fee() {
        return actual_pay_fee;
    }

    public void setActual_pay_fee(String actual_pay_fee) {
        this.actual_pay_fee = actual_pay_fee;
    }

    public String getApplymoney_time() {
        return applymoney_time;
    }

    public void setApplymoney_time(String applymoney_time) {
        this.applymoney_time = applymoney_time;
    }

    public String getGetmoney_time() {
        return getmoney_time;
    }

    public void setGetmoney_time(String getmoney_time) {
        this.getmoney_time = getmoney_time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
