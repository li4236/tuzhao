package com.tuzhao.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
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
import com.tianzhili.www.myselfsdk.update.UpdateHelper;
import com.tianzhili.www.myselfsdk.update.type.UpdateType;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.activity.mine.CarNumberActivity;
import com.tuzhao.activity.mine.CollectionActivity;
import com.tuzhao.activity.mine.MyParkActivity;
import com.tuzhao.activity.mine.MyWalletActivity;
import com.tuzhao.activity.mine.ParkOrderActivity;
import com.tuzhao.activity.mine.PersonalCreditActivity;
import com.tuzhao.activity.mine.PersonalMessageActivity;
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
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.LoginDialogFragment;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.map.ClusterClickListener;
import com.tuzhao.publicwidget.map.ClusterItem;
import com.tuzhao.publicwidget.map.ClusterOverlay;
import com.tuzhao.publicwidget.map.ClusterRender;
import com.tuzhao.publicwidget.map.SensorEventHelper;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.DeviceUtils;
import com.tuzhao.utils.ImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.tianzhili.www.myselfsdk.okgo.convert.FileConvert.DM_TARGET_FOLDER;
import static com.tuzhao.publicwidget.dialog.LoginDialogFragment.LOGIN_ACTION;
import static com.tuzhao.publicwidget.dialog.LoginDialogFragment.LOGOUT_ACTION;
import static com.tuzhao.utils.DensityUtil.dp2px;

public class MainActivity extends BaseActivity implements LocationSource, AMapLocationListener, View.OnClickListener, ClusterRender, ClusterClickListener {

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
    private ImageView imageview_turnown, imageview_user, imageview_huser, imageview_spark, imageview_scharge,
            imageview_search;
    private TextView textview_username, textview_citynodata, textview_credit;
    private TextView textview_mywallet, textview_parkorder, textview_share,
            textview_mycarnumble, textview_mypark, textview_set, textview_mycollection, textview_find;
    private ConstraintLayout constraintLayout_openuser, constraintLayout_user;
    private LoginDialogFragment loginDialogFragment;
    private CustomDialog mCustomDialog;
    /**
     * 定位相关
     */
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private Marker mLocationMarker;//定位marker
    private Marker mSearchMarker = null;//查找地址marker
    private Marker screenMarker;//选择中心点坐标的marker

    private boolean mHadShowGps;
    private boolean isFirstloc = true;
    private LatLng mLastlocationLatlng = null, showmarklal = null, lastLatlng = null;
    private float morenZoom = 14;//地图的默认缩放等级
    private List<ClusterItem> mMarkerData = new ArrayList<>();//当前城市地图标点的数据
    private List<ClusterItem> mShowMarkerData = new ArrayList<>();//当前城市地图标点的数据
    private List<ClusterItem> mQMarkerData = new ArrayList<>();//其他城市地图标点的数据
    private List<ClusterItem> mShowQMarkerData = new ArrayList<>();//其他城市地图标点的数据
    private List<NearPointPCInfo> mYData = new ArrayList<>(), mQYData = new ArrayList<>();
    private ClusterOverlay mClusterOverlay;//标点的聚拢
    private FragmentManager mFragmentManager;
    private View mFragment_content;
    private boolean show = false, show1 = false, isSPark = true, isSCharge = true, isFirstMove = true, isLcData = true;
    private int mapwidth, mapheight;//地图控件的宽高，用来地图中心点
    private int moveDistance = 2000;//移动再次请求的距离
    private GeocodeSearch geocoderSearch;//将经纬度转化为地址
    private String search_address = "";//搜索的地址
    private String moveCityCode = null;
    /**
     * 登录登出的广播接收器
     */
    private LoginBroadcastReceiver loginBroadcastReceiver = new LoginBroadcastReceiver();
    private LogoutBroadcastReceiver logoutBroadcastReceiver = new LogoutBroadcastReceiver();

    protected NetChangeObserver mNetChangeObserver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout_refatory);
        // 网络改变的一个回掉类
        mNetChangeObserver = new NetChangeObserver() {
            @Override
            public void onNetConnected(NetUtils.NetType type) {
                Log.e("唉唉唉", "网络连接上了" + type);
                mlocationClient.startLocation();
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
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "需要开启读取存储信息权限以便提供更好的服务", WRITE_REQUEST_CODE);
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, "需要开启位置信息权限才可以使用地图服务功能", LOCATION_REQUEST_CODE);
        } else {
            initMapStyle();//初始化地图样式文件
        }
        initData();//初始化数据
        initEvent();//初始化事件
    }

    private void initVersion() {
        UpdateHelper.getInstance().setUpdateType(UpdateType.autowifiupdate).check(MainActivity.this);
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

        geocoderSearch = new GeocodeSearch(this);
        mFragmentManager = getSupportFragmentManager();
        imageview_user = findViewById(R.id.id_activity_main_layout_imageview_user);
        imageview_turnown = findViewById(R.id.id_content_main_layout_imageview_turnown);
        imageview_search = findViewById(R.id.id_content_main_layout_imageview_search);
        mFragment_content = findViewById(R.id.id_content_main_layout_linerlayout_fragment);
        textview_username = findViewById(R.id.id_activity_main_layout_textview_username);
        constraintLayout_user = findViewById(R.id.user_info);
        textview_set = findViewById(R.id.id_activity_main_layout_linearlayout_set);
        textview_mywallet = findViewById(R.id.id_activity_main_layout_linearlayout_mywallet);
        textview_parkorder = findViewById(R.id.id_activity_main_layout_linearlayout_parkorder);
        textview_mycarnumble = findViewById(R.id.id_activity_main_layout_linearlayout_mycarnumble);
        textview_mypark = findViewById(R.id.id_activity_main_layout_linearlayout_mypark);
        textview_mycollection = findViewById(R.id.id_activity_main_layout_linearlayout_mycollection);
        textview_find = findViewById(R.id.id_activity_main_layout_linearlayout_find);
        textview_share = findViewById(R.id.id_activity_main_layout_linearlayout_share);
        imageview_spark = findViewById(R.id.id_content_main_layout_imageview_spark);
        imageview_huser = findViewById(R.id.id_content_main_layout_imageview_huser);
        imageview_scharge = findViewById(R.id.id_content_main_layout_imageview_scharge);
        textview_citynodata = findViewById(R.id.id_content_main_layout_textview_citynodata);
        constraintLayout_openuser = findViewById(R.id.id_content_main_layout_relativelayout_openuser);
        textview_credit = findViewById(R.id.id_activity_main_layout_textview_credit);

        int barHeight = setStyle(false);
        ConstraintSet userConstraintSet = new ConstraintSet();
        userConstraintSet.clone(constraintLayout_user);
        userConstraintSet.setMargin(R.id.id_activity_main_layout_imageview_user, ConstraintSet.TOP, DensityUtil.dp2px(this, 30) + barHeight);
        userConstraintSet.applyTo(constraintLayout_user);
        showMarkers(mMarkerData);
    }

    private void initData() {
        registerLogin();//注册登录广播接收器
        registerLogout();//注册退出登录广播接收器
        if (UserManager.getInstance().hasLogined()) {
            ImageUtil.showCirclePic(imageview_user, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                    R.mipmap.ic_usericon);
            ImageUtil.showCirclePic(imageview_huser, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                    R.mipmap.ic_usericon);

            textview_username.setText(UserManager.getInstance().getUserInfo().getNickname().equals("-1") ? UserManager.getInstance().getUserInfo().getUsername().substring(0, 3) + "*****" + UserManager.getInstance().getUserInfo().getUsername().substring(8, UserManager.getInstance().getUserInfo().getUsername().length()) : UserManager.getInstance().getUserInfo().getNickname());
            String credit = "信用分 " + UserManager.getInstance().getUserInfo().getCredit();
            textview_credit.setText(credit);
            mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//允许侧边滑动
        } else {
            mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//禁止侧边滑动
            ImageUtil.showCirclePic(imageview_huser, R.mipmap.ic_usericon);
        }
    }

    private void initEvent() {
        constraintLayout_openuser.setOnClickListener(this);
        imageview_turnown.setOnClickListener(this);
        textview_set.setOnClickListener(this);
        constraintLayout_user.setOnClickListener(this);
        imageview_search.setOnClickListener(this);
        textview_mywallet.setOnClickListener(this);
        textview_parkorder.setOnClickListener(this);
        textview_mycarnumble.setOnClickListener(this);
        textview_mycollection.setOnClickListener(this);
        textview_mypark.setOnClickListener(this);
        textview_find.setOnClickListener(this);
        textview_share.setOnClickListener(this);
        imageview_spark.setOnClickListener(this);
        imageview_scharge.setOnClickListener(this);
        textview_credit.setOnClickListener(this);
        findViewById(R.id.id_content_main_layout_textview_parknow).setOnClickListener(this);

        //等地图绘制完成后，获得地图宽高，来实现转到地图上部中心点
        ViewTreeObserver vto = mapView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                mapheight = mapView.getMeasuredHeight();
                mapwidth = mapView.getMeasuredWidth();
                addMarkerInScreenCenter();//初始化地图中心图标
                return true;
            }
        });

        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (show) {
                    controlAnimfragment(mFragment_content);
                }
            }
        });

        aMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                initLoading("加载中...");
                Log.e("当前经纬度", latLng.latitude + "    " + latLng.longitude);
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
                    ImageUtil.showCirclePic(imageview_user, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                            R.mipmap.ic_usericon);
                    textview_username.setText(UserManager.getInstance().getUserInfo().getNickname().equals("-1") ? UserManager.getInstance().getUserInfo().getUsername().substring(0, 3) + "*****" + UserManager.getInstance().getUserInfo().getUsername().substring(8, UserManager.getInstance().getUserInfo().getUsername().length()) : UserManager.getInstance().getUserInfo().getNickname());
                    String credit = "信用分 " + UserManager.getInstance().getUserInfo().getCredit();
                    textview_credit.setText(credit);
                    mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//允许侧边滑动
                    mDrawerlayout.openDrawer(GravityCompat.START); //侧边展出
                    // 关闭侧边  drawer.closeDrawer(GravityCompat.START);
                } else {
                    //弹出登陆窗口
                    mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//禁止侧边滑动
                    loginDialogFragment = new LoginDialogFragment();
                    login();
                }
                break;
            case R.id.id_content_main_layout_imageview_turnown:
                isFirstMove = true;
                isLcData = true;
                if (show) {
                    controlAnimfragment(mFragment_content);
                }
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastlocationLatlng, 14));
                if (mMarkerData.size() == 0 && LocationManager.getInstance().hasLocation()) {
                    initLoading("正在加载...");
                    requestHomePCLocData(LocationManager.getInstance().getmAmapLocation().getCityCode(), LocationManager.getInstance().getmAmapLocation().getLatitude() + "", LocationManager.getInstance().getmAmapLocation().getLongitude() + "", "10", isLcData, "当前城市");
                } else {
                    if (isSPark && isSCharge) {
                        showMarkers(mMarkerData);
                    } else {
                        showMarkers(mShowMarkerData);
                    }
                    if (show1) {
                        controlAnim(false);
                    }
                }
                break;
            case R.id.id_activity_main_layout_linearlayout_user:
                //跳转个人页面
                intent = new Intent(MainActivity.this, PersonalMessageActivity.class);
                startActivity(intent);
                break;
            case R.id.id_content_main_layout_imageview_search:
                //跳转搜索页面
                if (mClusterOverlay != null) {
                    mClusterOverlay.onDestroy();
                }
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
                intent = new Intent(MainActivity.this, CarNumberActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_main_layout_linearlayout_mypark:
                intent = new Intent(MainActivity.this, MyParkActivity.class);
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
                if (show) {
                    controlAnimfragment(mFragment_content);
                }
                if (isSPark) {
                    isSPark = false;
                    isSCharge = true;
                    imageview_spark.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_park6));
                    imageview_scharge.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_chong7));
                    if (isLcData) {
                        mShowMarkerData.clear();
                        for (ClusterItem item : mMarkerData) {
                            if (!item.isparkspace()) {
                                mShowMarkerData.add(item);
                            }
                        }
                        showMarkers(mShowMarkerData);
                    } else {
                        mShowQMarkerData.clear();
                        for (ClusterItem item : mQMarkerData) {
                            if (!item.isparkspace()) {
                                mShowQMarkerData.add(item);
                            }
                        }
                        showMarkers(mShowQMarkerData);
                    }
                } else {
                    imageview_spark.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_park5));
                    isSPark = true;
                    if (isLcData) {
                        mShowMarkerData.clear();
                        if (isSCharge) {
                            showMarkers(mMarkerData);
                        } else {
                            for (ClusterItem item : mMarkerData) {
                                if (item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        }
                    } else {
                        mShowQMarkerData.clear();
                        if (isSCharge) {
                            showMarkers(mQMarkerData);
                        } else {
                            for (ClusterItem item : mQMarkerData) {
                                if (item.isparkspace()) {
                                    mShowQMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowQMarkerData);
                        }
                    }
                }
                break;
            case R.id.id_content_main_layout_imageview_scharge:
                if (show) {
                    controlAnimfragment(mFragment_content);
                }
                if (isSCharge) {
                    isSCharge = false;
                    isSPark = true;
                    imageview_scharge.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_chong8));
                    imageview_spark.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_park5));
                    if (isLcData) {
                        mShowMarkerData.clear();
                        for (ClusterItem item : mMarkerData) {
                            if (item.isparkspace()) {
                                mShowMarkerData.add(item);
                            }
                        }
                        showMarkers(mShowMarkerData);
                    } else {
                        mShowQMarkerData.clear();
                        for (ClusterItem item : mQMarkerData) {
                            if (item.isparkspace()) {
                                mShowQMarkerData.add(item);
                            }
                        }
                        showMarkers(mShowQMarkerData);
                    }
                } else {
                    imageview_scharge.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_chong7));
                    isSCharge = true;
                    if (isLcData) {
                        mShowMarkerData.clear();
                        if (isSPark) {
                            mClusterOverlay.onDestroy();
                            showMarkers(mMarkerData);
                        } else {
                            for (ClusterItem item : mMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        }
                    } else {
                        mShowQMarkerData.clear();
                        if (isSPark) {
                            mClusterOverlay.onDestroy();
                            showMarkers(mQMarkerData);
                        } else {
                            for (ClusterItem item : mQMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowQMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowQMarkerData);
                        }
                    }
                }
                break;
            case R.id.id_activity_main_layout_linearlayout_set:
                intent = new Intent(MainActivity.this, SetActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_main_layout_textview_credit:
                intent = new Intent(MainActivity.this, PersonalCreditActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_main_layout_linearlayout_share:
                intent = new Intent(MainActivity.this, ShareActivity.class);
                startActivity(intent);
                break;
            case R.id.id_content_main_layout_textview_parknow:
                intent = new Intent(MainActivity.this, ParkOrChargeListActivity.class);
                intent.putExtra("citycode", isLcData ? (LocationManager.getInstance().hasLocation() ? LocationManager.getInstance().getmAmapLocation().getCityCode() : "010") : moveCityCode);
                if (mLastlocationLatlng != null) {
                    if (aMap.getCameraPosition().target.latitude != mLastlocationLatlng.latitude) {
                        if (isLcData) {
                            intent.putExtra("lat", mLastlocationLatlng.latitude);
                            intent.putExtra("lon", mLastlocationLatlng.longitude);
                        } else {
                            intent.putExtra("lat", aMap.getCameraPosition().target.latitude);
                            intent.putExtra("lon", aMap.getCameraPosition().target.longitude);
                        }
                    } else {
                        intent.putExtra("lat", aMap.getCameraPosition().target.latitude);
                        intent.putExtra("lon", aMap.getCameraPosition().target.longitude);
                    }
                } else {
                    intent.putExtra("lat", aMap.getCameraPosition().target.latitude);
                    intent.putExtra("lon", aMap.getCameraPosition().target.longitude);
                }
                startActivity(intent);
                break;
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
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
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
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
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

    private void addMarkerInScreenCenter() {
        if (screenMarker == null) {
            screenMarker = aMap.addMarker(new MarkerOptions().zIndex(2).icon(BitmapDescriptorFactory.fromResource(R.mipmap.biaozhu2)));
        }
        screenMarker.setAnchor(0.5f, 1.0f);
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        screenMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
        screenMarker.setClickable(false);
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
                        if (mCustomDialog != null) {
                            if (mCustomDialog.isShowing()) {
                                mCustomDialog.dismiss();
                            }
                        }
                        if (isLc) {
                            if (mMarkerData.size() == 0) {
                                mMarkerData = new ArrayList<>();
                                mYData = homePC_info.data;
                                for (NearPointPCInfo info : mYData) {
                                    RegionItem item = new RegionItem(info.getId(), new LatLng(info.getLatitude(), info.getLongitude()),
                                            info.getCancharge() == null ? "-1" : info.getCancharge(), info.getIsparkspace().equals("1"), citycode,
                                            info.getPicture(), info.getAddress(), info.getName(), info.getPrice(), info.getGrade());
                                    mMarkerData.add(item);
                                }
                                if (isSPark && isSCharge) {
                                    showMarkers(mMarkerData);
                                } else if (isSPark) {
                                    mShowMarkerData.clear();
                                    for (ClusterItem item : mMarkerData) {
                                        if (item.isparkspace()) {
                                            mShowMarkerData.add(item);
                                        }
                                    }
                                    showMarkers(mShowMarkerData);
                                } else {
                                    mShowMarkerData.clear();
                                    for (ClusterItem item : mMarkerData) {
                                        if (!item.isparkspace()) {
                                            mShowMarkerData.add(item);
                                        }
                                    }
                                    showMarkers(mShowMarkerData);
                                }
                            }
                        } else {
                            mQMarkerData = new ArrayList<>();
                            mQYData = homePC_info.data;
                            for (NearPointPCInfo info : mQYData) {
                                RegionItem item = new RegionItem(info.getId(), new LatLng(info.getLatitude(), info.getLongitude()),
                                        info.getCancharge() == null ? "-1" : info.getCancharge(), info.getIsparkspace().equals("1"), citycode,
                                        info.getPicture(), info.getAddress(), info.getName(), info.getPrice(), info.getGrade());
                                mQMarkerData.add(item);
                            }
                            if (isSPark && isSCharge) {
                                showMarkers(mQMarkerData);
                            } else {
                                mShowQMarkerData.clear();
                                if (isSPark) {
                                    for (ClusterItem item : mQMarkerData) {
                                        if (item.isparkspace()) {
                                            mShowQMarkerData.add(item);
                                        }
                                    }
                                    showMarkers(mShowQMarkerData);
                                } else {
                                    for (ClusterItem item : mQMarkerData) {
                                        if (!item.isparkspace()) {
                                            mShowQMarkerData.add(item);
                                        }
                                    }
                                    showMarkers(mShowQMarkerData);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog != null) {
                            if (mCustomDialog.isShowing()) {
                                mCustomDialog.dismiss();
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
                                    textview_citynodata.setText(noOpen);
                                    if (!show1) {
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

                            if (isSPark && isSCharge) {
                                showMarkers(mMarkerData);
                            } else {
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
                mLastlocationLatlng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                lastLatlng = mLastlocationLatlng;
                if (isFirstloc) {
                    //第一次定位成功
                    isFirstloc = false;
                    Log.e("TAG", "onLocationChanged latitude" + amapLocation.getLatitude() + "  longtitude:" + amapLocation.getLongitude());
                    addLoactionMarker(mLastlocationLatlng);//添加定位图标
                    Log.e(TAG, "onLocationChanged: addLoactionMarker" + String.valueOf(mLocationMarker == null));
                    Log.e(TAG, "onLocationChanged: " + String.valueOf(mSensorHelper == null));
                    if (mSensorHelper == null) {
                        mSensorHelper = new SensorEventHelper(MainActivity.this);
                        mSensorHelper.registerSensorListener();
                    }
                    mSensorHelper.setCurrentMarker(mLocationMarker);//定位图标旋转
                    Log.e(TAG, "onLocationChanged: setCurrentMarker");
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastlocationLatlng, 14));
                    Log.e("TAG", "last latitude" + mLastlocationLatlng.latitude + "  longtitude:" + mLastlocationLatlng.longitude);
                    requestHomePCLocData(LocationManager.getInstance().getmAmapLocation().getCityCode(), LocationManager.getInstance().getmAmapLocation().getLatitude() + "", LocationManager.getInstance().getmAmapLocation().getLongitude() + "", "10", isLcData, amapLocation.getCity());//进行请求充电桩和停车位数据
                } else {
                    if (mMarkerData == null) {
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastlocationLatlng, 14));
                        requestHomePCLocData(LocationManager.getInstance().getmAmapLocation().getCityCode(), LocationManager.getInstance().getmAmapLocation().getLatitude() + "", LocationManager.getInstance().getmAmapLocation().getLongitude() + "", "10", isLcData, amapLocation.getCity());//进行请求充电桩和停车位数据
                    }
                    mLocationMarker.setPosition(mLastlocationLatlng);
                }
                // mListener.onLocationChanged(amapLocation);
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
        Log.e("TAG", "activate: ");
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
    }

    private void addLoactionMarker(LatLng latlng) {
        if (mLocationMarker != null) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
       /* View view_ChargeStation = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_icon_chargestation_location, null);
        ImageView img_chargestation = view_ChargeStation.findViewById(R.id.view_icon_chargestation_location_img);
        img_chargestation.setImageResource(R.mipmap.ic_fangxiang);*/
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_fangxiang));
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocationMarker = aMap.addMarker(options);
    }

    public void login() {
        loginDialogFragment = new LoginDialogFragment();
        loginDialogFragment.show(getSupportFragmentManager(), "hahah");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mlocationClient.stopLocation();
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
        NetStateReceiver.removeRegisterObserver(mNetChangeObserver);
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        if (UserManager.getInstance().hasLogined()) {
            ImageUtil.showCirclePic(imageview_user, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                    R.mipmap.ic_usericon);
            textview_username.setText(UserManager.getInstance().getUserInfo().getNickname().equals("-1") ? UserManager.getInstance().getUserInfo().getUsername().substring(0, 3) + "*****" + UserManager.getInstance().getUserInfo().getUsername().substring(8, UserManager.getInstance().getUserInfo().getUsername().length()) : UserManager.getInstance().getUserInfo().getNickname());
            String crecdit = "信用分 " + UserManager.getInstance().getUserInfo().getCredit();
            textview_credit.setText(crecdit);
        }
        ImageUtil.showCirclePic(imageview_huser, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                R.mipmap.ic_usericon);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        mlocationClient.stopLocation();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
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
    public Drawable getDrawAble(int clusterNum, int type) {
        Drawable bitmapDrawable = null;
        if (clusterNum == 1) {
            switch (type) {
                case 1:
                    bitmapDrawable = ContextCompat.getDrawable(this, R.mipmap.ic_park8);
                    break;
                case 2:
                    bitmapDrawable = ContextCompat.getDrawable(this, R.mipmap.ic_cdzz);
                    break;
                case 3:
                    bitmapDrawable = ContextCompat.getDrawable(this, R.mipmap.ic_park8);
                    break;
            }
            return bitmapDrawable;
        } else {
            bitmapDrawable = ContextCompat.getDrawable(this, R.mipmap.ic_all);
        }
        return bitmapDrawable;
    }

    /**
     * @param marker 点击任意的聚合点
     */
    @Override
    public void onClick(Marker marker, List<ClusterItem> clusterItems) {
        if (clusterItems.size() > 1) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (ClusterItem clusterItem : clusterItems) {
                builder.include(clusterItem.getPosition());
            }
            LatLngBounds latLngBounds = builder.build();
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 1000 / clusterItems.size()));
        } else {
            screenMarker.setVisible(false);
            Animation markerAnimation = new ScaleAnimation(0, 1, 0, 1); //初始化生长效果动画
            markerAnimation.setDuration(700);  //设置动画时间 单位毫秒
            marker.setAnimation(markerAnimation);
            marker.startAnimation();
            showmarklal = clusterItems.get(0).getPosition();
            NearPointPCInfo info = new NearPointPCInfo();
            info.setId(clusterItems.get(0).getId());
            info.setLongitude(clusterItems.get(0).getPosition().latitude);
            info.setLongitude(clusterItems.get(0).getPosition().longitude);
            info.setCancharge(clusterItems.get(0).getCancharge());
            info.setCity_code(clusterItems.get(0).getCity_code());
            info.setPicture(clusterItems.get(0).getPicture());
            info.setAddress(clusterItems.get(0).getAddress());
            info.setName(clusterItems.get(0).getName());
            info.setPrice(clusterItems.get(0).getPrice());
            info.setGrade(clusterItems.get(0).getGrade());
            showUpWindowOnMap(info, clusterItems.get(0).isparkspace());
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clusterItems.get(0).getPosition(), 15.5f));
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
        controlAnimfragment(mFragment_content);
    }

    private void controlAnimfragment(final View ll_view) {
        show = !show;
        ValueAnimator va;
        int height = 73;
        if (show) {
            //显示view，高度从0变到height值
            aMap.setPointToCenter(mapwidth / 2, (mapheight - DensityUtil.dp2px(MainActivity.this, height - 50)) / 2);
            va = ValueAnimator.ofInt(0, DensityUtil.dp2px(this, height));
        } else {
            //隐藏view，高度从height变为0
            va = ValueAnimator.ofInt(height, 0);
            aMap.setPointToCenter(mapwidth / 2, mapheight / 2);
            if (showmarklal != null) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(showmarklal, 14));
            }
            screenMarker.setVisible(true);
        }
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                //动态更新view的高度
                ll_view.getLayoutParams().height = (Integer) valueAnimator.getAnimatedValue();
                ll_view.requestLayout();
            }
        });
        va.setDuration(700);
        va.start();
    }

    private void controlAnim(boolean show) {
        show1 = show;
        ValueAnimator va;
        int height = 40;
        if (show) {
            //显示view，高度从0变到height值
            va = ValueAnimator.ofInt(0, DensityUtil.dp2px(this, height));
        } else {
            //隐藏view，高度从height变为0
            va = ValueAnimator.ofInt(height, 0);
        }
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                //动态更新view的高度
                textview_citynodata.getLayoutParams().height = (Integer) valueAnimator.getAnimatedValue();
                textview_citynodata.requestLayout();
            }
        });
        va.setDuration(150);
        va.start();
    }

    /**
     * 自定义登录的局部广播接收器，用来处理登录广播
     */
    private class LoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (UserManager.getInstance().hasLogined()) {
                ImageUtil.showCirclePic(imageview_user, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                        R.mipmap.ic_usericon);
                ImageUtil.showCirclePic(imageview_huser, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                        R.mipmap.ic_usericon);
                textview_username.setText(UserManager.getInstance().getUserInfo().getNickname().equals("-1") ? UserManager.getInstance().getUserInfo().getUsername().substring(0, 3) + "*****" + UserManager.getInstance().getUserInfo().getUsername().substring(8, UserManager.getInstance().getUserInfo().getUsername().length()) : UserManager.getInstance().getUserInfo().getNickname());
                String credit = "信用分 " + UserManager.getInstance().getUserInfo().getCredit();
                textview_credit.setText(credit);
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
                ImageUtil.showCirclePic(imageview_huser, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url(),
                        R.mipmap.ic_usericon);
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
        if (mCustomDialog == null) {
            mCustomDialog = new CustomDialog(MainActivity.this, what);
        }
        if (!mCustomDialog.isShowing()) {
            mCustomDialog.show();
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
                    options.anchor(0.5f, 0.5f);
                    options.position(latLng);
                    if (mSearchMarker != null) {
                        mSearchMarker.remove();
                    }
                    mSearchMarker = aMap.addMarker(options);
                    moveCityCode = data.getStringExtra("citycode");
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, morenZoom));
                } else {
                    if (isLcData) {
                        if (isSPark && isSCharge) {
                            showMarkers(mMarkerData);
                        } else if (isSPark) {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mMarkerData) {
                                if (item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        } else {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        }
                    } else {
                        if (isSPark && isSCharge) {
                            showMarkers(mQMarkerData);
                        } else if (isSPark) {
                            mShowQMarkerData.clear();
                            for (ClusterItem item : mQMarkerData) {
                                if (item.isparkspace()) {
                                    mShowQMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowQMarkerData);
                        } else {
                            mShowQMarkerData.clear();
                            for (ClusterItem item : mQMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowQMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowQMarkerData);
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
                    LatLng latLng = new LatLng(Double.parseDouble(data.getStringExtra("lat")), Double.parseDouble(data.getStringExtra("lon")));
                    search_address = data.getStringExtra("keyword");
                    MarkerOptions options = new MarkerOptions();
                    View view_ChargeStation = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_icon_chargestation_location, null);
                    ImageView img_chargestation = view_ChargeStation.findViewById(R.id.view_icon_chargestation_location_img);
                    img_chargestation.setImageResource(R.mipmap.ic_biaojiweizhi);
                    options.icon(BitmapDescriptorFactory.fromView(view_ChargeStation));
                    options.anchor(0.5f, 0.5f);
                    options.position(latLng);
                    if (mSearchMarker != null) {
                        mSearchMarker.remove();
                    }
                    mSearchMarker = aMap.addMarker(options);

                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, morenZoom));

                    String citycode = data.getStringExtra("citycode");
                    isLcData = false;
                    requestHomePCLocData(citycode, data.getStringExtra("lat"), data.getStringExtra("lon"), "10", isLcData, "当前城市");
                } else {
                    if (isLcData) {
                        if (isSPark && isSCharge) {
                            showMarkers(mMarkerData);
                        } else if (isSPark) {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mMarkerData) {
                                if (item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        } else {
                            mShowMarkerData.clear();
                            for (ClusterItem item : mMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowMarkerData);
                        }
                    } else {
                        if (isSPark && isSCharge) {
                            showMarkers(mQMarkerData);
                        } else if (isSPark) {
                            mShowQMarkerData.clear();
                            for (ClusterItem item : mQMarkerData) {
                                if (item.isparkspace()) {
                                    mShowQMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowQMarkerData);
                        } else {
                            mShowQMarkerData.clear();
                            for (ClusterItem item : mQMarkerData) {
                                if (!item.isparkspace()) {
                                    mShowQMarkerData.add(item);
                                }
                            }
                            showMarkers(mShowQMarkerData);
                        }
                    }
                }
                break;
        }

        if (requestCode == OPEN_GPS) {
            isFirstloc = true;
            mlocationClient.startLocation();
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
                            if (mCustomDialog.isShowing()) {
                                mCustomDialog.dismiss();
                            }
                            MyToast.showToast(MainActivity.this, "选择的标记点无效，请重选", 5);
                        } else {
                            if (mCustomDialog.isShowing()) {
                                mCustomDialog.dismiss();
                            }
                            TipeDialog.Builder builder = new TipeDialog.Builder(MainActivity.this);
                            builder.setMessage("选中地址为\n" + result.getRegeocodeAddress().getFormatAddress() + "\n确定添加收藏吗？");
                            builder.setTitle("收藏该标记点");
                            builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            initLoading("添加中...");
                                            requestSaveBiaoji(result.getRegeocodeAddress().getFormatAddress());
                                        }
                                    });

                            builder.create().show();
                        }
                    } else {
                        if (result.getRegeocodeAddress().getCityCode().equals("1900")) {
                            textview_citynodata.setText("当前位置暂未开放");
                            if (!show1) {
                                controlAnim(true);
                            }
                            if (mCustomDialog.isShowing()) {
                                mCustomDialog.dismiss();
                            }
                        } else {
                            isLcData = false;
                            moveCityCode = result.getRegeocodeAddress().getCityCode();
                            requestHomePCLocData(moveCityCode, latLng.latitude + "", latLng.longitude + "", "10", isLcData, result.getRegeocodeAddress().getCity());
                        }
                    }

                } else {
                    if (mCustomDialog.isShowing()) {
                        mCustomDialog.dismiss();
                    }
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
        if (mClusterOverlay != null) {
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
                            initLoading("加载中...");
                            getAddressOrCitycode(aMap.getCameraPosition().target, true);
                        }
                    } else {
                        lastLatlng = aMap.getCameraPosition().target;
                        initLoading("加载中...");
                        getAddressOrCitycode(aMap.getCameraPosition().target, true);
                    }

                } else {
                    isFirstMove = false;
                }
            }
        });
        if (screenMarker != null) {
            mClusterOverlay.setScreeMarker(screenMarker);
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

    private void checkPermission(final String permission, String message, final int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                new TipeDialog.Builder(this)
                        .setTitle("权限申请")
                        .setMessage(message)
                        .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermission(permission, requestCode);
                            }
                        })
                        .create()
                        .show();
            } else {
                requestPermission(permission, requestCode);
            }
        } else {
            if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                initMapStyle();
            }
        }
    }

    private void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == LOCATION_REQUEST_CODE) {
                isFirstloc = true;
                mlocationClient.stopLocation();
                mlocationClient.startLocation();
            } else if (requestCode == WRITE_REQUEST_CODE) {
                initMapStyle();
                isFirstloc = true;
                mlocationClient.stopLocation();
                mlocationClient.startLocation();
            }
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
