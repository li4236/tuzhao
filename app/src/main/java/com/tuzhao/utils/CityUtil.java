package com.tuzhao.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuzhao.info.CityInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by juncoder on 2018/3/30.
 */

public class CityUtil {

    //读取方法
    private static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 解析357个城市数据
     */
    public static ArrayList<CityInfo> loadCityData(Context context) {
        String cityJson = getJson(context, "china_city_data.json");
        Type type = new TypeToken<ArrayList<CityInfo>>() {
        }.getType();

        return new Gson().fromJson(cityJson, type);
    }

}
