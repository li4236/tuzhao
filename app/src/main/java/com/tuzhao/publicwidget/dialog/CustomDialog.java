package com.tuzhao.publicwidget.dialog;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;

/**
 * 自定义透明的dialog
 */
public class CustomDialog extends Dialog{
    private String content;

    private ObjectAnimator mObjectAnimator;

    public CustomDialog(Context context, String content) {
        super(context, R.style.CustomDialog);
        this.content=content!=null ? content : "加载中...";
        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if(CustomDialog.this.isShowing())
                    CustomDialog.this.dismiss();
                break;
        }
        return true;
    }

    private void initView(){
        setContentView(R.layout.view_dialog);
        ((TextView)findViewById(R.id.tvcontent)).setText(content);
        setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha=0.7f;
        getWindow().setAttributes(attributes);
        setCancelable(false);
        ImageView imageView = findViewById(R.id.progress_iv);
        mObjectAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        mObjectAnimator.setDuration(1800);
        mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mObjectAnimator.setRepeatMode(ValueAnimator.RESTART);
        mObjectAnimator.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mObjectAnimator.cancel();
    }

}