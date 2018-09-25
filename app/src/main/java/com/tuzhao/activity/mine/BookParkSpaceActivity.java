package com.tuzhao.activity.mine;

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
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.SkipTopBottomDivider;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;

import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/9/4.
 * <p>
 * 预约亲友分享给我的车位
 * </p>
 */
public class BookParkSpaceActivity extends BaseRefreshActivity<Park_Info> {

    private DecimalFormat mDecimalFormat;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.no_address_empty_layout, mRecyclerView, false);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.setDimensionRatio(R.id.no_address_empty_iv, "361:300");
        constraintSet.applyTo(constraintLayout);
        ImageView imageView = constraintLayout.findViewById(R.id.no_address_empty_iv);
        ImageUtil.showPic(imageView, R.drawable.ic_noshare2);
        TextView textView = constraintLayout.findViewById(R.id.no_address_empty_tv);
        textView.setText("没有适合你的停车位哦...");
        mRecyclerView.setEmptyView(constraintLayout);
        mRecyclerView.setRefreshEnabled(false);
        mRecyclerView.setLoadingMoreEnable(false);
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, true, true));
    }

    @Override
    protected void initData() {
        mDecimalFormat = new DecimalFormat("0.00");
        mCommonAdapter.setNewData(getIntent().<Park_Info>getParcelableArrayListExtra(ConstansUtil.REQUEST_FOR_RESULT));
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void loadData() {

    }

    private void reserveFriendParkSpace(final Park_Info parkInfo) {
        showLoadingDialog("预定中...");
        getOkGo(HttpConstants.reserveFriendParkSpace)
                .params("parkLotId", parkInfo.getParkLotId())
                .params("parkSpaceId", parkInfo.getId())
                .params("carNumber", parkInfo.getCarNumber())
                .params("parkInterval", parkInfo.getParkInterval())
                .params("cityCode", parkInfo.getCityCode())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        notifyRemoveData(parkInfo);
                        IntentObserable.dispatch(ConstansUtil.BOOK_PARK_SPACE, ConstansUtil.INTENT_MESSAGE, parkInfo);
                        showFiveToast("预定成功");
                        dismmisLoadingDialog();
                        startActivity(UseFriendParkSpaceRecordActivity.class);
                        finish();
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
        return R.layout.activity_book_park_space_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final Park_Info park_info, int position) {
        holder.setText(R.id.share_park_space_space_name, park_info.getLocation_describe())
                .setText(R.id.share_park_space_share_name, "车主：" + park_info.getUserName())
                .setText(R.id.distance_to_distination_tv, park_info.isHaveDistination() ? "距目的地" : "距当前位置")
                .setOnClickListener(R.id.open_lock_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reserveFriendParkSpace(park_info);
                    }
                });
        if (park_info.getDistance() >= 1000) {
            holder.setText(R.id.distance_to_distination, mDecimalFormat.format(park_info.getDistance() / 1000) + "km");
        } else {
            holder.setText(R.id.distance_to_distination, (int) park_info.getDistance() + "m");
        }
    }

    @Override
    protected int resourceId() {
        return 0;
    }

    @NonNull
    @Override
    protected String title() {
        return "预定车位";
    }

}
