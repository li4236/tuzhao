package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.tuzhao.R;
import com.tuzhao.utils.DensityUtil;

import java.util.Objects;

/**
 * Created by juncoder on 2018/10/17.
 */
public class CornerImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint mPaint;

    private int mCornerColor;

    /**
     * 圆角矩形
     */
    private RectF mRectF;

    private Path mPath;

    /**
     * 角标的开始点
     */
    private PointF mCornerStartPoint;

    /**
     * 角标的结束点
     */
    private PointF mCornerBottomPoint;

    private Paint mTextPaint;

    private int mTextColor;

    private String mText;

    public CornerImageView(Context context) {
        this(context, null);
    }

    public CornerImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornerImageView, defStyleAttr, 0);
        mCornerColor = typedArray.getColor(R.styleable.CornerImageView_corner_color, Color.parseColor("#ff0101"));
        mTextColor = typedArray.getColor(R.styleable.CornerImageView_discount_text_color, Color.WHITE);
        mText = typedArray.getString(R.styleable.CornerImageView_discount);
        typedArray.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mCornerColor);

        mRectF = new RectF();
        mPath = new Path();
        mCornerStartPoint = new PointF();
        mCornerBottomPoint = new PointF();

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(DensityUtil.sp2px(context, 14));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRectF.left == 0) {
            mRectF.right = getMeasuredWidth() - 8;
            mRectF.left = mRectF.right - 56;
            mRectF.bottom = 56;
            mRectF.top = 2;

            mCornerStartPoint.x = mRectF.right * 5 / 6;
            mCornerStartPoint.y = mRectF.top;
            mCornerBottomPoint.x = mRectF.right;
            mCornerBottomPoint.y = mRectF.right - mCornerStartPoint.x;

            mPath.addArc(mRectF, 270, 90);      //添加弧形时之前的线画不会连接到弧形的起点，因此先画弧形
            mPath.lineTo(mCornerBottomPoint.x, mCornerBottomPoint.y);
            mPath.lineTo(mCornerStartPoint.x, mRectF.top);
            mPath.close();
        }
        canvas.drawPath(mPath, mPaint);

        if (mText != null) {
            drawText(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        canvas.save();
        canvas.translate(mCornerStartPoint.x, mCornerStartPoint.y);
        canvas.rotate(45);
        float textWidth = mTextPaint.measureText(mText);
        double cornerWidth = Math.sqrt(Math.pow(mRectF.right - mCornerStartPoint.x, 2) + Math.pow(mCornerBottomPoint.y, 2));
        float baseX = (float) (cornerWidth / 2 - textWidth / 2);
        canvas.drawText(mText, baseX, textWidth > 66 ? -16 : -20, mTextPaint);
        canvas.restore();
    }

    public void setCornerColor(int cornerColor) {
        if (mCornerColor != cornerColor) {
            mCornerColor = cornerColor;
            mPaint.setColor(mCornerColor);
            //invalidate();     不需要动态更新的话就不重绘了，防止重绘两次
        }
    }

    public void setTextColor(int textColor) {
        if (mTextColor != textColor) {
            mTextColor = textColor;
            mTextPaint.setColor(mTextColor);
            //invalidate();
        }
    }

    public void setText(String text) {
        if (!Objects.equals(mText, text)) {
            mText = text;
            //invalidate();
        }
    }

}
