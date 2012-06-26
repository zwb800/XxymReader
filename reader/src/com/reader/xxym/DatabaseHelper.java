package com.reader.xxym;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * @author zwb
 *
 *数据库连接类
 *
 */

public class DatabaseHelper {
	

	private Context context;
	protected SQLiteDatabase database;
	//数据库名
	private static final String DATABASE_NAME = "reader";

	
	public DatabaseHelper(Context context) {
		this.context = context;
	}
	
	//打开连接
	public void open()
	{
		SQLiteOpenHelper openhelper = new DatabaseOpenHelper(context);
		database = openhelper.getWritableDatabase();
	}
	
	/*
	 * 数据库开启帮助类
	 * @author zwb
	 *
	 */
	private class DatabaseOpenHelper extends SQLiteOpenHelper
	{
		private final static int NEWVERSION = 1;
		public DatabaseOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, NEWVERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			arg0.execSQL(
		"create table news (_id integer primary key autoincrement, "
        + "title text not null,description text not null," +
        "content text null,url text not null,"
		+"kindid integer not null,cover text null);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			arg0.execSQL("drop table if exists news;");
			onCreate(arg0);
		}

		@Override
		public synchronized SQLiteDatabase getWritableDatabase() {
			SQLiteDatabase database = null;
			if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
			{
				File file = Environment.getExternalStorageDirectory();
				file = new File(file.getAbsolutePath()+"/Android/data/"+context.getPackageName()+"/databases/");
				if(!file.exists())
				{
					file.mkdirs();
				}
				
				file = new File(file.getAbsolutePath()+"/"+DATABASE_NAME);
				database = SQLiteDatabase.openOrCreateDatabase(file, null);
			}
			else
			{
				database = super.getWritableDatabase();
			}
			
			int version = database.getVersion();
			
			//判断数据库版本
			if(NEWVERSION!=version)
			{
				database.beginTransaction();
				try
				{
					if(version==0)
					{
						onCreate(database);
					}
					else
					{
						onUpgrade(database,version,NEWVERSION);
					}
					database.setVersion(NEWVERSION);
					database.setTransactionSuccessful();
				}
				finally
				{
					database.endTransaction();
				}
			}
			
			return database;
		}
		
	}
	
	protected List<Map<String, Object>> convertToList(Cursor c)
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while(c.moveToNext())
		{
			list.add(convertToMap(c));
		}
		return list;
	}
	
	protected Map<String, Object> convertToMap(Cursor c)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		for(int i=0;i<c.getColumnCount();i++)
		{
			map.put(c.getColumnName(i), c.getString(i));
		}
		return map;
	}

	//关闭连接
	public void close() {
		database.close();
	}
}
