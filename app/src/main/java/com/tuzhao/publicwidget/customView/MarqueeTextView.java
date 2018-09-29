package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by juncoder on 2018/9/29.
 */
public class MarqueeTextView extends android.support.v7.widget.AppCompatTextView {

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        //源码中会判断当前是否有焦点，如果没有则不会对文字进行滚动
        return true;
    }

}
