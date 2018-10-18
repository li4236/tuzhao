package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.activity.base.BaseViewHolder;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.RentalRecordItemInfo;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.customView.SkipTopBottomDivider;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/3/27.
 * <p>
 * 出租记录
 * </p>
 */

public class RentalRecordActivity extends BaseRefreshActivity<RentalRecordItemInfo> {

    private Park_Info mPark_info;

    @Override
    protected int resourceId() {
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        if ((mPark_info = getIntent().getParcelableExtra("parkdata")) == null) {
            showFiveToast("获取出租记录失败，请返回重试");
            finish();
        }

        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, true, true));
    }

    @NonNull
    @Override
    protected String title() {
        return "出租记录";
    }

    @Override
    protected void loadData() {
        getOkgos(HttpConstants.getRentalRecord, "cityCode", mPark_info.getCityCode(), "parkSpaceId", mPark_info.getId())
                .execute(new JsonCallback<Base_Class_List_Info<RentalRecordItemInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_List_Info<RentalRecordItemInfo> o, Call call, Response response) {
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
                                        userError();
                                        break;
                                    case "102":
                                        showFiveToast("");
                                        break;
                                    case "103":
                                        showFiveToast("该车位已被删除，请重新选择");
                                        finish();
                                        break;
                                    case "104":
                                        userError();
                                        break;
                                    case "105":
                                        if (mCommonAdapter.getData().size() > 0) {
                                            showFiveToast("没有更多数据了哦");
                                        }
                                        break;
                                }
                            }
                        });
                    }
                });
    }

    @Override
    protected int itemViewResourceId() {
        return R.layout.item_rental_record_layout;
    }

    @Override
    protected void bindData(BaseViewHolder holder, RentalRecordItemInfo rentalRecordItemInfo, int position) {
        String[] rentalTimes = rentalRecordItemInfo.getRentalTime().split(":");
        StringBuilder time = new StringBuilder("出租时长:");

        if (!rentalTimes[0].equals("0")) {
            time.append(rentalTimes[0]);
            time.append("小时");
        }
        time.append(rentalTimes[1]);
        time.append("分");

        holder.setText(R.id.rental_record_time_item, time.toString())
                .setText(R.id.rental_record_car_number_item, rentalRecordItemInfo.getRentalCarNumber())
                .setText(R.id.rental_record_data_item, rentalRecordItemInfo.getRentalStartDate().substring(0, rentalRecordItemInfo.getRentalStartDate().length() - 3));
        if (rentalRecordItemInfo.getNoteName() == null) {
            //非亲友停车
            if ("2".equals(rentalRecordItemInfo.getOrderStatus())) {
                holder.setText(R.id.rental_record_earn_item, "正在停车中");
            } else {
                holder.setText(R.id.rental_record_earn_item, "获得收益：" + rentalRecordItemInfo.getRentalFee() + "元");
            }
        } else {
            if ("2".equals(rentalRecordItemInfo.getOrderStatus())) {
                holder.setText(R.id.rental_record_earn_item, rentalRecordItemInfo.getNoteName() + "正在停车中");
            } else {
                holder.setText(R.id.rental_record_earn_item, rentalRecordItemInfo.getNoteName() + "使用");
            }
        }
    }

}
