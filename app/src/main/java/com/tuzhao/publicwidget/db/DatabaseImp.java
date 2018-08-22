package com.tuzhao.publicwidget.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
        Log.e("TAG", "setRegistrationId: " + registrationId);
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
        String registrationId = "";
        if (cursor.moveToFirst()) {
            registrationId = cursor.getString(cursor.getColumnIndex(Database.REGISTRATION_ID));
            Log.e("TAG", "getRegistrationId: " + registrationId);
        } else {
            registrationId = JPushInterface.getRegistrationID(MyApplication.getInstance());
            setRegistrationId(registrationId);
        }
        cursor.close();
        sqLiteDatabase.close();
        return registrationId;
    }

    /**
     * 从本地读取数据库读取
     */
    public User_Info getUserFormDatabase() {
        SQLiteDatabase db = database.getReadableDatabase();
        String sql = "select * from tb_user";
        Cursor cursor = db.rawQuery(sql, null);
        User_Info user_info = null;
        while (cursor.moveToNext()) {
            user_info = new User_Info();
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
     *
     * @param user_info
     */
    public void insertUserToDatabase(User_Info user_info) {
        SQLiteDatabase db = database.getWritableDatabase();
        db.delete("tb_user", null, null);//清空表中的内容
        String sql = "insert into tb_user(_id,username,password,balance,nickname,img_url,autologin,car_number)" + "values(?,?,?,?,?,?,?,?)";
        db.execSQL(sql, new Object[]{user_info.getId(), user_info.getUsername(), user_info.getPassword(), user_info.getBalance(), user_info.getNickname(), user_info.getImg_url(), user_info.getAutologin()});
        db.close();
    }

    /**
     * 从本地读取搜索记录
     *
     * @return
     */
    public List<Search_Address_Info> getSearchLog() {
        SQLiteDatabase db = database.getReadableDatabase();
        String sql = "select * from tb_search_log";
        Cursor cursor = db.rawQuery(sql, null);

        List<Search_Address_Info> list = new ArrayList<Search_Address_Info>();

        if (cursor.moveToLast()) {
            do {
                Search_Address_Info info = new Search_Address_Info();
                info.set_id(cursor.getString(cursor.getColumnIndex("_id")));
                info.setKeyword(cursor.getString(cursor.getColumnIndex("keyword")));
                info.setLatitude(Double.valueOf(cursor.getString(cursor.getColumnIndex("lat"))));
                info.setLongitude(Double.valueOf(cursor.getString(cursor.getColumnIndex("lon"))));
                info.setCitycode(cursor.getString(cursor.getColumnIndex("citycode")));
                list.add(info);
            }
            while (cursor.moveToPrevious());
        }

        cursor.close();
        db.close();
        return list;
    }

    /**
     * 向本地数据添加搜索记录
     *
     * @param search_address_info
     */
    public void insertSearchLog(Search_Address_Info search_address_info) {
        SQLiteDatabase db = database.getWritableDatabase();

        String sql = "insert into tb_search_log(keyword,lat,lon,citycode)" + "values(?,?,?,?)";
        db.execSQL(sql, new Object[]{search_address_info.getKeyword(), search_address_info.getLatitude(), search_address_info.getLongitude(), search_address_info.getCitycode()});
        db.close();
    }

    /**
     * 删除
     *
     * @param _id
     */
    public void deleteSearchLog(String _id) {
        SQLiteDatabase db = database.getWritableDatabase();
        if (_id.equals("-1")) {
            db.delete("tb_search_log", null, null);//清空表中的内容
        } else {
            //删除某一条记录
            String[] args = {_id};
            db.delete("tb_search_log", "_id=?", args);
        }
        db.close();

    }

    //	public synchronized void insertDownloadRecord(String file_id , String file_name, String file_houzhui,String userid , String down_time)
//	{
//		SQLiteDatabase db = database.getWritableDatabase();
//		String sql = "insert into tb_down_list(file_id,file_name,file_houzhui,userid,down_time)" + "values(?,?,?,?,?)";
//		db.execSQL(sql, new Object[]{file_id,file_name,file_houzhui,userid,down_time});
//		db.close();
//	}
//
//	public List<DownRecord_Bean> getDownRecord_List()
//	{
//		SQLiteDatabase db = database.getReadableDatabase();
//		String sql = "select * from tb_down_list where userid = ?";
//		Cursor cursor = db.rawQuery(sql, new String[]{"13068176960"});
//		List<DownRecord_Bean> downRecord_list = new ArrayList<DownRecord_Bean>();
//
//		if(cursor.moveToLast())
//		{
//			do
//			{
//				DownRecord_Bean downRecord = new DownRecord_Bean(cursor.getString(cursor.getColumnIndex("file_id")), cursor.getString(cursor.getColumnIndex("file_name")),cursor.getString(cursor.getColumnIndex("file_houzhui")),cursor.getString(cursor.getColumnIndex("down_time")));
//				downRecord_list.add(downRecord);
//			}
//			while(cursor.moveToPrevious());
//		}
//
//		cursor.close();
//		db.close();
//		return downRecord_list;
//	}
}
