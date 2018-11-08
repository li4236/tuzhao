package com.tuzhao.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.fragment.chargestationdetail.ChargeDetailFragment;
import com.tuzhao.info.ChargeStationInfo;
import com.tuzhao.utils.ConstansUtil;

/**
 * Created by TZL12 on 2017/11/16.
 */

public class ChargestationDetailActivity extends BaseActivity {
    private ChargeDetailFragment chargeDetailFragment;
    private Bundle mBundle = new Bundle();
    private ChargeStationInfo chargestation_info = null;
    private String chargestation_id, city_code;
    private ImageView imageview_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargestationdetail_layout);
        XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.w0), 0);
        mBundle = new Bundle();
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        imageview_back = findViewById(R.id.id_activity_chargestationdetail_imageview_back);
    }

    private void initData() {

        if (getIntent().hasExtra("chargestation_info")) {
            chargestation_info = (ChargeStationInfo) getIntent().getSerializableExtra("chargestation_info");
        } else {
            chargestation_id = getIntent().getStringExtra(ConstansUtil.CHARGE_ID);
            city_code = getIntent().getStringExtra(ConstansUtil.CITY_CODE);
        }

        if (chargestation_info == null) {
            mBundle.putString("chargestation_id", chargestation_id);
            mBundle.putString("city_code", city_code);
            mBundle.putSerializable("chargestation_info", chargestation_info);
        } else {
            mBundle.putSerializable("chargestation_info", chargestation_info);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (chargeDetailFragment == null) {
            chargeDetailFragment = new ChargeDetailFragment();
            chargeDetailFragment.setArguments(mBundle);
            ft.add(R.id.id_activity_chargestationdetail_layout_linerlayout_content, chargeDetailFragment);
        } else {
            ft.show(chargeDetailFragment);
        }
        ft.commit();
    }

    private void initEvent() {

        imageview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
