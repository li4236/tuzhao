package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;

import okhttp3.Call;
import okhttp3.Response;

import static com.tianzhili.www.myselfsdk.okgo.OkGo.getContext;
import static com.tuzhao.publicwidget.dialog.LoginDialogFragment.LOGIN_ACTION;

/**
 * Created by TZL12 on 2017/12/1.
 */

public class ChangePhoneNumberActivity extends BaseActivity implements View.OnClickListener {

    private EditText edittext_oldnumber, edittext_oldconfirm, edittext_newnumber, edittext_newconfirm, edittext_oldpassword;
    private TextView textview_oldgetconfirm, textview_newgetconfirm;
    private LinearLayout linearlayout_oldnumble, linearlayout_oldpassword, linearlayout_newnumble;
    private ImageView imageview_oldclean,imageview_oldpassclean,imageview_newclean;
    private LoadingDialog mLoadingDialog;

    private static final int CODE_ING = 1;   //已发送，倒计时
    private static final int CODE_REPEAT = 2;  //重新发送
    private static final int SMSDDK_HANDLER = 3;  //短信回调
    private int TIME = 60;//倒计时60s
    private boolean isGetCode = false, isFisrstPage = true,isPassconfirm = false;
    private DatabaseImp databaseImp;
    private DateUtil dateUtil = new DateUtil();

    Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_ING://已发送,开始倒计时
                    textview_oldgetconfirm.setText((--TIME) + "s");
                    textview_oldgetconfirm.setBackground(ContextCompat.getDrawable(ChangePhoneNumberActivity.this, R.drawable.little_yuan_moregray_5dp));
                    textview_oldgetconfirm.setTextColor(ContextCompat.getColor(ChangePhoneNumberActivity.this, R.color.g6));
                    break;
                case CODE_REPEAT://重新发送
                    textview_oldgetconfirm.setText("重新获取");
                    textview_oldgetconfirm.setBackground(ContextCompat.getDrawable(ChangePhoneNumberActivity.this, R.drawable.little_yuan_yellow_5dp));
                    textview_oldgetconfirm.setTextColor(ContextCompat.getColor(ChangePhoneNumberActivity.this, R.color.b1));
                    textview_oldgetconfirm.setClickable(true);
                    break;
            }
        }
    };
    Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_ING://已发送,开始倒计时
                    textview_newgetconfirm.setText((--TIME) + "s");
                    textview_newgetconfirm.setBackground(ContextCompat.getDrawable(ChangePhoneNumberActivity.this, R.drawable.little_yuan_moregray_5dp));
                    textview_newgetconfirm.setTextColor(ContextCompat.getColor(ChangePhoneNumberActivity.this, R.color.g6));
                    break;
                case CODE_REPEAT://重新发送
                    textview_newgetconfirm.setText("重新获取");
                    textview_newgetconfirm.setBackground(ContextCompat.getDrawable(ChangePhoneNumberActivity.this, R.drawable.little_yuan_yellow_5dp));
                    textview_newgetconfirm.setTextColor(ContextCompat.getColor(ChangePhoneNumberActivity.this, R.color.b1));
                    textview_newgetconfirm.setClickable(true);
                    break;
            }
        }
    };
    Handler handlerChangge = new Handler() {
        public void handleMessage(Message msg) {
            isFisrstPage = false;
            linearlayout_oldnumble.setVisibility(View.GONE);
            linearlayout_newnumble.setVisibility(View.VISIBLE);
        }
    };
    private String phone_token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changephonenumber_layout);
        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {
        edittext_oldnumber = (EditText) findViewById(R.id.id_activity_changephonenumber_layout_edittext_oldnumber);
        edittext_oldconfirm = (EditText) findViewById(R.id.id_activity_changephonenumber_layout_edittext_oldconfirm);
        textview_oldgetconfirm = (TextView) findViewById(R.id.id_activity_changephonenumber_layout_textview_oldgetconfirm);
        edittext_newnumber = (EditText) findViewById(R.id.id_activity_changephonenumber_layout_edittext_newnumber);
        edittext_newconfirm = (EditText) findViewById(R.id.id_activity_changephonenumber_layout_edittext_newconfirm);
        edittext_oldpassword = (EditText) findViewById(R.id.id_activity_changephonenumber_layout_edittext_oldpassword);
        textview_newgetconfirm = (TextView) findViewById(R.id.id_activity_changephonenumber_layout_textview_newgetconfirm);
        linearlayout_oldnumble = (LinearLayout) findViewById(R.id.id_activity_changephonenumber_layout_linearlayout_oldnumble);
        linearlayout_oldpassword = (LinearLayout) findViewById(R.id.id_activity_changephonenumber_layout_linearlayout_oldpassword);
        linearlayout_newnumble = (LinearLayout) findViewById(R.id.id_activity_changephonenumber_layout_linearlayout_newnumble);
        imageview_oldclean = (ImageView) findViewById(R.id.id_activity_changephonenumber_layout_imageview_oldclean);
        imageview_oldpassclean = (ImageView) findViewById(R.id.id_activity_changephonenumber_layout_imageview_oldpassclean);
        imageview_newclean = (ImageView) findViewById(R.id.id_activity_changephonenumber_layout_imageview_newclean);
    }

    private void initData() {
    }

    private void initEvent() {
        textview_oldgetconfirm.setOnClickListener(this);
        textview_newgetconfirm.setOnClickListener(this);
        imageview_oldclean.setOnClickListener(this);
        imageview_oldpassclean.setOnClickListener(this);
        imageview_newclean.setOnClickListener(this);
        findViewById(R.id.id_activity_changenickname_layout_textview_oldgonext).setOnClickListener(this);
        findViewById(R.id.id_activity_changenickname_layout_textview_gochange).setOnClickListener(this);
        findViewById(R.id.id_activity_changenickname_layout_textview_turntopass).setOnClickListener(this);
        findViewById(R.id.id_activity_changenickname_layout_textview_oldpassgonext).setOnClickListener(this);
        findViewById(R.id.id_activity_changephonenumber_layout_imageview_back).setOnClickListener(this);

        edittext_oldnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((count > 0 || start>0)||s.toString().length()>0) {
                    if (imageview_oldclean.getVisibility() == View.GONE){
                        imageview_oldclean.setVisibility(View.VISIBLE);
                    }
                }else {
                    if (imageview_oldclean.getVisibility() == View.VISIBLE){
                        imageview_oldclean.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edittext_oldpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((count > 0 || start>0)||s.toString().length()>0) {
                    if (imageview_oldpassclean.getVisibility() == View.GONE){
                        imageview_oldpassclean.setVisibility(View.VISIBLE);
                    }
                }else {
                    if (imageview_oldpassclean.getVisibility() == View.VISIBLE){
                        imageview_oldpassclean.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edittext_newnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((count > 0 || start>0)||s.toString().length()>0) {
                    if (imageview_newclean.getVisibility() == View.GONE){
                        imageview_newclean.setVisibility(View.VISIBLE);
                    }
                }else {
                    if (imageview_newclean.getVisibility() == View.VISIBLE){
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
            case R.id.id_activity_changephonenumber_layout_textview_oldgetconfirm:
                if (edittext_oldnumber.getText().length() > 0) {
                    if (dateUtil.isPhoneNumble(edittext_oldnumber.getText().toString())) {
                        if (edittext_oldnumber.getText().toString().equals(UserManager.getInstance().getUserInfo().getUsername())) {
                            //获取验证码,并进入60秒倒计时状态
                            isGetCode = true;
                            textview_oldgetconfirm.setText("发送中");
                            textview_oldgetconfirm.setClickable(false);
                            requestSendSMS(UserManager.getInstance().getUserInfo().getUsername());
                        } else {
                            toast("原手机号不正确哦");
                        }
                    } else {
                        toast("要输入正确的手机号码哦");
                    }
                } else {
                    toast("手机号不能为空哦");
                }
                break;
            case R.id.id_activity_changenickname_layout_textview_oldgonext:
                if (isGetCode) {
                    if (edittext_oldnumber.getText().length() > 0) {
                        if (edittext_oldconfirm.getText().length() > 0) {
                            initLoading("正在验证...");
                            requestCheckCode();
                        } else {
                            toast("输入收到的验证码哦");
                        }
                    } else {
                        MyToast.showToast(ChangePhoneNumberActivity.this, "手机号不能为空哦", 5);
                    }
                } else {
                    MyToast.showToast(ChangePhoneNumberActivity.this, "要获取验证码才能继续哦", 5);
                }
                break;
            case R.id.id_activity_changenickname_layout_textview_turntopass:
                isFisrstPage = true;
                isPassconfirm = true;
                linearlayout_oldnumble.setVisibility(View.GONE);
                linearlayout_oldpassword.setVisibility(View.VISIBLE);
                break;
            case R.id.id_activity_changenickname_layout_textview_oldpassgonext:
                if (edittext_oldpassword.getText().length() > 0) {
                    if (DensityUtil.MD5code(edittext_oldpassword.getText().toString()).equals(UserManager.getInstance().getUserInfo().getPassword())) {
                        isFisrstPage = false;
                        isPassconfirm = false;
                        linearlayout_oldpassword.setVisibility(View.GONE);
                        linearlayout_newnumble.setVisibility(View.VISIBLE);
                    } else {
                        MyToast.showToast(ChangePhoneNumberActivity.this, "密码不正确哦", 5);
                    }
                } else {
                    MyToast.showToast(ChangePhoneNumberActivity.this, "要先输入密码哦", 5);
                }
                break;
            case R.id.id_activity_changephonenumber_layout_textview_newgetconfirm:
                if (edittext_newnumber.getText().length() > 0) {
                    if (dateUtil.isPhoneNumble(edittext_newnumber.getText().toString())) {
                        //获取验证码,并进入60秒倒计时状态
                        isGetCode = true;
                        textview_newgetconfirm.setText("发送中");
                        textview_newgetconfirm.setClickable(false);
                        requestSendSMS(edittext_newnumber.getText().toString());
                    } else {
                        MyToast.showToast(ChangePhoneNumberActivity.this, "要输入正确的手机号码哦", 5);
                    }
                } else {
                    MyToast.showToast(ChangePhoneNumberActivity.this, "手机号不能为空哦", 5);
                }
                break;
            case R.id.id_activity_changenickname_layout_textview_gochange:
                if (isGetCode) {
                    if (edittext_newnumber.getText().length() > 0) {
                        if (edittext_newconfirm.getText().length() > 0) {
                            initLoading("正在验证...");
                            requestCheckCode();
                        } else {
                            MyToast.showToast(ChangePhoneNumberActivity.this, "输入收到的验证码哦", 5);
                        }
                    } else {
                        MyToast.showToast(ChangePhoneNumberActivity.this, "手机号不能为空哦", 5);
                    }
                } else {
                    MyToast.showToast(ChangePhoneNumberActivity.this, "要获取验证码才能修改哦", 5);
                }
                break;
            case R.id.id_activity_changephonenumber_layout_imageview_back:
                if (isPassconfirm){
                    isFisrstPage = true;
                    isPassconfirm = false;
                    linearlayout_oldpassword.setVisibility(View.GONE);
                    linearlayout_oldnumble.setVisibility(View.VISIBLE);
                }else {
                    finish();
                }
                break;
            case R.id.id_activity_changephonenumber_layout_imageview_oldclean:
                edittext_oldnumber.setText("");
                break;
            case R.id.id_activity_changephonenumber_layout_imageview_oldpassclean:
                edittext_oldpassword.setText("");
                break;
            case R.id.id_activity_changephonenumber_layout_imageview_newclean:
                edittext_newnumber.setText("");
                break;
        }
    }

    private void requestChangeUserPhoneNumble(final String phone_numble) {
        OkGo.post(HttpConstants.changeUserPhoneNumble)
                .tag(this)
                .addInterceptor(new TokenInterceptor())
                .headers("token",UserManager.getInstance().getUserInfo().getToken())
                .params("phone_numble", phone_numble)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        User_Info userInfo = UserManager.getInstance().getUserInfo();
                        userInfo.setUsername(phone_numble);
                        UserManager.getInstance().setUserInfo(userInfo);
                        //更新覆盖本地数据库的用户信息
                        DatabaseImp databaseImp = new DatabaseImp(ChangePhoneNumberActivity.this);
                        databaseImp.insertUserToDatabase(userInfo);
                        sendLoginBroadcast();

                        MyToast.showToast(ChangePhoneNumberActivity.this, "更换成功", 5);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (!DensityUtil.isException(ChangePhoneNumberActivity.this, e)) {
                            Log.d("TAG", "请求失败， 信息为changeUserImage：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code) {
                                case 101:
                                    MyToast.showToast(ChangePhoneNumberActivity.this, "抱歉，此手机号已经注册过账户了哦", 5);
                                    break;
                                case 901:
                                    MyToast.showToast(ChangePhoneNumberActivity.this, "服务器正在维护中", 5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void requestSendSMS(String phonenumble){
        OkGo.post(HttpConstants.sendSms)
                .tag(getContext())
                .params("phone",phonenumble)
                .execute(new JsonCallback<Base_Class_Info<SMSInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<SMSInfo> smsInfo, Call call, Response response) {
                        phone_token = smsInfo.data.getPhone_token();
                        TIME = 60;
                        if (isFisrstPage){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 60; i > 0; i--) {
                                        handler1.sendEmptyMessage(CODE_ING);
                                        if (i <= 0) {
                                            break;
                                        }
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    handler1.sendEmptyMessage(CODE_REPEAT);
                                }
                            }).start();
                        }else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 60; i > 0; i--) {
                                        handler2.sendEmptyMessage(CODE_ING);
                                        if (i <= 0) {
                                            break;
                                        }
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    handler2.sendEmptyMessage(CODE_REPEAT);
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (isFisrstPage){
                            textview_oldgetconfirm.setText("发送失败");
                            textview_oldgetconfirm.setClickable(true);
                        }else {
                            textview_newgetconfirm.setText("发送失败");
                            textview_newgetconfirm.setClickable(true);
                        }

                        if (!DensityUtil.isException(getContext(),e)){
                            MyToast.showToast(getContext(),"发送失败，请重试",5);
                        }
                    }
                });
    }

    private void requestCheckCode() {

        OkGo.post(HttpConstants.checkCode)
                .tag(getContext())
                .headers("phoneToken",phone_token)
                .params("code",isFisrstPage?edittext_oldconfirm.getText().toString():edittext_newconfirm.getText().toString())
                .execute(new JsonCallback<Base_Class_Info<SMSInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<SMSInfo> responseData, Call call, Response response) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                        }
                        if (isFisrstPage) {
                            //跳转
                            handlerChangge.sendMessage(new Message());
                        } else {
                            //向服务器请求数据
                            initLoading("正在修改...");
                            requestChangeUserPhoneNumble(edittext_newnumber.getText().toString());
                        }
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


    /**
     * 弹出toast
     *
     * @param str
     */
    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.showToast(ChangePhoneNumberActivity.this, str, 5);
            }
        });
    }

    //初始化加载框控件
    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(ChangePhoneNumberActivity.this, what);
        mLoadingDialog.show();
    }

    /**
     * 发送登录的局部广播
     */
    private void sendLoginBroadcast() {

        LocalBroadcastManager.getInstance(ChangePhoneNumberActivity.this).sendBroadcast(new Intent(LOGIN_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
