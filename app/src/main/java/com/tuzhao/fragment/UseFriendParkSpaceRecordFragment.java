package com.tuzhao.fragment;

import android.os.Bundle;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.fragment.base.BaseRefreshFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkOrderInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
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
        return 0;
    }

    @Override
    protected int converGetItmeViewType(ParkOrderInfo parkOrderInfo, int position) {
        switch (parkOrderInfo.getOrderStatus()) {
            case "0":
                return R.layout.item_reserving_friend_park_space_layout;
            case "1":
                return R.layout.item_parking_friend_park_space_layout;
            case "3":
                return R.layout.item_park_friend_park_space_record_layout;
        }
        return super.converGetItmeViewType(parkOrderInfo, position);
    }

    @Override
    protected void bindData(BaseViewHolder holder, ParkOrderInfo parkOrderInfo, int position) {
        holder.setText(R.id.share_park_space_space_name, parkOrderInfo.getParkSpaceLocationDescribe())
                .setText(R.id.share_park_space_share_name, "车主：" + parkOrderInfo.getUserName());
        if (parkOrderInfo.getOrderStatus().equals("3")) {
            holder.setText(R.id.start_park_date_tv, parkOrderInfo.getPark_start_time().substring(0, parkOrderInfo.getPark_start_time().indexOf(" ")));
            holder.setText(R.id.park_duration, DateUtil.getDateDistanceForHourWithMinute(parkOrderInfo.getPark_start_time(), parkOrderInfo.getPark_end_time()));
        }
    }

    @Override
    protected int resourceId() {
        return 0;
    }

}
