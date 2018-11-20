package com.tuzhao.fragment.parkspace;

import android.os.Bundle;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.activity.mine.AddParkSpaceActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.utils.ViewUtil;

/**
 * Created by juncoder on 2018/11/20.
 */
public class AddParkSpaceFragment extends BaseStatusFragment implements View.OnClickListener {

    @Override
    protected int resourceId() {
        return R.layout.fragment_add_park_space_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        findViewById(R.id.add_park_space_tv).setOnClickListener(this);
        findViewById(R.id.add_park_space_pv).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_park_space_tv:
            case R.id.add_park_space_pv:
                if (!UserManager.getInstance().getUserInfo().isCertification()) {
                    ViewUtil.showCertificationDialog(getContext(), "添加车位");
                } else {
                    startActivity(AddParkSpaceActivity.class);
                }
                break;
        }
    }

}
