package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;

/**
 * Created by juncoder on 2018/6/12.
 * <p>
 * 停车订单的违规说明
 * </p>
 */
public class ViolationInstructionActivity extends BaseStatusActivity {

    @Override
    protected int resourceId() {
        return R.layout.activity_billing_rules_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    @Override
    protected void initData() {
    }

    @NonNull
    @Override
    protected String title() {
        return "违规说明";
    }

}
