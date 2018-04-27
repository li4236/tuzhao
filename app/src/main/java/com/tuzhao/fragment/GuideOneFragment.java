package com.tuzhao.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.tuzhao.R;
import com.tuzhao.utils.DensityUtil;

/**
 * Created by juncoder on 2018/4/27.
 */

public class GuideOneFragment extends Fragment {

    private static final String TAG = "GuideOneFragment";

    private ConstraintLayout mConstraintLayout;

    private ImageView mPark;

    private ImageView mCar;

    private ImageView mFloor;

    private boolean mCanPlay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mConstraintLayout = (ConstraintLayout) inflater.inflate(R.layout.fragment_guide_one, container, false);
        mPark = mConstraintLayout.findViewById(R.id.guide_one_park);
        mCar = mConstraintLayout.findViewById(R.id.guide_one_car);
        mFloor = mConstraintLayout.findViewById(R.id.guide_one_floor);
        return mConstraintLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCoorDinates();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    private void setCoorDinates() {
        final ViewTreeObserver treeObserver = mConstraintLayout.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(mConstraintLayout);
                int height = mFloor.getHeight();
                constraintSet.connect(R.id.guide_one_park, ConstraintSet.BOTTOM, R.id.guide_one_floor, ConstraintSet.BOTTOM, (int) (height * 0.52));
                constraintSet.connect(R.id.guide_one_car, ConstraintSet.BOTTOM, R.id.guide_one_floor, ConstraintSet.BOTTOM, (int) (height * 0.52));
                constraintSet.applyTo(mConstraintLayout);

                showDownAnimator(mPark);
                showDownAnimator(mCar);
                showUpAnimator(mFloor);

                Log.e(TAG, "onGlobalLayout: ");
                treeObserver.removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void showDownAnimator(ImageView imageView) {
        ObjectAnimator translationY = ObjectAnimator.ofFloat(imageView, "translationY", -DensityUtil.dp2px(getContext(), 108), 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView, "alpha", 0, 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(translationY, alpha);
        animatorSet.start();
    }

    private void showUpAnimator(ImageView imageView) {
        ObjectAnimator translationY = ObjectAnimator.ofFloat(imageView, "translationY", 360, 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView, "alpha", 0, 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(translationY, alpha);
        animatorSet.start();
    }

}
