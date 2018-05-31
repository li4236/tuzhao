package com.tuzhao.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tuzhao.R;
import com.tuzhao.activity.base.BaseActivity;
import com.tuzhao.http.HttpConstants;
import com.tuzhao.info.ParkspaceCommentInfo;
import com.tuzhao.info.base_info.Base_Class_Info;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.JsonCallback;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by TZL12 on 2017/12/14.
 */

public class SuggestActivity extends BaseActivity {

    private EditText edittext_suggest;
    private TextView textview_count, textview_go;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_layout);
        initView();//初始化控件
        initData();//初始化数据
        initEvent();//初始化事件
        setStyle(true);
    }

    private void initView() {

        edittext_suggest = (EditText) findViewById(R.id.id_activity_suggest_layout_edittext_suggest);
        textview_count = (TextView) findViewById(R.id.id_activity_suggest_layout_textview_count);
        textview_go = (TextView) findViewById(R.id.id_activity_suggest_layout_textview_go);
        edittext_suggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((count > 0 || start > 0) || s.toString().length() > 0) {
                    textview_count.setText(edittext_suggest.getText().toString().length() + "/150");
                } else {
                    textview_count.setText("0/150");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initData() {
    }

    private void initEvent() {

        textview_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittext_suggest.getText().length() > 0) {
                    initLoading("提交中...");
                    OkGo.post(HttpConstants.uploadSuggest)
                            .tag(SuggestActivity.this)
                            .addInterceptor(new TokenInterceptor())
                            .headers("token",UserManager.getInstance().getUserInfo().getToken())
                            .params("suggest", edittext_suggest.getText().toString())
                            .execute(new JsonCallback<Base_Class_Info<ParkspaceCommentInfo>>() {
                                @Override
                                public void onSuccess(Base_Class_Info<ParkspaceCommentInfo> parkspaceCommentInfoBase_class_info, Call call, Response response) {
                                    if (mLoadingDialog.isShowing()) {
                                        mLoadingDialog.dismiss();
                                    }
                                    MyToast.showToast(SuggestActivity.this, "提交成功，谢谢您的意见", 5);
                                    finish();
                                }

                                @Override
                                public void onError(Call call, Response response, Exception e) {
                                    if (mLoadingDialog.isShowing()) {
                                        mLoadingDialog.dismiss();
                                    }
                                    if (!DensityUtil.isException(SuggestActivity.this, e)) {
                                        Log.d("TAG", "请求失败， 信息为uploadUserAliNumber：" + e.getMessage());
                                        int code = Integer.parseInt(e.getMessage());
                                        switch (code) {
                                            case 101:
                                                MyToast.showToast(SuggestActivity.this, "提交失败，稍后再试哦", 5);
                                                break;
                                            case 901:
                                                MyToast.showToast(SuggestActivity.this, "服务器正在维护中", 5);
                                                break;
                                        }
                                    }
                                }
                            });
                } else {
                    MyToast.showToast(SuggestActivity.this, "客观说点什么吧", 5);
                }
            }
        });

        findViewById(R.id.id_activity_suggest_imageview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    //初始化加载框控件
    private void initLoading(String what) {
        mLoadingDialog = new LoadingDialog(SuggestActivity.this, what);
        mLoadingDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
