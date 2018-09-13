package com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean;

/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class ParkBean extends BaseIndexPinyinBean {

    private String parkStation;//停车位名字
    private String parkID;//停车场ID
    private boolean isTop;//是否是最上面的 不需要被转化成拼音的
    private String citycode;//城市码
    private String profit_ratio;//收益比

    public String getParkId() {
        return parkID;
    }

    public void setParkID(String parkID) {
        this.parkID = parkID;
    }


    public ParkBean() {
    }

    public ParkBean(String parkStation) {
        this.parkStation = parkStation;
    }

    public String getparkStation() {
        return parkStation;
    }

    public ParkBean setparkStation(String parkStation) {
        this.parkStation = parkStation;
        return this;
    }

    public boolean isTop() {
        return isTop;
    }

    public ParkBean setTop(boolean top) {
        isTop = top;
        return this;
    }

    public String getParkStation() {
        return parkStation;
    }

    public void setParkStation(String parkStation) {
        this.parkStation = parkStation;
    }

    @Override
    public String getTarget() {
        return parkStation;
    }

    @Override
    public boolean isNeedToPinyin() {
        return !isTop;
    }


    @Override
    public boolean isShowSuspension() {
        return !isTop;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getProfit_ratio() {
        return profit_ratio;
    }

    public void setProfit_ratio(String profit_ratio) {
        this.profit_ratio = profit_ratio;
    }
}
