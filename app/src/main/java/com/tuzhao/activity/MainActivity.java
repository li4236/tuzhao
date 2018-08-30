package com.tuzhao.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.tianzhili.www.myselfsdk.netStateLib.NetChangeObserver;
import com.tianzhili.www.myselfsdk.netStateLib.NetStateReceiver;
import com.tianzhili.www.myselfsdk.netStateLib.NetUtils;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.callback.FileCallback;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.activity.mine.CollectionActivity;
import com.tuzhao.activity.mine.CreditActivity;
import com.tuzhao.activity.mine.FriendParkSpaceActivity;
import com.tuzhao.activity.mine.MyCarActivity;
import com.tuzhao.activity.mine.MyParkspaceActivity;
import com.tuzhao.activity.mine.MyWalletActivity;
import com.tuzhao.activity.mine.ParkOrderActivity;
import com.tuzhao.activity.mine.PersonalInformationActivity;
import com.tuzhao.activity.mine.SetActivity;
import com.tuzhao.activity.mine.ShareActivity;
import com.tuzhao.activity.mine.TextActivity;
import com.tuzhao.fragment.home.ChargeFragment;
import com.tuzhao.fragment.home.ParkFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.CollectionInfo;
import com.tuzhao.info.NearPointPCInfo;
import com.tuzhao.info.RegionItem;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.CollectionManager;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.customView.CircleImageView;
import com.tuzhao.publicwidget.customView.CircleView;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.dialog.LoginDialogFragment;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.map.ClusterClickListener;
import com.tuzhao.publicwidget.map.ClusterItem;
import com.tuzhao.publicwidget.map.ClusterOverlay;
import com.tuzhao.publicwidget.map.ClusterRender;
import com.tuzhao.publicwidget.map.SensorEventHelper;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.update.UpdateManager;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.DeviceUtils;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.SpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

import static com.tianzhili.www.myselfsdk.okgo.convert.FileConvert.DM_TARGET_FOLDER;
import static com.tuzhao.publicwidget.dialog.LoginDialogFragment.LOGIN_ACTION;
import static com.tuzhao.publicwidget.dialog.LoginDialogFragment.LOGOUT_ACTION;
import static com.tuzhao.utils.DensityUtil.dp2px;

public class MainActivity extends BaseActivity implements LocationSource, AMapLocationListener, View.OnClickListener, ClusterRender, ClusterClickListener, IntentObserver {

    private static final String TAG = "MainActivity";

    private static final int WRITE_REQUEST_CODE = 0x111;

    private static final int LOCATION_REQUEST_CODE = 0x222;

    private static final int OPEN_GPS = 0x333;

    public static int ONLYPARK = 1, ONLYCHARGE = 2, PARKANDCHARGE = 3;

    /**
     * UI
     */
    private DrawerLayout mDrawerlayout;
    private MapView mapView;
    private AMap aMap;
    private SensorEventHelper mSensorHelper;
    private SensorEventHelper mCircleSensorHelper;
    private ImageView mLocationIv, mParkLotIv, mChargePileIv,
            mSearchIv;
    private CircleImageView mHomeProtraitIv, mDrawerProtraitIv;
    private TextView mUserName, mCityNotOpen, mCredit;
    private TextView mMyWallet, mOrder, mShare,
            mCar, mParkSpace, mSetting, mCollection, mFind;
    private TextView mParkNow;
    private ConstraintLayout mHomeDrawerCl, mDrawerTopCl;
    private LoginDialogFragment loginDialogFragment;
    private LoadingDialog mLoadingDialog;
    /**
     * 定位相关
     */
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private Marker mLocationMarker;//定位marker
    private Marker mLocationCircleMarker; //定位圆点marker
    private Marker mSearchMarker = null;//查找地址marker
    //private Marker screenMarker;//选择中心点坐标的marker

    private boolean mHadShowGps;
    private boolean isFirstloc = true;
    private LatLng mLastlocationLatlng = null;
    private float morenZoom = 14f;//地图的默认缩放等级
    private CameraPosition mLastPosition;//点击marker时地图的位置，用于点击地图后返回地图点击前的位置
    private LatLng mLastLatLng;//上次移动地图时的坐标
    private String mLastCity;//上次移动地图时未开放的城市名字
    private List<ClusterItem> mAllMarkerData = new LinkedList<>();//当前全部的地图标点的数据
    private List<ClusterItem> mShowMarkerData = new ArrayList<>();//当前显示的城市地图标点的数据
    private ClusterOverlay mClusterOverlay;//标点的聚拢
    private FragmentManager mFragmentManager;
    private View mFragment_content;
    private float mTranslationY;
    private float mCityNoDataTranslationY;
    private boolean show = false, showClusters, show1 = false, isShowPark = true, isShowCharge = true, isFirstMove = true, isLcData = true;
    private int mapwidth;//地图控件的宽高，用来地图中心点
    private GeocodeSearch geocoderSearch;//将经纬度转化为地址
    private String search_address = "";//搜索的地址
    private String moveCityCode = null;
    /**
     * 登录登出的广播接收器
     */
    private LoginBroadcastReceiver loginBroadcastReceiver = new LoginBroadcastReceiver();
    private LogoutBroadcastReceiver logoutBroadcastReceiver = new LogoutBroadcastReceiver();

    protected NetChangeObserver mNetChangeObserver = null;

    private Timer mTimer;

    private UpdateManager mUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout_refatory);
        SpUtils.getInstance(this).putBoolean(SpUtils.ALREADY_UPDATE, false);
        // 网络改变的一个回掉类
        mNetChangeObserver = new NetChangeObserver() {
            @Override
            public void onNetConnected(NetUtils.NetType type) {
                Log.e("唉唉唉", "网络连接上了" + type);
                mlocationClient.startLocation();
                if (!SpUtils.getInstance(MainActivity.this).getBoolean(SpUtils.ALREADY_UPDATE)) {
                    if (mUpdateManager != null) {
                        mUpdateManager.onDestroy();
                        mUpdateManager = null;
                    }
                    mUpdateManager = new UpdateManager(MainActivity.this);
                    mUpdateManager.checkUpdate();
                }
            }

            @Override
            public void onNetDisConnect() {
                Log.e("唉唉唉", "网络断了");
            }
        };

        //开启广播去监听 网络 改变事件
        NetStateReceiver.registerObserver(mNetChangeObserver);

        initVersion();
        initView(savedInstanceState);//初始化控件
        if (Build.VERSION.SDK_INT >= 23) {
            if (noHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) || noHavePermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //在某些手机不会弹出两个申请权限窗口，因此只能先申请一个，在申请结果回调了再申请另一个
                if (noHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_REQUEST_CODE);
                } else {
                    initMapStyle();//初始化地图样式文件
                    requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION_REQUEST_CODE);
                }
            }
        } else {
            initMapStyle();//初始化地图样式文件
        }
        initData();//初始化数据
        initEvent();//初始化事件
    }

    private void initVersion() {
        mUpdateManager = new UpdateManager(this);
        mUpdateManager.checkUpdate();
        //UpdateHelper.getInstance().setUpdateType(UpdateType.autowifiupdate).check(MainActivity.this);
    }

    private void initView(Bundle savedInstanceState) {
        mDrawerlayout = findViewById(R.id.drawer_layout);
        mapView = findViewById(R.id.id_content_main_layout_bmapView);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.getUiSettings().setRotateGesturesEnabled(false);
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.animateCamera(CameraUpdateFactory.zoomTo(morenZoom));
            initLocation();//初始化定位
        }

        mSensorHelper = new SensorEventHelper(this);//初始化定位图标旋转
        mSensorHelper.registerSensorListener();
        mCircleSensorHelper = new SensorEventHelper(this);
        mCircleSensorHelper.registerSensorListener();

        geocoderSearch = new GeocodeSearch(this);
        mFragmentManager = getSupportFragmentManager();
        mHomeProtraitIv = findViewById(R.id.id_activity_main_layout_imageview_user);
        mLocationIv = findViewById(R.id.id_content_main_layout_imageview_turnown);
        mSearchIv = findViewById(R.id.id_content_main_layout_imageview_search);
        mFragment_content = findViewById(R.id.id_content_main_layout_linerlayout_fragment);
        mUserName = findViewById(R.id.id_activity_main_layout_textview_username);
        mDrawerTopCl = findViewById(R.id.user_info);
        mSetting = findViewById(R.id.id_activity_main_layout_linearlayout_set);
        mMyWallet = findViewById(R.id.id_activity_main_layout_linearlayout_mywallet);
        mOrder = findViewById(R.id.id_activity_main_layout_linearlayout_parkorder);
        mCar = findViewById(R.id.id_activity_main_layout_linearlayout_mycarnumble);
        mParkSpace = findViewById(R.id.id_activity_main_layout_linearlayout_mypark);
        mCollection = findViewById(R.id.id_activity_main_layout_linearlayout_mycollection);
        mFind = findViewById(R.id.id_activity_main_layout_linearlayout_find);
        mShare = findViewById(R.id.id_activity_main_layout_linearlayout_share);
        mParkLotIv = findViewById(R.id.id_content_main_layout_imageview_spark);
        mDrawerProtraitIv = findViewById(R.id.id_content_main_layout_imageview_huser);
        mChargePileIv = findViewById(R.id.id_content_main_layout_imageview_scharge);
        mCityNotOpen = findViewById(R.id.id_content_main_layout_textview_citynodata);
        mHomeDrawerCl = findViewById(R.id.id_content_main_layout_relativelayout_openuser);
        mCredit = findViewById(R.id.id_activity_main_layout_textview_credit);
        mParkNow = findViewById(R.id.id_content_main_layout_textview_parknow);

        int barHeight = setStyle(false);
        ConstraintSet userConstraintSet = new ConstraintSet();
        userConstraintSet.clone(mDrawerTopCl);
        userConstraintSet.setMargin(R.id.id_activity_main_layout_imageview_user, ConstraintSet.TOP, dp2px(this, 30) + barHeight);
        userConstraintSet.applyTo(mDrawerTopCl);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mHomeDrawerCl);
        constraintSet.setMargin(R.id.id_content_main_layout_imageview_huser, ConstraintSet.BOTTOM, dp2px(this, 1.5f));
        constraintSet.applyTo(mHomeDrawerCl);
    }

    private void initData() {
        registerLogin();//注册登录广播接收器
        registerLogout();//注册退出登录广播接收器
        IntentObserable.registerObserver(this);
        if (UserManager.getInstance().hasLogined()) {
            ImageUtil.showPic(mHomeProtraitIv, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                    R.mipmap.ic_usericon);
            ImageUtil.showPic(mDrawerProtraitIv, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                    R.mipmap.ic_usericon);

            mUserName.setText(UserManager.getInstance().getUserInfo().getNickname().equals("-1") ? UserManager.getInstance().getUserInfo().getUsername().substring(0, 3) + "*****" + UserManager.getInstance().getUserInfo().getUsername().substring(8, UserManager.getInstance().getUserInfo().getUsername().length()) : UserManager.getInstance().getUserInfo().getNickname());
            String credit = "信用分 " + UserManager.getInstance().getUserInfo().getCredit();
            mCredit.setText(credit);
            mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//允许侧边滑动
        } else {
            mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//禁止侧边滑动
            ImageUtil.showCirclePic(mDrawerProtraitIv, R.mipmap.ic_usericon);
        }
    }

    private void initEvent() {
        mHomeDrawerCl.setOnClickListener(this);
        mLocationIv.setOnClickListener(this);
        mSetting.setOnClickListener(this);
        mDrawerTopCl.setOnClickListener(this);
        mSearchIv.setOnClickListener(this);
        mMyWallet.setOnClickListener(this);
        mOrder.setOnClickListener(this);
        mCar.setOnClickListener(this);
        mCollection.setOnClickListener(this);
        mParkSpace.setOnClickListener(this);
        mFind.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mParkLotIv.setOnClickListener(this);
        mChargePileIv.setOnClickListener(this);
        mCredit.setOnClickListener(this);
        mParkNow.setOnClickListener(this);
        mParkNow.setClickable(false);
        findViewById(R.id.id_activity_main_layout_linearlayout_friend_park).setOnClickListener(this);

        //等地图绘制完成后，获得地图宽高，来实现转到地图上部中心点
        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapwidth = mapView.getMeasuredWidth();
                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (show) {
                    //点击了地图时如果车场或电站详情的fragment正在显示则隐藏
                    controlAnimfragment(mFragment_content);
                } else if (showClusters) {
                    if (mLastPosition != null) {
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastPosition.target, mLastPosition.zoom));
                        showClusters = false;
                        mLastPosition = null;
                    }
                }
            }
        });

        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (mLastLatLng == null) {
                        //记录当前点击marker的坐标
                        mLastLatLng = aMap.getCameraPosition().target;
                    } else {
                        //判断屏幕移动的位置是否超过10公里，如果超过则请求新的数据
                        if (AMapUtils.calculateLineDistance(mLastLatLng, aMap.getCameraPosition().target) / 1000 >= 10) {
                            mLastLatLng = aMap.getCameraPosition().target;
                            getAddressOrCitycode(mLastLatLng, true);
                        }
                    }
                }
            }
        });

        aMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                initLoading("加载中...");
                getAddressOrCitycode(latLng, false);
            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.id_content_main_layout_relativelayout_openuser:
                if (UserManager.getInstance().hasLogined()) {
                    ImageUtil.showCirclePic(mHomeProtraitIv, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                            R.mipmap.ic_usericon);
                    mUserName.setText(UserManager.getInstance().getUserInfo().getNickname().equals("-1") ? UserManager.getInstance().getUserInfo().getUsername().substring(0, 3) + "*****" + UserManager.getInstance().getUserInfo().getUsername().substring(8, UserManager.getInstance().getUserInfo().getUsername().length()) : UserManager.getInstance().getUserInfo().getNickname());
                    String credit = "信用分 " + UserManager.getInstance().getUserInfo().getCredit();
                    mCredit.setText(credit);
                    mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//允许侧边滑动
                    mDrawerlayout.openDrawer(GravityCompat.START); //侧边展出
                    // 关闭侧边  drawer.closeDrawer(GravityCompat.START);
                } else {
                    //弹出登陆窗口
                    mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//禁止侧边滑动
                    login();
                }
                break;
            case R.id.id_content_main_layout_imageview_turnown:
                //左下角的定位图标
                isFirstMove = true;
                isLcData = true;
                if (show) {
                    controlAnimfragment(mFragment_content);
                }
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastlocationLatlng, morenZoom));
                if (mAllMarkerData.size() == 0 && LocationManager.getInstance().hasLocation()) {
                    initLoading("正在加载...");
                    requestHomePCLocData(LocationManager.getInstance().getmAmapLocation().getCityCode(),
                            LocationManager.getInstance().getmAmapLocation().getLatitude() + "",
                            LocationManager.getInstance().getmAmapLocation().getLongitude() + "", "10", isLcData, "当前城市");
                } else {
                    if (isShowPark && isShowCharge) {
                        showMarkers(mAllMarkerData);
                    } else {
                        showMarkers(mShowMarkerData);
                    }
                    if (show1) {
                        controlAnim(false);
                    }
                }
                break;
            case R.id.user_info:
                //跳转个人页面
                intent = new Intent(MainActivity.this, PersonalInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.id_content_main_layout_imageview_search:
                //跳转搜索页面
                intent = new Intent(MainActivity.this, SearchAddressActivity.class);
                intent.putExtra("whatPage", "2");
                intent.putExtra("cityCode", isLcData ? (LocationManager.getInstance().hasLocation() ? LocationManager.getInstance().getmAmapLocation().getCityCode() : "010") : moveCityCode);
                intent.putExtra("keyword", search_address);
                startActivityForResult(intent, 2);
                break;
            case R.id.id_activity_main_layout_linearlayout_mywallet:
                intent = new Intent(MainActivity.this, MyWalletActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_main_layout_linearlayout_parkorder:
                intent = new Intent(MainActivity.this, ParkOrderActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_main_layout_linearlayout_mycarnumble:
                intent = new Intent(MainActivity.this, MyCarActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_main_layout_linearlayout_mypark:
                intent = new Intent(MainActivity.this, MyParkspaceActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_main_layout_linearlayout_friend_park:
                intent = new Intent(MainActivity.this, FriendParkSpaceActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_main_layout_linearlayout_mycollection:
                intent = new Intent(MainActivity.this, CollectionActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.id_activity_main_layout_linearlayout_find:
                intent = new Intent(MainActivity.this, TextActivity.class);
                startActivity(intent);
                MyToast.showToast(MainActivity.this, "功能开发中", 5);
                break;
            case R.id.id_content_main_layout_imageview_spark:
                //右上角的车场图标
                onParkIconClick();
                break;
            case R.id.id_content_main_layout_imageview_scharge:
                //右上角的电站图标
                if (show) {
                    controlAnimfragment(mFragment_content);
                }
                if (isShowCharge) {
                    isShowCharge = false;
                    isShowPark = true;
                    ImageUtil.showPic(mParkLotIv, R.mipmap.ic_park5);
                    ImageUtil.showPic(mChargePileIv, R.mipmap.ic_chong8);
                    if (isLcData) {
                        mShowMarkerData.clear();
                        for (ClusterItem item : mAllMarkerData) {
                            if (item.isparkspace()) {
                                mShowMarkerData.add(item);
                            }
                        }
                        showMarkers(mShowMarkerData);
                    } else {
                        mShowMarkerData.clear();
                        for (ClusterItem item : mAllMarkerData) {
                            if (item.isparkspace()) {
                                mShowMarkerData.add(item);
                            }
                        }
                        showMarkers(mShowMarkerData);
                    }
                } else {
                    mChargePileIv.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_chong7));
                    isShowCharge = true;
                    if (isLcData) {
                        mShowMarkerData.clear();
                        if (isShowPark) {
                            showMarkers(mAllMarkerData);
                        } else {
                            for (ClusterItem item : mAllMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        }
                    } else {
                        mShowMarkerData.clear();
                        if (isShowPark) {
                            showMarkers(mAllMarkerData);
                        } else {
                            for (ClusterItem item : mAllMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        }
                    }
                }
                break;
            case R.id.id_activity_main_layout_linearlayout_set:
                intent = new Intent(MainActivity.this, SetActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_main_layout_textview_credit:
                intent = new Intent(MainActivity.this, CreditActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_main_layout_linearlayout_share:
                intent = new Intent(MainActivity.this, ShareActivity.class);
                startActivity(intent);
                break;
            case R.id.id_content_main_layout_textview_parknow:
                intent = new Intent(MainActivity.this, ParkOrChargeListActivity.class);
                intent.putExtra("citycode", isLcData ? (LocationManager.getInstance().hasLocation() ? LocationManager.getInstance().getmAmapLocation().getCityCode() : "010") : moveCityCode);
                intent.putExtra("lat", aMap.getCameraPosition().target.latitude);
                intent.putExtra("lon", aMap.getCameraPosition().target.longitude);
                startActivity(intent);
                break;
        }
    }

    private void onParkIconClick() {
        if (show) {
            controlAnimfragment(mFragment_content);
        }
        if (isShowPark) {
            isShowPark = false;
            isShowCharge = true;
            ImageUtil.showPic(mParkLotIv, R.mipmap.ic_park6);
            ImageUtil.showPic(mChargePileIv, R.mipmap.ic_chong7);

            if (isLcData) {
                mShowMarkerData.clear();
                for (ClusterItem item : mAllMarkerData) {
                    if (!item.isparkspace()) {
                        mShowMarkerData.add(item);
                    }
                }
                showMarkers(mShowMarkerData);
            } else {
                mShowMarkerData.clear();
                for (ClusterItem item : mAllMarkerData) {
                    if (!item.isparkspace()) {
                        mShowMarkerData.add(item);
                    }
                }
                showMarkers(mShowMarkerData);
            }
        } else {
            mParkLotIv.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_park5));
            isShowPark = true;
            if (isLcData) {
                mShowMarkerData.clear();
                if (isShowCharge) {
                    showMarkers(mAllMarkerData);
                } else {
                    for (ClusterItem item : mAllMarkerData) {
                        if (item.isparkspace()) {
                            mShowMarkerData.add(item);
                        }
                    }
                    showMarkers(mShowMarkerData);
                }
            } else {
                mShowMarkerData.clear();
                if (isShowCharge) {
                    showMarkers(mAllMarkerData);
                } else {
                    for (ClusterItem item : mAllMarkerData) {
                        if (item.isparkspace()) {
                            mShowMarkerData.add(item);
                        }
                    }
                    showMarkers(mShowMarkerData);
                }
            }
        }
    }


    private void requestSaveBiaoji(String address) {
        OkGo.post(HttpConstants.addCollection)
                .tag(MainActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("type", "3")
                .params("place", aMap.getCameraPosition().target.latitude + "," + aMap.getCameraPosition().target.longitude + "," + address)
                .execute(new JsonCallback<Base_Class_Info<CollectionInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<CollectionInfo> collectionInfoBase_class_info, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        List<CollectionInfo> collection_datas = CollectionManager.getInstance().getCollection_datas();
                        if (collection_datas == null) {
                            collection_datas = new ArrayList<>();
                        }
                        collection_datas.add(collectionInfoBase_class_info.data);
                        CollectionManager.getInstance().setCollection_datas(collection_datas);
                        MyToast.showToast(MainActivity.this, "标记点收藏成功", 5);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (!DensityUtil.isException(MainActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getUserParkOrderForAppoint" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(MainActivity.this, "标记点收藏失败", 5);
                                    break;
                                case 803:
                                    MyToast.showToast(MainActivity.this, "服务器异常", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(MainActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void requestHomePCLocData(final String citycode, String lat, String lon, String radius, final boolean isLc, final String cityname) {
        OkGo.post(HttpConstants.getNearPointLocData)
                .tag(HttpConstants.getNearPointLocData)
                .params("citycode", citycode)
                .params("lat", lat)
                .params("lon", lon)
                .params("radius", radius)
                .params("type", "1")
                .execute(new JsonCallback<Base_Class_List_Info<NearPointPCInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<NearPointPCInfo> homePC_info, Call call, Response response) {
                        if (show1) {
                            controlAnim(false);
                        }

                        for (NearPointPCInfo info : homePC_info.data) {
                            RegionItem item = new RegionItem(info.getId(), new LatLng(info.getLatitude(), info.getLongitude()),
                                    info.getCancharge() == null ? "-1" : info.getCancharge(), info.getIsparkspace().equals("1"), citycode,
                                    info.getPicture(), info.getAddress(), info.getName(), info.getPrice(), info.getGrade());
                            if (!mAllMarkerData.contains(item)) {
                                mAllMarkerData.add(item);
                            }
                        }

                        //最多显示80个marker
                        while (mAllMarkerData.size() > 80) {
                            mAllMarkerData.remove(0);
                        }

                        if (isShowPark && isShowCharge) {
                            showMarkers(mAllMarkerData);
                        } else if (isShowPark) {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mAllMarkerData) {
                                if (item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        } else {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mAllMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        }
                        /*if (isLc) {
                            //mAllMarkerData = new ArrayList<>();
                            //mYData = homePC_info.data;
                            if (mAllMarkerData.size() + homePC_info.data.size() > 80) {
                                for (int i = 0, moreDataSize = mAllMarkerData.size() + homePC_info.data.size() - 80; i <= moreDataSize; i++) {
                                    mAllMarkerData.remove(0);
                                }
                                //最多显示80个marker
                            }

                            for (NearPointPCInfo info : homePC_info.data) {
                                RegionItem item = new RegionItem(info.getId(), new LatLng(info.getLatitude(), info.getLongitude()),
                                        info.getCancharge() == null ? "-1" : info.getCancharge(), info.getIsparkspace().equals("1"), citycode,
                                        info.getPicture(), info.getAddress(), info.getName(), info.getPrice(), info.getGrade());
                                if (!mAllMarkerData.contains(item)) {
                                    mAllMarkerData.add(item);
                                }
                            }

                            if (isShowPark && isShowCharge) {
                                showMarkers(mAllMarkerData);
                            } else if (isShowPark) {
                                mShowMarkerData.clear();
                                for (ClusterItem item : mAllMarkerData) {
                                    if (item.isparkspace()) {
                                        mShowMarkerData.add(item);
                                    }
                                }
                                showMarkers(mShowMarkerData);
                            } else {
                                mShowMarkerData.clear();
                                for (ClusterItem item : mAllMarkerData) {
                                    if (!item.isparkspace()) {
                                        mShowMarkerData.add(item);
                                    }
                                }
                                showMarkers(mShowMarkerData);
                            }
                        } else {
                            mAllMarkerData.clear();
                            //mQYData = homePC_info.data;
                            for (NearPointPCInfo info : homePC_info.data) {
                                RegionItem item = new RegionItem(info.getId(), new LatLng(info.getLatitude(), info.getLongitude()),
                                        info.getCancharge() == null ? "-1" : info.getCancharge(), info.getIsparkspace().equals("1"), citycode,
                                        info.getPicture(), info.getAddress(), info.getName(), info.getPrice(), info.getGrade());
                                mAllMarkerData.add(item);
                            }
                            if (isShowPark && isShowCharge) {
                                showMarkers(mAllMarkerData);
                            } else {
                                mShowMarkerData.clear();
                                if (isShowPark) {
                                    for (ClusterItem item : mAllMarkerData) {
                                        if (item.isparkspace()) {
                                            mShowMarkerData.add(item);
                                        }
                                    }
                                    showMarkers(mShowMarkerData);
                                } else {
                                    for (ClusterItem item : mAllMarkerData) {
                                        if (!item.isparkspace()) {
                                            mShowMarkerData.add(item);
                                        }
                                    }
                                    showMarkers(mShowMarkerData);
                                }
                            }
                        }*/
                        dismissLoading();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog != null) {
                            if (mLoadingDialog.isShowing()) {
                                mLoadingDialog.dismiss();
                            }
                        }
                        if (!DensityUtil.isException(MainActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getHomePCLocData" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    if (show1) {
                                        controlAnim(false);
                                    }
                                    //未查找到数据
                                    break;
                                case 103:
                                    //城市未开放
                                    String noOpen = cityname + "暂未开放";
                                    mCityNotOpen.setText(noOpen);
                                    if (!show1 || !Objects.equals(mLastCity, cityname)) {
                                        mLastCity = cityname;
                                        controlAnim(true);
                                    }
                                    break;
                                case 901:
                                    if (show1) {
                                        controlAnim(false);
                                    }
                                    MyToast.showToast(MainActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        } else {
                            if (show1) {
                                controlAnim(false);
                            }

                            if (isShowPark && isShowCharge) {
                                Log.e(TAG, "onError four: " + mAllMarkerData.size());
                                showMarkers(mAllMarkerData);
                            } else {
                                Log.e(TAG, "onError five: " + mShowMarkerData.size());
                                showMarkers(mShowMarkerData);
                            }
                        }
                    }
                });
    }

    private void initLocation() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                Log.e("TAG", "定位成功" + isFirstloc);
                LocationManager.getInstance().setmAmapLocation(amapLocation);
                setViewClick();
                mLastlocationLatlng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                mLastLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (isFirstloc) {
                    //第一次定位成功
                    isFirstloc = false;
                    addLoactionMarker(mLastlocationLatlng);//添加定位图标
                    if (mSensorHelper == null) {
                        mSensorHelper = new SensorEventHelper(MainActivity.this);
                        mSensorHelper.registerSensorListener();
                    }
                    mSensorHelper.setCurrentMarker(mLocationMarker);//定位图标旋转
                    if (mCircleSensorHelper == null) {
                        mCircleSensorHelper = new SensorEventHelper(MainActivity.this);
                        mCircleSensorHelper.registerSensorListener();
                    }
                    mCircleSensorHelper.setCurrentMarker(mLocationCircleMarker);
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastlocationLatlng, morenZoom));
                    Log.e("TAG", "last latitude" + mLastlocationLatlng.latitude + "  longtitude:" + mLastlocationLatlng.longitude);
                    isLcData = true;
                    requestHomePCLocData(LocationManager.getInstance().getmAmapLocation().getCityCode(),
                            LocationManager.getInstance().getmAmapLocation().getLatitude() + "",
                            LocationManager.getInstance().getmAmapLocation().getLongitude() + "",
                            "10", isLcData, amapLocation.getCity());//进行请求充电桩和停车位数据
                } else {
                    if (mAllMarkerData == null) {
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastlocationLatlng, morenZoom));
                        isLcData = true;
                        requestHomePCLocData(LocationManager.getInstance().getmAmapLocation().getCityCode(), LocationManager.getInstance().getmAmapLocation().getLatitude() + "", LocationManager.getInstance().getmAmapLocation().getLongitude() + "", "10", isLcData, amapLocation.getCity());//进行请求充电桩和停车位数据
                    }
                    mLocationMarker.setPosition(mLastlocationLatlng);
                }
            } else {
                if (!mHadShowGps) {
                    openGps();
                    mHadShowGps = true;
                }
                Log.e("AmapErr", "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo());
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(MainActivity.this);
            AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            locationClientOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
            locationClientOption.setInterval(2000);//20秒定位一次
            locationClientOption.setOnceLocation(true);//只定位一次
            //设置定位参数
            mlocationClient.setLocationOption(locationClientOption);
            mlocationClient.stopLocation();
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        mUpdateManager.onDestroy();
    }

    private void addLoactionMarker(LatLng latlng) {
        if (mLocationMarker != null) {
            return;
        }

        ImageView arcView = new ImageView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dp2px(this, 21.75f),
                dp2px(this, 28.25f));
        arcView.setLayoutParams(params);
        arcView.setBackgroundResource(R.drawable.location_arrow);

        CircleView circleView = new CircleView(this);
        final ViewGroup.LayoutParams circleLayoutParams = new ViewGroup.LayoutParams(dp2px(this, 12),
                dp2px(this, 12));
        circleView.setLayoutParams(circleLayoutParams);

        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromView(arcView));
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        Log.e(TAG, "addLoactionMarker  longitude:" + latlng.longitude + "  latitude:" + latlng.latitude);
        mLocationMarker = aMap.addMarker(options);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromView(circleView));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(latlng);
        Log.e(TAG, "addLoactionMarker  longitude:" + latlng.longitude + "  latitude:" + latlng.latitude);
        mLocationCircleMarker = aMap.addMarker(markerOptions);
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

    public void login() {
        if (loginDialogFragment == null) {
            loginDialogFragment = new LoginDialogFragment();
        }
        loginDialogFragment.show(getSupportFragmentManager(), "hahah");
    }

    @Override
    public void onStart() {
        super.onStart();
        startMarkerAnimation();
        if (mSensorHelper == null) {
            mSensorHelper = new SensorEventHelper(this);
            mSensorHelper.registerSensorListener();
        }
        if (mLocationMarker != null) {
            mSensorHelper.setCurrentMarker(mLocationMarker);
        }

        if (mCircleSensorHelper == null) {
            mCircleSensorHelper = new SensorEventHelper(this);
            mCircleSensorHelper.registerSensorListener();
        }
        if (mLocationCircleMarker != null) {
            mCircleSensorHelper.setCurrentMarker(mLocationCircleMarker);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mlocationClient.stopLocation();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }

        if (mCircleSensorHelper != null) {
            mCircleSensorHelper.unRegisterSensorListener();
            mCircleSensorHelper.setCurrentMarker(null);
            mCircleSensorHelper = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (show) {
            controlAnimfragment(mFragment_content);
        }
        mapView.onDestroy();
        if (mlocationClient != null) {
            mlocationClient.onDestroy();
        }
        if (mClusterOverlay != null) {
            mClusterOverlay.onDestroy();
        }
        unregisterLogin();
        unregisterLogout();
        IntentObserable.unregisterObserver(this);
        NetStateReceiver.removeRegisterObserver(mNetChangeObserver);
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        if (UserManager.getInstance().hasLogined()) {
            ImageUtil.showCirclePic(mHomeProtraitIv, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                    R.mipmap.ic_usericon);
            mUserName.setText(UserManager.getInstance().getUserInfo().getNickname().equals("-1") ?
                    UserManager.getInstance().getUserInfo().getUsername().substring(0, 3) + "*****" + UserManager.getInstance().getUserInfo().getUsername().substring(8, UserManager.getInstance().getUserInfo().getUsername().length())
                    : UserManager.getInstance().getUserInfo().getNickname());
            String crecdit = "信用分 " + UserManager.getInstance().getUserInfo().getCredit();
            mCredit.setText(crecdit);
        }
        ImageUtil.showCirclePic(mDrawerProtraitIv, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                R.mipmap.ic_usericon);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (show) {
            controlAnimfragment(mFragment_content);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (Objects.equals(getIntent().getStringExtra(ConstansUtil.REQUEST_FOR_RESULT), ConstansUtil.CHANGE_PASSWORD)) {
            mDrawerlayout.closeDrawer(GravityCompat.START);//关闭侧边
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 聚拢标点的绘制
     */
    @Override
    public Drawable getDrawable(int clusterNum, int type) {
        Drawable bitmapDrawable = null;
        if (clusterNum == 1) {
            switch (type) {
                case 1:
                    bitmapDrawable = ContextCompat.getDrawable(this, R.mipmap.yellow_gps);
                    break;
                case 2:
                    bitmapDrawable = ContextCompat.getDrawable(this, R.mipmap.green_gps);
                    break;
                case 3:
                    bitmapDrawable = ContextCompat.getDrawable(this, R.mipmap.yellow_gps);
                    break;
            }
            return bitmapDrawable;
        } else {
            bitmapDrawable = ContextCompat.getDrawable(this, R.mipmap.purple_gps);
        }
        return bitmapDrawable;
    }

    /**
     * @param marker 点击任意的聚合点
     */
    @Override
    public void onClick(Marker marker, List<ClusterItem> clusterItems) {
        Log.e(TAG, "onClick: " + clusterItems.size());
        if (clusterItems.size() > 1) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (ClusterItem clusterItem : clusterItems) {
                builder.include(clusterItem.getPosition());
            }
            LatLngBounds latLngBounds = builder.build();
            showClusters = true;
            mLastPosition = aMap.getCameraPosition();
            //aMap.setPointToCenter(mapwidth / 2, mapheight / 2);
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, clusterItems.size() > 5 ? (int) (mapwidth / 5.0) : (int) (mapwidth * 1.0 / (clusterItems.size() + 1))));
        } else {
            //screenMarker.setVisible(false);
            Animation markerAnimation = new ScaleAnimation(0, 1, 0, 1); //初始化生长效果动画
            markerAnimation.setDuration(700);  //设置动画时间 单位毫秒
            marker.setAnimation(markerAnimation);
            marker.startAnimation();
            LatLng markerLatLng = clusterItems.get(0).getPosition();
            NearPointPCInfo info = new NearPointPCInfo();
            info.setId(clusterItems.get(0).getId());
            info.setLatitude(clusterItems.get(0).getPosition().latitude);
            info.setLongitude(clusterItems.get(0).getPosition().longitude);
            info.setCancharge(clusterItems.get(0).getCancharge());
            info.setCity_code(clusterItems.get(0).getCity_code());
            info.setPicture(clusterItems.get(0).getPicture());
            info.setAddress(clusterItems.get(0).getAddress());
            info.setName(clusterItems.get(0).getName());
            info.setPrice(clusterItems.get(0).getPrice());
            info.setGrade(clusterItems.get(0).getGrade());
            showUpWindowOnMap(info, clusterItems.get(0).isparkspace());
            Log.e(TAG, "onMarkerClick: ");
            //横坐标往右偏移一点以让图标居中
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(markerLatLng.latitude, markerLatLng.longitude - 0.000918), 15.5f));
        }
    }

    private void showUpWindowOnMap(NearPointPCInfo info, boolean isparkspace) {
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (isparkspace) {
            ParkFragment parkFragment = new ParkFragment();
            bundle.putSerializable("pssinfo", info);
            parkFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.id_content_main_layout_linerlayout_fragment, parkFragment);
            fragmentTransaction.commit();
        } else {
            ChargeFragment chargeFragment = new ChargeFragment();
            bundle.putSerializable("cssinfo", info);
            chargeFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.id_content_main_layout_linerlayout_fragment, chargeFragment);
            fragmentTransaction.commit();
        }
        if (!show) {
            controlAnimfragment(mFragment_content);
        }
    }

    /**
     * 显示或隐藏车场或电站的详情fragment
     */
    private void controlAnimfragment(final View ll_view) {
        if (mTranslationY == 0) {
            //
            mTranslationY = ll_view.getY() + ll_view.getHeight();
        }
        show = !show;
        ObjectAnimator objectAnimator;
        if (show) {
            mLastPosition = aMap.getCameraPosition();
            objectAnimator = ObjectAnimator.ofFloat(ll_view, "translationY", -mTranslationY, 0);
        } else {
            objectAnimator = ObjectAnimator.ofFloat(ll_view, "translationY", 0, -mTranslationY);
            if (mLastPosition != null) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastPosition.target, mLastPosition.zoom));
                mLastPosition = null;
            }
        }
        objectAnimator.setDuration(500);
        objectAnimator.start();
    }

    private void controlAnim(boolean show) {
        show1 = show;
        if (mCityNoDataTranslationY == 0) {
            mCityNoDataTranslationY = mCityNotOpen.getY() + mCityNotOpen.getHeight();
        }
        ObjectAnimator objectAnimator;
        if (show) {
            mCityNotOpen.setVisibility(View.VISIBLE);
            objectAnimator = ObjectAnimator.ofFloat(mCityNotOpen, "translationY", -mCityNoDataTranslationY, 0);
        } else {
            mCityNotOpen.setVisibility(View.GONE);
            objectAnimator = ObjectAnimator.ofFloat(mCityNotOpen, "translationY", 0, -mCityNoDataTranslationY);
        }
        objectAnimator.setDuration(500);
        objectAnimator.start();
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.FORCE_LOGOUT)) {
            mDrawerlayout.closeDrawer(GravityCompat.START);//关闭侧边
            mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//禁止侧边滑动
            ImageUtil.showPic(mDrawerProtraitIv, R.mipmap.ic_usericon);

            TipeDialog tipeDialog = new TipeDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("异地登录")
                    .setMessage("您的账号于" + intent.getStringExtra(ConstansUtil.REQUEST_FOR_RESULT) + "在别的设备登录，如果不是您本人操作，请更换密码")
                    .setPositiveButton("重新登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            login();
                        }
                    })
                    .create();
            tipeDialog.show();
        } else if (Objects.equals(intent.getAction(), ConstansUtil.CHANGE_NICKNAME)) {
            mUserName.setText(UserManager.getInstance().getUserInfo().getNickname());
        } else if (Objects.equals(intent.getAction(), ConstansUtil.CHANGE_PORTRAIT)) {
            ImageUtil.showPic(mDrawerProtraitIv, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url());
            ImageUtil.showPic(mHomeProtraitIv, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url());
        }
    }

    /**
     * 自定义登录的局部广播接收器，用来处理登录广播
     */
    private class LoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (UserManager.getInstance().hasLogined()) {
                Log.e(TAG, "onReceive: ");
                ImageUtil.showCirclePic(mHomeProtraitIv, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                        R.mipmap.ic_usericon);
                ImageUtil.showCirclePic(mDrawerProtraitIv, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                        R.mipmap.ic_usericon);
                mUserName.setText(UserManager.getInstance().getUserInfo().getNickname().equals("-1") ? UserManager.getInstance().getUserInfo().getUsername().substring(0, 3) + "*****" + UserManager.getInstance().getUserInfo().getUsername().substring(8, UserManager.getInstance().getUserInfo().getUsername().length()) : UserManager.getInstance().getUserInfo().getNickname());
                String credit = "信用分 " + UserManager.getInstance().getUserInfo().getCredit();
                mCredit.setText(credit);
                mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//允许侧边滑动
            }
        }
    }

    /**
     * 自定义退出登录的局部广播接收器，用来处理退出登录广播
     */
    private class LogoutBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!UserManager.getInstance().hasLogined()) {
                mDrawerlayout.closeDrawer(GravityCompat.START);//关闭侧边
                mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//禁止侧边滑动
                ImageUtil.showPic(mDrawerProtraitIv, R.mipmap.ic_usericon);
               /* ImageUtil.showCirclePic(mDrawerProtraitIv, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                        R.mipmap.ic_usericon);*/
            }
        }
    }

    //注册登录广播接收器的方法
    private void registerLogin() {
        IntentFilter filter = new IntentFilter(LOGIN_ACTION);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(loginBroadcastReceiver, filter);
    }

    //注册退出登录广播接收器的方法
    private void registerLogout() {
        IntentFilter filter = new IntentFilter(LOGOUT_ACTION);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(logoutBroadcastReceiver, filter);
    }

    //注销登录广播接收器
    private void unregisterLogin() {
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(loginBroadcastReceiver);
    }

    //注销退出登录广播接收器
    private void unregisterLogout() {
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(logoutBroadcastReceiver);
    }

    private void initLoading(String what) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(MainActivity.this, what);
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    /**
     * 监听Back键按下事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (show) {
                controlAnimfragment(mFragment_content);
                return true;
            } else {
                finish();
                return false;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                //收藏的标记点返回页面
                if (data.hasExtra("lat")) {
                    mDrawerlayout.closeDrawer(GravityCompat.START); //关闭侧边
                    if (show) {
                        controlAnimfragment(mFragment_content);
                    }
                    LatLng latLng = new LatLng(Double.parseDouble(data.getStringExtra("lat")), Double.parseDouble(data.getStringExtra("lon")));
                    MarkerOptions options = new MarkerOptions();
                    View view_ChargeStation = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_icon_chargestation_location, null);
                    ImageView img_chargestation = view_ChargeStation.findViewById(R.id.view_icon_chargestation_location_img);
                    img_chargestation.setImageResource(R.mipmap.ic_biaojiweizhi);
                    options.icon(BitmapDescriptorFactory.fromView(view_ChargeStation));
                    //options.anchor(0.5f, 0.5f);
                    options.position(latLng);
                    if (mSearchMarker != null) {
                        mSearchMarker.remove();
                    }
                    mSearchMarker = aMap.addMarker(options);
                    moveCityCode = data.getStringExtra("citycode");
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, morenZoom));
                } else {
                    if (isLcData) {
                        if (isShowPark && isShowCharge) {
                            showMarkers(mAllMarkerData);
                        } else if (isShowPark) {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mAllMarkerData) {
                                if (item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        } else {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mAllMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        }
                    } else {
                        if (isShowPark && isShowCharge) {
                            showMarkers(mAllMarkerData);
                        } else if (isShowPark) {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mAllMarkerData) {
                                if (item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        } else {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mAllMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        }
                    }
                }
                break;
            case 2:
                //搜索地址的返回页面
                if (data == null)
                    return;

                if (data.hasExtra("lat")) {
                    if (show) {
                        controlAnimfragment(mFragment_content);
                    }
                    LatLng latLng = new LatLng(data.getDoubleExtra("lat", 0), data.getDoubleExtra("lon", 0));
                    search_address = data.getStringExtra("keyword");
                    MarkerOptions options = new MarkerOptions();
                    View view_ChargeStation = getLayoutInflater().inflate(R.layout.view_icon_chargestation_location, null);
                    ImageView img_chargestation = view_ChargeStation.findViewById(R.id.view_icon_chargestation_location_img);
                    img_chargestation.setImageResource(R.mipmap.ic_biaojiweizhi);
                    options.icon(BitmapDescriptorFactory.fromView(view_ChargeStation));
                    //options.anchor(0.5f, 0.5f);
                    options.position(latLng);
                    if (mSearchMarker != null) {
                        mSearchMarker.remove();
                    }
                    mSearchMarker = aMap.addMarker(options);

                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, morenZoom));

                    String citycode = data.getStringExtra("citycode");
                    isLcData = false;
                    Log.e(TAG, "onActivityResult: 4");
                    requestHomePCLocData(citycode, data.getStringExtra("lat"), data.getStringExtra("lon"), "10", isLcData, "当前城市");
                } else {
                    if (isLcData) {
                        if (isShowPark && isShowCharge) {
                            showMarkers(mAllMarkerData);
                        } else if (isShowPark) {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mAllMarkerData) {
                                if (item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        } else {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mAllMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        }
                    } else {
                        if (isShowPark && isShowCharge) {
                            showMarkers(mAllMarkerData);
                        } else if (isShowPark) {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mAllMarkerData) {
                                if (item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        } else {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mAllMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        }
                    }
                }
                break;
        }

        if (requestCode == OPEN_GPS && resultCode == RESULT_OK) {
            isFirstloc = true;
            mlocationClient.startLocation();
        } else {
            mUpdateManager.onActivityResult(requestCode, resultCode);
        }
    }

    private void getAddressOrCitycode(final LatLng latLng, final boolean isCamreaMove) {
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            /**
             * 逆地理编码回调
             */
            @Override
            public void onRegeocodeSearched(final RegeocodeResult result, int rCode) {
                if (rCode == 1000) {
                    if (!isCamreaMove) {
                        if (result.getRegeocodeAddress().getFormatAddress().equals("")) {
                            if (mLoadingDialog.isShowing()) {
                                mLoadingDialog.dismiss();
                            }
                            MyToast.showToast(MainActivity.this, "选择的标记点无效，请重选", 5);
                        } else {
                            if (mLoadingDialog.isShowing()) {
                                mLoadingDialog.dismiss();
                            }
                            TipeDialog.Builder builder = new TipeDialog.Builder(MainActivity.this);
                            builder.setMessage("选中地址为\n" + result.getRegeocodeAddress().getFormatAddress() + "\n确定添加收藏吗？");
                            builder.setTitle("收藏该标记点");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    initLoading("添加中...");
                                    requestSaveBiaoji(result.getRegeocodeAddress().getFormatAddress());
                                }
                            });

                            builder.setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                            builder.create().show();
                        }
                    } else {
                        Log.e(TAG, "onRegeocodeSearched: " + result.getRegeocodeAddress().getCityCode());
                        if (result.getRegeocodeAddress().getCityCode().equals("1900")) {
                            mCityNotOpen.setText("当前位置暂未开放");
                            if (!show1) {
                                controlAnim(true);
                            }
                            dismissLoading();
                        } else {
                            isLcData = false;
                            moveCityCode = result.getRegeocodeAddress().getCityCode();
                            Log.e(TAG, "onRegeocodeSearched: 5");
                            requestHomePCLocData(moveCityCode, latLng.latitude + "", latLng.longitude + "",
                                    "10", isLcData, result.getRegeocodeAddress().getCity());
                        }
                    }

                } else {
                    dismissLoading();
                    MyToast.showToast(MainActivity.this, "选择的标记点异常，请重试", 5);
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

            }

        });

        //转化参数
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latLng.latitude, latLng.longitude), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    private void showMarkers(List<ClusterItem> markerdata) {
        /*if (mClusterOverlay != null) {
            mClusterOverlay.onDestroy();
        }
        int clusterRadius = 30;//标点之间的距离半径dp
        mClusterOverlay = new ClusterOverlay(aMap, markerdata, dp2px(getApplicationContext(), clusterRadius), getApplicationContext());
        mClusterOverlay.setClusterRenderer(MainActivity.this);
        mClusterOverlay.setOnClusterClickListener(MainActivity.this);
        mClusterOverlay.setOnCameraMoveRequestData(new ClusterOverlay.OnCameraMoveRequestData() {
            @Override
            public void OnCameraMoveRequestData() {
                if (!isFirstMove) {
                    if (lastLatlng != null) {
                        if (AMapUtils.calculateLineDistance(lastLatlng, aMap.getCameraPosition().target) > moveDistance) {
                            lastLatlng = aMap.getCameraPosition().target;
                            getAddressOrCitycode(aMap.getCameraPosition().target, true);
                        }
                    } else {
                        lastLatlng = aMap.getCameraPosition().target;
                        getAddressOrCitycode(aMap.getCameraPosition().target, true);
                    }

                } else {
                    isFirstMove = false;
                }
            }
        });*/
        if (mClusterOverlay == null) {
            mClusterOverlay = new ClusterOverlay(aMap, markerdata, dp2px(getApplicationContext(), 30), getApplicationContext());
            mClusterOverlay.setClusterRenderer(MainActivity.this);
            mClusterOverlay.setOnClusterClickListener(MainActivity.this);
        } else {
            mClusterOverlay.assignClusters(markerdata);
        }
    }

    private void initMapStyle() {
        File file = new File(Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER + "mystyle_sdk.data");
        if (file.exists()) {
            if (aMap != null) {
                aMap.setMapCustomEnable(true);//开启允许地图自定义样式true 开启; false 关闭
                aMap.setCustomMapStylePath(Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER + "mystyle_sdk.data");
            }
        } else {
            OkGo.get(HttpConstants.ROOT_MAPSTYLE_URL)
                    .execute(new FileCallback() {
                        @Override
                        public void onSuccess(File file, Call call, Response response) {
                            if (aMap != null) {
                                aMap.setMapCustomEnable(true);//开启允许地图自定义样式true 开启; false 关闭
                                aMap.setCustomMapStylePath(file.getAbsolutePath());
                            }
                        }
                    });
        }
    }

    private void setViewClick() {
        mParkNow.setClickable(true);
        mParkNow.setBackground(ContextCompat.getDrawable(this, R.drawable.normal_y6_press_y7_round));
    }

    private void dismissLoading() {
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }
    }

    private void showFiveToast(String msg) {
        MyToast.showToast(this, msg, 5);
    }

    private boolean noHavePermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isFirstloc = true;
                mlocationClient.stopLocation();
                mlocationClient.startLocation();
            }
            if (noHavePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_REQUEST_CODE);
            }
        } else if (requestCode == WRITE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMapStyle();
            }
            if (noHavePermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION_REQUEST_CODE);
            }
        } else {
            mUpdateManager.onRequestPermissionsResult(requestCode, grantResults);
        }
    }

    private void openGps() {
        if (!DeviceUtils.isGpsOpen(this)) {
            new TipeDialog.Builder(this)
                    .setTitle("打开GPS")
                    .setMessage("打开GPS可以获取更精确的定位结果哦")
                    .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, OPEN_GPS);
                        }
                    })
                    .create()
                    .show();
        }
    }

}
