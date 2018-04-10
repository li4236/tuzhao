package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseRefreshActivity;
import com.tuzhao.adapter.RentalRecordAdapter;
import com.tuzhao.info.RentalRecordInfo;
import com.tuzhao.info.RentalRecordItemInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicwidget.others.SkipTopBottomDivider;
import com.tuzhao.utils.ImageUtil;

import java.util.ArrayList;

/**
 * Created by juncoder on 2018/3/27.
 * <p>
 * 出租记录
 * </p>
 */

public class RentalRecordActivity extends BaseRefreshActivity {

    private ImageView mRentalRecordIv;

    private TextView mRentalRecordParkNumber;

    private TextView mRentalRecordParkStatus;

    private TextView mRentalRecordElectricity;

    private RentalRecordAdapter mAdapter;

    private int mStartItme;

    @Override
    protected int resourceId() {
        return R.layout.activity_rental_record_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mRentalRecordIv = findViewById(R.id.rental_record_iv);
        mRentalRecordParkNumber = findViewById(R.id.rental_record_car_number);
        mRentalRecordParkStatus = findViewById(R.id.rental_record_park_status);
        mRentalRecordElectricity = findViewById(R.id.rental_record_electricity);
        findViewById(R.id.rental_record_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ParkSpaceSettingActivity.class);
            }
        });
        mAdapter = new RentalRecordAdapter(new ArrayList<RentalRecordItemInfo>(), mRecyclerView.getRecyclerView());
        mAdapter.setHeaderView(R.layout.layout_placeholder);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SkipTopBottomDivider(this, true, true));
    }

    @Override
    protected void initData() {
        super.initData();
        requestRentalRecord();
        reqeustRentalRecordItme(true);
    }

    @NonNull
    @Override
    protected String title() {
        return "出租记录";
    }

    private void requestRentalRecord() {
       /* getOkGo("")
                .execute(new JsonCallback<Base_Class_Info<RentalRecordInfo>>() {

                    @Override
                    public void onSuccess(Base_Class_Info<RentalRecordInfo> rentalRecordInfo, Call call, Response response) {
                        rentalRecordInfo = new Base_Class_Info<>();
                        RentalRecordInfo recordInfo = new RentalRecordInfo();
                        recordInfo.setParkSpaceImg("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522131551312&di=52422f4384734a296b537d5040c2e89c&imgtype=0&src=http%3A%2F%2F4493bz.1985t.com%2Fuploads%2Fallimg%2F141025%2F4-141025144557.jpg");
                        recordInfo.setVoltage("电量状态:50%");
                        recordInfo.setPackSpaceStatus("车位状态:正在出租");
                        recordInfo.setParkSpaceNumber("车位编号:10086");
                        ImageUtil.showPic(mRentalRecordIv, rentalRecordInfo.data.getParkSpaceImg());
                        mRentalRecordParkNumber.setText(rentalRecordInfo.data.getParkSpaceNumber());
                        mRentalRecordParkStatus.setText(rentalRecordInfo.data.getPackSpaceStatus());
                        mRentalRecordElectricity.setText(rentalRecordInfo.data.getVoltage());
                    }
                });*/

        Base_Class_Info<RentalRecordInfo> rentalRecordInfo = new Base_Class_Info<>();
        RentalRecordInfo recordInfo = new RentalRecordInfo();
        recordInfo.setParkSpaceImg("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1522131551312&di=52422f4384734a296b537d5040c2e89c&imgtype=0&src=http%3A%2F%2F4493bz.1985t.com%2Fuploads%2Fallimg%2F141025%2F4-141025144557.jpg");
        recordInfo.setVoltage("电量状态:50%");
        recordInfo.setPackSpaceStatus("车位状态:正在出租");
        recordInfo.setParkSpaceNumber("车位编号:10086");
        rentalRecordInfo.data = recordInfo;
        ImageUtil.showPic(mRentalRecordIv, rentalRecordInfo.data.getParkSpaceImg());
        mRentalRecordParkNumber.setText(rentalRecordInfo.data.getParkSpaceNumber());
        mRentalRecordParkStatus.setText(rentalRecordInfo.data.getPackSpaceStatus());
        mRentalRecordElectricity.setText(rentalRecordInfo.data.getVoltage());
    }

    private void reqeustRentalRecordItme(final boolean refresh) {
        /*getOkGo("").execute(new JsonCallback<Base_Class_List_Info<RentalRecordItemInfo>>() {

            @Override
            public void onSuccess(Base_Class_List_Info<RentalRecordItemInfo> rentalRecordItemInfoBase_class_list_info, Call call, Response response) {
                dismmisLoadingDialog();
                if (rentalRecordItemInfoBase_class_list_info.data.isEmpty() && mAdapter.getData().isEmpty()) {
                    mRecyclerView.showEmpty(null);
                } else {
                    if (refresh) {
                        mAdapter.setNewData(rentalRecordItemInfoBase_class_list_info.data);
                    } else {
                        mAdapter.addData(rentalRecordItemInfoBase_class_list_info.data);
                    }
                }
                stopRefresh();
            }
        });*/

        dismmisLoadingDialog();
        Base_Class_List_Info<RentalRecordItemInfo> rentalRecordItemInfoBase_class_list_info = new Base_Class_List_Info<>();
        ArrayList<RentalRecordItemInfo> rentalRecordItemInfos = new ArrayList<>();
        RentalRecordItemInfo itemInfo;
        for (int i = 0; i < 3; i++) {
            itemInfo = new RentalRecordItemInfo();
            itemInfo.setRentalCarNumber("车牌编号:粤TBJ304");
            itemInfo.setRentalStartDate("2018-3-28 12:32");
            itemInfo.setRentalFee("获得收益:14.00元");
            itemInfo.setRentalTime("出租时长:2小时03分");
            rentalRecordItemInfos.add(itemInfo);
        }
        rentalRecordItemInfoBase_class_list_info.data = rentalRecordItemInfos;
        if (rentalRecordItemInfoBase_class_list_info.data.isEmpty() && mAdapter.getData().isEmpty()) {
            mRecyclerView.showEmpty(null);
        } else {
            if (refresh) {
                mAdapter.setNewData(rentalRecordItemInfoBase_class_list_info.data);
            } else {
                mAdapter.addData(rentalRecordItemInfoBase_class_list_info.data);
            }
        }
        stopRefresh();
        stopLoadMore();
    }

    @Override
    protected RecyclerView.LayoutManager createLayouManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void onRefresh() {
        reqeustRentalRecordItme(true);
    }

    @Override
    protected void onLoadMore() {
        reqeustRentalRecordItme(false);
    }

}
