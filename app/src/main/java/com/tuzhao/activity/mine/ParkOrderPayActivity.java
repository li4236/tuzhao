package com.tuzhao.activity.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.PayActivity;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Discount_Info;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.PollingUtil;
import com.tuzhao.utils.ViewUtil;

import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/10/8.
 */
public class ParkOrderPayActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private ParkOrderInfo mParkOrderInfo;

    private TextView mOrderAmount;

    private TextView mParkFee;

    private TextView mOvertimeFee;

    private TextView mDiscountDeduction;

    private TextView mMonthlyCardOffer;

    private TextView mMonthlyCardDiscount;

    private TextView mCarNumber;

    private TextView mParkLotName;

    private TextView mParkSpaceNumber;

    private TextView mParkSpaceDescription;

    private TextView mAppointmentStartParkTime;

    private TextView mAppointmentEndParkTime;

    private TextView mActualStartParkTime;

    private TextView mAcutalEndParkTime;

    private TextView mGraceTime;

    private TextView mOvertimeDuration;

    private TextView mOrderNumber;

    private TextView mOrderDate;

    private TextView mAlreadyDiscount;

    private TextView mTotalPrice;

    private double mMonthlyCardOfferPrice;

    private Discount_Info mChooseDiscount;

    private DecimalFormat mDecimalFormat;

    private double mShouldPay;

    private PollingUtil mPollingUtil;

    private int mRequestCount;

    @Override
    protected int resourceId() {
        return R.layout.activity_park_order_pay_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkOrderInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_ORDER_INFO);
        if (mParkOrderInfo == null) {
            showFiveToast("获取订单信息失败，请稍后重试");
            finish();
        }

        mOrderAmount = findViewById(R.id.order_amount);
        mParkFee = findViewById(R.id.park_fee);
        mOvertimeFee = findViewById(R.id.overtime_fee);
        mDiscountDeduction = findViewById(R.id.discount_deduction);
        mMonthlyCardOffer = findViewById(R.id.monthly_card_offer);
        mMonthlyCardDiscount = findViewById(R.id.monthly_card_discount);
        mCarNumber = findViewById(R.id.car_number);
        mParkLotName = findViewById(R.id.park_lot_name);
        mParkSpaceNumber = findViewById(R.id.park_space_number);
        mParkSpaceDescription = findViewById(R.id.park_space_description);
        mAppointmentStartParkTime = findViewById(R.id.appointment_start_park_time);
        mAppointmentEndParkTime = findViewById(R.id.appointment_end_park_time);
        mActualStartParkTime = findViewById(R.id.actual_start_park_time);
        mAcutalEndParkTime = findViewById(R.id.actual_end_park_time);
        mOvertimeDuration = findViewById(R.id.overtime_duration);
        mGraceTime = findViewById(R.id.grace_time);
        mOrderNumber = findViewById(R.id.order_number);
        mOrderDate = findViewById(R.id.order_date_tv);
        mAlreadyDiscount = findViewById(R.id.already_discont);
        mTotalPrice = findViewById(R.id.total_price);

        mDiscountDeduction.setOnClickListener(this);
        findViewById(R.id.billing_rules_cl).setOnClickListener(this);
        findViewById(R.id.billing_rules_av).setOnClickListener(this);
        findViewById(R.id.discount_deduction_av).setOnClickListener(this);
        findViewById(R.id.order_complaint_cl).setOnClickListener(this);
        findViewById(R.id.contact_service_cl).setOnClickListener(this);
        findViewById(R.id.copy_order_number).setOnClickListener(this);
        findViewById(R.id.pay_immediately).setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        showCantCancelLoadingDialog();
        getParkOrderDetail();
        IntentObserable.registerObserver(this);
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
                startActivity(BillingRuleActivity.class, ConstansUtil.PARK_LOT_ID, mParkOrderInfo.getParkLotId(),
                        ConstansUtil.CITY_CODE, mParkOrderInfo.getCityCode());
                break;
            case R.id.discount_deduction:
            case R.id.discount_deduction_av:
                if (mParkOrderInfo.getDiscount().size() == 1 && mParkOrderInfo.getDiscount().get(0).getId().equals("-1")) {
                    showFiveToast("暂无可用优惠券哦");
                } else {
                    Intent dicountIntent = new Intent(ParkOrderPayActivity.this, DiscountActivity.class);
                    dicountIntent.putParcelableArrayListExtra(ConstansUtil.DISCOUNT_LIST, mParkOrderInfo.getDiscount());
                    dicountIntent.putExtra(ConstansUtil.ORDER_FEE, mParkOrderInfo.getOrderFee());
                    dicountIntent.putExtra(ConstansUtil.TYPE, 1);
                    startActivityForResult(dicountIntent, ConstansUtil.DISOUNT_REQUEST_CODE);
                }
                break;
            case R.id.order_complaint_cl:
                startActivity(OrderComplaintActivity.class, ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                break;
            case R.id.contact_service_cl:
                ViewUtil.contactService(ParkOrderPayActivity.this);
                break;
            case R.id.copy_order_number:
                ViewUtil.clipContent(ParkOrderPayActivity.this, getText(mOrderNumber));
                break;
            case R.id.pay_immediately:
                if (mShouldPay >= 0.01) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstansUtil.PAY_TYPE, "0");
                    bundle.putString(ConstansUtil.PAY_MONEY, mShouldPay + "元");
                    bundle.putString(ConstansUtil.PARK_ORDER_ID, mParkOrderInfo.getId());
                    bundle.putString(ConstansUtil.CITY_CODE, mParkOrderInfo.getCityCode());
                    bundle.putString(ConstansUtil.CHOOSE_DISCOUNT, mChooseDiscount == null ? "-1" : mChooseDiscount.getId());
                    startActivity(PayActivity.class, bundle);
                } else {
                    requetFinishOrder();
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.DISOUNT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            mChooseDiscount = data.getParcelableExtra(ConstansUtil.CHOOSE_DISCOUNT);
            calculateShouldPayFee();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
        if (mPollingUtil != null) {
            mPollingUtil.cancel();
        }
    }

    private void getParkOrderDetail() {
        getOkGo(HttpConstants.getParkOrderDetail)
                .params("id", mParkOrderInfo.getId())
                .params("cityCode", mParkOrderInfo.getCityCode())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> o, Call call, Response response) {
                        o.data.copyFrom(mParkOrderInfo);
                        mParkOrderInfo = o.data;
                        init();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismmisLoadingDialog();
                        showFiveToast("获取订单信息失败，请稍后重试");
                        finish();
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        mDecimalFormat = new DecimalFormat("0.00");

        mParkFee.setText(mParkOrderInfo.getOrderFee());
        mOvertimeFee.setText(mParkOrderInfo.getFineFee() + "元");
        mCarNumber.setText(mParkOrderInfo.getCarNumber());
        mParkLotName.setText(mParkOrderInfo.getParkLotName());
        mParkSpaceNumber.setText(mParkOrderInfo.getParkNumber());
        mParkSpaceDescription.setText(mParkOrderInfo.getParkSpaceLocation());
        mAppointmentStartParkTime.setText(DateUtil.deleteSecond(mParkOrderInfo.getOrderStartTime()));
        mAppointmentEndParkTime.setText(DateUtil.deleteSecond(mParkOrderInfo.getOrderEndTime()));
        mActualStartParkTime.setText(DateUtil.deleteSecond(mParkOrderInfo.getPark_start_time()));
        mAcutalEndParkTime.setText(DateUtil.deleteSecond(mParkOrderInfo.getPark_end_time()));
        mGraceTime.setText(UserManager.getInstance().getUserInfo().getLeave_time() + "分钟");

        String overtimeDuration = DateUtil.getParkOvertime(mParkOrderInfo);
        mOvertimeDuration.setText(overtimeDuration);

        mOrderNumber.setText(mParkOrderInfo.getOrder_number());
        mOrderDate.setText("下单时间：" + DateUtil.deleteSecond(mParkOrderInfo.getOrderTime()));

        if (mParkOrderInfo.getMonthlyCardDiscount() != 1) {
            mMonthlyCardDiscount.setVisibility(View.VISIBLE);
            mMonthlyCardDiscount.setText(DateUtil.deleteZero(mParkOrderInfo.getMonthlyCardDiscount() * 10) + "折");
        }

        calculateShouldPayFee();
    }

    /**
     * 根据优惠券金额计算显示相应的优惠金额以及支付金额
     */
    @SuppressLint("SetTextI18n")
    private void calculateShouldPayFee() {
        mShouldPay = Double.parseDouble(mParkOrderInfo.getOrderFee());
        if (mChooseDiscount != null) {
            double discountFee = Double.valueOf(mChooseDiscount.getDiscount());
            if (Double.valueOf(mChooseDiscount.getDiscount()) > 0) {

                String parkDiscount = "—" + discountFee + "元";
                mDiscountDeduction.setText(parkDiscount);

                mShouldPay = Double.parseDouble(mDecimalFormat.format(Double.valueOf(mParkOrderInfo.getOrderFee()) - discountFee));
                calculateParkFeeWithMonthlyCard();
            } else {
                calculateParkFeeWithMonthlyCard();
            }
        } else {
            calculateParkFeeWithMonthlyCard();
            String discountCount;
            if (mParkOrderInfo.getDiscount().size() == 1 && mParkOrderInfo.getDiscount().get(0).getId().equals("-1")) {
                discountCount = "有0个红包";
            } else {
                discountCount = "有" + mParkOrderInfo.getDiscount().size() + "个红包";
            }
            mDiscountDeduction.setText(discountCount);
        }

        if (mChooseDiscount != null) {
            mAlreadyDiscount.setText("已优惠 ¥ " + mDecimalFormat.format(mMonthlyCardOfferPrice + Double.valueOf(mChooseDiscount.getDiscount())));
        } else {
            mAlreadyDiscount.setText("已优惠 ¥ " + mMonthlyCardOfferPrice);
        }

        mOrderAmount.setText(String.valueOf(mShouldPay));
        mTotalPrice.setText("合计 ¥ " + mShouldPay);
    }

    /**
     * 如果有月卡的话则价格打折
     */
    @SuppressLint("SetTextI18n")
    private void calculateParkFeeWithMonthlyCard() {
        if (mParkOrderInfo.getMonthlyCardDiscount() != 1) {
            mMonthlyCardOfferPrice = Double.parseDouble(mDecimalFormat.format(mShouldPay - mShouldPay * mParkOrderInfo.getMonthlyCardDiscount()));
            mMonthlyCardOffer.setText("—" + String.valueOf(mMonthlyCardOfferPrice) + "元");

            mShouldPay = Double.parseDouble(mDecimalFormat.format(mShouldPay * mParkOrderInfo.getMonthlyCardDiscount()));
        }
        if (mShouldPay <= 0) {
            mShouldPay = 0.01;
        }
    }

    /**
     * 支付成功后查询订单是否已经完成
     */
    private void getParkOrder() {
        if (mRequestCount >= 3) {
            dismmisLoadingDialog();
            showFiveToast("查询订单状态失败，请刷新后再查看");
            finish();
        }
        showLoadingDialog("正在查询订单状态");
        mRequestCount++;
        getOkGo(HttpConstants.getParkOrder)
                .params("cityCode", mParkOrderInfo.getCityCode())
                .params("orderId", mParkOrderInfo.getId())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> o, Call call, Response response) {
                        if (o.data.getOrderStatus().equals("4") || o.data.getOrderStatus().equals("5")) {
                            Intent intent = new Intent();
                            intent.setAction(ConstansUtil.FINISH_PAY_ORDER);
                            Bundle bundle = new Bundle();
                            mParkOrderInfo.setOrderStatus(o.data.getOrderStatus());
                            mParkOrderInfo.setActual_pay_fee(o.data.getActual_pay_fee());
                            bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                            intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, bundle);
                            IntentObserable.dispatch(intent);
                            dismmisLoadingDialog();
                            finish();
                        } else if (o.data.getOrderStatus().equals("3")) {
                            mPollingUtil = new PollingUtil(1000, new PollingUtil.OnTimeCallback() {
                                @Override
                                public void onTime() {
                                    getParkOrder();
                                    mPollingUtil.cancel();
                                    mPollingUtil = null;
                                }
                            });
                            mPollingUtil.start();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("查询订单状态失败，请刷新后再查看");
                            finish();
                        }
                    }
                });
    }

    private void requetFinishOrder() {
        //请求改变订单状态，完成订单
        showLoadingDialog("正在完成订单...");
        getOkGo(HttpConstants.finishParkOrder)
                .params("order_id", mParkOrderInfo.getId())
                .params("citycode", mParkOrderInfo.getCityCode())
                .params("pass_code", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" + UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> info, Call call, Response response) {
                        Intent intent = new Intent();
                        intent.setAction(ConstansUtil.FINISH_PAY_ORDER);
                        Bundle bundle = new Bundle();
                        mParkOrderInfo.setOrderStatus("4");
                        mParkOrderInfo.setActual_pay_fee("0.0");
                        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, bundle);
                        IntentObserable.dispatch(intent);
                        dismmisLoadingDialog();
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast(e.getMessage());
                        }
                    }
                });
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.PAY_SUCCESS:
                    getParkOrder();
                    break;
                case ConstansUtil.DIALOG_ON_BACK_PRESS:
                    dismmisLoadingDialog();
                    finish();
                    break;
            }
        }
    }

}
