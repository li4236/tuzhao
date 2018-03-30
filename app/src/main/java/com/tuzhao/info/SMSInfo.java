package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by TZL12 on 2017/5/16.
 */

public class SMSInfo extends BaseInfo {

    private String phone_token;//带有手机号码的token
//    private double longitude;//经度
//    private double latitude;//纬度
//    private String cancharge;//是否可充电（是->存放充电站id）
//    private String isparkspace;//是否是停车场
//    private String citycode;//城市码
//    private String picture;//车场或电站图片
//    private String address;//车场或电站地址
//    private String name;//车场或电站名
//    private double price;//车场或电站价格
//    private double grade;//车场或电站评分


    public String getPhone_token() {
        return phone_token;
    }

    public void setPhone_token(String phone_token) {
        this.phone_token = phone_token;
    }
}
