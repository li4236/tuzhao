package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.utils.ConstansUtil;

/**
 * Created by juncoder on 2018/8/11.
 * <p>
 * ×
 * </p>
 */
public class CrossView extends View {

    private Paint mPaint;

    public CrossView(Context context) {
        this(context,null);
    }

    public CrossView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CrossView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CrossView, defStyleAttr, 0);
        int color = typedArray.getColor(R.styleable.CrossView_cross_color, ConstansUtil.B1_COLOR);
        float strokeWidth = typedArray.getDimension(R.styleable.CrossView_cross_width, 4);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(getPaddingStart(), getPaddingTop(), getMeasuredWidth() - getPaddingEnd(), getMeasuredHeight() - getPaddingBottom(), mPaint);
        canvas.drawLine(getPaddingStart(), getMeasuredHeight() - getPaddingBottom(), getMeasuredWidth() - getPaddingEnd(), getPaddingTop(), mPaint);
    }

    public void setColor(@ColorInt int color) {
        mPaint.setColor(color);
        postInvalidate();
    }

    public void setStrokeWidth(@FloatRange(from = 1) float strokeWidth) {
        mPaint.setStrokeWidth(strokeWidth);
        postInvalidate();
    }

}
