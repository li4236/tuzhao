package com.tuzhao.info;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by TZL12 on 2017/6/17.
 */

public class Discount_Info implements Parcelable {

    private String id;
    private String user_id;//所属用户
    private String discount;//优惠金额
    private String what_type;//充电桩或者停车场优惠
    private String effective_time;//有效时间
    private String min_fee;//订单最低消费金额，-1代表无门槛
    private String is_usable;//是否是有效红包,1（可用），2（已使用），3（已过期）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBelong_user() {
        return user_id;
    }

    public void setBelong_user(String belong_user) {
        this.user_id = belong_user;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getWhat_type() {
        return what_type;
    }

    public void setWhat_type(String what_type) {
        this.what_type = what_type;
    }

    public String getEffective_time() {
        return effective_time;
    }

    public void setEffective_time(String effective_time) {
        this.effective_time = effective_time;
    }

    public String getMin_fee() {
        return min_fee;
    }

    public void setMin_fee(String min_fee) {
        this.min_fee = min_fee;
    }

    public String getIs_usable() {
        return is_usable;
    }

    public void setIs_usable(String is_usable) {
        this.is_usable = is_usable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discount_Info that = (Discount_Info) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(user_id, that.user_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user_id);
    }

    @Override
    public String toString() {
        return "Discount_Info{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", discount='" + discount + '\'' +
                ", what_type='" + what_type + '\'' +
                ", effective_time='" + effective_time + '\'' +
                ", min_fee='" + min_fee + '\'' +
                ", is_usable='" + is_usable + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.user_id);
        dest.writeString(this.discount);
        dest.writeString(this.what_type);
        dest.writeString(this.effective_time);
        dest.writeString(this.min_fee);
        dest.writeString(this.is_usable);
    }

    public Discount_Info() {
    }

    protected Discount_Info(Parcel in) {
        this.id = in.readString();
        this.user_id = in.readString();
        this.discount = in.readString();
        this.what_type = in.readString();
        this.effective_time = in.readString();
        this.min_fee = in.readString();
        this.is_usable = in.readString();
    }

    public static final Parcelable.Creator<Discount_Info> CREATOR = new Parcelable.Creator<Discount_Info>() {
        @Override
        public Discount_Info createFromParcel(Parcel source) {
            return new Discount_Info(source);
        }

        @Override
        public Discount_Info[] newArray(int size) {
            return new Discount_Info[size];
        }
    };
}
