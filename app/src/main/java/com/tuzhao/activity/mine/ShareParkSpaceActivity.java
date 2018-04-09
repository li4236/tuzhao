package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseAdapter;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.info.ShareParkSpaceInfo;
import com.tuzhao.publicwidget.others.SkipTopBottomDivider;
import com.tuzhao.utils.ImageUtil;

/**
 * Created by juncoder on 2018/4/9.
 */

public class ShareParkSpaceActivity extends BaseRefreshActivity {

    private ShareParkSpaceAdapter mSpaceAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mSpaceAdapter = new ShareParkSpaceAdapter();
        mRecyclerView.setAdapter(mSpaceAdapter);
        View view = LayoutInflater.from(this).inflate(R.layout.no_address_empty_layout, mRecyclerView, false);
        ImageView imageView = view.findViewById(R.id.no_address_empty_iv);
        ImageUtil.showPic(imageView, R.drawable.ic_noshare);
        TextView textView = view.findViewById(R.id.no_address_empty_tv);
        textView.setText("暂无好友给您分享车位哦...");
        mRecyclerView.setEmptyView(view);
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, true, true));
    }

    @Override
    protected void initData() {
        super.initData();
        if (mSpaceAdapter.getData().isEmpty()) {
            mRecyclerView.showEmpty(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoadingDialog();
                    loadData();
                    dismmisLoadingDialog();
                    mRecyclerView.showData();
                }
            });
        }
        dismmisLoadingDialog();
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void onRefresh() {
        refreshData();
        stopRefresh();
    }

    @Override
    protected void onLoadMore() {
        loadData();
        stopLoadMore();
    }

    @Override
    protected int resourceId() {
        return R.layout.activity_share_park_space_layout;
    }

    @NonNull
    @Override
    protected String title() {
        return "分享车位";
    }

    private void refreshData() {
        mSpaceAdapter.clearAll();
        ShareParkSpaceInfo shareParkSpaceInfo;
        for (int i = 0; i < 3; i++) {
            shareParkSpaceInfo = new ShareParkSpaceInfo();
            shareParkSpaceInfo.setCarOwnerName("天之力");
            shareParkSpaceInfo.setParkSpaceName("天之力停车场");
            shareParkSpaceInfo.setParkSpaceStatus(i % 2 == 0 ? "空闲中" : "使用中");
            mSpaceAdapter.addData(shareParkSpaceInfo);
        }
    }

    private void loadData() {
        ShareParkSpaceInfo shareParkSpaceInfo;
        for (int i = 0; i < 3; i++) {
            shareParkSpaceInfo = new ShareParkSpaceInfo();
            shareParkSpaceInfo.setCarOwnerName("新能源");
            shareParkSpaceInfo.setParkSpaceName("新能源停车场");
            shareParkSpaceInfo.setParkSpaceStatus(i % 2 == 0 ? "空闲中" : "使用中");
            mSpaceAdapter.addData(shareParkSpaceInfo);
        }
    }

    class ShareParkSpaceAdapter extends BaseAdapter<ShareParkSpaceInfo> {

        @Override
        protected void conver(@NonNull BaseViewHolder holder, ShareParkSpaceInfo shareParkSpaceInfo, int position) {
            holder.setText(R.id.share_park_space_space_name, shareParkSpaceInfo.getParkSpaceName())
                    .setText(R.id.share_park_space_share_name, shareParkSpaceInfo.getCarOwnerName())
                    .setText(R.id.share_park_space_status, shareParkSpaceInfo.getParkSpaceStatus());
            ((ImageView) holder.getView(R.id.share_park_space_status_iv)).setImageDrawable(
                    ContextCompat.getDrawable(ShareParkSpaceActivity.this,
                            shareParkSpaceInfo.getParkSpaceStatus().equals("使用中") ? R.drawable.circle_r5 : R.drawable.circle_green7));
        }

        @Override
        protected int itemViewId() {
            return R.layout.item_share_park_space_layout;
        }
    }

}
