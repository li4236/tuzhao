package com.tuzhao.activity.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tianzhili.www.myselfsdk.okgo.request.BaseRequest;
import com.tuzhao.R;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;
import com.tuzhao.utils.DensityUtil;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.net.ssl.SSLException;

/**
 * Created by juncoder on 2018/3/27.
 * <p>
 * 适用于只有一种布局的adpter
 * </p>
 */

public abstract class BaseRefreshActivity<T> extends BaseStatusActivity {

    protected SuperRefreshRecyclerView mRecyclerView;

    protected CommonAdapter mCommonAdapter;

    protected int mStartItme;

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
        mRecyclerView.setErrorView(R.drawable.ic_nosignal2, "网络开小差了哦");
        mCommonAdapter = new CommonAdapter(mRecyclerView.getRecyclerView());
        mRecyclerView.setAdapter(mCommonAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        loadData();
    }

    /**
     * recycler将使用该LayoutManager
     *
     * @return recycleview使用的布局管理器
     */
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
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
    protected BaseRequest getOkgos(String url) {
        return getOkGo(url)
                .params("startItem", mStartItme)
                .params("pageSize", 15);
    }

    /**
     * 获取包括startItem和pageSize的Okgo
     *
     * @param params 如果还需要其他参数则按键值对输入
     */
    protected BaseRequest getOkgos(String url, String... params) {
        BaseRequest baseRequest = getOkGo(url);
        for (int i = 0; i < params.length; i += 2) {
            baseRequest.params(params[i], params[i + 1]);
        }
        return baseRequest
                .params("startItem", mStartItme)
                .params("pageSize", 15);
    }

    /**
     * 加载成功后调用该方法会自动添加新的数据并更新布局
     *
     * @param base_class_list_info 新的数据不能为null
     */
    protected void loadDataSuccess(Base_Class_List_Info<T> base_class_list_info) {
        if (mStartItme == 0 && !mCommonAdapter.getData().isEmpty()) {
            //如果是刷新的则把旧数据清除
            mCommonAdapter.clearAll();
        }
        mCommonAdapter.addData(base_class_list_info.data);
        if (mStartItme == 0 && mCommonAdapter.getFooterView() != null && !base_class_list_info.data.isEmpty()) {
            //如果有底部布局的话第一次加载会移动到底部布局的位置，需要把它移回去
            mRecyclerView.getRecyclerView().scrollToPosition(0);
        }
        mRecyclerView.showData();
        stopLoadStatus();
        increateStartItem();
        dismmisLoadingDialog();
    }

    /**
     * 请求数据返回异常时调用，如果原本没有数据会显示空布局，并且停止加载状态
     *
     * @param callback 当是后台返回的错误码时回调
     */
    protected void loadDataFail(Exception e, LoadFailCallback callback) {
        if (mCommonAdapter.getData().size() == 0) {
            showEmpty();
        }
        stopLoadStatus();
        if (!handleException(e)) {
            callback.onLoadFail(e);
        }
    }

    @Override
    protected boolean handleException(Exception e) {
        dismmisLoadingDialog();
        if (!DensityUtil.isException(this, e)) {
            if (e instanceof ConnectException) {
                showError();
                return true;
            } else if (e instanceof SocketTimeoutException) {
                showError();
                return true;
            } else if (e instanceof NoRouteToHostException) {
                showError();
                return true;
            } else if (e instanceof UnknownHostException) {
                showError();
                return true;
            } else if (e instanceof SSLException) {
                showError();
                return true;
            } else if (e instanceof IllegalStateException) {
                switch (e.getMessage()) {
                    case "801":
                        showFiveToast("数据存储异常，请稍后重试");
                        return true;
                    case "802":
                        showFiveToast("客户端异常，请稍后重试");
                        return true;
                    case "803":
                        showFiveToast("参数异常，请检查是否全都填写了哦");
                        return true;
                    case "804":
                        showFiveToast("获取数据异常，请稍后重试");
                        return true;
                    case "805":
                        userError();
                        return true;
                    default:
                        return false;
                }
            } else {
                showFiveToast(e.getMessage());
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * 网络连接异常显示异常布局
     */
    private void showError() {
        if (mCommonAdapter.getDataSize() == 0) {
            mRecyclerView.showError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStartItme = 0;
                    showLoadingDialog();
                    loadData();
                }
            });
        }
    }

    /**
     * 请求的开始条数+15
     */
    protected void increateStartItem() {
        mStartItme += 15;
    }

    /**
     * 停止加载状态（包括下拉刷新和上拉加载）
     */
    protected void stopLoadStatus() {
        if (mStartItme == 0) {
            stopRefresh();
        } else {
            stopLoadMore();
        }
    }

    /**
     * 如果没有数据的话显示空布局
     */
    protected void showEmpty() {
        if (mCommonAdapter.getData().isEmpty()) {
            mRecyclerView.showEmpty();
        }
    }

    /**
     * 删除position位置的数据，如果删除后没有数据则显示空布局
     */
    protected void notifyRemoveData(int position) {
        mCommonAdapter.notifyRemoveData(position);
        showEmpty();
    }

    /**
     * 删除指定的一个数据，需要重写T的equal方法，否则可能找不到该数据
     */
    protected void notifyRemoveData(T t) {
        mCommonAdapter.notifyRemoveData(t);
        showEmpty();
    }

    /**
     * 加载数据的方法，在界面显示的时候会自动调用，下拉刷新和上拉加载默认调用
     */
    protected abstract void loadData();

    /**
     * @return adapter中的itemViewId
     */
    protected abstract int itemViewResourceId();

    /**
     * adapter绑定数据用
     */
    protected abstract void bindData(BaseViewHolder holder, T t, int position);

    /**
     * adapter局部刷新使用，使用的时候需要判断payloads，否则如果是第一次创建视图的时候会两个bindData方法都会调用
     */
    protected void bindData(BaseViewHolder holder, T t, int position, List<Object> payloads) {
    }

    protected class CommonAdapter extends BaseAdapter<T> {

        CommonAdapter(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        protected void conver(@NonNull BaseViewHolder holder, T t, int position) {
            bindData(holder, t, position);
        }

        @Override
        protected void conver(@NonNull BaseViewHolder holder, T t, int position, @NonNull List<Object> payloads) {
            super.conver(holder, t, position, payloads);
            bindData(holder, t, position, payloads);
        }

        @Override
        protected int itemViewId() {
            return itemViewResourceId();
        }

    }

}
