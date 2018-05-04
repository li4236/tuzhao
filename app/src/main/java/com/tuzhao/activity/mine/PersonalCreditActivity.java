package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tianzhili.www.myselfsdk.dashboardview.view.DashboardView;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.publicmanager.UserManager;

/**
 * Created by TZL12 on 2018/1/9.
 */

public class PersonalCreditActivity extends BaseActivity {

    DashboardView dashboardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalcredit_layout);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        dashboardView = findViewById(R.id.id_activity_personaldredit_layout_dashboardview);
    }

    private void initData() {
        try {
            int a = Integer.parseInt(UserManager.getInstance().getUserInfo().getCredit());
            dashboardView.setValue(a);
            if (a >= 0 && a <= 300) {
                dashboardView.setText("信用极差");
            } else if (a > 300 && a <= 500) {
                dashboardView.setText("信用一般");
            } else if (a > 500 && a <= 650) {
                dashboardView.setText("信用良好");
            } else if (a > 650 && a <= 800) {
                dashboardView.setText("信用优秀");
            } else if (a > 800 && a <= 1000) {
                dashboardView.setText("信用极好");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initEvent() {

        findViewById(R.id.id_activity_personalcredit_layout_imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
