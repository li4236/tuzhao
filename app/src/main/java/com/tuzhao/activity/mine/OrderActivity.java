package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

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
import com.tuzhao.fragment.parkorder.PayForOrderFragment;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.utils.ConstansUtil;

/**
 * Created by juncoder on 2018/5/29.
 */

public class OrderActivity extends BaseStatusActivity {

    private ParkOrderInfo mParkOrderInfo;

    private MapView mMapView;

    private AMap mAMap;


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

        mMapView = findViewById(R.id.order_mv);
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();

            LatLng latLng = new LatLng(mParkOrderInfo.getLatitude(), mParkOrderInfo.getLongitude());

            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.getUiSettings().setZoomControlsEnabled(false);
            mAMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_park8));

            Marker marker = mAMap.addMarker(markerOptions);
            ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1);
            animation.setDuration(500);
            marker.setAnimation(animation);
            marker.startAnimation();
        }

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.order_container, PayForOrderFragment.newInstance(mParkOrderInfo));
        transaction.commit();
    }

    @Override
    protected void initData() {
    }

    @NonNull
    @Override
    protected String title() {
        switch (mParkOrderInfo.getOrder_status()) {
            case "1":
                return "";
            case "2":
                return "";
            case "3":
                return "订单支付";
            case "4":
            case "5":

                return "";
            case "6":
                return "";
            default:
                return "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
