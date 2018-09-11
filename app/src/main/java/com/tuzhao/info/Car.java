package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Created by juncoder on 2018/8/20.
 */
public class Car implements Parcelable {

    @SerializedName(value = "carNumber", alternate = "car_num")
    private String carNumber;

    private String status;

    private String resaon;

    private String carOwner;

    private String idCard;

    private String driverLicense;

    private String vehicleLicense;

    private String groupPhoto;

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

    public String getCarOwner() {
        return carOwner;
    }

    public void setCarOwner(String carOwner) {
        this.carOwner = carOwner;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public String getVehicleLicense() {
        return vehicleLicense;
    }

    public void setVehicleLicense(String vehicleLicense) {
        this.vehicleLicense = vehicleLicense;
    }

    public String getGroupPhoto() {
        return groupPhoto;
    }

    public void setGroupPhoto(String groupPhoto) {
        this.groupPhoto = groupPhoto;
    }


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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.carNumber);
        dest.writeString(this.status);
        dest.writeString(this.resaon);
        dest.writeString(this.carOwner);
        dest.writeString(this.idCard);
        dest.writeString(this.driverLicense);
        dest.writeString(this.vehicleLicense);
        dest.writeString(this.groupPhoto);
    }

    public Car() {
    }

    protected Car(Parcel in) {
        this.carNumber = in.readString();
        this.status = in.readString();
        this.resaon = in.readString();
        this.carOwner = in.readString();
        this.idCard = in.readString();
        this.driverLicense = in.readString();
        this.vehicleLicense = in.readString();
        this.groupPhoto = in.readString();
    }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel source) {
            return new Car(source);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };
}
