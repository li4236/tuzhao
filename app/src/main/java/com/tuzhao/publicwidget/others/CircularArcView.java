package com.tuzhao.publicwidget.others;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tuzhao.R;

/**
 * Created by juncoder on 2018/5/17.
 */

public class CircularArcView extends View {

    private Paint mPaint;

    private Paint mCirclePaint;

    private float mStrokeWidth;

    private int mColor;

    private int mSweepAngle;

    private Path mPath;

    private RectF mRectF;

    private float mRadius;

    private int mWidth;

    private int mHeight;

    public CircularArcView(Context context) {
        super(context);
        mColor = Color.WHITE;
        mSweepAngle = 68;
        mStrokeWidth = 8;
    }

    public CircularArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs, 0);
        init();
    }

    public CircularArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.parseColor("#4df2f2f2"));

        mPath = new Path();
        mRectF = new RectF();
    }

    private void initAttribute(Context context, AttributeSet attributeSet, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CircularArcView, defStyleAttr, 0);
        mStrokeWidth = typedArray.getDimension(R.styleable.CircularArcView_stroke_width, 8);
        mColor = typedArray.getColor(R.styleable.CircularArcView_stroke_color, Color.WHITE);
        mSweepAngle = typedArray.getInt(R.styleable.CircularArcView_sweep_angle, 68);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mWidth == 0) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
            mRadius = mWidth * 147 / 167 / 2;

            int angle = (360 - 4 * mSweepAngle) / 8;
            int totalAngle = 0;
            mRectF.set(mPaint.getStrokeWidth() / 2, mPaint.getStrokeWidth() / 2, mWidth - mPaint.getStrokeWidth() / 2, mHeight - mPaint.getStrokeWidth() / 2);

            for (int i = 0; i < 4; i++) {
                if (i == 0) {
                    totalAngle += angle;
                    mPath.addArc(mRectF, totalAngle, mSweepAngle);
                } else {
                    mPath.addArc(mRectF, totalAngle, mSweepAngle);
                }
                totalAngle += mSweepAngle + angle * 2;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
        canvas.drawCircle(mWidth / 2, mHeight/ 2, mRadius, mCirclePaint);
    }

}
