package com.tuzhao.fragment.login;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.LoginActivity;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.db.DatabaseImp;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.IntentObserable;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/9/3.
 */
public class LoginInputFragment extends BaseStatusFragment {

    private User_Info mUserInfo;

    private String mTelephone;

    /**
     * 等于null则代表是输入手机号
     */
    private String mPassCode;

    private EditText mInputEt;

    private TextView mNextStep;

    /**
     * 0(新用户设置密码登录) 1(忘记密码,重置密码登录)
     */
    private int mType;

    public static LoginInputFragment getInstance(String telephone, String passCode, int type) {
        LoginInputFragment fragment = new LoginInputFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstansUtil.TELEPHONE_NUMBER, telephone);
        bundle.putString(ConstansUtil.PASS_CODE, passCode);
        bundle.putInt(ConstansUtil.TYPE, type);
        fragment.setArguments(bundle);
        fragment.setTAG(ConstansUtil.CHANGE_PASSWORD);
        return fragment;
    }

    public static LoginInputFragment getInstance(User_Info userInfo) {
        LoginInputFragment fragment = new LoginInputFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstansUtil.USER_INFO, userInfo);
        fragment.setArguments(bundle);
        fragment.setTAG(ConstansUtil.WECHAT_TELEPHONE_LOGIN);
        return fragment;
    }

    public static LoginInputFragment getInstance(User_Info userInfo, String passCode) {
        LoginInputFragment fragment = new LoginInputFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstansUtil.USER_INFO, userInfo);
        bundle.putString(ConstansUtil.PASS_CODE, passCode);
        fragment.setArguments(bundle);
        fragment.setTAG(ConstansUtil.WECHAT_PASSWORD_LOGIN);
        return fragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_login_input_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mUserInfo = getArguments().getParcelable(ConstansUtil.USER_INFO);
            mPassCode = getArguments().getString(ConstansUtil.PASS_CODE);
            mTelephone = getArguments().getString(ConstansUtil.TELEPHONE_NUMBER);
            mType = getArguments().getInt(ConstansUtil.TYPE);
        }

        TextView title = findViewById(R.id.title);
        mNextStep = findViewById(R.id.next_step_tv);
        mInputEt = findViewById(R.id.input_et);

        if (mPassCode != null) {
            title.setText("请设置登录密码");
            mNextStep.setText("完成");

            mInputEt.setHint("请输入登录密码");
            mInputEt.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqistuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./?';:[]{}!@#$%^*()~&<>！￥？【】、《》，。-=+_"));
            mInputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            mInputEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        mNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPassCode == null) {
                    onTelephoneClick();
                } else {
                    onPasswordClick();
                }
            }
        });
    }

    @Override
    protected void initData() {
    }

    private void onTelephoneClick() {
        if (TextUtils.isEmpty(getText(mInputEt))) {
            showFiveToast("请输入手机号");
        } else if (getTextLength(mInputEt) < 11) {
            showFiveToast("手机号格式不正确");
        } else if (!DateUtil.isPhoneNumble(getText(mInputEt))) {
            showFiveToast("手机号不正确");
        } else {
            checkPhoneIsNewUser();
        }
    }

    private void onPasswordClick() {
        if (getTextLength(mInputEt) < 8) {
            showFiveToast("密码长度为8-20位哦");
        } else {
            if (mUserInfo == null) {
                passwordLogin();
            } else {
                initialWechatLogin();
            }
        }
    }

    /**
     * 1(新用户)  2(老用户有openId)  3（老用户没有openId）
     */
    private void checkPhoneIsNewUser() {
        showLoadingDialog();
        mNextStep.setClickable(false);
        getOkGo(HttpConstants.phoneIsNewUser)
                .params("phoneNumber", getText(mInputEt))
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        mNextStep.setClickable(true);
                        mUserInfo.setUsername(getText(mInputEt));
                        switch (o.data) {
                            case "1":
                                Bundle bundle = new Bundle();
                                bundle.putInt(ConstansUtil.STATUS, 2);
                                bundle.putParcelable(ConstansUtil.USER_INFO, mUserInfo);
                                IntentObserable.dispatch(ConstansUtil.SMS_LOGIN, ConstansUtil.INTENT_MESSAGE, bundle);
                                break;
                            case "2":
                                showFiveToast("该手机号已绑定别的微信了哦");
                                break;
                            case "3":
                                Bundle newBundle = new Bundle();
                                newBundle.putInt(ConstansUtil.STATUS, 4);
                                newBundle.putParcelable(ConstansUtil.USER_INFO, mUserInfo);
                                IntentObserable.dispatch(ConstansUtil.SMS_LOGIN, ConstansUtil.INTENT_MESSAGE, newBundle);
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mNextStep.setClickable(true);
                        if (!handleException(e)) {
                            showFiveToast(ConstansUtil.SERVER_ERROR);
                        }
                    }
                });
    }

    private void initialWechatLogin() {
        showLoadingDialog("登录中...");
        getOkGo(HttpConstants.initialWechatLogin)
                .params("openId", mUserInfo.getOpenId())
                .params("unionId", mUserInfo.getUnionId())
                .params("wechatNickname", mUserInfo.getWechatNickname())
                .params("telephoneNumber", mUserInfo.getUsername())
                .params("password", DensityUtil.MD5code(getText(mInputEt)))
                .params("registrationId", new DatabaseImp(getContext()).getRegistrationId())
                .params("passCode", mPassCode)
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        IntentObserable.dispatch(ConstansUtil.LOGIN_SUCCESS, ConstansUtil.INTENT_MESSAGE, o.data);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                case "103":
                                case "104":
                                case "105":
                                case "106":
                                case "107":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                case "108":
                                    showFiveToast("微信登录已失效，请重新登录");
                                    startActivity(LoginActivity.class, ConstansUtil.INTENT_MESSAGE, "");
                                    break;
                                case "109":
                                    showFiveToast("该手机号已绑定别的微信了哦");
                                    break;
                            }
                        }
                    }
                });
    }

    private void passwordLogin() {
        showLoadingDialog("登录中...");
        getOkGo(mType == 0 ? HttpConstants.newUserLogin : HttpConstants.forgetPasswordLogin)
                .params("registrationId", new DatabaseImp(getContext()).getRegistrationId())
                .params("telephoneNumber", mTelephone)
                .params("password", DensityUtil.MD5code(getText(mInputEt)))
                .params("passCode", mPassCode)
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        IntentObserable.dispatch(ConstansUtil.LOGIN_SUCCESS, ConstansUtil.INTENT_MESSAGE, o.data);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                case "102":
                                case "103":
                                case "104":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                case "105":
                                    showFiveToast("验证码失效，请重新验证");
                                    break;
                                case "106":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                case "107":
                                    showFiveToast("该手机号已注册过了哦");
                                    startActivity(LoginActivity.class, ConstansUtil.INTENT_MESSAGE, "");
                                    break;
                            }
                        }
                    }
                });

    }

}
