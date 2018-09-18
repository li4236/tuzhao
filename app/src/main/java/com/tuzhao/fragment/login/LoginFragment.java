package com.tuzhao.fragment.login;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tuzhao.R;
import com.tuzhao.fragment.base.BaseStatusFragment;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.db.DatabaseImp;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by juncoder on 2018/8/31.
 */
public class LoginFragment extends BaseStatusFragment implements View.OnClickListener, IntentObserver {

    private EditText mTelephoneNumber;

    private TextView mNextStep;

    @Override
    protected int resourceId() {
        return R.layout.fragment_login_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mTelephoneNumber = findViewById(R.id.telephone_number_et);
        mNextStep = findViewById(R.id.next_step_tv);

        TextView termsOfService = findViewById(R.id.terms_of_service);
        String msg = "登录即代表阅读并同意《途找服务条款》";
        SpannableString spannableString = new SpannableString(msg);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showFiveToast("hh");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ConstansUtil.Y3_COLOR);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, msg.indexOf("《"), msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsOfService.setMovementMethod(LinkMovementMethod.getInstance());        //不设置点击事件没反应
        termsOfService.setText(spannableString);
        termsOfService.setHighlightColor(Color.TRANSPARENT);

        termsOfService.setOnClickListener(this);
        mNextStep.setOnClickListener(this);
        findViewById(R.id.wechat_login_iv).setOnClickListener(this);
        findViewById(R.id.wechat_login_tv).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        IntentObserable.registerObserver(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step_tv:
                if (TextUtils.isEmpty(getText(mTelephoneNumber))) {
                    showFiveToast("请输入新的手机号");
                } else if (getTextLength(mTelephoneNumber) < 11) {
                    showFiveToast("手机号格式不正确");
                } else if (!DateUtil.isPhoneNumble(getText(mTelephoneNumber))) {
                    showFiveToast("手机号不正确");
                } else {
                    phoneIsNewUser();
                }
                break;
            case R.id.wechat_login_iv:
            case R.id.wechat_login_tv:
                performWechatLogin();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IntentObserable.unregisterObserver(this);
    }

    /**
     * 1(新用户)  2(老用户有openId)  3（老用户没有openId）
     */
    private void phoneIsNewUser() {
        showLoadingDialog();
        mNextStep.setClickable(false);
        getOkGo(HttpConstants.phoneIsNewUser)
                .params("phoneNumber", getText(mTelephoneNumber))
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        dismmisLoadingDialog();
                        mNextStep.setClickable(true);
                        if (o.data.equals("1")) {
                            Bundle bundle = new Bundle();
                            bundle.putInt(ConstansUtil.STATUS, 3);
                            bundle.putString(ConstansUtil.TELEPHONE_NUMBER, getText(mTelephoneNumber));
                            IntentObserable.dispatch(ConstansUtil.SMS_LOGIN, ConstansUtil.INTENT_MESSAGE, bundle);
                        } else {
                            IntentObserable.dispatch(ConstansUtil.PASSWORD_LOGIN, ConstansUtil.TELEPHONE_NUMBER, getText(mTelephoneNumber));
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


    private void performWechatLogin() {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(getContext(), null);
        if (iwxapi.isWXAppInstalled()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "tuzhao_login";
            iwxapi.registerApp(ConstansUtil.WECHAT_APP_ID);
            iwxapi.sendReq(req);
        } else {
            Uri uri = Uri.parse("market://details?id=com.tencent.mm");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (getActivity()!=null&&intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    private void wechatLogin(String code) {
        showLoadingDialog("登录中...");
        final DatabaseImp databaseImp = new DatabaseImp(getContext());
        getOkGo(HttpConstants.wechatLogin)
                .params("code", code)
                .params("registrationId", databaseImp.getRegistrationId())
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> o, Call call, Response response) {
                        if (o.data.getId().equals("-1")) {
                            IntentObserable.dispatch(ConstansUtil.WECHAT_TELEPHONE_LOGIN, ConstansUtil.USER_INFO, o.data);
                        } else {
                            IntentObserable.dispatch(ConstansUtil.LOGIN_SUCCESS, ConstansUtil.INTENT_MESSAGE, o.data);
                        }
                        dismmisLoadingDialog();
                    }
                });
    }

    @Override
    public void onReceive(Intent intent) {
        if (Objects.equals(intent.getAction(), ConstansUtil.WECHAT_CODE)) {
            wechatLogin(intent.getStringExtra(ConstansUtil.FOR_REQEUST_RESULT));
        }
    }

}
