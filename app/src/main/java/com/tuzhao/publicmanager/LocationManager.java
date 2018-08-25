package com.tuzhao.publicmanager;

import com.amap.api.location.AMapLocation;

/**
 * Created by TZL13 on 2017/7/5.
 * 单例，用来管理用户信息
 */

public class LocationManager {

    private static LocationManager locationManager = null;
    private AMapLocation mAmapLocation = null;//用来显示用户当前位置的综合信息

    public static LocationManager getInstance(){

        if (locationManager == null) {

            synchronized (LocationManager.class) {

                if (locationManager == null) {

                    locationManager = new LocationManager();
                }
                return locationManager;
            }
        } else {

            return locationManager;
        }
    }

    public boolean hasLocation() {
        return mAmapLocation != null ;
    }

    public AMapLocation getmAmapLocation() {
        return mAmapLocation;
    }

    public void setmAmapLocation(AMapLocation mAmapLocation) {
        this.mAmapLocation = mAmapLocation;
    }
}
