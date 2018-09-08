package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Created by juncoder on 2018/8/20.
 */
public class Car implements Parcelable {

    @SerializedName(value = "carNumber", alternate = {"car_num"})
    private String carNumber;

    private String status;

    private String resaon;

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSortStatus() {
        switch (status) {
            case "1":
                return 2;
            case "2":
                return 1;
            default:
                return 3;
        }
    }

    public String getResaon() {
        return resaon;
    }

    public void setResaon(String resaon) {
        this.resaon = resaon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.carNumber);
        dest.writeString(this.status);
        dest.writeString(this.resaon);
    }

    public Car() {
    }

    protected Car(Parcel in) {
        this.carNumber = in.readString();
        this.status = in.readString();
        this.resaon = in.readString();
    }

    public static final Parcelable.Creator<Car> CREATOR = new Parcelable.Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel source) {
            return new Car(source);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(carNumber, car.carNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carNumber);
    }
}
