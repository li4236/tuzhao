package com.tuzhao.fragment.base;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tianzhili.www.myselfsdk.okgo.OkGo;
import com.tianzhili.www.myselfsdk.okgo.request.BaseRequest;
import com.tuzhao.R;
import com.tuzhao.publicmanager.UserManager;
import com.tuzhao.publicwidget.callback.TokenInterceptor;
import com.tuzhao.publicwidget.dialog.LoadingDialog;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;
import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by juncoder on 2018/5/17.
 */

public abstract class BaseStatusFragment extends Fragment {

    /**
     * 打印日志的时候会显示具体是哪个类的日志
     * <p>
     * 如果是一个ViewPager中显示多个同样的Fragment则需要重写该属性，因为网络请求会以该TAG作为标识，如果有多个同样其中一个被destroyView了会根据这个
     * TAG来取消请求，不重写则会导致其他Fragment的请求取消
     * </p>
     */
    protected String TAG = this.getClass().getName();

    /**
     * fragment所在的根view
     */
    private View mView;

    /**
     * 加载对话框
     */
    private LoadingDialog mLoadingDialog;

    /**
     * 加载对话框的文字
     */
    private String mDialogString = "";

    /**
     * true(在onDestroyView中取消网络请求)  false(不取消，需要自行在类中取消)
     */
    private boolean mAutoCancelRequest = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(resourceId() == 0 ? R.layout.fragment_base_refresh_layout : resourceId(), container, false);
        initView(mView, savedInstanceState);
        initView(mView, container, savedInstanceState);
        initData();
        return mView;
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

    protected <T extends View> T findViewById(@IdRes int id) {
        return mView.findViewById(id);
    }

    /**
     * 在此方法初始化数据，初始化完记得调用dismmisCustomDialog()
     * 如果不需要一进界面就显示加载框则重写该方法，并且不要掉super方法
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
        if (mAutoCancelRequest) {
            OkGo.getInstance().cancelTag(TAG);
        }
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public String getTAG() {
        return TAG;
    }

    /**
     */
    protected void setAutoCancelRequest() {
        mAutoCancelRequest = false;
    }

    /**
     * 显示加载对话框，一打开界面默认显示
     */
    protected void showLoadingDialog() {
        dismmisLoadingDialog();
        if (mLoadingDialog == null || !mDialogString.equals("")) {
            mDialogString = "";
            mLoadingDialog = new LoadingDialog(getContext(), null);
        }
        mLoadingDialog.show();
    }

    /**
     * 显示自定义加载提示的对话框
     */
    protected void showLoadingDialog(String msg) {
        dismmisLoadingDialog();
        if (mLoadingDialog == null || !mDialogString.equals(msg)) {
            mDialogString = msg;
            mLoadingDialog = new LoadingDialog(getContext(), msg);
        }
        mLoadingDialog.show();
    }

    /**
     * 显示自定义加载提示的对话框
     */
    protected void showCantCancelLoadingDialog(String msg) {
        dismmisLoadingDialog();
        mLoadingDialog = new LoadingDialog(getContext(), msg, false);
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

    /**
     * @param url 请求的路径
     * @return 返回BaseRequest，并带有tag和token
     */
    protected BaseRequest getOkGo(String url) {
        return OkGo.post(url)
                .tag(TAG)
                .addInterceptor(new TokenInterceptor())
                .headers("token", UserManager.getInstance().getUserInfo().getToken());
    }

    /**
     * @return true(处理了该异常)    false(未处理该异常，一般为后台返回的错误响应码)
     */
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
                        Log.e(TAG, "handleException: " + e.getMessage());
                        return false;
                }
            } else {
                //showFiveToast(e.getMessage());
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

    protected void startActivity(Class<?> tClass, Bundle value) {
        Intent intent = new Intent(getActivity(), tClass);
        intent.putExtras(value);
        startActivity(intent);
    }

    protected void startActivity(Class<?> tClass, String key, String data) {
        Intent intent = new Intent(getActivity(), tClass);
        intent.putExtra(key, data);
        startActivity(intent);
    }

    protected void startActivity(Class<?> tClass, String... keyWithValue) {
        Intent intent = new Intent(getActivity(), tClass);
        for (int i = 0; i < keyWithValue.length; i += 2) {
            intent.putExtra(keyWithValue[i], keyWithValue[i + 1]);
        }
        startActivity(intent);
    }

    protected void startActivity(Class<?> tClass, String key, Parcelable data) {
        Intent intent = new Intent(getActivity(), tClass);
        intent.putExtra(key, data);
        startActivity(intent);
    }

    protected void startActivityForResult(Class<?> tClass, int requestCode, String key, Parcelable value) {
        Intent intent = new Intent(getActivity(), tClass);
        intent.putExtra(key, value);
        if (getActivity() != null) {
            getActivity().startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 需要在fragment接受结果的不要调用activity的startActivityForResult方法，调用fragment的即可
     */
    protected void startActivityForResultByFragment(Class<?> tClass, int requestCode, String... keyWithValue) {
        Intent intent = new Intent(getActivity(), tClass);
        for (int i = 0; i < keyWithValue.length; i += 2) {
            intent.putExtra(keyWithValue[i], keyWithValue[i + 1]);
        }
        startActivityForResult(intent, requestCode);
    }

    protected void startActivityForResult(Class<?> tClass, int requestCode, String key, ArrayList<? extends Parcelable> data) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), tClass);
            intent.putParcelableArrayListExtra(key, data);
            getActivity().startActivityForResult(intent, requestCode);
        }
    }

    protected void startActivityForResult(Class<?> tClass, int requestCode, String stringKey, String vaule, String key, ArrayList<? extends Parcelable> data) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), tClass);
            intent.putExtra(stringKey, vaule);
            intent.putParcelableArrayListExtra(key, data);
            getActivity().startActivityForResult(intent, requestCode);
        }
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
        if (getContext() != null) {
            MyToast.showToast(getContext(), msg, 5);
        }
    }

    protected void showDialog(String title, String message, Dialog.OnClickListener onClickListener) {
        new TipeDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", onClickListener)
                .create()
                .show();
    }

    /**
     * @return textView里面的字符串
     */
    protected String getText(TextView textView) {
        return textView.getText().toString();
    }

    protected int getTextLength(TextView textView) {
        return getText(textView).trim().length();
    }

    protected void setNewText(TextView text, String string) {
        if (!text.getText().toString().equals(string)) {
            text.setText(string);
        }
    }

    /**
     * @param enable true(可编辑)   false(不可编辑)
     */
    protected void setEditTextEnable(EditText editText, boolean enable) {
        editText.setFocusable(enable);
        editText.setFocusableInTouchMode(enable);
        editText.setInputType(enable ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
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

    protected int dpToPx(float dp) {
        if (getContext() != null) {
            return DensityUtil.dp2px(getContext(), dp);
        }
        return 0;
    }

    protected void finish() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

}
