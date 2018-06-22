package com.tuzhao.publicwidget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
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

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (LoadingDialog.this.isShowing() && mCancelable) {
                    LoadingDialog.this.dismiss();
                    Log.e(TAG, "onKeyDown: " );
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