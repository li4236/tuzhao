package com.tuzhao.fragment.parkspace;

import android.os.Bundle;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.activity.ParkOrChargeActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.utils.ConstansUtil;

/**
 * Created by juncoder on 2018/11/20.
 */
public class SelectParkSpaceFragment extends BaseStatusFragment implements View.OnClickListener {

    @Override
    protected int resourceId() {
        return R.layout.fragment_select_park_space_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        findViewById(R.id.select_park_space_tv).setOnClickListener(this);
        findViewById(R.id.select_park_space_av).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_park_space_tv:
            case R.id.select_park_space_av:
                startActivity(ParkOrChargeActivity.class, ConstansUtil.CITY_CODE, LocationManager.getInstance().getmAmapLocation().getCityCode());
                break;
        }
    }

}
