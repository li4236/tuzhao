package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import java.util.Calendar;

/**
 * Created by juncoder on 2018/6/12.
 * <p>
 * 停车订单的计费规则
 * </p>
 */
public class BillingRuleActivity extends BaseStatusActivity {

    private TextView mParkLotName;

    private TextView mHighTime;

    private TextView mHighFee;

    private TextView mLowTime;

    private TextView mLowFee;

    private TextView mOvertimeFee;

    private ParkOrderInfo mParkOrderInfo;

    @Override
    protected int resourceId() {
        return R.layout.activity_billing_rules_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkOrderInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_ORDER_INFO);
        if (mParkOrderInfo == null) {
            finish();
        }

        mParkLotName = findViewById(R.id.park_lot_name);
        mHighTime = findViewById(R.id.high_time);
        mHighFee = findViewById(R.id.high_fee);
        mLowTime = findViewById(R.id.low_time);
        mLowFee = findViewById(R.id.low_fee);
        mOvertimeFee = findViewById(R.id.overtime_fee);
    }

    @Override
    protected void initData() {
        mParkLotName.setText(mParkOrderInfo.getParkLotName());

        //高峰时段的时间
        String highTime = mParkOrderInfo.getHigh_time();
        int position = highTime.indexOf(" - ");
        Calendar startCalendar = DateUtil.getSpecialCalendar(highTime.substring(0, position));
        Calendar endCalendar = DateUtil.getSpecialCalendar(highTime.substring(position + 3, highTime.length()));
        if (startCalendar.compareTo(endCalendar) >= 0) {
            //如果开始时间比结束时间晚，则结束时间为第二天的xx时段
            highTime = highTime.replace(highTime.substring(position + 3, highTime.length()),
                    "次日" + highTime.substring(position + 3, highTime.length()));
        }
        mHighTime.setText(highTime);

        String highFee = DateUtil.deleteZero(mParkOrderInfo.getHigh_fee()) + "元/小时";
        mHighFee.setText(highFee);

        String lowTime = mParkOrderInfo.getLow_time();
        position = lowTime.indexOf(" - ");
        startCalendar = DateUtil.getSpecialCalendar(lowTime.substring(0, position));
        endCalendar = DateUtil.getSpecialCalendar(lowTime.substring(position + 3, lowTime.length()));
        if (startCalendar.compareTo(endCalendar) >= 0) {
            lowTime = lowTime.replace(lowTime.substring(position + 3, lowTime.length()),
                    "次日" + lowTime.substring(position + 3, lowTime.length()));
        }
        mLowTime.setText(lowTime);

        String lowFee = DateUtil.deleteZero(mParkOrderInfo.getLow_fee()) + "元/小时";
        mLowFee.setText(lowFee);

        String overTimeFee = DateUtil.deleteZero(mParkOrderInfo.getFine()) + "元/小时";
        mOvertimeFee.setText(overTimeFee);
    }

    @NonNull
    @Override
    protected String title() {
        return "计费规则";
    }

}
