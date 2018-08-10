package com.tuzhao.publicwidget.others;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tuzhao.R;
import com.tuzhao.utils.ConstansUtil;

/**
 * Created by juncoder on 2018/8/9.
 * <p>
 * 箭头
 * </p>
 */
public class ArrowView extends View {

    public static final int TOP = 0;

    public static final int BOTTOM = 1;

    public static final int LEFT = 2;

    public static final int RIGHT = 3;

    private int mGravity;

    private int mColor;

    private float mArrowWidth;

    private Paint mPaint;

    public ArrowView(Context context) {
        this(context, null);
    }

    public ArrowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArrowView, defStyleAttr, 0);
        mGravity = typedArray.getInt(R.styleable.ArrowView_arrow_gravity, RIGHT);
        mColor = typedArray.getColor(R.styleable.ArrowView_arrow_color, ConstansUtil.B1_COLOR);
        mArrowWidth = typedArray.getDimension(R.styleable.ArrowView_arrow_width, 4);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mArrowWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mGravity) {
            case TOP:
                canvas.drawLine((getMeasuredWidth() - getPaddingStart() - getPaddingEnd()) / 2.0f + getPaddingStart(), getPaddingTop(), getPaddingStart(), getMeasuredHeight() - getPaddingBottom(), mPaint);
                canvas.drawLine((getMeasuredWidth() - getPaddingStart() - getPaddingEnd()) / 2.0f + getPaddingStart(), getPaddingTop(), getMeasuredWidth() - getPaddingEnd(), getMeasuredHeight() - getPaddingBottom(), mPaint);
                break;
            case BOTTOM:
                canvas.drawLine((getMeasuredWidth() - getPaddingStart() - getPaddingEnd()) / 2.0f + getPaddingStart(), getMeasuredHeight() - getPaddingBottom(), getPaddingStart(), getPaddingTop(), mPaint);
                canvas.drawLine((getMeasuredWidth() - getPaddingStart() - getPaddingEnd()) / 2.0f + getPaddingStart(), getMeasuredHeight() - getPaddingBottom(), getMeasuredWidth() - getPaddingEnd(), getPaddingTop(), mPaint);
                break;
            case LEFT:
                canvas.drawLine(getPaddingStart(), (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2.0f + getPaddingTop(), getMeasuredWidth() - getPaddingEnd(), getPaddingTop(), mPaint);
                canvas.drawLine(getPaddingStart(), (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2.0f + getPaddingTop(), getMeasuredWidth() - getPaddingEnd(), getMeasuredHeight() - getPaddingBottom(), mPaint);
                break;
            case RIGHT:
                canvas.drawLine(getMeasuredWidth() - getPaddingEnd(), (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2.0f + getPaddingTop(), getPaddingStart(), getPaddingTop(), mPaint);
                canvas.drawLine(getMeasuredWidth() - getPaddingEnd(), (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2.0f + getPaddingTop(), getPaddingStart(), getMeasuredHeight() - getPaddingBottom(), mPaint);
                break;
        }
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
        invalidate();
    }

    public void setColor(int color) {
        mColor = color;
        invalidate();
    }

    public void setArrowWidth(float arrowWidth) {
        mArrowWidth = arrowWidth;
        invalidate();
    }

}
