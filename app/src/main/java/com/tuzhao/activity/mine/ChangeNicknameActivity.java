package com.tuzhao.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.User_Info;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.editviewwatch.LimitInputTextWatcher;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import okhttp3.Call;
import okhttp3.Response;

import static com.tuzhao.publicwidget.dialog.LoginDialogFragment.LOGIN_ACTION;

/**
 * Created by TZL12 on 2017/12/1.
 */

public class ChangeNicknameActivity extends BaseActivity {
    private EditText edittext_newnickname;
    private CustomDialog mCustomDialog;
    private ImageView imageview_clean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changenickname_layout);
        initView();
        initData();
        initEvent();
        setStyle(true);
    }

    private void initView() {
        edittext_newnickname = (EditText) findViewById(R.id.id_activity_changenickname_layout_edittext_newnickname);
        edittext_newnickname.addTextChangedListener(new LimitInputTextWatcher(edittext_newnickname, "[^a-zA-Z0-9\\u4E00-\\u9FA5_,，.。?？:：;；￥$%@!~、！!“”（）()*·{}【】—+=]"));
        imageview_clean = (ImageView) findViewById(R.id.id_activity_changenickname_layout_imageview_clean);
    }

    private void initData() {
        if (UserManager.getInstance().hasLogined()){
            edittext_newnickname.setText(UserManager.getInstance().getUserInfo().getNickname().equals("-1") ? "" : UserManager.getInstance().getUserInfo().getNickname());
        }
    }

    private void initEvent() {
        findViewById(R.id.id_activity_changenickname_layout_imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.id_activity_changenickname_layout_textview_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext_newnickname.getText().length()<1){
                    MyToast.showToast(ChangeNicknameActivity.this,"需要填写昵称哦",5);
                } else if (UserManager.getInstance().getUserInfo().getNickname().equals(edittext_newnickname.getText().toString().trim())){
                    MyToast.showToast(ChangeNicknameActivity.this,"未作更改",5);
                }else {
                    initLoading();
                    OkGo.post(HttpConstants.changeUserNickname)
                            .tag(ChangeNicknameActivity.this)
                            .addInterceptor(new TokenInterceptor())
                            .headers("token",UserManager.getInstance().getUserInfo().getToken())
                            .params("nickname", edittext_newnickname.getText().toString().trim())
                            .execute(new JsonCallback<Base_Class_Info<User_Info>>() {
                                @Override
                                public void onSuccess(Base_Class_Info<User_Info> user_infoBase_class_info, Call call, Response response) {
                                    if (mCustomDialog.isShowing()) {
                                        mCustomDialog.dismiss();
                                    }
                                    User_Info userInfo = UserManager.getInstance().getUserInfo();
                                    userInfo.setNickname(edittext_newnickname.getText().toString().trim());
                                    UserManager.getInstance().setUserInfo(userInfo);
                                    sendLoginBroadcast();
                                    MyToast.showToast(ChangeNicknameActivity.this,"更换成功",5);
                                    finish();
                                }

                                @Override
                                public void onError(Call call, Response response, Exception e) {
                                    super.onError(call, response, e);
                                    if (mCustomDialog.isShowing()) {
                                        mCustomDialog.dismiss();
                                    }
                                    if (!DensityUtil.isException(ChangeNicknameActivity.this, e)){
                                        Log.d("TAG", "请求失败， 信息为changeUserImage：" + e.getMessage());
                                        int code = Integer.parseInt(e.getMessage());
                                        switch (code) {
                                            case 101:
                                                MyToast.showToast(ChangeNicknameActivity.this, "更换失败", 5);
                                                break;
                                            case 901:
                                                MyToast.showToast(ChangeNicknameActivity.this,"服务器正在维护中", 5);
                                                break;
                                        }
                                    }
                                }
                            });
                }
            }
        });

        imageview_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext_newnickname.setText("");
            }
        });

        edittext_newnickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0 || start>0) {
                    if (imageview_clean.getVisibility() == View.GONE){
                        imageview_clean.setVisibility(View.VISIBLE);
                    }
                }else {
                    if (imageview_clean.getVisibility() == View.VISIBLE){
                        imageview_clean.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 发送登录的局部广播
     */
    private void sendLoginBroadcast(){
        LocalBroadcastManager.getInstance(ChangeNicknameActivity.this).sendBroadcast(new Intent(LOGIN_ACTION));
    }

    private void initLoading() {
        mCustomDialog = new CustomDialog(this, "正在更换昵称...");
        mCustomDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
        if (mCustomDialog!=null){
            mCustomDialog.cancel();
        }
    }
}
