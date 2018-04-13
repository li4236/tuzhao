package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.AcceptTicketAddressInfo;
import com.tuzhao.info.InvoiceInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.others.SkipTopBottomDivider;
import com.tuzhao.utils.ConstansUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/4/12.
 */

public class ConfirmTicketOrderActivity extends BaseStatusActivity implements View.OnClickListener {

    private NestedScrollView mNestedScrollView;

    private TextView mCompany;

    private TextView mName;

    private TextView mTelephone;

    private TextView mType;

    private TextView mAddress;

    private TextView mArriveDate;

    private TextView mTotalMoney;

    private ConstraintLayout mNoAddressCl;

    private ConstraintLayout mOrderAddressCl;

    private ConstraintLayout mOrderUndoCl;

    private List<InvoiceInfo> mInvoiceInfos;

    private List<InvoiceInfo> mUndoInvoiceInfos;

    private TicketOrderAdapter mOrderAdapter;

    private AcceptTicketAddressInfo mAddressInfo;

    private java.text.DecimalFormat mDecimalFormat;

    private double mTotalPrice;

    private static final int REQUEST_ADDRESS = 0x286;

    @Override
    protected int resourceId() {
        return R.layout.activity_confirm_ticket_order_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if ((mInvoiceInfos = getIntent().getParcelableArrayListExtra(ConstansUtil.INVOICE_LIST)) == null) {
            showFiveToast("获取订单信息失败，请返回重试");
            finish();
        }

        mDecimalFormat = new DecimalFormat("0.00");

        mNestedScrollView = findViewById(R.id.confirm_ticket_order_nsv);
        mCompany = findViewById(R.id.confirm_ticket_order_company);
        mName = findViewById(R.id.confirm_ticket_order_name);
        mTelephone = findViewById(R.id.confirm_ticket_order_telephone);
        mType = findViewById(R.id.confirm_ticket_order_type);
        mAddress = findViewById(R.id.confirm_ticket_order_address);
        mArriveDate = findViewById(R.id.confirm_ticket_order_arrive_time);
        mTotalMoney = findViewById(R.id.confirm_ticket_order_total_money);
        mOrderAddressCl = findViewById(R.id.confirm_ticket_order_address_cl);
        mNoAddressCl = findViewById(R.id.confirm_ticket_order_no_address_cl);
        mOrderUndoCl = findViewById(R.id.confirm_ticket_order_undo_cl);

        TextView orderUndo = findViewById(R.id.confirm_ticket_order_undo);
        String undoString = orderUndo.getText().toString();
        SpannableString spannableString = new SpannableString(undoString);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff564f")), undoString.indexOf("撤销"), undoString.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        orderUndo.setText(spannableString);

        RecyclerView recyclerView = findViewById(R.id.confirm_ticket_order_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SkipTopBottomDivider(this, false, true));
        recyclerView.setNestedScrollingEnabled(false);
        mOrderAdapter = new TicketOrderAdapter(recyclerView);
        mOrderAdapter.setHeaderView(new View(this));
        recyclerView.setAdapter(mOrderAdapter);

        mOrderAddressCl.setOnClickListener(this);
        mNoAddressCl.setOnClickListener(this);
        findViewById(R.id.confirm_ticket_order_confirm).setOnClickListener(this);
        orderUndo.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mUndoInvoiceInfos = new ArrayList<>(mInvoiceInfos.size());
        mOrderAdapter.setNewData(mInvoiceInfos);
        mNestedScrollView.smoothScrollTo(0, 0);
        calculateTotalPrice();
        setTotalPrice();
        getDefalutAddress();
    }

    @NonNull
    @Override
    protected String title() {
        return "确认订单";
    }

    @Override
    protected void turnBack() {
        super.turnBack();

    }

    private void getDefalutAddress() {
        getOkGo(HttpConstants.getDefalutAcceptTicketAddress)
                .execute(new JsonCallback<Base_Class_Info<AcceptTicketAddressInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<AcceptTicketAddressInfo> o, Call call, Response response) {
                        mAddressInfo = o.data;
                        setAddressInfo();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mOrderAddressCl.setVisibility(View.INVISIBLE);
                        mNoAddressCl.setVisibility(View.VISIBLE);
                        dismmisLoadingDialog();
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    private void setAddressInfo() {
        if (mAddressInfo == null || mAddressInfo.getType() == null) {
            if (mOrderAddressCl.getVisibility() == View.VISIBLE) {
                mOrderAddressCl.setVisibility(View.INVISIBLE);
                mNoAddressCl.setVisibility(View.VISIBLE);
            }
        } else {
            mCompany.setText(mAddressInfo.getCompany());
            mName.setText(mAddressInfo.getAcceptPersonName());
            mTelephone.setText(mAddressInfo.getAcceptPersonTelephone() == null ? "" : mAddressInfo.getAcceptPersonTelephone());
            mType.setText(mAddressInfo.getType());
            String address;
            if (mAddressInfo.getType().equals("电子")) {
                address = mAddressInfo.getAcceptPersonEmail();
            } else {
                address = mAddressInfo.getAcceptArea() + mAddressInfo.getAcceptAddress();
            }
            mAddress.setText(address);

            getInvoiceArriveTime();

            if (mOrderAddressCl.getVisibility() != View.VISIBLE) {
                mOrderAddressCl.setVisibility(View.VISIBLE);
                mNoAddressCl.setVisibility(View.GONE);
            }
        }
    }

    private void getInvoiceArriveTime() {
        OkGo.post(HttpConstants.getInvoiceArriveTime)
                .params("type", mAddressInfo.getType())
                .params("acceptPersonEmail", mAddressInfo.getAcceptPersonEmail())
                .params("acceptArea", mAddressInfo.getAcceptArea())
                .params("acceptAddress", mAddressInfo.getAcceptAddress())
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> voidBase_class_info, Call call, Response response) {
                        mArriveDate.setText(voidBase_class_info.data);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mAddressInfo.getType().equals("电子")) {
                            Calendar calendar = Calendar.getInstance();
                            String arriveTime = (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "（今天）";
                            mArriveDate.setText(arriveTime);
                        } else if (!handleException(e)) {
                            showFiveToast("获取预计到达时间失败，但我们会尽快为您发货");
                        }
                    }
                });
    }

    private void applyInvoiceReimbursement() {
        showLoadingDialog();
        StringBuilder orderId = new StringBuilder();
        for (InvoiceInfo invoiceInfo : mOrderAdapter.getData()) {
            orderId.append(invoiceInfo.getOrderId());
            orderId.append(",");
        }
        orderId.deleteCharAt(orderId.length() - 1);
        getOkGo(HttpConstants.applyInvoiceReimbursement)
                .params("orderId", orderId.toString())
                .params("ticketId", mAddressInfo.getTicketId())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        showFiveToast("开票成功,我们将尽快为您发货");
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismmisLoadingDialog();
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    private void calculateTotalPrice() {
        mTotalPrice = 0;
        for (InvoiceInfo invoiceInfo : mOrderAdapter.getData()) {
            mTotalPrice += Double.valueOf(invoiceInfo.getActualFee());
        }
        mTotalPrice = Double.parseDouble(mDecimalFormat.format(mTotalPrice));
    }

    private void setTotalPrice() {
        String price = "开票总额:" + mTotalPrice + "元";
        mTotalMoney.setText(price);
    }

    private void showOrderUndo() {
        if (mOrderUndoCl.getVisibility() != View.VISIBLE) {
            mOrderUndoCl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_ticket_order_no_address_cl:
                startActivityForResult(AcceptTicketAddressActivity.class, REQUEST_ADDRESS);
                break;
            case R.id.confirm_ticket_order_address_cl:
                startActivityForResult(AcceptTicketAddressActivity.class, REQUEST_ADDRESS);
                break;
            case R.id.confirm_ticket_order_confirm:
                if (mAddressInfo == null) {
                    showFiveToast("请先添加收票地址");
                } else if (mOrderAdapter.getData().isEmpty()) {
                    showFiveToast("没有需要开票的订单,请重新选择");
                    finish();
                } else {
                    calculateTotalPrice();
                    if (mTotalPrice <= 100) {
                        showFiveToast("总额大于100才可以开票哦,请重新选择");
                        finish();
                    } else {
                        applyInvoiceReimbursement();
                    }
                }
                break;
            case R.id.confirm_ticket_order_undo:
                InvoiceInfo invoiceInfo = mUndoInvoiceInfos.remove(mUndoInvoiceInfos.size() - 1);
                mOrderAdapter.addData(invoiceInfo);
                mTotalPrice = Double.valueOf(mDecimalFormat.format(mTotalPrice + Double.valueOf(invoiceInfo.getActualFee())));
                setTotalPrice();
                if (mUndoInvoiceInfos.isEmpty()) {
                    mOrderUndoCl.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADDRESS && resultCode == RESULT_OK) {
            if (data != null) {
                mAddressInfo = (AcceptTicketAddressInfo) data.getSerializableExtra(ConstansUtil.ACCEPT_ADDRESS_INFO);
                setAddressInfo();
            }
        }
    }

    class TicketOrderAdapter extends BaseAdapter<InvoiceInfo> {

        TicketOrderAdapter(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final InvoiceInfo invoiceInfo, final int position) {
            holder.setText(R.id.confirm_ticket_order_park_address_item, invoiceInfo.getParkspaceAddress() + invoiceInfo.getParkspaceName())
                    .setText(R.id.confirm_ticket_order_park_date_item, invoiceInfo.getParkStarttime())
                    .setText(R.id.confirm_ticket_order_ticket_price_item, "￥" + invoiceInfo.getActualFee())
                    .getView(R.id.confirm_ticket_order_ticket_delete_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTotalPrice = Double.valueOf(mDecimalFormat.format(mTotalPrice - Double.valueOf(invoiceInfo.getActualFee())));
                    setTotalPrice();
                    mUndoInvoiceInfos.add(invoiceInfo);
                    notifyRemoveData(position);
                    showOrderUndo();
                }
            });

        }

        @Override
        protected int itemViewId() {
            return R.layout.item_confirm_ticket_order_layout;
        }
    }
}
