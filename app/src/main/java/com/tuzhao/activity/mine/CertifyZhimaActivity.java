package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.info.User_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.alipay.ZhimaCertification;
import com.tuzhao.publicwidget.callback.OnLoadCallback;
import com.tuzhao.utils.IDCard;

/**
 * Created by juncoder on 2018/8/22.
 */
public class CertifyZhimaActivity extends BaseStatusActivity {

    private EditText mRealNameEt;

    private EditText mIdCardNumberEt;

    private ZhimaCertification mZhimaCertification;

    @Override
    protected int resourceId() {
        return R.layout.activity_certify_zhima_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mRealNameEt = findViewById(R.id.real_name_et);
        mIdCardNumberEt = findViewById(R.id.id_card_et);

        mZhimaCertification = new ZhimaCertification(this);

        findViewById(R.id.start_certify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getTextLength(mRealNameEt) == 0) {
                    showFiveToast("请输入您的姓名");
                } else if (getTextLength(mIdCardNumberEt) == 0) {
                    showFiveToast("请输入您的身份证号");
                } else {
                    String result = IDCard.IDCardValidate(getText(mIdCardNumberEt).trim().toUpperCase());
                    if (result.equals("")) {
                        getCertifyZhimaUrl();
                    } else {
                        showFiveToast(result);
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
    }

    @NonNull
    @Override
    protected String title() {
        return "实名认证";
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mZhimaCertification.isCertifyZhima()) {
            getCertifyZhimaResult();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mZhimaCertification.onDestroy();
    }

    private void getCertifyZhimaUrl() {
        mZhimaCertification.getCertifyZhimaUrl(getText(mRealNameEt).trim(), getText(mIdCardNumberEt).trim(), new LoadFailCallback() {
            @Override
            public void onLoadFail(Exception e) {
                mZhimaCertification.setCertifyZhima(false);
                if (!handleException(e)) {
                    switch (e.getMessage()) {
                        case "101":
                            showFiveToast("你已实名认证过了哦");
                            break;
                        case "102":
                            showFiveToast("服务器异常，请稍后再试");
                            break;
                    }
                }
            }
        });
    }

    private void getCertifyZhimaResult() {
        showLoadingDialog("查询结果...");
        mZhimaCertification.getCertifyZhimaResult(new OnLoadCallback<User_Info, Exception>() {
            @Override
            public void onSuccess(User_Info user_info) {
                User_Info userInfo = UserManager.getInstance().getUserInfo();
                userInfo.setGender(user_info.getGender());
                userInfo.setBirthday(user_info.getBirthday());
                userInfo.setRealName(user_info.getRealName());

                showFiveToast("实名认证已完成");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFail(Exception e) {
                if (!handleException(e)) {
                    mZhimaCertification.setCertifyZhima(false);
                }
            }
        });
    }

}
