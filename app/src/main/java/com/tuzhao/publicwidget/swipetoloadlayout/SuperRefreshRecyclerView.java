package com.tuzhao.publicwidget.swipetoloadlayout;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuzhao.R;


/**
 * Created by ex-liyongqiang001 on 16/6/30.
 */
public class SuperRefreshRecyclerView extends FrameLayout {

    private RelativeLayout emptyView, errorView;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView recyclerView;
    private boolean move;
    private int mIndex;
    private RecyclerView.LayoutManager layoutManager;

    private Context mContext;


    ChangeScrollStateCallback mChangeScrollStateCallback;
    ChangeScrollCallback mChangeScrollCallback;

    public SuperRefreshRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public SuperRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SuperRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.layout_super_refresh_recycler, this);
        emptyView = findViewById(R.id.layout_empty);
        errorView = findViewById(R.id.layout_error);
        swipeToLoadLayout = findViewById(R.id.swipe_to_load);
        recyclerView = findViewById(R.id.swipe_target);
    }

    public void init(RecyclerView.LayoutManager layoutManager, OnRefreshListener onRefreshListener, OnLoadMoreListener onLoadMoreListener) {
        recyclerView.setLayoutManager(layoutManager);
        this.layoutManager = layoutManager;
        swipeToLoadLayout.setOnRefreshListener(onRefreshListener);
        swipeToLoadLayout.setOnLoadMoreListener(onLoadMoreListener);
        recyclerView.addOnScrollListener(new RecyclerViewListener());
    }

    public void showEmpty() {
        if (emptyView.getChildCount() != 0) {
            swipeToLoadLayout.setVisibility(GONE);

            emptyView.setVisibility(VISIBLE);
            errorView.setVisibility(GONE);
        }
    }

    public void showEmpty(OnClickListener onEmptyClick) {
        if (emptyView.getChildCount() != 0) {
            swipeToLoadLayout.setVisibility(GONE);

            emptyView.setVisibility(VISIBLE);
            errorView.setVisibility(GONE);
            if (onEmptyClick != null) {
                emptyView.setOnClickListener(onEmptyClick);
            }
        }
    }

    public void setErrorView(@DrawableRes int drawableRes, String text) {
        errorView.removeAllViews();

        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_empty, recyclerView, false);
        ImageView imageView = constraintLayout.findViewById(R.id.empty_iv);
        imageView.setImageResource(drawableRes);
        TextView textView = constraintLayout.findViewById(R.id.empty_tv);
        textView.setText(text);

        errorView.addView(constraintLayout);
    }

    public void showError(OnClickListener onErrorClick) {
        swipeToLoadLayout.setVisibility(GONE);
        emptyView.setVisibility(GONE);
        errorView.setVisibility(VISIBLE);
        if (onErrorClick != null) {
            errorView.setOnClickListener(onErrorClick);
        }
    }

    public void showData() {
        if (emptyView.getVisibility() == VISIBLE||errorView.getVisibility()==VISIBLE) {
            swipeToLoadLayout.setVisibility(VISIBLE);
            emptyView.setVisibility(GONE);
            errorView.setVisibility(GONE);
        }
    }

    public void setEmptyView(@LayoutRes int resourceId) {
        emptyView.removeAllViews();
        emptyView.addView(LayoutInflater.from(mContext).inflate(resourceId, recyclerView, false));
    }

    public void setEmptyView(View view) {
        emptyView.removeAllViews();
        if (view != null) {
            emptyView.addView(view);
        }
    }

    public void setEmptyView(@DrawableRes int drawableRes, String text) {
        emptyView.removeAllViews();

        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_empty, recyclerView, false);
        ImageView imageView = constraintLayout.findViewById(R.id.empty_iv);
        imageView.setImageResource(drawableRes);
        TextView textView = constraintLayout.findViewById(R.id.empty_tv);
        textView.setText(text);

        emptyView.addView(constraintLayout);
    }

    public boolean isRefreshing() {
        return swipeToLoadLayout.isRefreshing();
    }

    public void setRefreshing(boolean refreshing) {
        swipeToLoadLayout.setRefreshing(refreshing);
    }

    public boolean isLoadingMore() {
        return swipeToLoadLayout.isLoadingMore();
    }

    public void setLoadingMore(boolean loadMore) {
        swipeToLoadLayout.setLoadingMore(loadMore);
    }

    public void setLoadingMoreEnable(boolean enable) {
        swipeToLoadLayout.setLoadMoreEnabled(enable);
    }

    public void setRefreshEnabled(boolean enable) {
        swipeToLoadLayout.setRefreshEnabled(enable);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
    }

    public void moveToPosition(int n) {
        mIndex = n;
        LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) layoutManager;
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (n <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            recyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int top = recyclerView.getChildAt(n - firstItem).getTop();
            recyclerView.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            recyclerView.scrollToPosition(n);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
        }

    }

    class RecyclerViewListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (mChangeScrollStateCallback != null) {
                mChangeScrollStateCallback.change(newState);
            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (dy > 0 || dy < 0) {
                if (mChangeScrollCallback != null) {
                    mChangeScrollCallback.scroll(recyclerView, dx, dy);
                }

            }

            //在这里进行第二次滚动（最后的100米！）
            if (move) {

                move = false;
                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) layoutManager;
                int n = mIndex - mLinearLayoutManager.findFirstVisibleItemPosition();
                if (0 <= n && n < recyclerView.getChildCount()) {
                    //获取要置顶的项顶部离RecyclerView顶部的距离
                    int top = recyclerView.getChildAt(n).getTop();
                    //最后的移动
                    recyclerView.scrollBy(0, top);
                }
            }
        }
    }

    public void setChangeScrollStateCallback(ChangeScrollStateCallback mChangeScrollStateCallback) {
        this.mChangeScrollStateCallback = mChangeScrollStateCallback;
    }

    public void setChangeScrollCallback(ChangeScrollCallback changeScrollCallback) {
        mChangeScrollCallback = changeScrollCallback;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
