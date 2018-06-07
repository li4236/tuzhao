package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.fragment.parkorder.AppointmentDetailFragment;
import com.tuzhao.fragment.parkorder.CancelOrderFragment;
import com.tuzhao.fragment.parkorder.CommentOrderFragment;
import com.tuzhao.fragment.parkorder.OrderDetailFragment;
import com.tuzhao.fragment.parkorder.ParkingOrderFragment;
import com.tuzhao.fragment.parkorder.PayForOrderFragment;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
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

    @Override
    protected int resourceId() {
        return R.layout.activity_order_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if ((mParkOrderInfo = (ParkOrderInfo) getIntent().getSerializableExtra(ConstansUtil.PARK_ORDER_INFO)) == null) {
            showFiveToast("获取订单信息失败，请稍后重试");
            finish();
        }

        mPoint = new Point(1080, 1920);
        getWindowManager().getDefaultDisplay().getRealSize(mPoint);

        mMapView = findViewById(R.id.order_mv);
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();

            final LatLng latLng = new LatLng(mParkOrderInfo.getLatitude() - 0.003, mParkOrderInfo.getLongitude());

            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.getUiSettings().setZoomControlsEnabled(false);
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    return true;
                }
            });

            mAMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    TextView textView = new TextView(OrderActivity.this);
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setTextColor(Color.parseColor("#323232"));
                    textView.setText(marker.getTitle());
                    textView.setMaxWidth((int) (mPoint.x * 0.6));
                    textView.setPadding(DensityUtil.dp2px(OrderActivity.this, 8), DensityUtil.dp2px(OrderActivity.this, 8),
                            DensityUtil.dp2px(OrderActivity.this, 8), DensityUtil.dp2px(OrderActivity.this, 8));
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(layoutParams);
                    return textView;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });

            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(mParkOrderInfo.getLatitude(), mParkOrderInfo.getLongitude()))
                    .title(mParkOrderInfo.getPark_space_name());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_park8));
            Marker marker = mAMap.addMarker(markerOptions);
            marker.showInfoWindow();
            ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1);
            animation.setDuration(500);
            marker.setAnimation(animation);
            marker.startAnimation();
        }

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (mParkOrderInfo.getOrder_status()) {
            case "1":
                transaction.replace(R.id.order_container, AppointmentDetailFragment.newInstance(mParkOrderInfo));
                break;
            case "2":
                transaction.replace(R.id.order_container, ParkingOrderFragment.newInstance(mParkOrderInfo));
                break;
            case "3":
                transaction.replace(R.id.order_container, PayForOrderFragment.newInstance(mParkOrderInfo));
                break;
            case "4":
            case "5":
                transaction.replace(R.id.order_container, OrderDetailFragment.newInstance(mParkOrderInfo));
                break;
            case "6":
                transaction.replace(R.id.order_container, CancelOrderFragment.newInstance(mParkOrderInfo));
                break;
            default:
                transaction.replace(R.id.order_container, OrderDetailFragment.newInstance(mParkOrderInfo));
                break;
        }
        transaction.commit();
    }

    @Override
    protected void initData() {
        IntentObserable.registerObserver(this);
    }

    @NonNull
    @Override
    protected String title() {
        switch (mParkOrderInfo.getOrder_status()) {
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
                case ConstansUtil.PICTURE_REQUEST_CODE:
                    data.setAction(ConstansUtil.PHOTO_IMAGE);
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
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.FINISH_APPOINTMENT:
                    Bundle bundle = intent.getBundleExtra(ConstansUtil.FOR_REQUEST_RESULT);
                    ParkOrderInfo parkOrderInfo = bundle.getParcelable(ConstansUtil.PARK_ORDER_INFO);
                    if (parkOrderInfo != null) {
                        parkOrderInfo.setOrder_status("2");
                        if (parkOrderInfo.getPark_start_time().startsWith("0000")) {
                            parkOrderInfo.setPark_start_time(DateUtil.getCurrentYearToSecond());
                        }
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.order_container, ParkingOrderFragment.newInstance(mParkOrderInfo));
                        fragmentTransaction.commit();
                    }
                    break;
                case ConstansUtil.OPEN_PARK_COMMENT:
                    android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.order_container, CommentOrderFragment.newInstance(mParkOrderInfo));
                    transaction.addToBackStack(null);
                    transaction.commit();
                    break;
                case ConstansUtil.CLOSE_PARK_COMMENT:
                    getSupportFragmentManager().popBackStack();
                    break;
            }
        }
    }
}
