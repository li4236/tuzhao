package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.tuzhao.http.HttpConstants;

/**
 * Created by juncoder on 2018/6/14.
 */
public class ParkSpaceInfo implements Parcelable {

    private String id;

    private String status;

    private String parkLotId;

    private String parkLotName;

    private String cityCode;

    @SerializedName(value = "revenueRatio", alternate = {"parofitRatio"})
    private String revenueRatio;

    private String parkSpaceDescription;

    private String realName;

    //正面照，反面照（逗号隔开）
    private String idCardPhoto;

    private String idCardPositiveUrl = "-1";

    private String idCardNegativeUrl = "-1";

    //产权照，第一张，第二张，第三张（逗号隔开，最少一张，最多三张）
    private String propertyPhoto;

    private String propertyFirstUrl = "-1";

    private String propertySecondUrl = "-1";

    private String propertyThirdUrl = "-1";

    //审核类型（1：安装，2：拆卸）
    private String type;

    //预约安装时间(2018-06-21 上午)
    private String installTime;

    //押金状态（0：未交押金，1：已交押金，2：已退押金）
    private String DepositStatus;

    //审核失败的理由
    private String reason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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
        if (!idCardPositiveUrl.equals("-1") && !idCardPositiveUrl.startsWith(HttpConstants.ROOT_IMG_URL_ID_CARD)) {
            idCardPositiveUrl = HttpConstants.ROOT_IMG_URL_ID_CARD + idCardPositiveUrl;
        }
        return idCardPositiveUrl;
    }

    public void setIdCardPositiveUrl(String idCardPositiveUrl) {
        this.idCardPositiveUrl = idCardPositiveUrl;
    }

    public String getIdCardNegativeUrl() {
        if (!idCardNegativeUrl.equals("-1") && !idCardNegativeUrl.startsWith(HttpConstants.ROOT_IMG_URL_ID_CARD)) {
            idCardNegativeUrl = HttpConstants.ROOT_IMG_URL_ID_CARD + idCardNegativeUrl;
        }
        return idCardNegativeUrl;
    }

    public void setIdCardNegativeUrl(String idCardNegativeUrl) {
        this.idCardNegativeUrl = idCardNegativeUrl;
    }

    public String getPropertyFirstUrl() {
        if (!propertyFirstUrl.equals("-1") && !propertyFirstUrl.startsWith(HttpConstants.ROOT_IMG_URL_PROPERTY)) {
            propertyFirstUrl = HttpConstants.ROOT_IMG_URL_PROPERTY + propertyFirstUrl;
        }
        return propertyFirstUrl;
    }

    public void setPropertyFirstUrl(String propertyFirstUrl) {
        this.propertyFirstUrl = propertyFirstUrl;
    }

    public String getPropertySecondUrl() {
        if (!propertySecondUrl.equals("-1") && !propertySecondUrl.startsWith(HttpConstants.ROOT_IMG_URL_PROPERTY)) {
            propertySecondUrl = HttpConstants.ROOT_IMG_URL_PROPERTY + propertySecondUrl;
        }
        return propertySecondUrl;
    }

    public void setPropertySecondUrl(String propertySecondUrl) {
        this.propertySecondUrl = propertySecondUrl;
    }

    public String getPropertyThirdUrl() {
        if (!propertyThirdUrl.equals("-1") && !propertyThirdUrl.startsWith(HttpConstants.ROOT_IMG_URL_PROPERTY)) {
            propertyThirdUrl = HttpConstants.ROOT_IMG_URL_PROPERTY + propertyThirdUrl;
        }
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
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getIdCardPhoto() {
        return idCardPhoto;
    }

    public void setIdCardPhoto(String idCardPhoto) {
        this.idCardPhoto = idCardPhoto;
    }

    public String getPropertyPhoto() {
        return propertyPhoto;
    }

    public void setPropertyPhoto(String propertyPhoto) {
        this.propertyPhoto = propertyPhoto;
    }

    public String getInstallTime() {
        return installTime;
    }

    public void setInstallTime(String installTime) {
        this.installTime = installTime;
    }

    @Override
    public String toString() {
        return "ParkSpaceInfo{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", parkLotId='" + parkLotId + '\'' +
                ", parkLotName='" + parkLotName + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", revenueRatio='" + revenueRatio + '\'' +
                ", parkSpaceDescription='" + parkSpaceDescription + '\'' +
                ", realName='" + realName + '\'' +
                ", idCardPhoto='" + idCardPhoto + '\'' +
                ", idCardPositiveUrl='" + idCardPositiveUrl + '\'' +
                ", idCardNegativeUrl='" + idCardNegativeUrl + '\'' +
                ", propertyPhoto='" + propertyPhoto + '\'' +
                ", propertyFirstUrl='" + propertyFirstUrl + '\'' +
                ", propertySecondUrl='" + propertySecondUrl + '\'' +
                ", propertyThirdUrl='" + propertyThirdUrl + '\'' +
                ", type='" + type + '\'' +
                ", installTime='" + installTime + '\'' +
                ", DepositStatus='" + DepositStatus + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.status);
        dest.writeString(this.parkLotId);
        dest.writeString(this.parkLotName);
        dest.writeString(this.cityCode);
        dest.writeString(this.revenueRatio);
        dest.writeString(this.parkSpaceDescription);
        dest.writeString(this.realName);
        dest.writeString(this.idCardPhoto);
        dest.writeString(this.idCardPositiveUrl);
        dest.writeString(this.idCardNegativeUrl);
        dest.writeString(this.propertyPhoto);
        dest.writeString(this.propertyFirstUrl);
        dest.writeString(this.propertySecondUrl);
        dest.writeString(this.propertyThirdUrl);
        dest.writeString(this.type);
        dest.writeString(this.installTime);
        dest.writeString(this.DepositStatus);
        dest.writeString(this.reason);
    }

    public ParkSpaceInfo() {
    }

    protected ParkSpaceInfo(Parcel in) {
        this.id = in.readString();
        this.status = in.readString();
        this.parkLotId = in.readString();
        this.parkLotName = in.readString();
        this.cityCode = in.readString();
        this.revenueRatio = in.readString();
        this.parkSpaceDescription = in.readString();
        this.realName = in.readString();
        this.idCardPhoto = in.readString();
        this.idCardPositiveUrl = in.readString();
        this.idCardNegativeUrl = in.readString();
        this.propertyPhoto = in.readString();
        this.propertyFirstUrl = in.readString();
        this.propertySecondUrl = in.readString();
        this.propertyThirdUrl = in.readString();
        this.type = in.readString();
        this.installTime = in.readString();
        this.DepositStatus = in.readString();
        this.reason = in.readString();
    }

    public static final Creator<ParkSpaceInfo> CREATOR = new Creator<ParkSpaceInfo>() {
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
