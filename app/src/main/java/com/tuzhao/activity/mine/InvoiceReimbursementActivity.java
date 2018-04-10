package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.info.InvoiceInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/3/28.
 */

public class InvoiceReimbursementActivity extends BaseRefreshActivity {

    private List<InvoiceInfo> mChooseInvoice;

    private TextView mTotalPrice;

    private InvoiceReimbursementAdapter mAdapter;

    private DecimalFormat mDecimalFormat;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mChooseInvoice = new ArrayList<>();
        mDecimalFormat = new DecimalFormat("0.00");
        mTotalPrice = findViewById(R.id.invoice_reimbursement_total_invoice);
        mAdapter = new InvoiceReimbursementAdapter();
        mRecyclerView.setAdapter(mAdapter);
        final CheckBox allChoose = findViewById(R.id.invoice_reimbursement_all_rb);
        allChoose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAllCheck(isChecked);
                allChoose.setChecked(isChecked);
            }
        });
        findViewById(R.id.invoice_reimbursement_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AcceptTicketAddressActivity.class);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestLoadMore();
        dismmisLoadingDialog();
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void onRefresh() {
        List<InvoiceInfo> invoiceInfos = new ArrayList<>();
        InvoiceInfo invoiceInfo;
        mChooseInvoice.clear();
        for (int i = 0; i < 3; i++) {
            invoiceInfo = new InvoiceInfo();
            invoiceInfo.setCheck(i % 2 == 0 ? "ture" : "false");
            invoiceInfo.setPictures("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522131551312&di=52422f4384734a296b537d5040c2e89c&imgtype=0&src=http%3A%2F%2F4493bz.1985t.com%2Fuploads%2Fallimg%2F141025%2F4-141025144557.jpg");
            invoiceInfo.setParkspaceAddress("地下一层03车位");
            invoiceInfo.setParkspaceName("天之力停车场");
            invoiceInfo.setParkDuration(i + "小时3分");
            invoiceInfo.setParkStartTime("2018-03-03 18:33");
            invoiceInfo.setActualFee("15.54");
            invoiceInfos.add(invoiceInfo);
            if (invoiceInfo.getCheck().equals("ture")) {
                mChooseInvoice.add(invoiceInfo);
            }
        }
        mAdapter.setNewData(invoiceInfos);
        calculateTotalPrice();
        mRecyclerView.setRefreshing(false);
    }

    @Override
    protected void onLoadMore() {
        requestLoadMore();
    }

    @Override
    protected int resourceId() {
        return R.layout.activity_invoice_reimbursement_layout;
    }

    @NonNull
    @Override
    protected String title() {
        return "发票报销";
    }

    private void requestLoadMore() {
        InvoiceInfo invoiceInfo;
        for (int i = 0; i < 3; i++) {
            invoiceInfo = new InvoiceInfo();
            invoiceInfo.setCheck(i % 2 == 0 ? "ture" : "false");
            invoiceInfo.setPictures("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522131551312&di=52422f4384734a296b537d5040c2e89c&imgtype=0&src=http%3A%2F%2F4493bz.1985t.com%2Fuploads%2Fallimg%2F141025%2F4-141025144557.jpg");
            invoiceInfo.setParkspaceAddress("地下一层03车位");
            invoiceInfo.setParkspaceName("天之力停车场");
            invoiceInfo.setParkDuration(i + "小时3分");
            invoiceInfo.setParkStartTime("2018-03-03 18:33");
            invoiceInfo.setActualFee("15.54");
            mAdapter.addData(invoiceInfo);
            if (invoiceInfo.getCheck().equals("ture")) {
                mChooseInvoice.add(invoiceInfo);
            }
        }
        mRecyclerView.setLoadingMore(false);
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        double totolPric = 0;
        for (InvoiceInfo invoiceInfo : mChooseInvoice) {
            totolPric += Double.valueOf(invoiceInfo.getActualFee());
        }

        String string = "开票总额:Y" + mDecimalFormat.format(totolPric);
        mTotalPrice.setText(string);
    }

    private void setAllCheck(boolean check) {
        mChooseInvoice.clear();
        for (InvoiceInfo invoiceInfo : mAdapter.getData()) {
            invoiceInfo.setCheck(check ? "ture" : "false");
            if (check) {
                mChooseInvoice.add(invoiceInfo);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    class InvoiceReimbursementAdapter extends BaseAdapter<InvoiceInfo> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final InvoiceInfo invoiceInfo, int position) {
            holder.setText(R.id.invoice_reimbursement_park_lot, invoiceInfo.getParkspaceName())
                    .setText(R.id.invoice_reimbursement_park_duration, "停车时长:" + invoiceInfo.getParkDuration())
                    .setText(R.id.invoice_reimbursement_park_time, invoiceInfo.getParkStartTime())
                    .setText(R.id.invoice_reimbursement_location, invoiceInfo.getParkspaceName())
                    .setText(R.id.invoice_reimbursement_total_price, invoiceInfo.getActualFee())
                    .setText(R.id.invoice_reimbursement_park_lot, invoiceInfo.getParkspaceName())
                    .showPic(R.id.invoice_reimbursement_iv, invoiceInfo.getPictures())
                    .setCheckboxCheck(R.id.invoice_reimbursement_rb, invoiceInfo.getCheck().equals("ture"));
            final CheckBox radioButton = (CheckBox) holder.getView(R.id.invoice_reimbursement_rb);
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.e(TAG, "onCheckedChanged: " + isChecked);
                    if (isChecked) {
                        mChooseInvoice.add(invoiceInfo);
                    } else {
                        mChooseInvoice.remove(invoiceInfo);
                    }
                    radioButton.setChecked(isChecked);
                    calculateTotalPrice();
                }
            });
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_invoice_reimbursement_layout;
        }

    }
}
