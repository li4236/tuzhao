package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.utils.ConstansUtil;

/**
 * Created by juncoder on 2018/8/14.
 * <p>
 * 加号
 * </p>
 */
public class PlusView extends View {

    private Paint mPaint;

    private Paint mOtherPaint;

    public PlusView(Context context) {
        this(context, null);
    }

    public PlusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlusView, defStyleAttr, 0);
        int color = typedArray.getColor(R.styleable.PlusView_plus_color, ConstansUtil.G10_COLOR);
        float strokeWidth = typedArray.getDimension(R.styleable.PlusView_plus_width, 6);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(color);

        mOtherPaint = new Paint();
        mOtherPaint.setAntiAlias(true);
        mOtherPaint.setStyle(Paint.Style.STROKE);
        mOtherPaint.setStrokeWidth(1);
        mOtherPaint.setColor(Color.RED);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画横线
        canvas.drawLine(getPaddingStart() + mPaint.getStrokeWidth()/2, getPaddingTop() + (getHeight() - getPaddingBottom() - getPaddingTop()) / 2 ,
                getWidth() - getPaddingEnd() - mPaint.getStrokeWidth()/2, getPaddingTop() + (getHeight() - getPaddingBottom() - getPaddingTop()) / 2, mPaint);
        //画竖线
        canvas.drawLine(getPaddingStart() + (getWidth() - getPaddingStart() - getPaddingEnd()) / 2,
                getPaddingTop()+mPaint.getStrokeWidth()/2, getPaddingStart() + (getWidth() - getPaddingStart() - getPaddingEnd()) / 2 ,
                getHeight() - getPaddingBottom() - mPaint.getStrokeWidth()/2, mPaint);
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
