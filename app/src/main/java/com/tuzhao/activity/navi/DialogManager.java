package com.tuzhao.activity.navi;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;

/**
 * @author Xinxin Shi
 *         录音时的弹出框工具类
 */

public class DialogManager {
    private static DialogManager instance;
    private ImageView ivLoad;
    private TextView tvPrompt;


    public static DialogManager getInstance() {
        if (null == instance) {
            synchronized (DialogManager.class) {
                instance = new DialogManager();
            }
        }
        return instance;
    }

    public AlertDialog recordDialogShow(Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialog);
        dialogBuilder.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_voice_speak, null);
        tvPrompt = (TextView) view.findViewById(R.id.tv_prompt);
        ivLoad = (ImageView) view.findViewById(R.id.iv_load);
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
//        dialog.getWindow().setBackgroundDrawable(new BitmapDrawable());
        return dialog;
    }

    public void updateUI(int resId, String prompt) {
        if (null != tvPrompt && null != ivLoad) {
            tvPrompt.setText(prompt);
            ivLoad.setImageResource(resId);
        }
    }

}
