package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.adapter.ConsumRecordAdapter;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ConsumRecordInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/12/25.
 */

public class BillActivity extends BaseActivity {

    private LinearLayout linearlayout_norecord;
    private SuperRefreshRecyclerView mRecycleview;
    private ConsumRecordAdapter mAdapter;
    private LoadingDialog mLoadingDialog;

    private List<ConsumRecordInfo> mDataList = new ArrayList<>();
    private boolean isFirstIn = true, isNoData = false, isNoInternet = false;
    private int mLoadingtimes = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_layout);

        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {
        mRecycleview = findViewById(R.id.id_activity_bill_layout_recycleview);
        linearlayout_norecord = findViewById(R.id.id_activity_bill_layout_linearlayout_nodata);
        mRecycleview.init(new LinearLayoutManager(BillActivity.this), new onMyRefresh(), new onMyLoadMore());
        mRecycleview.setRefreshEnabled(true);
        mRecycleview.setLoadingMoreEnable(true);
        mAdapter = new ConsumRecordAdapter(BillActivity.this, mDataList);
        mRecycleview.setAdapter(mAdapter);
    }

    private void initData() {
        initLoading();
        requstGetRecordList(null, null);
    }

    private void requstGetRecordList(String startItem, String pageSize) {

        OkGo.post(HttpConstants.getUserBill)
                .tag(BillActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("startItem", startItem == null ? "0" : startItem)
                .params("pageSize", pageSize == null ? "15" : pageSize)
                .execute(new JsonCallback<Base_Class_List_Info<ConsumRecordInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ConsumRecordInfo> consumRecordInfoBase_class_list_info, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.hide();
                        }
                        linearlayout_norecord.setVisibility(View.GONE);
                        if (isFirstIn) {
                            isFirstIn = false;
                            mDataList = consumRecordInfoBase_class_list_info.data;
                            mAdapter = new ConsumRecordAdapter(BillActivity.this, mDataList);
                            mRecycleview.setAdapter(mAdapter);
                        }

                        if (mRecycleview.isRefreshing()) {
                            isNoInternet = false;
                            mRecycleview.setRefreshing(false);
                            mDataList.clear();
                            mDataList.addAll(consumRecordInfoBase_class_list_info.data);
                            mAdapter.notifyDataSetChanged();
                        }
                        if (mRecycleview.isLoadingMore()) {
                            if (consumRecordInfoBase_class_list_info.data.size() > 0) {
                                isNoInternet = false;
                                mRecycleview.setLoadingMore(false);
                                mDataList.addAll(consumRecordInfoBase_class_list_info.data);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                isNoData = true;
                                isNoInternet = false;
                                mRecycleview.setLoadingMore(false);
                                Toast.makeText(BillActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (!DensityUtil.isException(BillActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + "getUserBill" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    if (isFirstIn) {
                                        isFirstIn = false;
                                        linearlayout_norecord.setVisibility(View.VISIBLE);
                                    }

                                    if (mRecycleview.isRefreshing()) {
                                        isNoInternet = false;
                                        mRecycleview.setRefreshing(false);
                                        mDataList.clear();
                                        mAdapter.notifyDataSetChanged();
                                        linearlayout_norecord.setVisibility(View.VISIBLE);
                                        MyToast.showToast(BillActivity.this, "没有数据哦", 5);
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        isNoData = true;
                                        isNoInternet = false;
                                        mRecycleview.setLoadingMore(false);
                                        MyToast.showToast(BillActivity.this, "没有更多数据", 5);
                                    }
                                    break;
                                case 901:
                                    if (mRecycleview.isRefreshing()) {
                                        mRecycleview.setRefreshing(false);
                                    }
                                    if (mRecycleview.isLoadingMore()) {
                                        mRecycleview.setLoadingMore(false);
                                    }
                                    MyToast.showToast(BillActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void initEvent() {

        findViewById(R.id.id_activity_bill_imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class onMyRefresh implements OnRefreshListener {
        @Override
        public void onRefresh() {
            //开始下拉刷新
            isNoData = false;
            mLoadingtimes = 0;
            requstGetRecordList(null, null);
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
                requstGetRecordList((mLoadingtimes * 10) + "", null);
            } else {
                mRecycleview.setLoadingMore(false);
                Toast.makeText(BillActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initLoading() {
        mLoadingDialog = new LoadingDialog(BillActivity.this, null);
        mLoadingDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
