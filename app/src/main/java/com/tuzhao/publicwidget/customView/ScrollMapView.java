package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.amap.api.maps.MapView;

/**
 * Created by juncoder on 2018/9/30.
 */
public class ScrollMapView extends MapView {

    public ScrollMapView(Context context) {
        super(context);
    }

    public ScrollMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ScrollMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //让父布局不拦截Touch事件，交给自己处理
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }

}
