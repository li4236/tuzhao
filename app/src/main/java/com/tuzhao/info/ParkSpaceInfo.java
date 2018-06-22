package com.tuzhao.info;

/**
 * Created by juncoder on 2018/6/14.
 */
public class ParkSpaceInfo {

    private String parkLotId;

    private String parkLotName;

    private String mCityCode;

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

    public String getCityCode() {
        return mCityCode;
    }

    public void setCityCode(String cityCode) {
        mCityCode = cityCode;
    }

    @Override
    public String toString() {
        return "ParkSpaceInfo{" +
                "parkLotId='" + parkLotId + '\'' +
                ", parkLotName='" + parkLotName + '\'' +
                ", mCityCode='" + mCityCode + '\'' +
                ", revenueRatio='" + revenueRatio + '\'' +
                ", parkSpaceDescription='" + parkSpaceDescription + '\'' +
                ", realName='" + realName + '\'' +
                ", idCardPositiveUrl='" + idCardPositiveUrl + '\'' +
                ", idCardNegativeUrl='" + idCardNegativeUrl + '\'' +
                ", propertyFirstUrl='" + propertyFirstUrl + '\'' +
                ", propertySecondUrl='" + propertySecondUrl + '\'' +
                ", propertyThirdUrl='" + propertyThirdUrl + '\'' +
                '}';
    }
}
