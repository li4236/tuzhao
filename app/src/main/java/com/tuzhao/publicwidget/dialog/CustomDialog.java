package com.tuzhao.publicwidget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.tuzhao.R;
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.IntentObserable;

import java.util.Objects;

/**
 * Created by juncoder on 2018/5/31.
 */

public class CustomDialog extends Dialog {

    private static final String TAG = "CustomDialog";

    private boolean mIsShowAnimation;

    private PaymentPasswordHelper mPasswordHelper;

    public CustomDialog(@NonNull Context context, View view) {
        super(context, R.style.CustomDialogStyle);
        setContentView(view);
        setCanceledOnTouchOutside(false);
    }

    public CustomDialog(@NonNull Context context, View view, boolean isShowAnimation) {
        super(context, R.style.CustomDialogStyle);
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            //防止复用出错，在添加车辆，上传图片时会复用
            viewGroup.removeView(view);
        }
        setContentView(view);
        mIsShowAnimation = isShowAnimation;
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.SlideAnimationStyle);
        }
    }

    public CustomDialog(PaymentPasswordHelper helper) {
        super(helper.getContext(), R.style.CustomDialogStyle);
        //设置安全flag，禁止截屏，防止密码泄露
        Objects.requireNonNull(getWindow()).setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(helper.getView());
        mPasswordHelper = helper;
        mIsShowAnimation = true;

        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.SlideAnimationStyle);
        }

        helper.setCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public int getPasswordType() {
        if (mPasswordHelper != null) {
            return mPasswordHelper.getPaymentPasswordType();
        }
        return 0;
    }

    public PaymentPasswordHelper getPasswordHelper() {
        return mPasswordHelper;
    }

    @Override
    public void show() {
        super.show();

        if (mPasswordHelper != null) {
            mPasswordHelper.clearPassword();
            mPasswordHelper.showPasswordError("");
        }

        if (mIsShowAnimation) {
            Intent intent = new Intent();
            intent.setAction(ConstansUtil.DIALOG_SHOW);
            IntentObserable.dispatch(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (mPasswordHelper != null && !mPasswordHelper.isBackPressedCanCancel()) {
            mPasswordHelper.setPasswordFirst();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();

        if (mIsShowAnimation) {
            Intent intent = new Intent();
            intent.setAction(ConstansUtil.DIALOG_DISMISS);
            IntentObserable.dispatch(intent);
        }
    }

}
