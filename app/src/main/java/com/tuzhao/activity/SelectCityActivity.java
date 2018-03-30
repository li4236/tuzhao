package com.tuzhao.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.DividerItemDecoration;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.HeaderRecyclerAndFooterWrapperAdapter;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.IndexBar;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.OnItemClickListener;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.ViewHolder;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.adpater.CityAdapter;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.adpater.CommonAdapter;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.BaseIndexPinyinBean;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.CityBean;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.HeaderBean;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.TopHeaderBean;
import com.tianzhili.www.myselfsdk.SuspensionIndexBar.suspension.SuspensionDecoration;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


/**
 * 介绍：城市选择页面
 * 作者：TZL13
 * 时间： 2017/7/5.
 */
public class SelectCityActivity extends BaseActivity {
    private Context mContext;
    private RecyclerView mRv;
    private CityAdapter mAdapter;
    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAdapter;
    private LinearLayoutManager mManager;

    //设置给InexBar、ItemDecoration的完整数据集
    private List<BaseIndexPinyinBean> mSourceDatas;
    //头部数据源
    private List<HeaderBean> mHeaderDatas;
    //主体部分数据源（城市数据）
    private List<CityBean> mBodyDatas;

    private SuspensionDecoration mDecoration;

    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;

    /**
     * 显示指示器DialogText
     */
    private TextView mTvSideBarHint;
    private String mCity;

    private CustomDialog mCustomDialog;

    /**
     * 定位相关
     */
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;

    private ViewHolder sbholder;
    private boolean is_first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        initLocation();//初始化定位
        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {
        mRv = (RecyclerView) findViewById(R.id.id_activity_select_city_rv);
        mRv.setLayoutManager(mManager = new LinearLayoutManager(this));

        mTvSideBarHint = (TextView) findViewById(R.id.id_activity_select_city_tv_SideBarHint);//HintTextView
        mIndexBar = (IndexBar) findViewById(R.id.id_activity_select_city_indexBar);//IndexBar
    }

    private void initData() {
        mContext = this;

        mSourceDatas = new ArrayList<>();
        mHeaderDatas = new ArrayList<>();
        List<CityBean> locationCity = new ArrayList<>();
        if (LocationManager.getInstance().hasLocation()) {
            CityBean ctb = new CityBean();
            ctb.setCityname(LocationManager.getInstance().getmAmapLocation().getCity());
            ctb.setCitycode(LocationManager.getInstance().getmAmapLocation().getCityCode());
            locationCity.add(ctb);
        } else {
            locationClient.startLocation();
            CityBean ctb = new CityBean();
            ctb.setCityname("定位中");
            ctb.setCitycode("0");
            locationCity.add(ctb);
        }

        mHeaderDatas.add(new HeaderBean(locationCity, "定位城市", "定"));
        List<CityBean> hotCitys = new ArrayList<>();
        CityBean cb1 = new CityBean();
        cb1.setCityname("上海市");
        cb1.setCitycode("021");
        hotCitys.add(cb1);
        CityBean cb2 = new CityBean();
        cb2.setCityname("北京市");
        cb2.setCitycode("010");
        hotCitys.add(cb2);
        CityBean cb3 = new CityBean();
        cb3.setCityname("深圳市");
        cb3.setCitycode("0755");
        hotCitys.add(cb3);
        CityBean cb4 = new CityBean();
        cb4.setCityname("广州市");
        cb4.setCitycode("020");
        hotCitys.add(cb4);
        mHeaderDatas.add(new HeaderBean(hotCitys, "热门城市", "热"));
        mSourceDatas.addAll(mHeaderDatas);

        mAdapter = new CityAdapter(this, R.layout.item_select_city, mBodyDatas);
        mHeaderAdapter = new HeaderRecyclerAndFooterWrapperAdapter(mAdapter) {
            @Override
            protected void onBindHeaderHolder(ViewHolder holder, int headerPos, int layoutId, Object o) {
                switch (layoutId) {
                    case R.layout.item_header1:
                        final HeaderBean HeaderBean1 = (com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.HeaderBean) o;
                        //网格
                        RecyclerView recyclerView1 = holder.getView(R.id.rvCity);
                        recyclerView1.setAdapter(
                                new CommonAdapter<CityBean>(mContext, R.layout.item_header_item, HeaderBean1.getCityList()) {
                                    @Override
                                    public void convert(ViewHolder holder, final CityBean cityBean) {
                                        sbholder = holder;

                                        holder.setText(R.id.tvName, cityBean.getCityname());
                                        if (!is_first) {
                                            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //返回主页的城市选择
                                                    Intent intent = new Intent();
                                                    intent.putExtra("city", cityBean);
                                                    setResult(201, intent);
                                                    finish();
                                                }
                                            });
                                        }
                                        is_first = false;
                                    }
                                });
                        recyclerView1.setLayoutManager(new GridLayoutManager(mContext, 3));
                        break;
                    case R.layout.item_header2:
                        final HeaderBean HeaderBean = (com.tianzhili.www.myselfsdk.SuspensionIndexBar.bean.HeaderBean) o;
                        //网格
                        RecyclerView recyclerView = holder.getView(R.id.rvCity);
                        recyclerView.setAdapter(
                                new CommonAdapter<CityBean>(mContext, R.layout.item_header_item, HeaderBean.getCityList()) {
                                    @Override
                                    public void convert(ViewHolder holder, final CityBean cityBean) {
                                        holder.setText(R.id.tvName, cityBean.getCityname());
                                        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //返回主页的城市选择
                                                Intent intent = new Intent();
                                                intent.putExtra("city", cityBean);
                                                setResult(201, intent);
                                                finish();
                                            }
                                        });
                                    }
                                });
                        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
                        break;
                    case R.layout.item_header_top:
                        TopHeaderBean topHeaderBean = (TopHeaderBean) o;
                        holder.setText(R.id.tvCurrent, topHeaderBean.getTxt());
                        break;
                    default:
                        break;
                }
            }
        };
        mCity = getIntent().getStringExtra("city");
        if (mCity.equals("定位中")) {
            mHeaderAdapter.setHeaderView(0, R.layout.item_header_top, new TopHeaderBean(mCity));
        } else {
            mHeaderAdapter.setHeaderView(0, R.layout.item_header_top, new TopHeaderBean("当前选择：" + mCity));
        }
        mHeaderAdapter.setHeaderView(1, R.layout.item_header1, mHeaderDatas.get(0));
        mHeaderAdapter.setHeaderView(2, R.layout.item_header2, mHeaderDatas.get(1));


        mRv.setAdapter(mHeaderAdapter);
        mRv.addItemDecoration(mDecoration = new SuspensionDecoration(this, mSourceDatas)
                .setmTitleHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()))
                .setColorTitleBg(0xffefefef)
                .setTitleFontSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, getResources().getDisplayMetrics()))
                .setColorTitleFont(mContext.getResources().getColor(android.R.color.black))
                .setHeaderViewCount(mHeaderAdapter.getHeaderViewCount() - mHeaderDatas.size()));
        mRv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));


        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(false)//设置需要真实的索引
                .setmLayoutManager(mManager)//设置RecyclerView的LayoutManager
                .setHeaderViewCount(mHeaderAdapter.getHeaderViewCount() - mHeaderDatas.size());

        initLoading("正在加载...");
        requestGetAllOpenCity();
    }

    private void initEvent() {
        findViewById(R.id.id_activity_select_city_imageView_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, Object o, int position) {
                if (o instanceof CityBean) {
                    //返回主页的城市选择
                    Intent intent = new Intent();
                    intent.putExtra("city", (CityBean) o);
                    setResult(201, intent);
                    finish();
                }
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                return false;
            }
        });
    }

    private void requestGetAllOpenCity() {

        OkGo.post(HttpConstants.getAllOpenCity)
                .tag(HttpConstants.getAllOpenCity)
                .execute(new JsonCallback<Base_Class_List_Info<CityBean>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<CityBean> cityBeanBase_class_list_info, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        initDatas(cityBeanBase_class_list_info.data);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(SelectCityActivity.this,e)){
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    //暂无已开放城市
                                    break;
                                case 901:
                                    MyToast.showToast(SelectCityActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(this, what);
        mCustomDialog.show();
    }

    /**
     * 组织数据源
     *
     * @param data
     * @return
     */
    private void initDatas(final ArrayList<CityBean> data) {

        mBodyDatas = data;
//        for (int i = 0; i < data.length; i++) {
//            CityBean cityBean = new CityBean();
//            cityBean.setCity(data[i]);//设置城市名称
//            mBodyDatas.add(cityBean);
//        }
        //先排序
        mIndexBar.getDataHelper().sortSourceDatas(mBodyDatas);

        mAdapter.setDatas(mBodyDatas);
        mHeaderAdapter.notifyDataSetChanged();
        mSourceDatas.addAll(mBodyDatas);

        mIndexBar.setmSourceDatas(mSourceDatas)//设置数据
                .invalidate();
        mDecoration.setmDatas(mSourceDatas);

        HeaderBean header1 = mHeaderDatas.get(0);
        header1.getCityList().clear();
        CityBean cb = new CityBean();
        if (LocationManager.getInstance().hasLocation()) {
            cb.setCityname(LocationManager.getInstance().getmAmapLocation().getCity());
            cb.setCitycode(LocationManager.getInstance().getmAmapLocation().getCityCode());
        } else {
            initLocation();
            cb.setCityname("定位中");
            cb.setCitycode("0");
        }

        header1.getCityList().add(cb);

        HeaderBean header2 = mHeaderDatas.get(1);
        List<CityBean> hotCitys = new ArrayList<>();
        CityBean cb1 = new CityBean();
        cb1.setCityname("上海市");
        cb1.setCitycode("021");
        hotCitys.add(cb1);
        CityBean cb2 = new CityBean();
        cb2.setCityname("北京市");
        cb2.setCitycode("010");
        hotCitys.add(cb2);
        CityBean cb3 = new CityBean();
        cb3.setCityname("深圳市");
        cb3.setCitycode("0755");
        hotCitys.add(cb3);
        CityBean cb4 = new CityBean();
        cb4.setCityname("广州市");
        cb4.setCitycode("020");
        hotCitys.add(cb4);
        header2.setCityList(hotCitys);
        mHeaderAdapter.notifyItemRangeChanged(1, 3);
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
            if (mCustomDialog.isShowing()) {
                mCustomDialog.dismiss();
            }
            if (null != location) {

                if (location.getErrorCode() == 0) {
                    //errCode等于0代表定位成功，其他的为定位失败
                    Log.e("dsad", "选择城市页面的定位成功了");
                    HeaderBean header1 = mHeaderDatas.get(0);
                    header1.getCityList().clear();
                    final CityBean cb = new CityBean();
                    cb.setCityname(location.getCity());
                    cb.setCitycode(location.getCityCode());
                    header1.getCityList().add(cb);
                    mHeaderAdapter.notifyItemRangeChanged(1, 1);
                    LocationManager.getInstance().setmAmapLocation(location);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
        if (locationClient!=null){
            locationClient.onDestroy();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //正在定位中的按钮的点击事件
            if (LocationManager.getInstance().hasLocation()) {
                sbholder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //返回主页的城市选择
                        Intent intent = new Intent();
                        CityBean cityBean = new CityBean();
                        cityBean.setCityname(LocationManager.getInstance().getmAmapLocation().getCity());
                        cityBean.setCitycode(LocationManager.getInstance().getmAmapLocation().getCityCode());
                        intent.putExtra("city", cityBean);
                        setResult(201, intent);
                        finish();
                    }
                });
            } else {
                sbholder.getConvertView().setClickable(false);
            }
        }
    }

    /**
     * 监听Back键按下事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (LocationManager.getInstance().hasLocation()) {
                //返回主页的城市选择
                Intent intent = new Intent();
                CityBean cityBean = new CityBean();
                cityBean.setCityname(LocationManager.getInstance().getmAmapLocation().getCity());
                cityBean.setCitycode(LocationManager.getInstance().getmAmapLocation().getCityCode());
                intent.putExtra("city", cityBean);
                intent.putExtra("nocenter","1");
                setResult(201, intent);
                finish();
            }else {
                finish();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
