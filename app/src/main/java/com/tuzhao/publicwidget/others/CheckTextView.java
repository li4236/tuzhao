package com.tuzhao.publicwidget.others;

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
            if (mOnCheckChangeListener != null) {
                mOnCheckChangeListener.onCheckChange(!mIsCheck);
            } else {
                setChecked(!mIsCheck);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setChecked(boolean checked) {
        mIsCheck = checked;
        if (mIsCheck) {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.yuan_little_y2_all_5dp));
        } else {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.little_yuan_5dp_g10));
        }
    }

    public void setOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        mOnCheckChangeListener = onCheckChangeListener;
    }

    @Override
    public boolean isChecked() {
        return mIsCheck;
    }

    @Override
    public void toggle() {

    }

    public Drawable getCheckDrawable() {
        return mCheckDrawable;
    }

    public void setCheckDrawable(@DrawableRes int drawableId) {
        mCheckDrawable = ContextCompat.getDrawable(getContext(), drawableId);
    }

    public void setCheckDrawable(Drawable checkDrawable) {
        mCheckDrawable = checkDrawable;
    }

    public Drawable getNoCheckDrawble() {
        return mNoCheckDrawble;
    }

    public void setNoCheckDrawble(@DrawableRes int drawableId) {
        mNoCheckDrawble = ContextCompat.getDrawable(getContext(), drawableId);
    }

    public void setNoCheckDrawble(Drawable noCheckDrawble) {
        mNoCheckDrawble = noCheckDrawble;
    }

    public interface OnCheckChangeListener {
        void onCheckChange(boolean isCheck);
    }

}
