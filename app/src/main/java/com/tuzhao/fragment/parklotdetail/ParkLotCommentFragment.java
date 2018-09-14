package com.tuzhao.fragment.parklotdetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cb.ratingbar.CBRatingBar;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.BigPictureActivity;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.fragment.base.BaseFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkLotInfo;
import com.tuzhao.info.ParkspaceCommentInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/11/8.
 */

public class ParkLotCommentFragment extends BaseFragment {

    /**
     * UI
     */
    private View mContentView;
    private SuperRefreshRecyclerView mRecycleview;
    private ParkSpaceCommentAdapter mAdapter;
    private TextView textview_all, textview_good, textview_bad;
    private LinearLayout linearlayout_nodata, linearlayout_seeimg;
    private ImageView imageview_seeic;

    /**
     * 页面相关
     */
    private String parkspace_id, city_code;
    private ParkLotInfo parkspace_info = null;
    private ArrayList<ParkspaceCommentInfo> mCommentData = new ArrayList<>();
    private boolean isFirstIn = true, isNoData = false, isNoInternet = false, needSeeImg = false;
    private int mLoadingtimes = 0, type = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_parkspacecomment_layout, container, false);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件

        return mContentView;
    }

    private void initView() {
        mRecycleview = mContentView.findViewById(R.id.id_fragment_parkspacecomment_layout_recycleview);
        mRecycleview.init(new LinearLayoutManager(mContext), new onMyRefresh(), new onMyLoadMore());
        mRecycleview.setRefreshEnabled(true);
        mRecycleview.setLoadingMoreEnable(true);
        /*mRecycleview.setChangeScrollStateCallback(new ChangeScrollStateCallback() {
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
        });*/
        mAdapter = new ParkSpaceCommentAdapter(mRecycleview.getRecyclerView());
        mRecycleview.setAdapter(mAdapter);

        textview_all = mContentView.findViewById(R.id.id_fragment_parkspacecomment_layout_textview_all);
        textview_good = mContentView.findViewById(R.id.id_fragment_parkspacecomment_layout_textview_good);
        textview_bad = mContentView.findViewById(R.id.id_fragment_parkspacecomment_layout_textview_bad);
        imageview_seeic = mContentView.findViewById(R.id.id_fragment_parkspacecomment_layout_imageview_seeic);

        linearlayout_nodata = mContentView.findViewById(R.id.id_fragment_parkspacecomment_linearlayout_nodata);

        linearlayout_seeimg = mContentView.findViewById(R.id.id_fragment_parkspacecomment_layout_linearlayout_seeimg);
    }

    private void initData() {
        if (getArguments() != null) {
            parkspace_info = (ParkLotInfo) getArguments().getSerializable("parkspace_info");
        }

        if (parkspace_info == null) {
            if (getArguments() != null) {
                parkspace_id = getArguments().getString("parkspace_id");
                city_code = getArguments().getString("city_code");
                requestGetParkspceComment(parkspace_id, city_code, null, null);
            }
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
                            mAdapter.notifyAddData(datas.data);
                            datas.data.clear();
                           /* mAdapter = new ParkSpaceCommentAdapter(mRecycleview.getRecyclerView());
                            mRecycleview.setAdapter(mAdapter);*/
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
                                                        if (Float.valueOf(info.getGrade()) >= 4) {
                                                            infodata.add(info);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else {
                                            try {
                                                if (Float.valueOf(info.getGrade()) >= 4) {
                                                    infodata.add(info);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } else if (type == 3) {
                                    for (ParkspaceCommentInfo info : datas.data) {
                                        if (needSeeImg) {
                                            if (info.getImg_url() != null) {
                                                try {
                                                    if (!(info.getImg_url().equals("") || info.getImg_url().equals("-1"))) {
                                                        if (Float.valueOf(info.getGrade()) < 4) {
                                                            infodata.add(info);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else {
                                            try {
                                                if (Float.valueOf(info.getGrade()) < 4) {
                                                    infodata.add(info);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } else {
                                    infodata.addAll(datas.data);
                                }
                                if (infodata.size() > 0) {
                                    mAdapter.notifyAddData(datas.data);
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
                mAdapter.clearAll();
                if (needSeeImg) {
                    for (ParkspaceCommentInfo info : mCommentData) {
                        if (!(info.getImg_url().equals("") || info.getImg_url().equals("-1"))) {
                            mAdapter.notifyAddData(info);
                        }
                    }
                } else {
                    mAdapter.notifyAddData(mCommentData);
                }
                break;
            case 2:
                mAdapter.clearAll();
                for (ParkspaceCommentInfo info : mCommentData) {
                    if (needSeeImg) {
                        if (!(info.getImg_url().equals("") || info.getImg_url().equals("-1"))) {
                            try {
                                if (Float.valueOf(info.getGrade()) >= 4) {
                                    mAdapter.notifyAddData(info);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            if (Float.valueOf(info.getGrade()) >= 4) {
                                mAdapter.notifyAddData(info);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case 3:
                mAdapter.clearAll();
                for (ParkspaceCommentInfo info : mCommentData) {
                    if (needSeeImg) {
                        if (!(info.getImg_url().equals("") || info.getImg_url().equals("-1"))) {
                            try {
                                if (Float.valueOf(info.getGrade()) < 4) {
                                    mAdapter.notifyAddData(info);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            if (Float.valueOf(info.getGrade()) < 4) {
                                mAdapter.notifyAddData(info);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
        }
        if (mAdapter.getDataSize() > 0) {
            linearlayout_nodata.setVisibility(View.GONE);
        } else {
            linearlayout_nodata.setVisibility(View.VISIBLE);
        }
    }

    private void showView(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);

        }
    }

    private void hideView(View view) {
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }

    class ParkSpaceCommentAdapter extends BaseAdapter<ParkspaceCommentInfo> {

        private ArrayList<String> mCommentPictures;

        ParkSpaceCommentAdapter(RecyclerView recyclerView) {
            super(recyclerView);
            mCommentPictures = new ArrayList<>();
        }

        @Override
        protected void conver(@NonNull BaseViewHolder holder, ParkspaceCommentInfo parkspaceCommentInfo, int position) {
            holder.showCircleUserPic(R.id.id_item_parkspacemoment_layout_imageview_user, parkspaceCommentInfo.getUser_img_url())
                    .setText(R.id.id_item_parkspacemoment_layout_textview_user, parkspaceCommentInfo.getNickname().equals("-1") ?
                            parkspaceCommentInfo.getUsername().substring(0, 3) + "*****" +
                                    parkspaceCommentInfo.getUsername().substring(8, parkspaceCommentInfo.getUsername().length()) :
                            parkspaceCommentInfo.getNickname())
                    .setText(R.id.id_item_parkspacemoment_layout_textview_grade, parkspaceCommentInfo.getGrade() == null ? (4 + "分") : parkspaceCommentInfo.getGrade() + "分")
                    .setText(R.id.id_item_parkspacemoment_layout_textview_content, parkspaceCommentInfo.getContent())
                    .setText(R.id.id_item_parkspacemoment_layout_textview_parktime, "停车时间 " + parkspaceCommentInfo.getPark_time());
            CBRatingBar cbRatingBar = holder.getView(R.id.id_item_parkspacecomment_layout_cbratingbar);
            cbRatingBar.setStarProgress(parkspaceCommentInfo.getGrade() == null ? 80 : (Float.valueOf(parkspaceCommentInfo.getGrade()) * 100 / 5));
            if (position == getDataSize() - 1) {
                holder.getView(R.id.id_item_parkspacemoment_layout_imageview_downline).setVisibility(View.GONE);
            }
            LinearLayout linearlayout_show = holder.getView(R.id.id_item_parkspacemoment_layout_linearlayout_show);
            ImageView imageview_show1 = holder.getView(R.id.id_item_parkspacemoment_layout_imageview_show1);
            ImageView imageview_show2 = holder.getView(R.id.id_item_parkspacemoment_layout_imageview_show2);
            ImageView imageview_show3 = holder.getView(R.id.id_item_parkspacemoment_layout_imageview_show3);

            if (parkspaceCommentInfo.getImg_url().equals("-1") || parkspaceCommentInfo.getImg_url().equals("")) {
                hideView(linearlayout_show);
            } else {
                final String img_Url[] = parkspaceCommentInfo.getImg_url().split(",");
                showView(linearlayout_show);
                if (img_Url.length == 1) {
                    showView(imageview_show1);
                    hideView(imageview_show2);
                    hideView(imageview_show3);
                    ImageUtil.showImpPic(imageview_show1, HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[0]);
                    imageview_show1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startBigPictureActivity(HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[0]);
                        }
                    });
                } else if (img_Url.length == 2) {
                    showView(imageview_show1);
                    showView(imageview_show2);
                    hideView(imageview_show3);
                    ImageUtil.showImpPic(imageview_show1, HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[0]);
                    imageview_show1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startBigPictureActivity(HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[0]);
                        }
                    });
                    ImageUtil.showImpPic(imageview_show2, HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[1]);
                    imageview_show2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startBigPictureActivity(HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[1]);
                        }
                    });
                } else if (img_Url.length == 3) {
                    showView(imageview_show1);
                    showView(imageview_show2);
                    showView(imageview_show3);
                    ImageUtil.showImpPic(imageview_show1, HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[0]);
                    imageview_show1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startBigPictureActivity(HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[0]);
                        }
                    });
                    ImageUtil.showImpPic(imageview_show2, HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[1]);
                    imageview_show2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startBigPictureActivity(HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[1]);
                        }
                    });
                    ImageUtil.showImpPic(imageview_show3, HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[2]);
                    imageview_show3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startBigPictureActivity(HttpConstants.ROOT_IMG_URL_PSCOM + img_Url[2]);
                        }
                    });
                }
            }
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_parkspacecomment_layout;
        }

        @Override
        public void notifyAddData(ParkspaceCommentInfo parkspaceCommentInfo) {
            super.notifyAddData(parkspaceCommentInfo);
            String img_Url[] = parkspaceCommentInfo.getImg_url().split(",");
            if (img_Url.length > 0 && !img_Url[0].equals("-1")) {
                for (String s : img_Url) {
                    mCommentPictures.add(HttpConstants.ROOT_IMG_URL_PSCOM + s);
                }
            }
        }

        @Override
        public void notifyAddData(List<ParkspaceCommentInfo> data) {
            super.notifyAddData(data);
            String img_Url[];
            for (ParkspaceCommentInfo parkspaceCommentInfo : data) {
                img_Url = parkspaceCommentInfo.getImg_url().split(",");
                if (img_Url.length > 0 && !img_Url[0].equals("-1")) {
                    for (String s : img_Url) {
                        mCommentPictures.add(HttpConstants.ROOT_IMG_URL_PSCOM + s);
                    }
                }
            }
        }

        @Override
        public void clearAll() {
            super.clearAll();
            mCommentPictures.clear();
        }

        private void startBigPictureActivity(String pictureUrl) {
            Intent intent = new Intent(getActivity(), BigPictureActivity.class);
            intent.putStringArrayListExtra("picture_list", mCommentPictures);
            intent.putExtra("position", mCommentPictures.indexOf(pictureUrl));
            startActivity(intent);
        }

    }

}
