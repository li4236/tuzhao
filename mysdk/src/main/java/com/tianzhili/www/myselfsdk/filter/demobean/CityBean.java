package com.tianzhili.www.myselfsdk.filter.demobean;

import java.io.Serializable;
import java.util.List;

/**
 * 市
 */

public class CityBean implements Serializable {
    int id;
    String name;
    String pid;
    List<AreaBean> child;

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

    public List<AreaBean> getChild() {
        return child;
    }

    public void setChild(List<AreaBean> child) {
        this.child = child;
    }
}
