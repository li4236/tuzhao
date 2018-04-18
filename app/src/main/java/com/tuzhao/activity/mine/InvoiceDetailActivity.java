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
 */

public class InvoiceDetailActivity extends BaseStatusActivity {

    private InvoiceSituation mInvoiceSituation;

    private TextView mInvoiceDetailStatus;

    private TextView mInvoiceDetailStatusDescription;

    private TextView mCompany;

    private TextView mName;

    private TextView mTelephone;

    private TextView mAddress;

    private TextView mTicketDetail;

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
        if ((mInvoiceSituation = (InvoiceSituation) getIntent().getSerializableExtra(ConstansUtil.INVOICE_SITUATION)) == null) {
           /* showFiveToast("获取发票详情失败，请返回重试");
            finish();*/
        }
        mInvoiceDetailStatus = findViewById(R.id.invoice_detail_status_tv);
        mInvoiceDetailStatusDescription = findViewById(R.id.invoice_detail_status_description);
        mCompany = findViewById(R.id.invoice_detail_company);
        mName = findViewById(R.id.invoice_detail_name);
        mTelephone = findViewById(R.id.invoice_detail_telephone);
        mAddress = findViewById(R.id.invoice_detail_address);
        mTicketDetail = findViewById(R.id.invoice_detail_ticket_detail_tv);
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
        super.initData();
        mInvoiceDetailStatus.setText(mInvoiceSituation.getStatus());
        //mInvoiceDetailStatusDescription.setText();
        mCompany.setText(mInvoiceSituation.getCompany());
        mName.setText(mInvoiceSituation.getPersonName());
        mTelephone.setText(mInvoiceSituation.getTelephone());
        mAddress.setText(mInvoiceSituation.getAddress());
        mTicketContent.setText(mInvoiceSituation.getTicketContent());
        mTicketType.setText(mInvoiceSituation.getType());
        mTicketRise.setText(mInvoiceSituation.getTicketRise());
        mTaxpayerNumber.setText(mInvoiceSituation.getTaxpayerNumber());
        mTicketPrice.setText(mInvoiceSituation.getTotalPrice());

        String courierNumber = "快递单号：" + mInvoiceSituation.getCourierNumber();
        mCourierNumber.setText(courierNumber);

        String deliveryDate = "发货时间：" + mInvoiceSituation.getDeliveryDate();
        mDeliveryDate.setText(deliveryDate);
        dismmisLoadingDialog();
    }

    @NonNull
    @Override
    protected String title() {
        return "开票记录";
    }

}