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
import com.tuzhao.utils.ConstansUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/3/28.
 */

public class ParkSpaceSettingActivity extends BaseStatusActivity {

    private TextView mParkSpaceNumber;

    private TextView mRentDate;

    private TextView mPauseRentDate;

    private ParkSpaceRentTimeAdapter mAdapter;

    private String mParkSpaceId;

    @Override
    protected int resourceId() {
        return R.layout.activity_park_space_setting_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        if ((mParkSpaceId = getIntent().getStringExtra(ConstansUtil.PARK_SPACE_ID)) == null) {
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

        SwitchButton switchButton = findViewById(R.id.park_space_setting_renten_sb);
        switchButton.setChecked(true);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        findViewById(R.id.park_space_space_setting_cl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ModifyShareTimeActivity.class, ConstansUtil.PARK_SPACE_ID, mParkSpaceId);
            }
        });

        findViewById(R.id.park_space_setting_bluetooth_binding).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(BluetoothBindingActivity.class,ConstansUtil.PARK_SPACE_ID, mParkSpaceId);
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
        mParkSpaceNumber.setText("A车位");
        mRentDate.setText("2018-08-16 — 2018-09-16");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add("9:00 - 18:00 (每周六)");
        }
        mAdapter.setNewData(list);
        mPauseRentDate.setText("8月20日，9月1日，10月1日，10月11日");
        dismmisLoadingDialog();
    }

}
