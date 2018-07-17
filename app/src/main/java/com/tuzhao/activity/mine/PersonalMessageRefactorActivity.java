package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.AuthTask;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseStatusActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.alipay.AuthResult;
import com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.publicwidget.others.CircleImageView;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserver;

import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

import static com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0.SDK_AUTH_FLAG;

/**
 * Created by juncoder on 2018/7/14.
 */
public class PersonalMessageRefactorActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

    private static final String UNBIND = "解绑";

    private static final String UNBOUND = "未绑定";

    private CircleImageView mCircleImageView;

    private TextView mNickname;

    private TextView mUserGender;

    private ImageView mUserGenderIv;

    private TextView mBirthday;

    private TextView mNumberOfPark;

    private TextView mUserLevel;

    private TextView mTelephoneNumber;

    private TextView mWechat;

    private TextView mAlipay;

    private TextView mSesame;

    private TextView mRealName;

    private Handler mHandler;

    private Thread mThread;

    @Override
    protected int resourceId() {
        return R.layout.activity_personal_message_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mCircleImageView = findViewById(R.id.user_protrait);
        mNickname = findViewById(R.id.nickname);
        mUserGender = findViewById(R.id.user_gender);
        mUserGenderIv = findViewById(R.id.user_gender_iv);
        mBirthday = findViewById(R.id.birthday);
        mNumberOfPark = findViewById(R.id.number_of_park);
        mUserLevel = findViewById(R.id.user_level);
        mTelephoneNumber = findViewById(R.id.telephone_number_tv);
        mWechat = findViewById(R.id.wechat_bingding_tv);
        mAlipay = findViewById(R.id.alipay_bingding_tv);
        mSesame = findViewById(R.id.sesame_certification_tv);
        mRealName = findViewById(R.id.real_name_certification_tv);

        mCircleImageView.setOnClickListener(this);
        findViewById(R.id.edit_personal_message).setOnClickListener(this);
        findViewById(R.id.telephone_number_cl).setOnClickListener(this);
        findViewById(R.id.modify_password_cl).setOnClickListener(this);
        findViewById(R.id.wechat_bingding_cl).setOnClickListener(this);
        findViewById(R.id.alipay_bingding_cl).setOnClickListener(this);
        findViewById(R.id.sesame_certification_cl).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        User_Info userInfo = com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo();
        ImageUtil.showPic(mCircleImageView, HttpConstants.ROOT_IMG_URL_USER + userInfo.getImg_url());
        String nickname;
        if (userInfo.getNickname().equals("-1")) {
            nickname = "昵称（未设置）";
        } else {
            nickname = com.tuzhao.publicmanager.UserManager.getInstance().getUserInfo().getNickname();
        }
        mNickname.setText(nickname);

        if (Objects.equals(userInfo.getGender(), "1")) {
            mUserGender.setText("男");
            ImageUtil.showPic(mUserGenderIv, R.drawable.ic_man);
        } else {
            mUserGender.setText("女");
            ImageUtil.showPic(mUserGenderIv, R.drawable.ic_woman);
        }
        mBirthday.setText(userInfo.getBirthday());
        if (userInfo.getNumberOfPark() != null && !userInfo.getNumberOfPark().equals("-1")) {
            int numberOfPark = Integer.parseInt(userInfo.getNumberOfPark());
            if (0 < numberOfPark && numberOfPark <= 20) {
                mUserLevel.setText("停车小白");
            } else if (numberOfPark <= 50) {
                mUserLevel.setText("泊车达人");
            } else if (numberOfPark <= 100) {
                mUserLevel.setText("资深途友");
            } else {
                mUserLevel.setText("神级泊客");
            }
            mNumberOfPark.setText("总停车次数" + numberOfPark + "次");
        }

        if (!userInfo.getUsername().equals("-1")) {
            StringBuilder telephone = new StringBuilder(userInfo.getUsername());
            if (telephone.length() > 6) {
                telephone.replace(3, 8, "*****");
            }
            mTelephoneNumber.setText(telephone.toString());
        }

        if (!Objects.equals(userInfo.getOpenId(), "-1")) {
            mWechat.setText(UNBIND);
        }

        if (!Objects.equals(userInfo.getAliNickName(), "-1")) {
            mAlipay.setText(UNBIND);
        }

        if (!Objects.equals(userInfo.getSesameFraction(), "-1")) {
            mSesame.setText("已认证");
        }

        if (userInfo.getRealName() != null && !userInfo.getRealName().equals("-1")) {
            mRealName.setText(userInfo.getRealName());
        }

    }

    @NonNull
    @Override
    protected String title() {
        return "个人信息";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_protrait:

                break;
            case R.id.edit_personal_message:

                break;
            case R.id.telephone_number_cl:

                break;
            case R.id.modify_password_cl:

                break;
            case R.id.wechat_bingding_cl:
                if (getText(mWechat).equals(UNBOUND)) {
                    wechatLogin();
                } else {
                    TipeDialog.Builder builder = new TipeDialog.Builder(PersonalMessageRefactorActivity.this);
                    builder.setTitle("解除绑定");
                    builder.setMessage("将与当前绑定的微信账号解除绑定，解除绑定后可重新绑定新的微信账号，是否解除绑定？");
                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.setPositiveButton("解除绑定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            requestUnbind(1);
                        }
                    });
                }
                break;
            case R.id.alipay_bingding_cl:
                if (getText(mAlipay).equals(UNBOUND)) {
                    initHandler();
                    reqeustAlipayLogin();
                } else {
                    TipeDialog.Builder builder = new TipeDialog.Builder(PersonalMessageRefactorActivity.this);
                    builder.setMessage("将与当前绑定的支付宝账号解除绑定，解除绑定后可重新绑定新的支付宝账号，是否解除绑定？");
                    builder.setTitle("解除绑定");
                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.setPositiveButton("解除绑定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            requestUnbind(2);
                        }
                    });
                }
                break;
            case R.id.sesame_certification_cl:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mThread != null && mThread.isAlive()) {
            mThread.interrupt();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void initHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == SDK_AUTH_FLAG) {
                        AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                        if (TextUtils.equals(authResult.getResultStatus(), "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                            showLoadingDialog("绑定中...");

                            if (UserManager.getInstance().getUserInfo().getAlinumber() != null) {
                                if (UserManager.getInstance().getUserInfo().getAlinumber().equals(authResult.getUser_id() + ",1")) {
                                    MyToast.showToast(PersonalMessageRefactorActivity.this, "账号和之前一样，未作修改", 5);
                                    if (mLoadingDialog != null) {
                                        mLoadingDialog.dismiss();
                                    }
                                } else {
                                    showLoadingDialog("提交中...");
                                    requestUploadUserAliNumber(authResult.getUser_id(), authResult.getAuthCode());
                                }
                            } else {
                                showLoadingDialog("提交中...");
                                requestUploadUserAliNumber(authResult.getUser_id(), authResult.getAuthCode());
                            }
                        } else {
                            // 其他状态值则为授权失败
                            MyToast.showToast(PersonalMessageRefactorActivity.this, "授权异常，绑定失败", 5);
                        }
                    }
                }
            };
        }
    }

    private void reqeustAlipayLogin() {
        //去绑定
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap("201701010102", true);
        final String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
        //String sign = OrderInfoUtil2_0.getSign(authInfoMap, "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC3dPPLfCrGnYcl4Z96xrC/5MaQ38AnDvqNi+QJwO26nAfru7xuho0U7nyTtx0gbfd0zWbGX0DV3FGab0k8LGIJnKhcC7tXBTgoWlVtdpJ3yxeK3YyPfX1J3O+W2/FddnJ1oZAUqCp81z+fWsoyu907h4rR+Vt06Y85YujlkguRmmiydWsKnU5gaFTaW6IVbL/dLmW/FLVzjtZ3D7rPwnJvRjTxQgTQUTKu4WsGmfjssZFxyNskkXCSvCH+ODVAbF8sAfFrk3d3gR0Yr1McOHte7YJoYzvgXQ/Khf3JbvvfXdzkfhacmwsbFIq5jNO4/6amof6sLiQsANU3n5NAKNHbAgMBAAECggEAPoN/v8sj2GI9d08WxQOHnxwZ/awBEk4sWqdcoWY4m+onWNC0OQVodcV+dedj6XUPaaHEb7xtn3Jf7DwXvIVzMstLI3Jr+A8zP6zoh1BsQJ3X+93a09pzIwRCMCnqaWq/Pg47xo43TCsc5vRNClSx4mMhjsNlwsUDpQ5rRi8p+gcK2tISt5LuYw6Kw36El/paaBz6idd7lZ5eEKqb2p2DQbftj0K5m4RbdayJ1JKpY+co2c2MnIu09isMj8/ONauesh6dg/qMBMcyeuaHCI8YfPj4KYGI5foXqcw8SwRaGErQtNKZlAp84nTp20uvdmH1m1KREF146VoG2eK+iV6d4QKBgQD2EeAZWX/DWcTOu7iR1wTNlBo1WrJ34dFh6p13Pa7clYvSUOZlsidTz2S37vzIrpIYutbrhAV651uWUiDaYAgodzPQDgvn6VOV3V1ss9tigaUgD9GaZ5DOspCojfRfcKPrdEZn09Fv+WumAZecLUEGKH8My8SlkwvPB7r65zFoZwKBgQC+3Dpfv/+3QHMJMQJwDIeO2MptHZvQ7gVPsYi554gfmmnrsRdV9LCCFo6iTBCUQ/xijm3N8lh+qHz9Q627MqGy8wNqkEnjSqUH6xxSLIAd/8sw7vLnfuhvc+VOXOYUMcbRBiqWRK7RItTRRjk7X7WAkGJbE6WvfaP/rUiwPGLybQKBgQDsqhSXTPUMtfILw5Co89yyvJbYafrsQkxXmIcKgFEF5u8rwJNXjBk3CmkcXsbRXNU247yBl+CNbKcx1Ju0bFhsUvmKSXg5/LdflCCew/1kqLxgMdauYp1rr2JiOuWmRXfipVpx0c/FmmZmq3FdzEiV260WaYUgmmTpIc48Ms/aUQKBgAeRpkL700FKLgW0Stt0s7+He2eeX/qGJfHGIZz1wKE4N3EgYcOH46QVDu0CxTmMBKtH6LTdIoNLXUGR+IbO+DiniIAmXrfD0w2gVkwv9Zi69yzmnP1vO/qHhxV3e6xbWP4bF39EFAa5MeVmuohPQFxr3WqtOcne2q0eCx7qFiZ1AoGBAIcctLQUoJ4r5dnbH9ax0fLAHmm8NAZjcao2yzDIi8X4/6593fx4gYqt5ppD1lo/AnfmDvDPEHm9v45XsK1Fa1rgEyA4eMKn6MJMjjQtmbvFINqHtIuk6C4w1VFa7iTUYcjgWVEiim+z4J3zkXgVecsploc8ZS9RYJTTc/Di5/Ab", true);
        // final String authInfo = info + "&" + sign;
        Runnable authRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(PersonalMessageRefactorActivity.this);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(info, true);
                Message msg = new Message();
                msg.what = SDK_AUTH_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        mThread = new Thread(authRunnable);
        mThread.start();
    }

    private void requestUploadUserAliNumber(final String aliuser_id, String authCode) {
        getOkGo(HttpConstants.uploadUserAliNumber)
                .params("pass_code", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" + UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .params("authCode", authCode)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> class_info, Call call, Response response) {
                        dismmisLoadingDialog();
                        MyToast.showToast(PersonalMessageRefactorActivity.this, "绑定成功", 5);
                        UserManager.getInstance().getUserInfo().setAlinumber(aliuser_id + ",1");
                        UserManager.getInstance().getUserInfo().setAliNickName(class_info.data);
                        mAlipay.setText(UNBIND);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismmisLoadingDialog();
                        if (!DensityUtil.isException(PersonalMessageRefactorActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为uploadUserAliNumber：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(PersonalMessageRefactorActivity.this, "绑定失败", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(PersonalMessageRefactorActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void requestUnbind(final int type) {
        showLoadingDialog("解除绑定");
        getOkGo(type == 1 ? HttpConstants.requestUnbindWechat : HttpConstants.requestUnbindAlipay)
                .params("passCode", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" +
                        UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .params("type", type)
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        if (type == 1) {
                            mWechat.setText(UNBOUND);
                        } else {
                            mAlipay.setText(UNBOUND);
                        }
                        showFiveToast("解除绑定成功");
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {

                        }
                    }
                });
    }

    private void wechatLogin() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "tuzhao_login";

        IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, null);
        iwxapi.registerApp(ConstansUtil.WECHAT_APP_ID);
        iwxapi.sendReq(req);
    }

    private void requestWechatBinding(String code) {
        showLoadingDialog("正在绑定");
        getOkGo(HttpConstants.requestBindWechat)
                .params("code", code)
                .params("passCode", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" + UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> o, Call call, Response response) {
                        UserManager.getInstance().getUserInfo().setOpenId(o.data);
                        mWechat.setText("解绑");
                        showFiveToast("绑定成功");
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast(e.getMessage());
                        }
                    }
                });
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.WECHAT_CODE:
                    requestWechatBinding(intent.getStringExtra(ConstansUtil.FOR_REQUEST_RESULT));
                    break;
            }
        }
    }

}
