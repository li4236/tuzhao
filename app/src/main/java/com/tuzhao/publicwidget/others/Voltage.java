package com.tuzhao.publicwidget.others;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tuzhao.R;

/**
 * Created by juncoder on 2018/5/3.
 */

public class Voltage extends View {

    private Paint mBorderPaint;

    private Paint mVoltagePaint;

    private RectF mBorderRect;

    private RectF mVoltageRect;

    private RectF mHeadRect;

    private int mVoltage;

    //电量的总宽度
    private float mVoltageWidth;

    private float mBorderWidth;

    private float mBorderRadius;

    private int mBorderColor;

    private int mLowVoltageColor;

    private int mMiddleVoltageColor;

    private int mHeighVoltageColor;

    public Voltage(Context context) {
        super(context);
        mBorderRadius = 2;
        mBorderWidth = 4;
        mBorderColor = Color.BLACK;
        mLowVoltageColor = Color.parseColor("#980000");
        mMiddleVoltageColor = Color.parseColor("#fbbb11");
        mHeighVoltageColor = Color.parseColor("#4cda64");
        init();
    }

    public Voltage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs, 0);
        init();
    }

    public Voltage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);

        mVoltagePaint = new Paint();
        mVoltagePaint.setStyle(Paint.Style.FILL);

        mBorderRect = new RectF();
        mHeadRect = new RectF();
        mVoltageRect = new RectF();
    }

    private void initAttribute(Context context, AttributeSet attributeSet, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.Voltage, defStyleAttr, 0);
        mBorderRadius = typedArray.getDimension(R.styleable.Voltage_border_radius, 2);
        mBorderWidth = typedArray.getDimension(R.styleable.Voltage_border_width, 4);
        mVoltage = typedArray.getInt(R.styleable.Voltage_voltage, 0);
        mBorderColor = typedArray.getColor(R.styleable.Voltage_border_color, Color.BLACK);
        mLowVoltageColor = typedArray.getColor(R.styleable.Voltage_low_voltage_color, Color.parseColor("#980000"));
        mMiddleVoltageColor = typedArray.getColor(R.styleable.Voltage_middle_voltage_color, Color.parseColor("#fbbb11"));
        mHeighVoltageColor = typedArray.getColor(R.styleable.Voltage_heigh_voltage_color, Color.parseColor("#4cda64"));
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mHeadRect.set(width - height / 4, height * 3 / 8, width, height * 5 / 8);
        mBorderRect.set(mBorderPaint.getStrokeWidth()/2, mBorderPaint.getStrokeWidth()/2, mHeadRect.left, height - mBorderPaint.getStrokeWidth()/2);

        mVoltageWidth = mBorderRect.right - mBorderRect.left - mBorderWidth * 2;
        mVoltageRect.left = mBorderRect.left + mBorderWidth;
        mVoltageRect.top = mBorderRect.top + mBorderWidth;
        mVoltageRect.bottom = mBorderRect.bottom - mBorderWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        canvas.drawRoundRect(mBorderRect, mBorderRadius, mBorderRadius, mBorderPaint);

        mBorderPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mHeadRect, mBorderPaint);

        mVoltageRect.right = mVoltageWidth * mVoltage / 100 + mBorderRect.left + mBorderWidth;
        if (mVoltage > 0) {
            mVoltageRect.set(mBorderRect.left + mBorderWidth, mBorderRect.top + mBorderWidth,
                    mVoltageWidth * mVoltage / 100 + mBorderRect.left + mBorderWidth, mBorderRect.bottom - mBorderWidth);
            if (mVoltage <= 20) {
                mVoltagePaint.setColor(mLowVoltageColor);
            } else if (mVoltage >= 70) {
                mVoltagePaint.setColor(mHeighVoltageColor);
            } else {
                mVoltagePaint.setColor(mMiddleVoltageColor);
            }

            canvas.drawRect(mVoltageRect, mVoltagePaint);
        }
    }

    public void setVoltage(@IntRange(from = 0, to = 100) int voltage) {
        if (voltage < 5) {
            mVoltage = 5;
        } else if (voltage > 100) {
            mVoltage = 100;
        } else {
            mVoltage = voltage;
        }
        invalidate();
    }

}
