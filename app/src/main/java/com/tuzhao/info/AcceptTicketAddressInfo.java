package com.tuzhao.info;

/**
 * Created by juncoder on 2018/3/29.
 */

public class AcceptTicketAddressInfo {

    private String ticketId;

    //发票类型：电子，普票，专票  （非空）
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
    private String isDefault;

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getType() {
        return type;
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
}
