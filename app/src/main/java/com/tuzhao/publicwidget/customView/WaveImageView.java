package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/4/17.
 * <p>
 * 发票详情的水波浪
 */

public class WaveImageView extends AppCompatImageView {

    private Paint mPaint;

    private Path mInnerWavePath;

    private Path mOuterWavePath;

    /**
     * 各个点的横坐标
     */
    private int[] mAbscissa;

    private List<Point> mPoints;

    private int mWidth;

    private int mHeigth;

    public WaveImageView(Context context) {
        this(context, null);
    }

    public WaveImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#69ffffff"));
        mPaint.setPathEffect(new CornerPathEffect(10));

        mInnerWavePath = new Path();
        mOuterWavePath = new Path();

        mPoints = new ArrayList<>();
        mAbscissa = new int[18];
        mAbscissa[0] = 100;
        mAbscissa[1] = 150;
        mAbscissa[2] = 221;
        mAbscissa[3] = 270;
        mAbscissa[4] = 317;
        mAbscissa[5] = 359;
        mAbscissa[6] = 400;
        mAbscissa[7] = 415;
        //最里面波浪的控制点和结束点
        mPoints.add(new Point(dpToPx(100), dpToPx(59)));
        mPoints.add(new Point(dpToPx(150), dpToPx(76)));
        mPoints.add(new Point(dpToPx(221), dpToPx(93)));
        mPoints.add(new Point(dpToPx(270), dpToPx(69)));
        mPoints.add(new Point(dpToPx(317), dpToPx(44)));
        mPoints.add(new Point(dpToPx(359), dpToPx(74)));
        mPoints.add(new Point(dpToPx(400), dpToPx(93)));
        mPoints.add(new Point(dpToPx(415), dpToPx(70)));

        mAbscissa[8] = 35;
        mAbscissa[9] = 80;
        mAbscissa[10] = 158;
        mAbscissa[11] = 193;
        mAbscissa[12] = 240;
        mAbscissa[13] = 295;
        mAbscissa[14] = 341;
        mAbscissa[15] = 362;
        mAbscissa[16] = 387;
        mAbscissa[17] = 422;
        //外层波浪的控制点和结束点
        mPoints.add(new Point(dpToPx(35), dpToPx(56)));
        mPoints.add(new Point(dpToPx(80), dpToPx(77)));
        mPoints.add(new Point(dpToPx(158), dpToPx(94)));
        mPoints.add(new Point(dpToPx(193), dpToPx(70)));
        mPoints.add(new Point(dpToPx(240), dpToPx(45)));
        mPoints.add(new Point(dpToPx(295), dpToPx(77)));
        mPoints.add(new Point(dpToPx(341), dpToPx(89)));
        mPoints.add(new Point(dpToPx(362), dpToPx(75)));
        mPoints.add(new Point(dpToPx(387), dpToPx(65)));
        mPoints.add(new Point(dpToPx(422), dpToPx(83)));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeigth = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //onMeasure会调用多次，onDraw调用一次,所以放在这里重新赋值横坐标
        //view大小变化后(横竖屏切换)需要重新设置横坐标，因为高度是不变的，所以不用重新设置
        for (int i = 0; i < mPoints.size(); i++) {
            mPoints.get(i).x = getScaleWidth(mAbscissa[i]);
        }

        mInnerWavePath.reset();
        mInnerWavePath.moveTo(dpToPx(76), dpToPx(87));   //先移动到里层波浪的起始点
        for (int i = 0; i < 8; i += 2) {
            mInnerWavePath.quadTo(mPoints.get(i).x, mPoints.get(i).y, mPoints.get(i + 1).x, mPoints.get(i + 1).y);
        }
        mInnerWavePath.lineTo(mWidth, mHeigth);       //移动到View的右下角坐标，用来闭合
        mInnerWavePath.close();

        mOuterWavePath.reset();
        mOuterWavePath.moveTo(0, dpToPx(87));
        for (int i = 8; i <= mPoints.size() - 2; i += 2) {
            mOuterWavePath.quadTo(mPoints.get(i).x, mPoints.get(i).y, mPoints.get(i + 1).x, mPoints.get(i + 1).y);
        }
        mOuterWavePath.lineTo(mWidth, mHeigth);
        mOuterWavePath.close();

        canvas.drawPath(mInnerWavePath, mPaint);
        canvas.drawPath(mOuterWavePath, mPaint);
    }

    private int dpToPx(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据屏幕宽度获取对应比例的值
     */
    private int getScaleWidth(int dpValue) {
        return (int) (dpValue / 375f * mWidth);
    }

}
