package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juncoder on 2018/7/9.
 */
public class CardInfo implements Parcelable {

    private String id;

    /**
     * 全国卡/地区卡（中山卡）
     */
    private String area;

    /**
     * 卡的状态，0（未启用），1（正在使用），2（已过期）
     */
    private String status;

    /**
     * 过期时间（yyyy-MM-dd)
     */
    private String expiredDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.area);
        dest.writeString(this.status);
        dest.writeString(this.expiredDate);
    }

    public CardInfo() {
    }

    protected CardInfo(Parcel in) {
        this.id = in.readString();
        this.area = in.readString();
        this.status = in.readString();
        this.expiredDate = in.readString();
    }

    public static final Parcelable.Creator<CardInfo> CREATOR = new Parcelable.Creator<CardInfo>() {
        @Override
        public CardInfo createFromParcel(Parcel source) {
            return new CardInfo(source);
        }

        @Override
        public CardInfo[] newArray(int size) {
            return new CardInfo[size];
        }
    };
}
