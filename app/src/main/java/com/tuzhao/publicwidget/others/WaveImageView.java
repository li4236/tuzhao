package com.tuzhao.publicwidget.others;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.WindowManager;

import com.tuzhao.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juncoder on 2018/4/17.
 */

public class WaveImageView extends AppCompatImageView {

    private Paint mPaint;

    private Path mPath;

    private List<Point> mPoints;

    private Point mScreenPoint;

    public WaveImageView(Context context) {
        super(context);
        init();
    }

    public WaveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#69ffffff"));

        mPath = new Path();
        mPoints = new ArrayList<>();
        mScreenPoint = new Point(1080, 1920);
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getRealSize(mScreenPoint);
        }
        mScreenPoint.y = DensityUtil.dp2px(getContext(), 87);

        mPoints.add(new Point(dpToPx(100), dpToPx(59)));
        mPoints.add(new Point(dpToPx(150), dpToPx(76)));
        mPoints.add(new Point(dpToPx(221), dpToPx(93)));
        mPoints.add(new Point(dpToPx(270), dpToPx(69)));
        mPoints.add(new Point(dpToPx(317), dpToPx(44)));
        mPoints.add(new Point(dpToPx(359), dpToPx(74)));
        mPoints.add(new Point(dpToPx(400), dpToPx(93)));
        mPoints.add(new Point(dpToPx(415), dpToPx(70)));

       /* mPoints.add(new Point(dpToPx(26), dpToPx(56)));
        mPoints.add(new Point(dpToPx(80), dpToPx(74)));
        mPoints.add(new Point(dpToPx(158), dpToPx(91)));
        mPoints.add(new Point(dpToPx(193), dpToPx(66)));
        mPoints.add(new Point(dpToPx(240), dpToPx(37)));
        mPoints.add(new Point(dpToPx(295), dpToPx(76)));
        mPoints.add(new Point(dpToPx(341), dpToPx(86)));
        mPoints.add(new Point(dpToPx(362), dpToPx(72)));
        mPoints.add(new Point(dpToPx(387), dpToPx(62)));
        mPoints.add(new Point(dpToPx(422), dpToPx(80)));*/

        mPoints.add(new Point(dpToPx(35), dpToPx(56)));
        mPoints.add(new Point(dpToPx(80), dpToPx(77)));
        mPoints.add(new Point(dpToPx(158), dpToPx(94)));
        mPoints.add(new Point(dpToPx(193), dpToPx(70)));
        mPoints.add(new Point(dpToPx(240), dpToPx(45)));
        mPoints.add(new Point(dpToPx(295), dpToPx(77)));
        mPoints.add(new Point(dpToPx(341), dpToPx(89)));
        mPoints.add(new Point(dpToPx(362), dpToPx(75)));
        mPoints.add(new Point(dpToPx(387), dpToPx(65)));
        mPoints.add(new Point(dpToPx(422), dpToPx(83)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        mPath.moveTo(dpToPx(76), dpToPx(87));
        for (int i = 0; i < 8; i += 2) {
            mPath.quadTo(mPoints.get(i).x, mPoints.get(i).y, mPoints.get(i + 1).x, mPoints.get(i + 1).y);
        }
        mPath.lineTo(mScreenPoint.x, dpToPx(87));
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        mPath.reset();
        mPath.moveTo(0, dpToPx(87));
        for (int i = 8; i <= mPoints.size() - 2; i += 2) {
            mPath.quadTo(mPoints.get(i).x, mPoints.get(i).y, mPoints.get(i + 1).x, mPoints.get(i + 1).y);
        }
        mPath.lineTo(mScreenPoint.x, dpToPx(87));
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    private int dpToPx(int dpValue) {
        return DensityUtil.dp2px(getContext(), dpValue);
    }

}
