package com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean;

/**
 * 介绍：城市bean
 * 作者：TZL13
 * 时间： 2017/7/5.
 */

public class CityBean extends BaseIndexPinyinBean {
    private String cityname;//城市名字
    private String citycode;//城市码

    public CityBean() {
    }

    public CityBean(String city) {
        this.cityname = city;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    @Override
    public String getTarget() {
        return cityname;
    }
}
