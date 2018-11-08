package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Checkable;

import com.tuzhao.utils.ConstansUtil;

/**
 * Created by juncoder on 2018/11/7.
 */
public class CompatCheckTextView extends AppCompatTextView implements Checkable {

    private boolean mIsCheck;

    private int mCheckTextColor = ConstansUtil.B1_COLOR;

    private int mNoCheckTextColor = ConstansUtil.G6_COLOR;

    private Drawable mCheckDrawable;

    private Drawable mNoCheckDrawble;

    private CheckTextView.OnCheckChangeListener mOnCheckChangeListener;

    public CompatCheckTextView(Context context) {
        this(context, null);
    }

    public CompatCheckTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompatCheckTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        mIsCheck = checked;
        if (mIsCheck) {
            setTextColor(mCheckTextColor);
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mCheckDrawable, null);
        } else {
            setTextColor(mNoCheckTextColor);
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mNoCheckDrawble, null);
        }
    }

    @Override
    public boolean isChecked() {
        return mIsCheck;
    }

    @Override
    public void toggle() {
        setChecked(!mIsCheck);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP && inViewArea(event.getX(), event.getY())) {
            if (mOnCheckChangeListener != null) {
                mOnCheckChangeListener.onCheckChange(!mIsCheck);
            } else {
                setChecked(!mIsCheck);
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * @return 触摸的范围是否在View里面
     */
    private boolean inViewArea(float x, float y) {
        return x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight();
    }


    public void setCheckDrawable(@DrawableRes int drawableId) {
        if (mCheckDrawable == null) {
            mCheckDrawable = ContextCompat.getDrawable(getContext(), drawableId);
        }
    }

    public void setCheckDrawable(Drawable checkDrawable) {
        mCheckDrawable = checkDrawable;
    }

    public void setNoCheckDrawble(@DrawableRes int drawableId) {
        if (mNoCheckDrawble == null) {
            mNoCheckDrawble = ContextCompat.getDrawable(getContext(), drawableId);
        }
    }

    public void setNoCheckDrawble(Drawable noCheckDrawble) {
        mNoCheckDrawble = noCheckDrawble;
    }

    public void setCheckTextColor(int checkTextColor) {
        mCheckTextColor = checkTextColor;
    }

    public void setNoCheckTextColor(int noCheckTextColor) {
        mNoCheckTextColor = noCheckTextColor;
    }

    public void setOnCheckChangeListener(CheckTextView.OnCheckChangeListener onCheckChangeListener) {
        mOnCheckChangeListener = onCheckChangeListener;
    }
}
