package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.publicmanager.UserManager;

/**
 * Created by juncoder on 2018/7/20.
 */
public class ChangePasswordMethodActivity extends BaseStatusActivity {

    private TextView mChangePassword;

    private TextView mTelephone;

    @Override
    protected int resourceId() {
        return R.layout.activity_change_password_method_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mChangePassword = findViewById(R.id.change_passwrod_hint);
        mTelephone = findViewById(R.id.telephone_number_tv);

        findViewById(R.id.origin_password_cv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ChangePasswordRefactoryActivity.class);
            }
        });

        findViewById(R.id.telephone_number_cv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SMSVerificationActivity.class);
            }
        });
    }

    @Override
    protected void initData() {
        mChangePassword.setText("为保障用户账号安全\n修改密码请确认身份");
        StringBuilder telephone = new StringBuilder(UserManager.getInstance().getUserInfo().getUsername());
        if (telephone.length() > 6) {
            telephone.replace(3, 8, "*****");
        }
        mTelephone.setText(telephone.toString());
    }

    @NonNull
    @Override
    protected String title() {
        return "修改密码";
    }

}
