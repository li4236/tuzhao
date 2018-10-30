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

import com.tuzhao.R;

/**
 * Created by juncoder on 2018/5/10.
 */

public class NavigationView extends View {

    private Paint mBackgroundPaint;

    private Paint mInnerPaint;

    private float mBackgroundRadius;

    private float mInnerRadius;

    private float mTriangleHeight;

    private RectF mTriangelRect;

    private boolean mIsDrawCenterCircle;

    public NavigationView(Context context) {
        this(context, null);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavigationView, defStyleAttr, 0);
        int backgroundColor = typedArray.getColor(R.styleable.NavigationView_background_color, Color.WHITE);
        int innerColor = typedArray.getColor(R.styleable.NavigationView_inner_color, Color.parseColor("#f4bb67"));
        mInnerRadius = typedArray.getDimension(R.styleable.NavigationView_inner_radius, 0);
        typedArray.recycle();

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(backgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setAntiAlias(true);

        mInnerPaint = new Paint();
        mInnerPaint.setColor(innerColor);
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setAntiAlias(true);

        mTriangelRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mBackgroundRadius = width / 2;

        float triangleWidth = width * 13 / 20;
        mTriangleHeight = height * 12 / 30;
        mTriangelRect.top = -mTriangleHeight / 2;
        mTriangelRect.left = -triangleWidth;
        mTriangelRect.right = triangleWidth;
        mTriangelRect.bottom = mTriangleHeight;

        if (mInnerRadius == 0 || mInnerRadius > mBackgroundRadius) {
            mInnerRadius = (float) (mBackgroundRadius * 0.7);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mBackgroundRadius, 0);
        if (mIsDrawCenterCircle) {
            canvas.translate(0, mBackgroundRadius + mTriangleHeight * 10 / 13);
            canvas.drawCircle(0, 0, mInnerRadius, mInnerPaint);
        } else {
            canvas.drawArc(mTriangelRect, 63, 53, true, mInnerPaint);
            canvas.translate(0, mBackgroundRadius + mTriangleHeight * 10 / 13);
            canvas.drawCircle(0, 0, mBackgroundRadius, mBackgroundPaint);
        }
        canvas.restore();
    }

    public void setDrawCenterCircle(boolean drawCenterCircle) {
        mIsDrawCenterCircle = drawCenterCircle;
    }

}
