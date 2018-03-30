package com.tianzhili.www.myselfsdk.filter.demobean;

import java.io.Serializable;
import java.util.List;

/**
 * 省
 */

public class ProvinceBean implements Serializable {
    int id;
    String name;
    String pid;
    List<CityBean> child;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<CityBean> getChild() {
        return child;
    }

    public void setChild(List<CityBean> child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "ProvinceBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", child=" + child +
                '}';
    }
}
