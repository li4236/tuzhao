package com.tuzhao.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;

/**
 * Created by TZL12 on 2018/2/28.
 */

public class PayActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_layout);

        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {
    }

    private void initData() {
    }

    private void initEvent() {
    }
}
