package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.LoadFailCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.alipay.ZhimaCertification;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.OnLoadCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.IDCard;
import com.tuzhao.utils.ViewUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/22.
 */
public class CertifyZhimaActivity extends BaseStatusActivity {

    private EditText mRealNameEt;

    private EditText mIdCardNumberEt;

    private ZhimaCertification mZhimaCertification;

    private String mPassCode;

    @Override
    protected int resourceId() {
        return R.layout.activity_certify_zhima_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mPassCode = getIntent().getStringExtra(ConstansUtil.PASS_CODE);

        mRealNameEt = findViewById(R.id.real_name_et);
        mIdCardNumberEt = findViewById(R.id.id_card_et);

        if (mPassCode != null) {
            goneView(findViewById(R.id.real_name_tv));
            ((TextView) findViewById(R.id.start_certify)).setText("确定重置");
            mIdCardNumberEt.requestFocus();
        }

        findViewById(R.id.start_certify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPassCode == null && getTextLength(mRealNameEt) == 0) {
                    showFiveToast("请输入您的姓名");
                } else if (getTextLength(mIdCardNumberEt) == 0) {
                    showFiveToast("请输入您的身份证号");
                } else {
                    String result = IDCard.IDCardValidate(getText(mIdCardNumberEt).trim().toUpperCase());
                    if (result.equals("")) {
                        if (mPassCode != null) {
                            resetPaymentPassword();
                        } else {
                            getCertifyZhimaUrl();
                        }
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
        if (mPassCode != null) {
            return "重置密码";
        }
        return "实名认证";
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mZhimaCertification != null && mZhimaCertification.isCertifyZhima()) {
            getCertifyZhimaResult();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mZhimaCertification != null) {
            mZhimaCertification.onDestroy();
        }
    }

    private void getCertifyZhimaUrl() {
        if (mZhimaCertification == null) {
            mZhimaCertification = new ZhimaCertification(this);
        }
        mZhimaCertification.getCertifyZhimaUrl(getText(mRealNameEt).trim(), getText(mIdCardNumberEt).trim(), new LoadFailCallback() {
            @Override
            public void onLoadFail(Exception e) {
                mZhimaCertification.setCertifyZhima(false);
                if (!handleException(e)) {
                    switch (e.getMessage()) {
                        case "101":
                            showFiveToast("你已实名认证过了哦");
                            finish();
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

    private void resetPaymentPassword() {
        getOkGo(HttpConstants.resetPaymentPassword)
                .params("paymentPassword", DensityUtil.MD5code(getIntent().getStringExtra(ConstansUtil.PAYMENT_PASSWORD)))
                .params("passCode", mPassCode)
                .params("idCardNumber", getText(mIdCardNumberEt).trim())
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        setResult(RESULT_OK);
                        showFiveToast("设置支付密码成功");
                        UserManager.getInstance().getUserInfo().setPaymentPassword("1");
                        ViewUtil.closeInputMethod(mIdCardNumberEt);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                case "106":
                                    paramsError();
                                    break;
                                case "103":
                                    showFiveToast("短信验证码失效，请重新获取");
                                    finish();
                                    break;
                                case "104":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                case "105":
                                    showFiveToast("身份证错误，请仔细核查");
                                    break;
                                case "107":
                                    setResult(RESULT_OK);
                                    showFiveToast("设置支付密码成功");
                                    UserManager.getInstance().getUserInfo().setPaymentPassword("1");
                                    ViewUtil.closeInputMethod(mIdCardNumberEt);
                                    finish();
                                    break;
                            }
                        }
                    }
                });
    }

}
