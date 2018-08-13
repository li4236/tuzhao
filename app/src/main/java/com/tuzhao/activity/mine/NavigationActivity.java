package com.tuzhao.activity.mine;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.navi.model.NaviLatLng;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.map.SensorEventHelper;
import com.tuzhao.publicwidget.others.CircleView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.DeviceUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by juncoder on 2018/8/13.
 */
public class NavigationActivity extends BaseStatusActivity implements View.OnClickListener {

    private TextView mParkLotName;

    private TextView mParkLotAddress;

    private MapView mMapView;

    private AMap mAMap;

    private NaviLatLng mCurrentLocation;

    private NaviLatLng mParkLotLocation;

    private Marker mLocationMarker;//定位marker

    private Marker mLocationCircleMarker; //定位圆点marker

    private Timer mTimer;

    private SensorEventHelper mSensorHelper;

    private CustomDialog mCustomDialog;

    @Override
    protected int resourceId() {
        return R.layout.activity_navigation_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mParkLotName = findViewById(R.id.park_lot_name);
        mParkLotAddress = findViewById(R.id.park_lot_address);
        mMapView = findViewById(R.id.navigation_mv);
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }

        findViewById(R.id.turn_back).setOnClickListener(this);
        findViewById(R.id.my_location).setOnClickListener(this);
        findViewById(R.id.start_navigate).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mCurrentLocation = intent.getParcelableExtra("start");
        mParkLotLocation = intent.getParcelableExtra("end");
        mParkLotName.setText(intent.getStringExtra(ConstansUtil.PARK_LOT_NAME));
        mParkLotAddress.setText(intent.getStringExtra("address"));

        mAMap.getUiSettings().setRotateGesturesEnabled(false);
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mParkLotLocation.getLatitude(), mParkLotLocation.getLongitude()), 16));

        showParkLotMarker();
        showMyLoactionMarker(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));

        mSensorHelper = new SensorEventHelper(this);//初始化定位图标旋转
        mSensorHelper.registerSensorListener();
        mSensorHelper.setCurrentMarker(mLocationMarker);

        initDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSensorHelper == null) {
            mSensorHelper = new SensorEventHelper(this);
            mSensorHelper.registerSensorListener();
        }
        if (mLocationMarker != null) {
            mSensorHelper.setCurrentMarker(mLocationMarker);
        }
        startMarkerAnimation();
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
    protected void onStop() {
        super.onStop();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @NonNull
    @Override
    protected String title() {
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.turn_back:
                finish();
                break;
            case R.id.my_location:
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 16));
                break;
            case R.id.start_navigate:
                mCustomDialog.show();
                break;
        }
    }

    private void showParkLotMarker() {
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(mParkLotLocation.getLatitude(), mParkLotLocation.getLongitude()));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_park8));
        Marker marker = mAMap.addMarker(markerOptions);
        marker.showInfoWindow();
        ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1);
        animation.setDuration(500);
        marker.setAnimation(animation);
        marker.startAnimation();
    }

    private void showMyLoactionMarker(LatLng latlng) {
        ImageView arcView = new ImageView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(DensityUtil.dp2px(this, 21.75f),
                DensityUtil.dp2px(this, 28.25f));
        arcView.setLayoutParams(params);
        arcView.setBackgroundResource(R.drawable.location_arrow);

        CircleView circleView = new CircleView(this);
        final ViewGroup.LayoutParams circleLayoutParams = new ViewGroup.LayoutParams(DensityUtil.dp2px(this, 12),
                DensityUtil.dp2px(this, 12));
        circleView.setLayoutParams(circleLayoutParams);

        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromView(arcView));
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocationMarker = mAMap.addMarker(options);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromView(circleView));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(latlng);
        mLocationCircleMarker = mAMap.addMarker(markerOptions);
        startMarkerAnimation();
    }

    private void startMarkerAnimation() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mLocationCircleMarker != null) {
                    markerAnimation(mLocationCircleMarker);
                }
            }
        }, 0, 1600);
    }

    private void markerAnimation(final Marker marker) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.7f, 1f, 0.7f);
        scaleAnimation.setDuration(800);
        marker.setAnimation(scaleAnimation);
        marker.startAnimation();
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                ScaleAnimation animation = new ScaleAnimation(0.7f, 1f, 0.7f, 1f);
                animation.setDuration(800);
                marker.setAnimation(animation);
                marker.startAnimation();
            }
        });
    }

    private void initDialog() {
        LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_list_layout, null);
        TextView textView = new TextView(this);
        textView.setTextSize(12);
        textView.setPadding(0, dpToPx(16), 0, dpToPx(16));
        textView.setText("取消");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.parseColor("#0979fe"));
        textView.setBackgroundResource(R.drawable.white_all_10dp);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = dpToPx(15);
        layoutParams.leftMargin = dpToPx(16);
        layoutParams.rightMargin = dpToPx(16);
        textView.setLayoutParams(layoutParams);
        view.addView(textView);     //在下面添加取消按钮

        mCustomDialog = new CustomDialog(this, view, true);
        ListView listView = view.findViewById(R.id.dialog_lv);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_center_text_blue2_layout, new String[]{"高德地图", "腾讯地图", "百度地图"});
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        openMap("com.autonavi.minimap");
                        break;
                    case 1:
                        openMap("com.tencent.map");
                        break;
                    case 2:
                        openMap("com.baidu.BaiduMap");
                        break;
                }
                mCustomDialog.dismiss();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDialog.dismiss();
            }
        });
    }

    private void openMap(String packageName) {
        if (DeviceUtils.isAppInstalled(this, packageName)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);

            //将功能Scheme以URI的方式传入data
            Uri uri;
            switch (packageName) {
                case "com.autonavi.minimap":
                    //高德地图
                    uri = Uri.parse("androidamap://route/plan/?dlat=" + mParkLotLocation.getLatitude() + "&dlon=" + mParkLotLocation.getLongitude()
                            + "&dname=" + getText(mParkLotName) + "&dev=0&t=0");
                    break;
                case "com.tencent.map":
                    //腾讯地图
                    uri = Uri.parse("qqmap://map/routeplan?type=drive&to=" + getText(mParkLotName) + "&tocoord=" + mParkLotLocation.getLatitude() + "," + mParkLotLocation.getLongitude() +
                            "&referer=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77");
                    break;
                default:
                    //百度地图
                    uri = Uri.parse("baidumap://map/direction?destination=name:" + getText(mParkLotName) + "|latlng:" +
                            mParkLotLocation.getLatitude() + "," + mParkLotLocation.getLongitude() + "&mode=driving&src=" + getPackageName());
                    break;
            }
            intent.setData(uri);

            //启动该页面即可
            startActivity(intent);
        } else {
            Uri uri = Uri.parse("market://details?id=" + packageName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

}
