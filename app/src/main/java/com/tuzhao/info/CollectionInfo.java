package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by TZL13 on 2017/6/9.
 */

public class CollectionInfo extends BaseInfo {

    private String id;
    private String chargestation_id;//所属充电站
    private String parkspace_id;//停车位所属停车场
    private String place;//标记点
    private String type;//值为1代表是充电桩，2代表是停车位
    private String ps_name;//停车场名字
    private String ps_address;//停车场的地址
    private String pimgs_url;//停车场的展示图片
    private String cs_name;//充电站名称
    private String cs_address;//充电站的地址
    private String cimgs_url;//充电桩的展示图片
    private String p_count;//车位数量
    private String c_count;//电桩数量
    private String citycode;//城市码
    private boolean isSelect = false;//被选中

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChargestation_id() {
        return chargestation_id;
    }

    public void setChargestation_id(String chargestation_id) {
        this.chargestation_id = chargestation_id;
    }

    public String getParkspace_id() {
        return parkspace_id;
    }

    public void setParkspace_id(String parkspace_id) {
        this.parkspace_id = parkspace_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPs_name() {
        return ps_name;
    }

    public void setPs_name(String ps_name) {
        this.ps_name = ps_name;
    }

    public String getPs_address() {
        return ps_address;
    }

    public void setPs_address(String ps_address) {
        this.ps_address = ps_address;
    }

    public String getPimgs_url() {
        return pimgs_url;
    }

    public void setPimgs_url(String pimgs_url) {
        this.pimgs_url = pimgs_url;
    }

    public String getCs_name() {
        return cs_name;
    }

    public void setCs_name(String cs_name) {
        this.cs_name = cs_name;
    }

    public String getCs_address() {
        return cs_address;
    }

    public void setCs_address(String cs_address) {
        this.cs_address = cs_address;
    }

    public String getCimgs_url() {
        return cimgs_url;
    }

    public void setCimgs_url(String cimgs_url) {
        this.cimgs_url = cimgs_url;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getP_count() {
        return p_count;
    }

    public void setP_count(String p_count) {
        this.p_count = p_count;
    }

    public String getC_count() {
        return c_count;
    }

    public void setC_count(String c_count) {
        this.c_count = c_count;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
}
