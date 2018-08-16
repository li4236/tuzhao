package com.tuzhao.publicwidget.customView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;

import com.tuzhao.utils.ConstansUtil;

/**
 * Created by juncoder on 2018/8/11.
 */
public class ShapeDrawable extends android.graphics.drawable.ShapeDrawable {

    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        super.onDraw(shape, canvas, paint);
        paint.setColor(ConstansUtil.G10_COLOR);

    }

}
