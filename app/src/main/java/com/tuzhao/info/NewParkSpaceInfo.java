package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by juncoder on 2018/4/18.
 */

public class NewParkSpaceInfo extends BaseInfo {

    private String parkspace_id;

    private String citycode;

    private String address_memo;

    private String applicant_name;

    private String install_time;

    private boolean isHourRent;

    public String getParkspace_id() {
        return parkspace_id;
    }

    public void setParkspace_id(String parkspace_id) {
        this.parkspace_id = parkspace_id;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getAddress_memo() {
        return address_memo;
    }

    public void setAddress_memo(String address_memo) {
        this.address_memo = address_memo;
    }

    public String getApplicant_name() {
        return applicant_name;
    }

    public void setApplicant_name(String applicant_name) {
        this.applicant_name = applicant_name;
    }

    public String getInstall_time() {
        return install_time;
    }

    public void setInstall_time(String install_time) {
        this.install_time = install_time;
    }

    public boolean isHourRent() {
        return isHourRent;
    }

    public void setHourRent(boolean dayRent) {
        isHourRent = dayRent;
    }
}
