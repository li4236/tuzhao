package com.tuzhao.publicwidget.others;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by juncoder on 2018/6/26.
 * <p>
 * 信用分上面的椭圆
 * </p>
 */
public class OvalView extends View {

    private Paint mPaint;

    private RectF mRectF;

    private LinearGradient mLinearGradient;

    public OvalView(Context context) {
        super(context);
        init();
    }

    public OvalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OvalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
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

        if (mLinearGradient == null) {
            mLinearGradient = new LinearGradient(0, 0, 0, mRectF.bottom, new int[]{Color.parseColor("#fed641"),
                    Color.parseColor("#ffd641")}, null, Shader.TileMode.CLAMP);
            mPaint.setShader(mLinearGradient);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(mRectF, mPaint);
    }

}
