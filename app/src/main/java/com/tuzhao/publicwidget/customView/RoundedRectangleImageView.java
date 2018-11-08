package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.tuzhao.R;

/**
 * Created by juncoder on 2018/8/24.
 * <p>
 * 圆角图片
 * </p>
 */
public class RoundedRectangleImageView extends AppCompatImageView {

    private float mRadius;
    private Path mRoundedRectPath;
    private RectF mRectF;

    public RoundedRectangleImageView(Context context) {
        this(context, null);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedRectangleImageView, defStyleAttr, 0);
        mRadius = typedArray.getDimension(R.styleable.RoundedRectangleImageView_rounded_radius,
                (float) (getResources().getDisplayMetrics().density * 2 + 0.5));
        typedArray.recycle();

        mRoundedRectPath = new Path();
        mRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRectF.right == 0) {
            mRectF.right = getMeasuredWidth();
            mRectF.bottom = getMaxHeight();
            mRoundedRectPath.addRoundRect(mRectF, mRadius, mRadius, Path.Direction.CW);
        }

        canvas.save();
        canvas.clipPath(mRoundedRectPath);
        super.onDraw(canvas);
        canvas.restore();
    }

}
