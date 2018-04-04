package com.tuzhao.publicwidget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.TextView;

import com.tuzhao.R;

/**
 * 自定义透明的dialog
 */
public class CustomDialog extends Dialog {

    private String content;

    public CustomDialog(Context context, String content) {
        super(context, R.style.CustomDialog);
        this.content = content != null ? content : "加载中...";
        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (CustomDialog.this.isShowing())
                    CustomDialog.this.dismiss();
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