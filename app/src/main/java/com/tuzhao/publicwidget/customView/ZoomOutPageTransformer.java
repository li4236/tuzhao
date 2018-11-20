package com.tuzhao.publicwidget.customView;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by juncoder on 2018/11/14.
 */
public class ZoomOutPageTransformer implements ViewPager.PageTransformer {

    private static final float MIN_SCALE = 0.75f;

    private static final float MIN_ALPHA = 0.75f;

    private ViewGroup mViewGroup;

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (mViewGroup == null) {
            mViewGroup = (ViewGroup) page.getParent();
        }

        int leftInScreen = page.getLeft() - mViewGroup.getScrollX();
        int centerXInViewPager = leftInScreen + page.getMeasuredWidth() / 2;
        int offsetX = centerXInViewPager - mViewGroup.getMeasuredWidth() / 2;
        float offsetRate = (float) offsetX * 0.25f / mViewGroup.getMeasuredWidth();
        float scaleFactor = 1 - Math.abs(offsetRate);
        if (scaleFactor > 0) {
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setAlpha(scaleFactor);
        }

/*        if (position < 0) {
            //0->-1 从屏幕中心往左移动
            //-1->0 从左边往右移动到屏幕中心
            page.setScaleX((1 - MIN_SCALE) * (1 + position) + MIN_SCALE);
            page.setScaleY((1 - MIN_SCALE) * (1 + position) + MIN_SCALE);
            page.setAlpha((1 - MIN_ALPHA) * (1 + position) + MIN_ALPHA);
        }else if (position <= 1) {
            //0->1 从屏幕中心往右移动
            //1->0 从左边往左移动到屏幕中心
            page.setScaleX((1 - MIN_SCALE) * (1 - position) + MIN_SCALE);
            page.setScaleY((1 - MIN_SCALE) * (1 - position) + MIN_SCALE);
            page.setAlpha((1 - MIN_ALPHA) * (1 - position) + MIN_ALPHA);
        } else {
            //在屏幕右边的位置（如果viewpager只能显示一个则不可见）
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setAlpha(MIN_ALPHA);
        }*/
    }

}
