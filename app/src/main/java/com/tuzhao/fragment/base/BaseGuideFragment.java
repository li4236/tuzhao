package com.tuzhao.fragment.base;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;

import com.tuzhao.utils.DensityUtil;

/**
 * Created by juncoder on 2018/4/28.
 */

public abstract class BaseGuideFragment extends BaseFragment {

    protected final String TAG = this.getClass().getName();

    public abstract void isVisibilityToUser(boolean visibility);

    /**
     * 显示向下移动并且透明度从0到1的动画
     */
    protected void showDownAnimator(ImageView imageView) {
        ObjectAnimator translationY = ObjectAnimator.ofFloat(imageView, "translationY", -DensityUtil.dp2px(getContext(), 108), 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView, "alpha", 0, 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(translationY, alpha);
        animatorSet.start();
    }

    /**
     * 显示向上移动并且透明度从0到1的动画
     */
    protected void showUpAnimator(ImageView imageView) {
        ObjectAnimator translationY = ObjectAnimator.ofFloat(imageView, "translationY", 360, 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView, "alpha", 0, 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(translationY, alpha);
        animatorSet.start();
    }

    protected void setViewAlpha(View view) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        objectAnimator.setDuration(300);
        objectAnimator.start();
    }

}
