package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by juncoder on 2018/8/14.
 */
public class PasswordView extends View {

    private Paint mPaint;

    /**
     * 边框的宽度
     */
    private float mStrokeWidth;

    /**
     * 边框的圆角半径
     */
    private float mCornerRadius;

    /**
     * 圆形密码的半径
     */
    private float mCicleRadius;

    /**
     * 没有焦点时的颜色,包括边框和焦点的颜色
     */
    private int mNormalColor;

    /**
     * 有焦点时的颜色
     */
    private int mFocusColor;

    /**
     * 出错时的背景色
     */
    private int mErrorColor;

    private int mTextColor;

    private float mTextSize;

    /**
     * 当前是否有焦点
     */
    private boolean mHaveFocus;

    /**
     * 是否绘制光标
     */
    private boolean mDrawFocus;

    /**
     * 是否绘制错误色
     */
    private boolean mIsShowError;

    /**
     * 内容显示方式，0（数字），1（圆形）
     */
    private int mTextType;

    private String mText;

    /**
     * 焦点线的宽
     */
    private int mLineWidth;

    /**
     * 焦点线的高
     */
    private float mLineHeigth;

    /**
     * 边框
     */
    private RectF mRectF;

    private Handler mHandler;

    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        mRectF = new RectF();

        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRect(canvas);
        if (!mIsShowError) {
            //如果是显示错误的话则不画文字和光标
            drawText(canvas);
            drawCursor(canvas);
        }
    }

    /**
     * 画背景框
     */
    private void drawRect(Canvas canvas) {
        if (mRectF.right == 0) {
            mRectF.left = mStrokeWidth / 2;
            mRectF.top = mStrokeWidth / 2;
            mRectF.right = getWidth() - mStrokeWidth / 2;
            mRectF.bottom = getHeight() - mStrokeWidth / 2;
        }

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        if (mIsShowError) {
            mPaint.setColor(mErrorColor);
        } else {
            if (mHaveFocus) {
                mPaint.setColor(mFocusColor);
            } else {
                mPaint.setColor(mNormalColor);
            }
        }
        canvas.drawRoundRect(mRectF, mCornerRadius, mCornerRadius, mPaint);
    }

    /**
     * 画文字
     */
    private void drawText(Canvas canvas) {
        if (mText != null) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mTextColor);
            if (mTextType == 0) {
                //画文字
                float textWidth = mPaint.measureText(mText);
                float baseX = getWidth() / 2 - textWidth / 2;
                float baseY = getHeight() / 2 - ((mPaint.descent() + mPaint.ascent()) / 2) + textWidth / 5;
                canvas.drawText(mText, baseX, baseY, mPaint);
            } else if (mTextType == 1) {
                //画圆
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, mCicleRadius, mPaint);
            }
        }
    }

    /**
     * 画光标
     */
    private void drawCursor(Canvas canvas) {
        if (mHaveFocus && mDrawFocus) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mLineWidth);
            mPaint.setColor(mFocusColor);
            if (mLineHeigth == 0) {
                //设置光标的高
                mLineHeigth = getHeight() * 0.6f;
            }
            if (mText != null) {
                if (mTextType == 0) {
                    float textWidth = mPaint.measureText(mText);
                    float baseX = getWidth() / 2 - textWidth / 2;
                    //光标位置为文字结尾后4px
                    canvas.drawLine(baseX + textWidth + 4, (getHeight() - mLineHeigth) / 2,
                            baseX + textWidth + 4, mLineHeigth + (getHeight() - mLineHeigth) / 2, mPaint);
                } else if (mTextType == 1) {
                    //光标位置为圆点结尾后4px
                    canvas.drawLine(getWidth() / 2 + mCicleRadius * 2 + 4, (getHeight() - mLineHeigth) / 2,
                            getWidth() / 2 + mCicleRadius * 2 + 4 + mLineWidth, mLineHeigth + (getHeight() - mLineHeigth) / 2, mPaint);
                }
            } else {
                //光标位置为中心
                canvas.drawLine(getWidth() / 2, (getHeight() - mLineHeigth) / 2, getWidth() / 2, mLineHeigth + (getHeight() - mLineHeigth) / 2, mPaint);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    public void updateFocus(boolean haveFocus) {
        mHaveFocus = haveFocus;
        mHandler.removeCallbacksAndMessages(null);
        if (haveFocus) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //不断重绘，用来展示光标的闪烁效果
                    invalidate();
                    mDrawFocus = !mDrawFocus;
                    mHandler.postDelayed(this, 600);
                }
            });
        } else {
            invalidate();
        }
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
    }

    public float getCornerRadius() {
        return mCornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        mCornerRadius = cornerRadius;
    }

    public int getNormalColor() {
        return mNormalColor;
    }

    public void setNormalColor(int normalColor) {
        mNormalColor = normalColor;
    }

    public int getFocusColor() {
        return mFocusColor;
    }

    public void setFocusColor(int focusColor) {
        mFocusColor = focusColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        mPaint.setTextSize(textSize);
    }

    public boolean isHaveFocus() {
        return mHaveFocus;
    }

    public void setHaveFocus(boolean haveFocus) {
        mHaveFocus = haveFocus;
    }

    public int getTextType() {
        return mTextType;
    }

    public void setTextType(int textType) {
        mTextType = textType;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public int getLineWidth() {
        return mLineWidth;
    }

    public void setLineWidth(int lineWidth) {
        mLineWidth = lineWidth;
    }

    public float getLineHeigth() {
        return mLineHeigth;
    }

    public void setLineHeigth(float lineHeigth) {
        mLineHeigth = lineHeigth;
    }

    public float getCicleRadius() {
        return mCicleRadius;
    }

    public void setCicleRadius(float cicleRadius) {
        mCicleRadius = cicleRadius;
    }

    public int getErrorColor() {
        return mErrorColor;
    }

    public void setErrorColor(int errorColor) {
        mErrorColor = errorColor;
    }

    public boolean isShowError() {
        return mIsShowError;
    }

    /**
     * @param showError true（显示错误的密码框，并且不绘制文字和光标）
     */
    public void setShowError(boolean showError) {
        mIsShowError = showError;
        invalidate();
    }

}
