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

    private Path mRoundedRectPath;
    private RectF mRectF;
    private float[] mRadiusArray = new float[8];

    public RoundedRectangleImageView(Context context) {
        this(context, null);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedRectangleImageView, defStyleAttr, 0);
        float radius = typedArray.getDimension(R.styleable.RoundedRectangleImageView_rounded_radius, 0);
        if (radius == 0) {
            float topLeft = typedArray.getDimension(R.styleable.RoundedRectangleImageView_top_left_radius, 0);
            float topRight = typedArray.getDimension(R.styleable.RoundedRectangleImageView_top_right_radius,0);
            float bottomLeft = typedArray.getDimension(R.styleable.RoundedRectangleImageView_bottom_left_radius,0);
            float bottomRight = typedArray.getDimension(R.styleable.RoundedRectangleImageView_bottom_right_radius,0);
            mRadiusArray[0] = topLeft;
            mRadiusArray[1] = topLeft;
            mRadiusArray[2] = topRight;
            mRadiusArray[3] = topRight;
            mRadiusArray[4] = bottomRight;
            mRadiusArray[5] = bottomRight;
            mRadiusArray[6] = bottomLeft;
            mRadiusArray[7] = bottomLeft;
        } else {
            for (int i = 0; i < mRadiusArray.length; i++) {
                mRadiusArray[i] = radius;
            }
        }
        typedArray.recycle();

        mRoundedRectPath = new Path();
        mRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRectF.right == 0) {
            mRectF.right = getMeasuredWidth();
            mRectF.bottom = getMeasuredHeight();
            mRoundedRectPath.addRoundRect(mRectF, mRadiusArray, Path.Direction.CW);
        }

        canvas.save();
        canvas.clipPath(mRoundedRectPath);
        super.onDraw(canvas);
        canvas.restore();
    }

}
