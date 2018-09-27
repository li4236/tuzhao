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
import com.tuzhao.publicwidget.customView.CircleImageView;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;
import com.tuzhao.utils.ImageUtil;
import com.tuzhao.utils.IntentObserable;
import com.tuzhao.utils.IntentObserver;

import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

import static com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0.SDK_AUTH_FLAG;

/**
 * Created by juncoder on 2018/7/14.
 */
public class PersonalInformationActivity extends BaseStatusActivity implements View.OnClickListener, IntentObserver {

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

    //private TextView mRealName;

    private Handler mHandler;

    private Thread mThread;

    @Override
    protected int resourceId() {
        return R.layout.activity_personal_information_layout;
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
        //mRealName = findViewById(R.id.real_name_certification_tv);

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
        ImageUtil.showPic(mCircleImageView, HttpConstants.ROOT_IMG_URL_USER + userInfo.getImg_url(), R.mipmap.ic_usericon);
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

        if (userInfo.getBirthday().equals("0000-00-00")) {
            mBirthday.setText("出生日期（未设置）");
        } else {
            mBirthday.setText(userInfo.getBirthday());
        }
        if (userInfo.getNumberOfPark() != null && !userInfo.getNumberOfPark().equals("-1")) {
            int numberOfPark = Integer.parseInt(userInfo.getNumberOfPark());
            if (numberOfPark <= 20) {
                mUserLevel.setText("停车小白");
            } else if (numberOfPark <= 50) {
                mUserLevel.setText("泊车达人");
            } else if (numberOfPark <= 100) {
                mUserLevel.setText("资深途友");
            } else {
                mUserLevel.setText("神级泊客");
            }
            mNumberOfPark.setText("总停车次数");
            mNumberOfPark.append(userInfo.getNumberOfPark());
            mNumberOfPark.append("次");
        }

        if (!userInfo.getUsername().equals("-1")) {
            StringBuilder telephone = new StringBuilder(userInfo.getUsername());
            if (telephone.length() > 6) {
                telephone.replace(3, 8, "*****");
            }
            mTelephoneNumber.setText(telephone.toString());
        }

        if (userInfo.getOpenId() != null && !userInfo.getOpenId().equals("-1") && !userInfo.getOpenId().equals("")) {
            mWechat.setText(UNBIND);
        }

        if (!Objects.equals(userInfo.getAlinumber(), "-1")) {
            mAlipay.setText(UNBIND);
        }

        if (userInfo.isCertification()) {
            mSesame.setText("已认证");
        }

        IntentObserable.registerObserver(this);
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
                startActivity(PhotoActivity.class, ConstansUtil.PHOTO_IMAGE, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url());
                break;
            case R.id.edit_personal_message:
                startActivity(ChangePersonalInformationActivity.class);
                break;
            case R.id.telephone_number_cl:
                startActivity(SMSVerificationActivity.class, ConstansUtil.INTENT_MESSAGE, ConstansUtil.TELEPHONE_NUMBER);
                break;
            case R.id.modify_password_cl:
                startActivity(ChangePasswordMethodActivity.class);
                break;
            case R.id.wechat_bingding_cl:
                if (getText(mWechat).equals(UNBOUND)) {
                    wechatLogin();
                } else {
                    TipeDialog.Builder builder = new TipeDialog.Builder(PersonalInformationActivity.this);
                    builder.setTitle("解除绑定");
                    builder.setMessage("解除绑定后可重新绑定新的微信账号，是否解除绑定？");
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
                    builder.create().show();
                }
                break;
            case R.id.alipay_bingding_cl:
                if (getText(mAlipay).equals(UNBOUND)) {
                    initHandler();
                    reqeustAlipayLogin();
                } else {
                    TipeDialog.Builder builder = new TipeDialog.Builder(PersonalInformationActivity.this);
                    builder.setMessage("解除绑定后可重新绑定新的支付宝账号，是否解除绑定？");
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
                    builder.create().show();
                }
                break;
            case R.id.sesame_certification_cl:
                if (UserManager.getInstance().getUserInfo().isCertification()) {
                    showFiveToast("你已实名认证过了哦");
                } else {
                    startActivityForResult(CertifyZhimaActivity.class, ConstansUtil.REQUSET_CODE);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstansUtil.REQUSET_CODE && resultCode == RESULT_OK) {
            mSesame.setText("已认证");
            mBirthday.setText(UserManager.getInstance().getUserInfo().getBirthday());
            if (UserManager.getInstance().getUserInfo().getGender().equals("2")) {
                mUserGender.setText("女");
                ImageUtil.showPic(mUserGenderIv, R.drawable.ic_woman);
            }

            showFiveToast("实名认证已完成");
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
        IntentObserable.unregisterObserver(this);
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
                                    MyToast.showToast(PersonalInformationActivity.this, "账号和之前一样，未作修改", 5);
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
                            MyToast.showToast(PersonalInformationActivity.this, "授权异常，绑定失败", 5);
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
        Runnable authRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(PersonalInformationActivity.this);
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
                .params("pass_code", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*"
                        + UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .params("authCode", authCode)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> class_info, Call call, Response response) {
                        dismmisLoadingDialog();
                        MyToast.showToast(PersonalInformationActivity.this, "绑定成功", 5);
                        UserManager.getInstance().getUserInfo().setAlinumber(aliuser_id + ",1");
                        UserManager.getInstance().getUserInfo().setAliNickname(class_info.data);
                        mAlipay.setText(UNBIND);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismmisLoadingDialog();
                        if (!DensityUtil.isException(PersonalInformationActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为uploadUserAliNumber：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(PersonalInformationActivity.this, "绑定失败", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(PersonalInformationActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void requestUnbind(final int type) {
        showLoadingDialog("解除绑定");
        getOkGo(HttpConstants.requestUnbindThirdPartyAccount)
                .params("passCode", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" +
                        UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .params("type", type)
                .execute(new JsonCallback<Base_Class_Info<Void>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<Void> o, Call call, Response response) {
                        if (type == 1) {
                            mWechat.setText(UNBOUND);
                            UserManager.getInstance().getUserInfo().setOpenId("-1");
                            UserManager.getInstance().getUserInfo().setWechatNickname("");
                        } else {
                            UserManager.getInstance().getUserInfo().setAlinumber("-1");
                            UserManager.getInstance().getUserInfo().setAliNickname("");
                            mAlipay.setText(UNBOUND);
                        }
                        showFiveToast("解除绑定成功");
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            showFiveToast("解除绑定失败，请稍后重试");
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
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> o, Call call, Response response) {
                        UserManager.getInstance().getUserInfo().setOpenId(o.data.getOpenId());
                        UserManager.getInstance().getUserInfo().setWechatNickname(o.data.getWechatNickname());
                        mWechat.setText(UNBIND);
                        showFiveToast("绑定成功");
                        dismmisLoadingDialog();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!handleException(e)) {
                            switch (e.getMessage()) {
                                case "101":
                                    showFiveToast("绑定微信超时，请重试绑定");
                                    break;
                                case "103":
                                    mWechat.setText(UNBIND);
                                    showFiveToast("绑定成功");
                                    break;
                                case "104":
                                    showFiveToast("该微信已绑定别的账号");
                                    break;
                                case "102":
                                default:
                                    showFiveToast(ConstansUtil.SERVER_ERROR);
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    public void onReceive(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ConstansUtil.WECHAT_CODE:
                    requestWechatBinding(intent.getStringExtra(ConstansUtil.FOR_REQEUST_RESULT));
                    break;
                case ConstansUtil.CHANGE_PORTRAIT:
                    ImageUtil.showPic(mCircleImageView, HttpConstants.ROOT_IMG_URL_USER + UserManager.getInstance().getUserInfo().getImg_url());
                    break;
                case ConstansUtil.CHANGE_NICKNAME:
                    mNickname.setText(UserManager.getInstance().getUserInfo().getNickname());
                    break;
            }
        }
    }

}
