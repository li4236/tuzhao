package com.tuzhao.activity.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.AuthTask;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.alipay.AuthResult;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

import static com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0.SDK_AUTH_FLAG;
import static com.tuzhao.publicwidget.dialog.LoginDialogFragment.LOGIN_ACTION;

/**
 * Created by TZL12 on 2017/12/14.
 */

public class SetAccountAndSafeActivity extends BaseActivity {

    private LinearLayout linearlayout_changepass, linearlayout_tellnumber, linearlayout_alipay;
    private LoadingDialog mLoadingDialog;
    private TextView mUserName;
    private TextView mUserBindingStatus;

    /**
     * 登录的广播接收器
     */
    private LoginBroadcastReceiver loginBroadcastReceiver = new LoginBroadcastReceiver();

    private Handler mHandler;

    private Thread mThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setaccountandsafe_layout);
        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
        initHandler();
        setStyle(true);
    }

    private void initView() {
        linearlayout_changepass = findViewById(R.id.id_activity_setaccountandsafe_layout_linearlayout_changepass);
        linearlayout_tellnumber = findViewById(R.id.id_activity_setaccountandsafe_layout_linearlayout_tellnumber);
        linearlayout_alipay = findViewById(R.id.id_activity_setaccountandsafe_layout_linearlayout_alipay);
        mUserBindingStatus = findViewById(R.id.id_activity_setaccountandsafe_layout_textview_alipay);
    }

    private void initData() {
        if (UserManager.getInstance().hasLogined()) {
            registerLogin();//注册登录广播接收器
            String userName = UserManager.getInstance().getUserInfo().getUsername().substring(0, 3) + "*****" + UserManager.getInstance().getUserInfo().getUsername().substring(8, UserManager.getInstance().getUserInfo().getUsername().length());
            mUserName = findViewById(R.id.id_activity_setaccountandsafe_layout_textview_tellnumber);
            mUserName.setText(userName);
            if (Objects.equals(UserManager.getInstance().getUserInfo().getAliNickname(), "-1")) {
                mUserBindingStatus.setText("未绑定");
            } else {
                mUserBindingStatus.setText(UserManager.getInstance().getUserInfo().getAliNickname());
            }
        } else {
            finish();
        }
    }

    private void initEvent() {

        linearlayout_changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetAccountAndSafeActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        linearlayout_tellnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetAccountAndSafeActivity.this, ChangePhoneNumberActivity.class);
                startActivity(intent);
            }
        });

        linearlayout_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (UserManager.getInstance().getUserInfo().getAlinumber() != null) {
                    if (!UserManager.getInstance().getUserInfo().getAlinumber().equals("-1")) {
                        initDialog(false);
                    } else {
                        initDialog(true);
                    }
                } else {
                    initDialog(true);
                }
            }
        });

        findViewById(R.id.id_activity_setaccountandsafe_imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initDialog(boolean b) {
        TipeDialog.Builder builder = new TipeDialog.Builder(SetAccountAndSafeActivity.this);
        if (b) {
            builder.setMessage("绑定支付宝方便提现哦，是否绑定？");
            builder.setTitle("支付宝绑定");
            builder.setNegativeButton("不绑定",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.setPositiveButton("立即绑定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    getAliAuthMessage();
                }
            });
        } else {
            builder.setMessage("现已绑定过支付宝，是否更换支付宝？");
            builder.setTitle("更换支付宝");
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.setPositiveButton("立即更换", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    getAliAuthMessage();
                }
            });
        }
        builder.create().show();
    }

    private void initHandler() {
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == SDK_AUTH_FLAG) {
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    if (TextUtils.equals(authResult.getResultStatus(), "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        initLoading("绑定中...");

                        if (UserManager.getInstance().getUserInfo().getAlinumber() != null) {
                            if (UserManager.getInstance().getUserInfo().getAlinumber().equals(authResult.getUser_id() + ",1")) {
                                MyToast.showToast(SetAccountAndSafeActivity.this, "账号和之前一样，未作修改", 5);
                                if (mLoadingDialog != null) {
                                    mLoadingDialog.dismiss();
                                }
                            } else {
                                initLoading("提交中...");
                                requestUploadUserAliNumber(authResult.getUser_id(), authResult.getAuthCode());
                            }
                        } else {
                            initLoading("提交中...");
                            requestUploadUserAliNumber(authResult.getUser_id(), authResult.getAuthCode());
                        }
                    } else {
                        // 其他状态值则为授权失败
                        MyToast.showToast(SetAccountAndSafeActivity.this, "授权异常，绑定失败", 5);
                    }
                }
            }
        };
    }

    private void getAliAuthMessage() {
        initLoading("绑定中...");
        OkGo.post(HttpConstants.getAliAuthMessage)
                .headers("token", UserManager.getInstance().getToken())
                .tag(this.getClass().getName())
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> s, Call call, Response response) {
                        reqeustAlipayLogin(s.data);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (!DensityUtil.isException(SetAccountAndSafeActivity.this, e)) {
                            MyToast.showToast(SetAccountAndSafeActivity.this, "授权异常，绑定失败", 5);
                        }
                    }
                });
    }

    private void reqeustAlipayLogin(final String authInfo) {
        //去绑定
        Runnable authRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造AuthTask 对象
                AuthTask authTask = new AuthTask(SetAccountAndSafeActivity.this);
                // 调用授权接口，获取授权结果
                Map<String, String> result = authTask.authV2(authInfo, true);
                Log.e("TAG", "run: " + result);
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
        OkGo.post(HttpConstants.uploadUserAliNumber)
                .tag(SetAccountAndSafeActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken())
                .params("pass_code", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" + UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .params("authCode", authCode)
                .execute(new JsonCallback<Base_Class_Info<String>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<String> class_info, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        MyToast.showToast(SetAccountAndSafeActivity.this, "绑定成功", 5);
                        UserManager.getInstance().getUserInfo().setAlinumber(aliuser_id + ",1");
                        UserManager.getInstance().getUserInfo().setAliNickname(class_info.data);
                        mUserBindingStatus.setText(class_info.data);
                        /*Intent intent = new Intent(SetAccountAndSafeActivity.this, GetMoneyActivty.class);
                        startActivity(intent);            ??????? */
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (!DensityUtil.isException(SetAccountAndSafeActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为uploadUserAliNumber：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(SetAccountAndSafeActivity.this, "绑定失败", 5);
                                    break;
                                case 801:
                                    MyToast.showToast(SetAccountAndSafeActivity.this, "绑定成功", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(SetAccountAndSafeActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 自定义登录的局部广播接收器，用来处理登录广播
     */
    private class LoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (UserManager.getInstance().hasLogined()) {
                String userName = UserManager.getInstance().getUserInfo().getUsername().substring(0, 3) + "*****" + UserManager.getInstance().getUserInfo().getUsername().substring(8, UserManager.getInstance().getUserInfo().getUsername().length());
                mUserName.setText(userName);
            }
        }
    }

    //注册登录广播接收器的方法
    private void registerLogin() {
        IntentFilter filter = new IntentFilter(LOGIN_ACTION);
        LocalBroadcastManager.getInstance(SetAccountAndSafeActivity.this).registerReceiver(loginBroadcastReceiver, filter);
    }

    //注销登录广播接收器
    private void unregisterLogin() {
        LocalBroadcastManager.getInstance(SetAccountAndSafeActivity.this).unregisterReceiver(loginBroadcastReceiver);
    }

    //初始化加载框控件
    private void initLoading(String what) {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = new LoadingDialog(SetAccountAndSafeActivity.this, what);
        mLoadingDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterLogin();//注销登录广播接收器
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
        if (mThread != null) {
            mThread.interrupt();
        }
        mHandler.removeCallbacksAndMessages(null);
    }
}
