package com.tuzhao.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

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

}
