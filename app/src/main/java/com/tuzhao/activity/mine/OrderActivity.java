package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import com.tianzhili.www.myselfsdk.photopicker.controller.PhotoPickConfig;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.fragment.parkorder.AppointmentDetailFragment;
import com.tuzhao.fragment.parkorder.CancelOrderFragment;
import com.tuzhao.fragment.parkorder.OrderDetailFragment;
import com.tuzhao.fragment.parkorder.ParkingOrderFragment;
import com.tuzhao.fragment.parkorder.PayForOrderFragment;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.publicwidget.callback.BackPressedCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

/**
 * Created by juncoder on 2018/5/29.
 */

public class OrderActivity extends BaseStatusActivity implements IntentObserver {

    private ParkOrderInfo mParkOrderInfo;

    private MapView mMapView;

    private AMap mAMap;

    private Point mPoint;

    private BackPressedCallback mBackPressedCallback;

    private LatLng mLatLng;

    @Override
    protected int resourceId() {
        return R.layout.activity_order_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkOrderInfo = getIntent().getParcelableExtra(ConstansUtil.PARK_ORDER_INFO);
        if (mParkOrderInfo == null) {
            showFiveToast("获取订单信息失败，请稍后重试");
            finish();
        }

        mPoint = new Point(1080, 1920);
        getWindowManager().getDefaultDisplay().getRealSize(mPoint);

        mMapView = findViewById(R.id.order_mv);
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (mParkOrderInfo.getOrderStatus()) {
            case "1":
                transaction.replace(R.id.order_container, AppointmentDetailFragment.newInstance(mParkOrderInfo));
                mLatLng = new LatLng(mParkOrderInfo.getLatitude() - 0.004, mParkOrderInfo.getLongitude());
                break;
            case "2":
                ParkingOrderFragment orderFragment = ParkingOrderFragment.newInstance(mParkOrderInfo);
                mBackPressedCallback = orderFragment;
                transaction.replace(R.id.order_container, orderFragment);
                mLatLng = new LatLng(mParkOrderInfo.getLatitude() - 0.005, mParkOrderInfo.getLongitude());
                break;
            case "3":
                transaction.replace(R.id.order_container, PayForOrderFragment.newInstance(mParkOrderInfo));
                mLatLng = new LatLng(mParkOrderInfo.getLatitude() - 0.004, mParkOrderInfo.getLongitude());
                break;
            case "4":
            case "5":
                transaction.replace(R.id.order_container, OrderDetailFragment.newInstance(mParkOrderInfo));
                mLatLng = new LatLng(mParkOrderInfo.getLatitude() - 0.003, mParkOrderInfo.getLongitude());
                break;
            case "6":
                transaction.replace(R.id.order_container, CancelOrderFragment.newInstance(mParkOrderInfo));
                mLatLng = new LatLng(mParkOrderInfo.getLatitude() - 0.004, mParkOrderInfo.getLongitude());
                break;
            default:
                transaction.replace(R.id.order_container, OrderDetailFragment.newInstance(mParkOrderInfo));
                mLatLng = new LatLng(mParkOrderInfo.getLatitude() - 0.003, mParkOrderInfo.getLongitude());
                break;
        }
        transaction.commit();

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

        mAMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                TextView textView = view.findViewById(R.id.info_window_title);
                textView.setText(marker.getTitle());
                textView.setMaxWidth((int) (mPoint.x * 0.6));
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(mParkOrderInfo.getLatitude(), mParkOrderInfo.getLongitude()))
                .title(mParkOrderInfo.getParkLotName());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_park8));
        Marker marker = mAMap.addMarker(markerOptions);
        marker.showInfoWindow();
        ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1);
        animation.setDuration(500);
        marker.setAnimation(animation);
        marker.startAnimation();
    }

    @Override
    protected void initData() {
        IntentObserable.registerObserver(this);
    }

    @NonNull
    @Override
    protected String title() {
        switch (mParkOrderInfo.getOrderStatus()) {
            case "1":
            case "2":
                return "订单详情";
            case "3":
                return "订单支付";
            case "4":
            case "5":
                return "订单完成";
            default:
                return "订单详情";
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
        IntentObserable.unregisterObserver(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case PhotoPickConfig.PICK_MORE_REQUEST_CODE:
                    data.setAction(ConstansUtil.PHOTO_IMAGE);
                    Log.e(TAG, "onActivityResult: " + data.getStringArrayListExtra(PhotoPickConfig.EXTRA_STRING_ARRAYLIST));
                    IntentObserable.dispatch(data);
                    break;
                case ConstansUtil.DISOUNT_REQUEST_CODE:
                    data.setAction(ConstansUtil.CHOOSE_DISCOUNT);
                    IntentObserable.dispatch(data);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedCallback == null || !mBackPressedCallback.hanleBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.FINISH_APPOINTMENT:
                    Bundle bundle = intent.getBundleExtra(ConstansUtil.FOR_REQEUST_RESULT);
                    ParkOrderInfo parkOrderInfo = bundle.getParcelable(ConstansUtil.PARK_ORDER_INFO);
                    if (parkOrderInfo != null) {
                        parkOrderInfo.setOrderStatus("2");
                        parkOrderInfo.setPark_start_time(DateUtil.getCurrentYearToSecond());

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.order_container, ParkingOrderFragment.newInstance(mParkOrderInfo));
                        fragmentTransaction.commit();
                    }
                    break;
                case ConstansUtil.CHANGE_APPOINTMENT_INFO:
                    Bundle changBundle = intent.getBundleExtra(ConstansUtil.FOR_REQEUST_RESULT);
                    ParkOrderInfo changeOrderInfo = changBundle.getParcelable(ConstansUtil.PARK_ORDER_INFO);
                    if (changeOrderInfo != null) {
                        FragmentTransaction changeTransaction = getSupportFragmentManager().beginTransaction();
                        changeTransaction.replace(R.id.order_container, AppointmentDetailFragment.newInstance(changeOrderInfo));
                        changeTransaction.commit();
                    }
                    break;
                case ConstansUtil.FINISH_PARK:
                    Bundle parkingBundle = intent.getBundleExtra(ConstansUtil.FOR_REQEUST_RESULT);
                    ParkOrderInfo parkingOrderInfo = parkingBundle.getParcelable(ConstansUtil.PARK_ORDER_INFO);
                    if (parkingOrderInfo != null) {
                        FragmentTransaction parkingTransaction = getSupportFragmentManager().beginTransaction();
                        parkingTransaction.replace(R.id.order_container, PayForOrderFragment.newInstance(parkingOrderInfo));
                        parkingTransaction.commit();
                    }
                    break;
                case ConstansUtil.FINISH_PAY_ORDER:
                    Bundle finishBundle = intent.getBundleExtra(ConstansUtil.FOR_REQEUST_RESULT);
                    ParkOrderInfo finishParkOrder = finishBundle.getParcelable(ConstansUtil.PARK_ORDER_INFO);
                    if (finishParkOrder != null) {
                        FragmentTransaction finishTransaction = getSupportFragmentManager().beginTransaction();
                        finishTransaction.replace(R.id.order_container, OrderDetailFragment.newInstance(finishParkOrder));
                        finishTransaction.commit();
                    }
                    break;
            }
        }
    }
}
