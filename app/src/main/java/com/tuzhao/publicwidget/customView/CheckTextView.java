package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Checkable;

import com.tuzhao.R;

/**
 * Created by juncoder on 2018/3/27.
 */

public class CheckTextView extends AppCompatTextView implements Checkable {

    private boolean mIsCheck;

    private Drawable mCheckDrawable;

    private Drawable mNoCheckDrawble;

    private OnCheckChangeListener mOnCheckChangeListener;

    public CheckTextView(Context context) {
        super(context);
    }

    public CheckTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    @Override
    public void setChecked(boolean checked) {
        mIsCheck = checked;
        if (mIsCheck) {
            if (mCheckDrawable == null) {
                setBackground(ContextCompat.getDrawable(getContext(), R.drawable.solid_y2_corner_5dp));
            } else {
                setBackground(mCheckDrawable);
            }
        } else {
            if (mNoCheckDrawble == null) {
                setBackground(ContextCompat.getDrawable(getContext(), R.drawable.solid_g10_corner_5dp));
            } else {
                setBackground(mNoCheckDrawble);
            }
        }
    }

    public void setOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        mOnCheckChangeListener = onCheckChangeListener;
    }

    public OnCheckChangeListener getOnCheckChangeListener() {
        return mOnCheckChangeListener;
    }

    @Override
    public boolean isChecked() {
        return mIsCheck;
    }

    @Override
    public void toggle() {
        setChecked(!mIsCheck);
    }

    public Drawable getCheckDrawable() {
        return mCheckDrawable;
    }

    public void setCheckDrawable(@DrawableRes int drawableId) {
        if (mCheckDrawable == null) {
            mCheckDrawable = ContextCompat.getDrawable(getContext(), drawableId);
        }
    }

    public void setCheckDrawable(Drawable checkDrawable) {
        mCheckDrawable = checkDrawable;
    }

    public Drawable getNoCheckDrawble() {
        return mNoCheckDrawble;
    }

    public void setNoCheckDrawble(@DrawableRes int drawableId) {
        if (mNoCheckDrawble == null) {
            mNoCheckDrawble = ContextCompat.getDrawable(getContext(), drawableId);
        }
    }

    public void setNoCheckDrawble(Drawable noCheckDrawble) {
        mNoCheckDrawble = noCheckDrawble;
    }

    public interface OnCheckChangeListener {
        void onCheckChange(boolean isCheck);
    }

}
