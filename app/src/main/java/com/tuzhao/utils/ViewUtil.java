package com.tuzhao.utils;

import android.view.View;
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

    private static boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

}
