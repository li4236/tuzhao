package com.tuzhao.fragment.welcome;

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

import com.tuzhao.R;
import com.tuzhao.activity.base.BaseGuideFragment;
import com.tuzhao.utils.ImageUtil;

/**
 * Created by juncoder on 2018/4/27.
 */

public class GuideOneFragment extends BaseGuideFragment {

    private ConstraintLayout mConstraintLayout;

    private ImageView mPark;

    private ImageView mCar;

    private ImageView mFloor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mConstraintLayout = (ConstraintLayout) inflater.inflate(R.layout.fragment_guide_one, container, false);
        mPark = mConstraintLayout.findViewById(R.id.guide_one_park);
        mCar = mConstraintLayout.findViewById(R.id.guide_one_car);
        mFloor = mConstraintLayout.findViewById(R.id.guide_one_floor);
        ImageUtil.showPic(mPark, R.drawable.ic_park3);
        ImageUtil.showPic(mCar, R.drawable.ic_car3);
        ImageUtil.showPic(mFloor, R.drawable.ic_floor1);
        ImageUtil.showPic((ImageView) mConstraintLayout.findViewById(R.id.guide_one_top), R.drawable.guide_one_top);
        ImageUtil.showPic((ImageView) mConstraintLayout.findViewById(R.id.guide_one_bottom), R.drawable.guide_one_bottom);
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
                //测量出floor后把park和car移动到距离floor底部的高度的0.52的位置上，再执行动画
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(mConstraintLayout);
                int height = mFloor.getHeight();
                constraintSet.connect(R.id.guide_one_park, ConstraintSet.BOTTOM, R.id.guide_one_floor, ConstraintSet.BOTTOM, (int) (height * 0.52));
                constraintSet.connect(R.id.guide_one_car, ConstraintSet.BOTTOM, R.id.guide_one_floor, ConstraintSet.BOTTOM, (int) (height * 0.52));
                constraintSet.applyTo(mConstraintLayout);

                showDownAnimator(mPark);
                showDownAnimator(mCar);
                showUpAnimator(mFloor);

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
        } else {
            //如果不可见则把图片隐藏，否则第二次执行移动动画的时候会看到图片消失再执行动画
            setViewAlpha(mPark);
            setViewAlpha(mCar);
            setViewAlpha(mFloor);
        }
    }

}
