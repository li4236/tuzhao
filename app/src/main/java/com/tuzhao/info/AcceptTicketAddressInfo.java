package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by juncoder on 2018/3/29.
 */

public class AcceptTicketAddressInfo implements Parcelable {

    private String ticketId;

    //发票类型：电子(1)，普票(2)，专票(3)  （非空）
    private String type;

    //公司名称（非空）
    private String company;

    //公司电话（非空）
    private String companyPhone;

    //收件人名（非空）
    private String acceptPersonName;

    //收件号码
    private String acceptPersonTelephone;

    //停车实际支付的金额
    private String acceptPersonEmail;

    //收件地区(如：广东省中山市五桂山)
    private String acceptArea;

    //收件详细地址(如：长命水长逸路天之力新能源科技有限公司2楼)
    private String acceptAddress;

    //税务编号
    private String taxNumber;

    //对公银行
    private String bank;

    //银行卡号
    private String bankNumber;

    //是否是默认收票地址（0：不是，1：是）
    private String isDefault = "0";

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getType() {
        switch (type) {
            case "1":
                return "电子";
            case "2":
                return "普票";
            case "3":
                return "专票";
        }
        return "电子";
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getAcceptPersonName() {
        return acceptPersonName;
    }

    public void setAcceptPersonName(String acceptPersonName) {
        this.acceptPersonName = acceptPersonName;
    }

    public String getAcceptPersonTelephone() {
        return acceptPersonTelephone;
    }

    public void setAcceptPersonTelephone(String acceptPersonTelephone) {
        this.acceptPersonTelephone = acceptPersonTelephone;
    }

    public String getAcceptPersonEmail() {
        return acceptPersonEmail;
    }

    public void setAcceptPersonEmail(String acceptPersonEmail) {
        this.acceptPersonEmail = acceptPersonEmail;
    }

    public String getAcceptArea() {
        return acceptArea;
    }

    public void setAcceptArea(String acceptArea) {
        this.acceptArea = acceptArea;
    }

    public String getAcceptAddress() {
        return acceptAddress;
    }

    public void setAcceptAddress(String acceptAddress) {
        this.acceptAddress = acceptAddress;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ticketId);
        dest.writeString(this.type);
        dest.writeString(this.company);
        dest.writeString(this.companyPhone);
        dest.writeString(this.acceptPersonName);
        dest.writeString(this.acceptPersonTelephone);
        dest.writeString(this.acceptPersonEmail);
        dest.writeString(this.acceptArea);
        dest.writeString(this.acceptAddress);
        dest.writeString(this.taxNumber);
        dest.writeString(this.bank);
        dest.writeString(this.bankNumber);
        dest.writeString(this.isDefault);
    }

    public AcceptTicketAddressInfo() {
    }

    protected AcceptTicketAddressInfo(Parcel in) {
        this.ticketId = in.readString();
        this.type = in.readString();
        this.company = in.readString();
        this.companyPhone = in.readString();
        this.acceptPersonName = in.readString();
        this.acceptPersonTelephone = in.readString();
        this.acceptPersonEmail = in.readString();
        this.acceptArea = in.readString();
        this.acceptAddress = in.readString();
        this.taxNumber = in.readString();
        this.bank = in.readString();
        this.bankNumber = in.readString();
        this.isDefault = in.readString();
    }

    public static final Creator<AcceptTicketAddressInfo> CREATOR = new Creator<AcceptTicketAddressInfo>() {
        @Override
        public AcceptTicketAddressInfo createFromParcel(Parcel source) {
            return new AcceptTicketAddressInfo(source);
        }

        @Override
        public AcceptTicketAddressInfo[] newArray(int size) {
            return new AcceptTicketAddressInfo[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcceptTicketAddressInfo that = (AcceptTicketAddressInfo) o;
        return Objects.equals(ticketId, that.ticketId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ticketId);
    }

}
