package com.tuzhao.publicwidget.customView;

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
import com.tuzhao.utils.ConstansUtil;
import com.tuzhao.utils.DensityUtil;

/**
 * Created by juncoder on 2018/6/27.
 * <p>
 * 信用分的进度条以及上面的倒三角形
 * </p>
 */
public class CreditView extends View {

    /**
     * 倒三角形的画笔
     */
    private Paint mTrianglePaint;

    /**
     * 倒三角形的路径
     */
    private Path mTrianglePath;

    private int mTriangleColor;

    private Paint mCreditPaint;

    /**
     * 信用分极差对应的矩形
     */
    private RectF mVeryPoorRectF;

    /**
     * 信用分差
     */
    private RectF mPoorRectF;

    /**
     * 良好
     */
    private RectF mFineRectF;

    /**
     * 优秀
     */
    private RectF mGoodRect;

    /**
     * 极好
     */
    private RectF mVeryGoodRect;

    private int mVeryPoorColor;

    private int mPoorColor;

    private int mFineColor;

    private int mGoodColor;

    private int mVeryGoodColor;

    private int mTextColor;

    private int mTextSize;

    private int mFourDp;

    private int mSixDp;

    private int mTenDp;

    private int mTwoHundredAndSixtyDp;

    private float mCurrentCredit;

    private String mZero;

    private String mThreeHundredAndFifty;

    private String mFiveHundredAndFifty;

    private String mSixtyHundredAndFifty;

    private String mSevenHundredAndFifty;

    private String mNineHundredAndFifty;

    private int mHeight;

    /**
     * 倒三角形下面那个角所在的横坐标
     */
    private float mTriangleCoodinate;

    public CreditView(Context context) {
        this(context, null);
    }

    public CreditView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTrianglePaint = new Paint();
        mTrianglePaint.setAntiAlias(true);
        mTrianglePaint.setStyle(Paint.Style.FILL);
        mTrianglePaint.setColor(mTriangleColor);

        mTrianglePath = new Path();

        mCreditPaint = new Paint();
        mCreditPaint.setAntiAlias(true);

        mVeryPoorRectF = new RectF();
        mPoorRectF = new RectF();
        mFineRectF = new RectF();
        mGoodRect = new RectF();
        mVeryGoodRect = new RectF();

        mTwoHundredAndSixtyDp = dpToPx(260);
        mFourDp = dpToPx(4);
        mTenDp = dpToPx(10);

        mZero = "200";
        mThreeHundredAndFifty = "350";
        mFiveHundredAndFifty = "550";
        mSixtyHundredAndFifty = "650";
        mSevenHundredAndFifty = "750";
        mNineHundredAndFifty = "950";
    }

    private void initAttribute(Context context, @Nullable AttributeSet attributeSet, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CreditView, defStyleAttr, 0);
        mTriangleColor = typedArray.getColor(R.styleable.CreditView_flag_color, Color.parseColor("#ffa830"));
        mVeryPoorColor = typedArray.getColor(R.styleable.CreditView_poor_color, Color.parseColor("#ff3052"));
        mPoorColor = typedArray.getColor(R.styleable.CreditView_poor_color, Color.parseColor("#ffcc30"));
        mFineColor = typedArray.getColor(R.styleable.CreditView_fine_color, Color.parseColor("#ffa830"));
        mGoodColor = typedArray.getColor(R.styleable.CreditView_good_color, Color.parseColor("#3dd6fa"));
        mVeryGoodColor = typedArray.getColor(R.styleable.CreditView_very_good_color, Color.parseColor("#82D093"));
        mTextColor = typedArray.getColor(R.styleable.CreditView_text_color, Color.parseColor("#323232"));
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.CreditView_text_size, (int) DensityUtil.sp2px(context, 8));
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth() - mTenDp;
        mSixDp = dpToPx(6) * width / mTwoHundredAndSixtyDp;
        width = width - mSixDp * 4;

        mVeryPoorRectF.left = mTextSize / 2;                    //矩形的开始位置为文字的中点
        mVeryPoorRectF.top = mSixDp;                            //倒三角形的高为6dp，所以矩形的top坐标为6dp
        mVeryPoorRectF.right = (ConstansUtil.POOR_CREDIT_SCORE - ConstansUtil.VERY_POOR_CREDIT_SCORE) / 750 * width + mVeryPoorRectF.left;
        mVeryPoorRectF.bottom = mVeryPoorRectF.top + mFourDp;   //矩形的高为4dp

        mPoorRectF.left = mVeryPoorRectF.right + mSixDp;        //每个矩形之间相距6dp
        mPoorRectF.top = mVeryPoorRectF.top;
        mPoorRectF.right = (float) (mPoorRectF.left + (ConstansUtil.FINE_CREDIT_SCORE - ConstansUtil.POOR_CREDIT_SCORE) / 750.0 * width);
        mPoorRectF.bottom = mVeryPoorRectF.bottom;

        mFineRectF.left = mPoorRectF.right + mSixDp;
        mFineRectF.top = mVeryPoorRectF.top;
        mFineRectF.bottom = mVeryPoorRectF.bottom;
        mFineRectF.right = (float) (mFineRectF.left + (ConstansUtil.GOOD_CREDIT_SCORE - ConstansUtil.FINE_CREDIT_SCORE) / 750.0 * width);

        mGoodRect.left = mFineRectF.right + mSixDp;
        mGoodRect.top = mVeryPoorRectF.top;
        mGoodRect.bottom = mVeryPoorRectF.bottom;
        mGoodRect.right = (float) (mGoodRect.left + (ConstansUtil.VERY_GOOD_CREDIT_SCORE - ConstansUtil.GOOD_CREDIT_SCORE) / 750.0 * width);

        mVeryGoodRect.left = mGoodRect.right + mSixDp;
        mVeryGoodRect.right = (float) (mVeryGoodRect.left + (ConstansUtil.MAX_CREDIT_SCORE - ConstansUtil.VERY_GOOD_CREDIT_SCORE) / 750.0 * width);
        mVeryGoodRect.top = mVeryPoorRectF.top;
        mVeryGoodRect.bottom = mVeryPoorRectF.bottom;

        mHeight = dpToPx(16) + mTextSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        caculateTriangle();
        canvas.drawPath(mTrianglePath, mTrianglePaint);

        mCreditPaint.setStyle(Paint.Style.FILL);
        mCreditPaint.setColor(mVeryPoorColor);
        canvas.drawRoundRect(mVeryPoorRectF, mFourDp, mFourDp, mCreditPaint);

        mCreditPaint.setColor(mPoorColor);
        canvas.drawRoundRect(mPoorRectF, mFourDp, mFourDp, mCreditPaint);

        mCreditPaint.setColor(mFineColor);
        canvas.drawRoundRect(mFineRectF, mFourDp, mFourDp, mCreditPaint);

        mCreditPaint.setColor(mGoodColor);
        canvas.drawRoundRect(mGoodRect, mFourDp, mFourDp, mCreditPaint);

        mCreditPaint.setColor(mVeryGoodColor);
        canvas.drawRoundRect(mVeryGoodRect, mFourDp, mFourDp, mCreditPaint);

        mCreditPaint.setColor(mTextColor);
        mCreditPaint.setTextSize(mTextSize);
        canvas.drawText(mZero, 0, mHeight, mCreditPaint);
        canvas.drawText(mThreeHundredAndFifty, mVeryPoorRectF.right - mTextSize / 2, mHeight, mCreditPaint);
        canvas.drawText(mFiveHundredAndFifty, mPoorRectF.right - mTextSize / 2, mHeight, mCreditPaint);
        canvas.drawText(mSixtyHundredAndFifty, mFineRectF.right - mTextSize / 2, mHeight, mCreditPaint);
        canvas.drawText(mSevenHundredAndFifty, mGoodRect.right - mTextSize / 2, mHeight, mCreditPaint);
        canvas.drawText(mNineHundredAndFifty, mVeryGoodRect.right - mTextSize, mHeight, mCreditPaint);
    }

    /**
     * 用于动画更新(不可删除！！！)
     */
    public void setCurrentCredit(float credit) {
        if (credit >= 0) {
            mCurrentCredit = credit;
        }
        invalidate();
    }

    /**
     * 计算当前倒三角形所在的坐标和颜色
     */
    private void caculateTriangle() {
        mTrianglePath.reset();
        if (mCurrentCredit <= 350) {
            if (mCurrentCredit < 200) {
                mCurrentCredit = 200;
            }
            mTriangleCoodinate = (mCurrentCredit - 200) / 150 * (mVeryPoorRectF.right - mVeryPoorRectF.left) + mVeryPoorRectF.left;
            if (mTrianglePaint.getColor() != mVeryPoorColor) {
                mTrianglePaint.setColor(mVeryPoorColor);
            }
        } else if (mCurrentCredit <= 550) {
            mTriangleCoodinate = (mCurrentCredit - 350) / 200 * (mPoorRectF.right - mPoorRectF.left) + mPoorRectF.left;
            if (mTrianglePaint.getColor() != mPoorColor) {
                mTrianglePaint.setColor(mPoorColor);
            }
        } else if (mCurrentCredit <= 650) {
            mTriangleCoodinate = (mCurrentCredit - 550) / 100 * (mFineRectF.right - mFineRectF.left) + mFineRectF.left;
            if (mTrianglePaint.getColor() != mFineColor) {
                mTrianglePaint.setColor(mFineColor);
            }
        } else if (mCurrentCredit <= 750) {
            mTriangleCoodinate = (mCurrentCredit - 650) / 100 * (mGoodRect.right - mGoodRect.left) + mGoodRect.left;
            if (mTrianglePaint.getColor() != mGoodColor) {
                mTrianglePaint.setColor(mGoodColor);
            }
        } else if (mCurrentCredit <= 950) {
            mTriangleCoodinate = (mCurrentCredit - 750) / 200 * (mVeryGoodRect.right - mVeryGoodRect.left) + mVeryGoodRect.left;
            if (mTrianglePaint.getColor() != mVeryGoodColor) {
                mTrianglePaint.setColor(mVeryGoodColor);
            }
        }

        mTrianglePath.reset();
        mTrianglePath.moveTo(mTriangleCoodinate - mSixDp / 2, 0);
        mTrianglePath.lineTo(mTriangleCoodinate, mSixDp);
        mTrianglePath.lineTo(mTriangleCoodinate + mSixDp / 2, 0);
        mTrianglePath.close();
    }

    private int dpToPx(float dp) {
        return DensityUtil.dp2px(getContext(), dp);
    }

}
