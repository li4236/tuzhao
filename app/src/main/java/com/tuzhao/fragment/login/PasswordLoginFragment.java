package com.tuzhao.fragment.login;

import android.os.Bundle;
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
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.IntentObserable;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/31.
 */
public class PasswordLoginFragment extends BaseStatusFragment implements View.OnClickListener {

    private EditText mPasswordEt;

    private TextView mUserLogin;

    private String mTelephone;

    public static PasswordLoginFragment getInstance(String telephone) {
        PasswordLoginFragment fragment = new PasswordLoginFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstansUtil.TELEPHONE_NUMBER, telephone);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int resourceId() {
        return R.layout.fragment_password_login_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mTelephone = getArguments().getString(ConstansUtil.TELEPHONE_NUMBER);
        }

        mPasswordEt = findViewById(R.id.password_et);
        mUserLogin = findViewById(R.id.login_tv);

        mPasswordEt.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqistuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,./?';:[]{}!@#$%^*()~&<>！￥？【】、《》，。-=+_"));

        mUserLogin.setOnClickListener(this);
        findViewById(R.id.forget_password).setOnClickListener(this);
        findViewById(R.id.sms_login).setOnClickListener(this);
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_password:
                Bundle forgetBundle = new Bundle();
                forgetBundle.putInt(ConstansUtil.STATUS, 1);
                forgetBundle.putString(ConstansUtil.TELEPHONE_NUMBER, mTelephone);
                IntentObserable.dispatch(ConstansUtil.SMS_LOGIN, ConstansUtil.INTENT_MESSAGE, forgetBundle);
                break;
            case R.id.sms_login:
                Bundle bundle = new Bundle();
                bundle.putString(ConstansUtil.TELEPHONE_NUMBER, mTelephone);
                bundle.putInt(ConstansUtil.STATUS, 0);
                IntentObserable.dispatch(ConstansUtil.SMS_LOGIN, ConstansUtil.INTENT_MESSAGE, bundle);
                break;
            case R.id.login_tv:
                if (getTextLength(mPasswordEt) < 8) {
                    showFiveToast("你输入的密码不正确哦");
                } else {
                    requestPasswordLogin();
                }
                break;
        }
    }

    private void requestPasswordLogin() {
        showLoadingDialog();
        mUserLogin.setClickable(false);
        getOkGo(HttpConstants.requestPasswordLogin)
                .params("telephoneNumber", mTelephone)
                .params("password", DensityUtil.MD5code(getText(mPasswordEt)))
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
                                case "102":
                                case "103":
                                case "105":
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                                case "104":
                                    showFiveToast("密码错误，请重新输入");
                                    break;
                            }
                        }
                    }
                });
    }

}
