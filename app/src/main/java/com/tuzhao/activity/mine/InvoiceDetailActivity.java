package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.info.InvoiceSituation;
import com.tuzhao.utils.ConstansUtil;

/**
 * Created by juncoder on 2018/4/16.
 * <p>
 *     开票详情
 * </p>
 */

public class InvoiceDetailActivity extends BaseStatusActivity {

    private InvoiceSituation mInvoiceSituation;

    private TextView mInvoiceDetailStatus;

    private TextView mInvoiceDetailStatusDescription;

    private TextView mCompany;

    private TextView mName;

    private TextView mTelephone;

    private TextView mAddress;

    private TextView mTicketContent;

    private TextView mTicketType;

    private TextView mTicketRise;

    private TextView mTaxpayerNumber;

    private TextView mTicketPrice;

    private TextView mCourierNumber;

    private TextView mDeliveryDate;

    @Override
    protected int resourceId() {
        return R.layout.activity_invoice_detail_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if ((mInvoiceSituation = getIntent().getParcelableExtra(ConstansUtil.INVOICE_SITUATION)) == null) {
            showFiveToast("获取发票详情失败，请返回重试");
            finish();
        }
        mInvoiceDetailStatus = findViewById(R.id.invoice_detail_status_tv);
        mInvoiceDetailStatusDescription = findViewById(R.id.invoice_detail_status_description);
        mCompany = findViewById(R.id.invoice_detail_company);
        mName = findViewById(R.id.invoice_detail_name);
        mTelephone = findViewById(R.id.invoice_detail_telephone);
        mAddress = findViewById(R.id.invoice_detail_address);
        mTicketContent = findViewById(R.id.invoice_detail_ticket_content_tv);
        mTicketType = findViewById(R.id.invoice_detail_ticket_type_tv);
        mTicketRise = findViewById(R.id.invoice_detail_ticket_rise_tv);
        mTaxpayerNumber = findViewById(R.id.invoice_detail_taxpayer_number_tv);
        mTicketPrice = findViewById(R.id.invoice_detail_ticket_price_tv);
        mCourierNumber = findViewById(R.id.invoice_detail_courier_number);
        mDeliveryDate = findViewById(R.id.invoice_detail_dilivery_date);
    }

    @Override
    protected void initData() {
        mInvoiceDetailStatus.setText(mInvoiceSituation.getStatus());
        String statusDescription;
        switch (mInvoiceSituation.getStatus()) {
            case "未开票":
                statusDescription = "我们会尽快为您开票，请耐心等待";
                break;
            case "未发出":
                statusDescription = "我们会尽快为您发出，请耐心等待";
                break;
            case "已收货":
                statusDescription = "您已签收，如遇到问题请联系工作人员";
                break;
            case "拒绝开票":
                statusDescription = mInvoiceSituation.getReason();
                break;
            default:
                statusDescription = "敬请留意近期快递电话、邮件";
                break;
        }
        mInvoiceDetailStatusDescription.setText(statusDescription);
        mCompany.setText(mInvoiceSituation.getCompany());
        mName.setText(mInvoiceSituation.getPersonName());
        mTelephone.setText(mInvoiceSituation.getTelephone().equals("-1") ? "" : mInvoiceSituation.getTelephone());
        mAddress.setText(mInvoiceSituation.getAddress());
        mTicketContent.setText(mInvoiceSituation.getTicketContent());
        mTicketType.setText(mInvoiceSituation.getType());
        mTicketRise.setText(mInvoiceSituation.getTicketRise());
        mTaxpayerNumber.setText(mInvoiceSituation.getTaxpayerNumber());
        mTicketPrice.setText(mInvoiceSituation.getTotalPrice());

        if (mInvoiceSituation.getType().equals("电子")) {
            goneView(findViewById(R.id.invoice_detail_logistics_status));
            goneView(findViewById(R.id.invoice_detail_logistics_status_divider));
            goneView(mCourierNumber);
            goneView(mDeliveryDate);
        } else {
            StringBuilder courierNumber = new StringBuilder("快递单号：");
            if (mInvoiceSituation.getCourierNumber().equals("-1")) {
                courierNumber.append("暂无");
            } else {
                courierNumber.append(mInvoiceSituation.getCourierNumber());
                courierNumber.append("(");
                courierNumber.append(mInvoiceSituation.getCourier());
                courierNumber.append(")");
            }
            mCourierNumber.setText(courierNumber);

            StringBuilder deliveryDate = new StringBuilder("发货时间：");
            deliveryDate.append(mInvoiceSituation.getDeliveryDate() == null ? "暂未发货" : mInvoiceSituation.getDeliveryDate());
            mDeliveryDate.setText(deliveryDate);
        }
    }

    @NonNull
    @Override
    protected String title() {
        return "开票详情";
    }

}
