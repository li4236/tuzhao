package com.tuzhao.fragment.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.LoginActivity;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.Pair;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.db.DatabaseImp;
import com.tuzhao.utils.ClipboardObserver;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.CountdownUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;
import com.tuzhao.utils.SmsObserver;
import com.tuzhao.utils.ViewUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/31.
 */
public class SmsLoginFragment extends BaseStatusFragment implements View.OnClickListener, IntentObserver {

    private EditText mVerifyCodeEt;

    private TextView mSendSms;

    private TextView mUserLogin;

    private CountdownUtil mCountdownUtil;

    private SmsObserver mSmsObserver;

    private ClipboardObserver mClipboardObserver;

    /**
     * 0(短信登录),1(忘记密码),2(初始微信登录),3(新用户登录),4(新用户绑定微信登录)
     */
    private int mStatus;

    private String mTelephoneNumber;

    private String mTelephoneToken;

    private User_Info mUserInfo;

    public static SmsLoginFragment getInstance(int status, String telephoneNumber) {
        SmsLoginFragment fragment = new SmsLoginFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstansUtil.STATUS, status);
        bundle.putString(ConstansUtil.TELEPHONE_NUMBER, telephoneNumber);
        fragment.setArguments(bundle);
        fragment.setTAG(fragment.getTAG() + ",status:" + status);
        return fragment;
    }

    public static SmsLoginFragment getInstance(int status, User_Info userInfo) {
        SmsLoginFragment fragment = new SmsLoginFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstansUtil.STATUS, status);
        bundle.putParcelable(ConstansUtil.USER_INFO, userInfo);
        fragment.setArguments(bundle);
        fragment.setTAG(fragment.getTAG() + ",status:" + status);
        return fragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_sms_login_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mStatus = getArguments().getInt(ConstansUtil.STATUS);
            if (mStatus == 2 || mStatus == 4) {
                mUserInfo = getArguments().getParcelable(ConstansUtil.USER_INFO);
                if (mUserInfo != null) {
                    mTelephoneNumber = mUserInfo.getUsername();
                } else {
                    showFiveToast("参数异常，请重新登录");
                    finish();
                }
            } else {
                mTelephoneNumber = getArguments().getString(ConstansUtil.TELEPHONE_NUMBER);
            }
        }

        mVerifyCodeEt = findViewById(R.id.sms_et);
        mSendSms = findViewById(R.id.send_sms);
        mUserLogin = findViewById(R.id.login_tv);

        if (mStatus == 2 || mStatus == 3 || mStatus == 4) {
            hideView(findViewById(R.id.password_login));
            mUserLogin.setText(mStatus == 4 ? "登录" : "下一步");
            TextView title = findViewById(R.id.title);
            title.setText(mStatus == 4 ? "请进行短信验证" : "请先进行短信验证");
        } else {
            findViewById(R.id.password_login).setOnClickListener(this);
        }

        mVerifyCodeEt.requestFocus();
        mSendSms.setOnClickListener(this);
        mUserLogin.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mClipboardObserver = new ClipboardObserver(requireContext(), new SuccessCallback<String>() {
            @Override
            public void onSuccess(String s) {
                mVerifyCodeEt.setText(s);
            }
        });
        mClipboardObserver.registerClipEvents();
        IntentObserable.registerObserver(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_sms:
                sendSms();
                break;
            case R.id.password_login:
                IntentObserable.dispatch(ConstansUtil.PASSWORD_LOGIN, ConstansUtil.TELEPHONE_NUMBER, mTelephoneNumber);
                break;
            case R.id.login_tv:
                if (mTelephoneToken == null) {
                    showFiveToast("请先获取短信验证码");
                } else if (getTextLength(mVerifyCodeEt) != 4) {
                    showFiveToast("你的验证码不正确哦");
                } else {
                    onLoginClick();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof LoginActivity) {
            ViewUtil.translateView(mUserLogin, mView, ((LoginActivity) getActivity()).mPair);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mClipboardObserver.unregisterClipEvents();
        if (mCountdownUtil != null) {
            mCountdownUtil.cancel();
        }
        if (mSmsObserver != null) {
            requireContext().getContentResolver().unregisterContentObserver(mSmsObserver);
        }
        IntentObserable.unregisterObserver(this);
    }

    private void sendSms() {
        showLoadingDialog("发送中...");
        getOkGo(HttpConstants.sendSms)
                .params("phone", mTelephoneNumber)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        mTelephoneToken = o.data;
                        startCountdown();
                        registerSmsObserver();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "103":
                                case "106":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                case "104":
                                    showFiveToast("验证码过期，请重新获取");
                                    break;
                                case "105":
                                    showFiveToast("验证码错误");
                                    break;
                            }
                        }
                    }
                });

    }

    /**
     * 重新发送验证码的倒计时
     */
    private void startCountdown() {
        mSendSms.setClickable(false);
        if (mCountdownUtil == null) {
            mCountdownUtil = new CountdownUtil(60, new CountdownUtil.OnTimeCallback() {
                @Override
                public void onTime(int time) {
                    String sendAgain = "重新发送(" + time + "s)";
                    mSendSms.setText(sendAgain);
                }

                @Override
                public void onTimeEnd() {
                    mSendSms.setClickable(true);
                    mSendSms.setText("重新发送");
                }
            });
        }
        mCountdownUtil.start();
    }

    /**
     * 注册自动读取短信内容监听事件
     */
    private void registerSmsObserver() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_SMS}, 0x123);
        } else {
            mSmsObserver = new SmsObserver(new Handler(), requireContext(), new SmsObserver.SmsListener() {
                @Override
                public void onResult(String smsContent) {
                    mVerifyCodeEt.setText(smsContent);
                }
            });
            requireContext().getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mSmsObserver);
        }
    }

    private void onLoginClick() {
        switch (mStatus) {
            case 0:
                requestSmsLogin();
                break;
            case 1:
            case 2:
            case 3:
                checkVerificationCode();
                break;
            case 4:
                bindingWechatLogin();
                break;
        }
    }

    private void requestSmsLogin() {
        showLoadingDialog("登录中...");
        mUserLogin.setClickable(false);
        OkGo.post(HttpConstants.requestSmsLogin)
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("telephoneToken", mTelephoneToken)
                .params("verifyCode", getText(mVerifyCodeEt))
                .params("registrationId", new DatabaseImp(requireContext()).getRegistrationId())
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        IntentObserable.dispatch(ConstansUtil.LOGIN_SUCCESS, ConstansUtil.INTENT_MESSAGE, o.data);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mUserLogin.setClickable(true);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "103":
                                case "106":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                case "104":
                                    showFiveToast("验证码已过期，请重新发送");
                                    break;
                                case "105":
                                    showFiveToast("验证码错误");
                                    break;
                                default:
                                    showFiveToast("登录失败，请稍后重试");
                                    break;
                            }
                        }
                    }
                });
    }

    private void checkVerificationCode() {
        showLoadingDialog("验证中...");
        OkGo.post(HttpConstants.checkCode)
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("phoneToken", mTelephoneToken)
                .params("code", getText(mVerifyCodeEt))
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> stringBase_class_info, Call call, Response response) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstansUtil.PASS_CODE, stringBase_class_info.data);
                        switch (mStatus) {
                            case 1:
                                bundle.putString(ConstansUtil.TELEPHONE_NUMBER, mTelephoneNumber);
                                IntentObserable.dispatch(ConstansUtil.CHANGE_PASSWORD, ConstansUtil.INTENT_MESSAGE, bundle);
                                break;
                            case 2:
                                //微信初次登录设置密码
                                bundle.putParcelable(ConstansUtil.USER_INFO, mUserInfo);
                                IntentObserable.dispatch(ConstansUtil.WECHAT_PASSWORD_LOGIN, ConstansUtil.INTENT_MESSAGE, bundle);
                                break;
                            case 3:
                                bundle.putString(ConstansUtil.TELEPHONE_NUMBER, mTelephoneNumber);
                                IntentObserable.dispatch(ConstansUtil.SET_NEW_USER_PASSWORD, ConstansUtil.INTENT_MESSAGE, bundle);
                                break;
                        }
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("验证码已过期，请重新发送");
                                    break;
                                case "102":
                                    showFiveToast("验证码错误");
                                    break;
                            }
                        }
                    }
                });
    }

    private void bindingWechatLogin() {
        showLoadingDialog("登录中...");
        OkGo.post(HttpConstants.bindingWechatLogin)
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("telephoneToken", mTelephoneToken)
                .params("openId", mUserInfo.getOpenId())
                .params("unionId", mUserInfo.getUnionId())
                .params("wechatNickname", mUserInfo.getWechatNickname())
                .params("registrationId", new DatabaseImp(getContext()).getRegistrationId())
                .params("verifyCode", getText(mVerifyCodeEt))
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {

                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> user_infoBase_class_info, Call call, Response response) {
                        dismmisLoadingDialog();
                        IntentObserable.dispatch(ConstansUtil.LOGIN_SUCCESS, ConstansUtil.INTENT_MESSAGE, user_infoBase_class_info.data);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "106":
                                    showFiveToast("验证码过期，请重新获取");
                                    break;
                                case "107":
                                    showFiveToast("验证码错误");
                                    break;
                                case "108":
                                    showFiveToast("该手机号已绑定微信");
                                    break;
                                case "109":
                                    showFiveToast("该手机号未注册，不能绑定微信");
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    public void onReceive(Intent intent) {
        if (ConstansUtil.KEYBOARD_HEIGHT_CHANGE.equals(intent.getAction())) {
            ViewUtil.translateView(mUserLogin, mView, new Pair<>(intent.getIntExtra(ConstansUtil.INTENT_MESSAGE, 0),
                    intent.getIntExtra(ConstansUtil.HEIGHT, 1920)));
        }
    }

}
