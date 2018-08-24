package com.tuzhao.publicwidget.map;

import android.graphics.drawable.Drawable;

/**
 * Created by yiyi.qi on 16/10/10.
 */

public interface ClusterRender {
    /**
     * 根据聚合点的元素数目返回渲染背景样式
     */
    Drawable getDrawable(int clusterNum, int type);
}
