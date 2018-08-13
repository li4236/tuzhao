package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Space_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/6/12.
 */
public class BillingRuleActivity extends BaseStatusActivity {

    private TextView mParkLotName;

    private TextView mHighTime;

    private TextView mHighFee;

    private TextView mLowTime;

    private TextView mLowFee;

    private TextView mOvertimeFee;

    private String mParkLotId;

    private String mCityCode;

    @Override
    protected int resourceId() {
        return R.layout.activity_billing_rules_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkLotName = findViewById(R.id.park_lot_name);
        mHighTime = findViewById(R.id.high_time);
        mHighFee = findViewById(R.id.high_fee);
        mLowTime = findViewById(R.id.low_time);
        mLowFee = findViewById(R.id.low_fee);
        mOvertimeFee = findViewById(R.id.overtime_fee);
    }

    @Override
    protected void initData() {
        super.initData();
        mParkLotId = getIntent().getStringExtra(ConstansUtil.PARK_LOT_ID);
        mCityCode = getIntent().getStringExtra(ConstansUtil.CITY_CODE);
        getParkLotData();
    }

    @NonNull
    @Override
    protected String title() {
        return "计费规则";
    }

    private void getParkLotData() {
        getOkGo(HttpConstants.getOneParkSpaceData)
                .params("parkspace_id", mParkLotId)
                .params("citycode", mCityCode)
                .params("ad_position", "2")
                .execute(new JsonCallback<Base_Class_Info<Park_Space_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Park_Space_Info> o, Call call, Response response) {
                        mParkLotName.setText(o.data.getParkLotName());

                        String highTime = o.data.getHigh_time();
                        int position = highTime.indexOf(" - ");
                        Calendar startCalendar = DateUtil.getSpecialCalendar(highTime.substring(0, position));
                        Calendar endCalendar = DateUtil.getSpecialCalendar(highTime.substring(position + 3, highTime.length()));
                        if (startCalendar.compareTo(endCalendar) >= 0) {
                            highTime = highTime.replace(highTime.substring(position + 3, highTime.length()),
                                    "次日" + highTime.substring(position + 3, highTime.length()));
                        }
                        mHighTime.setText(highTime);

                        String highFee = DateUtil.deleteZero(o.data.getHigh_fee()) + "元/小时";
                        mHighFee.setText(highFee);

                        String lowTime = o.data.getLow_time();
                        position = lowTime.indexOf(" - ");
                        startCalendar = DateUtil.getSpecialCalendar(lowTime.substring(0, position));
                        endCalendar = DateUtil.getSpecialCalendar(lowTime.substring(position + 3, lowTime.length()));
                        if (startCalendar.compareTo(endCalendar) >= 0) {
                            Log.e(TAG, "onSuccess: startCalendar:"+DateUtil.getCalenarYearToMinutes(startCalendar) );
                            Log.e(TAG, "onSuccess: endCalendar:"+DateUtil.getCalenarYearToMinutes(endCalendar) );
                            lowTime = lowTime.replace(lowTime.substring(position + 3, lowTime.length()),
                                    "次日" + lowTime.substring(position + 3, lowTime.length()));
                        }
                        mLowTime.setText(lowTime);

                        String lowFee = DateUtil.deleteZero(o.data.getLow_fee()) + "元/小时";
                        mLowFee.setText(lowFee);

                        String overTimeFee = DateUtil.deleteZero(o.data.getFine()) + "元/小时";
                        mOvertimeFee.setText(overTimeFee);
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

}
