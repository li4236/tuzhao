package com.tuzhao.activity.mine;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.CountdownUtil;
import com.tuzhao.utils.SmsObserver;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/7/20.
 */
public class SMSVerificationActivity extends BaseStatusActivity {

    private TextView mInputSmsHint;

    private ConstraintLayout mVerifyCodeCl;

    private EditText mFirstVerifyCode;

    private EditText mSecondVerifyCode;

    private EditText mThirdVerifyCode;

    private EditText mFourthVerifyCode;

    private EditText[] mVerfifyCodes;

    private TextWatcher[] mTextWatchers;

    private TextView mVerfiCodeError;

    private TextView mSendAgain;

    private String mTelephoneToken;

    private CountdownUtil mCountdownUtil;

    private ObjectAnimator mCycleAnimator;

    private ValueAnimator mHideAnimator;

    private SmsObserver mSmsObserver;

    @Override
    protected int resourceId() {
        return R.layout.activity_sms_verification_layout;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {
        mTextWatchers = new TextWatcher[4];
        mVerfifyCodes = new EditText[4];
        mInputSmsHint = findViewById(R.id.input_sms_hint);
        mVerifyCodeCl = findViewById(R.id.verify_code_cl);
        mVerfifyCodes[0] = mFirstVerifyCode = findViewById(R.id.first_verify_code);
        mVerfifyCodes[1] = mSecondVerifyCode = findViewById(R.id.second_verify_code);
        mVerfifyCodes[2] = mThirdVerifyCode = findViewById(R.id.third_verify_code);
        mVerfifyCodes[3] = mFourthVerifyCode = findViewById(R.id.fourth_verify_code);
        mVerfiCodeError = findViewById(R.id.verify_code_error);
        mSendAgain = findViewById(R.id.send_again);

        for (int i = 0; i < 4; i++) {
            final int finalI = i;
            mTextWatchers[finalI] = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!TextUtils.isEmpty(getText(mVerfifyCodes[finalI]))) {
                        setFocus(finalI);
                    } else {
                        deleteAll();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };
            mVerfifyCodes[finalI].addTextChangedListener(mTextWatchers[finalI]);

            mVerfifyCodes[finalI].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        for (EditText mVerfifyCode : mVerfifyCodes) {
                            if (TextUtils.isEmpty(getText(mVerfifyCode))) {
                                mVerfifyCodes[finalI].clearFocus();
                                mVerfifyCode.requestFocus();
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });

            mVerfifyCodes[finalI].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        deleteAll();
                    }
                    return false;
                }
            });
        }

        mSendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChangePasswordCode();
            }
        });
    }

    @Override
    protected void initData() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 0x111);

        String telephoneHint = UserManager.getInstance().getUserInfo().getUsername();
        telephoneHint = "请输入手机尾号为" + telephoneHint.substring(telephoneHint.length() - 4, telephoneHint.length()) + "收到的验证码";
        mInputSmsHint.setText(telephoneHint);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 0x123);
        } else {
            registerSmsObserver();
        }
        sendChangePasswordCode();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerSmsObserver();
            }
        }
    }

    @NonNull
    @Override
    protected String title() {
        return "短信验证";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCycleAnimator != null) {
            mCycleAnimator.cancel();
        }
        if (mHideAnimator != null) {
            mHideAnimator.cancel();
        }
        if (mCountdownUtil != null) {
            mCountdownUtil.cancel();
        }

        if (mSmsObserver != null) {
            getContentResolver().unregisterContentObserver(mSmsObserver);
        }
    }

    /**
     * 自动读取短信内容
     */
    private void registerSmsObserver() {
        mSmsObserver = new SmsObserver(new Handler(), this, new SmsObserver.SmsListener() {
            @Override
            public void onResult(String smsContent) {
                for (int i = 0; i < mVerfifyCodes.length; i++) {
                    mVerfifyCodes[i].removeTextChangedListener(mTextWatchers[i]);
                    mVerfifyCodes[i].setText(String.valueOf(smsContent.charAt(i)));
                }
                verifyChangePasswordCode(smsContent);
            }
        });
        getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mSmsObserver);
    }

    private void deleteAll() {
        for (int i = 0; i < mVerfifyCodes.length; i++) {
            mVerfifyCodes[i].removeTextChangedListener(mTextWatchers[i]);
            mVerfifyCodes[i].setText("");
            mVerfifyCodes[i].addTextChangedListener(mTextWatchers[i]);
        }
        mVerfifyCodes[0].requestFocus();
    }

    /**
     * @param current 当前是第几个EditText，从0开始算
     */
    private void setFocus(int current) {
        current++;
        if (current <= 3) {
            //下一个在有效范围内[0-3]
            if (TextUtils.isEmpty(getText(mVerfifyCodes[current]))) {
                //如果下一个EditText还没输入验证码则请求焦点
                mVerfifyCodes[current].requestFocus();
            } else {
                //如果下一个EditText已经输入验证码了则递归看下下一个是否已经输入
                setFocus(current);
            }
        } else {
            //第3个EditText调用的则判断是否全部EditText都输入了验证码
            if (allInput()) {
                //如果都输入了验证码则请求看验证码是否正确
                StringBuilder verifyCode = new StringBuilder();
                for (EditText editText : mVerfifyCodes) {
                    verifyCode.append(getText(editText));
                }
                verifyChangePasswordCode(verifyCode.toString());
            }
        }
    }

    /**
     * @return true(全部EditText都输入验证码)
     */
    private boolean allInput() {
        return !TextUtils.isEmpty(getText(mFirstVerifyCode)) && !TextUtils.isEmpty(getText(mSecondVerifyCode))
                && !TextUtils.isEmpty(getText(mThirdVerifyCode)) && !TextUtils.isEmpty(getText(mFourthVerifyCode));
    }

    /**
     * 重新发送验证码的倒计时
     */
    private void startCountdown() {
        mSendAgain.setTextColor(ConstansUtil.G6_COLOR);
        if (mCountdownUtil == null) {
            mCountdownUtil = new CountdownUtil(60, new CountdownUtil.OnTimeCallback() {
                @Override
                public void onTime(int time) {
                    String sendAgain = "重新发送(" + time + "s)";
                    mSendAgain.setText(sendAgain);
                }

                @Override
                public void onTimeEnd() {
                    mSendAgain.setTextColor(ConstansUtil.Y3_COLOR);
                    mSendAgain.setClickable(true);
                    mSendAgain.setText("重新发送");
                }
            });
        }
        mCountdownUtil.start();
    }

    /**
     * 请求发送验证码
     */
    private void sendChangePasswordCode() {
        mSendAgain.setClickable(false);
        showLoadingDialog("正在发送...");
        getOkGo(HttpConstants.sendChangePasswordCode)
                .params("telephone", UserManager.getInstance().getUserInfo().getUsername())
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        mTelephoneToken = o.data;
                        startCountdown();
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("今日发送已达上限，请明天再试");
                        }
                    }
                });

    }

    private void verifyChangePasswordCode(String verifyCode) {
        if (mTelephoneToken == null) {
            showFiveToast("请先发送短信");
            return;
        }
        showLoadingDialog("正在验证...");
        OkGo.post(HttpConstants.verifyChangePasswordCode)
                .tag(TAG)
                .headers("telephoneToken", mTelephoneToken)
                .params("verifyCode", verifyCode)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        startActivity(ChangePasswordRefactoryActivity.class, ConstansUtil.PASS_CODE, o.data);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        deleteAll();
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("验证码已过期，请重新发送");
                                    break;
                                case "102":
                                    setVerifyCodeStatus(false);
                                    break;
                            }
                        }
                    }
                });
    }

    private void setVerifyCodeStatus(boolean normal) {
        if (normal) {
            //把验证码恢复到正常状态，并为第一个EditText请求焦点
            for (EditText editText : mVerfifyCodes) {
                editText.setBackgroundResource(R.drawable.normal_g6_focus_y3_stroke_all_3dp);
            }
            mFirstVerifyCode.requestFocus();
            mFirstVerifyCode.setSelection(getText(mFourthVerifyCode).length());
        } else {
            //验证码错误则背景框变红，并且显示出“验证码错误”，同时EditText进行抖动
            for (EditText editText : mVerfifyCodes) {
                editText.clearFocus();
                editText.setBackgroundResource(R.drawable.r8_stroke_all_3dp);
            }
            if (!isVisible(mVerfiCodeError)) {
                mVerfiCodeError.setVisibility(View.VISIBLE);
            }
            startVerifyCodeErrorAnimator();
        }
    }

    private void startVerifyCodeErrorAnimator() {
        if (mCycleAnimator == null) {
            //EditText的抖动效果，在500毫秒内左右移动EditText1/4的宽度3次，抖动完后显示正常输入状态
            mCycleAnimator = ObjectAnimator.ofFloat(mVerifyCodeCl, "translationX", mFirstVerifyCode.getWidth() / 4);
            mCycleAnimator.setInterpolator(new CycleInterpolator(3));
            mCycleAnimator.setDuration(500);
            mCycleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Log.e(TAG, "onAnimationEnd: ");
                    setVerifyCodeStatus(true);
                }
            });
        }
        if (mHideAnimator == null) {
            //3s后隐藏“验证码错误”的字
            mHideAnimator = ValueAnimator.ofInt(1, 2);
            mHideAnimator.setDuration(3000);
            mHideAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mVerfiCodeError.setVisibility(View.INVISIBLE);
                }
            });
        } else if (mHideAnimator.isRunning()) {
            mHideAnimator.cancel();
        }
        mCycleAnimator.start();
        mHideAnimator.start();
    }

}
