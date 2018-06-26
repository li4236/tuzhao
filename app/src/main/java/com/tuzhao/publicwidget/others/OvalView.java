package com.tuzhao.publicwidget.others;

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
 * Created by juncoder on 2018/6/26.
 */
public class OvalView extends View {

    private Paint mPaint;

    private RectF mRectF;

    private int mColor;

    public OvalView(Context context) {
        super(context);
        mColor = Color.parseColor("#feee39");
        init();
    }

    public OvalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs, 0);
        init();
    }

    public OvalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs, defStyleAttr);
        init();
    }

    private void initAttribute(Context context, AttributeSet attributeSet, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.OvalView, defStyleAttr, 0);
        mColor = typedArray.getColor(R.styleable.OvalView_oval_color, Color.parseColor("#feee39"));
        typedArray.recycle();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);

        mRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        mRectF.right = width;
        mRectF.bottom = width / 3;
        setMeasuredDimension(width, width / 3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(mRectF, mPaint);
    }
}
