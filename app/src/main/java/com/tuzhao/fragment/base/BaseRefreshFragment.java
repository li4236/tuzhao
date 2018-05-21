package com.tuzhao.fragment.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tianzhili.www.myselfsdk.okgo.request.BaseRequest;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;

/**
 * Created by juncoder on 2018/5/21.
 */

public abstract class BaseRefreshFragment<T> extends BaseStatusFragment {

    protected SuperRefreshRecyclerView mRecyclerView;

    protected CommonAdapter mCommonAdapter;

    protected int mStartItme;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.base_srrv);
        mRecyclerView.init(createLayouManager(), new OnRefreshListener() {
            @Override
            public void onRefresh() {
                BaseRefreshFragment.this.onRefresh();
            }
        }, new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                BaseRefreshFragment.this.onLoadMore();
            }
        });
        mRecyclerView.setRefreshEnabled(true);
        mRecyclerView.setLoadingMoreEnable(true);
        mRecyclerView.setEmptyView(R.layout.layout_empty);
        mCommonAdapter = new CommonAdapter(mRecyclerView.getRecyclerView());
        mRecyclerView.setAdapter(mCommonAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        loadData();
    }

    /**
     * 当下拉刷新时将会回调该方法
     */
    protected void onRefresh() {
        mStartItme = 0;
        loadData();
    }

    /**
     * 当上拉加载更多时将回调该方法
     */
    protected void onLoadMore() {
        loadData();
    }

    /**
     * 停止下拉刷新
     */
    protected void stopRefresh() {
        if (mRecyclerView.isRefreshing()) {
            mRecyclerView.setRefreshing(false);
        }
    }

    /**
     * 停止上拉加载更多
     */
    protected void stopLoadMore() {
        if (mRecyclerView.isLoadingMore()) {
            mRecyclerView.setLoadingMore(false);
        }
    }

    /**
     * 获取包括startItem和pageSize的Okgo
     */
    protected BaseRequest getOkgo(String url) {
        return getOkGo(url)
                .params("startItem", mStartItme)
                .params("pageSize", 15);
    }

    /**
     * 获取包括startItem和pageSize的Okgo
     *
     * @param params 如果还需要其他参数则按键值对输入
     */
    protected BaseRequest getOkgo(String url, String... params) {
        BaseRequest baseRequest = getOkgo(url);
        for (int i = 0; i < params.length; i += 2) {
            baseRequest.params(params[i], params[i + 1]);
        }
        return baseRequest
                .params("startItem", mStartItme)
                .params("pageSize", 15);
    }

    /**
     * recycler将使用该LayoutManager
     *
     * @return recycleview使用的布局管理器
     */
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(getContext());
    }

    protected void loadDataSuccess(Base_Class_List_Info<T> base_class_list_info) {
        if (mStartItme == 0 && !mCommonAdapter.getData().isEmpty()) {
            mCommonAdapter.clearAll();
        }
        mCommonAdapter.addData(base_class_list_info.data);
        mRecyclerView.showData();
        stopLoadStatus();
        increateStartItem();
        dismmisLoadingDialog();
    }

    protected void loadDataFail(Exception e, LoadFailCallback callback) {
        if (mCommonAdapter.getData().size() == 0) {
            showEmpty();
        }
        stopLoadStatus();
        if (!handleException(e)) {
            callback.onLoadFail(e);
        }
    }

    protected void increateStartItem() {
        mStartItme += 15;
    }

    protected void stopLoadStatus() {
        if (mStartItme == 0) {
            stopRefresh();
        } else {
            stopLoadMore();
        }
    }

    protected void showEmpty() {
        if (mCommonAdapter.getData().isEmpty()) {
            mRecyclerView.showEmpty(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStartItme = 0;
                    showLoadingDialog();
                    loadData();
                }
            });
        }
    }

    protected abstract void loadData();

    protected abstract int itemViewResourceId();

    protected abstract void bindData(BaseViewHolder holder, T t, int position);

    protected class CommonAdapter extends BaseAdapter<T> {

        CommonAdapter(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        protected void conver(@NonNull BaseViewHolder holder, T t, int position) {
            bindData(holder, t, position);
        }

        @Override
        protected int itemViewId() {
            return itemViewResourceId();
        }
    }

}
