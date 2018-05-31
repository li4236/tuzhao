package com.tuzhao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bumptech.glide.Glide;
import com.tianzhili.www.myselfsdk.filter.DropDownMenu;
import com.tianzhili.www.myselfsdk.filter.adapter.DropMenuAdapter;
import com.tianzhili.www.myselfsdk.filter.demobean.InstitutionPriceBean;
import com.tianzhili.www.myselfsdk.filter.interfaces.OnFilterDoneListener;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.adapter.ParkOrChargeListAdapter;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.NearPointPCInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.swipetoloadlayout.ChangeScrollStateCallback;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2018/1/24.
 */

public class ParkOrChargeListActivity extends BaseActivity implements OnFilterDoneListener {

    private DropDownMenu dropDownMenu;
    private DropMenuAdapter dropMenuAdapter;//筛选器适配
    private SuperRefreshRecyclerView mRecycleview;
    private ParkOrChargeListAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout linearlayout_nodata;
    private TextView textview_address, textview_nodata;
    private LoadingDialog mLoadingDialog;
    private ConstraintLayout linearlayout_address;

    //筛选菜单
    private String[] titleList;//标题
    private List<InstitutionPriceBean> sortListData, kindListData, distanceListData;//排序,类别,距离

    private String citycode;
    private LatLng latLng;
    private List<NearPointPCInfo> mData = new ArrayList<>();
    private String mRadius = "10";//数据半径，10km
    private String mLineType = "1";//排序规则
    private String mType = "2";//类别
    private String search_address = "";//查找地址
    private GeocodeSearch geocoderSearch;//将经纬度转化为地址
    private RegeocodeQuery query;//转化参数
    private boolean isFirstIn = true;
    private int mLoadingtimes = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkorcharge_layout);

        initView();
        initData();
        initEvent();
        initFilterDropDownView();
        setStyle(true);
    }

    private void initView() {
        dropDownMenu = findViewById(R.id.id_activity_parkorcharge_layout_dropdownmenu);
        mRecycleview = findViewById(R.id.mFilterRecyclerView);
        linearLayoutManager = new LinearLayoutManager(ParkOrChargeListActivity.this);
        mRecycleview.init(linearLayoutManager, new onMyRefresh(), new onMyLoadMore());
        mRecycleview.setRefreshEnabled(true);
        mRecycleview.setLoadingMoreEnable(true);
        mRecycleview.setChangeScrollStateCallback(new ChangeScrollStateCallback() {
            @Override
            public void change(int c) {
                switch (c) {
                    case 2:
                        Glide.with(ParkOrChargeListActivity.this).pauseRequests();
                        break;
                    case 0:
                        Glide.with(ParkOrChargeListActivity.this).resumeRequests();
                        break;
                    case 1:
                        Glide.with(ParkOrChargeListActivity.this).resumeRequests();
                        break;
                }
            }
        });
        mAdapter = new ParkOrChargeListAdapter(ParkOrChargeListActivity.this, mData);
        textview_address = findViewById(R.id.id_activity_parkorcharge_layout_textview_address);
        textview_nodata = findViewById(R.id.id_activity_parkorcharge_layout_textview_nodata);
        linearlayout_nodata = findViewById(R.id.id_activity_parkorcharge_layout_linearlayout_nodata);
        linearlayout_address = findViewById(R.id.id_activity_parkorcharge_layout_linearlayout_address);
    }

    private void initData() {

        //筛选标题数据（固定数据）
        titleList = new String[]{"排序", "类别", "距离"};
        //排序数据
        sortListData = new ArrayList<>();
        InstitutionPriceBean bean1;
        bean1 = new InstitutionPriceBean();
        bean1.setId(0);
        bean1.setName("智能排序");
        sortListData.add(bean1);
        bean1 = new InstitutionPriceBean();
        bean1.setId(1);
        bean1.setName("均价最低");
        sortListData.add(bean1);
        bean1 = new InstitutionPriceBean();
        bean1.setId(2);
        bean1.setName("离我最近");
        sortListData.add(bean1);
        bean1 = new InstitutionPriceBean();
        bean1.setId(3);
        bean1.setName("评分最高");
        sortListData.add(bean1);
        //类别数据
        kindListData = new ArrayList<>();
        bean1 = new InstitutionPriceBean();
        bean1.setId(0);
        bean1.setName("停车场");
        kindListData.add(bean1);
        bean1 = new InstitutionPriceBean();
        bean1.setId(1);
        bean1.setName("充电站");
        kindListData.add(bean1);
        //距离数据
        distanceListData = new ArrayList<>();
        bean1 = new InstitutionPriceBean();
        bean1.setId(0);
        bean1.setName("2km内");
        distanceListData.add(bean1);
        bean1 = new InstitutionPriceBean();
        bean1.setId(1);
        bean1.setName("5km内");
        distanceListData.add(bean1);
        bean1 = new InstitutionPriceBean();
        bean1.setId(2);
        bean1.setName("10km内");
        distanceListData.add(bean1);
        bean1 = new InstitutionPriceBean();
        bean1.setId(3);
        bean1.setName("18km内");
        distanceListData.add(bean1);
        bean1 = new InstitutionPriceBean();
        bean1.setId(4);
        bean1.setName("25km内");
        distanceListData.add(bean1);

        citycode = getIntent().getStringExtra("citycode");
        latLng = new LatLng(getIntent().getDoubleExtra("lat", 22.481234), getIntent().getDoubleExtra("lon", 113.411234));
        initLoading("加载中...");
        getAddress();
        requestHomePCLocData(citycode, latLng.latitude + "", latLng.longitude + "", null, null);
    }

    private void initEvent() {

        findViewById(R.id.id_activity_parkorcharge_layout_imageiew_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        linearlayout_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParkOrChargeListActivity.this, SearchAddressActivity.class);
                intent.putExtra("whatPage", "2");
                intent.putExtra("cityCode", citycode);
                intent.putExtra("keyword", search_address);
                startActivityForResult(intent, 2);
            }
        });
    }

    /**
     * 筛选框 初始化+获取列表数据+筛选条件监听
     */
    private void initFilterDropDownView() {
        //绑定数据源
        dropMenuAdapter = new DropMenuAdapter(this, titleList, this);
        dropMenuAdapter.setSortListData(sortListData);
        dropMenuAdapter.setKindListData(kindListData);
        dropMenuAdapter.setDistanceListData(distanceListData);
        dropDownMenu.setMenuAdapter(dropMenuAdapter);
        dropDownMenu.setOnOpenWindow(new DropDownMenu.OnOpenWindow() {
            @Override
            public void onDoVisible(boolean a) {
                if (a) {
                    if (mData.size() <= 0) {
                        linearlayout_nodata.setVisibility(View.VISIBLE);
                    } else {
                        linearlayout_nodata.setVisibility(View.GONE);
                    }
                } else {
                    linearlayout_nodata.setVisibility(View.GONE);
                }
            }
        });

        //04 dropMenuAdapter 排序回调 0 1 2
        dropMenuAdapter.setOnSortCallbackListener(new DropMenuAdapter.OnSortCallbackListener() {
            @Override
            public void onSortCallbackListener(int item, int tabposition) {
                switch (tabposition) {
                    case 0:
                        mLineType = (item + 1) + "";
                        mRecycleview.setRefreshing(true);
                        break;
                    case 1:
                        mType = (item + 2) + "";
                        mRecycleview.setRefreshing(true);
                        break;
                    case 2:
                        if (item == 0) {
                            mRadius = "2";
                        } else if (item == 1) {
                            mRadius = "5";
                        } else if (item == 2) {
                            mRadius = "10";
                        } else if (item == 3) {
                            mRadius = "18";
                        } else if (item == 4) {
                            mRadius = "25";
                        }
                        mRecycleview.setRefreshing(true);
                        break;
                }
            }
        });

        //02 多条件 回调参数
        dropMenuAdapter.setOnMultiFilterCallbackListener(new DropMenuAdapter.OnMultiFilterCallbackListener() {
            @Override
            public void onMultiFilterCallbackListener(int objId, int propertyId, int bedId, int typeId, String serviceId) {
                MyToast.showToast(ParkOrChargeListActivity.this, "多选的i依次是=" + objId + "--" + propertyId + "--" + bedId + "--" + typeId + "--" + serviceId, 5);
            }
        });
    }

    private class onMyRefresh implements OnRefreshListener {
        @Override
        public void onRefresh() {
            //开始下拉刷新
            mLoadingtimes = 0;
            requestHomePCLocData(citycode, latLng.latitude + "", latLng.longitude + "", null, null);
        }
    }

    private class onMyLoadMore implements OnLoadMoreListener {
        @Override
        public void onLoadMore() {
            //开始上拉加载更多数据
            mLoadingtimes++;
            requestHomePCLocData(citycode, latLng.latitude + "", latLng.longitude + "", (mLoadingtimes * 10) + "", null);
        }
    }

    @Override
    public void onFilterDone(int position, String positionTitle, String urlValue) {
        //数据显示到筛选标题中
        dropDownMenu.setPositionIndicatorText(position, positionTitle);
        dropDownMenu.close();
    }

    private void getAddress() {
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            /**
             * 逆地理编码回调
             */
            @Override
            public void onRegeocodeSearched(final RegeocodeResult result, int rCode) {
                if (rCode == 1000) {
                    if (result.getRegeocodeAddress().getFormatAddress().equals("")) {
                        textview_address.setText("不支持当前位置，请点击尝试查找其他位置");
                    } else {
                        textview_address.setText(result.getRegeocodeAddress().getFormatAddress());
                    }
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

            }

        });
        query = new RegeocodeQuery(new LatLonPoint(latLng.latitude, latLng.longitude), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    private void requestHomePCLocData(final String citycode, String lat, String lon, String startItem, String pageSize) {
        OkGo.post(HttpConstants.getNearPointLocData)
                .tag(HttpConstants.getNearPointLocData)
                .params("citycode", citycode)
                .params("lat", lat)
                .params("lon", lon)
                .params("radius", mRadius)
                .params("type", mType)
                .params("order_type", mLineType)
                .params("startItem", startItem == null ? "0" : startItem)
                .params("pageSize", pageSize == null ? "10" : pageSize)
                .execute(new JsonCallback<Base_Class_List_Info<NearPointPCInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<NearPointPCInfo> homePC_info, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (isFirstIn) {
                            isFirstIn = false;
                            mData = homePC_info.data;
                            mAdapter = new ParkOrChargeListAdapter(ParkOrChargeListActivity.this, mData);
                            mRecycleview.setAdapter(mAdapter);
                        }

                        if (mRecycleview.isRefreshing()) {
                            linearlayout_nodata.setVisibility(View.GONE);
                            mRecycleview.setRefreshing(false);
                            mData.clear();
                            mData.addAll(homePC_info.data);
                            mAdapter.notifyDataSetChanged();
                        }
                        if (mRecycleview.isLoadingMore()) {
                            if (homePC_info.data.size() > 0) {
                                mRecycleview.setLoadingMore(false);
                                mData.addAll(homePC_info.data);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (!DensityUtil.isException(ParkOrChargeListActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getNearPointLocData" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    //未查找到数据
                                    if (isFirstIn) {
                                        isFirstIn = false;
                                        textview_nodata.setText("没有数据哦...");
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }

                                    if (mRecycleview.isRefreshing()) {
                                        textview_nodata.setText("没有数据哦...");
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                        mRecycleview.setRefreshing(false);
                                        mData.clear();
                                        mAdapter.notifyDataSetChanged();
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        mLoadingtimes--;
                                        mRecycleview.setLoadingMore(false);
                                        MyToast.showToast(ParkOrChargeListActivity.this, "没有更多数据", 5);
                                    }
                                    break;
                                case 103:
                                    //城市未开放
                                    if (isFirstIn) {
                                        isFirstIn = false;
                                        textview_nodata.setText("城市未开放哦...");
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }

                                    if (mRecycleview.isRefreshing()) {
                                        textview_nodata.setText("城市未开放哦...");
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                        mRecycleview.setRefreshing(false);
                                        mData.clear();
                                        mAdapter.notifyDataSetChanged();
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        mLoadingtimes--;
                                        mRecycleview.setLoadingMore(false);
                                        MyToast.showToast(ParkOrChargeListActivity.this, "城市未开放", 5);
                                    }
                                    break;
                                case 803:
                                    if (isFirstIn) {
                                        isFirstIn = false;
                                        textview_nodata.setText("抱歉，服务器异常");
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }

                                    if (mRecycleview.isRefreshing()) {
                                        textview_nodata.setText("抱歉，服务器异常");
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                        mRecycleview.setRefreshing(false);
                                        mData.clear();
                                        mAdapter.notifyDataSetChanged();
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        mLoadingtimes--;
                                        mRecycleview.setLoadingMore(false);
                                        MyToast.showToast(ParkOrChargeListActivity.this, "抱歉，服务器异常", 5);
                                    }
                                    break;
                                case 901:
                                    if (isFirstIn) {
                                        isFirstIn = false;
                                        textview_nodata.setText("服务器正在维护中...");
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }

                                    if (mRecycleview.isRefreshing()) {
                                        textview_nodata.setText("服务器正在维护中...");
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                        mRecycleview.setRefreshing(false);
                                        mData.clear();
                                        mAdapter.notifyDataSetChanged();
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        mLoadingtimes--;
                                        mRecycleview.setLoadingMore(false);
                                        MyToast.showToast(ParkOrChargeListActivity.this, "服务器正在维护中", 5);
                                    }
                                    break;
                            }
                        } else {
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 2:
                //搜索地址的返回页面
                if (data == null)
                    return;

                if (data.hasExtra("lat")) {
                    citycode = data.getStringExtra("citycode");
                    latLng = new LatLng(Double.parseDouble(data.getStringExtra("lat")), Double.parseDouble(data.getStringExtra("lon")));
                    search_address = data.getStringExtra("keyword");
                    textview_address.setText(search_address);
                    mRecycleview.setRefreshing(true);
                }
                break;
        }
    }

    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(ParkOrChargeListActivity.this, what);
        mLoadingDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
