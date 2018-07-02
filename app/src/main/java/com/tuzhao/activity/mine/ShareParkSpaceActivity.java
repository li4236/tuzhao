package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.others.SkipTopBottomDivider;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/4/9.
 */

public class ShareParkSpaceActivity extends BaseRefreshActivity<Park_Info> {

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.no_address_empty_layout, mRecyclerView, false);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.setDimensionRatio(R.id.no_address_empty_iv, "438:334");
        constraintSet.applyTo(constraintLayout);
        ImageView imageView = constraintLayout.findViewById(R.id.no_address_empty_iv);
        ImageUtil.showPic(imageView, R.drawable.ic_noshare);
        TextView textView = constraintLayout.findViewById(R.id.no_address_empty_tv);
        textView.setText("暂无好友给您分享车位哦...");
        mRecyclerView.setEmptyView(constraintLayout);
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, true, true));
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
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

    @Override
    protected void loadData() {
        getOkgos(HttpConstants.getFriendShareParkspace)
                .execute(new JsonCallback<Base_Class_List_Info<Park_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<Park_Info> o, Call call, Response response) {
                        loadDataSuccess(o);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {

                            }
                        });
                    }
                });
    }

    private String getParkspaceStatus(Park_Info park_info) {
        String nowDate = DateUtil.getCurrentYearToMinutes();
        String afterTwoMinutesDate = DateUtil.getCurrentYearToMinutes(System.currentTimeMillis() + 1000 * 60 * 2);

        if (DateUtil.isInShareDate(nowDate, afterTwoMinutesDate, park_info.getOpen_date()) == 0) {
            return "停租中";
        }

        if (0 == DateUtil.isInPauseDate(nowDate, afterTwoMinutesDate, park_info.getPauseShareDate())) {
            return "停租中";
        }

        if (0 == DateUtil.isInShareDay(nowDate, afterTwoMinutesDate, park_info.getShareDay())) {
            return "停租中";
        }

        if (null == DateUtil.isInShareTime(nowDate, afterTwoMinutesDate, park_info.getOpen_time(), false)) {
            return "停租中";
        }

        if (DateUtil.isInOrderDate(nowDate, afterTwoMinutesDate, park_info.getOrder_times())) {
            return "出租中";
        }
        return "空闲中";
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_share_park_space_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, Park_Info park_info, int position) {
        holder.setText(R.id.share_park_space_space_name, park_info.getLocation_describe())
                .setText(R.id.share_park_space_share_name, "车主：" + park_info.getUserName());
        TextView status = holder.getView(R.id.share_park_space_status);
        ImageView statusIv = holder.getView(R.id.share_park_space_status_iv);
        status.setText(getParkspaceStatus(park_info));
        if (status.getText().toString().equals("空闲中")) {
            statusIv.setBackgroundResource(R.drawable.circle_green7);
        } else {
            statusIv.setBackgroundResource(R.drawable.circle_r5);
        }
    }

}
