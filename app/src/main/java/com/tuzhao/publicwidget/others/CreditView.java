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
import com.tuzhao.utils.DensityUtil;

/**
 * Created by juncoder on 2018/6/27.
 */
public class CreditView extends View {

    private Paint mFlagPaint;

    private RectF mFlagRectF;

    private Path mFlagPath;

    private int mFlagColor;

    private Paint mCreditPaint;

    private RectF mVeryPoorRectF;

    private RectF mPoorRectF;

    private RectF mFineRectF;

    private RectF mGoodRect;

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

    private String mZero = "0";

    private String mThreeHundredAndFifty;

    private String mFiveHundredAndFifty;

    private String mSixtyHundredAndFifty;

    private String mSevenHundredAndFifty;

    private String mNineHundredAndFifty;

    private int mHeight;

    public CreditView(Context context) {
        super(context);
        mFlagColor = Color.parseColor("#ffa830");
        mVeryPoorColor = Color.parseColor("#ff3052");
        mPoorColor = Color.parseColor("#ffcc30");
        mFineColor = Color.parseColor("#ffa830");
        mGoodColor = Color.parseColor("#3dd6fa");
        mVeryPoorColor = Color.parseColor("#14d499");
        mTextColor = Color.parseColor("#323232");
        mTextSize = (int) DensityUtil.sp2px(context, 8);
        init();
    }

    public CreditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs, 0);
        init();
    }

    public CreditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mFlagPaint = new Paint();
        mFlagPaint.setAntiAlias(true);
        mFlagPaint.setStyle(Paint.Style.FILL);
        mFlagPaint.setColor(mFlagColor);

        mFlagRectF = new RectF();
        mFlagPath = new Path();

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
        mFlagColor = typedArray.getColor(R.styleable.CreditView_flag_color, Color.parseColor("#ffa830"));
        mVeryPoorColor = typedArray.getColor(R.styleable.CreditView_poor_color, Color.parseColor("#ff3052"));
        mPoorColor = typedArray.getColor(R.styleable.CreditView_poor_color, Color.parseColor("#ffcc30"));
        mFineColor = typedArray.getColor(R.styleable.CreditView_fine_color, Color.parseColor("#ffa830"));
        mGoodColor = typedArray.getColor(R.styleable.CreditView_good_color, Color.parseColor("#3dd6fa"));
        mVeryGoodColor = typedArray.getColor(R.styleable.CreditView_very_good_color, Color.parseColor("#14d499"));
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

        mVeryPoorRectF.left = mTextSize / 2;
        mVeryPoorRectF.top = dpToPx(8);
        mVeryPoorRectF.right = (float) (150 / 750.0 * width + mVeryPoorRectF.left);
        mVeryPoorRectF.bottom = mVeryPoorRectF.top + mFourDp;

        mPoorRectF.left = mVeryPoorRectF.right + mSixDp;
        mPoorRectF.top = mVeryPoorRectF.top;
        mPoorRectF.right = (float) (mPoorRectF.left + 200 / 750.0 * width);
        mPoorRectF.bottom = mVeryPoorRectF.bottom;

        mFineRectF.left = mPoorRectF.right + mSixDp;
        mFineRectF.top = mVeryPoorRectF.top;
        mFineRectF.bottom = mVeryPoorRectF.bottom;
        mFineRectF.right = (float) (mFineRectF.left + 100 / 750.0 * width);

        mGoodRect.left = mFineRectF.right + mSixDp;
        mGoodRect.top = mVeryPoorRectF.top;
        mGoodRect.bottom = mVeryPoorRectF.bottom;
        mGoodRect.right = (float) (mGoodRect.left + 100 / 750.0 * width);

        mVeryGoodRect.left = mGoodRect.right + mSixDp;
        mVeryGoodRect.right = (float) (mVeryGoodRect.left + 200 / 750.0 * width);
        mVeryGoodRect.top = mVeryPoorRectF.top;
        mVeryGoodRect.bottom = mVeryPoorRectF.bottom;

        mHeight = dpToPx(16) + mTextSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        caculateFlag();
        canvas.drawRect(mFlagRectF, mFlagPaint);
        canvas.drawPath(mFlagPath, mFlagPaint);

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
     * 用于动画更新
     */
    public void setCurrentCredit(float credit) {
        if (credit >= 0) {
            mCurrentCredit = credit;
        }
        invalidate();
    }

    private void caculateFlag() {
        if (mCurrentCredit <= 350) {
            if (mCurrentCredit < 200) {
                mCurrentCredit = 200;
            }

            mFlagRectF.left = (mCurrentCredit - 200) / 150 * (mVeryPoorRectF.right - mVeryPoorRectF.left) + mVeryPoorRectF.left;
            if (mFlagPaint.getColor() != mVeryPoorColor) {
                mFlagPaint.setColor(mVeryPoorColor);
            }
        } else if (mCurrentCredit <= 550) {
            mFlagRectF.left = (mCurrentCredit - 350) / 200 * (mPoorRectF.right - mPoorRectF.left) + mPoorRectF.left;
            if (mFlagPaint.getColor() != mPoorColor) {
                mFlagPaint.setColor(mPoorColor);
            }
        } else if (mCurrentCredit <= 650) {
            mFlagRectF.left = (mCurrentCredit - 550) / 100 * (mFineRectF.right - mFineRectF.left) + mFineRectF.left;
            if (mFlagPaint.getColor() != mFineColor) {
                mFlagPaint.setColor(mFineColor);
            }
        } else if (mCurrentCredit <= 750) {
            mFlagRectF.left = (mCurrentCredit - 650) / 100 * (mGoodRect.right - mGoodRect.left) + mGoodRect.left;
            if (mFlagPaint.getColor() != mGoodColor) {
                mFlagPaint.setColor(mGoodColor);
            }
        } else if (mCurrentCredit <= 950) {
            mFlagRectF.left = (mCurrentCredit - 750) / 200 * (mVeryGoodRect.right - mVeryGoodRect.left) + mVeryGoodRect.left;
            if (mFlagPaint.getColor() != mVeryGoodColor) {
                mFlagPaint.setColor(mVeryGoodColor);
            }
        }
        mFlagRectF.right = mFlagRectF.left + dpToPx(1);
        mFlagRectF.bottom = dpToPx(8);

        mFlagPath.reset();
        mFlagPath.moveTo(mFlagRectF.right, 0);
        mFlagPath.lineTo(mFlagRectF.right + mFourDp, mFourDp / 2);
        mFlagPath.lineTo(mFlagRectF.right, mFourDp);
        mFlagPath.close();
    }

    private int dpToPx(float dp) {
        return DensityUtil.dp2px(getContext(), dp);
    }

}
