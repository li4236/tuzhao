package com.tuzhao.activity.mine;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.MainActivity;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ClipboardObserver;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.CountdownUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.SmsObserver;
import com.tuzhao.utils.ViewUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/7.
 * <p>
 * 修改手机号
 * </p>
 */
public class ChangeTetephoneNumberActivity extends BaseStatusActivity implements View.OnClickListener {

    private EditText mTelephoneNumber;

    private ImageView mClearTetephoneNumber;

    private TextView mGetVerificationCode;

    private TextView mTelephoneNumberError;

    private EditText mVerificationCode;

    private ImageView mClearVerificationCode;

    private TextView mVerificationCodeError;

    private CountdownUtil mCountdownUtil;

    private String mPassCode;

    private String mTelephoneToken;

    private String mGetVerificationCodeTelephone;

    private SmsObserver mSmsObserver;

    private ClipboardObserver mClipboardObserver;

    @Override
    protected int resourceId() {
        return R.layout.activity_change_telephone_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mTelephoneNumber = findViewById(R.id.telephone_number_et);
        mClearTetephoneNumber = findViewById(R.id.clear_telephone_number);
        mGetVerificationCode = findViewById(R.id.get_verify_code);
        mTelephoneNumberError = findViewById(R.id.telephone_number_error);
        mVerificationCode = findViewById(R.id.verify_code_et);
        mClearVerificationCode = findViewById(R.id.clear_verify_code);
        mVerificationCodeError = findViewById(R.id.verify_code_error);

        mTelephoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int telephoneNumberLength = getTextLength(mTelephoneNumber);
                if (telephoneNumberLength > 0) {
                    showView(mClearTetephoneNumber);
                    if (telephoneNumberLength == 11 && DateUtil.isPhoneNumble(getText(mTelephoneNumber)) && mTelephoneToken != null) {
                        //如果之前输入框显示了红色背景的，则手机号正确的时候显示为黄色背景
                        hideView(mTelephoneNumberError);
                        mTelephoneNumber.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);
                    }
                } else {
                    hideView(mClearTetephoneNumber);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int verificationCodeLength = getTextLength(mVerificationCode);
                if (verificationCodeLength > 0) {
                    showView(mClearVerificationCode);
                    if (verificationCodeLength == 4 && isVisible(mVerificationCodeError)) {
                        hideView(mVerificationCodeError);
                        mVerificationCode.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);
                    }
                } else {
                    hideView(mClearVerificationCode);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ViewUtil.openInputMethod(mTelephoneNumber);

        mClearTetephoneNumber.setOnClickListener(this);
        mGetVerificationCode.setOnClickListener(this);
        mClearVerificationCode.setOnClickListener(this);
        findViewById(R.id.confirm_change).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mPassCode = getIntent().getStringExtra(ConstansUtil.PASS_CODE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            //因为验证旧手机号的时候会发送验证码，那时候就已经申请获取权限了，如果用户拒绝了则不再申请
            mSmsObserver = new SmsObserver(new Handler(Looper.getMainLooper()), this, new SmsObserver.SmsListener() {
                @Override
                public void onResult(String smsContent) {
                    hideView(mVerificationCodeError);
                    mVerificationCode.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);
                    mVerificationCode.setText(smsContent);
                }
            });
            getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mSmsObserver);
        }

        mClipboardObserver = new ClipboardObserver(this, new SuccessCallback<String>() {
            @Override
            public void onSuccess(String s) {
                hideView(mVerificationCodeError);
                mVerificationCode.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);
                mVerificationCode.setText(s);
            }
        });
        mClipboardObserver.registerClipEvents();
    }

    @NonNull
    @Override
    protected String title() {
        return "修改手机号";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_telephone_number:
                mTelephoneNumber.setText("");
                break;
            case R.id.get_verify_code:
                if (TextUtils.isEmpty(getText(mTelephoneNumber))) {
                    showTelephoneError("请输入新的手机号");
                } else if (getTextLength(mTelephoneNumber) < 11) {
                    showTelephoneError("手机号格式不正确");
                } else if (!DateUtil.isPhoneNumble(getText(mTelephoneNumber))) {
                    showTelephoneError("手机号不正确");
                } else if (getText(mTelephoneNumber).equals(UserManager.getInstance().getUserInfo().getUsername())) {
                    showTelephoneError("不能与旧手机号一样");
                } else {
                    hideView(mTelephoneNumberError);
                    mTelephoneNumber.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);
                    mGetVerificationCode.setClickable(false);
                    mGetVerificationCode.setTextColor(ConstansUtil.G6_COLOR);
                    sendVerificationCode();
                }
                break;
            case R.id.clear_verify_code:
                mVerificationCode.setText("");
                break;
            case R.id.confirm_change:
                if (getTextLength(mTelephoneNumber) == 0) {
                    showFiveToast("请输入新的手机号");
                } else if (getTextLength(mTelephoneNumber) < 11) {
                    showFiveToast("手机号格式不正确");
                } else if (mTelephoneToken == null) {
                    showTelephoneError("请先获取验证码");
                } else if (!getText(mTelephoneNumber).equals(mGetVerificationCodeTelephone)) {
                    showTelephoneError("手机号与获取验证码的不一致");
                } else if (TextUtils.isEmpty(getText(mVerificationCode))) {
                    showVerificationCodeError("请输入验证码");
                } else if (getTextLength(mVerificationCode) < 4) {
                    showVerificationCodeError("验证码格式不正确");
                } else {
                    changeTelephoneNumber();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountdownUtil != null) {
            mCountdownUtil.cancel();
        }
        if (mSmsObserver != null) {
            getContentResolver().unregisterContentObserver(mSmsObserver);
        }
        mClipboardObserver.unregisterClipEvents();
    }

    private void sendVerificationCode() {
        showLoadingDialog("正在发送...");
        mGetVerificationCodeTelephone = getText(mTelephoneNumber);
        getOkGo(HttpConstants.sendVerificationCode)
                .params("telephone", mGetVerificationCodeTelephone)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        mTelephoneToken = o.data;
                        dismmisLoadingDialog();
                        startCountdown();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mGetVerificationCode.setClickable(true);
                        if (!handleException(e)) {
                            showFiveToast("获取验证码失败，请重新获取");
                        }
                    }
                });
    }

    private void startCountdown() {
        if (mCountdownUtil == null) {
            mCountdownUtil = new CountdownUtil(60, new CountdownUtil.OnTimeCallback() {
                @Override
                public void onTime(int time) {
                    mGetVerificationCode.setText("重新发送(");
                    mGetVerificationCode.append(String.valueOf(time));
                    mGetVerificationCode.append(")");
                }

                @Override
                public void onTimeEnd() {
                    mGetVerificationCode.setText("获取验证码");
                    mGetVerificationCode.setTextColor(ConstansUtil.Y3_COLOR);
                    mGetVerificationCode.setClickable(true);
                }
            });
        }
        mCountdownUtil.start();
    }

    private void changeTelephoneNumber() {
        showLoadingDialog("正在修改...");
        getOkGo(HttpConstants.changeTelephoneNumber)
                .params("passCode", mPassCode)
                .params("telephoneToken", mTelephoneToken)
                .params("verificationCode", getText(mVerificationCode))
                .params("telephoneNumber", getText(mTelephoneNumber))
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        UserManager.getInstance().getUserInfo().setUsername(mGetVerificationCodeTelephone);
                        showFiveToast("修改手机号成功");
                        dismmisLoadingDialog();
                        startActivity(MainActivity.class);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showVerificationCodeError("验证码已过期，请重新获取");
                                    break;
                                case "102":
                                    showVerificationCodeError("验证码不正确");
                                    break;
                                case "103":
                                    showFiveToast("旧手机号的验证码出错，请重新获取");
                                    finish();
                                    break;
                                case "104":
                                    showFiveToast("修改失败，请稍后重试");
                                    break;
                                case "105":
                                    showTelephoneError("新手机号不能与旧的相同哦");
                                    break;
                                case "106":
                                    showTelephoneError("该手机号已被占用");
                                    break;
                                default:
                                    showFiveToast(e.getMessage());
                                    break;
                            }
                        }
                    }
                });
    }

    private void showTelephoneError(String msg) {
        setNewText(mTelephoneNumberError, msg);
        showView(mTelephoneNumberError);
        mTelephoneNumber.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
    }

    private void showVerificationCodeError(String msg) {
        setNewText(mVerificationCodeError, msg);
        showView(mVerificationCodeError);
        mVerificationCode.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
    }

}
