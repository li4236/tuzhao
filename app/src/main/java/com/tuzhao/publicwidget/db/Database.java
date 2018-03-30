package com.tuzhao.publicwidget.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper 
{
	private static final String DB_NAME = "db";
	private static Database database = null;//初始化数据库类
	
	private Database(Context context) 
	{
		super(context, DB_NAME, null, 1); 
	}
	
	/**
	 * 初始化单例
	 * @param context
	 * @return
	 */
	public static Database getInstance(Context context)
	{
		if (database == null)
		{
			database = new Database(context);
		}
		return database;
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		// 用户个人信息表
		db.execSQL("CREATE TABLE tb_user("
				+ "_id_ INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "_id TEXT DEFAULT \"\","
				+ "username TEXT DEFAULT \"\","
				+ "password TEXT DEFAULT \"\","
				+ "balance TEXT DEFAULT \"\","
				+ "nickname TEXT DEFAULT \"\","
				+ "img_url TEXT DEFAULT \"\","
				+ "autologin TEXT DEFAULT \"\","
				+ "car_number TEXT DEFAULT \"\")");

		// 用户个人信息表
		db.execSQL("CREATE TABLE tb_search_log("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "keyword TEXT DEFAULT \"\","
				+ "lat TEXT DEFAULT \"\","
				+ "lon TEXT DEFAULT \"\","
				+ "citycode TEXT DEFAULT \"\")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// 数据库更新
	}

}
