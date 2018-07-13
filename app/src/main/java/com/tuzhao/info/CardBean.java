package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by juncoder on 2018/7/9.
 */
public class CardBean implements Parcelable {

    private int id;
    private String area;
    private String expiredDate;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardBean cardBean = (CardBean) o;
        return id == cardBean.id;
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
    }

    public CardBean() {
    }

    CardBean(Parcel in) {
        this.id = in.readInt();
        this.area = in.readString();
        this.expiredDate = in.readString();
    }

    public static final Parcelable.Creator<CardBean> CREATOR = new Parcelable.Creator<CardBean>() {
        @Override
        public CardBean createFromParcel(Parcel source) {
            return new CardBean(source);
        }

        @Override
        public CardBean[] newArray(int size) {
            return new CardBean[size];
        }
    };

}
