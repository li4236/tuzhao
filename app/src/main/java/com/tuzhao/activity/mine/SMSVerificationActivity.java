package com.tuzhao.activity.mine;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.others.PasswordLinearLayout;
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

    private PasswordLinearLayout mPasswordLinearLayout;

    private TextView mVerfiCodeError;

    private TextView mSendAgain;

    private String mTelephoneToken;

    private CountdownUtil mCountdownUtil;

    private ObjectAnimator mCycleAnimator;

    private SmsObserver mSmsObserver;

    private ClipboardManager mClipboardManager;

    private ClipboardManager.OnPrimaryClipChangedListener mClipChangedListener;

    private CharSequence mClipData;

    private boolean mIsResume;

    @Override
    protected int resourceId() {
        return R.layout.activity_sms_verification_layout;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {
        mInputSmsHint = findViewById(R.id.input_sms_hint);
        mPasswordLinearLayout = findViewById(R.id.verify_code_cl);
        mVerfiCodeError = findViewById(R.id.verify_code_error);
        mSendAgain = findViewById(R.id.send_again);

        mPasswordLinearLayout.setSuccessCallback(new SuccessCallback<String>() {
            @Override
            public void onSuccess(String s) {
                checkVerificationCode(s);
            }
        });

        mSendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
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
        registerClipEvents();
    }

    /**
     * 注册复制的监听事件
     */
    private void registerClipEvents() {
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (mClipboardManager != null) {
            mClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    if (mClipboardManager.hasPrimaryClip() && mClipboardManager.getPrimaryClip().getItemCount() > 0) {
                        mClipData = mClipboardManager.getPrimaryClip().getItemAt(0).getText();
                        if (mClipData.length() == 4) {
                            for (int i = 0; i < mClipData.length(); i++) {
                                if (!Character.isDigit(mClipData.charAt(i))) {
                                    //判断复制的是否是纯数字
                                    mClipData = null;
                                    break;
                                }
                            }
                        } else {
                            mClipData = null;
                        }

                        //短信来了直接在弹窗就复制了的，则直接输入
                        if (mClipData != null && mIsResume) {
                            mPasswordLinearLayout.setText((String) mClipData);
                            mClipData = null;
                        }
                    }
                }
            };
            mClipboardManager.addPrimaryClipChangedListener(mClipChangedListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsResume = true;
        if (mClipData != null) {
            //如果是跳到短信复制验证码再返回的则自动输入
            mPasswordLinearLayout.setText((String) mClipData);
            mClipData = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsResume = false;
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
        if (mCountdownUtil != null) {
            mCountdownUtil.cancel();
        }

        if (mSmsObserver != null) {
            getContentResolver().unregisterContentObserver(mSmsObserver);
        }
        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(mClipChangedListener);
        }
    }

    /**
     * 注册自动读取短信内容监听事件
     */
    private void registerSmsObserver() {
        mSmsObserver = new SmsObserver(new Handler(), this, new SmsObserver.SmsListener() {
            @Override
            public void onResult(String smsContent) {
                mPasswordLinearLayout.setText(smsContent);
            }
        });
        getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mSmsObserver);
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
    private void sendVerificationCode() {
        mSendAgain.setClickable(false);
        showNotInputLoadingDialog("正在发送...");
        getOkGo(HttpConstants.sendVerificationCode)
                .params("telephone", UserManager.getInstance().getUserInfo().getUsername())
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        if (mTelephoneToken == null) {
                            mPasswordLinearLayout.deleteAll();
                        }
                        mTelephoneToken = o.data;
                        startCountdown();
                        showFiveToast("验证码发送成功");
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mSendAgain.setClickable(true);
                        if (!handleException(e)) {
                            showFiveToast("今日发送已达上限，请明天再试");
                        }
                    }
                });

    }

    /**
     * 检查验证码是否正确
     */
    private void checkVerificationCode(String verifyCode) {
        if (mTelephoneToken == null) {
            showFiveToast("请先获取验证码");
            return;
        }
        showNotInputLoadingDialog("正在验证...");
        OkGo.post(HttpConstants.checkVerificationCode)
                .tag(TAG)
                .headers("telephoneToken", mTelephoneToken)
                .params("verifyCode", verifyCode)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        if (getIntent().hasExtra(ConstansUtil.INTENT_MESSAGE)) {
                            startActivity(ChangeTetephoneNumberActivity.class, ConstansUtil.PASS_CODE, o.data);
                        } else {
                            startActivity(ChangePasswordRefactoryActivity.class, ConstansUtil.PASS_CODE, o.data);
                        }
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mPasswordLinearLayout.deleteAll();
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

    /**
     * @param normal false(验证码错误则背景框变红，并且显示出“验证码错误”，同时mPasswordLinearLayout进行抖动)
     *               true(密码框恢复原来的颜色)
     */
    private void setVerifyCodeStatus(boolean normal) {
        mPasswordLinearLayout.setShowError(!normal);
        if (!normal) {
            showView(mVerfiCodeError);
            startVerifyCodeErrorAnimator();
        }
    }

    private void startVerifyCodeErrorAnimator() {
        if (mCycleAnimator == null) {
            //mPasswordLinearLayout的抖动效果，在500毫秒内左右移动mPasswordLinearLayout1/10的宽度3次，抖动完后显示正常输入状态
            mCycleAnimator = ObjectAnimator.ofFloat(mPasswordLinearLayout, "translationX", mPasswordLinearLayout.getWidth() / 10);
            mCycleAnimator.setInterpolator(new CycleInterpolator(3));
            mCycleAnimator.setDuration(500);
            mCycleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    setVerifyCodeStatus(true);
                }
            });
        }
        mCycleAnimator.start();
    }

}
