package com.tuzhao.activity.mine;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.ParkspaceDetailActivity;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.ViewUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/10/8.
 */
public class ParkOrderCancelActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private ParkOrderInfo mParkOrderInfo;

    private TextView mCarNumber;

    private TextView mParkSpaceNumber;

    private TextView mParkLotName;

    private TextView mParkSpaceDescription;

    private TextView mApointmentStartParkTime;

    private TextView mAppointmentParkDuration;

    private TextView mGraceTime;

    private TextView mOrderNumber;

    private TextView mOrderDate;

    @Override
    protected int resourceId() {
        return R.layout.activity_park_order_cancel_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkOrderInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_ORDER_INFO);
        if (mParkOrderInfo == null) {
            showFiveToast("获取订单信息失败，请稍后重试");
            finish();
        }

        mCarNumber = findViewById(R.id.car_number);
        mParkSpaceNumber = findViewById(R.id.park_space_number);
        mParkLotName = findViewById(R.id.park_lot_name);
        mParkSpaceDescription = findViewById(R.id.park_space_description);
        mApointmentStartParkTime = findViewById(R.id.appointment_start_park_time);
        mAppointmentParkDuration = findViewById(R.id.appointment_park_duration);
        mGraceTime = findViewById(R.id.grace_time);
        mOrderNumber = findViewById(R.id.order_number);
        mOrderDate = findViewById(R.id.order_date_tv);

        findViewById(R.id.delete_order).setOnClickListener(this);
        findViewById(R.id.appointment_again).setOnClickListener(this);
        findViewById(R.id.contact_service_cl).setOnClickListener(this);
        findViewById(R.id.copy_order_number).setOnClickListener(this);
    }

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
            case R.id.delete_order:
                showDialog("确定删除订单吗", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletelOrder();
                    }
                });
                break;
            case R.id.appointment_again:
                startActivity(ParkspaceDetailActivity.class, "parkspace_id", mParkOrderInfo.getParkLotId(),
                        "city_code", mParkOrderInfo.getCityCode());
                break;
            case R.id.contact_service_cl:
                ViewUtil.contactService(ParkOrderCancelActivity.this);
                break;
            case R.id.copy_order_number:
                ViewUtil.clipContent(ParkOrderCancelActivity.this, getText(mOrderNumber));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentObserable.unregisterObserver(this);
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
        mCarNumber.setText(mParkOrderInfo.getCarNumber());
        mParkSpaceNumber.setText(mParkOrderInfo.getParkNumber());
        mParkLotName.setText(mParkOrderInfo.getParkLotName());
        mParkSpaceDescription.setText(mParkOrderInfo.getParkSpaceLocation());
        mApointmentStartParkTime.setText(DateUtil.deleteSecond(mParkOrderInfo.getOrderStartTime()));
        mAppointmentParkDuration.setText(DateUtil.getDistanceForDayHourMinute(mParkOrderInfo.getOrderStartTime(), mParkOrderInfo.getOrderEndTime()));
        mGraceTime.setText(UserManager.getInstance().getUserInfo().getLeave_time() + "分钟");
        mOrderNumber.setText(mParkOrderInfo.getOrder_number());
        mOrderDate.setText("下单时间：" + DateUtil.deleteSecond(mParkOrderInfo.getOrderTime()));
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

    @Override
    public void onReceive(Intent intent) {
        if (ConstansUtil.DIALOG_ON_BACK_PRESS.equals(intent.getAction())) {
            dismmisLoadingDialog();
            finish();
        }
    }

}
