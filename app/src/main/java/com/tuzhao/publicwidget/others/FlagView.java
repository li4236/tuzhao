package com.tuzhao.publicwidget.others;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by juncoder on 2018/6/26.
 */
public class FlagView extends View {

    private Paint mPaint;


    public FlagView(Context context) {
        super(context);
    }

    public FlagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FlagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
