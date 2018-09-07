package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

   /* private ImageView mRentalRecordIv;

    private TextView mRentalRecordParkNumber;

    private TextView mRentalRecordParkStatus;

    private TextView mRentalRecordVoltage;*/

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

        /*mRentalRecordIv = findViewById(R.id.rental_record_iv);
        mRentalRecordParkNumber = findViewById(R.id.rental_record_car_number);
        mRentalRecordParkStatus = findViewById(R.id.rental_record_park_status);
        mRentalRecordVoltage = findViewById(R.id.rental_record_electricity);*/
        //mCommonAdapter.setHeaderView(R.layout.layout_placeholder);
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, true, true));
    }

    @Override
    protected void initData() {
        super.initData();
        /*ImageUtil.showImpPic(mRentalRecordIv, mPark_info.getPark_img());
        String parkNumber = "车位编号:" + mPark_info.getPark_number();
        mRentalRecordParkNumber.setText(parkNumber);
        String parkStatus;
        switch (mPark_info.getPark_status()) {
            case "1":
                parkStatus = "车位状态:正在审核";
                break;
            case "2":
                parkStatus = "车位状态:正在出租";
                break;
            case "3":
                parkStatus = "车位状态:暂未开放";
                break;
            default:
                parkStatus = "车位状态:未知状态";
                break;
        }
        mRentalRecordParkStatus.setText(parkStatus);

        String voltage = "电量状态" + mPark_info.getVoltage();
        mRentalRecordVoltage.setText(voltage);*/
    }

    @NonNull
    @Override
    protected String title() {
        return "出租记录";
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
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
                .setText(R.id.rental_record_data_item, rentalRecordItemInfo.getRentalStartDate().substring(0, rentalRecordItemInfo.getRentalStartDate().length() - 3))
                .setText(R.id.rental_record_earn_item, "获得收益:" + rentalRecordItemInfo.getRentalFee() + "元");
    }

}
