package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Checkable;

import com.tuzhao.R;

/**
 * Created by juncoder on 2018/4/13.
 */

public class CheckBox extends AppCompatTextView implements Checkable {

    private static final String TAG = "CheckBox";

    private boolean mIsCheck;

    private Drawable mCheckDrawable;

    private Drawable mNoCheckDrawble;

    private OnCheckChangeListener mOnCheckChangeListener;

    private OnCheckHandeListener mOnCheckHandeListener;

    public CheckBox(Context context) {
        this(context, null);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCheckDrawable = ContextCompat.getDrawable(context, R.drawable.ic_chose);
        mNoCheckDrawble = ContextCompat.getDrawable(context, R.drawable.ic_nochose);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果只是ActionDown就触发的话很容易误触
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //消费ActionDown事件，否则不会收到ActionUp事件
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP && inViewArea(event.getX(), event.getY())) {
            if (mOnCheckHandeListener == null || mOnCheckHandeListener.onCheckChange(!mIsCheck)) {
                setChecked(!mIsCheck);
                setDrawableStart();

                if (mOnCheckChangeListener != null) {
                    mOnCheckChangeListener.onCheckChange(mIsCheck);
                    return true;
                }

            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * @return 触摸的范围是否在View里面
     */
    private boolean inViewArea(float x, float y) {
        return x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight();
    }

    /**
     * 设置选择的效果，并不会触发OnCheckChangeListener
     */
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
