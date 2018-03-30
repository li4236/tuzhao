package com.tuzhao.fragment.parkorder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.adapter.ParkOrderAdapter;
import com.tuzhao.fragment.BaseFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.TimeManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.swipetoloadlayout.ChangeScrollStateCallback;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/6/26.
 */

public class CancleParkOrderFragment extends BaseFragment {

    /**
     * UI
     */
    private View mContentView;
    private SuperRefreshRecyclerView mRecycleview;
    private ParkOrderAdapter mAdapter;
    private DateUtil dateUtil = new DateUtil();
    private CustomDialog mCustomDialog;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout linearlayout_nodata;

    /**
     * 页面相关
     */
    private List<ParkOrderInfo> mOrdersData = new ArrayList<>();
    private boolean isFirstIn = true;
    private int mLoadingtimes = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_allorderlist_layout, container, false);
            initView();//初始化控件
            initData();//初始化数据
            initEvent();//初始化事件
        }
        return mContentView;
    }

    private void initView() {
        linearLayoutManager = new LinearLayoutManager(mContext);
        mRecycleview = (SuperRefreshRecyclerView) mContentView.findViewById(R.id.id_fragment_allorderlist_layout_recycleview);
        mRecycleview.init(linearLayoutManager, new onMyRefresh(), new onMyLoadMore());
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
        mAdapter = new ParkOrderAdapter(mContext, mOrdersData);
        mAdapter.setOnItemDeleteOrder(new ParkOrderAdapter.OnItemDeleteOrder() {
            @Override
            public void onItemDeleteOrder(int position) {
                initLoading("删除中...");
                deletelOrder(position);
            }
        });
        mRecycleview.setAdapter(mAdapter);
        linearlayout_nodata = (LinearLayout) mContentView.findViewById(R.id.id_fragment_allorderlist_layout_linearlayout_nodata);
    }

    private void initData() {
        requestGetAllOrders(null, null);
    }

    private void initEvent() {
    }

    private class onMyRefresh implements OnRefreshListener {
        @Override
        public void onRefresh() {
            //开始下拉刷新
            mLoadingtimes = 0;
            requestGetAllOrders(null, null);
        }
    }

    private class onMyLoadMore implements OnLoadMoreListener {
        @Override
        public void onLoadMore() {
            //开始上拉加载更多数据
            mLoadingtimes++;
            requestGetAllOrders((mLoadingtimes * 10) + "", null);
        }
    }

    private void requestGetAllOrders(String startItem, String pageSize) {
        OkGo.post(HttpConstants.getKindParkOrder)
                .tag(mContext)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("startItem", startItem == null ? "0" : startItem)
                .params("pageSize", pageSize == null ? "10" : pageSize)
                .params("order_status", "6")
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkOrderInfo> datas, Call call, Response response) {

                        if (isFirstIn) {
                            isFirstIn = false;
                            for (ParkOrderInfo info : datas.data) {
                                if (info.getOrder_status().equals("6")) {
                                    mOrdersData.add(info);
                                } else if (info.getOrder_status().equals("1")) {
                                    if (!dateUtil.compareTwoTime(TimeManager.getInstance().getNowTime(true, false), dateUtil.addTime(info.getOrder_starttime(), UserManager.getInstance().getUserInfo().getRide_time()), true)) {
                                        info.setOrder_status("7");
                                        mOrdersData.add(info);
                                    }
                                }
                            }
                            mAdapter = new ParkOrderAdapter(mContext, mOrdersData);
                            mAdapter.setOnItemDeleteOrder(new ParkOrderAdapter.OnItemDeleteOrder() {
                                @Override
                                public void onItemDeleteOrder(int position) {
                                    initLoading("删除中...");
                                    deletelOrder(position);
                                }
                            });
                            mRecycleview.setAdapter(mAdapter);
                            if (mOrdersData.size()<=0){
                                linearlayout_nodata.setVisibility(View.VISIBLE);
                            }
                        }

                        if (mRecycleview.isRefreshing()) {
                            mRecycleview.setRefreshing(false);
                            mOrdersData.clear();
                            List<ParkOrderInfo> fsa = new ArrayList<>();
                            for (ParkOrderInfo info : datas.data) {
                                if (info.getOrder_status().equals("6")) {
                                    fsa.add(info);
                                } else if (info.getOrder_status().equals("1")) {
                                    if (!dateUtil.compareTwoTime(TimeManager.getInstance().getNowTime(true, false), dateUtil.addTime(info.getOrder_starttime(), UserManager.getInstance().getUserInfo().getRide_time()), true)) {
                                        info.setOrder_status("7");
                                        fsa.add(info);
                                    }
                                }
                            }
                            mOrdersData.addAll(fsa);
                            mAdapter.notifyDataSetChanged();
                            if (mOrdersData.size()<=0){
                                linearlayout_nodata.setVisibility(View.VISIBLE);
                            }
                        }
                        if (mRecycleview.isLoadingMore()) {
                            if (datas.data.size() > 0) {
                                mRecycleview.setLoadingMore(false);
                                List<ParkOrderInfo> fsa = new ArrayList<>();
                                for (ParkOrderInfo info : datas.data) {
                                    if (info.getOrder_status().equals("6")) {
                                        fsa.add(info);
                                    } else if (info.getOrder_status().equals("1")) {
                                        if (!dateUtil.compareTwoTime(TimeManager.getInstance().getNowTime(true, false), dateUtil.addTime(info.getOrder_starttime(), UserManager.getInstance().getUserInfo().getRide_time()), true)) {
                                            info.setOrder_status("7");
                                            fsa.add(info);
                                        }
                                    }
                                }
                                mOrdersData.addAll(fsa);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                mRecycleview.setLoadingMore(false);
                                Toast.makeText(mContext, "没有更多数据", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!DensityUtil.isException(mContext, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    //
                                    if (isFirstIn) {
                                        isFirstIn = false;
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }
                                    if (mRecycleview.isRefreshing()) {
                                        mRecycleview.setRefreshing(false);
                                        mOrdersData.clear();
                                        mAdapter.notifyDataSetChanged();
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                        Toast.makeText(mContext, "没有数据哦", Toast.LENGTH_SHORT).show();
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        mLoadingtimes--;
                                        mRecycleview.setLoadingMore(false);
                                        Toast.makeText(mContext, "没有更多数据", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 901:
                                    if (isFirstIn) {
                                        isFirstIn = false;
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }
                                    if (mRecycleview.isRefreshing()) {
                                        mRecycleview.setRefreshing(false);
                                        linearlayout_nodata.setVisibility(View.VISIBLE);
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        mLoadingtimes--;
                                        mRecycleview.setLoadingMore(false);
                                    }
                                    MyToast.showToast(mContext, "服务器正在维护中", 2);
                                    break;
                            }
                        }
                    }
                });
    }

    private void deletelOrder(final int position) {
        OkGo.post(HttpConstants.deletelParkOrder)
                .tag(mContext)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("order_id", mOrdersData.get(position).getId())
                .params("citycode", mOrdersData.get(position).getCitycode())
                .execute(new JsonCallback<Base_Class_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<ParkOrderInfo> responseData, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        MyToast.showToast(mContext, "删除成功", 5);
                        mOrdersData.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(mContext, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    MyToast.showToast(mContext, "删除失败", 5);
                                case 103:
                                    MyToast.showToast(mContext, "删除失败", 5);
                                    break;
                                case 104:
                                    MyToast.showToast(mContext, "删除失败", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(mContext, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(mContext, what);
        mCustomDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((isVisibleToUser && isResumed())) {
            Log.e("我是已取消的页面", "我被显示啦setUserVisibleHint");
            requestGetAllOrdersAgain(0 + "", ((mLoadingtimes + 1) * 10) + "");
        } else if (!isVisibleToUser) {
            Log.e("我是已取消的页面", "我被隐藏啦setUserVisibleHint");
        } else if ((isVisibleToUser && !isResumed())) {
            Log.e("我是已取消的页面", "我被显示啦setUserVisibleHint");
            if (!isFirstIn){
                requestGetAllOrdersAgain(0 + "", ((mLoadingtimes + 1) * 10) + "");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            Log.e("我是已取消的页面", "我被显示啦onResume");
            if (!isFirstIn){
                requestGetAllOrdersAgain(0 + "", ((mLoadingtimes + 1) * 10) + "");
            }
        }
    }

    private void requestGetAllOrdersAgain(String startItem, String pageSize) {
        //获取订单信息，判断是否超时
        OkGo.post(HttpConstants.getKindParkOrder)
                .tag(mContext)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("startItem", startItem == null ? "0" : startItem)
                .params("pageSize", pageSize == null ? "10" : pageSize)
                .params("order_status", "6")
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkOrderInfo> datas, Call call, Response response) {
                        if (!isFirstIn) {
                            int position = linearLayoutManager.findFirstVisibleItemPosition();
                            mOrdersData.clear();
                            List<ParkOrderInfo> fsa = new ArrayList<>();
                            for (ParkOrderInfo info : datas.data) {
                                if (info.getOrder_status().equals("6")) {
                                    fsa.add(info);
                                } else if (info.getOrder_status().equals("1")) {
                                    if (!dateUtil.compareTwoTime(TimeManager.getInstance().getNowTime(true, false), dateUtil.addTime(info.getOrder_starttime(), UserManager.getInstance().getUserInfo().getRide_time()), true)) {
                                        info.setOrder_status("7");
                                        fsa.add(info);
                                    }
                                }
                            }
                            mOrdersData.addAll(fsa);
                            mAdapter.notifyDataSetChanged();

                            if (position < mOrdersData.size()) {
                                View view = mRecycleview.getRecyclerView().getChildAt(position);
                                if (view != null) {
                                    int top = view.getTop();
                                    linearLayoutManager.scrollToPositionWithOffset(position, top);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!DensityUtil.isException(mContext, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getCollectionDatas" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 102:
                                    //
                                    mOrdersData.clear();
                                    mAdapter.notifyDataSetChanged();
                                    break;
                            }
                        }
                    }
                });
    }
}
