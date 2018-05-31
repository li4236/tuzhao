package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.adapter.MyParkAdpater;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
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

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL13 on 2017/6/28.
 */

public class MyParkActivity extends BaseActivity implements View.OnClickListener {

    private SuperRefreshRecyclerView mRecyclerView;
    private ArrayList<Park_Info> mData = new ArrayList<>();
    private MyParkAdpater mMyParkAdpater;
    private LoadingDialog mLoadingDialog;
    private LinearLayout linearlayout_nodata;

    private boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypark_layout);
        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initData() {
        mData = new ArrayList<>();
        initLoading("加载中...");
        requestParkData();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.id_activity_mypark_layout_recyclerview);
        mRecyclerView.init(new LinearLayoutManager(this), new onMyRefresh(), new onMyLoadMore());
        mRecyclerView.setRefreshEnabled(true);
        mRecyclerView.setLoadingMoreEnable(false);
        mRecyclerView.setEmptyView(R.layout.layout_empty);
        linearlayout_nodata = findViewById(R.id.id_activity_mypark_layout_linearlayout_nodata);
    }

    private void initEvent() {
        findViewById(R.id.id_activity_mypark_textview_add).setOnClickListener(this);
        findViewById(R.id.id_activity_mypark_layout_imageview_back).setOnClickListener(this);
        findViewById(R.id.id_activity_mypark_layout_textview_more).setOnClickListener(this);
    }

    private void initLoading(String what) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(MyParkActivity.this, what);
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.id_activity_mypark_textview_add:
                intent = new Intent(MyParkActivity.this, AddParkActivity.class);
                startActivity(intent);
                break;
            case R.id.id_activity_mypark_layout_imageview_back:
                finish();
                break;
            case R.id.id_activity_mypark_layout_textview_more:
                intent = new Intent(MyParkActivity.this, MyPassingParkActivity.class);
                startActivity(intent);
                break;
        }
    }

    private class onMyRefresh implements OnRefreshListener {
        @Override
        public void onRefresh() {
            //开始下拉刷新
            requestParkData();
        }
    }

    private class onMyLoadMore implements OnLoadMoreListener {
        @Override
        public void onLoadMore() {
            //开始上拉加载更多数据
        }
    }

    private void requestParkData() {
        OkGo.post(HttpConstants.getParkFromUser)//请求数据的接口地址
                .tag(MyParkActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Park_Info> responseData, Call call, Response response) {
                        //请求成功
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.hide();
                        }
                        linearlayout_nodata.setVisibility(View.GONE);
                        if (mData.isEmpty()) {
                            mData = responseData.data;
                            mMyParkAdpater = new MyParkAdpater(MyParkActivity.this, mData);
                            mRecyclerView.setAdapter(mMyParkAdpater);
                        } else {
                            mData.clear();
                            mData.addAll(responseData.data);
                            mMyParkAdpater.notifyDataSetChanged();
                        }
                        if (mRecyclerView.isRefreshing()) {
                            mRecyclerView.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.hide();
                        }
                        if (!DensityUtil.isException(MyParkActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    //暂无自己的车位
                                    if (mRecyclerView.isRefreshing()) {
                                        mRecyclerView.setRefreshing(false);
                                    }

                                    if (mMyParkAdpater != null) {
                                        mData.clear();
                                        mMyParkAdpater.notifyDataSetChanged();
                                    }
                                    linearlayout_nodata.setVisibility(View.VISIBLE);
                                    break;
                                case 901:
                                    if (mRecyclerView.isRefreshing()) {
                                        mRecyclerView.setRefreshing(false);
                                    }
                                    MyToast.showToast(MyParkActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
        } else {
            initLoading("加载中...");
            requestParkData();
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
