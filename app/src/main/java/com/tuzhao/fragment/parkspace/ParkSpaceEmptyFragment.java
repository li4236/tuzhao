package com.tuzhao.fragment.parkspace;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.ParkOrChargeActivity;
import com.tuzhao.activity.mine.AddParkSpaceActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.publicmanager.LocationManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.customView.ArrowView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.ViewUtil;

/**
 * Created by juncoder on 2018/11/20.
 */
public class ParkSpaceEmptyFragment extends BaseStatusFragment implements View.OnClickListener {

    /**
     * 0(添加我的车位)    1(跳转到长租车位)
     */
    private int mType;

    public static ParkSpaceEmptyFragment newInstance(int type) {
        ParkSpaceEmptyFragment fragment = new ParkSpaceEmptyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstansUtil.TYPE, type);
        fragment.setTAG(fragment.getTAG() + " type:" + type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.viewstub_select_park_space_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mType = getArguments().getInt(ConstansUtil.TYPE);
        }

        TextView textView = findViewById(R.id.viewstub_tv);
        ArrowView arrowView = findViewById(R.id.viewstub_av);
        if (mType == 0) {
            textView.setText("添加车位");
        }

        textView.setOnClickListener(this);
        arrowView.setOnClickListener(this);
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewstub_tv:
            case R.id.viewstub_av:
                if (mType == 0) {
                    addMyParkSpace();
                } else {
                    startActivity(ParkOrChargeActivity.class, ConstansUtil.CITY_CODE, LocationManager.getInstance().getmAmapLocation().getCityCode());
                }
                break;
        }
    }

    private void addMyParkSpace() {
        if (!UserManager.getInstance().getUserInfo().isCertification()) {
            ViewUtil.showCertificationDialog(getContext(), "添加车位");
        } else {
            startActivity(AddParkSpaceActivity.class);
        }
    }

}
