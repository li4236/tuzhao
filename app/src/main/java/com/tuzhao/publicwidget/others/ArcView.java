package com.tuzhao.publicwidget.others;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by juncoder on 2018/5/10.
 * <p>
 * 三角形，用于地图当前定位
 */

public class ArcView extends View {

    private Paint mPaint;

    private RectF mRectF;

    public ArcView(Context context) {
        super(context);
        init();
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#f4bb67"));
        //mPaint.setShadowLayer(60,0,0,Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mRectF.top = -height / 2;
        mRectF.left = -width / 2;
        mRectF.right = width / 2;
        mRectF.bottom = height / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate((mRectF.right - mRectF.left) / 2, 0);
        canvas.drawArc(mRectF, 65, 50, true, mPaint);
        canvas.restore();
    }

}
