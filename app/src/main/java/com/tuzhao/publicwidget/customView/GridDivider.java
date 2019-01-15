package com.tuzhao.publicwidget.customView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by juncoder on 2018/8/11.
 */
public class GridDivider extends RecyclerView.ItemDecoration {

    private Paint mPaint;

    public GridDivider() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.parseColor("#cccccc"));
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        View view = parent.getChildAt(0);
        int width = view.getWidth();
        int heigh = 0;
        for (int i = 0; i < 4; i++) {
            //画横线
            c.drawLine(0, heigh, parent.getWidth(), heigh + mPaint.getStrokeWidth(), mPaint);
            heigh += view.getHeight();
        }

        //画竖线
        c.drawLine(width, mPaint.getStrokeWidth(), width + mPaint.getStrokeWidth(), parent.getHeight(), mPaint);
        width += view.getWidth();
        c.drawLine(width, mPaint.getStrokeWidth(), width + mPaint.getStrokeWidth(), parent.getHeight(), mPaint);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }
}
