package com.tuzhao.utils;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.tuzhao.R;
import com.tuzhao.activity.base.SuccessCallback;
import com.tuzhao.info.Pair;

/**
 * Created by juncoder on 2018/10/19.
 * <p>
 * https://juejin.im/post/5bc6ce54f265da0afd4b69cb#comment
 * </p>
 */
public class KeyboardHeightUtil extends PopupWindow {
    /**
     * The root activity that uses this KeyboardHeightProvider
     */
    private Activity activity;

    /**
     * The parent view
     */
    private View parentView;

    /**
     * The view that is used to calculate the keyboard height
     */
    private View popupView;

    /**
     * The keyboard height mCallback
     */
    private SuccessCallback<Pair<Integer, Integer>> mCallback;

    private Pair<Integer, Integer> mPair;

    private Point mPoint;

    private Rect mRect;

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    /**
     * Construct a new KeyboardHeightUtil
     *
     * @param activity The parent activity
     */
    public KeyboardHeightUtil(Activity activity) {
        super(activity);
        this.activity = activity;

        this.popupView = activity.getLayoutInflater().inflate(R.layout.layout_placeholder, null, false);
        setContentView(popupView);

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        parentView = activity.findViewById(android.R.id.content);

        setWidth(0);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);

        mPair = new Pair<>(0, 0);
        mPoint = new Point();
        mRect = new Rect();

        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (popupView != null) {
                    handleOnGlobalLayout();
                }
            }
        };
        popupView.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    /**
     * Start the KeyboardHeightProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    public void start() {
        if (!isShowing() && parentView.getWindowToken() != null) {
            setBackgroundDrawable(new ColorDrawable(0));
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    /**
     * Close the keyboard height provider,
     * this provider will not be used anymore.
     */
    public void close() {
        this.mCallback = null;
        popupView.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
        dismiss();
    }

    /**
     * Set the keyboard height mCallback to this provider. The
     * mCallback will be notified when the keyboard height has changed.
     * For example when the keyboard is opened or closed.
     *
     * @param observer The mCallback to be added to this provider.
     */
    public void setKeyboardHeightObserver(SuccessCallback<Pair<Integer, Integer>> observer) {
        this.mCallback = observer;
    }

    /**
     * Popup window itself is as big as the window of the Activity.
     * The keyboard can then be calculated by extracting the popup view bottom
     * from the activity window height.
     */
    private void handleOnGlobalLayout() {
        activity.getWindowManager().getDefaultDisplay().getSize(mPoint);
        popupView.getWindowVisibleDisplayFrame(mRect);

        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        int keyboardHeight = mPoint.y - mRect.bottom;
        if (mPair.getFirst() != keyboardHeight) {
            mPair.setFirst(keyboardHeight);
            mPair.setSecond(mPoint.y);
            notifyKeyboardHeightChanged();
        }
    }

    private void notifyKeyboardHeightChanged() {
        if (mCallback != null) {
            mCallback.onSuccess(mPair);
        }
    }

}
