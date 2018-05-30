package com.tuzhao.fragment.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.request.BaseRequest;
import com.tuzhao.R;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.CustomDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by juncoder on 2018/5/17.
 */

public abstract class BaseStatusFragment extends Fragment {

    protected String TAG = this.getClass().getName();

    private CustomDialog mCustomDialog;

    private String mDialogString = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(resourceId() == 0 ? R.layout.fragment_base_refresh_layout : resourceId(), container, false);
        initView(view, savedInstanceState);
        initView(view, container, savedInstanceState);
        initData();
        return view;
    }

    /**
     * @return activity的contentView布局
     */
    @LayoutRes
    protected abstract int resourceId();

    /**
     * 在此方法初始化控件
     */
    protected abstract void initView(View view, Bundle savedInstanceState);

    protected void initView(View view, ViewGroup container, Bundle savedInstanceState) {

    }

    /**
     * 在此方法初始化数据，初始化完记得调用dismmisCustomDialog()
     */
    protected void initData() {
        showLoadingDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismmisLoadingDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkGo.getInstance().cancelTag(TAG);
    }

    /**
     * 显示加载对话框，一打开界面默认显示
     */
    protected void showLoadingDialog() {
        dismmisLoadingDialog();
        if (mCustomDialog == null || !mDialogString.equals("")) {
            mDialogString = "";
            mCustomDialog = new CustomDialog(getContext(), null);
        }
        mCustomDialog.show();
    }

    /**
     * 显示自定义加载提示的对话框
     */
    protected void showLoadingDialog(String msg) {
        dismmisLoadingDialog();
        if (mCustomDialog == null || !mDialogString.equals(msg)) {
            mDialogString = msg;
            mCustomDialog = new CustomDialog(getContext(), msg);
        }
        mCustomDialog.show();
    }

    /**
     * 显示自定义加载提示的对话框
     */
    protected void showCantCancelLoadingDialog(String msg) {
        dismmisLoadingDialog();
        mCustomDialog = new CustomDialog(getContext(), msg, false);
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
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken());
    }

    protected void setTAG(String TAG) {
        this.TAG = TAG;
    }

    protected boolean handleException(Exception e) {
        dismmisLoadingDialog();
        if (!DensityUtil.isException(getContext(), e)) {
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

    protected void startActivity(Class<?> tClass) {
        Intent intent = new Intent(getActivity(), tClass);
        startActivity(intent);
    }

    protected void startActivity(Class<?> tClass, String key, Serializable data) {
        Intent intent = new Intent(getActivity(), tClass);
        intent.putExtra(key, data);
        startActivity(intent);
    }

    protected void startActivityForResult(Class<?> tClass, int requestCode, String key, Serializable value) {
        Intent intent = new Intent(getActivity(), tClass);
        intent.putExtra(key, value);
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityWithList(Class<?> tClass, String key, ArrayList<? extends Parcelable> data) {
        Intent intent = new Intent(getActivity(), tClass);
        intent.putParcelableArrayListExtra(key, data);
        startActivity(intent);
    }

    /**
     * 显示位置在屏幕1/5的Toast
     *
     * @param msg 显示的消息
     */
    protected void showFiveToast(String msg) {
        MyToast.showToast(getContext(), msg, 5);
    }

    /**
     * @return textView里面的字符串
     */
    protected String getText(TextView textView) {
        return textView.getText().toString();
    }

}
