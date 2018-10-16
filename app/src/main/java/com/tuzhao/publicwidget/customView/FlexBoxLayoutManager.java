package com.tuzhao.publicwidget.customView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by juncoder on 2018/10/15.
 */
public class FlexBoxLayoutManager extends RecyclerView.LayoutManager {

    @Override
    public boolean isAutoMeasureEnabled() {
        return true;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0 && state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);

        int startOffset = getPaddingStart();    //子view对应的left坐标
        int topOffset = getPaddingTop();        //子view对应的top坐标

        for (int i = 0; i < state.getItemCount(); i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            int viewWidthUse = getDecoratedMeasuredWidth(view) + layoutParams.leftMargin + layoutParams.rightMargin;
            int viewHightUse = getDecoratedMeasuredHeight(view) + layoutParams.topMargin + layoutParams.bottomMargin;
            int left, top, right, bottom;

            left = startOffset;
            right = left + viewWidthUse;

            if (right > getWidth()) {
                //显示不下，换行
                topOffset +=viewHightUse;
                startOffset = getPaddingStart();

                left = startOffset;
                right = left + viewWidthUse;
            }

            top = topOffset;
            bottom = top + viewHightUse;

            startOffset = right;

            //四个坐标代表了view的整体占用的空间，包括margin的
            layoutDecoratedWithMargins(view, left, top, right, bottom);
        }

    }

}
