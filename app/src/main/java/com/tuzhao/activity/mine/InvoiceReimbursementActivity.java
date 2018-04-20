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
        mAllChoose.setCheckDrawable(ContextCompat.getDrawable(this, R.drawable.ic_chose));
        mAllChoose.setNoCheckDrawble(ContextCompat.getDrawable(this, R.drawable.ic_nochose));
        mAllChoose.setOnCheckChangeListener(new com.tuzhao.publicwidget.others.CheckBox.OnCheckChangeListener() {
            @Override
            public void onCheckChange(boolean isCheck) {
                setAllCheck(isCheck);
                setTotalPrice();
            }
        });

        findViewById(R.id.invoice_reimbursement_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommonAdapter.getData().isEmpty()) {
                    showFiveToast("没有能够报销的发票哦");
                } else if (mChooseInvoice.isEmpty()) {
                    showFiveToast("你还没选择需要报销的发票哦");
                } else if (calculateTotalPrice() <= 100) {
                    showFiveToast("订单总额大于100才可以开票哦");
                } else {
                    startActivityForResult(ConfirmTicketOrderActivity.class, REQUEST_CODE, ConstansUtil.INVOICE_LIST, mChooseInvoice);
                }
            }
        });
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
        getOkgo(HttpConstants.getInvoice)
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
        String pic = invoiceInfo.getPictures();
        if (pic.contains(",")) {
            pic = pic.substring(0, pic.indexOf(","));
        }
        holder.setText(R.id.invoice_reimbursement_park_lot, invoiceInfo.getParkspaceName())
                .setText(R.id.invoice_reimbursement_park_duration, "停车时长:" + invoiceInfo.getParkDuration())
                .setText(R.id.invoice_reimbursement_park_time, invoiceInfo.getParkStarttime())
                .setText(R.id.invoice_reimbursement_location, invoiceInfo.getLocationDescribe())
                .setText(R.id.invoice_reimbursement_total_price, "￥" + invoiceInfo.getActualFee())
                .showPic(R.id.invoice_reimbursement_iv, HttpConstants.ROOT_IMG_URL_PS + pic)
                .setCheckboxCheck(R.id.invoice_reimbursement_rb, invoiceInfo.getCheck().equals("true"));
        final CheckBox checkBox = holder.getView(R.id.invoice_reimbursement_rb);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mRecyclerView.getRecyclerView().getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                        && !mRecyclerView.getRecyclerView().isComputingLayout()) {
                    if (isChecked) {
                        invoiceInfo.setCheck("true");
                        mChooseInvoice.add(invoiceInfo);
                        if (mChooseInvoice.size() == mCommonAdapter.getData().size()) {
                            mAllChoose.setChecked(true);
                        }
                    } else {
                        invoiceInfo.setCheck("false");
                        mAllChoose.setChecked(false);
                        mChooseInvoice.remove(invoiceInfo);
                    }
                    setTotalPrice();
                }
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
        String string = "开票总额:" + mDecimalFormat.format(calculateTotalPrice()) + "元";
        mTotalPrice.setText(string);
    }

    private void setAllCheck(boolean check) {
        mChooseInvoice.clear();
        for (InvoiceInfo invoiceInfo : mCommonAdapter.getData()) {
            invoiceInfo.setCheck(check ? "true" : "false");
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
            if ((arrayList = data.getParcelableArrayListExtra(ConstansUtil.FOR_REQUEST_RESULT)) != null) {
                mCommonAdapter.removeData(arrayList);
            }
        }
    }
}
