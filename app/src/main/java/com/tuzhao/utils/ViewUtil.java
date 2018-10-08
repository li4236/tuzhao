package com.tuzhao.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.tuzhao.activity.mine.CertifyZhimaActivity;
import com.tuzhao.publicwidget.dialog.TipeDialog;
import com.tuzhao.publicwidget.mytoast.MyToast;

/**
 * Created by juncoder on 2018/7/12.
 */
public class ViewUtil {

    public static void showProgressStatus(TextView textView, boolean showProgress) {
        if (showProgress) {
            if (!isVisible(textView)) {
                textView.setVisibility(View.VISIBLE);
                textView.setText("0%");
            }
        } else {
            if (isVisible(textView)) {
                textView.setVisibility(View.GONE);
            }
        }
    }

    public static boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    public static void setVisible(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void setInvisible(View view) {
        if (view.getVisibility() != View.INVISIBLE) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void setGone(View view) {
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }

    public static void addEndText(TextView textView, int placeholderCount) {
        StringBuilder stringBuilder = new StringBuilder(textView.getText());
        for (int i = 0; i < placeholderCount; i++) {
            stringBuilder.append("哈");
        }
        SpannableString spannableString = new SpannableString(stringBuilder.toString());
        spannableString.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), textView.getText().length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }

    /**
     * 弹出软键盘
     */
    public static void openInputMethod(View view) {
        if (view != null) {
            InputMethodManager methodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (methodManager != null) {
                methodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        }
    }

    /**
     * 关闭软键盘
     */
    public static void closeInputMethod(View view) {
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void showCertificationDialog(final Context context, String msg) {
        new TipeDialog.Builder(context)
                .setTitle("提示")
                .setMessage(msg + "前需要进行实名认证哦，马上实名认证吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(context, CertifyZhimaActivity.class));
                    }
                })
                .create()
                .show();
    }

    public static void contactService(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4006505058"));
        context.startActivity(intent);
    }

    public static void clipContent(Context context, String content) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            ClipData clipData = ClipData.newPlainText("订单编号", content);
            clipboardManager.setPrimaryClip(clipData);
            MyToast.showToast(context, "已复制", 5);
        } else {
            MyToast.showToast(context, "复制失败", 5);
        }
    }

}
