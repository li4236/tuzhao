package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juncoder on 2018/11/9.
 * <p>
 * 车位长租的天数以及价格
 * </p>
 */
public class LongRentInfo implements Parcelable {

    /**
     * 天数
     */
    private int rentDay;

    /**
     * 正常价格
     */
    private double normalPrice;

    /**
     * 折扣
     */
    private double discount = 1;

    private boolean choose;

    public int getRentDay() {
        return rentDay;
    }

    public void setRentDay(int rentDay) {
        this.rentDay = rentDay;
    }

    public double getNormalPrice() {
        return normalPrice;
    }

    public void setNormalPrice(double normalPrice) {
        this.normalPrice = normalPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.rentDay);
        dest.writeDouble(this.normalPrice);
        dest.writeDouble(this.discount);
    }

    public LongRentInfo() {
    }

    protected LongRentInfo(Parcel in) {
        this.rentDay = in.readInt();
        this.normalPrice = in.readDouble();
        this.discount = in.readDouble();
    }

    public static final Creator<LongRentInfo> CREATOR = new Creator<LongRentInfo>() {
        @Override
        public LongRentInfo createFromParcel(Parcel source) {
            return new LongRentInfo(source);
        }

        @Override
        public LongRentInfo[] newArray(int size) {
            return new LongRentInfo[size];
        }
    };
}
