package com.tuzhao.info;


import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by TZL12 on 2017/6/17.
 */

public class Discount_Info extends BaseInfo {

    private String id;
    private String user_id;//所属用户
    private String discount;//优惠金额
    private String what_type;//充电桩或者停车场优惠
    private String effective_time;//有效时间
    private String min_fee;//订单最低消费金额，-1代表无门槛
    private String is_usable;//是否是有效红包

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBelong_user() {
        return user_id;
    }

    public void setBelong_user(String belong_user) {
        this.user_id = belong_user;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getWhat_type() {
        return what_type;
    }

    public void setWhat_type(String what_type) {
        this.what_type = what_type;
    }

    public String getEffective_time() {
        return effective_time;
    }

    public void setEffective_time(String effective_time) {
        this.effective_time = effective_time;
    }

    public String getMin_fee() {
        return min_fee;
    }

    public void setMin_fee(String min_fee) {
        this.min_fee = min_fee;
    }

    public String getIs_usable() {
        return is_usable;
    }

    public void setIs_usable(String is_usable) {
        this.is_usable = is_usable;
    }
}
