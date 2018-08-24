package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by juncoder on 2018/8/24.
 * <p>
 * 圆角图片,copy from "com.zhihu.matisse.internal.ui.widget" in "https://github.com/zhihu/Matisse"
 * </p>
 */
public class RoundedRectangleImageView extends AppCompatImageView {

    private float mRadius; // dp
    private Path mRoundedRectPath;
    private RectF mRectF;

    public RoundedRectangleImageView(Context context) {
        super(context);
        init(context);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        mRadius = 2.0f * density;
        mRoundedRectPath = new Path();
        mRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRectF.set(0.0f, 0.0f, getWidth(), getHeight());
        mRoundedRectPath.addRoundRect(mRectF, mRadius, mRadius, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(mRoundedRectPath);
        super.onDraw(canvas);
    }

}
