package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.PayActivity;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.fragment.addParkSpace.DepositPaymentFragment;
import com.tuzhao.fragment.addParkSpace.ParkSpaceInfoFragment;
import com.tuzhao.fragment.addParkSpace.TimeSettingFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.AppointmentInstallInfo;
import com.tuzhao.info.ParkSpaceInfo;
import com.tuzhao.info.ShareTimeInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/6/4.
 */
public class AddParkSpaceActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private View mParkSpaceInfoStartLine;

    private ImageView mParkSpaceInfoIv;

    private View mParkSpaceInfoEndLine;

    private View mTimeSettingStartLine;

    private ImageView mTimeSettingIv;

    private View mTimeSettingEndLine;

    private TextView mTimeSettingTv;

    private View mDepositPaymentStartLine;

    private ImageView mDepositPaymentIv;

    private View mDepositPaymentEndLine;

    private TextView mDepositPaymentTv;

    private ParkSpaceInfoFragment mParkSpaceInfoFragment;

    private TimeSettingFragment mTimeSettingFragment;

    private DepositPaymentFragment mDepositPaymentFragment;

    private int mCurrentPosition;

    private String mCityCode;

    @Override
    protected int resourceId() {
        return R.layout.activity_add_park_space_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkSpaceInfoStartLine = findViewById(R.id.park_space_info_start_line);
        mParkSpaceInfoIv = findViewById(R.id.park_space_info_iv);
        mParkSpaceInfoEndLine = findViewById(R.id.park_space_info_end_line);
        mTimeSettingStartLine = findViewById(R.id.time_setting_start_line);
        mTimeSettingIv = findViewById(R.id.time_setting_iv);
        mTimeSettingEndLine = findViewById(R.id.time_setting_end_line);
        mTimeSettingTv = findViewById(R.id.time_setting_tv);
        mDepositPaymentStartLine = findViewById(R.id.deposit_payment_start_line);
        mDepositPaymentIv = findViewById(R.id.deposit_payment_iv);
        mDepositPaymentEndLine = findViewById(R.id.deposit_payment_end_line);
        mDepositPaymentTv = findViewById(R.id.deposit_payment_tv);

        findViewById(R.id.park_space_info_cl).setOnClickListener(this);
        findViewById(R.id.time_setting_cl).setOnClickListener(this);
        findViewById(R.id.deposit_payment_cl).setOnClickListener(this);
        findViewById(R.id.next_step_tv).setOnClickListener(this);

        ImageUtil.showPic(mParkSpaceInfoIv, R.drawable.ic_yellowpark);
        ImageUtil.showPic(mTimeSettingIv, R.drawable.ic_graytime);
        ImageUtil.showPic(mDepositPaymentIv, R.drawable.ic_graymoney);

        mParkSpaceInfoFragment = new ParkSpaceInfoFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.add_park_space_container, mParkSpaceInfoFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void initData() {
        mCityCode = getIntent().getStringExtra(ConstansUtil.CITY_CODE);
        IntentObserable.registerObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
    }

    @NonNull
    @Override
    protected String title() {
        return "添加车位";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.park_space_info_cl:
                if (mCurrentPosition != 0) {
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.add_park_space_container, mParkSpaceInfoFragment);
                    fragmentTransaction.commit();
                    mCurrentPosition = 0;
                    setTimeSettingChoose(false);
                    //setDepositPaymentChoose(false);
                }
                break;
            case R.id.time_setting_cl:
                if (mTimeSettingFragment == null) {
                    showFiveToast("请先完成车位信息哦");
                } else {
                    if (mCurrentPosition == 0) {
                        nextStep();
                    } else if (mCurrentPosition == 2) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.add_park_space_container, mTimeSettingFragment);
                        transaction.commit();
                        mCurrentPosition = 1;
                        setTimeSettingChoose(true);
                        //setDepositPaymentChoose(false);
                    }
                }
                break;
            case R.id.deposit_payment_cl:
                if (mDepositPaymentFragment == null) {
                    if (mTimeSettingFragment == null) {
                        showFiveToast("请先完成车位信息哦");
                    } else {
                        showFiveToast("请先完成时间设置哦");
                    }
                }
                break;
            case R.id.next_step_tv:
                nextStep();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentPosition == 1) {
            transaction.replace(R.id.add_park_space_container, mParkSpaceInfoFragment);
            transaction.commit();
            mCurrentPosition = 0;
            setTimeSettingChoose(false);
        } else if (mCurrentPosition == 2) {
            transaction.replace(R.id.add_park_space_container, mTimeSettingFragment);
            transaction.commit();
            mCurrentPosition = 1;
            setTimeSettingChoose(true);
            //setDepositPaymentChoose(false);
        } else {
            super.onBackPressed();
        }
    }

    private void nextStep() {
        Intent intent = new Intent();
        intent.setAction(ConstansUtil.NEXT_STEP);
        intent.putExtra(ConstansUtil.POSITION, mCurrentPosition);
        IntentObserable.dispatch(intent);
    }

    private void setTimeSettingChoose(boolean isChoose) {
        if (isChoose) {
            mTimeSettingStartLine.setBackgroundColor(Color.parseColor("#ffcc30"));
            mTimeSettingEndLine.setBackgroundColor(Color.parseColor("#ffcc30"));
            mTimeSettingTv.setTextColor(Color.parseColor("#323232"));
            ImageUtil.showPic(mTimeSettingIv, R.drawable.ic_yellowtime);
        } else {
            mTimeSettingStartLine.setBackgroundColor(Color.parseColor("#cccccc"));
            mTimeSettingEndLine.setBackgroundColor(Color.parseColor("#cccccc"));
            mTimeSettingTv.setTextColor(Color.parseColor("#cccccc"));
            ImageUtil.showPic(mTimeSettingIv, R.drawable.ic_graytime);
        }
    }

    /*private void setDepositPaymentChoose(boolean isChoose) {
        if (isChoose) {
            mDepositPaymentStartLine.setBackgroundColor(Color.parseColor("#ffcc30"));
            mDepositPaymentEndLine.setBackgroundColor(Color.parseColor("#ffcc30"));
            mDepositPaymentTv.setTextColor(Color.parseColor("#ffcc30"));
            ImageUtil.showPic(mDepositPaymentIv, R.drawable.ic_yellowmoney);
        } else {
            mDepositPaymentStartLine.setBackgroundColor(Color.parseColor("#cccccc"));
            mDepositPaymentEndLine.setBackgroundColor(Color.parseColor("#cccccc"));
            mDepositPaymentTv.setTextColor(Color.parseColor("#cccccc"));
            ImageUtil.showPic(mDepositPaymentIv, R.drawable.ic_graymoney);
        }
    }*/

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.JUMP_TO_TIME_SETTING:
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    if (mTimeSettingFragment == null) {
                        mTimeSettingFragment = new TimeSettingFragment();
                    }
                    fragmentTransaction.replace(R.id.add_park_space_container, mTimeSettingFragment);
                    fragmentTransaction.commit();
                    mCurrentPosition = 1;
                    setTimeSettingChoose(true);
                    break;
                case ConstansUtil.JUMP_TO_DEPOSIT_PAYMENT:

                   /* FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    if (mDepositPaymentFragment == null) {
                        mDepositPaymentFragment = new DepositPaymentFragment();
                    }
                    transaction.replace(R.id.add_park_space_container, mDepositPaymentFragment);
                    transaction.commit();
                    mCurrentPosition = 2;
                    setDepositPaymentChoose(true);*/

                    break;
            }
        }
    }

    private void addUserPark() {
        showLoadingDialog("正在提交");
        ParkSpaceInfo parkSpaceInfo = mParkSpaceInfoFragment.getParkSpaceInfo();
        StringBuilder propertyPhoto = new StringBuilder(parkSpaceInfo.getPropertyFirstUrl());
        propertyPhoto.append(",");
        if (!parkSpaceInfo.getPropertySecondUrl().equals("-1")) {
            propertyPhoto.append(parkSpaceInfo.getPropertySecondUrl());
            propertyPhoto.append(",");
        }
        if (!parkSpaceInfo.getPropertyThirdUrl().equals("-1")) {
            propertyPhoto.append(parkSpaceInfo.getPropertyThirdUrl());
            propertyPhoto.append(",");
        }

        ShareTimeInfo shareTimeInfo = mTimeSettingFragment.getShareTimeInfo();
        Calendar calendar = Calendar.getInstance();
        String appointmentDate = shareTimeInfo.getAppointmentDate();
        if (appointmentDate.startsWith("明天")) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        } else if (appointmentDate.startsWith("后天")) {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
        } else {
            calendar.set(Calendar.MONTH, Integer.valueOf(appointmentDate.substring(0, appointmentDate.indexOf("月"))));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(appointmentDate.substring(appointmentDate.indexOf("月") + 1,
                    appointmentDate.indexOf("日"))));
        }
        appointmentDate = DateUtil.getCalendarYearToDay(calendar) + " " +
                appointmentDate.substring(appointmentDate.length() - 2, appointmentDate.length());

        getOkGo(HttpConstants.addUserPark)
                .params("parkspace_id", parkSpaceInfo.getParkLotId())
                .params("citycode", mCityCode)
                .params("address_memo", parkSpaceInfo.getParkSpaceDescription())
                .params("applicant_name", parkSpaceInfo.getRealName())
                .params("idCardPhoto", parkSpaceInfo.getIdCardPositiveUrl() + "," + parkSpaceInfo.getIdCardNegativeUrl())
                .params("propertyPhoto", propertyPhoto.toString())
                .params("available_date", shareTimeInfo.getShareDate())
                .params("dayRent", shareTimeInfo.isHourRent() ? 0 : 1)
                .params("shareDay", shareTimeInfo.getShareDay())
                .params("available_time", shareTimeInfo.getEveryDayShareTime())
                .params("pauseShareDate", shareTimeInfo.getPauseShareDate())
                .params("install_time", appointmentDate)
                .execute(new JsonCallback<Base_Class_Info<AppointmentInstallInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<AppointmentInstallInfo> o, Call call, Response response) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstansUtil.PAY_TYPE, "1");
                        bundle.putString(ConstansUtil.PAY_MONEY, o.data.getSum() + "元");
                        bundle.putString(ConstansUtil.PARK_SPACE_ID, o.data.getParkSpaceId());
                        bundle.putString(ConstansUtil.CITY_CODE, mCityCode);
                        startActivity(PayActivity.class, bundle);
                        finish();
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
