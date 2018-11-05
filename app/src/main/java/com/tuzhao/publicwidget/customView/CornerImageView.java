package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;

import com.tuzhao.R;
import com.tuzhao.utils.DensityUtil;

import java.util.Objects;

/**
 * Created by juncoder on 2018/10/17.
 * <p>
 * Xfermode使用教程 https://cloud.tencent.com/developer/article/1015278
 * </p>
 */
public class CornerImageView extends android.support.v7.widget.AppCompatImageView {

    private RectF mCornerRectF;

    private float mCornerRadius;

    private Bitmap mSrcBitmap;

    private Canvas mSrcCanvas;

    private Bitmap mDetBitmap;

    private Canvas mDetCanvas;

    private Paint mCornerPaint;

    private Xfermode mXfermode;

    private Paint mPaint;

    private int mCornerColor;

    private Path mPath;

    /**
     * 角标的开始点
     */
    private PointF mCornerStartPoint;

    /**
     * 角标的结束点
     */
    private PointF mCornerBottomPoint;

    private Paint mCornerTextPaint;

    private int mCornerTextColor;

    private String mCornerText;

    /**
     * 画下面左右两边文字的paint
     */
    private Paint mTextPaint;

    private float mTextSize;

    private int mTextColor;

    private float mHorizontalMargin;

    private float mBottomMargin;

    private String mStartText;

    private String mEndText;

    public CornerImageView(Context context) {
        this(context, null);
    }

    public CornerImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornerImageView, defStyleAttr, 0);
        mCornerRadius = typedArray.getDimension(R.styleable.CornerImageView_corner_radius, 28);
        mCornerColor = typedArray.getColor(R.styleable.CornerImageView_corner_color, Color.parseColor("#ff0101"));
        mCornerTextColor = typedArray.getColor(R.styleable.CornerImageView_discount_text_color, Color.WHITE);
        mCornerText = typedArray.getString(R.styleable.CornerImageView_discount);
        mTextSize = typedArray.getDimension(R.styleable.CornerImageView_corner_text_size, spToPx(12));
        mTextColor = typedArray.getColor(R.styleable.CornerImageView_corner_text_color, Color.parseColor("#323232"));
        mHorizontalMargin = typedArray.getDimension(R.styleable.CornerImageView_corner_text_horizontal_margin, dpToPx(16));
        mBottomMargin = typedArray.getDimension(R.styleable.CornerImageView_corner_text_bottom_margin, dpToPx(12));
        typedArray.recycle();

        mCornerRectF = new RectF();
        mCornerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mCornerColor);

        mPath = new Path();
        mCornerStartPoint = new PointF();
        mCornerBottomPoint = new PointF();

        mCornerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCornerTextPaint.setColor(mCornerTextColor);
        mCornerTextPaint.setTextSize(DensityUtil.sp2px(context, 14));

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCornerRectF.bottom == 0) {
            mCornerRectF.right = getMeasuredWidth();
            mCornerRectF.bottom = getMeasuredHeight();
        }

        if (mSrcBitmap == null) {
            mSrcBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            mSrcCanvas = new Canvas(mSrcBitmap);

            mDetBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            mDetCanvas = new Canvas(mDetBitmap);
        }

        //把图片画在mDetCanvas
        super.onDraw(mDetCanvas);
        //画右上角的角标
        if (mCornerStartPoint.x == 0) {
            mCornerStartPoint.x = getMeasuredWidth() * 5 / 6;
            mCornerBottomPoint.x = getMeasuredWidth();
            mCornerBottomPoint.y = mCornerBottomPoint.x - mCornerStartPoint.x;

            //形成一个三角形的path，因为最后mDetCanvas只会显示圆角矩形部分，所以最终显示的是圆角三角形
            mPath.moveTo(mCornerStartPoint.x, 0);
            mPath.lineTo(mCornerBottomPoint.x, 0);
            mPath.lineTo(mCornerBottomPoint.x, mCornerBottomPoint.y);
            mPath.close();
        }
        mDetCanvas.drawPath(mPath, mPaint);
        canvas.drawBitmap(mDetBitmap, 0, 0, mCornerPaint);

        //再画圆角矩形，并且使用Xfermode取mDetCanvas绘制的内容中和mSrcCanvas绘制的内存重叠的部分(即是圆角矩形的图片)
        mSrcCanvas.drawRoundRect(mCornerRectF, mCornerRadius, mCornerRadius, mCornerPaint);
        mCornerPaint.setXfermode(mXfermode);
        canvas.drawBitmap(mSrcBitmap, 0, 0, mCornerPaint);
        mCornerPaint.setXfermode(null);

        drawText(canvas);
        drawBottomText(canvas);
    }

    /**
     * 角标的文字
     */
    private void drawText(Canvas canvas) {
        if (mCornerText != null) {
            //把canvas移动到角标的开始坐标点，并旋转45度，这样方便计算文字的基线坐标
            canvas.save();
            canvas.translate(mCornerStartPoint.x, mCornerStartPoint.y);
            canvas.rotate(45);
            float textWidth = mCornerTextPaint.measureText(mCornerText);
            double cornerWidth = Math.sqrt(Math.pow(mCornerBottomPoint.x - mCornerStartPoint.x, 2) + Math.pow(mCornerBottomPoint.y, 2));
            float baseX = (float) (cornerWidth / 2 - textWidth / 2);
            canvas.drawText(mCornerText, baseX, -dpToPx(6), mCornerTextPaint);
            canvas.restore();
        }
    }

    /**
     * 画图片下面的文字
     */
    private void drawBottomText(Canvas canvas) {
        float baseY = getMeasuredHeight() - mBottomMargin;
        if (mStartText != null) {
            canvas.drawText(mStartText, mHorizontalMargin, baseY, mTextPaint);
        }

        if (mEndText != null) {
            float endTextBaseX = getMeasuredWidth() - mHorizontalMargin - mTextPaint.measureText(mEndText);
            canvas.drawText(mEndText, endTextBaseX, baseY, mTextPaint);
        }
    }

    public void setCornerColor(int cornerColor) {
        if (mCornerColor != cornerColor) {
            mCornerColor = cornerColor;
            mPaint.setColor(mCornerColor);
            //invalidate();     不需要动态更新的话就不重绘了，防止重绘两次
        }
    }

    public void setCornerTextColor(int textColor) {
        if (mCornerTextColor != textColor) {
            mCornerTextColor = textColor;
            mCornerTextPaint.setColor(mCornerTextColor);
            //invalidate();
        }
    }

    public void setCornerText(String text) {
        if (!Objects.equals(mCornerText, text)) {
            mCornerText = text;
            //invalidate();
        }
    }

    public void setStartText(String startText) {
        if (!Objects.equals(mStartText, startText)) {
            mStartText = startText;
        }
    }

    public void setEndText(String endText) {
        if (!Objects.equals(mEndText, endText)) {
            mEndText = endText;
        }
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    private float spToPx(int sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    private float dpToPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

}
