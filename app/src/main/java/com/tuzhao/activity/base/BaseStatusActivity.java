package com.tuzhao.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.request.BaseRequest;
import com.tuzhao.R;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.OnLoginListener;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.dialog.LoginDialogFragment;
import com.tuzhao.publicwidget.mytoast.MyToast;

/**
 * Created by juncoder on 2018/3/27.
 */

public abstract class BaseStatusActivity extends BaseActivity {

    protected String TAG = this.getClass().getName();

    private CustomDialog mCustomDialog;

    private LoginDialogFragment mLoginDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resourceId());
        Toolbar toolbar = findViewById(R.id.base_tb);
        final ImageView turnBackIv = toolbar.findViewById(R.id.toolbar_back);
        turnBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: ");
                turnBack();
                finish();
            }
        });

        TextView textView = toolbar.findViewById(R.id.toolbar_title);
        textView.setText(title());

        initView(savedInstanceState);
        initData();
    }

    @LayoutRes
    protected abstract int resourceId();

    /**
     * 在此方法初始化控件
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 在此方法初始化数据，初始化完记得调用dismmisCustomDialog()
     */
    protected void initData() {
        showLoadingDialog();
    }

    /**
     * @return 当前页面的标题
     */
    @NonNull
    protected abstract String title();

    /**
     * 当用户点击左上角返回上一个页面前如果需要做什么操作就重写该方法
     */
    protected void turnBack() {

    }

    @Override
    protected void onPause() {
        super.onPause();

        dismmisLoadingDialog();

        if (mLoginDialogFragment != null) {
            mLoginDialogFragment.dismiss();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this.getClass().getName());
    }

    /**
     * 显示加载对话框，一打开界面默认显示
     */
    protected void showLoadingDialog() {
        dismmisLoadingDialog();
        mCustomDialog = new CustomDialog(this, null);
        mCustomDialog.show();
    }

    /**
     * 显示自定义加载提示的对话框
     */
    protected void showLoadingDialog(String msg) {
        dismmisLoadingDialog();
        mCustomDialog = new CustomDialog(this, msg);
        mCustomDialog.show();
    }

    /**
     * 关闭加载对话框
     */
    protected void dismmisLoadingDialog() {
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
    }

    protected BaseRequest getOkGo(String url) {
        return OkGo.post(url)
                .tag(this.getClass().getName())
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken());
    }

    /**
     * 判断当前用户是否登录，如果登录了则会调用回调方法，没登录则弹出登录对话框
     */
    protected void judgeLogin(OnLoginListener listener) {
        if (com.tuzhao.publicmanager.UserManager.getInstance().hasLogined()) {
            listener.onLogin();
        } else {
            mLoginDialogFragment = new LoginDialogFragment();
            mLoginDialogFragment.show(getSupportFragmentManager(), this.getClass().getName());
        }
    }

    protected void startActivity(Class<?> tClass) {
        Intent intent = new Intent(this, tClass);
        startActivity(intent);
    }

    protected void startActivity(Class<?> tClass, String key, Bundle bundle) {
        Intent intent = new Intent(this, tClass);
        intent.putExtra(key, bundle);
        startActivity(intent);
    }

    protected void showSecondToast(String msg) {
        MyToast.showToast(this, msg, 2);
    }

    protected void showFiveToast(String msg) {
        MyToast.showToast(this, msg, 5);
    }

}
