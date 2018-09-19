package com.tuzhao.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.fragment.base.BaseRefreshFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.CircleView;
import com.tuzhao.publicwidget.customView.SkipTopBottomDivider;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/30.
 */
public class UseFriendParkSpaceRecordFragment extends BaseRefreshFragment<ParkOrderInfo> {

    private String mStatus;

    public static UseFriendParkSpaceRecordFragment getInstance(String status) {
        UseFriendParkSpaceRecordFragment fragment = new UseFriendParkSpaceRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstansUtil.STATUS, status);
        fragment.setArguments(bundle);
        if (status.equals("1")) {
            fragment.setTAG("预定中");
        } else {
            fragment.setTAG("历史记录");
        }
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        if (getArguments() != null) {
            mStatus = getArguments().getString(ConstansUtil.STATUS);
        }
        if ("0".equals(mStatus)) {
            mRecyclerView.setEmptyView(R.drawable.ic_noreview, "你没有正在预定中的记录哦");
        } else {
            mRecyclerView.setEmptyView(R.drawable.ic_noreview, "你没有历史记录哦");
        }
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(getContext(), true, true));
    }

    @Override
    protected void loadData() {
        getOkgos(HttpConstants.getFriendParkSpaceRecord, "status", mStatus)
                .execute(new JsonCallback<Base_Class_List_Info<ParkOrderInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<ParkOrderInfo> o, Call call, Response response) {
                        loadDataSuccess(o);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        loadDataFail(e, new LoadFailCallback() {
                            @Override
                            public void onLoadFail(Exception e) {
                                switch (e.getMessage()) {
                                    case "101":
                                    case "102":
                                    case "103":
                                        showFiveToast(ConstansUtil.SERVER_ERROR);
                                        break;
                                }
                            }
                        });
                    }
                });
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_my_order_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, ParkOrderInfo parkOrderInfo, int position) {
        CircleView circleView = holder.getView(R.id.my_order_status_iv);
        TextView orderTime = holder.getView(R.id.my_order_time);
        TextView orderTimeDescription = holder.getView(R.id.my_order_time_description);
        TextView orderStatus = holder.getView(R.id.my_order_waiting_for_pay);
        if (holder.itemView.getTag() == null) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone((ConstraintLayout) holder.getView(R.id.top_cl));
            constraintSet.constrainWidth(R.id.my_order_park_car_number_iv, dpToPx(12));
            constraintSet.constrainHeight(R.id.my_order_park_car_number_iv, dpToPx(13));
            constraintSet.applyTo((ConstraintLayout) holder.getView(R.id.top_cl));
            holder.itemView.setTag(constraintSet);
        } else {
            ConstraintSet constraintSet = (ConstraintSet) holder.itemView.getTag();
            constraintSet.applyTo((ConstraintLayout) holder.getView(R.id.top_cl));
        }
        holder.showPic(R.id.my_order_park_car_number_iv, R.drawable.ic_carman);
        switch (parkOrderInfo.getOrderStatus()) {
            case "0":
                //已预约
                circleView.setColor(Color.parseColor("#6a6bd9"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getOrderStartTime()));
                String appointParkDistance = "预停" + DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrderStartTime(), parkOrderInfo.getOrderEndTime());
                orderTimeDescription.setText(appointParkDistance);
                orderStatus.setTextColor(Color.parseColor("#6a6bd9"));
                orderStatus.setText("已预约");
                break;
            case "1":
                //停车中
                circleView.setColor(Color.parseColor("#ffa830"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getPark_start_time()));
                String parkTimeDistance;
                if (DateUtil.getYearToSecondCalendar(parkOrderInfo.getOrderEndTime()).compareTo(
                        DateUtil.getYearToSecondCalendar(DateUtil.getCurrentYearToSecond())) < 0) {
                    //停车时长超过预约时长
                    parkTimeDistance = "已超时" + DateUtil.getDistanceForDayHourMinuteAddStart(parkOrderInfo.getOrderEndTime(), DateUtil.getCurrentYearToSecond(), parkOrderInfo.getExtensionTime());
                } else {
                    parkTimeDistance = "剩余" + DateUtil.getDistanceForDayHourMinute(DateUtil.getCurrentYearToSecond(), parkOrderInfo.getOrderEndTime());
                }
                orderTimeDescription.setText(parkTimeDistance);
                orderStatus.setTextColor(Color.parseColor("#ffa830"));
                orderStatus.setText("租用中");
                break;
            case "2":
                //已完成
                circleView.setColor(Color.parseColor("#1dd0a1"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getPark_start_time()));
                String actualPay = "停车" + DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getPark_start_time(), parkOrderInfo.getPark_end_time());
                orderTimeDescription.setText(actualPay);

                orderStatus.setTextColor(Color.parseColor("#1dd0a1"));
                orderStatus.setText("已完成");
                break;
            case "3":
                //已取消（超时取消、正常手动取消）
                circleView.setColor(Color.parseColor("#808080"));
                orderTime.setText(DateUtil.getMonthToMinute(parkOrderInfo.getOrderStartTime()));
                String appointDistance = "预停" + DateUtil.getDistanceForDayHourMinute(parkOrderInfo.getOrderStartTime(), parkOrderInfo.getOrderEndTime());
                orderTimeDescription.setText(appointDistance);
                orderStatus.setTextColor(Color.parseColor("#808080"));
                orderStatus.setText("已取消");
                break;
        }

        holder.setText(R.id.my_order_appoint_date, parkOrderInfo.getOrderTime().substring(0, parkOrderInfo.getOrderTime().indexOf(" ")))
                .setText(R.id.my_order_park_lot, parkOrderInfo.getParkLotName())
                .setText(R.id.my_order_park_car_number, parkOrderInfo.getUserNoteName())
                .itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected int resourceId() {
        return 0;
    }

}
