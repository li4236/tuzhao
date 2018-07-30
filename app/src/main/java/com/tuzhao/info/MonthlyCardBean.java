package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by juncoder on 2018/7/9.
 */
public class MonthlyCardBean implements Parcelable {

    private int id;
    private String area;
    private String expiredDate;
    private String cityCode;
    private String discount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonthlyCardBean monthlyCardBean = (MonthlyCardBean) o;
        return id == monthlyCardBean.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.area);
        dest.writeString(this.expiredDate);
        dest.writeString(this.cityCode);
        dest.writeString(this.discount);
    }

    private MonthlyCardBean(Parcel in) {
        this.id = in.readInt();
        this.area = in.readString();
        this.expiredDate = in.readString();
        this.cityCode = in.readString();
        this.discount = in.readString();
    }

    public static final Parcelable.Creator<MonthlyCardBean> CREATOR = new Parcelable.Creator<MonthlyCardBean>() {
        @Override
        public MonthlyCardBean createFromParcel(Parcel source) {
            return new MonthlyCardBean(source);
        }

        @Override
        public MonthlyCardBean[] newArray(int size) {
            return new MonthlyCardBean[size];
        }
    };

}
