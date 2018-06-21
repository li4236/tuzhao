package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juncoder on 2018/6/14.
 */
public class ParkSpaceInfo implements Parcelable {

    private String parkLotId;

    private String parkLotName;

    private String revenueRatio;

    private String parkSpaceDescription;

    private String realName;

    private String idCardPositiveUrl = "-1";

    private String idCardNegativeUrl = "-1";

    private String propertyFirstUrl = "-1";

    private String propertySecondUrl = "-1";

    private String propertyThirdUrl = "-1";

    public String getParkLotName() {
        return parkLotName;
    }

    public void setParkLotName(String parkLotName) {
        this.parkLotName = parkLotName;
    }

    public String getRevenueRatio() {
        return revenueRatio;
    }

    public void setRevenueRatio(String revenueRatio) {
        this.revenueRatio = revenueRatio;
    }

    public String getParkSpaceDescription() {
        return parkSpaceDescription;
    }

    public void setParkSpaceDescription(String parkSpaceDescription) {
        this.parkSpaceDescription = parkSpaceDescription;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCardPositiveUrl() {
        return idCardPositiveUrl;
    }

    public void setIdCardPositiveUrl(String idCardPositiveUrl) {
        this.idCardPositiveUrl = idCardPositiveUrl;
    }

    public String getIdCardNegativeUrl() {
        return idCardNegativeUrl;
    }

    public void setIdCardNegativeUrl(String idCardNegativeUrl) {
        this.idCardNegativeUrl = idCardNegativeUrl;
    }

    public String getPropertyFirstUrl() {
        return propertyFirstUrl;
    }

    public void setPropertyFirstUrl(String propertyFirstUrl) {
        this.propertyFirstUrl = propertyFirstUrl;
    }

    public String getPropertySecondUrl() {
        return propertySecondUrl;
    }

    public void setPropertySecondUrl(String propertySecondUrl) {
        this.propertySecondUrl = propertySecondUrl;
    }

    public String getPropertyThirdUrl() {
        return propertyThirdUrl;
    }

    public void setPropertyThirdUrl(String propertyThirdUrl) {
        this.propertyThirdUrl = propertyThirdUrl;
    }

    public String getParkLotId() {
        return parkLotId;
    }

    public void setParkLotId(String parkLotId) {
        this.parkLotId = parkLotId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.parkLotName);
        dest.writeString(this.revenueRatio);
        dest.writeString(this.parkSpaceDescription);
        dest.writeString(this.realName);
        dest.writeString(this.idCardPositiveUrl);
        dest.writeString(this.idCardNegativeUrl);
        dest.writeString(this.propertyFirstUrl);
        dest.writeString(this.propertySecondUrl);
        dest.writeString(this.propertyThirdUrl);
        dest.writeString(this.parkLotId);
    }

    public ParkSpaceInfo() {
    }

    protected ParkSpaceInfo(Parcel in) {
        this.parkLotName = in.readString();
        this.revenueRatio = in.readString();
        this.parkSpaceDescription = in.readString();
        this.realName = in.readString();
        this.idCardPositiveUrl = in.readString();
        this.idCardNegativeUrl = in.readString();
        this.propertyFirstUrl = in.readString();
        this.propertySecondUrl = in.readString();
        this.propertyThirdUrl = in.readString();
        this.parkLotId = in.readString();
    }

    public static final Parcelable.Creator<ParkSpaceInfo> CREATOR = new Parcelable.Creator<ParkSpaceInfo>() {
        @Override
        public ParkSpaceInfo createFromParcel(Parcel source) {
            return new ParkSpaceInfo(source);
        }

        @Override
        public ParkSpaceInfo[] newArray(int size) {
            return new ParkSpaceInfo[size];
        }
    };
}
