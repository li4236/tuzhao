package com.tuzhao.publicwidget.dialog;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.CollectionInfo;
import com.tuzhao.info.SMSInfo;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.info.base_info.Base_Class_List_Info;
import com.tuzhao.publicmanager.CollectionManager;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.db.DatabaseImp;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DateUtil;
import com.tuzhao.utils.DensityUtil;
import com.umeng.analytics.MobclickAgent;

import okhttp3.Call;
import okhttp3.Response;

import static android.view.View.GONE;

/**
 * Created by TZL12 on 2017/7/12.
 */

public class LoginDialogFragment extends DialogFragment {

    /**
     * UI
     */
    private View mContentView;
    private ImageView imageview_close,imageview_lookpassword,imageview_del1,imageview_del2,imageview_del3;
    private TextView textview_state,textview_getconfirm_code,textview_turn_pass_login,textview_turn_phone_login,textview_forgetpassword,textview_login;
    private LinearLayout linearlayout_phone_login,linearlayout_pass_login,linearlayout_set_password;
    private EditText edittext_phonenumble,edittext_confirm_code,edittext_username,edittext_password,edittext_set_password;

    /**
     * 页面相关
     */
    private static final int CODE_ING = 1;   //已发送，倒计时
    private static final int CODE_REPEAT = 2;  //重新发送
    public static final String LOGIN_ACTION = "LOGIN_ACTION";//登录成功的动作
    public static final String LOGOUT_ACTION = "LOGOUT_ACTION";//退出登录的动作
    private int TIME = 60;//倒计时60s
    private String phone_token = null;
    private boolean isGetCode = false;
    private DatabaseImp databaseImp;

    private int isWhatPage = 1;
    private boolean isForgetPassword = false,isrealForget = false;

    private CustomDialog mCustomDialog;

    private DateUtil dateUtil = new DateUtil();

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_ING://已发送,开始倒计时
                    textview_getconfirm_code.setText("重新发送(" + (--TIME) + "s)");
                    if (isAdded()){//判断fragment是否已经加入到activity中
                        textview_getconfirm_code.setTextColor(ContextCompat.getColor(getContext(),R.color.gray));
                    }

                    break;
                case CODE_REPEAT://重新发送
                    textview_getconfirm_code.setText("重新获取验证码");
                    if (isAdded()) {//判断fragment是否已经加入到activity中
                        textview_getconfirm_code.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.line_little_yuan_yellow_5dp));
                    }
                    textview_getconfirm_code.setClickable(true);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//设置没有title
        getDialog().setCanceledOnTouchOutside(false);//设置阴影部分点击不可消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mContentView = inflater.inflate(R.layout.dialog_login_layout, container);

        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
        return mContentView;
    }

    private void initView() {
        imageview_close = (ImageView) mContentView.findViewById(R.id.id_dialog_login_layout_imageview_close);
        imageview_lookpassword = (ImageView) mContentView.findViewById(R.id.id_dialog_login_layout_imageview_lookpassword);
        textview_state = (TextView) mContentView.findViewById(R.id.id_dialog_login_layout_textview_state);
        textview_getconfirm_code = (TextView) mContentView.findViewById(R.id.id_dialog_login_layout_textview_getconfirm_code);
        textview_turn_pass_login = (TextView) mContentView.findViewById(R.id.id_dialog_login_layout_textview_turn_pass_login);
        textview_turn_phone_login = (TextView) mContentView.findViewById(R.id.id_dialog_login_layout_textview_turn_phone_login);
        textview_forgetpassword = (TextView) mContentView.findViewById(R.id.id_dialog_login_layout_textview_forgetpassword);
        textview_login = (TextView) mContentView.findViewById(R.id.id_dialog_login_layout_textview_login);
        linearlayout_phone_login = (LinearLayout) mContentView.findViewById(R.id.id_dialog_login_layout_linearlayout_phone_login);
        linearlayout_pass_login = (LinearLayout) mContentView.findViewById(R.id.id_dialog_login_layout_linearlayout_pass_login);
        linearlayout_set_password = (LinearLayout) mContentView.findViewById(R.id.id_dialog_login_layout_linearlayout_set_password);
        edittext_phonenumble = (EditText) mContentView.findViewById(R.id.id_dialog_login_layout_edittext_phonenumble);
        edittext_confirm_code = (EditText) mContentView.findViewById(R.id.id_dialog_login_layout_edittext_confirm_code);
        edittext_username = (EditText) mContentView.findViewById(R.id.id_dialog_login_layout_edittext_username);
        edittext_password = (EditText) mContentView.findViewById(R.id.id_dialog_login_layout_edittext_password);
        edittext_set_password = (EditText) mContentView.findViewById(R.id.id_dialog_login_layout_edittext_set_password);
        imageview_del1 = (ImageView) mContentView.findViewById(R.id.id_dialog_login_layout_imageview_del1);
        imageview_del2 = (ImageView) mContentView.findViewById(R.id.id_dialog_login_layout_imageview_del2);
        imageview_del3 = (ImageView) mContentView.findViewById(R.id.id_dialog_login_layout_imageview_del3);
    }
    private void initData() {

        databaseImp = new DatabaseImp(getContext());
        User_Info user_info = databaseImp.getUserFormDatabase();
        if (user_info != null){
            //本地数据库有之前登录过的用户信息，则自动将用户名填入文本框中
            if (user_info.getUsername()!=null){
                edittext_phonenumble.setText(user_info.getUsername());
                edittext_username.setText(user_info.getUsername());
                imageview_del2.setVisibility(View.VISIBLE);
            }
        }
    }
    private void initEvent() {

        imageview_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        textview_getconfirm_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext_phonenumble.getText().length()>0)
                {
                    if (dateUtil.isPhoneNumble(edittext_phonenumble.getText().toString())){
                        //获取验证码,并进入60秒倒计时状态
                        isGetCode = true;
                        textview_getconfirm_code.setText("发送中");
                        requestSendSMS(edittext_phonenumble.getText().toString());
                    }else {
                        MyToast.showToast(getContext(),"要输入正确的手机号码哦",5);
                    }
                }else {
                    MyToast.showToast(getContext(),"手机号不能为空哦",5);
                }
            }
        });

        textview_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (isWhatPage){
                    case 1:
                        if (isGetCode){
                            if (edittext_phonenumble.getText().length()>0){
                                if (edittext_confirm_code.getText().length()>0){
                                    initLoading("正在登陆...");
                                    requestConfirmCodeLogin();
                                }else {
                                    MyToast.showToast(getContext(),"输入收到的验证码哦",5);
                                }
                            }else {
                                MyToast.showToast(getContext(),"手机号不能为空哦",5);
                            }
                        }else {
                            MyToast.showToast(getContext(),"要获取验证码才能登录哦",5);
                        }
                        break;
                    case 2:
                        if (edittext_username.getText().length()>0){
                            if (dateUtil.isPhoneNumble(edittext_username.getText().toString())){
                                if (edittext_password.getText().length()>0){
                                    initLoading("正在登陆...");
                                    requestPasswordLogin(edittext_username.getText().toString(),edittext_password.getText().toString().trim());
                                }else {
                                    MyToast.showToast(getContext(),"密码不能为空哦",5);
                                }
                            }else {
                                MyToast.showToast(getContext(),"要输入正确的手机号码哦",5);
                            }
                        }else {
                            MyToast.showToast(getContext(),"手机号不能为空哦",5);
                        }
                        break;
                    case 3:
                        if (edittext_set_password.getText().length()>=8 && edittext_set_password.getText().length()<=30){
                            if (isForgetPassword){
                                initLoading("正在重置密码...");
                            }else {
                                initLoading("正在设置密码...");
                            }
                            requestChangePassword(edittext_set_password.getText().toString());
                        }else {
                            MyToast.showToast(getContext(),"密码不符合规则哦",5);
                        }
                        break;
                }
            }
        });

        textview_turn_pass_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isForgetPassword = false;
                isWhatPage = 2;
                linearlayout_phone_login.setVisibility(GONE);
                linearlayout_pass_login.setVisibility(View.VISIBLE);
                textview_state.setText("账号密码登录");
                if (edittext_phonenumble.getText().length()>0){
                    edittext_username.setText(edittext_phonenumble.getText().toString());
                }
                if (dateUtil.isPhoneNumble(edittext_username.getText().toString()) && edittext_password.getText().length()>0){
                    textview_login.setEnabled(true);
                    textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.little_yuan_yellow_5dp));
                    textview_login.setTextColor(ContextCompat.getColor(getContext(),R.color.b1));
                }else {
                    textview_login.setEnabled(false);
                    textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yuan_little_gray_login_5dp));
                    textview_login.setTextColor(Color.WHITE);
                }
            }
        });

        textview_turn_phone_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isForgetPassword = false;
                isWhatPage = 1;
                linearlayout_phone_login.setVisibility(View.VISIBLE);
                linearlayout_pass_login.setVisibility(View.GONE);
                textview_state.setText("手机号登录");
                if (edittext_username.getText().length()>0){
                    edittext_phonenumble.setText(edittext_username.getText().toString());
                }
                if (dateUtil.isPhoneNumble(edittext_phonenumble.getText().toString()) && edittext_confirm_code.getText().length()>0&&isGetCode){
                    textview_login.setEnabled(true);
                    textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.little_yuan_yellow_5dp));
                    textview_login.setTextColor(ContextCompat.getColor(getContext(),R.color.b1));
                }else {
                    textview_login.setEnabled(false);
                    textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yuan_little_gray_login_5dp));
                    textview_login.setTextColor(Color.WHITE);
                }
            }
        });

        textview_forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isForgetPassword = true;
                isWhatPage = 1;
                linearlayout_phone_login.setVisibility(View.VISIBLE);
                linearlayout_pass_login.setVisibility(View.GONE);
                textview_state.setText("手机号登录");
                if (edittext_username.getText().length()>0){
                    edittext_phonenumble.setText(edittext_username.getText().toString());
                }
                if (dateUtil.isPhoneNumble(edittext_phonenumble.getText().toString()) && edittext_confirm_code.getText().length()>0){
                    textview_login.setEnabled(true);
                    textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.little_yuan_yellow_5dp));
                    textview_login.setTextColor(ContextCompat.getColor(getContext(),R.color.b1));
                }else {
                    textview_login.setEnabled(false);
                    textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yuan_little_gray_login_5dp));
                    textview_login.setTextColor(Color.WHITE);
                }
            }
        });

        edittext_phonenumble.addTextChangedListener(new isEmptyTextWatch(edittext_phonenumble));
        edittext_confirm_code.addTextChangedListener(new isEmptyTextWatch(edittext_confirm_code));
        edittext_username.addTextChangedListener(new isEmptyTextWatch(edittext_username));
        edittext_password.addTextChangedListener(new isEmptyTextWatch(edittext_password));
        edittext_set_password.addTextChangedListener(new isEmptyTextWatch(edittext_set_password));

        imageview_del1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext_phonenumble.setText("");
            }
        });
        imageview_del2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext_username.setText("");
            }
        });
        imageview_del3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext_password.setText("");
            }
        });

        imageview_lookpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext_set_password.getInputType() == (InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                    edittext_set_password.setInputType(InputType.TYPE_CLASS_TEXT);
                    imageview_lookpassword.setImageResource(R.mipmap.ic_notsee);
                }else {
                    edittext_set_password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imageview_lookpassword.setImageResource(R.mipmap.ic_see);
                }
            }
        });
    }

    //初始化加载框控件
    private void initLoading(String what) {
        mCustomDialog = new CustomDialog(getContext(), what);
        mCustomDialog.show();
    }

    private void requestConfirmCodeLogin(){
        OkGo.post(HttpConstants.checkCodeLogin)
                .tag(getContext())
                .headers("phoneToken",phone_token)
                .params("code",edittext_confirm_code.getText().toString())
                .params("isforget",isForgetPassword ? "1":"2")
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> responseData, Call call, Response response) {
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        User_Info userInfo = responseData.data;
                        userInfo.setAutologin("1");
                        UserManager.getInstance().setUserInfo(userInfo);
                        //更新覆盖本地数据库的用户信息
                        DatabaseImp databaseImp = new DatabaseImp(getContext());
                        databaseImp.insertUserToDatabase(userInfo);

                        //登录成功之后请求用户的收藏记录
                        OkGo.post(HttpConstants.getCollectionDatas)
                                .tag(this)
                                .headers("token",UserManager.getInstance().getUserInfo().getToken())
                                .execute(new JsonCallback<Base_Class_List_Info<CollectionInfo>>() {
                                    @Override
                                    public void onSuccess(Base_Class_List_Info<CollectionInfo> collection_infoBase_class_list_info, Call call, Response response) {
                                        CollectionManager.getInstance().setCollection_datas(collection_infoBase_class_list_info.data);
                                    }
                                });

                        //发送登录成功的局部广播
                        sendLoginBroadcast();
                        MobclickAgent.onProfileSignIn(edittext_username.getText().toString());//友盟在用户登录操作统计
                        MyToast.showToast(getContext(),"登录成功",5);
                        dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
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
                                case 103:
                                    //用户第一次登录，需要填写登录密码
                                    linearlayout_phone_login.setVisibility(GONE);
                                    linearlayout_set_password.setVisibility(View.VISIBLE);
                                    isWhatPage = 3;
                                    textview_state.setText("设置登录密码");
                                    textview_login.setText("确定");
                                    textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yuan_little_gray_login_5dp));
                                    textview_login.setTextColor(Color.WHITE);
                                    textview_login.setEnabled(false);
                                    edittext_set_password.setText("");
                                    break;
                                case 104:
                                    //用户不是第一次登录，需要重置密码
                                    isrealForget = true;
                                    linearlayout_phone_login.setVisibility(GONE);
                                    linearlayout_set_password.setVisibility(View.VISIBLE);
                                    isWhatPage = 3;
                                    textview_state.setText("设置登录密码");
                                    textview_login.setText("确定");
                                    textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yuan_little_gray_login_5dp));
                                    textview_login.setTextColor(Color.WHITE);
                                    textview_login.setEnabled(false);
                                    edittext_set_password.setText("");
                                    break;
                                case 901:
                                    MyToast.showToast(getContext(),"服务器异常，登录失败",5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void requestPasswordLogin(final String username, final String password){

        OkGo.post(HttpConstants.requestLogin)//请求数据的接口地址
                .tag(HttpConstants.requestLogin)//
                .params("username", username)
                .params("password", DensityUtil.MD5code(password))//通过MD5加密
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> responseData, Call call, Response response) {
                        //请求成功,储存用户登录信息
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        User_Info userInfo = responseData.data;
                        userInfo.setAutologin("1");
                        UserManager.getInstance().setUserInfo(userInfo);
                        //更新覆盖本地数据库的用户信息
                        DatabaseImp databaseImp = new DatabaseImp(getContext());
                        databaseImp.insertUserToDatabase(userInfo);

                        //登录成功之后请求用户的收藏记录
                        OkGo.post(HttpConstants.getCollectionDatas)
                                .tag(this)
                                .headers("token",UserManager.getInstance().getUserInfo().getToken())
                                .execute(new JsonCallback<Base_Class_List_Info<CollectionInfo>>() {
                                    @Override
                                    public void onSuccess(Base_Class_List_Info<CollectionInfo> collection_infoBase_class_list_info, Call call, Response response) {
                                        CollectionManager.getInstance().setCollection_datas(collection_infoBase_class_list_info.data);
                                    }
                                });

                        //发送登录成功的局部广播
                        sendLoginBroadcast();
                        MobclickAgent.onProfileSignIn(username);//友盟在用户登录操作统计
                        MyToast.showToast(getContext(),"登录成功",5);
                        dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(getContext(),e)){
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code){
                                case 102:
                                    //此手机号未登录过，不能用密码登录
                                    MyToast.showToast(getContext(),"此手机号是新用户，需要手机验证码登录哦",5);
                                    isForgetPassword = false;
                                    isWhatPage = 1;
                                    linearlayout_pass_login.setVisibility(GONE);
                                    linearlayout_phone_login.setVisibility(View.VISIBLE);
                                    textview_state.setText("手机号登录");
                                    if (edittext_username.getText().length()>0){
                                        edittext_phonenumble.setText(edittext_username.getText().toString());
                                    }
                                    if (dateUtil.isPhoneNumble(edittext_phonenumble.getText().toString()) && edittext_confirm_code.getText().length()>0){
                                        textview_login.setEnabled(true);
                                        textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.little_yuan_yellow_5dp));
                                        textview_login.setTextColor(ContextCompat.getColor(getContext(),R.color.b1));
                                    }else {
                                        textview_login.setEnabled(false);
                                        textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yuan_little_gray_login_5dp));
                                        textview_login.setTextColor(Color.WHITE);
                                    }
                                    break;
                                case 101:
                                    MyToast.showToast(getContext(),"账号或密码不正确哦",5);
                                    break;
                                case 202:
                                    MyToast.showToast(getContext(),"服务器异常，登录失败",5);
                                    break;
                                case 900:
                                    MyToast.showToast(getContext(),"服务器异常，登录失败",5);
                                    break;
                                case 901:
                                    MyToast.showToast(getContext(),"服务器异常，登录失败",5);
                                    break;
                            }
                        }
                    }
                });
    }

    private void requestChangePassword(String password){

        OkGo.post(HttpConstants.setPasswordLogin)//请求数据的接口地址
                .tag(getContext())
                .headers("phoneToken", phone_token)
                .params("password", DensityUtil.MD5code(password))//通过MD5加密
                .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<User_Info> responseData, Call call, Response response) {
                        //请求成功,储存用户登录信息
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        User_Info userInfo = responseData.data;
                        userInfo.setAutologin("1");
                        UserManager.getInstance().setUserInfo(userInfo);
                        //更新覆盖本地数据库的用户信息
                        DatabaseImp databaseImp = new DatabaseImp(getContext());
                        databaseImp.insertUserToDatabase(userInfo);

                        //登录成功之后请求用户的收藏记录
                        OkGo.post(HttpConstants.getCollectionDatas)
                                .tag(this)
                                .headers("token",UserManager.getInstance().getUserInfo().getToken())
                                .execute(new JsonCallback<Base_Class_List_Info<CollectionInfo>>() {
                                    @Override
                                    public void onSuccess(Base_Class_List_Info<CollectionInfo> collection_infoBase_class_list_info, Call call, Response response) {
                                        CollectionManager.getInstance().setCollection_datas(collection_infoBase_class_list_info.data);
                                    }
                                });

                        //发送登录成功的局部广播
                        sendLoginBroadcast();
                        MyToast.showToast(getContext(),"登录成功",5);
                        dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (mCustomDialog.isShowing()) {
                            mCustomDialog.dismiss();
                        }
                        if (!DensityUtil.isException(getContext(),e)){
                            Log.d("TAG", "请求失败， 信息为：" + e.getMessage());
                            int code = Integer.parseInt(e.getMessage());
                            switch (code){
                                case 101:
                                    MyToast.showToast(getContext(),"异常，登录失败",5);
                                    break;
                                case 102:
                                    MyToast.showToast(getContext(),"异常，登录失败",5);
                                    break;
                                case 901:
                                    MyToast.showToast(getContext(),"服务器异常，密码设置失败",5);
                                    break;
                            }
                        }
                    }
                });
    }

    private class isEmptyTextWatch implements TextWatcher {

        private TextView view;

         isEmptyTextWatch(View view) {
            if (view instanceof TextView){
                this.view = (TextView) view;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (view.getId()){
                case R.id.id_dialog_login_layout_edittext_phonenumble :
                    if (dateUtil.isPhoneNumble(s.toString()) && edittext_confirm_code.getText().length()>0&&isGetCode){
                        textview_login.setEnabled(true);
                        textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.little_yuan_yellow_5dp));
                        textview_login.setTextColor(ContextCompat.getColor(getContext(),R.color.b1));
                    }else {
                        textview_login.setEnabled(false);
                        textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yuan_little_gray_login_5dp));
                        textview_login.setTextColor(Color.WHITE);
                    }
                    if ((count > 0 || start > 0) || s.toString().length() > 0) {
                        imageview_del1.setVisibility(View.VISIBLE);
                    } else {
                        imageview_del1.setVisibility(View.GONE);
                    }
                    break;
                case R.id.id_dialog_login_layout_edittext_confirm_code :
                    if (dateUtil.isPhoneNumble(edittext_phonenumble.getText().toString()) && edittext_confirm_code.getText().length()>0&&isGetCode){
                        textview_login.setEnabled(true);
                        textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.little_yuan_yellow_5dp));
                        textview_login.setTextColor(ContextCompat.getColor(getContext(),R.color.b1));
                    }else {
                        textview_login.setEnabled(false);
                        textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yuan_little_gray_login_5dp));
                        textview_login.setTextColor(Color.WHITE);
                    }
                    break;
                case R.id.id_dialog_login_layout_edittext_username :
                    if (dateUtil.isPhoneNumble(edittext_username.getText().toString()) && edittext_password.getText().length()>0){
                        textview_login.setEnabled(true);
                        textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.little_yuan_yellow_5dp));
                        textview_login.setTextColor(ContextCompat.getColor(getContext(),R.color.b1));
                    }else {
                        textview_login.setEnabled(false);
                        textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yuan_little_gray_login_5dp));
                        textview_login.setTextColor(Color.WHITE);
                    }
                    if ((count > 0 || start > 0) || s.toString().length() > 0) {
                        imageview_del2.setVisibility(View.VISIBLE);
                    } else {
                        imageview_del2.setVisibility(View.GONE);
                    }
                    break;
                case R.id.id_dialog_login_layout_edittext_password :
                    if (dateUtil.isPhoneNumble(edittext_username.getText().toString()) && edittext_password.getText().length()>0){
                        textview_login.setEnabled(true);
                        textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.little_yuan_yellow_5dp));
                        textview_login.setTextColor(ContextCompat.getColor(getContext(),R.color.b1));
                    }else {
                        textview_login.setEnabled(false);
                        textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yuan_little_gray_login_5dp));
                        textview_login.setTextColor(Color.WHITE);
                    }
                    if ((count > 0 || start > 0) || s.toString().length() > 0) {
                        imageview_del3.setVisibility(View.VISIBLE);
                    } else {
                        imageview_del3.setVisibility(View.GONE);
                    }
                    break;
                case R.id.id_dialog_login_layout_edittext_set_password :
                    if (dateUtil.isCorrectPassword(edittext_set_password.getText().toString())){
                        textview_login.setEnabled(true);
                        textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.little_yuan_yellow_5dp));
                        textview_login.setTextColor(ContextCompat.getColor(getContext(),R.color.b1));
                    }else {
                        textview_login.setEnabled(false);
                        textview_login.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yuan_little_gray_login_5dp));
                        textview_login.setTextColor(Color.WHITE);
                    }
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void requestSendSMS(String phone_numble){
        OkGo.post(HttpConstants.sendSms)
                .tag(getContext())
                .params("phone",phone_numble)
                .execute(new JsonCallback<Base_Class_Info<SMSInfo>>() {
                    @Override
                    public void onSuccess(Base_Class_Info<SMSInfo> smsInfo, Call call, Response response) {
                        phone_token = smsInfo.data.getPhone_token();
                        textview_getconfirm_code.setClickable(false);
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
                        textview_getconfirm_code.setText("发送失败");
                        textview_getconfirm_code.setClickable(true);
                        if (!DensityUtil.isException(getContext(),e)){
                            MyToast.showToast(getContext(),"发送失败，请重试",5);
                        }
                    }
                });
    }

    /**
     * 发送登录的局部广播
     */
    private void sendLoginBroadcast(){

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(LOGIN_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCustomDialog!=null){
            mCustomDialog.cancel();
        }
    }
}
