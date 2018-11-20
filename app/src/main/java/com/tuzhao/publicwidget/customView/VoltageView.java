package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.tuzhao.R;


/**
 * Created by juncoder on 2018/5/17.
 */

public class VoltageView extends View {

    private Paint mBorderPaint;

    private Paint mVoltagePaint;

    private RectF mBorderRect;

    private RectF mVoltageRect;

    private RectF mHeadRect;

    private int mVoltage;

    //电量的总长度
    private float mVoltageHeight;

    private float mBorderWidth;

    private float mBorderRadius;

    private int mBorderColor;

    private int mLowVoltageColor;

    private int mMiddleVoltageColor;

    private int mHeighVoltageColor;

    private int mOrientation;

    public VoltageView(Context context) {
        this(context, null);
    }

    public VoltageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoltageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs, defStyleAttr);
        init();
    }

    private void initAttribute(Context context, AttributeSet attributeSet, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.VoltageView, defStyleAttr, 0);
        mBorderRadius = typedArray.getDimension(R.styleable.VoltageView_vv_border_radius, 4);
        mBorderWidth = typedArray.getDimension(R.styleable.VoltageView_vv_border_width, 4);
        mVoltage = typedArray.getInt(R.styleable.VoltageView_vv_voltage, 0);
        mBorderColor = typedArray.getColor(R.styleable.VoltageView_vv_border_color, Color.WHITE);
        mLowVoltageColor = typedArray.getColor(R.styleable.VoltageView_vv_low_voltage_color, Color.parseColor("#980000"));
        mMiddleVoltageColor = typedArray.getColor(R.styleable.VoltageView_vv_middle_voltage_color, Color.parseColor("#fbbb11"));
        mHeighVoltageColor = typedArray.getColor(R.styleable.VoltageView_vv_heigh_voltage_color, Color.parseColor("#4cda64"));
        mOrientation = typedArray.getInt(R.styleable.VoltageView_vv_orientation, LinearLayout.HORIZONTAL);
        typedArray.recycle();
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (mOrientation == LinearLayout.HORIZONTAL) {
            int headHeight = height / 3;
            mHeadRect.set(width - headHeight/2, headHeight, width, height - headHeight);
            mBorderRect.set(mBorderWidth / 2, mBorderWidth / 2, width - headHeight/2, height - mBorderWidth / 2);

            mVoltageHeight = mBorderRect.right - mBorderRect.left - mBorderWidth * 2;
            mVoltageRect.top = mBorderRect.top + mBorderWidth;
            mVoltageRect.left = mBorderRect.left + mBorderWidth;
            mVoltageRect.bottom = mBorderRect.bottom - mBorderWidth;
        } else {
            mHeadRect.set(width / 3, 0, width * 2 / 3, width / 7);
            mBorderRect.set(mBorderWidth / 2, mHeadRect.bottom, width - mBorderWidth / 2, height - mBorderWidth / 2);

            mVoltageHeight = mBorderRect.bottom - mBorderRect.top - mBorderWidth * 2;
            mVoltageRect.left = mBorderRect.left + mBorderWidth;
            mVoltageRect.right = mBorderRect.right - mBorderWidth;
            mVoltageRect.bottom = mBorderRect.bottom - mBorderWidth;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBorderPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mHeadRect, mBorderPaint);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(mBorderRect, mBorderRadius, mBorderRadius, mBorderPaint);

        if (mOrientation == LinearLayout.HORIZONTAL) {
            mVoltageRect.right = mVoltageRect.left + mVoltageHeight * mVoltage / 100;
        } else {
            mVoltageRect.top = mVoltageRect.bottom - mVoltageHeight * mVoltage / 100;
        }
        if (mVoltage <= 20) {
            mVoltagePaint.setColor(mLowVoltageColor);
        } else if (mVoltage >= 70) {
            mVoltagePaint.setColor(mHeighVoltageColor);
        } else {
            mVoltagePaint.setColor(mMiddleVoltageColor);
        }
        canvas.drawRect(mVoltageRect, mVoltagePaint);
    }

    public void setVoltage(int voltage) {
        if (voltage < 0) {
            mVoltage = 0;
        } else if (voltage > 100) {
            mVoltage = 100;
        } else {
            mVoltage = voltage;
        }
        invalidate();
    }

}
