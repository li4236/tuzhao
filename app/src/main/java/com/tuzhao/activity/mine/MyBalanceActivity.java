package com.tuzhao.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alipay.sdk.app.AuthTask;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.alipay.AuthResult;
import com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static com.tuzhao.publicwidget.alipay.OrderInfoUtil2_0.SDK_AUTH_FLAG;

/**
 * Created by TZL12 on 2017/12/2.
 */

public class MyBalanceActivity extends BaseActivity {

    private CustomDialog mCustomDialog;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SDK_AUTH_FLAG){
                AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                if (TextUtils.equals(authResult.getResultStatus(), "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                    initLoading("提交中...");
                    requestUploadUserAliNumber(authResult.getUser_id());
                } else {
                    // 其他状态值则为授权失败
                    MyToast.showToast(MyBalanceActivity.this,"授权异常，绑定失败",5);

                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybalance_layout);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
        setStyle(true);
    }

    private void initView() {
        ((TextView)findViewById(R.id.id_activity_mybalance_layout_textview_yue)).setText(UserManager.getInstance().getUserInfo().getBalance());
    }

    private void initData() {
        if (!UserManager.getInstance().hasLogined()){
            finish();
        }
    }

    private void initEvent() {
        findViewById(R.id.id_activity_mybalance_layout_textview_tixian).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.getInstance().getUserInfo().getAlinumber().equals("-1")){
                    TipeDialog.Builder builder = new TipeDialog.Builder(MyBalanceActivity.this);
                    builder.setMessage("绑定支付宝方便提现哦，是否绑定？");
                    builder.setTitle("支付宝绑定");
                    builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(MyBalanceActivity.this,GetMoneyActivty.class);
                            startActivity(intent);
                        }
                    });

                    builder.setNegativeButton("立即绑定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //去绑定
                                    Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap("201701010102", true);
                                    String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
                                    String sign = OrderInfoUtil2_0.getSign(authInfoMap, "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC3dPPLfCrGnYcl4Z96xrC/5MaQ38AnDvqNi+QJwO26nAfru7xuho0U7nyTtx0gbfd0zWbGX0DV3FGab0k8LGIJnKhcC7tXBTgoWlVtdpJ3yxeK3YyPfX1J3O+W2/FddnJ1oZAUqCp81z+fWsoyu907h4rR+Vt06Y85YujlkguRmmiydWsKnU5gaFTaW6IVbL/dLmW/FLVzjtZ3D7rPwnJvRjTxQgTQUTKu4WsGmfjssZFxyNskkXCSvCH+ODVAbF8sAfFrk3d3gR0Yr1McOHte7YJoYzvgXQ/Khf3JbvvfXdzkfhacmwsbFIq5jNO4/6amof6sLiQsANU3n5NAKNHbAgMBAAECggEAPoN/v8sj2GI9d08WxQOHnxwZ/awBEk4sWqdcoWY4m+onWNC0OQVodcV+dedj6XUPaaHEb7xtn3Jf7DwXvIVzMstLI3Jr+A8zP6zoh1BsQJ3X+93a09pzIwRCMCnqaWq/Pg47xo43TCsc5vRNClSx4mMhjsNlwsUDpQ5rRi8p+gcK2tISt5LuYw6Kw36El/paaBz6idd7lZ5eEKqb2p2DQbftj0K5m4RbdayJ1JKpY+co2c2MnIu09isMj8/ONauesh6dg/qMBMcyeuaHCI8YfPj4KYGI5foXqcw8SwRaGErQtNKZlAp84nTp20uvdmH1m1KREF146VoG2eK+iV6d4QKBgQD2EeAZWX/DWcTOu7iR1wTNlBo1WrJ34dFh6p13Pa7clYvSUOZlsidTz2S37vzIrpIYutbrhAV651uWUiDaYAgodzPQDgvn6VOV3V1ss9tigaUgD9GaZ5DOspCojfRfcKPrdEZn09Fv+WumAZecLUEGKH8My8SlkwvPB7r65zFoZwKBgQC+3Dpfv/+3QHMJMQJwDIeO2MptHZvQ7gVPsYi554gfmmnrsRdV9LCCFo6iTBCUQ/xijm3N8lh+qHz9Q627MqGy8wNqkEnjSqUH6xxSLIAd/8sw7vLnfuhvc+VOXOYUMcbRBiqWRK7RItTRRjk7X7WAkGJbE6WvfaP/rUiwPGLybQKBgQDsqhSXTPUMtfILw5Co89yyvJbYafrsQkxXmIcKgFEF5u8rwJNXjBk3CmkcXsbRXNU247yBl+CNbKcx1Ju0bFhsUvmKSXg5/LdflCCew/1kqLxgMdauYp1rr2JiOuWmRXfipVpx0c/FmmZmq3FdzEiV260WaYUgmmTpIc48Ms/aUQKBgAeRpkL700FKLgW0Stt0s7+He2eeX/qGJfHGIZz1wKE4N3EgYcOH46QVDu0CxTmMBKtH6LTdIoNLXUGR+IbO+DiniIAmXrfD0w2gVkwv9Zi69yzmnP1vO/qHhxV3e6xbWP4bF39EFAa5MeVmuohPQFxr3WqtOcne2q0eCx7qFiZ1AoGBAIcctLQUoJ4r5dnbH9ax0fLAHmm8NAZjcao2yzDIi8X4/6593fx4gYqt5ppD1lo/AnfmDvDPEHm9v45XsK1Fa1rgEyA4eMKn6MJMjjQtmbvFINqHtIuk6C4w1VFa7iTUYcjgWVEiim+z4J3zkXgVecsploc8ZS9RYJTTc/Di5/Ab", true);
                                    final String authInfo = info + "&" + sign;
                                    Log.e("请求数据",authInfo);
                                    Runnable authRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            // 构造AuthTask 对象
                                            AuthTask authTask = new AuthTask(MyBalanceActivity.this);
                                            // 调用授权接口，获取授权结果
                                            Map<String, String> result = authTask.authV2(authInfo, true);

                                            Message msg = new Message();
                                            msg.what = SDK_AUTH_FLAG;
                                            msg.obj = result;
                                            mHandler.sendMessage(msg);
                                        }
                                    };

                                    // 必须异步调用
                                    Thread authThread = new Thread(authRunnable);
                                    authThread.start();
                                }
                            });

                    builder.create().show();
                }else {
                    Intent intent = new Intent(MyBalanceActivity.this,GetMoneyActivty.class);
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.id_activity_mybalance_imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void requestUploadUserAliNumber(final String aliuser_id) {
        OkGo.post(HttpConstants.uploadUserAliNumber)
                .tag(MyBalanceActivity.this)
                .addInterceptor(new TokenInterceptor())
                .headers("token",UserManager.getInstance().getUserInfo().getToken())
                .params("pass_code",DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code()+"*&*"+UserManager.getInstance().getUserInfo().getCreate_time()+"*&*"+UserManager.getInstance().getUserInfo().getId()))
                .params("alinumber",aliuser_id+",1")
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> class_info, Call call, Response response) {
                        if (mCustomDialog.isShowing()){
                            mCustomDialog.dismiss();
                        }
                        MyToast.showToast(MyBalanceActivity.this,"绑定成功",5);
                        UserManager.getInstance().getUserInfo().setAlinumber(aliuser_id+",1");
                        Intent intent = new Intent(MyBalanceActivity.this,GetMoneyActivty.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (mCustomDialog.isShowing()){
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(MyBalanceActivity.this,e)){
                            Log.d("TAG", "请求失败， 信息为uploadUserAliNumber：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(MyBalanceActivity.this, "绑定失败", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(MyBalanceActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    //初始化加载框控件
    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(MyBalanceActivity.this, what);
        mCustomDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCustomDialog != null) {
            mCustomDialog.cancel();
        }
    }
}
