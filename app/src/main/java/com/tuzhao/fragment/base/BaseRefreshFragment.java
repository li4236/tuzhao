package com.tuzhao.fragment.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tianzhili.www.myselfsdk.okgo.request.BaseRequest;
import com.tuzhao.R;
import com.tuzhao.activity.LoginActivity;
import com.tuzhao.adapter.BaseAdapter;
import com.tuzhao.adapter.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.net.ssl.SSLException;

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
        if (mStartItme == 0 && mCommonAdapter.getFooterView() != null && !base_class_list_info.data.isEmpty()) {
            //如果有底部布局的话第一次加载会移动到底部布局的位置，需要把它移回去
            mRecyclerView.getRecyclerView().scrollToPosition(0);
        }
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

    @Override
    protected boolean handleException(Exception e) {
        dismmisLoadingDialog();
        if (!DensityUtil.isException(getContext(), e)) {
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

    protected void userError() {
        showFiveToast("账号异常，请重新登录");
        dismmisLoadingDialog();
        startActivity(LoginActivity.class, ConstansUtil.INTENT_MESSAGE, true);
    }

    protected void increateStartItem() {
        mStartItme += 15;
    }

    protected void stopLoadStatus() {
        stopRefresh();
        stopLoadMore();
    }

    protected void showEmpty() {
        if (mCommonAdapter.getData().isEmpty()) {
            mRecyclerView.showEmpty();
        }
    }

    protected void notifyRemoveData(T t) {
        mCommonAdapter.notifyRemoveData(t);
        showEmpty();
    }

    protected void notifyRemoveData(int position) {
        mCommonAdapter.notifyRemoveData(position);
        showEmpty();
    }

    protected void notifyAddData(T t) {
        mCommonAdapter.notifyAddData(t);
        mRecyclerView.showData();
    }

    protected void notifyAddData(int position, T t) {
        mCommonAdapter.notifyAddData(position, t);
        mRecyclerView.showData();
    }

    protected abstract void loadData();

    protected int converItemViewId(int viewType) {
        if (viewType != -1) {
            return viewType;
        }
        return 0;
    }

    protected int converGetItmeViewType(T t, int position) {
        return 0;
    }

    protected abstract int itemViewResourceId();

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
        protected int itemViewId(int viewType) {
            return converItemViewId(viewType);
        }

        @Override
        protected int converGetItemViewType(T t, int position) {
            return converGetItmeViewType(t, position);
        }

        @Override
        protected int itemViewId() {
            return itemViewResourceId();
        }

    }

}
