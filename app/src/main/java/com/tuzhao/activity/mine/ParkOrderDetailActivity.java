package com.tuzhao.activity.mine;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.InvoiceInfo;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.ViewUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/9/28.
 */
public class ParkOrderDetailActivity extends BaseStatusActivity implements View.OnClickListener {

    private ParkOrderInfo mParkOrderInfo;

    private TextView mOrderAmount;

    private TextView mCarNumber;

    private TextView mParkLotName;

    private TextView mParkSpaceDescription;

    private TextView mActualStartParkTime;

    private TextView mAcutalEndParkTime;

    private TextView mOvertimeDuration;

    private TextView mParkDuration;

    private TextView mOrderNumber;

    private TextView mOrderDate;

    @Override
    protected int resourceId() {
        return R.layout.activity_park_order_detail_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkOrderInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_ORDER_INFO);
        if (mParkOrderInfo == null) {
            showFiveToast("获取订单信息失败，请稍后重试");
            finish();
        }

        mOrderAmount = findViewById(R.id.order_amount);
        mCarNumber = findViewById(R.id.car_number);
        mParkLotName = findViewById(R.id.park_lot_name);
        mParkSpaceDescription = findViewById(R.id.park_space_description);
        mActualStartParkTime = findViewById(R.id.actual_start_park_time);
        mAcutalEndParkTime = findViewById(R.id.actual_end_park_time);
        mOvertimeDuration = findViewById(R.id.overtime_duration);
        mParkDuration = findViewById(R.id.park_duration);
        mOrderNumber = findViewById(R.id.order_number);
        mOrderDate = findViewById(R.id.order_date_tv);

        findViewById(R.id.billing_rules_cl).setOnClickListener(this);
        findViewById(R.id.billing_rules_av).setOnClickListener(this);
        findViewById(R.id.buy_monthly_card).setOnClickListener(this);
        findViewById(R.id.order_comment).setOnClickListener(this);
        findViewById(R.id.order_complaint_cl).setOnClickListener(this);
        findViewById(R.id.contact_service_cl).setOnClickListener(this);
        findViewById(R.id.apply_invoice_cl).setOnClickListener(this);
        findViewById(R.id.copy_order_number).setOnClickListener(this);
        findViewById(R.id.appointment_again).setOnClickListener(this);
        findViewById(R.id.delete_order).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mOrderAmount.setText(mParkOrderInfo.getActual_pay_fee());
        mCarNumber.setText(mParkOrderInfo.getCarNumber());
        mParkLotName.setText(mParkOrderInfo.getParkLotName());
        mParkSpaceDescription.setText(mParkOrderInfo.getParkSpaceLocation());
        mActualStartParkTime.setText(DateUtil.deleteSecond(mParkOrderInfo.getPark_start_time()));
        mAcutalEndParkTime.setText(DateUtil.deleteSecond(mParkOrderInfo.getPark_end_time()));

        String overtimeDuration = DateUtil.getParkOvertime(mParkOrderInfo);
        if (!overtimeDuration.equals("未超时")) {
            mOvertimeDuration.setTextColor(Color.parseColor("#ff2020"));
        }
        mOvertimeDuration.setText(overtimeDuration);

        mParkDuration.setText(DateUtil.getDistanceForDayHourMinute(mParkOrderInfo.getParkStartTime(), mParkOrderInfo.getParkEndTime()));
        mOrderNumber.setText(mParkOrderInfo.getOrder_number());
        mOrderDate.setText("下单时间：" + DateUtil.deleteSecond(mParkOrderInfo.getOrderTime()));
    }

    @NonNull
    @Override
    protected String title() {
        return "订单详情";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.billing_rules_cl:
            case R.id.billing_rules_av:
                startActivity(BillingRuleActivity.class);
                break;
            case R.id.buy_monthly_card:
                startActivity(BuyMonthlyCardActivity.class);
                break;
            case R.id.order_comment:

                break;
            case R.id.order_complaint_cl:
                startActivity(OrderComplaintActivity.class, ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                break;
            case R.id.contact_service_cl:
                ViewUtil.contactService(ParkOrderDetailActivity.this);
                break;
            case R.id.apply_invoice_cl:
                if (mParkOrderInfo.isInvoiced()) {
                    showFiveToast("该订单已经开过发票了哦");
                } else if (Double.valueOf(mParkOrderInfo.getActualFee()) >= ConstansUtil.MINIMUN_INVOICE_AMOUNT) {
                    startInvoiceReimbrusement();
                } else {
                    startActivity(InvoiceReimbursementActivity.class);
                }
                break;
            case R.id.copy_order_number:
                copyOrderNumber();
                break;
            case R.id.appointment_again:

                break;
            case R.id.delete_order:
                showDialog("确定删除订单吗", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletelOrder();
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<InvoiceInfo> arrayList;
            if ((arrayList = data.getParcelableArrayListExtra(ConstansUtil.FOR_REQEUST_RESULT)) != null && !arrayList.isEmpty()) {
                //开了发票之后改为已开票状态
                mParkOrderInfo.setIsInvoiced("1");

                //通知订单fragment把订单改为已开票状态，不能使用setResult的办法，因为那样只有startActivityForResult的那个fragment才能收到结果
                IntentObserable.dispatch(ConstansUtil.INVOICE_SUCCESS, ConstansUtil.INTENT_MESSAGE, arrayList.get(0).getOrderId());
            }
        }
    }

    private void startInvoiceReimbrusement() {
        InvoiceInfo invoiceInfo = new InvoiceInfo();
        invoiceInfo.setActualFee(mParkOrderInfo.getActualFee());
        invoiceInfo.setLocationDescribe(mParkOrderInfo.getParkSpaceLocation());
        invoiceInfo.setOrderId(mParkOrderInfo.getOrdersId());
        invoiceInfo.setParkspaceName(mParkOrderInfo.getParkLotName());
        invoiceInfo.setPictures(mParkOrderInfo.getPictures());
        invoiceInfo.setParkStarttime(mParkOrderInfo.getParkStartTime());
        invoiceInfo.setParkDuration(DateUtil.getDistanceForDayHourMinute(mParkOrderInfo.getParkStartTime(), mParkOrderInfo.getParkEndTime()));

        ArrayList<InvoiceInfo> invoiceInfos = new ArrayList<>(1);
        invoiceInfos.add(invoiceInfo);
        Intent intent = new Intent(this, ConfirmAcceptInvoiceAddressActivity.class);
        intent.putParcelableArrayListExtra(ConstansUtil.INVOICE_LIST, invoiceInfos);
        intent.putExtra(ConstansUtil.INTENT_MESSAGE, mParkOrderInfo.getActualFee());
        startActivityForResult(intent, ConstansUtil.REQUSET_CODE);
    }

    private void copyOrderNumber() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            ClipData clipData = ClipData.newPlainText("订单编号", getText(mOrderNumber));
            clipboardManager.setPrimaryClip(clipData);
            showFiveToast("已复制");
        } else {
            showFiveToast("复制失败");
        }
    }

    private void deletelOrder() {
        showLoadingDialog("正在删除");
        getOkGo(HttpConstants.deletelParkOrder)
                .params("order_id", mParkOrderInfo.getId())
                .params("citycode", mParkOrderInfo.getCityCode())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        Intent intent = new Intent();
                        intent.setAction(ConstansUtil.DELETE_PARK_ORDER);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, bundle);
                        IntentObserable.dispatch(intent);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "102":
                                case "103":
                                case "104":
                                    showFiveToast("删除失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

}
