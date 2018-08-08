package com.tuzhao.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juncoder on 2018/4/14.
 * <p>
 * 开票进度
 * </p>
 */

public class InvoiceSituation implements Parcelable {

    //发票id
    private String invoiceSituationId;

    //开票状态(0:未开票，1:已开票未寄出，2:已寄出，5:拒绝开票)
    private String status;

    //收票公司名称
    private String company;

    //收票人
    private String personName;

    //收票手机号码
    private String telephone;

    //收票地址，如果是电子发票返回邮箱地址
    private String address;

    //发票内容
    private String ticketContent;

    //发票抬头
    private String ticketRise;

    //纳税人识别号
    private String taxpayerNumber;

    //发票类型
    private String type;

    //快递公司
    private String courier;

    //快递单号
    private String courierNumber;

    //发货时间
    private String deliveryDate;

    //开票时间
    private String applicationDate;

    //开票的总金额
    private String totalPrice;

    /**
     * 拒绝开票的理由
     */
    private String reason;

    public String getInvoiceSituationId() {
        return invoiceSituationId;
    }

    public void setInvoiceSituationId(String invoiceSituationId) {
        this.invoiceSituationId = invoiceSituationId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getType() {
        if (type.equals("1")) {
            return "电子";
        } else if (type.equals("2")) {
            return "普票";
        }
        return "专票";
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        switch (status) {
            case "0":
                return "未开票";
            case "1":
                return "未发出";
            case "2":
                return "已寄出";
            case "3":
                return "待收货";
            case "4":
                return "已收货";
            case "5":
                return "拒绝开票";
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTicketContent() {
        return ticketContent;
    }

    public void setTicketContent(String ticketContent) {
        this.ticketContent = ticketContent;
    }

    public String getTicketRise() {
        return ticketRise;
    }

    public void setTicketRise(String ticketRise) {
        this.ticketRise = ticketRise;
    }

    public String getTaxpayerNumber() {
        return taxpayerNumber;
    }

    public void setTaxpayerNumber(String taxpayerNumber) {
        this.taxpayerNumber = taxpayerNumber;
    }

    public String getCourierNumber() {
        return courierNumber;
    }

    public void setCourierNumber(String courierNumber) {
        this.courierNumber = courierNumber;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.invoiceSituationId);
        dest.writeString(this.status);
        dest.writeString(this.company);
        dest.writeString(this.personName);
        dest.writeString(this.telephone);
        dest.writeString(this.address);
        dest.writeString(this.ticketContent);
        dest.writeString(this.ticketRise);
        dest.writeString(this.taxpayerNumber);
        dest.writeString(this.type);
        dest.writeString(this.courier);
        dest.writeString(this.courierNumber);
        dest.writeString(this.deliveryDate);
        dest.writeString(this.applicationDate);
        dest.writeString(this.totalPrice);
        dest.writeString(this.reason);
    }

    public InvoiceSituation() {
    }

    protected InvoiceSituation(Parcel in) {
        this.invoiceSituationId = in.readString();
        this.status = in.readString();
        this.company = in.readString();
        this.personName = in.readString();
        this.telephone = in.readString();
        this.address = in.readString();
        this.ticketContent = in.readString();
        this.ticketRise = in.readString();
        this.taxpayerNumber = in.readString();
        this.type = in.readString();
        this.courier = in.readString();
        this.courierNumber = in.readString();
        this.deliveryDate = in.readString();
        this.applicationDate = in.readString();
        this.totalPrice = in.readString();
        this.reason = in.readString();
    }

    public static final Creator<InvoiceSituation> CREATOR = new Creator<InvoiceSituation>() {
        @Override
        public InvoiceSituation createFromParcel(Parcel source) {
            return new InvoiceSituation(source);
        }

        @Override
        public InvoiceSituation[] newArray(int size) {
            return new InvoiceSituation[size];
        }
    };
}
