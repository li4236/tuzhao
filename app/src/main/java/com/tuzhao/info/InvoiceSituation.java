package com.tuzhao.info;

import com.tuzhao.info.base_info.BaseInfo;

/**
 * Created by juncoder on 2018/4/14.
 * <p>
 * 开票进度
 * </p>
 */

public class InvoiceSituation extends BaseInfo {

    //发票id
    private String invoiceSituationId;

    //开票状态(已发出，待发货...)
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

    //进度（已发出 EMS 103454684541)
    private String progress;

    //快递单号
    private String courierNumber;

    //发货时间
    private String deliveryDate;

    //开票的总金额
    private String totalPrice;

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
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
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
}
