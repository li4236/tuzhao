package com.tuzhao.publicwidget.map;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiyi.qi on 16/10/10.
 */

public class Cluster {

    private LatLng mLatLng;
    private List<ClusterItem> mClusterItems;
    private Marker mMarker;
    private boolean mAggregation;

    Cluster(LatLng latLng) {
        mLatLng = latLng;
        mClusterItems = new ArrayList<ClusterItem>();
    }

    void addClusterItem(ClusterItem clusterItem) {
        mClusterItems.add(clusterItem);
    }

    int getClusterCount() {
        return mClusterItems.size();
    }

    LatLng getCenterLatLng() {
        return mLatLng;
    }

    void setCenterLatLng(LatLng latLng) {
        mLatLng = latLng;
    }

    void setMarker(Marker marker) {
        mMarker = marker;
    }

    Marker getMarker() {
        return mMarker;
    }

    List<ClusterItem> getClusterItems() {
        return mClusterItems;
    }

    public boolean isAggregation() {
        return mAggregation;
    }

    public void setAggregation(boolean aggregation) {
        mAggregation = aggregation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LatLng latLng = ((Cluster) o).getCenterLatLng();

        return mLatLng.latitude == latLng.latitude && mLatLng.longitude == latLng.longitude;
    }

    @Override
    public int hashCode() {
        return mLatLng.hashCode();
    }
}
