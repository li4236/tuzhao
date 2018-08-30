package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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
import com.tuzhao.publicwidget.customView.SkipTopBottomDivider;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.ImageUtil;

import java.text.DecimalFormat;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/4/9.
 */

public class ShareParkSpaceActivity extends BaseRefreshActivity<Park_Info> {

    private List<Park_Info> mParkInfos;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mParkInfos = getIntent().getParcelableArrayListExtra(ConstansUtil.REQUEST_FOR_RESULT);
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.no_address_empty_layout, mRecyclerView, false);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.setDimensionRatio(R.id.no_address_empty_iv, "361:300");
        constraintSet.applyTo(constraintLayout);
        ImageView imageView = constraintLayout.findViewById(R.id.no_address_empty_iv);
        ImageUtil.showPic(imageView, R.drawable.ic_noshare2);
        TextView textView = constraintLayout.findViewById(R.id.no_address_empty_tv);
        if (mParkInfos == null) {
            textView.setText("暂无好友给您分享车位哦...");
        } else {
            textView.setText("没有适合你的停车位哦...");
            mCommonAdapter.setNewData(mParkInfos);
        }
        mRecyclerView.setEmptyView(constraintLayout);
        mRecyclerView.setRefreshEnabled(false);
        mRecyclerView.setLoadingMoreEnable(false);
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, true, true));
    }

    @Override
    protected void initData() {
        if (mParkInfos == null) {
            super.initData();
        }
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
        switch (park_info.getPark_status()) {
            case "0":
                return "未开放";
            case "3":
                return "停租中";
            case "4":
                return "已删除";
        }
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

    private void showAppointmentDialog(final Park_Info parkInfo) {
        new TipeDialog.Builder(this)
                .setTitle("预约车位")
                .setMessage("确定预约" + parkInfo.getLocation_describe() + "车位吗")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reserveFriendParkSpace(parkInfo);
                    }
                })
                .create()
                .show();
    }

    private void reserveFriendParkSpace(final Park_Info parkInfo) {
        getOkGo(HttpConstants.reserveFriendParkSpace)
                .params("parkLotId", parkInfo.getParkLotId())
                .params("parkSpaceId", parkInfo.getId())
                .params("carNumber", parkInfo.getCarNumber())
                .params("parkInterval", parkInfo.getParkInterval())
                .params("cityCode", parkInfo.getCityCode())
                .execute(new JsonCallback() {
                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        notifyRemoveData(parkInfo);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast(e.getMessage());
                        }
                    }
                });
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_share_park_space_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final Park_Info park_info, int position) {
        holder.setText(R.id.share_park_space_space_name, park_info.getLocation_describe())
                .setText(R.id.share_park_space_share_name, "车主：" + park_info.getUserName());
        if (mParkInfos == null) {
            TextView status = holder.getView(R.id.share_park_space_status);
            ImageView statusIv = holder.getView(R.id.share_park_space_status_iv);
            status.setText(getParkspaceStatus(park_info));
            if (status.getText().toString().equals("空闲中")) {
                statusIv.setBackgroundResource(R.drawable.circle_green7);
            } else {
                statusIv.setBackgroundResource(R.drawable.circle_r5);
            }
        } else {
            if (park_info.getDistance() != 0) {
                holder.setText(R.id.share_park_space_status, new DecimalFormat("0.00").format(park_info.getDistance() / 1000));
                holder.appendText(R.id.share_park_space_status, "km");
            } else {
                goneView(holder.getView(R.id.share_park_space_status));
            }
            goneView(holder.getView(R.id.share_park_space_status_iv));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAppointmentDialog(park_info);
                }
            });
        }
    }

}
