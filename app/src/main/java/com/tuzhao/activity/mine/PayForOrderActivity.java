package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Discount_Info;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/5/23.
 */

public class PayForOrderActivity extends BaseStatusActivity implements View.OnClickListener {

    private ParkOrderInfo mParkOrderInfo;

    private TextView mParkTime;

    private TextView mParkTimeDescription;

    private TextView mParkOrderFee;

    private TextView mParkOrderDiscount;

    private TextView mParkOrderCredit;

    private TextView mUserTotalCredit;

    private TextView mShouldPayFee;

    private ArrayList<Discount_Info> mDiscountInfos;

    private double mDiscountAmount;

    @Override
    protected int resourceId() {
        return R.layout.activity_pay_for_order_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if ((mParkOrderInfo = (ParkOrderInfo) getIntent().getSerializableExtra(ConstansUtil.PARK_ORDER_INFO)) == null) {
            showFiveToast(ConstansUtil.APP_ERROR_HINT);
            finish();
        }

        mParkTime = findViewById(R.id.pay_for_order_time);
        mParkTimeDescription = findViewById(R.id.pay_for_order_time_description);
        mParkOrderFee = findViewById(R.id.pay_for_order_fee);
        mParkOrderDiscount = findViewById(R.id.pay_for_order_discount);
        mParkOrderCredit = findViewById(R.id.pay_for_order_credit);
        mUserTotalCredit = findViewById(R.id.pay_for_order_total_credit);
        mShouldPayFee = findViewById(R.id.pay_for_order_should_pay);

        findViewById(R.id.pay_for_order_question).setOnClickListener(this);
        findViewById(R.id.pay_for_order_time_iv).setOnClickListener(this);
        findViewById(R.id.pay_for_order_fee_iv).setOnClickListener(this);
        findViewById(R.id.pay_for_order_credit_iv).setOnClickListener(this);
        mParkOrderDiscount.setOnClickListener(this);
        mShouldPayFee.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mDiscountInfos = new ArrayList<>();
        getDiscount();
        if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getExtensionTime()).compareTo(
                DateUtil.getYearToSecondCalendar(mParkOrderInfo.getPark_end_time())) < 0) {
            //停车时长超过预约时长
            mParkTime.setText(DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getPark_end_time()));
            String timeout = "超时" + DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_endtime(), mParkOrderInfo.getPark_end_time());
            mParkTimeDescription.setText(timeout);

            mParkOrderCredit.setText("-5");
        } else if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrder_endtime()).compareTo(
                DateUtil.getYearToSecondCalendar(mParkOrderInfo.getPark_end_time())) < 0) {
            //停车时间在顺延时长内
            mParkTime.setText(DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getPark_end_time()));
            mParkOrderCredit.setText("+3");
        } else {
            //停车时间不到预约的结束时间
            mParkTime.setText(DateUtil.getDateDistanceForHourWithMinute(mParkOrderInfo.getOrder_starttime(), mParkOrderInfo.getOrder_endtime()));
            mParkOrderCredit.setText("+3");
        }
        mParkOrderFee.setText(mParkOrderInfo.getOrder_fee());
        String totalCredit = "(总分" + com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getCredit() + ")";
        mUserTotalCredit.setText(totalCredit);

    }

    @NonNull
    @Override
    protected String title() {
        return "订单完成";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_for_order_question:

                break;
            case R.id.pay_for_order_time_iv:

                break;
            case R.id.pay_for_order_fee_iv:

                break;
            case R.id.pay_for_order_credit_iv:

                break;
            case R.id.pay_for_order_discount:
                if (mDiscountInfos.isEmpty()) {
                    showFiveToast("暂无可用优惠券哦");
                } else {
                    startActivityWithList(DiscountActivity.class, ConstansUtil.DISCOUNT_LIST, mDiscountInfos);
                }
                break;
            case R.id.pay_for_order_should_pay:

                break;
        }
    }

    /**
     * 获取优惠券
     */
    private void getDiscount() {
        getOkGo(HttpConstants.getUserDiscount)
                .execute(new JsonCallback<Base_Class_List_Info<Discount_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Discount_Info> o, Call call, Response response) {
                        mDiscountInfos = o.data;
                        List<Discount_Info> mCanUseDiscount = new ArrayList<>();
                        for (Discount_Info discount_info : mDiscountInfos) {
                            if (discount_info.getIs_usable().equals("1")) {
                                //可用
                                if (discount_info.getWhat_type().equals("1")) {
                                    //是停车券
                                    if (Integer.valueOf(mParkOrderInfo.getOrder_fee()) >= Integer.valueOf(discount_info.getMin_fee())) {
                                        //大于最低消费
                                        if (DateUtil.isInUsefulDate(discount_info.getEffective_time())) {
                                            //在可用范围内
                                            mCanUseDiscount.add(discount_info);
                                        }
                                    }
                                }
                            }
                        }

                        //按照优惠金额从大到小排序
                        Collections.sort(mCanUseDiscount, new Comparator<Discount_Info>() {
                            @Override
                            public int compare(Discount_Info o1, Discount_Info o2) {
                                return Integer.valueOf(o2.getDiscount()) - Integer.valueOf(o1.getDiscount());
                            }
                        });

                        for (Discount_Info discount_info : mCanUseDiscount) {
                            if (Double.valueOf(mParkOrderInfo.getOrder_fee()) >= Double.valueOf(discount_info.getDiscount())) {
                                mDiscountAmount = Double.valueOf(discount_info.getDiscount());
                                break;
                            }
                        }
                        calculateShouldPayFee();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    /**
     * 根据优惠券金额计算显示相应的优惠金额以及支付金额
     */
    private void calculateShouldPayFee() {
        if (mDiscountAmount != 0) {
            String discount = "优惠券-" + mDiscountAmount;
            SpannableString spannableString = new SpannableString(discount);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1dd0a1")),
                    discount.indexOf("-"), discount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mParkOrderDiscount.setText(spannableString);
        }

        String shouldPay = "确认支付" + DateUtil.decreseOneZero(Double.valueOf(mParkOrderInfo.getOrder_fee()) - mDiscountAmount);
        mShouldPayFee.setText(shouldPay);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && resultCode == RESULT_OK && data != null) {
            Discount_Info discount_info = (Discount_Info) data.getSerializableExtra(ConstansUtil.FOR_REQUEST_RESULT);
            mDiscountAmount = Double.valueOf(discount_info.getDiscount());
            calculateShouldPayFee();
        }
    }

}
