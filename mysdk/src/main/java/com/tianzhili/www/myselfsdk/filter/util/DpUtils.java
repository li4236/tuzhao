package com.tianzhili.www.myselfsdk.filter.util;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * 屏幕尺寸工具类
 * <p>
 * dpi即每英寸多少像素
 * px代表像素点
 * dp最终设计使用的尺寸
 * <p>
 * Created by sjy on 2017/4/28.
 */

public class DpUtils {

    /**
     * 将dp转成px,代码设置尺寸需要用到
     */
    public static int dp2px(Context context, float dipValue) {
        //
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * density + 0.5f);
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics()) + 0.5F);
    }

    public static View infalte(Context context, @LayoutRes int layoutId, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(layoutId, parent, false);

    }

}
