package com.tuzhao.publicwidget.others;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Checkable;

/**
 * Created by juncoder on 2018/4/13.
 */

public class CheckBox extends AppCompatTextView implements Checkable {

    private boolean mIsCheck;

    private Drawable mCheckDrawable;

    private Drawable mNoCheckDrawble;

    private OnCheckChangeListener mOnCheckChangeListener;

    private OnCheckHandeListener mOnCheckHandeListener;

    public CheckBox(Context context) {
        super(context);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mOnCheckHandeListener == null || mOnCheckHandeListener.onCheckChange(!mIsCheck)) {
                setChecked(!mIsCheck);
                setDrawableStart();

                if (mOnCheckChangeListener != null) {
                    mOnCheckChangeListener.onCheckChange(mIsCheck);
                }

            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mIsCheck != checked) {
            mIsCheck = checked;
            setDrawableStart();
        }
    }

    @Override
    public boolean isChecked() {
        return mIsCheck;
    }

    @Override
    public void toggle() {

    }

    private void setDrawableStart() {
        if (mIsCheck) {
            setCompoundDrawablesWithIntrinsicBounds(mCheckDrawable, null, null, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(mNoCheckDrawble, null, null, null);
        }
    }

    public Drawable getCheckDrawable() {
        return mCheckDrawable;
    }

    public void setCheckDrawable(Drawable checkDrawable) {
        mCheckDrawable = checkDrawable;
    }

    public Drawable getNoCheckDrawble() {
        return mNoCheckDrawble;
    }

    public void setNoCheckDrawble(Drawable noCheckDrawble) {
        mNoCheckDrawble = noCheckDrawble;
    }

    public OnCheckChangeListener getOnCheckChangeListener() {
        return mOnCheckChangeListener;
    }

    public void setOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        mOnCheckChangeListener = onCheckChangeListener;
    }

    public OnCheckHandeListener getOnCheckHandeListener() {
        return mOnCheckHandeListener;
    }

    public void setOnCheckHandeListener(OnCheckHandeListener onCheckHandeListener) {
        mOnCheckHandeListener = onCheckHandeListener;
    }

    public interface OnCheckChangeListener {

        void onCheckChange(boolean isCheck);

    }

    public interface OnCheckHandeListener {

        boolean onCheckChange(boolean isChecked);

    }

}
