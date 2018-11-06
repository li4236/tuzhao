package com.tuzhao.publicwidget.map;

import com.amap.api.maps.model.LatLng;

/**
 * Created by yiyi.qi on 16/10/10.
 */

public interface ClusterItem {
    /**
     * 返回聚合元素的地理位置
     */
    LatLng getPosition();

    String getId();

    String getCancharge();

    boolean isparkspace();

    String getCity_code();

    String getPicture();

    String getAddress();

    String getName();

    double getPrice();

    double getGrade();

    int getFreeNumber();

}
