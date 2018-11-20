package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by juncoder on 2018/11/19.
 */
public class TransformerViewPager extends ViewPager {

    public TransformerViewPager(@NonNull Context context) {
        super(context);
    }

    public TransformerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 为了让不是子类或者同一个包都能调用该方法，该方法最终会调用PagerTransformer的transformPage方法
     */
    @Override
    public void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
    }

}
