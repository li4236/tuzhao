package com.tuzhao.activity.base;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.tuzhao.R;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;

/**
 * Created by juncoder on 2018/3/27.
 * <p>
 * 适用于只有一种布局的adpter
 * </p>
 */

public abstract class BaseRefreshActivity extends BaseStatusActivity {

    protected SuperRefreshRecyclerView mRecyclerView;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mRecyclerView = findViewById(R.id.base_srrv);
        mRecyclerView.init(createLayouManager(), new OnRefreshListener() {
            @Override
            public void onRefresh() {
                BaseRefreshActivity.this.onRefresh();
            }
        }, new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                BaseRefreshActivity.this.onLoadMore();
            }
        });
        mRecyclerView.setRefreshEnabled(true);
        mRecyclerView.setLoadingMoreEnable(true);
        mRecyclerView.setEmptyView(R.layout.layout_empty);
    }

    /**
     * recycler将使用该LayoutManager
     *
     * @return recycleview使用的布局管理器
     */
    protected abstract RecyclerView.LayoutManager createLayouManager();

    /**
     * 当下拉刷新时将会回调该方法
     */
    protected abstract void onRefresh();

    /**
     * 当上拉加载更多时将回调该方法
     */
    protected abstract void onLoadMore();

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

}
