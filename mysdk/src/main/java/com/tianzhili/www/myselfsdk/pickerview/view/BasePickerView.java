package com.tianzhili.www.myselfsdk.pickerview.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.tianzhili.www.myselfsdk.R;
import com.tianzhili.www.myselfsdk.pickerview.listener.OnDismissListener;
import com.tianzhili.www.myselfsdk.pickerview.utils.PickerViewAnimateUtil;


/**
 * Created by Sai on 15/11/22.
 * 精仿iOSPickerViewController控件
 */
public class BasePickerView {
    private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
    );

    private Context context;
    protected ViewGroup contentContainer;
    private ViewGroup decorView;//activity的根View
    private ViewGroup rootView;//附加View 的 根View

    private OnDismissListener onDismissListener;
    private boolean isDismissing;

    private Animation outAnim;
    private Animation inAnim;
    private int gravity = Gravity.BOTTOM;

    private boolean autoDismiss = true;

    private boolean mIsShowing;

    public BasePickerView(Context context) {
        this.context = context;

        initViews();
        init();
        initEvents();
    }

    protected void initViews() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        decorView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = (ViewGroup) layoutInflater.inflate(R.layout.layout_basepickerview, decorView, false);
        rootView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        contentContainer = rootView.findViewById(R.id.content_container);
        contentContainer.setLayoutParams(params);
    }

    protected void init() {
        inAnim = getInAnimation();
        outAnim = getOutAnimation();
    }

    private void initEvents() {
        //消失动画
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isDismissing = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //从activity根视图移除
                decorView.removeView(rootView);
                mIsShowing = false;
                isDismissing = false;
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(BasePickerView.this);
                }
                /*decorView.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });*/
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * show的时候调用
     *
     * @param view 这个View
     */
    private void onAttached(View view) {
        decorView.addView(view);
        contentContainer.startAnimation(inAnim);
    }

    /**
     * 添加这个View到Activity的根视图
     */
    public void show() {
        if (isShowing()) {
            return;
        }
        onAttached(rootView);
        mIsShowing = true;
    }

    /**
     * 检测该View是不是已经添加到根视图
     *
     * @return 如果视图已经存在该View返回true
     */
    public boolean isShowing() {
        //如果一个Activity使用了两个该控件，用下面的方法检测会不准确
        /*View view = decorView.findViewById(R.id.outmost_container);
        return view != null;*/
        return mIsShowing;
    }

    public void dismiss() {
        if (!autoDismiss) {
            return;
        }

        if (isDismissing) {
            return;
        }

        contentContainer.startAnimation(outAnim);
    }

    /**
     * 强制关闭
     */
    public void forceDismiss() {
        if (isDismissing) {
            return;
        }
        contentContainer.startAnimation(outAnim);
    }

    public Animation getInAnimation() {
        int res = PickerViewAnimateUtil.getAnimationResource(this.gravity, true);
        return AnimationUtils.loadAnimation(context, res);
    }

    public Animation getOutAnimation() {
        int res = PickerViewAnimateUtil.getAnimationResource(this.gravity, false);
        return AnimationUtils.loadAnimation(context, res);
    }

    public BasePickerView setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public BasePickerView setCancelable(boolean isCancelable) {
        View view = rootView.findViewById(R.id.outmost_container);

        if (isCancelable) {
            view.setOnTouchListener(onCancelableTouchListener);
        } else {
            view.setOnTouchListener(null);
        }
        return this;
    }

    /**
     * Called when the user touch on black overlay in order to dismiss the dialog
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
            }
            return false;
        }
    };

    public <T extends View> T findViewById(int id) {
        return contentContainer.findViewById(id);
    }

    public boolean isAutoDismiss() {
        return autoDismiss;
    }

    public void setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
    }
}
