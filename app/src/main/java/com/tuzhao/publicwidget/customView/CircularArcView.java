package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tuzhao.R;

/**
 * Created by juncoder on 2018/5/17.
 * <p>
 * 用于我的车位开锁，和预约的订单,好友订单开锁
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

    //外圆弧path的开始角度
    private float[] mCloseStartAngle;

    //外圆弧之间的间隔角度的一半
    private int mBinaryAngle;

    //内圆半径
    private float mRadius;

    private int mWidth;

    private int mHeight;

    //勾的path
    private Path mHookPath;

    private int mProgress = -1;

    public CircularArcView(Context context) {
        this(context, null);
    }

    public CircularArcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs, defStyleAttr);
        init();
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

        mCloseStartAngle = new float[4];

        mHookPath = new Path();
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
        int startAngle = 0;     //每个外圆弧开始的角度
        mRectF.set(mArcPaint.getStrokeWidth() / 2, mArcPaint.getStrokeWidth() / 2, mWidth - mArcPaint.getStrokeWidth() / 2, mHeight - mArcPaint.getStrokeWidth() / 2);

        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                startAngle += mBinaryAngle;
            }
            mPath.addArc(mRectF, startAngle, mSweepAngle);
            mCloseStartAngle[i] = startAngle;
            startAngle += mSweepAngle + mBinaryAngle * 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mProgress == -1) {
            //画外圆弧和内圆
            canvas.drawPath(mPath, mArcPaint);
            canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mCirclePaint);
        } else {
            //画√和外圆(外圆弧已闭合)
            canvas.drawPath(mHookPath, mArcPaint);
            canvas.drawPath(mPath, mArcPaint);
        }
    }

    /**
     * @param progress 画√的进度
     */
    public void setProgress(@IntRange(from = 0, to = 100) int progress) {
        if (progress >= 0 && progress <= 100) {
            mProgress = progress;

            //画√
            mHookPath.reset();
            mHookPath.moveTo((float) widthTransform(37), (float) heightTransform(64));
            if (mProgress < 36) {
                //连接√的前面一部分
                mHookPath.lineTo((float) (widthTransform(37) + widthTransform(92 - 37) * mProgress / 100.0), (float) (heightTransform(64) + heightTransform(57 - 37) * mProgress / 36));
            } else {
                //连接前面一部分和往上勾的后一部分
                mHookPath.lineTo((float) (widthTransform(37) + widthTransform(92 - 37) * 36 / 100.0), (float) (heightTransform(64) + heightTransform(57 - 37)));
                mHookPath.lineTo((float) (widthTransform(37) + widthTransform(92 - 37) * mProgress / 100.0), (float) (heightTransform(83) - heightTransform(83 - 42) * (mProgress - 36) / 64));
            }

            invalidate();
        }
    }

    /**
     * 中间部分逐渐变透明，然后在原来圆弧的基础上再画圆弧进行闭合
     */
    public void setCicleAlpha(@IntRange(from = 0, to = 255) int cicleAlpha) {
        mCirclePaint.setAlpha(cicleAlpha);

        mPath.reset();
        for (int i = 0; i < 4; i++) {
            //修改扫描角度，让圆弧的弧长不断变长，形成闭合的效果
            mPath.addArc(mRectF, mCloseStartAngle[i], (float) (mSweepAngle + mBinaryAngle * 2 * ((255 - cicleAlpha) / 255.0)));
        }
        invalidate();
    }

    private double heightTransform(int height) {
        return height / 126.0 * mHeight;
    }

    private double widthTransform(int width) {
        return width / 126.0 * mWidth;
    }

}
