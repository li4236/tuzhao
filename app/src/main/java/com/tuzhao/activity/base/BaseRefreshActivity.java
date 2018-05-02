package com.tuzhao.activity.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.request.BaseRequest;
import com.tuzhao.R;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.swipetoloadlayout.OnLoadMoreListener;
import com.tuzhao.publicwidget.swipetoloadlayout.OnRefreshListener;
import com.tuzhao.publicwidget.swipetoloadlayout.SuperRefreshRecyclerView;

import okhttp3.Call;
import okhttp3.Response;

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
    protected abstract RecyclerView.LayoutManager createLayouManager();

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
     * @param url      请求的url
     * @param callback 当请求成功的时候会自动显示数据，然后回调onSuccess方法，接着会把请求状态取消
     *                 当请求失败的时候如果没有数据则显示空数据并取消加载对话框，接着会回调onError方法
     */
    protected void requestData(String url, final BaseCallback<Base_Class_List_Info<T>> callback) {
        OkGo.<Base_Class_List_Info<T>>post(url)
                .tag(this.getClass().getName())
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("startItem", mStartItme)
                .params("pageSize", 15)
                .execute(new JsonCallback<Base_Class_List_Info<T>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<T> base_class_list_info, Call call, Response response) {
                        mRecyclerView.showData();
                        if (mStartItme == 0 && !mCommonAdapter.getData().isEmpty()) {
                            mCommonAdapter.clearAll();
                        }
                        mCommonAdapter.addData(base_class_list_info.data);
                        callback.onSuccess(base_class_list_info, call, response);
                        stopLoadStatus();
                        increateStartItem();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        showEmpty();
                        dismmisLoadingDialog();
                        if (!handleException(e)) {
                            callback.onError(call, response, e);
                        }
                    }
                });
    /*    getOkgo(url)
                .execute(new JsonListCallback<Base_Class_List_Info<T>, T>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<T> t, Call call, Response response) {
                        Log.e(TAG, "onSuccess: " + t);
                        mRecyclerView.showData();
                        if (mStartItme == 0 && !mCommonAdapter.getData().isEmpty()) {
                            mCommonAdapter.clearAll();
                        }
                        mCommonAdapter.addData(t.data);
                        callback.onSuccess(t, call, response);
                        stopLoadStatus();
                        increateStartItem();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        showEmpty();
                        dismmisLoadingDialog();
                        if (!handleException(e)) {
                            callback.onError(call, response, e);
                        }
                    }
                });*/
    }

    /**
     * @param url      请求的url
     * @param callback 当请求成功的时候会自动显示数据，然后回调onSuccess方法，接着会把请求状态取消
     *                 当请求失败的时候如果没有数据则显示空数据并取消加载对话框，接着会回调onError方法
     * @param params   当请求除了token和startItme，pageSize时可以在此添加，注意添加的键值对都需要匹配
     */
    protected void requestData(String url, final BaseCallback<Base_Class_List_Info<T>> callback, String... params) {
        getOkgo(url, params)
                .execute(new JsonCallback<Base_Class_List_Info<T>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<T> t, Call call, Response response) {
                        Log.e(TAG, "onSuccess: " + t);
                        mRecyclerView.showData();
                        if (mStartItme == 0 && !mCommonAdapter.getData().isEmpty()) {
                            mCommonAdapter.clearAll();
                        }
                        mCommonAdapter.addData(t.data);
                        callback.onSuccess(t, call, response);
                        stopLoadStatus();
                        increateStartItem();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        showEmpty();
                        if (!handleException(e)) {
                            callback.onError(call, response, e);
                        }
                    }
                });
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
