package com.tuzhao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.FilterInfo;
import com.tuzhao.info.NearPointPCInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.CheckTextView;
import com.tuzhao.publicwidget.customView.CompatCheckTextView;
import com.tuzhao.publicwidget.customView.SkipTopBottomDivider;
import com.tuzhao.utils.ConstansUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/11/7.
 */
public class ParkOrChargeActivity extends BaseRefreshActivity<NearPointPCInfo> implements View.OnClickListener {

    private TextView mSearchAddress;

    private CompatCheckTextView[] mCheckTextViews;

    private View mFilterBackgound;

    private RecyclerView mFilterRecyclerView;

    private FilterAdapter mFilterAdapter;

    private List<FilterInfo> mSortList;

    private List<FilterInfo> mTypeList;

    private List<FilterInfo> mDistanceList;

    /**
     * TextView最多能展示的文字个数
     */
    private int mMaxTextNum;

    /**
     * 0(排序),1(类别),2(距离)
     */
    private int mRequestPostion = -1;

    private int mSortPosition;

    private int mTypePosition;

    private int mDistancePosition = 2;

    private DecimalFormat mDecimalFormat;

    private String mCityCode;

    private LatLng mLatLng;

    private GeocodeSearch mGeocodeSearch;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        ImageView searchAddressIv = findViewById(R.id.search_iv);
        mSearchAddress = findViewById(R.id.search_address);
        CompatCheckTextView requestBySort = findViewById(R.id.request_by_sort);
        CompatCheckTextView requestByType = findViewById(R.id.request_by_type);
        CompatCheckTextView requestByDistance = findViewById(R.id.request_by_distance);

        mCheckTextViews = new CompatCheckTextView[3];
        mCheckTextViews[0] = requestBySort;
        mCheckTextViews[1] = requestByType;
        mCheckTextViews[2] = requestByDistance;
        setTextViewPadding();

        searchAddressIv.setOnClickListener(this);
        mSearchAddress.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mCityCode = getIntent().getStringExtra(ConstansUtil.CITY_CODE);
        mLatLng = new LatLng(getIntent().getDoubleExtra(ConstansUtil.LATITUDE, 22.481234),
                getIntent().getDoubleExtra(ConstansUtil.LONGITUDE, 113.411234));
        mDecimalFormat = new DecimalFormat("0.0");
        getAddress();
        super.initData();
    }

    @NonNull
    @Override
    protected String title() {
        return getResources().getString(R.string.app_name);
    }

    @Override
    protected void loadData() {
        getOkgos(HttpConstants.getNearPointLocData)
                .params("citycode", mCityCode)
                .params("lat", mLatLng.latitude)
                .params("lon", mLatLng.longitude)
                .params("radius", convertDistance())
                .params("type", mTypePosition + 2)
                .params("order_type", mSortPosition + 1)
                .execute(new JsonCallback<Base_Class_List_Info<NearPointPCInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<NearPointPCInfo> o, Call call, Response response) {
                        loadDataSuccess(o);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {
                                switch (e.getMessage()) {
                                    case "103":
                                        mSearchAddress.setText("城市未开放哦");
                                        mCommonAdapter.clearAll();
                                        mRecyclerView.showEmpty();
                                        break;
                                    default:
                                        showFiveToast(ConstansUtil.SERVER_ERROR);
                                        break;
                                }
                            }
                        });
                    }
                });
    }

    private void setTextViewPadding() {
        mCheckTextViews[0].getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mCheckTextViews[0].getWidth();
                float textWidth = mCheckTextViews[0].getPaint().measureText(getText(mCheckTextViews[0]));
                int padding = (int) (width / 2 - textWidth / 2 - dpToPx(10));
                initTextView(padding);
                mCheckTextViews[0].getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initTextView(int padding) {
        for (int i = 0; i < 3; i++) {
            mCheckTextViews[i].setCheckDrawable(R.mipmap.xialaup);
            mCheckTextViews[i].setNoCheckDrawble(R.mipmap.xiala);
            mCheckTextViews[i].setPadding(padding, mCheckTextViews[i].getPaddingTop(), padding, mCheckTextViews[i].getPaddingBottom());
            mCheckTextViews[i].setChecked(false);
            final int finalI = i;
            mCheckTextViews[i].setOnCheckChangeListener(new CheckTextView.OnCheckChangeListener() {
                @Override
                public void onCheckChange(boolean isCheck) {
                    if (isCheck) {
                        if (mRequestPostion != -1) {
                            mCheckTextViews[mRequestPostion].setChecked(false);
                        }
                        mRequestPostion = finalI;
                        showFilter();
                    } else {
                        hideFilter();
                    }
                    mCheckTextViews[finalI].setChecked(isCheck);
                }
            });

        }
    }

    private void updateTextView(int typePosition, String filterParam) {
        mCheckTextViews[typePosition].setChecked(false);
        if (!filterParam.equals(getText(mCheckTextViews[typePosition]))) {
            updatePadding(typePosition, filterParam);
        } else {
            mCheckTextViews[typePosition].setText(filterParam);
        }

        hideFilter();
        mStartItme = 0;
        loadData();
    }

    private void updatePadding(final int typePosition, String filterParam) {
        mCheckTextViews[typePosition].setText(filterParam);
        mCheckTextViews[typePosition].getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mCheckTextViews[typePosition].getWidth();
                float textWidth = mCheckTextViews[typePosition].getPaint().measureText(getText(mCheckTextViews[typePosition]));
                int padding = (int) (width / 2 - textWidth / 2 - dpToPx(10));
                mCheckTextViews[typePosition].setPadding(padding, mCheckTextViews[typePosition].getPaddingTop(), padding, mCheckTextViews[typePosition].getPaddingBottom());
                mCheckTextViews[typePosition].getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void showFilter() {
        if (mFilterRecyclerView == null) {
            mFilterRecyclerView = findViewById(R.id.filter_rv);
            mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mFilterRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, false, true));
            mFilterAdapter = new FilterAdapter();
            mFilterRecyclerView.setAdapter(mFilterAdapter);

            mFilterBackgound = findViewById(R.id.filter_bg);
            mFilterBackgound.setOnClickListener(this);
        }

        mFilterAdapter.setNewData(getFilterList());
        showView(mFilterBackgound);
    }

    private void hideFilter() {
        mFilterAdapter.clearAll();
        goneView(mFilterBackgound);
    }

    private List<FilterInfo> getFilterList() {
        switch (mRequestPostion) {
            case 0:
                return getSortList();
            case 1:
                return getTypeList();
            case 2:
                return getDistanceList();
            default:
                return getSortList();
        }
    }

    private List<FilterInfo> getSortList() {
        if (mSortList == null) {
            mSortList = new ArrayList<>(4);
            mSortList.add(new FilterInfo(0, true, "智能排序"));
            mSortList.add(new FilterInfo(0, false, "均价最低"));
            mSortList.add(new FilterInfo(0, false, "离我最近"));
            mSortList.add(new FilterInfo(0, false, "评分最高"));
        }
        return mSortList;
    }

    private List<FilterInfo> getTypeList() {
        if (mTypeList == null) {
            mTypeList = new ArrayList<>(2);
            mTypeList.add(new FilterInfo(1, true, "停车场"));
            mTypeList.add(new FilterInfo(1, false, "充电站"));
        }
        return mTypeList;
    }

    private List<FilterInfo> getDistanceList() {
        if (mDistanceList == null) {
            mDistanceList = new ArrayList<>(5);
            mDistanceList.add(new FilterInfo(2, false, "2km内"));
            mDistanceList.add(new FilterInfo(2, false, "5km内"));
            mDistanceList.add(new FilterInfo(2, true, "10km内"));
            mDistanceList.add(new FilterInfo(2, false, "15km内"));
            mDistanceList.add(new FilterInfo(2, false, "20km内"));
        }
        return mDistanceList;
    }

    private int convertDistance() {
        switch (mDistancePosition) {
            case 0:
                return 2;
            case 1:
                return 5;
            case 2:
                return 10;
            case 3:
                return 15;
            default:
                return 20;
        }
    }

    private void getAddress() {
        if (mGeocodeSearch == null) {
            mGeocodeSearch = new GeocodeSearch(this);
            mGeocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                /**
                 * 逆地理编码回调
                 */
                @Override
                public void onRegeocodeSearched(final RegeocodeResult result, int rCode) {
                    if (rCode == 1000) {
                        if (result.getRegeocodeAddress().getFormatAddress().equals("")) {
                            mSearchAddress.setText("不支持当前位置，请点击尝试查找其他位置");
                        } else {
                            if (mMaxTextNum <= 0) {
                                mMaxTextNum = (int) ((mSearchAddress.getWidth() - dpToPx(16)) / mSearchAddress.getTextSize());
                                if (mMaxTextNum <= 8) {
                                    //getWidth可能为0，或者有这么小屏幕的手机?
                                    mMaxTextNum = 10;
                                }
                            }

                            StringBuilder address = new StringBuilder(result.getRegeocodeAddress().getFormatAddress());
                            int position;
                            if (address.length() >= mMaxTextNum) {
                                if ((position = address.indexOf("自治州")) != -1 && position != address.length() - 1) {
                                    address.delete(0, position + 3);
                                } else if ((position = address.indexOf("市")) != -1 && position != address.length() - 1) {
                                    address.delete(0, position + 1);
                                } else if ((position = address.indexOf("地区")) != -1) {
                                    address.delete(0, position + 2);
                                } else if ((position = address.indexOf("行政区")) != -1) {
                                    //河南省
                                    address.delete(0, position + 3);
                                } else if ((position = address.indexOf("盟")) != -1 && position != address.length() - 1) {
                                    //内蒙古
                                    address.delete(0, position + 1);
                                } else if ((position = address.indexOf("省")) != -1) {
                                    address.delete(0, position + 1);
                                }
                            }

                            if (address.length() >= mMaxTextNum) {
                                //如果还是显示不完全继续删
                                if ((position = address.indexOf("街道")) != -1 && position != address.length() - 1) {
                                    address.delete(0, position + 2);
                                } else if ((position = address.indexOf("区")) != -1 && position != address.length() - 1) {
                                    address.delete(0, position + 1);
                                } else if ((position = address.indexOf("县")) != -1 && position != address.length() - 1) {
                                    address.delete(0, position + 1);
                                } else if ((position = address.indexOf("镇")) != -1 && position != address.length() - 1) {
                                    address.delete(0, position + 1);
                                }
                            }

                            if (address.length() > mMaxTextNum) {
                                //全删了等下重新获取
                                address.delete(0, address.length());
                            }

                            if (address.length() == 0 || address.charAt(0) == ' ') {
                                address.append(result.getRegeocodeAddress().getDistrict());
                                address.append(result.getRegeocodeAddress().getTownship());
                                if (!result.getRegeocodeAddress().getPois().isEmpty()) {
                                    address.append(result.getRegeocodeAddress().getPois().get(0));
                                }
                            }

                            mSearchAddress.setText(address);
                        }
                    }
                }

                @Override
                public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

                }

            });
        }
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(mLatLng.latitude, mLatLng.longitude), 200, GeocodeSearch.AMAP);
        mGeocodeSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_iv:
            case R.id.search_address:
                startActivityForResult(SearchAddressActivity.class, ConstansUtil.REQUSET_CODE, "whatPage", "2",
                        "keyword", getText(mSearchAddress), ConstansUtil.CITY_CODE, mCityCode);
                break;
            case R.id.filter_bg:
                mCheckTextViews[mRequestPostion].setChecked(false);
                hideFilter();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && resultCode == RESULT_OK && data != null) {
            mCityCode = data.getStringExtra(ConstansUtil.CITY_CODE);
            mLatLng = new LatLng(data.getDoubleExtra(ConstansUtil.LATITUDE, mLatLng.latitude),
                    data.getDoubleExtra(ConstansUtil.LONGITUDE, mLatLng.longitude));
            mSearchAddress.setText(data.getStringExtra(ConstansUtil.INTENT_MESSAGE));
            onRefresh();
        }
    }

    private class FilterAdapter extends BaseAdapter<FilterInfo> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, final FilterInfo filterInfo, final int position) {
            holder.setText(R.id.common_tv, filterInfo.getContent())
                    .setTextColor(R.id.common_tv, filterInfo.isChoose() ? ConstansUtil.B1_COLOR : ConstansUtil.G6_COLOR)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (filterInfo.getType()) {
                                case 0:
                                    get(mSortPosition).setChoose(false);

                                    mSortPosition = position;
                                    get(mSortPosition).setChoose(true);
                                    updateTextView(0, filterInfo.getContent());
                                    break;
                                case 1:
                                    get(mTypePosition).setChoose(false);

                                    mTypePosition = position;
                                    get(mTypePosition).setChoose(true);
                                    updateTextView(1, filterInfo.getContent());
                                    break;
                                case 2:
                                    get(mDistancePosition).setChoose(false);

                                    mDistancePosition = position;
                                    get(mDistancePosition).setChoose(true);
                                    updateTextView(2, filterInfo.getContent());
                                    break;
                            }
                        }
                    });
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_center_text_layout;
        }
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_park_or_charge_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final NearPointPCInfo nearPointPCInfo, int position) {
        if (nearPointPCInfo.getDistance() == null) {
            int distance = (int) AMapUtils.calculateLineDistance(mLatLng, new LatLng(nearPointPCInfo.getLatitude(), nearPointPCInfo.getLongitude()));
            if (distance >= 1000) {
                nearPointPCInfo.setDistance(mDecimalFormat.format(distance / 1000) + "km");
            } else {
                nearPointPCInfo.setDistance(distance + "m");
            }
        }

        holder.setText(R.id.station_type, nearPointPCInfo.isParkSpace() ? "停车场" : "充电站")
                .setText(R.id.charge_price, "均价" + nearPointPCInfo.getPrice() + "元/" + (nearPointPCInfo.isParkSpace() ? "小时" : "度")
                        , 2, 2 + String.valueOf(nearPointPCInfo.getPrice()).length(), ConstansUtil.Y3_COLOR, spToPx(12))
                .setText(R.id.location_description, nearPointPCInfo.getAddress())
                .setText(R.id.park_or_charge_distance, nearPointPCInfo.getDistance())
                .setStartProgress(R.id.park_or_charge_cb, nearPointPCInfo.getGrade())
                .showViewOrGone(R.id.free_park_space_tv, nearPointPCInfo.isParkSpace())
                .showViewOrGone(R.id.free_park_space, nearPointPCInfo.isParkSpace())
                .showViewOrGone(R.id.free_park_time_tv, nearPointPCInfo.isParkSpace())
                .showViewOrGone(R.id.free_park_time, nearPointPCInfo.isParkSpace())
                .showViewOrGone(R.id.can_charge_tv, nearPointPCInfo.isParkSpace() && nearPointPCInfo.canCharge())
                .showViewOrGone(R.id.can_charge, nearPointPCInfo.isParkSpace() && nearPointPCInfo.canCharge())
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (nearPointPCInfo.isParkSpace()) {
                            startActivity(ParkspaceDetailActivity.class, ConstansUtil.PARK_LOT_ID, nearPointPCInfo.getId(),
                                    ConstansUtil.CITY_CODE, mCityCode);
                        } else {
                            startActivity(ChargestationDetailActivity.class, ConstansUtil.CHARGE_ID, nearPointPCInfo.getId(),
                                    ConstansUtil.CITY_CODE, mCityCode);
                        }
                    }
                });

        if (nearPointPCInfo.isParkSpace()) {
            holder.setText(R.id.free_park_space, "空闲车位" + nearPointPCInfo.getFreeNumber() + "个", 4,
                    4 + String.valueOf(nearPointPCInfo.getFreeNumber()).length(), ConstansUtil.Y3_COLOR, spToPx(12))
                    .setText(R.id.free_park_time, "前" + nearPointPCInfo.getFreeTime() + "分钟免费");
        }
    }

    @Override
    protected int resourceId() {
        return R.layout.activity_park_or_charge_layout;
    }

}
