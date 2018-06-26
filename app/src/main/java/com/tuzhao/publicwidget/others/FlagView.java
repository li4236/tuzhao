package com.tuzhao.publicwidget.others;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tuzhao.utils.DensityUtil;

/**
 * Created by juncoder on 2018/6/26.
 */
public class FlagView extends View {

    private Paint mPaint;

    private RectF mRectF;

    private Path mPath;

    private int mColor;

    public FlagView(Context context) {
        super(context);
        init();
    }

    public FlagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mColor = Color.parseColor("#ffa830");

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);

        mRectF = new RectF();
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRectF.right = dpToPx(1);
        mRectF.bottom = dpToPx(8);

        mPath.moveTo(mRectF.right, 0);
        mPath.lineTo(mRectF.right + dpToPx(4), mRectF.right * 2);
        mPath.lineTo(mRectF.right, mRectF.right * 4);
        mPath.close();
        setMeasuredDimension((int) mRectF.right * 5, (int) mRectF.bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
        canvas.drawRect(mRectF, mPaint);
    }

    public void setColor(int color) {
        mColor = color;
        postInvalidate();
    }

    private int dpToPx(float dp) {
        return DensityUtil.dp2px(getContext(), dp);
    }

}
