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
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.InvoiceInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.DensityUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/3/28.
 */

public class InvoiceReimbursementActivity extends BaseRefreshActivity<InvoiceInfo> {

    private List<InvoiceInfo> mChooseInvoice;

    private TextView mTotalPrice;

    private DecimalFormat mDecimalFormat;

    private CheckBox mAllChoose;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mChooseInvoice = new ArrayList<>();
        mDecimalFormat = new DecimalFormat("0.00");
        mTotalPrice = findViewById(R.id.invoice_reimbursement_total_invoice);
        mAllChoose = findViewById(R.id.invoice_reimbursement_all_rb);
        mAllChoose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAllCheck(isChecked);
                mAllChoose.setChecked(isChecked);
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
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void loadData() {
        getOkgo(HttpConstants.getInvoice)
                .execute(new JsonCallback<Base_Class_List_Info<InvoiceInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<InvoiceInfo> datas, Call call, Response response) {
                        mCommonAdapter.addData(datas.data);
                        calculateTotalPrice();
                        stopLoadStatus();
                        increateStartItem();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismmisLoadingDialog();
                        showEmpty();
                        if (!DensityUtil.isException(InvoiceReimbursementActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                        }
                    }
                });
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_invoice_reimbursement_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final InvoiceInfo invoiceInfo, int position) {
        holder.setText(R.id.invoice_reimbursement_park_lot, invoiceInfo.getParkspaceName())
                .setText(R.id.invoice_reimbursement_park_duration, "停车时长:" + invoiceInfo.getParkDuration())
                .setText(R.id.invoice_reimbursement_park_time, invoiceInfo.getParkStarttime())
                .setText(R.id.invoice_reimbursement_location, invoiceInfo.getParkspaceName())
                .setText(R.id.invoice_reimbursement_total_price, "￥" + invoiceInfo.getActualFee())
                .setText(R.id.invoice_reimbursement_park_lot, invoiceInfo.getParkspaceName())
                .showPic(R.id.invoice_reimbursement_iv, invoiceInfo.getPictures())
                .setCheckboxCheck(R.id.invoice_reimbursement_rb, invoiceInfo.getCheck().equals("ture"));
        final CheckBox radioButton = (CheckBox) holder.getView(R.id.invoice_reimbursement_rb);
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mChooseInvoice.add(invoiceInfo);
                } else {
                    mAllChoose.setChecked(false);
                    mChooseInvoice.remove(invoiceInfo);
                }
                radioButton.setChecked(isChecked);
                calculateTotalPrice();
            }
        });
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

    private void calculateTotalPrice() {
        double totolPric = 0;
        for (InvoiceInfo invoiceInfo : mChooseInvoice) {
            totolPric += Double.valueOf(invoiceInfo.getActualFee());
        }

        String string = "开票总额:￥" + mDecimalFormat.format(totolPric);
        mTotalPrice.setText(string);
    }

    private void setAllCheck(boolean check) {
        mChooseInvoice.clear();
        for (InvoiceInfo invoiceInfo : mCommonAdapter.getData()) {
            invoiceInfo.setCheck(check ? "ture" : "false");
            if (check) {
                mChooseInvoice.add(invoiceInfo);
            }
        }
        mCommonAdapter.notifyDataSetChanged();
    }

}
