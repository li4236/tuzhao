package com.tuzhao.activity.navi;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;

/**
 * Created by juncoder on 2018/4/4.
 */

public class VoiceDialog  {

    private Context mContext;

    private ImageView mImageView;

    private TextView mTextView;

    private Point mPoint;

    private Dialog mDialog;

    public VoiceDialog(@NonNull Context context) {
        mContext = context;
        init();
    }

    private void init() {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mPoint = new Point(1080,1920);

        if (windowManager != null) {
            windowManager.getDefaultDisplay().getRealSize(mPoint);
        }
    }

    public Dialog createDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_voice_speak_refactor, null);
        mDialog= new Dialog(mContext, R.style.DialogStyle);
        mDialog.setCancelable(false);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mPoint.x / 3, mPoint.x / 3);
        mDialog.setContentView(view,layoutParams);
        return mDialog;
    }

    public void updateUI(int resId, String prompt) {
        if (null == mTextView || null == mImageView) {
            if (mDialog != null) {
                mTextView = mDialog.findViewById(R.id.tv_prompt);
                mImageView = mDialog.findViewById(R.id.iv_load);
            }
        }

        if (null != mTextView && null != mImageView) {
            mTextView.setText(prompt);
            mImageView.setImageResource(resId);
        }
    }

}
