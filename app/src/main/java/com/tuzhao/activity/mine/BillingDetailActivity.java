package com.tuzhao.activity.mine;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import java.text.DecimalFormat;

/**
 * Created by juncoder on 2018/10/25.
 */
public class BillingDetailActivity extends BaseStatusActivity {

    private TextView mOrderFee;

    private TextView mNormalTimeSlotUnitPrice;

    private TextView mNormalTimeSlot;

    private TextView mNormalTimeSlotFee;

    private TextView mHighTimeSlotUnitPrice;

    private TextView mHighTimeSlot;

    private TextView mHighTimeSlotFee;

    private TextView mOvertimeSlotUnitPrice;

    private TextView mOvertimeSlot;

    private TextView mOvertimeSlotFee;

    private TextView mDiscountDecrease;

    private TextView mMonthlyCardDisount;

    private TextView mMonthlyCardDecrease;

    private TextView mTotalPrice;

    private ParkOrderInfo mParkOrderInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_billing_detail_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkOrderInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_ORDER_INFO);
        if (mParkOrderInfo == null) {
            showFiveToast("获取计费详情失败，请稍后重试");
            finish();
        }

        mOrderFee = findViewById(R.id.order_fee);
        mNormalTimeSlotUnitPrice = findViewById(R.id.normal_time_slot_unit_price);
        mNormalTimeSlot = findViewById(R.id.normal_time_slot_tv);
        mNormalTimeSlotFee = findViewById(R.id.normal_time_slot_fee);
        mHighTimeSlotUnitPrice = findViewById(R.id.high_time_slot_unit_price);
        mHighTimeSlot = findViewById(R.id.high_time_slot_tv);
        mHighTimeSlotFee = findViewById(R.id.high_time_slot_fee);
        mOvertimeSlotUnitPrice = findViewById(R.id.overtime_slot_unit_price);
        mOvertimeSlot = findViewById(R.id.overtime_time_slot_tv);
        mOvertimeSlotFee = findViewById(R.id.overtime_time_slot_fee);
        mDiscountDecrease = findViewById(R.id.discount_decrease);
        mMonthlyCardDisount = findViewById(R.id.monthly_card_discount_tv);
        mMonthlyCardDecrease = findViewById(R.id.monthly_card_discount_decrease);
        mTotalPrice = findViewById(R.id.total_price);

        findViewById(R.id.violation_statement_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(BillingRuleActivity.class, ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        mOrderFee.setText(mParkOrderInfo.getOrderFee());
        String[] timeSlot = DateUtil.getParkOrderTime(mParkOrderInfo).split(",");
        double normalTime = Double.valueOf(decimalFormat.format(Long.valueOf(timeSlot[0]) / 3600.0));
        double highTime = Double.valueOf(decimalFormat.format(Long.valueOf(timeSlot[1]) / 3600.0));
        double overtimeTime = Double.valueOf(decimalFormat.format(Long.valueOf(timeSlot[2]) / 3600.0));
        double normalUnitPrice = Double.valueOf(mParkOrderInfo.getLow_fee());
        double highUnitPrice = Double.valueOf(mParkOrderInfo.getHigh_fee());
        double overtimeUnitPrice = Double.valueOf(mParkOrderInfo.getFine());
        double normalFee = normalTime * normalUnitPrice;
        double highFee = highTime * highUnitPrice;
        double overtimeFee = overtimeTime * overtimeUnitPrice;
        double discountDecrease = 0;
        double monthlyCardDecrease = 0;

        mNormalTimeSlot.setText("正常时段(" + mParkOrderInfo.getLow_time() + ")");
        mNormalTimeSlotUnitPrice.setText(normalUnitPrice + "元/时");
        mNormalTimeSlotFee.setText(normalTime + "小时*" + normalUnitPrice + "元/时=" + normalFee + "元");
        mHighTimeSlot.setText("高峰时段(" + mParkOrderInfo.getHigh_time() + ")");
        mHighTimeSlotUnitPrice.setText(highUnitPrice + "元/时");
        mHighTimeSlotFee.setText(highTime + "小时*" + highUnitPrice + "元/时=" + highFee + "元");

        if (overtimeTime > 0) {
            mOvertimeSlot.setText("超时时段(" + DateUtil.getDayToMinute(mParkOrderInfo.getOrderEndTime(), mParkOrderInfo.getExtensionTime()) +
                    " - " + DateUtil.getDayToMinute(mParkOrderInfo.getParkEndTime()) + ")");
        }
        mOvertimeSlotUnitPrice.setText(overtimeUnitPrice + "元/小时");
        mOvertimeSlotFee.setText(overtimeTime + "小时*" + overtimeUnitPrice + "元/时=" + overtimeFee + "元");

        if (!"-1".equals(mParkOrderInfo.getDiscount().get(0).getId())) {
            discountDecrease = Double.valueOf(mParkOrderInfo.getDiscount().get(0).getDiscount());
            mDiscountDecrease.setText("-" + discountDecrease + "元");
        } else {
            mDiscountDecrease.setText("未使用优惠券");
        }

        if (mParkOrderInfo.getMonthlyCardDiscount() != 1) {
            monthlyCardDecrease = Double.parseDouble(decimalFormat.format(
                    (normalFee + highFee + overtimeFee - discountDecrease) * (1 - mParkOrderInfo.getMonthlyCardDiscount())));
            mMonthlyCardDisount.setText("月卡" + DateUtil.deleteZero(mParkOrderInfo.getMonthlyCardDiscount() * 10) + "折");
            mMonthlyCardDecrease.setText("-" + monthlyCardDecrease + "元");
        } else {
            mMonthlyCardDisount.setText("未使用月卡");
        }

        StringBuilder totalFee = new StringBuilder();
        if (normalFee > 0) {
            totalFee.append(normalFee);
        }
        if (highFee > 0) {
            if (normalFee > 0) {
                totalFee.append("+");
            }
            totalFee.append(highFee);
        }
        if (overtimeFee > 0) {
            if (totalFee.length() > 0) {
                totalFee.append("+");
            }
            totalFee.append(overtimeFee);
        }
        if (discountDecrease > 0) {
            totalFee.append("-");
            totalFee.append(discountDecrease);
        }
        if (monthlyCardDecrease > 0) {
            totalFee.append("-");
            totalFee.append(monthlyCardDecrease);
        }
        totalFee.append("=");
        totalFee.append(normalFee + highFee + overtimeFee - discountDecrease - monthlyCardDecrease);
        totalFee.append("元");
        mTotalPrice.setText(totalFee);
    }

    @NonNull
    @Override
    protected String title() {
        return "计费详情";
    }

}
