package com.tuzhao.fragment.welcome;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuzhao.R;
import com.tuzhao.activity.SplashActivity;
import com.tuzhao.fragment.base.BaseGuideFragment;
import com.tuzhao.utils.ImageUtil;

/**
 * Created by juncoder on 2018/5/4.
 */

public class GuideThreeFragment extends BaseGuideFragment {

    private ConstraintLayout mConstraintLayout;

    private ImageView mPark;

    private ImageView mCar;

    private ImageView mFloor;

    private TextView mStartUse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mConstraintLayout = (ConstraintLayout) inflater.inflate(R.layout.fragment_guide_three, container, false);
        mPark = mConstraintLayout.findViewById(R.id.guide_three_park);
        mCar = mConstraintLayout.findViewById(R.id.guide_three_car);
        mFloor = mConstraintLayout.findViewById(R.id.guide_three_floor);
        mStartUse = mConstraintLayout.findViewById(R.id.guide_three_start_use);

        ImageUtil.showPic(mPark, R.drawable.ic_way);
        ImageUtil.showPic(mCar, R.drawable.ic_find3);
        ImageUtil.showPic(mFloor, R.drawable.ic_floor3);
        ImageUtil.showPic((ImageView) mConstraintLayout.findViewById(R.id.guide_three_top), R.drawable.ic_advertisement5);
        ImageUtil.showPic((ImageView) mConstraintLayout.findViewById(R.id.guide_three_bottom), R.drawable.ic_advertisement6);

        mStartUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterMainActivity();
            }
        });
        return mConstraintLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCoordinates();
    }

    private void setCoordinates() {
        final ViewTreeObserver treeObserver = mConstraintLayout.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(mConstraintLayout);
                int height = mFloor.getHeight();
                constraintSet.connect(R.id.guide_three_park, ConstraintSet.BOTTOM, R.id.guide_three_floor, ConstraintSet.BOTTOM, (int) (height * 0.058));
                constraintSet.connect(R.id.guide_three_car, ConstraintSet.BOTTOM, R.id.guide_three_floor, ConstraintSet.BOTTOM, (int) (height * 0.07));
                constraintSet.applyTo(mConstraintLayout);

                showDownAnimator(mPark);
                showDownAnimator(mCar);
                showUpAnimator(mFloor);
                mStartUse.setVisibility(View.VISIBLE);
                setStartUseAlpha();

                treeObserver.removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void isVisibilityToUser(boolean visibility) {
        if (visibility) {
            showDownAnimator(mPark);
            showDownAnimator(mCar);
            showUpAnimator(mFloor);
            mStartUse.setVisibility(View.VISIBLE);
            setStartUseAlpha();
        } else {
            //如果不可见则把图片隐藏，否则第二次执行移动动画的时候会看到图片消失再执行动画
            setViewAlpha(mPark);
            setViewAlpha(mCar);
            setViewAlpha(mFloor);
            setViewAlpha(mStartUse);
            mStartUse.setVisibility(View.INVISIBLE);
        }
    }

    protected void setStartUseAlpha() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mStartUse, "alpha", 0, 1);
        objectAnimator.setDuration(1500);
        objectAnimator.start();
    }

    private void enterMainActivity() {
        Intent intent = new Intent(getActivity(), SplashActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            SharedPreferences activityPreferences = getActivity().getSharedPreferences("tuzhaoapp", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = activityPreferences.edit();
            editor.putBoolean("first_open", true);
            editor.apply();
            getActivity().finish();
        }
    }

}
