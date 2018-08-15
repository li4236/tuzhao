package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tuzhao.R;

import static android.graphics.Color.parseColor;

/**
 * Created by juncoder on 2018/5/10.
 */

public class CircleView extends View {

    private Paint mPaint;

    private int mWidth;

    private int mHeight;

    private float mRadius;

    private int mColor;

    public CircleView(Context context) {
        super(context);
        mColor = parseColor("#f2ac4c");
        init();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs, 0);
        init();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs, defStyleAttr);
        init();
    }

    private void initAttribute(Context context, AttributeSet attributeSet, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CircleView, defStyleAttr, 0);
        mColor = typedArray.getColor(R.styleable.CircleView_color, parseColor("#f2ac4c"));
        typedArray.recycle();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        //mPaint.setShadowLayer(30,5,2, Color.RED);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mRadius = Math.min(mWidth / 2, mHeight / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mWidth / 2, mHeight / 2);
        canvas.drawCircle(0, 0, mRadius, mPaint);
        canvas.restore();
    }

    public void setColor(int color) {
        mColor = color;
        mPaint.setColor(mColor);
    }

}
