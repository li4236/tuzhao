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
 * <p>
 * 用于我的车位开锁，和预约的订单开锁
 * </p>
 */

public class CircularArcView extends View {

    private static final String TAG = "CircularArcView";

    //外圆弧和外圆的画笔
    private Paint mArcPaint;

    //内圆的画笔
    private Paint mCirclePaint;

    //外圆弧和外圆的画笔宽
    private float mArcWidth;

    private int mArcColor;

    private int mCicleColor;

    private int mSweepAngle;

    //外圆弧的path
    private Path mPath;

    private RectF mRectF;

    //把圆弧闭合的path
    private Path[] mClosePath;

    private float[] mCloseStartAngle;

    //圆弧间隔角度的一半
    private int mBinaryAngle;

    //内圆半径
    private float mRadius;

    private int mWidth;

    private int mHeight;

    //勾的path
    private Path mHookPath;

    private int mProgress = -1;

    private int mCircleAlpha = 256;

    public CircularArcView(Context context) {
        super(context);
        mArcColor = Color.WHITE;
        mCicleColor = Color.parseColor("#4df2f2f2");
        mSweepAngle = 68;
        mArcWidth = 8;
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
        mArcPaint = new Paint();
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setStrokeJoin(Paint.Join.ROUND);
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(mArcColor);

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCicleColor);

        mPath = new Path();
        mRectF = new RectF();

        mClosePath = new Path[4];
        for (int i = 0; i < mClosePath.length; i++) {
            mClosePath[i] = new Path();
        }

        mCloseStartAngle = new float[mClosePath.length];

        mHookPath = new Path();
    }

    private void initAttribute(Context context, AttributeSet attributeSet, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CircularArcView, defStyleAttr, 0);
        mArcWidth = typedArray.getDimension(R.styleable.CircularArcView_stroke_width, 8);
        mArcColor = typedArray.getColor(R.styleable.CircularArcView_CircularArcView_arc_color, Color.WHITE);
        mCicleColor = typedArray.getColor(R.styleable.CircularArcView_CircularArcView_circle_color, Color.parseColor("#4df2f2f2"));
        mRadius = typedArray.getDimension(R.styleable.CircularArcView_CircularArcView_circle_radius, 0);
        mSweepAngle = typedArray.getInt(R.styleable.CircularArcView_sweep_angle, 68);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (mRadius == 0) {
            mRadius = mWidth * 147 / 167 / 2;
        }

        mBinaryAngle = (360 - 4 * mSweepAngle) / 8;
        int startAngle = 0;
        mRectF.set(mArcPaint.getStrokeWidth() / 2, mArcPaint.getStrokeWidth() / 2, mWidth - mArcPaint.getStrokeWidth() / 2, mHeight - mArcPaint.getStrokeWidth() / 2);

        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                startAngle += mBinaryAngle;
            }
            mPath.addArc(mRectF, startAngle, mSweepAngle);
            mCloseStartAngle[i] = startAngle - mBinaryAngle * 2;
            mClosePath[i].addArc(mRectF, mCloseStartAngle[i], 0);
            startAngle += mSweepAngle + mBinaryAngle * 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mProgress == -1) {
            canvas.drawPath(mPath, mArcPaint);
            canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mCirclePaint);
            if (mCircleAlpha != 256) {
                for (Path path : mClosePath) {
                    canvas.drawPath(path, mArcPaint);
                }
            }
        } else if (mProgress == 0) {
            //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mHookPath.reset();
            mHookPath.moveTo((float) widthTransfor(37), (float) heightTransfor(64));
            canvas.drawPath(mPath, mArcPaint);
            canvas.drawCircle(mWidth / 2, mHeight / 2, Math.min(mWidth, mHeight) / 2 - mArcPaint.getStrokeWidth() / 2, mArcPaint);
        } else {
            if (mProgress < 36) {
                mHookPath.lineTo((float) (widthTransfor(37) + widthTransfor(92 - 37) * mProgress / 100.0), (float) (heightTransfor(64) + heightTransfor(57 - 37) * mProgress / 36));
            } else {
                mHookPath.lineTo((float) (widthTransfor(37) + widthTransfor(92 - 37) * mProgress / 100.0), (float) (heightTransfor(83) - heightTransfor(83 - 42) * (mProgress - 36) / 64));
            }
            canvas.drawPath(mHookPath, mArcPaint);
            canvas.drawCircle(mWidth / 2, mHeight / 2, Math.min(mWidth, mHeight) / 2 - mArcPaint.getStrokeWidth() / 2, mArcPaint);
        }
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    public void setCicleAlpha(int cicleAlpha) {
        mCircleAlpha = cicleAlpha;
        mCirclePaint.setAlpha(cicleAlpha);
        for (int i = 0; i < mClosePath.length; i++) {
            mClosePath[i].addArc(mRectF, mCloseStartAngle[i], (float) (mBinaryAngle * 2 * ((255 - cicleAlpha) / 255.0)));
        }
        invalidate();
    }

    private double heightTransfor(int height) {
        return height / 126.0 * mHeight;
    }

    private double widthTransfor(int width) {
        return width / 126.0 * mWidth;
    }

}
