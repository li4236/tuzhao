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

    /**
     * 收票公司名称
     */
    private String company;

    /**
     * 发票类型
     */
    private String type;

    /**
     * 进度（已发出 EMS 103454684541)
     */
    private String progress;

    /**
     * 开票时间
     */
    private String applyDate;

    /**
     * 开票的总金额
     */
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

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
