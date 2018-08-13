package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.DividerItemDecoration;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.HeaderRecyclerAndFooterWrapperAdapter;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.IndexBar;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.ViewHolder;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.adpater.ParkSelectAdapter;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.CityBean;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.ParkBean;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.suspension.SuspensionDecoration;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.SelectCityActivity;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Space_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class SelectParkSpaceActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView mRv;
    private ParkSelectAdapter mAdapter;
    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAdapter;
    private LinearLayoutManager mManager;
    private List<ParkBean> mDatas;

    private SuspensionDecoration mDecoration;

    private LinearLayout linearlayout_search, linearlayout_no_open;
    private TextView textview_goapply;
    private ArrayList<Park_Space_Info> info_list = new ArrayList<>();

    /**
     * 定位相关
     */
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;

    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;

    /**
     * 显示指示器DialogText
     */
    private TextView mTvSideBarHint;
    private TextView mTv_city;

    private LoadingDialog mLoadingDialog;
    private CityBean mCityBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkspace_select);

        initLocation();//初始化定位
        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {
        mTv_city = findViewById(R.id.id_activity_park_select_textview_city);
        mRv = findViewById(R.id.id_activity_park_select_rv);
        mRv.setLayoutManager(mManager = new LinearLayoutManager(this));
        //使用indexBar
        mTvSideBarHint = findViewById(R.id.id_activity_park_select_tv_SideBarHint);//HintTextView
        mIndexBar = findViewById(R.id.id_activity_park_select_indexBar);//IndexBar
        linearlayout_search = findViewById(R.id.id_activity_park_select_linearlayout_search);
        linearlayout_no_open = findViewById(R.id.id_activity_parkspace_select_linearlayout_no_open);
        textview_goapply = findViewById(R.id.id_activity_parkspace_select_textview_goapply);
    }

    private void initData() {
        if (LocationManager.getInstance().hasLocation()) {
            mTv_city.setText(LocationManager.getInstance().getmAmapLocation().getCity());
            initLoading("正在加载...");
            requestParkData(LocationManager.getInstance().getmAmapLocation().getCityCode() + "");
        } else {
            initLoading("正在定位...");
            locationClient.startLocation();
        }
        mAdapter = new ParkSelectAdapter(this, mDatas);
        mHeaderAdapter = new HeaderRecyclerAndFooterWrapperAdapter(mAdapter) {
            @Override
            protected void onBindHeaderHolder(ViewHolder holder, int headerPos, int layoutId, Object o) {
                holder.setText(R.id.tvCity, (String) o);
            }
        };
        mRv.setAdapter(mHeaderAdapter);
        //设置每个开头字母
        mRv.addItemDecoration(mDecoration = new SuspensionDecoration(this, mDatas).setHeaderViewCount(mHeaderAdapter.getHeaderViewCount()));

        //如果add两个，那么按照先后顺序，依次渲染。
        mRv.addItemDecoration(new DividerItemDecoration(SelectParkSpaceActivity.this, DividerItemDecoration.VERTICAL_LIST));

        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(false)//设置需要真实的索引
                .setmLayoutManager(mManager);//设置RecyclerView的LayoutManager

        mAdapter.setItemClickListener(new ParkSelectAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                Intent intent = new Intent();
                intent.putExtra("park", mDatas.get(postion));
                setResult(1, intent);
                finish();
            }
        });
    }

    private void initEvent() {
        findViewById(R.id.id_activity_park_select_imageview_back).setOnClickListener(this);
        mTv_city.setOnClickListener(this);
        linearlayout_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到已准备的停车场或小区的搜索页面
                Intent intent = new Intent(SelectParkSpaceActivity.this, SearchParkSpaceActivity.class);
                intent.putExtra("info_list", info_list);
                intent.putExtra("citycode", mCityBean == null ? LocationManager.getInstance().getmAmapLocation().getCityCode() : mCityBean.getCitycode());
                startActivityForResult(intent, 101);
            }
        });

        textview_goapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到申请停车场的页面
                Intent intent = new Intent(SelectParkSpaceActivity.this, ApplyParkSpace.class);
                intent.putExtra("citycode", mCityBean == null ? LocationManager.getInstance().getmAmapLocation().getCityCode() : mCityBean.getCitycode());
                startActivity(intent);
            }
        });
    }

    private void initLocation() {

        //初始化client
        locationClient = new AMapLocationClient(this);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {

        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.hide();
            }
            if (null != location) {

                if (location.getErrorCode() == 0) {
                    //errCode等于0代表定位成功，其他的为定位失败
                    Log.e("dsad", "选择停车位页面的定位成功了");
                    mTv_city.setText(location.getCity());
                    LocationManager.getInstance().setmAmapLocation(location);
                    initLoading("正在加载...");
                    requestParkData(location.getCityCode() + "");
                } else {
                    //定位失败
                    Log.e("dsa", "定位失败");
                }
            } else {
                //定位失败
                Log.e("dsa", "定位失败");
            }
        }
    };

    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(this, what);
        mLoadingDialog.show();
    }

    /**
     * 组织数据源
     *
     * @param data
     * @return
     */
    private void initDatas(ArrayList<Park_Space_Info> data, String citycode) {
        if (mDatas != null) {
            mDatas.clear();
        } else {
            mDatas = new ArrayList<>();
        }
        if (data == null) {
            mDatas.clear();
            mAdapter.setDatas(mDatas);
            mHeaderAdapter.notifyDataSetChanged();
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            ParkBean ParkBean = new ParkBean();
            ParkBean.setParkID(data.get(i).getId());
            ParkBean.setparkStation(data.get(i).getParkLotName());//设置停车场名称
            ParkBean.setCitycode(citycode);
            ParkBean.setProfit_ratio(data.get(i).getProfit_ratio());
            mDatas.add(ParkBean);
        }

        mIndexBar.setmSourceDatas(mDatas)//设置数据
                .setHeaderViewCount(mHeaderAdapter.getHeaderViewCount())//设置HeaderView数量
                .invalidate();

        mAdapter.setDatas(mDatas);
        mHeaderAdapter.notifyDataSetChanged();
        mDecoration.setmDatas(mDatas);
    }

    private void requestParkData(final String belong_citycode) {

        OkGo.post(HttpConstants.getParkSpaceDatasForCity)
                .tag(HttpConstants.getParkSpaceDatasForCity)
                .params("citycode", belong_citycode)
                .execute(new JsonCallback<Base_Class_List_Info<Park_Space_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Park_Space_Info> responseData, Call call, Response response) {
                        //请求成功
                        linearlayout_no_open.setVisibility(View.GONE);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.hide();
                        }
                        info_list = responseData.data;
                        for (int i = 0; i < info_list.size(); i++) {
                            info_list.get(i).setCity_code(belong_citycode);
                        }
                        initDatas(responseData.data, belong_citycode);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.hide();
                        }
                        if (e instanceof ConnectException) {
                            Log.d("TAG", "请求失败，" + " 信息为：连接异常" + e.toString());
                        } else if (e instanceof SocketTimeoutException) {
                            Log.d("TAG", "请求失败，" + " 信息为：超时异常" + e.toString());
                        } else if (e instanceof NoRouteToHostException) {
                            Log.d("TAG", "请求失败，" + " 信息为：没有路由到主机" + e.toString());
                        } else {
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    //定位失败
                                    MyToast.showToast(SelectParkSpaceActivity.this, "定位失败，退出应用打开定位开关再试试哦", 2);
                                    break;
                                case 103:
                                    //本城市不存在停车场
                                    linearlayout_no_open.setVisibility(View.VISIBLE);
                                    info_list.clear();
                                    initDatas(null, null);
                                    break;
                                case 901:
                                    MyToast.showToast(SelectParkSpaceActivity.this, "服务器正在维护中", 2);
                                    break;
                                default:
                                    MyToast.showToast(SelectParkSpaceActivity.this, "服务器繁忙，稍后再试", 2);
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_activity_park_select_imageview_back:
                finish();
                break;
            case R.id.id_activity_park_select_textview_city:
                Intent intent = new Intent(SelectParkSpaceActivity.this, SelectCityActivity.class);
                intent.putExtra("city", mTv_city.getText().toString());
                startActivityForResult(intent, 2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 201:
                if (data.hasExtra("city")) {
                    mCityBean = (CityBean) data.getSerializableExtra("city");
                    mTv_city.setText(mCityBean.getCityname());
                    initLoading("正在加载...");
                    requestParkData(mCityBean.getCitycode());
                }
                break;
            case 101:
                Intent intent = new Intent();
                intent.putExtra("park", data.getSerializableExtra("park"));
                setResult(102, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
