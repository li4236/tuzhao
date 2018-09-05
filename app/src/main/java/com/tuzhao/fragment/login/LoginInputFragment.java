package com.tuzhao.fragment.login;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tuzhao.R;
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

    public static LoginInputFragment getInstance(String telephone, String passCode) {
        LoginInputFragment fragment = new LoginInputFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstansUtil.TELEPHONE_NUMBER, telephone);
        bundle.putString(ConstansUtil.PASS_CODE, passCode);
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
        }

        TextView title = findViewById(R.id.title);
        mNextStep = findViewById(R.id.next_step_tv);
        mInputEt = findViewById(R.id.input_et);

        if (mPassCode != null) {
            title.setText("请设置登录密码");
            mNextStep.setText("完成");
            mInputEt.setHint("请输入登录密码");
            mInputEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

            mInputEt.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqistuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./?';:[]{}!@#$%^*()~&<>！￥？【】、《》，。-=+_"));
            mInputEt.setMaxLines(20);
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
            phoneIsNewUser();
        }
    }

    private void onPasswordClick() {
        if (getTextLength(mInputEt) < 8) {
            showFiveToast("密码长度为8-20位哦");
        } else {
            if (mUserInfo == null) {
                forgetPasswordLogin();
            } else {
                initialWechatLogin();
            }
        }
    }

    /**
     * 1(新用户)  2(老用户有openId)  3（老用户没有openId）
     */
    private void phoneIsNewUser() {
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
                                IntentObserable.dispatch(ConstansUtil.SMS_LOGIN, ConstansUtil.USER_INFO, mUserInfo);
                                break;
                            case "2":
                                showFiveToast("该手机号已绑定别的微信了哦");
                                break;
                            case "3":
                                showFiveToast("该手机已注册，如需绑定微信请登录后在个人信息界面绑定");
                                IntentObserable.dispatch(ConstansUtil.FINISH_FRAGMENT);
                                break;
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mNextStep.setClickable(true);
                        if (!handleException(e)) {
                            showFiveToast(e.getMessage());
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
                            showFiveToast("登录失败，请稍后重试");
                        }
                    }
                });
    }

    private void forgetPasswordLogin() {
        showLoadingDialog("登录中...");
        getOkGo(HttpConstants.forgetPasswordLogin)
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

                        }
                    }
                });

    }

}
