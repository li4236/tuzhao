package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by juncoder on 2018/4/28.
 */

public class ViolationInfo extends BaseInfo {

    private String id;

    private String userId;

    private String orderId;

    /**
     * 1(早走)  2(超时) 3(不来)
     */
    private String type;

    /**
     * 违规时长(按秒算)
     */
    private String time;

    /**
     * 违规时间
     */
    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
