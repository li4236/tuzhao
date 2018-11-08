package com.tuzhao.activity.base;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.chenjing.XStatusBarHelper;
import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.request.BaseRequest;
import com.tuzhao.R;
import com.tuzhao.activity.LoginActivity;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.OnLoginListener;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.customView.ArrowView;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by juncoder on 2018/3/27.
 * <p>
 * 封装了加载对话框以及登录对话框，简单跳转页面
 */

public abstract class BaseStatusActivity extends BaseActivity {

    protected String TAG = this.getClass().getName();

    protected LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(resourceId() == 0 ? R.layout.activity_base_refresh_layout : resourceId());
        if (tintStatusBar()) {
            XStatusBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.w0), 0);
        }
        ConstraintLayout toolbar = findViewById(R.id.base_tb);
        ArrowView turnBackIv = toolbar.findViewById(R.id.toolbar_back);
        turnBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnBack();
                finish();
            }
        });

        TextView textView = toolbar.findViewById(R.id.toolbar_title);

        initView(savedInstanceState);
        textView.setText(title());
        initData();
    }

    /**
     * @return activity的contentView布局
     */
    @LayoutRes
    protected abstract int resourceId();

    /**
     * @return 是否修改状态栏为纯白
     */
    protected boolean tintStatusBar() {
        return true;
    }

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(TAG);
    }

    /**
     * 显示加载对话框，一打开界面默认显示
     */
    protected void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, null);
        } else if (!mLoadingDialog.getContent().equals("加载中...") && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog.show();
    }

    /**
     * 显示自定义加载提示的对话框
     */
    protected void showLoadingDialog(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, msg);
        } else if (!mLoadingDialog.getContent().equals(msg) && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = new LoadingDialog(this, msg);
        }
        mLoadingDialog.show();
    }

    /**
     * 显示自定义加载提示的对话框
     */
    protected void showNotInputLoadingDialog(String msg) {
        dismmisLoadingDialog();
        mLoadingDialog = new LoadingDialog(this, msg, 0);
        mLoadingDialog.show();
    }

    /**
     * 显示自定义加载提示的对话框
     */
    protected void showCantCancelLoadingDialog() {
        dismmisLoadingDialog();
        mLoadingDialog = new LoadingDialog(this, null, false);
        mLoadingDialog.show();
    }

    /**
     * 显示自定义加载提示的对话框
     */
    protected void showCantCancelLoadingDialog(String msg) {
        dismmisLoadingDialog();
        mLoadingDialog = new LoadingDialog(this, msg, false);
        mLoadingDialog.show();
    }

    /**
     * 关闭加载对话框
     */
    protected void dismmisLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    protected BaseRequest getOkGo(String url) {
        return OkGo.post(url)
                .tag(this.getClass().getName())
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken());
    }

    /**
     * @return ture（后台返回的响应码异常）
     */
    protected boolean handleException(Exception e) {
        dismmisLoadingDialog();
        if (!DensityUtil.isException(this, e)) {
            if (e instanceof IllegalStateException) {
                switch (e.getMessage()) {
                    case "801":
                        showFiveToast("数据存储异常，请稍后重试");
                        return true;
                    case "802":
                        showFiveToast("客户端异常，请稍后重试");
                        return true;
                    case "803":
                        showFiveToast("参数异常，请检查是否全都填写了哦");
                        return true;
                    case "804":
                        showFiveToast("获取数据异常，请稍后重试");
                        return true;
                    case "805":
                        userError();
                        return true;
                    default:
                        return false;
                }
            } else {
                showFiveToast(e.getMessage());
                return true;
            }
        } else {
            return true;
        }
    }

    protected void userError() {
        showFiveToast("账号异常，请重新登录");
        dismmisLoadingDialog();
        startActivity(LoginActivity.class, ConstansUtil.INTENT_MESSAGE, true);
    }

    protected void paramsError() {
        showFiveToast("客户端异常，请稍后重试");
        finish();
    }

    /**
     * 判断当前用户是否登录，如果登录了则会调用回调方法，没登录则弹出登录对话框
     */
    protected void judgeLogin(OnLoginListener listener) {
        if (com.tuzhao.publicmanager.UserManager.getInstance().hasLogined()) {
            listener.onLogin();
        } else {
            startActivity(LoginActivity.class, ConstansUtil.INTENT_MESSAGE, true);
        }
    }

    protected void startActivity(Class<?> tClass) {
        Intent intent = new Intent(this, tClass);
        startActivity(intent);
    }

    protected void startActivity(Class<?> tClass, Bundle bundle) {
        Intent intent = new Intent(this, tClass);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void startActivity(Class<?> tClass, String key, boolean value) {
        Intent intent = new Intent(this, tClass);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    protected void startActivity(Class<?> tClass, String key, String value) {
        Intent intent = new Intent(this, tClass);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    protected void startActivity(Class<?> tClass, String... keyWithValue) {
        Intent intent = new Intent(this, tClass);
        for (int i = 0; i < keyWithValue.length; i += 2) {
            intent.putExtra(keyWithValue[i], keyWithValue[i + 1]);
        }
        startActivity(intent);
    }

    protected void startActivity(Class<?> tClass, String key, Parcelable data) {
        Intent intent = new Intent(this, tClass);
        intent.putExtra(key, data);
        startActivity(intent);
    }

    protected void startActivity(Class<?> tClass, String parcelableKey, Parcelable parcelable, String stringKey, String string) {
        Intent intent = new Intent(this, tClass);
        intent.putExtra(parcelableKey, parcelable);
        intent.putExtra(stringKey, string);
        startActivity(intent);
    }

    protected void startActivityWithList(Class<?> tClass, String key, ArrayList<? extends Parcelable> data) {
        Intent intent = new Intent(this, tClass);
        intent.putParcelableArrayListExtra(key, data);
        startActivity(intent);
    }

    protected void startActivity(Class<?> tClass, String key, Bundle bundle) {
        Intent intent = new Intent(this, tClass);
        intent.putExtra(key, bundle);
        startActivity(intent);
    }

    protected void startActivityForResult(Class<?> tClass, int requestCode) {
        Intent intent = new Intent(this, tClass);
        intent.putExtra(ConstansUtil.FOR_REQEUST_RESULT, true);
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResult(Class<?> tClass, int requestCode, String key, String value) {
        Intent intent = new Intent(this, tClass);
        intent.putExtra(key, value);
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResult(Class<?> tClass, int requestCode, String key, Parcelable value) {
        Intent intent = new Intent(this, tClass);
        intent.putExtra(key, value);
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResult(Class<?> tClass, int requestCode, String key, ArrayList<? extends Parcelable> data) {
        Intent intent = new Intent(this, tClass);
        intent.putParcelableArrayListExtra(key, data);
        startActivityForResult(intent, requestCode);
    }

    protected void showSecondToast(String msg) {
        MyToast.showToast(getApplicationContext(), msg, 2);
    }

    /**
     * 显示位置在屏幕1/5的Toast
     *
     * @param msg 显示的消息
     */
    protected void showFiveToast(String msg) {
        //如果用Application的Context的话会被软键盘挡住
        MyToast.showToast(this, msg, 5);
    }

    /**
     * 显示对话框
     *
     * @param message         对话框里面的文字
     * @param onClickListener 点击确定按钮后的回调
     */
    protected void showDialog(String message, Dialog.OnClickListener onClickListener) {
        new TipeDialog.Builder(this)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", onClickListener)
                .create()
                .show();
    }

    /**
     * 显示对话框
     *
     * @param title           对话框的标题
     * @param message         对话框里面的文字
     * @param onClickListener 点击确定按钮后的回调
     */
    protected void showDialog(String title, String message, Dialog.OnClickListener onClickListener) {
        new TipeDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", onClickListener)
                .create()
                .show();
    }

    /**
     * 显示对话框
     *
     * @param title           对话框的标题
     * @param message         对话框里面的文字
     * @param onClickListener 点击确定按钮后的回调
     */
    protected void showDialog(String title, String message, String positiveButtonText, Dialog.OnClickListener onClickListener) {
        new TipeDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, onClickListener)
                .create()
                .show();
    }

    /**
     * @return textView里面的字符串
     */
    protected String getText(TextView textView) {
        return textView.getText().toString();
    }

    /**
     * @return TextView里面的文字长度（不包括前后空格)
     */
    protected int getTextLength(TextView textView) {
        return getText(textView).trim().length();
    }

    protected boolean isEmpty(TextView textView) {
        return TextUtils.isEmpty(textView.getText());
    }

    /**
     * 如果TextView里面的文字不是string才设置
     */
    protected void setNewText(TextView textView, String string) {
        if (!textView.getText().toString().equals(string)) {
            textView.setText(string);
        }
    }

    /**
     * 设置光标位置为文字的最后面
     */
    protected void setSelection(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    protected boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    protected void showView(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    protected void hideView(View view) {
        if (view.getVisibility() != View.INVISIBLE) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    protected void goneView(View view) {
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * @return dp对应的px
     */
    protected int dpToPx(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected int spToPx(float sp) {
        return (int) (getResources().getDisplayMetrics().scaledDensity * sp);
    }

}
