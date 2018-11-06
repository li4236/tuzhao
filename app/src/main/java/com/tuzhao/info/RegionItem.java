package com.tuzhao.info;

import com.amap.api.maps.model.LatLng;
import com.tuzhao.publicwidget.map.ClusterItem;

import java.util.Objects;

/**
 * Created by yiyi.qi on 16/10/10.
 */

public class RegionItem implements ClusterItem {
    private LatLng mLatLng;
    private String id;
    private String cancharge;
    private boolean isparkspace;
    private String city_code;
    private String picture;//车场或电站图片
    private String address;//车场或电站地址
    private String name;//车场或电站名
    private double price;//车场或电站价格
    private double grade;//车场或电站评分
    private int freeNumber;//空闲车位数量

    public RegionItem( String id,LatLng latLng, String cancharge, boolean isparkspace,String city_code,String picture,String address,
                       String name,double price,double grade,int freeNumber) {
        mLatLng=latLng;
        this.id=id;
        this.cancharge=cancharge;
        this.isparkspace=isparkspace;
        this.city_code = city_code;
        this.picture = picture;
        this.address = address;
        this.name = name;
        this.price = price;
        this.grade = grade;
        this.freeNumber = freeNumber;
    }

    @Override
    public LatLng getPosition() {
        return mLatLng;
    }
    @Override
    public String getId() {
        return id;
    }
    @Override
    public String getCancharge() {
        return cancharge;
    }
    @Override
    public boolean isparkspace() {
        return isparkspace;
    }

    public String getCity_code() {
        return city_code;
    }

    public String getPicture() {
        return picture;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getGrade() {
        return grade;
    }

    public int getFreeNumber() {
        return freeNumber;
    }

    public void setFreeNumber(int freeNumber) {
        this.freeNumber = freeNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionItem item = (RegionItem) o;
        return isparkspace == item.isparkspace &&
                Objects.equals(id, item.id) &&
                Objects.equals(cancharge, item.cancharge);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, cancharge, isparkspace);
    }
}
