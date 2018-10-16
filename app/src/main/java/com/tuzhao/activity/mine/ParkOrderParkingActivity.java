package com.tuzhao.activity.mine;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.navi.model.NaviLatLng;
import com.tuzhao.R;
import com.tuzhao.activity.ParkspaceDetailActivity;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.jiguang_notification.MyReceiver;
import com.tuzhao.activity.jiguang_notification.OnLockListenerAdapter;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DeviceUtils;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.PollingUtil;
import com.tuzhao.utils.ViewUtil;

import java.text.DecimalFormat;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/10/7.
 */
public class ParkOrderParkingActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private ParkOrderInfo mParkOrderInfo;

    private TextView mOrderAmount;

    private TextView mAlreadyParkTime;

    private TextView mGraceTime;

    private TextView mOvertimeDuration;

    private TextView mLatestLeaveTime;

    private TextView mTimeoutBilling;

    private MapView mMapView;

    private AMap mAMap;

    private TextView mCarNumber;

    private TextView mParkLotName;

    private TextView mParkSpaceNumber;

    private TextView mParkSpaceDescription;

    private TextView mAppointmentStartParkTime;

    private TextView mAppointmentEndParkTime;

    private TextView mActualStartParkTime;

    private TextView mOrderNumber;

    private TextView mOrderDate;

    private PollingUtil mPollingUtil;

    private DecimalFormat mDecimalFormat;

    private String mOrderFee;

    /**
     * 超时费用
     */
    private double mFineFee;

    /**
     * 预约时间内的全部费用
     */
    private double mTotalAppointmentFee;

    /**
     * 预约时间加上顺延时长的总费用
     */
    private double mAppointmentWithExtendsionFee;

    @Override
    protected int resourceId() {
        return R.layout.activity_park_order_parking_layout;
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
        mAlreadyParkTime = findViewById(R.id.already_park_time);
        mGraceTime = findViewById(R.id.grace_time);
        mOvertimeDuration = findViewById(R.id.overtime_duration);
        mLatestLeaveTime = findViewById(R.id.latest_leave_time);
        mTimeoutBilling = findViewById(R.id.timeout_billing);
        mParkLotName = findViewById(R.id.park_lot_name);
        mParkSpaceNumber = findViewById(R.id.park_space_number);
        mParkSpaceDescription = findViewById(R.id.park_space_description);
        mAppointmentStartParkTime = findViewById(R.id.appointment_start_park_time);
        mAppointmentEndParkTime = findViewById(R.id.appointment_end_park_time);
        mActualStartParkTime = findViewById(R.id.actual_start_park_time);
        mOrderNumber = findViewById(R.id.order_number);
        mOrderDate = findViewById(R.id.order_date_tv);

        mMapView = findViewById(R.id.order_mv);
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        initMapView();

        mParkLotName.setOnClickListener(this);
        findViewById(R.id.park_lot_name_av).setOnClickListener(this);
        findViewById(R.id.billing_rules_cl).setOnClickListener(this);
        findViewById(R.id.billing_rules_av).setOnClickListener(this);
        findViewById(R.id.order_complaint_cl).setOnClickListener(this);
        findViewById(R.id.contact_service_cl).setOnClickListener(this);
        findViewById(R.id.navigation_cl).setOnClickListener(this);
        findViewById(R.id.copy_order_number).setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        showCantCancelLoadingDialog();
        mDecimalFormat = new DecimalFormat("0.00");
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
            case R.id.order_complaint_cl:
                startActivity(OrderComplaintActivity.class, ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                break;
            case R.id.contact_service_cl:
                ViewUtil.contactService(ParkOrderParkingActivity.this);
                break;
            case R.id.park_lot_name:
            case R.id.park_lot_name_av:
                startActivity(ParkspaceDetailActivity.class, "parkspace_id", mParkOrderInfo.getParkLotId(),
                        "city_code", mParkOrderInfo.getCityCode());
                break;
            case R.id.navigation_cl:
                startNavigation();
                break;
            case R.id.copy_order_number:
                copyOrderNumber();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (mPollingUtil != null) {
            mPollingUtil.cancel();
        }
        MyReceiver.removeLockListener(mParkOrderInfo.getLockId());
        IntentObserable.unregisterObserver(this);
    }

    private void initMapView() {
        final LatLng mLatLng = new LatLng(mParkOrderInfo.getLatitude() + 0.0005, mParkOrderInfo.getLongitude());
        mAMap.getUiSettings().setRotateGesturesEnabled(false);
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));
                return true;
            }
        });

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(mParkOrderInfo.getLatitude(), mParkOrderInfo.getLongitude()))
                .title(mParkOrderInfo.getParkLotName());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.yellow_gps));
        Marker marker = mAMap.addMarker(markerOptions);
        marker.hideInfoWindow();
        ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1);
        animation.setDuration(500);
        marker.setAnimation(animation);
        marker.startAnimation();
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
        initPolling();

        mCarNumber.setText(mParkOrderInfo.getCarNumber());
        mGraceTime.setText(Integer.valueOf(mParkOrderInfo.getExtensionTime()) / 60 + "分钟");

        Calendar calendar = DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrderEndTime());
        calendar.add(Calendar.SECOND, Integer.parseInt(mParkOrderInfo.getExtensionTime()));
        mLatestLeaveTime.setText(DateUtil.getCalendarMonthToMinuteWithText(calendar));

        String timeoutBilling = "超时部分按" + mParkOrderInfo.getFine() + "元/时计费";
        SpannableString spannableString = new SpannableString(timeoutBilling);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0101")), 5, timeoutBilling.indexOf("/"), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTimeoutBilling.setText(spannableString);

        mParkLotName.setText(mParkOrderInfo.getParkLotName());
        mParkSpaceNumber.setText(mParkOrderInfo.getParkNumber());
        mParkSpaceDescription.setText(mParkOrderInfo.getParkSpaceLocation());
        mAppointmentStartParkTime.setText(DateUtil.deleteSecond(mParkOrderInfo.getOrderStartTime()));
        mAppointmentEndParkTime.setText(DateUtil.deleteSecond(mParkOrderInfo.getOrderEndTime()));
        mActualStartParkTime.setText(DateUtil.deleteSecond(mParkOrderInfo.getPark_start_time()));

        mOrderNumber.setText(mParkOrderInfo.getOrder_number());
        mOrderDate.setText("下单时间：" + DateUtil.deleteSecond(mParkOrderInfo.getOrderTime()));

        initLockListener();
    }

    private void initPolling() {
        mPollingUtil = new PollingUtil(1000 * 60, new PollingUtil.OnTimeCallback() {
            @Override
            public void onTime() {
                updateOrderInfo();
            }
        });
        mPollingUtil.start();
    }

    /**
     * 每分钟更新一次订单金额，已停时长，超时时长
     */
    private void updateOrderInfo() {
        int extensionTime = Integer.valueOf(mParkOrderInfo.getExtensionTime());
        double parkFee = 0;
        Calendar nowCalendar = Calendar.getInstance();
        if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrderStartTime()).compareTo(
                DateUtil.getYearToSecondCalendar(mParkOrderInfo.getParkStartTime())) > 0) {
            //提前停车
            if (nowCalendar.compareTo(DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrderStartTime())) < 0) {
                //还没到预约开始停车时间,计算开始停车时间到当前时间的费用
                parkFee = DateUtil.caculateParkFee(DateUtil.deleteSecond(mParkOrderInfo.getParkStartTime()), DateUtil.getCurrentYearToMinutes(),
                        mParkOrderInfo.getHigh_time(), Double.valueOf(mParkOrderInfo.getHigh_fee()), Double.valueOf(mParkOrderInfo.getLow_fee()));
            } else {
                //到了预约开始停车时间，则计算开始停车时间到预约开始停车时间之间的费用
                parkFee = DateUtil.caculateParkFee(DateUtil.deleteSecond(mParkOrderInfo.getParkStartTime()),
                        DateUtil.deleteSecond(mParkOrderInfo.getOrderStartTime()),
                        mParkOrderInfo.getHigh_time(), Double.valueOf(mParkOrderInfo.getHigh_fee()), Double.valueOf(mParkOrderInfo.getLow_fee()));
            }
        }

        if (DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrderEndTime(), mParkOrderInfo.getExtensionTime()).compareTo(
                nowCalendar) < 0) {
            //超时

            //计算在预约时间内的费用
            if (mAppointmentWithExtendsionFee == 0) {
                mAppointmentWithExtendsionFee = DateUtil.caculateParkFee(DateUtil.deleteSecond(mParkOrderInfo.getOrderStartTime()),
                        DateUtil.getYearToMinute(mParkOrderInfo.getOrderEndTime(), extensionTime),
                        mParkOrderInfo.getHigh_time(), Double.valueOf(mParkOrderInfo.getHigh_fee()), Double.valueOf(mParkOrderInfo.getLow_fee()));
            }
            parkFee += mAppointmentWithExtendsionFee;

            //计算超时部分的费用
            Calendar startOverTimeCalendar = DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrderEndTime(), mParkOrderInfo.getExtensionTime());
            mFineFee = DateUtil.getCalendarDistance(startOverTimeCalendar, Calendar.getInstance()) * Double.valueOf(mParkOrderInfo.getFine()) / 60;
            parkFee += mFineFee;
            mOvertimeDuration.setText(DateUtil.getDistanceForDayHourMinuteAddStart(mParkOrderInfo.getOrderEndTime(),
                    DateUtil.getCurrentYearToSecond(), mParkOrderInfo.getExtensionTime()));
        } else {
            //没有超时
            if (UserManager.getInstance().getUserInfo().getLeaveEarlyTime() > 0) {
                //可以提前结单
                if (Calendar.getInstance().compareTo(DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrderStartTime())) > 0) {
                    //在预约停车的时间范围内,则停了多久就算多久的钱
                    parkFee += DateUtil.caculateParkFee(DateUtil.deleteSecond(mParkOrderInfo.getOrderStartTime()), DateUtil.getCurrentYearToMinutes(),
                            mParkOrderInfo.getHigh_time(), Double.valueOf(mParkOrderInfo.getHigh_fee()), Double.valueOf(mParkOrderInfo.getLow_fee()));
                }
            } else {
                //不可以提前结单,则停车费用按预约时间的全额费用算

                //计算预约时间的全额费用
                if (mTotalAppointmentFee == 0) {
                    //因为会每分钟更新一次，如果已经算出了全额费用就不再算了
                    mTotalAppointmentFee = DateUtil.caculateParkFee(DateUtil.deleteSecond(mParkOrderInfo.getOrderStartTime()),
                            DateUtil.deleteSecond(mParkOrderInfo.getOrderEndTime()),
                            mParkOrderInfo.getHigh_time(), Double.valueOf(mParkOrderInfo.getHigh_fee()), Double.valueOf(mParkOrderInfo.getLow_fee()));
                }
                parkFee += mTotalAppointmentFee;

                if (Calendar.getInstance().compareTo(DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrderEndTime())) > 0) {
                    //计算在宽限的时间停车的费用内
                    parkFee += DateUtil.caculateParkFee(DateUtil.deleteSecond(mParkOrderInfo.getOrderEndTime()), DateUtil.getCurrentYearToMinutes(),
                            mParkOrderInfo.getHigh_time(), Double.valueOf(mParkOrderInfo.getHigh_fee()), Double.valueOf(mParkOrderInfo.getLow_fee()));
                }
            }
        }

        mAlreadyParkTime.setText(DateUtil.getDistanceForDayHourMinute(mParkOrderInfo.getParkStartTime(), DateUtil.getCurrentYearToSecond()));
        mOrderFee = mDecimalFormat.format(parkFee);
        mOrderAmount.setText(mOrderFee);
    }

    private void initLockListener() {
        OnLockListenerAdapter lockListenerAdapter = new OnLockListenerAdapter() {
            @Override
            public void closeSuccess() {
                notifyFinishPark();
            }

            @Override
            public void closeFailed() {
                showDialog("关锁失败，是否需要联系客服?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ViewUtil.contactService(ParkOrderParkingActivity.this);
                    }
                });
            }

            @Override
            public void closeFailedHaveCar() {
                showDialog("关锁失败，车尚未完全离开车位", null);
            }
        };

        MyReceiver.addLockListener(mParkOrderInfo.getLockId(), lockListenerAdapter);
    }

    private void notifyFinishPark() {
        Intent intent = new Intent();
        intent.setAction(ConstansUtil.FINISH_PARK);
        Bundle bundle = new Bundle();
        mParkOrderInfo.setOrderStatus("3");
        mParkOrderInfo.setOrderFee(mOrderFee);
        mParkOrderInfo.setFineFee(String.valueOf(mFineFee));
        mParkOrderInfo.setPark_end_time(DateUtil.getCurrentYearToSecond());
        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, bundle);
        IntentObserable.dispatch(intent);

        showFiveToast("已结束停车");
        startActivity(ParkOrderPayActivity.class, ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
        finish();
    }

    private void startNavigation() {
        if (LocationManager.getInstance().hasLocation()) {
            Intent intent = new Intent(this, NavigationActivity.class);
            intent.putExtra("gps", true);
            intent.putExtra("start", new NaviLatLng(LocationManager.getInstance().getmAmapLocation().getLatitude(), LocationManager.getInstance().getmAmapLocation().getLongitude()));
            intent.putExtra("end", new NaviLatLng(mParkOrderInfo.getLatitude(), mParkOrderInfo.getLongitude()));
            intent.putExtra(ConstansUtil.PARK_LOT_NAME, mParkOrderInfo.getParkLotName());
            intent.putExtra("address", mParkOrderInfo.getParkSpaceLocation());
            startActivity(intent);
        } else {
            if (DeviceUtils.isGpsOpen(ParkOrderParkingActivity.this)) {
                showFiveToast("请稍后重试");
            } else {
                showFiveToast("定位失败，请开启GPS后重试");
            }
        }
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

    @Override
    public void onReceive(Intent intent) {
        if (ConstansUtil.DIALOG_ON_BACK_PRESS.equals(intent.getAction())) {
            dismmisLoadingDialog();
            finish();
        }
    }

}
