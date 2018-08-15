package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by TZL12 on 2018/1/2.
 */

public class FixViewPager extends ViewPager {

    public FixViewPager(Context context) {
        super(context);
    }
    public FixViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception  e) {
            return false;
        }
    }
}
