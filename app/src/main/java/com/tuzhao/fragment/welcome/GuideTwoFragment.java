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
import com.tuzhao.fragment.base.BaseGuideFragment;
import com.tuzhao.utils.ImageUtil;

/**
 * Created by juncoder on 2018/4/28.
 */

public class GuideTwoFragment extends BaseGuideFragment {

    private ConstraintLayout mConstraintLayout;

    private ImageView mCharge;

    private ImageView mElectric;

    private ImageView mFloor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mConstraintLayout = (ConstraintLayout) inflater.inflate(R.layout.fragment_guide_two, container, false);
        mCharge = mConstraintLayout.findViewById(R.id.guide_two_charge);
        mElectric = mConstraintLayout.findViewById(R.id.guide_two_electric);
        mFloor = mConstraintLayout.findViewById(R.id.guide_two_floor);
        ImageUtil.showPic(mCharge, R.drawable.ic_charge);
        ImageUtil.showPic(mElectric, R.drawable.ic_electric);
        ImageUtil.showPic(mFloor, R.drawable.ic_floor2);
        ImageUtil.showPic((ImageView) mConstraintLayout.findViewById(R.id.guide_two_top), R.drawable.guide_two_top);
        ImageUtil.showPic((ImageView) mConstraintLayout.findViewById(R.id.guide_two_bottom), R.drawable.guide_two_bottom);
        return mConstraintLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCoorDinates();
    }

    private void setCoorDinates() {
        final ViewTreeObserver treeObserver = mConstraintLayout.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //测量出floor后把charge和electric移动到距离floor底部的高度的0.52的位置上
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(mConstraintLayout);
                int height = mFloor.getHeight();
                constraintSet.connect(R.id.guide_two_charge, ConstraintSet.BOTTOM, R.id.guide_two_floor, ConstraintSet.BOTTOM, (int) (height * 0.52));
                constraintSet.connect(R.id.guide_two_electric, ConstraintSet.BOTTOM, R.id.guide_two_floor, ConstraintSet.BOTTOM, (int) (height * 0.52));
                constraintSet.applyTo(mConstraintLayout);

                treeObserver.removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void isVisibilityToUser(boolean visibility) {
        if (visibility) {
            showDownAnimator(mCharge);
            showDownAnimator(mElectric);
            showUpAnimator(mFloor);
        } else {
            setViewAlpha(mElectric);
            setViewAlpha(mCharge);
            setViewAlpha(mFloor);
        }
    }

}
