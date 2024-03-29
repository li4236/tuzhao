package com.tuzhao.publicwidget.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tuzhao.application.MyApplication;
import com.tuzhao.info.Search_Address_Info;
import com.tuzhao.info.User_Info;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class DatabaseImp {

    private Database database;

    public DatabaseImp(Context context) {
        database = Database.getInstance(context);
    }

    public void setRegistrationId(String registrationId) {
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.REGISTRATION_ID, registrationId);
        Cursor cursor = sqLiteDatabase.query(Database.TB_JI_GUANG, null, null, null, null, null,
                "_id desc");
        if (cursor.moveToFirst()) {
            //如果有记录了的就更新
            sqLiteDatabase.update(Database.TB_JI_GUANG, contentValues, null, null);
        } else {
            //没有记录的就插入一条新记录
            sqLiteDatabase.insert(Database.TB_JI_GUANG, null, contentValues);
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    public String getRegistrationId() {
        SQLiteDatabase sqLiteDatabase = database.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(Database.TB_JI_GUANG, null, null, null, null, null,
                "_id desc");
        String registrationId;
        if (cursor.moveToFirst()) {
            //如果数据库有保存的则获取数据库的
            registrationId = cursor.getString(cursor.getColumnIndex(Database.REGISTRATION_ID));
        } else {
            //数据库没有保存则通过极光推送获取
            registrationId = JPushInterface.getRegistrationID(MyApplication.getInstance());
            setRegistrationId(registrationId);
        }
        cursor.close();
        sqLiteDatabase.close();
        if (null == registrationId || "".equals(registrationId)) {
            //ios说它可能获取不到，则登录的时候传-1
            return "-1";
        }
        return registrationId;
    }

    /**
     * 从本地读取数据库读取
     */
    public User_Info getUserFormDatabase() {
        SQLiteDatabase db = database.getReadableDatabase();
        String sql = "select * from tb_user";
        Cursor cursor = db.rawQuery(sql, null);
        User_Info user_info = new User_Info();
        while (cursor.moveToNext()) {
            user_info.setId(cursor.getString(cursor.getColumnIndex("_id")));
            user_info.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            user_info.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user_info.setBalance(cursor.getString(cursor.getColumnIndex("balance")));
            user_info.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
            user_info.setImg_url(cursor.getString(cursor.getColumnIndex("img_url")));
            user_info.setAutologin(cursor.getString(cursor.getColumnIndex("autologin")));
        }

        cursor.close();
        db.close();
        return user_info;
    }

    /**
     * 向本地数据添加用户数据
     */
    public void insertUserToDatabase(User_Info user_info) {
        SQLiteDatabase db = database.getWritableDatabase();
        db.delete("tb_user", null, null);//清空表中的内容
        String sql = "insert into tb_user(_id,username,password,balance,nickname,img_url,autologin)" + "values(?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{user_info.getId(), user_info.getUsername(), user_info.getPassword(), user_info.getBalance(), user_info.getNickname(), user_info.getImg_url(), user_info.getAutologin()});
        db.close();
    }

    /**
     * 从本地读取搜索记录
     */
    public List<Search_Address_Info> getSearchLog() {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.query("tb_search_log", null, null, null, null, null, "time desc");

        List<Search_Address_Info> list = new ArrayList<Search_Address_Info>();

        boolean haveNext = cursor.moveToFirst();
        Search_Address_Info info;
        while (haveNext) {
            info = new Search_Address_Info();
            info.set_id(cursor.getString(cursor.getColumnIndex("_id")));
            info.setKeyword(cursor.getString(cursor.getColumnIndex("keyword")));
            info.setLatitude(Double.valueOf(cursor.getString(cursor.getColumnIndex("lat"))));
            info.setLongitude(Double.valueOf(cursor.getString(cursor.getColumnIndex("lon"))));
            info.setCitycode(cursor.getString(cursor.getColumnIndex("citycode")));
            info.setTime(cursor.getLong(cursor.getColumnIndex("time")));
            list.add(info);
            haveNext = cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 向本地数据添加搜索记录
     */
    public void insertSearchLog(Search_Address_Info search_address_info) {
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = db.query("tb_search_log", null, "keyword = ?", new String[]{search_address_info.getKeyword()}, null, null, null);
        if (cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("time", search_address_info.getTime());
            db.update("tb_search_log", contentValues, "keyword = ?", new String[]{search_address_info.getKeyword()});
        } else {
            String sql = "insert into tb_search_log(keyword,lat,lon,citycode,time)" + "values(?,?,?,?,?)";
            db.execSQL(sql, new Object[]{search_address_info.getKeyword(), search_address_info.getLatitude(), search_address_info.getLongitude(),
                    search_address_info.getCitycode(), search_address_info.getTime()});
        }
        cursor.close();
        db.close();
    }

    /**
     * 删除
     */
    public void deleteSearchLog(String _id) {
        SQLiteDatabase db = database.getWritableDatabase();
        if (_id.equals("-1")) {
            db.delete("tb_search_log", null, null);//清空表中的内容
        } else {
            if (_id.equals("")) {
                Cursor cursor = db.query("tb_search_log", null, null, null, null, null, "time asc", "1");
                if (cursor.moveToFirst()) {
                    db.delete("tb_search_log", "_id = ?", new String[]{cursor.getString(cursor.getColumnIndex("_id"))});
                }
                cursor.close();
            }
        }
        db.close();
    }

}
