package com.tuzhao.publicwidget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.tuzhao.R;

/**
 * 自定义透明的dialog
 */
public class LoadingDialog extends Dialog {

    private static final String TAG = "LoadingDialog";

    private String content;

    private boolean mCancelable = true;

    public LoadingDialog(Context context, String content, boolean cancelable) {
        super(context, R.style.CustomDialog);
        this.content = content != null ? content : "加载中...";
        initView();
        mCancelable = cancelable;
    }

    public LoadingDialog(Context context, String content) {
        super(context, R.style.CustomDialog);
        this.content = content != null ? content : "加载中...";
        initView();
    }

    public LoadingDialog(Context context, String content, int type) {
        super(context, R.style.CustomDialog);
        this.content = content != null ? content : "加载中...";
        if (type == 0) {
            if (getWindow() != null) {
                //如果设置了本flag而没有设置FLAG_NOT_FOCUSABLE, 则窗口表现为不需要同输入法交互, 同时会被至于输入法之上
                //防止输入法的输入类型变为其他的
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        }
        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (LoadingDialog.this.isShowing() && mCancelable) {
                    LoadingDialog.this.dismiss();
                    Log.e(TAG, "onKeyDown: ");
                    return false;
                }
                break;
        }
        return true;
    }

    private void initView() {
        setContentView(R.layout.view_dialog);
        ((TextView) findViewById(R.id.tvcontent)).setText(content);
        setCanceledOnTouchOutside(true);
        setCancelable(false);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}