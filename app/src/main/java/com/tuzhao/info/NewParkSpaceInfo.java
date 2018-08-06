package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juncoder on 2018/4/18.
 */

public class NewParkSpaceInfo implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.parkspace_id);
        dest.writeString(this.citycode);
        dest.writeString(this.address_memo);
        dest.writeString(this.applicant_name);
        dest.writeString(this.install_time);
        dest.writeByte(this.isHourRent ? (byte) 1 : (byte) 0);
    }

    public NewParkSpaceInfo() {
    }

    protected NewParkSpaceInfo(Parcel in) {
        this.parkspace_id = in.readString();
        this.citycode = in.readString();
        this.address_memo = in.readString();
        this.applicant_name = in.readString();
        this.install_time = in.readString();
        this.isHourRent = in.readByte() != 0;
    }

    public static final Creator<NewParkSpaceInfo> CREATOR = new Creator<NewParkSpaceInfo>() {
        @Override
        public NewParkSpaceInfo createFromParcel(Parcel source) {
            return new NewParkSpaceInfo(source);
        }

        @Override
        public NewParkSpaceInfo[] newArray(int size) {
            return new NewParkSpaceInfo[size];
        }
    };
}
