package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.callback.StringCallback;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.SMSInfo;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.db.DatabaseImp;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.editviewwatch.LimitInputTextWatcher;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

import okhttp3.Call;
import okhttp3.Response;

import static com.tianzhili.www.myselfsdk.okgo.OkGo.getContext;

/**
 * Created by TZL12 on 2017/12/14.
 */

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText edittext_oldconfirm, edittext_newpassword, edittext_oldpassword;
    private TextView textview_oldgetconfirm;
    private LinearLayout linearlayout_tell, linearlayout_oldpassword, linearlayout_newpassword;
    private ImageView imageview_oldpassclean, imageview_newclean;
    private LoadingDialog mLoadingDialog;

    private static final int CODE_ING = 1;   //已发送，倒计时
    private static final int CODE_REPEAT = 2;  //重新发送
    private static final int SMSDDK_HANDLER = 3;  //短信回调
    private int TIME = 60;//倒计时60s
    private boolean isGetCode = false, isPassconfirm = false;
    private DatabaseImp databaseImp;
    private DateUtil dateUtil = new DateUtil();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_ING://已发送,开始倒计时
                    textview_oldgetconfirm.setText((--TIME) + "s");
                    textview_oldgetconfirm.setBackground(ContextCompat.getDrawable(ChangePasswordActivity.this, R.drawable.little_yuan_moregray_5dp));
                    textview_oldgetconfirm.setTextColor(ContextCompat.getColor(ChangePasswordActivity.this, R.color.g6));
                    break;
                case CODE_REPEAT://重新发送
                    textview_oldgetconfirm.setText("重新获取");
                    textview_oldgetconfirm.setBackground(ContextCompat.getDrawable(ChangePasswordActivity.this, R.drawable.little_yuan_yellow_5dp));
                    textview_oldgetconfirm.setTextColor(ContextCompat.getColor(ChangePasswordActivity.this, R.color.b1));
                    textview_oldgetconfirm.setClickable(true);
                    break;
            }
        }
    };

    Handler handlerChangge = new Handler() {
        public void handleMessage(Message msg) {
            linearlayout_tell.setVisibility(View.GONE);
            linearlayout_newpassword.setVisibility(View.VISIBLE);
        }
    };
    private String phone_token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword_layout);
        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {
        edittext_oldconfirm = (EditText) findViewById(R.id.id_activity_changepassword_layout_edittext_oldconfirm);
        textview_oldgetconfirm = (TextView) findViewById(R.id.id_activity_changepassword_layout_textview_oldgetconfirm);
        edittext_newpassword = (EditText) findViewById(R.id.id_activity_changepassword_layout_edittext_newpassword);
        edittext_oldpassword = (EditText) findViewById(R.id.id_activity_changepassword_layout_edittext_oldpassword);
        linearlayout_tell = (LinearLayout) findViewById(R.id.id_activity_changepassword_layout_linearlayout_tell);
        linearlayout_oldpassword = (LinearLayout) findViewById(R.id.id_activity_changepassword_layout_linearlayout_oldpassword);
        linearlayout_newpassword = (LinearLayout) findViewById(R.id.id_activity_changepassword_layout_linearlayout_newpassword);
        imageview_oldpassclean = (ImageView) findViewById(R.id.id_activity_changepassword_layout_imageview_oldpassclean);
        imageview_newclean = (ImageView) findViewById(R.id.id_activity_changepassword_layout_imageview_newclean);
    }

    private void initData() {
    }

    private void initEvent() {
        textview_oldgetconfirm.setOnClickListener(this);
        imageview_oldpassclean.setOnClickListener(this);
        imageview_newclean.setOnClickListener(this);
        findViewById(R.id.id_activity_changepassword_layout_textview_oldgonext).setOnClickListener(this);
        findViewById(R.id.id_activity_changepassword_layout_textview_gochange).setOnClickListener(this);
        findViewById(R.id.id_activity_changepassword_layout_textview_turntopass).setOnClickListener(this);
        findViewById(R.id.id_activity_changepassword_layout_textview_oldpassgonext).setOnClickListener(this);
        findViewById(R.id.id_activity_changepassword_layout_imageview_back).setOnClickListener(this);

        edittext_newpassword.addTextChangedListener(new LimitInputTextWatcher(edittext_newpassword, "[^a-zA-Z0-9,，.。?？:：;；￥$%@!~、！!“”（）()*·{}【】—+=]"));

        edittext_oldpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((count > 0 || start > 0) || s.toString().length() > 0) {
                    if (imageview_oldpassclean.getVisibility() == View.GONE) {
                        imageview_oldpassclean.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (imageview_oldpassclean.getVisibility() == View.VISIBLE) {
                        imageview_oldpassclean.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edittext_newpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((count > 0 || start > 0) || s.toString().length() > 0) {
                    if (imageview_newclean.getVisibility() == View.GONE) {
                        imageview_newclean.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (imageview_newclean.getVisibility() == View.VISIBLE) {
                        imageview_newclean.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_activity_changepassword_layout_textview_oldgetconfirm:
                //获取验证码,并进入60秒倒计时状态
                isGetCode = true;
                textview_oldgetconfirm.setText("发送中");
                requestSendSMS();
                textview_oldgetconfirm.setClickable(false);
                break;
            case R.id.id_activity_changepassword_layout_textview_oldgonext:
                if (isGetCode) {
                    if (edittext_oldconfirm.getText().length() > 0) {
                        initLoading("正在验证...");
                        requestCheckCode();
                    } else {
                        toast("输入收到的验证码哦");
                    }
                } else {
                    MyToast.showToast(ChangePasswordActivity.this, "要获取验证码才能继续哦", 5);
                }
                break;
            case R.id.id_activity_changepassword_layout_textview_turntopass:
                isPassconfirm = true;
                linearlayout_tell.setVisibility(View.GONE);
                linearlayout_oldpassword.setVisibility(View.VISIBLE);
                break;
            case R.id.id_activity_changepassword_layout_textview_oldpassgonext:
                if (edittext_oldpassword.getText().length() > 0) {
                    if (DensityUtil.MD5code(edittext_oldpassword.getText().toString()).equals(UserManager.getInstance().getUserInfo().getPassword())) {
                        isPassconfirm = false;
                        linearlayout_oldpassword.setVisibility(View.GONE);
                        linearlayout_newpassword.setVisibility(View.VISIBLE);
                    } else {
                        MyToast.showToast(ChangePasswordActivity.this, "密码不正确哦", 5);
                    }
                } else {
                    MyToast.showToast(ChangePasswordActivity.this, "要先输入密码哦", 5);
                }
                break;
            case R.id.id_activity_changepassword_layout_textview_gochange:
                if (edittext_newpassword.getText().length() >= 8 && edittext_newpassword.getText().length() <= 20) {
                    if (DensityUtil.MD5code(edittext_newpassword.getText().toString()).equals(UserManager.getInstance().getUserInfo().getPassword())){
                        edittext_newpassword.setText("");
                        MyToast.showToast(ChangePasswordActivity.this, "新旧密码不能一样哦", 5);
                    }else {
                        initLoading("正在修改...");
                        requestChangePassword(edittext_newpassword.getText().toString());
                    }
                } else {
                    MyToast.showToast(ChangePasswordActivity.this, "密码不符合规则哦", 5);
                }
                break;
            case R.id.id_activity_changepassword_layout_imageview_back:
                if (isPassconfirm) {
                    isPassconfirm = false;
                    linearlayout_oldpassword.setVisibility(View.GONE);
                    linearlayout_tell.setVisibility(View.VISIBLE);
                } else {
                    finish();
                }
                break;
            case R.id.id_activity_changepassword_layout_imageview_oldpassclean:
                edittext_oldpassword.setText("");
                break;
            case R.id.id_activity_changepassword_layout_imageview_newclean:
                edittext_newpassword.setText("");
                break;
        }
    }

    private void requestCheckCode() {

        OkGo.post(HttpConstants.checkCode)
                .tag(getContext())
                .headers("phoneToken",phone_token)
                .params("code",edittext_oldconfirm.getText().toString())
                .execute(new JsonCallback<Base_Class_Info<SMSInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<SMSInfo> responseData, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        linearlayout_tell.setVisibility(View.GONE);
                        linearlayout_newpassword.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (!DensityUtil.isException(getContext(),e)){
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code){
                                case 101:
                                    MyToast.showToast(getContext(),"验证码已过期，重新获取再试试哦",5);
                                    break;
                                case 102:
                                    MyToast.showToast(getContext(),"验证码不匹配，核对后再重试哦",5);
                                    break;
                                case 901:
                                    MyToast.showToast(getContext(),"服务器异常，再点击试试哦",5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void requestChangePassword(final String password) {
        OkGo.post(HttpConstants.changePassword)
                .tag(this)
                .addInterceptor(new TokenInterceptor())
                .headers("token",UserManager.getInstance().getUserInfo().getToken())
                .params("pass_code", DensityUtil.MD5code(UserManager.getInstance().getUserInfo().getSerect_code() + "*&*" + UserManager.getInstance().getUserInfo().getCreate_time() + "*&*" + UserManager.getInstance().getUserInfo().getId()))
                .params("newpassword", DensityUtil.MD5code(password))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        User_Info userInfo = UserManager.getInstance().getUserInfo();
                        userInfo.setPassword(DensityUtil.MD5code(password));
                        UserManager.getInstance().setUserInfo(userInfo);
                        //更新覆盖本地数据库的用户信息
                        DatabaseImp databaseImp = new DatabaseImp(ChangePasswordActivity.this);
                        databaseImp.insertUserToDatabase(userInfo);

                        MyToast.showToast(ChangePasswordActivity.this, "修改成功", 5);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (!DensityUtil.isException(ChangePasswordActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为changePassword：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(ChangePasswordActivity.this, "修改失败", 5);
                                    break;
                                case 102:
                                    MyToast.showToast(ChangePasswordActivity.this, "修改失败", 5);
                                    break;
                                case 103:
                                    MyToast.showToast(ChangePasswordActivity.this, "修改失败", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(ChangePasswordActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void requestSendSMS(){
        OkGo.post(HttpConstants.sendSms)
                .tag(getContext())
                .params("phone",UserManager.getInstance().getUserInfo().getUsername())
                .execute(new JsonCallback<Base_Class_Info<SMSInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<SMSInfo> smsInfo, Call call, Response response) {
                        phone_token = smsInfo.data.getPhone_token();
                        textview_oldgetconfirm.setClickable(false);
                        TIME = 60;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 60; i > 0; i--) {
                                    handler.sendEmptyMessage(CODE_ING);
                                    if (i <= 0) {
                                        break;
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                handler.sendEmptyMessage(CODE_REPEAT);
                            }
                        }).start();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        textview_oldgetconfirm.setText("发送失败");
                        textview_oldgetconfirm.setClickable(true);
                        if (!DensityUtil.isException(getContext(),e)){
                            MyToast.showToast(getContext(),"发送失败，请重试",5);
                        }
                    }
                });
    }

    /**
     * 弹出toast
     *
     * @param str
     */
    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.showToast(ChangePasswordActivity.this, str, 5);
            }
        });
    }

    //初始化加载框控件
    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(ChangePasswordActivity.this, what);
        mLoadingDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}