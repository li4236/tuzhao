package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.adapter.ParkSpaceRentTimeAdapter;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Park_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/3/28.
 */

public class ParkSpaceSettingActivity extends BaseStatusActivity {

    private TextView mParkSpaceNumber;

    private TextView mRentDate;

    private TextView mPauseRentDate;

    private SwitchButton mSwitchButton;

    private ParkSpaceRentTimeAdapter mAdapter;

    private Park_Info mPark_info;

    @Override
    protected int resourceId() {
        return R.layout.activity_park_space_setting_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        if ((mPark_info = (Park_Info) getIntent().getSerializableExtra(ConstansUtil.PARK_SPACE_INFO)) == null) {
            showFiveToast("获取车位信息失败，请稍后重试");
            finish();
        }

        mParkSpaceNumber = findViewById(R.id.park_space_setting_space_number);
        mRentDate = findViewById(R.id.park_space_space_setting_renten_date);
        mPauseRentDate = findViewById(R.id.park_space_setting_pause_date);
        RecyclerView recyclerView = findViewById(R.id.park_space_setting_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ParkSpaceRentTimeAdapter();
        recyclerView.setAdapter(mAdapter);

        mSwitchButton = findViewById(R.id.park_space_setting_renten_sb);
        mSwitchButton.setChecked(mPark_info.getPark_status().equals("2"));
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeParkSpaceStatus(isChecked);
            }
        });

        findViewById(R.id.park_space_space_setting_cl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ModifyShareTimeActivity.class, ConstansUtil.PARK_SPACE_ID, mPark_info.getId());
            }
        });

        findViewById(R.id.park_space_setting_bluetooth_binding).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(BluetoothBindingActivity.class, ConstansUtil.PARK_SPACE_ID, mPark_info.getId());
            }
        });
    }

    @NonNull
    @Override
    protected String title() {
        return "车位设置";
    }

    @Override
    protected void initData() {
        super.initData();
        mParkSpaceNumber.setText(mPark_info.getPark_number());
        mRentDate.setText(mPark_info.getOpen_date());
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add("9:00 - 18:00 (每周六)");
        }
        mAdapter.setNewData(list);
        mPauseRentDate.setText("8月20日，9月1日，10月1日，10月11日");
        dismmisLoadingDialog();
    }

    private void changeParkSpaceStatus(final boolean open) {
        showLoadingDialog("正在修改出租状态");
        getOkGo(HttpConstants.changeParkSpaceStatus)
                .params("parkSpaceId", mPark_info.getId())
                .params("parkSpaceStatus", open ? "1" : "2")
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        mSwitchButton.setChecked(open);
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
    }
}
