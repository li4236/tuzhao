package com.tuzhao.activity.mine;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.ParkspaceDetailActivity;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.activity.jiguang_notification.MyReceiver;
import com.tuzhao.activity.jiguang_notification.OnLockListenerAdapter;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.JsonCodeCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.OpenLockDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DataUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DeviceUtils;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.ViewUtil;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/9/29.
 */
public class ParkOrderAppointmentActivity extends BaseStatusActivity implements View.OnClickListener {

    private ParkOrderInfo mParkOrderInfo;

    private TextView mStartParkDate;

    private TextView mCarNumber;

    private TextView mParkSpaceNumber;

    private TextView mParkLotName;

    private TextView mParkSpaceDescription;

    private TextView mActualStartParkTime;

    private TextView mAcutalEndParkTime;

    private TextView mGraceTime;

    private TextView mOrderNumber;

    private TextView mOrderDate;

    private MapView mMapView;

    private AMap mAMap;

    private boolean mIsOpening;

    private OpenLockDialog mOpenLockDialog;

    private List<Park_Info> mParkSpaceList;

    private List<Park_Info> mCanParkList;

    @Override
    protected int resourceId() {
        return R.layout.activity_park_order_appointment_detail_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkOrderInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_ORDER_INFO);
        if (mParkOrderInfo == null) {
            showFiveToast("获取订单信息失败，请稍后重试");
            finish();
        }

        mStartParkDate = findViewById(R.id.start_park_date);
        mCarNumber = findViewById(R.id.car_number);
        mParkSpaceNumber = findViewById(R.id.park_space_number);
        mParkLotName = findViewById(R.id.park_lot_name);
        mParkSpaceDescription = findViewById(R.id.park_space_description);
        mActualStartParkTime = findViewById(R.id.actual_start_park_time);
        mAcutalEndParkTime = findViewById(R.id.actual_end_park_time);
        mGraceTime = findViewById(R.id.grace_time);
        mOrderNumber = findViewById(R.id.order_number);
        mOrderDate = findViewById(R.id.order_date_tv);

        mMapView = findViewById(R.id.order_mv);
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        initMapView();

        mParkLotName.setOnClickListener(this);
        findViewById(R.id.park_lot_name_av).setOnClickListener(this);
        findViewById(R.id.cancel_appoint).setOnClickListener(this);
        findViewById(R.id.start_parking).setOnClickListener(this);
        findViewById(R.id.navigation_cl).setOnClickListener(this);
        findViewById(R.id.order_complaint_cl).setOnClickListener(this);
        findViewById(R.id.contact_service_cl).setOnClickListener(this);
        findViewById(R.id.copy_order_number).setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        mStartParkDate.setText("入场时间：" + DateUtil.getYearToMinuteWithText(mParkOrderInfo.getOrderStartTime()));
        mCarNumber.setText(mParkOrderInfo.getCarNumber());
        mParkSpaceNumber.setText(mParkOrderInfo.getParkNumber());
        mParkLotName.setText(mParkOrderInfo.getParkLotName());
        mParkSpaceDescription.setText(mParkOrderInfo.getParkSpaceLocation());
        mActualStartParkTime.setText(DateUtil.deleteSecond(mParkOrderInfo.getOrderStartTime()));
        mAcutalEndParkTime.setText(DateUtil.deleteSecond(mParkOrderInfo.getOrderEndTime()));
        mGraceTime.setText(UserManager.getInstance().getUserInfo().getLeave_time() + "分钟");
        mOrderNumber.setText(mParkOrderInfo.getOrder_number());
        mOrderDate.setText("下单时间：" + DateUtil.deleteSecond(mParkOrderInfo.getOrderTime()));

        registerLockListener();
    }

    @NonNull
    @Override
    protected String title() {
        return "订单详情";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_appoint:
                showDialog("确定取消预约吗", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelAppointment();
                    }
                });
                break;
            case R.id.start_parking:
                if (mIsOpening) {
                    showOpenLockDialog();
                } else {
                    requestOrderPark();
                }
                break;
            case R.id.park_lot_name:
            case R.id.park_lot_name_av:
                startActivity(ParkspaceDetailActivity.class, "parkspace_id", mParkOrderInfo.getParkLotId(),
                        "city_code", mParkOrderInfo.getCityCode());
                break;
            case R.id.navigation_cl:
                startNavigation();
                break;
            case R.id.order_complaint_cl:
                startActivity(OrderComplaintActivity.class, ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
                break;
            case R.id.contact_service_cl:
                ViewUtil.contactService(ParkOrderAppointmentActivity.this);
                break;
            case R.id.copy_order_number:
                ViewUtil.clipContent(ParkOrderAppointmentActivity.this, getText(mOrderNumber));
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
        MyReceiver.removeLockListener(mParkOrderInfo.getLockId());
        if (mOpenLockDialog != null) {
            mOpenLockDialog.setOpenLockStatus(-1);
            mOpenLockDialog.cancel();
        }
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

    private void registerLockListener() {
        OnLockListenerAdapter lockListenerAdapter = new OnLockListenerAdapter() {
            @Override
            public void openSuccess() {
                super.openSuccess();
                mOpenLockDialog.setOpenLockStatus(0);
                mIsOpening = false;
                mOpenLockDialog.cancelOpenLockAnimator();
                if (mOpenLockDialog != null && !mOpenLockDialog.isShowing()) {
                    finishAppointment();
                    showFiveToast("开锁成功");
                }
            }

            @Override
            public void openFailed() {
                super.openFailed();
                mOpenLockDialog.setOpenLockStatus(2);
                mIsOpening = false;
                if (mOpenLockDialog.isShowing()) {
                    mOpenLockDialog.cancelOpenLockAnimator();
                } else {
                    showFiveToast("开锁失败，请稍后重试");
                }
            }

            @Override
            public void openSuccessHaveCar() {
                super.openSuccessHaveCar();
                mOpenLockDialog.setOpenLockStatus(1);
                mIsOpening = false;
                mOpenLockDialog.cancelOpenLockAnimator();
                mOpenLockDialog.dismiss();
                showDialog("车锁已开，因为车位上方有车辆滞留，是否为您重新分配车位？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getParkSpaceList();
                    }
                });
            }
        };

        MyReceiver.addLockListener(mParkOrderInfo.getLockId(), lockListenerAdapter);
    }

    private void showOpenLockDialog() {
        if (mOpenLockDialog == null) {
            mOpenLockDialog = new OpenLockDialog(this, new SuccessCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    if (aBoolean) {
                        finishAppointment();
                    } else {
                        requestOrderPark();
                    }
                }
            });
        }
        mOpenLockDialog.startOpenLock();
    }

    private void finishAppointment() {
        Intent intent = new Intent();
        intent.setAction(ConstansUtil.FINISH_APPOINTMENT);
        Bundle bundle = new Bundle();
        mParkOrderInfo.setOrderStatus("2");
        mParkOrderInfo.setParkStartTime(DateUtil.getCurrentYearToSecond());
        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, bundle);
        IntentObserable.dispatch(intent);

        MyReceiver.removeLockListener(mParkOrderInfo.getLockId());
        startActivity(ParkOrderParkingActivity.class, ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
        finish();
    }

    private void requestOrderPark() {
        showOpenLockDialog();
        mIsOpening = true;
        OkGo.post(HttpConstants.requestOrderPark)
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("orderId", mParkOrderInfo.getId())
                .params("cityCode", mParkOrderInfo.getCityCode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> aVoid, Call call, Response response) {
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mIsOpening = false;
                        mOpenLockDialog.dismiss();
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("设备暂时离线，请稍后重试");
                                    break;
                                case "103":
                                    Calendar calendar = Calendar.getInstance();
                                    if (calendar.compareTo(DateUtil.getYearToSecondCalendar(mParkOrderInfo.getOrderStartTime())) < 0) {
                                        showFiveToast("该车位已有别人预约，请到您预约的时间再停车哦");
                                    } else {
                                        showFiveToast(ConstansUtil.SERVER_ERROR);
                                    }
                                case "102":
                                case "104":
                                case "105":
                                case "106":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                default:
                                    showFiveToast("开锁失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

    private void getParkSpaceList() {
        getOkGo(HttpConstants.getParkList)
                .params("citycode", mParkOrderInfo.getCityCode())
                .params("parkspace_id", mParkOrderInfo.getParkLotId())
                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Park_Info> o, Call call, Response response) {
                        mParkSpaceList = o.data;
                        if (mCanParkList == null) {
                            mCanParkList = new LinkedList<>();
                        }
                        DataUtil.findCanParkList(mCanParkList, mParkSpaceList, DateUtil.getYearToMinute(mParkOrderInfo.getOrderStartTime(), 0),
                                DateUtil.deleteSecond(mParkOrderInfo.getOrderEndTime()));
                        if (mCanParkList.isEmpty()) {
                            showNoParkSpaceDialog();
                        } else {
                            if (mCanParkList.size() > 1) {
                                DataUtil.sortCanParkByIndicator(mCanParkList, DateUtil.deleteSecond(mParkOrderInfo.getOrderEndTime()));
                            }
                            redistributionOrderParkSpace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("获取车位信息失败，请检查网络后返回重试");
                        }
                    }
                });
    }

    private void showNoParkSpaceDialog() {
        TipeDialog tipeDialog = new TipeDialog.Builder(this)
                .setTitle("提示")
                .setMessage("很抱歉，暂无适合您的车位。\n是否需要取消该订单")
                .setNegativeButton("不取消", null)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelAppointment();
                    }
                })
                .create();
        tipeDialog.show();
    }

    /**
     * 请求重新分配车位给该订单
     */
    private void redistributionOrderParkSpace() {
        final StringBuilder readyParkId = new StringBuilder();
        for (int i = 1, size = mCanParkList.size() > 3 ? 3 : mCanParkList.size(); i < size; i++) {
            readyParkId.append(mCanParkList.get(i).getId());
            readyParkId.append(",");
        }
        if (readyParkId.length() > 0) {
            readyParkId.deleteCharAt(readyParkId.length() - 1);
        }

        StringBuilder readyParkUpdateTime = new StringBuilder();
        for (int i = 1, size = mCanParkList.size() > 3 ? 3 : mCanParkList.size(); i < size; i++) {
            readyParkUpdateTime.append(mCanParkList.get(i).getUpdate_time());
            readyParkUpdateTime.append(",");
        }
        if (readyParkUpdateTime.length() > 0) {
            readyParkUpdateTime.deleteCharAt(readyParkId.length() - 1);
        }

        showLoadingDialog("重新分配...");
        getOkGo(HttpConstants.redistributionOrderParkSpace)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("orderId", mParkOrderInfo.getId())
                .params("parkSpaceId", mCanParkList.get(0).getId())
                .params("parkInterval", DateUtil.deleteSecond(mParkOrderInfo.getOrderStartTime()) + "*" +
                        DateUtil.deleteSecond(mParkOrderInfo.getOrderEndTime()))
                .params("parkSpaceUpdateTime", mParkOrderInfo.getUpdateTime())
                .params("alternateParkSpaceId", readyParkId.toString().equals("") ? "-1" : readyParkId.toString())
                .params("alternateParkSpaceUpdateTime", readyParkUpdateTime.toString().equals("") ? "-1" : readyParkUpdateTime.toString())
                .params("cityCode", mParkOrderInfo.getCityCode())
                .execute(new JsonCodeCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        switch (responseData.code) {
                            case "0":
                                dismmisLoadingDialog();
                                redistributionParkSpace(mCanParkList.get(0), null);
                                break;
                            case "101":
                                dismmisLoadingDialog();

                                //备选车位都不能用，则删掉
                                mCanParkList.remove(0);
                                String[] readyPark = readyParkId.toString().split(",");
                                if (!readyPark[0].equals("")) {
                                    for (int i = 0; i < readyPark.length; i++) {
                                        mCanParkList.remove(0);
                                    }
                                }

                                if (mCanParkList.size() > 0) {
                                    //还有可停的车位，则继续请求
                                    if (mCanParkList.size() != 1) {
                                        DataUtil.sortCanParkByIndicator(mCanParkList, mParkOrderInfo.getOrderEndTime());
                                    }
                                    redistributionOrderParkSpace();
                                } else {
                                    showFiveToast("未匹配到合适您时间的车位，请尝试更换时间");
                                }
                                break;
                            case "102":
                                dismmisLoadingDialog();
                                for (int i = 0; i < mCanParkList.size(); i++) {
                                    if (mCanParkList.get(i).getId().equals(responseData.data.getParkSpaceId())) {
                                        showRequestAppointOrderDialog(mCanParkList.get(i), responseData.data.getExtensionTime());
                                        break;
                                    }
                                }
                                break;
                            case "103":
                                showFiveToast("服务器异常，请重新选择");
                                finish();
                                break;
                            case "104":
                                dismmisLoadingDialog();
                                showFiveToast("您有效订单已达上限，暂不可预约车位哦");
                                break;
                            case "105":
                                dismmisLoadingDialog();
                                showFiveToast("您当前车位在该时段内已有过预约，请尝试更换时间");
                                break;
                            case "106":
                                dismmisLoadingDialog();
                                mCanParkList.remove(0);
                                showRequestAppointOrderDialog(mCanParkList.get(0), String.valueOf(Integer.valueOf(responseData.data.getExtensionTime()) / 60));
                                break;
                            case "107":
                                dismmisLoadingDialog();
                                showFiveToast("您有订单需要前去付款，要先处理哦");
                            default:
                                dismmisLoadingDialog();
                                showFiveToast("服务器正在维护中");
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (!handleException(e)) {
                            showFiveToast(e.getMessage());
                        }
                    }
                });
    }

    /**
     * @param park_info     预约的车位
     * @param extensionTime 可停车的顺延时长（分钟）
     */
    private void showRequestAppointOrderDialog(final Park_Info park_info, final String extensionTime) {
        TipeDialog.Builder builder = new TipeDialog.Builder(this);
        builder.setMessage("可分配车位宽限时长为" + extensionTime + "分钟，是否预定？");
        builder.setTitle("确认预定");
        builder.setPositiveButton("立即预定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showLoadingDialog("提交中...");
                reserveLockedParkSpaceForOrder(park_info, extensionTime);
            }
        });

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        builder.create().show();
    }

    /**
     * 预约被锁定的车位
     */
    private void reserveLockedParkSpaceForOrder(final Park_Info park_info, final String extensionTime) {
        getOkGo(HttpConstants.reserveLockedParkSpaceForOrder)
                .params("orderId", mParkOrderInfo.getId())
                .params("parkSpaceId", park_info.getId())
                .params("cityCode", mParkOrderInfo.getCityCode())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        redistributionParkSpace(park_info, extensionTime);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        showLoadingDialog();
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "102":
                                    showFiveToast("请求已超时，请重新预定");
                                    break;
                                case "901":
                                    showFiveToast("服务器正在维护中");
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 重新分配车位给该订单
     */
    private void redistributionParkSpace(Park_Info parkInfo, String extensionTime) {
        for (int i = 0; i < mCanParkList.size(); i++) {
            if (mCanParkList.get(i).getId().equals(parkInfo.getId())) {
                mCanParkList.remove(i);
                break;
            }
        }

        if (extensionTime != null) {
            mParkOrderInfo.setExtensionTime(extensionTime);
        }

        Intent intent = new Intent(ConstansUtil.CHANGE_PARK_ORDER_INRO);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstansUtil.PARK_ORDER_INFO, mParkOrderInfo);
        bundle.putString(ConstansUtil.PARK_SPACE_ID, parkInfo.getId());
        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, bundle);
        IntentObserable.dispatch(intent);

        mParkOrderInfo.setParkSpaceId(parkInfo.getId());
    }

    private void cancelAppointment() {
        showLoadingDialog("取消预约");
        getOkGo(HttpConstants.cancleAppointOrder)
                .params("order_id", mParkOrderInfo.getId())
                .params("citycode", mParkOrderInfo.getCityCode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        Intent intent = new Intent();
                        intent.setAction(ConstansUtil.CANCEL_ORDER);
                        Bundle bundle = new Bundle();
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
                            switch (e.getMessage()) {
                                case "101":
                                case "103":
                                case "104":
                                    showFiveToast("服务器异常，请稍后重试");
                                    break;
                                case "102":
                                    showFiveToast("取消订单失败，请稍后重试");
                                    break;
                            }
                            showFiveToast(e.getMessage());
                        }
                    }
                });
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
            if (DeviceUtils.isGpsOpen(ParkOrderAppointmentActivity.this)) {
                showFiveToast("请稍后重试");
            } else {
                showFiveToast("定位失败，请开启GPS后重试");
            }
        }
    }

}
