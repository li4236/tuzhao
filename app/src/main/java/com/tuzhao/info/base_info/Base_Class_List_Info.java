package com.tuzhao.info.base_info;

import java.util.ArrayList;

/**
 * Created by 汪何龙 on 2017/4/29.
 * 停车场数据实体的基类
 */

public class Base_Class_List_Info<T> extends BaseInfo {

    public String code;
    public String time;
    public ArrayList<T> data;

    @Override
    public String toString() {
        return "Base_Class_List_Info{" +
                "code='" + code + '\'' +
                ", time='" + time + '\'' +
                ", data=" + data +
                '}';
    }
}
