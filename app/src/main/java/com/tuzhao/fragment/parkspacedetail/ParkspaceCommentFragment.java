package com.tuzhao.fragment.parkspacedetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.adapter.ParkspaceCommentAdapter;
import com.tuzhao.fragment.BaseFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Space_Info;
import com.tuzhao.info.ParkspaceCommentInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.swipetoloadlayout.ChangeScrollStateCallback;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/11/8.
 */

public class ParkspaceCommentFragment extends BaseFragment {

    /**
     * UI
     */
    private View mContentView;
    private SuperRefreshRecyclerView mRecycleview;
    private ParkspaceCommentAdapter mAdapter;
    private TextView textview_all, textview_good, textview_bad;
    private LinearLayout linearlayout_nodata, linearlayout_seeimg;
    private ImageView imageview_seeic;

    /**
     * 页面相关
     */
    private String parkspace_id, city_code;
    private Park_Space_Info parkspace_info = null;
    private ArrayList<ParkspaceCommentInfo> mCommentData = new ArrayList<>();
    private ArrayList<ParkspaceCommentInfo> mChooseData = new ArrayList<>();
    private boolean isFirstIn = true, isNoData = false, isNoInternet = false, needSeeImg = false;
    private int mLoadingtimes = 0, type = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_parkspacecomment_layout, container, false);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件

        return mContentView;
    }

    private void initView() {
        mRecycleview = (SuperRefreshRecyclerView) mContentView.findViewById(R.id.id_fragment_parkspacecomment_layout_recycleview);
        mRecycleview.init(new LinearLayoutManager(mContext), new onMyRefresh(), new onMyLoadMore());
        mRecycleview.setRefreshEnabled(true);
        mRecycleview.setLoadingMoreEnable(true);
        mRecycleview.setChangeScrollStateCallback(new ChangeScrollStateCallback() {
            @Override
            public void change(int c) {
                switch (c) {
                    case 2:
                        Glide.with(mContext).pauseRequests();
                        break;
                    case 0:
                        Glide.with(mContext).resumeRequests();
                        break;
                    case 1:
                        Glide.with(mContext).resumeRequests();
                        break;
                }
            }
        });
        mAdapter = new ParkspaceCommentAdapter(mContext, mChooseData);
        mRecycleview.setAdapter(mAdapter);

        textview_all = (TextView) mContentView.findViewById(R.id.id_fragment_parkspacecomment_layout_textview_all);
        textview_good = (TextView) mContentView.findViewById(R.id.id_fragment_parkspacecomment_layout_textview_good);
        textview_bad = (TextView) mContentView.findViewById(R.id.id_fragment_parkspacecomment_layout_textview_bad);
        imageview_seeic = (ImageView) mContentView.findViewById(R.id.id_fragment_parkspacecomment_layout_imageview_seeic);

        linearlayout_nodata = (LinearLayout) mContentView.findViewById(R.id.id_fragment_parkspacecomment_linearlayout_nodata);

        linearlayout_seeimg = (LinearLayout) mContentView.findViewById(R.id.id_fragment_parkspacecomment_layout_linearlayout_seeimg);
    }

    private void initData() {
        parkspace_info = (Park_Space_Info) getArguments().getSerializable("parkspace_info");

        if (parkspace_info == null) {
            parkspace_id = getArguments().getString("parkspace_id");
            city_code = getArguments().getString("city_code");
            requestGetParkspceComment(parkspace_id, city_code, null, null);
        } else {
            requestGetParkspceComment(parkspace_info.getId(), parkspace_info.getCity_code(), null, null);
        }
    }

    private void initEvent() {
        textview_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textview_all.setBackground(ContextCompat.getDrawable(mContext, R.drawable.yuan_little_green_lt_lb_5dp));
                textview_all.setTextColor(ContextCompat.getColor(mContext, R.color.w0));
                textview_good.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nocolor));
                textview_good.setTextColor(ContextCompat.getColor(mContext, R.color.b1));
                textview_bad.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nocolor));
                textview_bad.setTextColor(ContextCompat.getColor(mContext, R.color.b1));
                type = 1;
                initAdapterData();
            }
        });
        textview_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 2;
                textview_all.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nocolor));
                textview_all.setTextColor(ContextCompat.getColor(mContext, R.color.b1));
                textview_good.setBackgroundColor(ContextCompat.getColor(mContext, R.color.y2));
                textview_good.setTextColor(ContextCompat.getColor(mContext, R.color.w0));
                textview_bad.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nocolor));
                textview_bad.setTextColor(ContextCompat.getColor(mContext, R.color.b1));
                initAdapterData();
            }
        });
        textview_bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textview_all.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nocolor));
                textview_all.setTextColor(ContextCompat.getColor(mContext, R.color.b1));
                textview_good.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nocolor));
                textview_good.setTextColor(ContextCompat.getColor(mContext, R.color.b1));
                textview_bad.setBackground(ContextCompat.getDrawable(mContext, R.drawable.yuan_little_green_rt_rb_5dp));
                textview_bad.setTextColor(ContextCompat.getColor(mContext, R.color.w0));
                type = 3;
                initAdapterData();
            }
        });

        linearlayout_seeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (needSeeImg) {
                    needSeeImg = false;
                    imageview_seeic.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_weixuanze));
                    initAdapterData();
                } else {
                    needSeeImg = true;
                    imageview_seeic.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_youhuixuanze));
                    initAdapterData();
                }
            }
        });
    }

    private void requestGetParkspceComment(String parkspace_id, String city_code, String startItem, String pageSize) {
        OkGo.post(HttpConstants.getParkspceComment)
                .tag(this)
                .params("parkspace_id", parkspace_id)
                .params("citycode", city_code)
                .params("startItem", startItem == null ? "0" : startItem)
                .params("pageSize", pageSize == null ? "15" : pageSize)
                .execute(new JsonCallback<Base_Class_List_Info<ParkspaceCommentInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkspaceCommentInfo> datas, Call call, Response response) {

                        if (isFirstIn) {
                            isFirstIn = false;
                            mCommentData.addAll(datas.data);
                            mChooseData.addAll(datas.data);
                            datas.data.clear();
                            mAdapter = new ParkspaceCommentAdapter(mContext, mChooseData);
                            mRecycleview.setAdapter(mAdapter);
                            linearlayout_nodata.setVisibility(View.GONE);
                        }

                        if (mRecycleview.isRefreshing()) {
                            linearlayout_nodata.setVisibility(View.GONE);
                            isNoInternet = false;
                            mRecycleview.setRefreshing(false);
                            mCommentData.clear();
                            mCommentData.addAll(datas.data);
                            datas.data.clear();
                            initAdapterData();
                        }
                        if (mRecycleview.isLoadingMore()) {
                            if (datas.data.size() > 0) {
                                isNoInternet = false;
                                mCommentData.addAll(datas.data);
                                ArrayList<ParkspaceCommentInfo> infodata = new ArrayList<>();
                                if (type == 2) {
                                    for (ParkspaceCommentInfo info : datas.data) {
                                        if (needSeeImg) {
                                            if (info.getImg_url() != null) {
                                                try {
                                                    if (!(info.getImg_url().equals("") || info.getImg_url().equals("-1"))) {
                                                        if (new Float(info.getGrade()) >= 4) {
                                                            infodata.add(info);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                }
                                            }
                                        } else {
                                            try {
                                                if (new Float(info.getGrade()) >= 4) {
                                                    infodata.add(info);
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                    }
                                } else if (type == 3) {
                                    for (ParkspaceCommentInfo info : datas.data) {
                                        if (needSeeImg) {
                                            if (info.getImg_url() != null) {
                                                try {
                                                    if (!(info.getImg_url().equals("") || info.getImg_url().equals("-1"))) {
                                                        if (new Float(info.getGrade()) < 4) {
                                                            infodata.add(info);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                }
                                            }
                                        } else {
                                            try {
                                                if (new Float(info.getGrade()) < 4) {
                                                    infodata.add(info);
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                    }
                                } else {
                                    infodata.addAll(datas.data);
                                }
                                if (infodata.size() > 0) {
                                    mChooseData.addAll(datas.data);
                                    mAdapter.notifyDataSetChanged();
                                    infodata.clear();
                                }
                                mRecycleview.setLoadingMore(false);
                            } else {
                                isNoData = true;
                                isNoInternet = false;
                                mRecycleview.setLoadingMore(false);
                                Toast.makeText(mContext, "没有更多数据", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!DensityUtil.isException(mContext, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    if (isFirstIn) {
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }
                                    if (mRecycleview.isRefreshing()) {
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                        isNoInternet = false;
                                        mRecycleview.setRefreshing(false);
                                        mCommentData.clear();
                                        mAdapter.notifyDataSetChanged();
                                        MyToast.showToast(mContext, "没有数据", 5);
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        isNoData = true;
                                        isNoInternet = false;
                                        mRecycleview.setLoadingMore(false);
                                        MyToast.showToast(mContext, "没有更多数据", 5);
                                    }
                                    break;
                                case 103:
                                    if (isFirstIn) {
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }
                                    if (mRecycleview.isRefreshing()) {
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                        isNoInternet = false;
                                        mRecycleview.setRefreshing(false);
                                        mCommentData.clear();
                                        mAdapter.notifyDataSetChanged();
                                        MyToast.showToast(mContext, "没有数据", 5);
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        isNoData = true;
                                        isNoInternet = false;
                                        mRecycleview.setLoadingMore(false);
                                        MyToast.showToast(mContext, "没有更多数据", 5);
                                    }
                                    break;
                                case 104:
                                    if (isFirstIn) {
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }
                                    if (mRecycleview.isRefreshing()) {
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                        isNoInternet = false;
                                        mRecycleview.setRefreshing(false);
                                        mCommentData.clear();
                                        mAdapter.notifyDataSetChanged();
                                        MyToast.showToast(mContext, "没有数据", 5);
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        isNoData = true;
                                        isNoInternet = false;
                                        mRecycleview.setLoadingMore(false);
                                        MyToast.showToast(mContext, "没有更多数据", 5);
                                    }
                                    break;
                                case 105:
                                    if (isFirstIn) {
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }
                                    if (mRecycleview.isRefreshing()) {
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                        isNoInternet = false;
                                        mRecycleview.setRefreshing(false);
                                        mCommentData.clear();
                                        mAdapter.notifyDataSetChanged();
                                        MyToast.showToast(mContext, "没有数据", 5);
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        isNoData = true;
                                        isNoInternet = false;
                                        mRecycleview.setLoadingMore(false);
                                        MyToast.showToast(mContext, "没有更多数据", 5);
                                    }
                                    break;
                                case 901:
                                    MyToast.showToast(mContext, "服务器正在维护中", 5);
                                    if (mRecycleview.isRefreshing()) {
                                        mRecycleview.setRefreshing(false);
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        mRecycleview.setLoadingMore(false);
                                    }
                                    break;
                            }
                        }
                    }
                });
    }

    private class onMyRefresh implements OnRefreshListener {
        @Override
        public void onRefresh() {
            //开始下拉刷新
            isNoData = false;
            mLoadingtimes = 0;
            if (parkspace_info == null) {
                parkspace_id = getArguments().getString("parkspace_id");
                city_code = getArguments().getString("city_code");
                requestGetParkspceComment(parkspace_id, city_code, null, null);
            } else {
                requestGetParkspceComment(parkspace_info.getId(), parkspace_info.getCity_code(), null, null);
            }
        }
    }

    private class onMyLoadMore implements OnLoadMoreListener {
        @Override
        public void onLoadMore() {
            //开始上拉加载更多数据
            if (!isNoData) {
                if (!isNoInternet) {
                    mLoadingtimes++;
                }
                if (parkspace_info == null) {
                    parkspace_id = getArguments().getString("parkspace_id");
                    city_code = getArguments().getString("city_code");
                    requestGetParkspceComment(parkspace_id, city_code, mCommentData.size() + "", null);
                } else {
                    requestGetParkspceComment(parkspace_info.getId(), parkspace_info.getCity_code(), mCommentData.size() + "", null);
                }
            } else {
                mRecycleview.setLoadingMore(false);
                Toast.makeText(mContext, "没有更多数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initAdapterData() {
        switch (type) {
            case 1:
                mChooseData.clear();
                if (needSeeImg) {
                    for (ParkspaceCommentInfo info : mCommentData) {
                        if (!(info.getImg_url().equals("") || info.getImg_url().equals("-1"))) {
                            mChooseData.add(info);
                        }
                    }
                } else {
                    mChooseData.addAll(mCommentData);
                }
                mAdapter.notifyDataSetChanged();
                break;
            case 2:
                mChooseData.clear();
                for (ParkspaceCommentInfo info : mCommentData) {
                    if (needSeeImg) {
                        if (!(info.getImg_url().equals("") || info.getImg_url().equals("-1"))) {
                            try {
                                if (new Float(info.getGrade()) >= 4) {
                                    mChooseData.add(info);
                                }
                            } catch (Exception e) {
                            }
                        }
                    } else {
                        try {
                            if (new Float(info.getGrade()) >= 4) {
                                mChooseData.add(info);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            case 3:
                mChooseData.clear();
                for (ParkspaceCommentInfo info : mCommentData) {
                    if (needSeeImg) {
                        if (!(info.getImg_url().equals("") || info.getImg_url().equals("-1"))) {
                            try {
                                if (new Float(info.getGrade()) < 4) {
                                    mChooseData.add(info);
                                }
                            } catch (Exception e) {
                            }
                        }
                    } else {
                        try {
                            if (new Float(info.getGrade()) < 4) {
                                mChooseData.add(info);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
        }
        if (mChooseData.size() > 0) {
            linearlayout_nodata.setVisibility(View.GONE);
        } else {
            linearlayout_nodata.setVisibility(View.VISIBLE);
        }
    }
}
