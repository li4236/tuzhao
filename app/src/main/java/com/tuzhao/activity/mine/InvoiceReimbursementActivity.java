package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.InvoiceInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/3/28.
 */

public class InvoiceReimbursementActivity extends BaseRefreshActivity<InvoiceInfo> {

    private ArrayList<InvoiceInfo> mChooseInvoice;

    private TextView mTotalPrice;

    private TextView mInvoceReimburesmentSubmit;

    private DecimalFormat mDecimalFormat;

    private com.tuzhao.publicwidget.others.CheckBox mAllChoose;

    private static final int REQUEST_CODE = 0x372;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mChooseInvoice = new ArrayList<>();
        mDecimalFormat = new DecimalFormat("0.00");
        mTotalPrice = findViewById(R.id.invoice_reimbursement_total_invoice);
        mAllChoose = findViewById(R.id.invoice_reimbursement_all_rb);
        mInvoceReimburesmentSubmit = findViewById(R.id.invoice_reimbursement_submit);

        mAllChoose.setCheckDrawable(ContextCompat.getDrawable(this, R.drawable.ic_chose));
        mAllChoose.setNoCheckDrawble(ContextCompat.getDrawable(this, R.drawable.ic_nochose));
        mAllChoose.setOnCheckChangeListener(new com.tuzhao.publicwidget.others.CheckBox.OnCheckChangeListener() {
            @Override
            public void onCheckChange(boolean isCheck) {
                setAllCheck(isCheck);
                setTotalPrice();
            }
        });

        mInvoceReimburesmentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InvoiceReimbursementActivity.this, ConfirmAcceptInvoiceAddressActivity.class);
                intent.putParcelableArrayListExtra(ConstansUtil.INVOICE_LIST, mChooseInvoice);
                intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, getText(mTotalPrice).substring(5, getTextLength(mTotalPrice) - 1));
                startActivityForResult(intent, REQUEST_CODE);
                //startActivityForResult(ConfirmTicketOrderActivity.class, REQUEST_CODE, ConstansUtil.INVOICE_LIST, mChooseInvoice);
            }
        });

        View headView = getLayoutInflater().inflate(R.layout.layout_placeholder, mRecyclerView, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(mRecyclerView.getRecyclerView().getLayoutParams());
        layoutParams.height = dpToPx(10);
        headView.setLayoutParams(layoutParams);
        mCommonAdapter.setHeaderView(headView);
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        mChooseInvoice.clear();
        mAllChoose.setChecked(false);
        setTotalPrice();
    }

    @Override
    protected void loadData() {
        getOkgos(HttpConstants.getInvoice)
                .execute(new JsonCallback<Base_Class_List_Info<InvoiceInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<InvoiceInfo> datas, Call call, Response response) {
                        loadDataSuccess(datas);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {
                                switch (e.getMessage()) {
                                    case "101":
                                        showFiveToast("账号异常，请重新登录");
                                        startLogin();
                                        break;
                                    case "102":
                                        showFiveToast("没有更多数据啦");
                                        break;
                                }
                            }
                        });
                    }
                });
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_invoice_reimbursement_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final InvoiceInfo invoiceInfo, int position) {
        final CheckBox checkBox = holder.getView(R.id.invoice_reimbursement_cb);
        final CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mRecyclerView.getRecyclerView().getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                        && !mRecyclerView.getRecyclerView().isComputingLayout()) {
                    if (isChecked) {
                        invoiceInfo.setCheck(true);
                        mChooseInvoice.add(invoiceInfo);
                        if (mChooseInvoice.size() == mCommonAdapter.getData().size()) {
                            mAllChoose.setChecked(true);
                        }
                    } else {
                        invoiceInfo.setCheck(false);
                        mAllChoose.setChecked(false);
                        mChooseInvoice.remove(invoiceInfo);
                    }
                    setTotalPrice();
                }
            }
        };

        checkBox.setOnCheckedChangeListener(checkedChangeListener);

        holder.setText(R.id.invoice_reimbursement_park_lot, invoiceInfo.getParkspaceName())
                .setText(R.id.invoice_reimbursement_park_time, DateUtil.getMonthToDay(invoiceInfo.getParkStarttime()))
                .setText(R.id.invoice_reimbursement_location, invoiceInfo.getLocationDescribe())
                .setText(R.id.invoice_reimbursement_total_price, invoiceInfo.getActualFee())
                .setCheckboxCheck(R.id.invoice_reimbursement_cb, invoiceInfo.getCheck())
                .itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    invoiceInfo.setCheck(true);
                    mChooseInvoice.add(invoiceInfo);
                    if (mChooseInvoice.size() == mCommonAdapter.getData().size()) {
                        mAllChoose.setChecked(true);
                    }
                } else {
                    invoiceInfo.setCheck(false);
                    mAllChoose.setChecked(false);
                    mChooseInvoice.remove(invoiceInfo);
                }
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(!checkBox.isChecked());
                checkBox.setOnCheckedChangeListener(checkedChangeListener);
                setTotalPrice();
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

    private double calculateTotalPrice() {
        double totolPric = 0;
        for (InvoiceInfo invoiceInfo : mChooseInvoice) {
            totolPric += Double.valueOf(invoiceInfo.getActualFee());
        }
        return totolPric;
    }

    private void setTotalPrice() {
        String totalPriceString = mDecimalFormat.format(calculateTotalPrice());
        String string = "开票总额:" + totalPriceString + "元";
        mTotalPrice.setText(string);

        if (Double.valueOf(totalPriceString) >= 100) {
            mInvoceReimburesmentSubmit.setBackgroundResource(R.drawable.bg_yellow);
            if (!mInvoceReimburesmentSubmit.isClickable()) {
                mInvoceReimburesmentSubmit.setClickable(true);
            }
        } else {
            mInvoceReimburesmentSubmit.setBackgroundColor(ConstansUtil.G10_COLOR);
            if (mInvoceReimburesmentSubmit.isClickable()) {
                mInvoceReimburesmentSubmit.setClickable(false);
            }
        }
    }

    private void setAllCheck(boolean check) {
        mChooseInvoice.clear();
        for (InvoiceInfo invoiceInfo : mCommonAdapter.getData()) {
            invoiceInfo.setCheck(check);
            if (check) {
                mChooseInvoice.add(invoiceInfo);
            }
        }
        mCommonAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<InvoiceInfo> arrayList;
            if ((arrayList = data.getParcelableArrayListExtra(ConstansUtil.FOR_REQEUST_RESULT)) != null) {
                mCommonAdapter.removeData(arrayList);
                mChooseInvoice.clear();
                setTotalPrice();
            }
        }
    }

}
