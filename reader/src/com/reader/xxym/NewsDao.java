package com.reader.xxym;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class NewsDao extends DatabaseHelper {

	public NewsDao(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	// //添加新闻
	// public long addNews(String title,String description,String listcover,
	// int newsid, int parentkindid, int kindid,String path)
	// {
	// open();
	// ContentValues values = new ContentValues();
	// values.put(News.TITLE, title);
	// values.put(News.DESCRIPTION, description);
	// values.put(News.LISTCOVER, listcover);
	// values.put(News.NEWSID, newsid);
	// values.put(News.PARENTKINDID, parentkindid);
	// values.put(News.KINDID, kindid);
	// values.put(News.PATH, path);
	// long r = database.insert(News.TABLE_NAME, null, values);
	// close();
	// return r;
	// }

	public long updateNews(String url, String content) {
		
		ContentValues values = new ContentValues();
		values.put("content", content);
		long r =-1;
		Object news = getNewsContentByURL(url);
		open();
		if(news==null)
		{
			values.put("url", url);
			r = database.insert("content", null, values);
		}
		else
		{
			r = database.update("content", values, "url=?", new String[] {url});
		}
		close();
		return r;
	}

	// 添加新闻
	public long addAllNews(List<Map<String, Object>> list, int kindid) {
		open();
		database.beginTransaction();
		long r = 0;

		try {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				if (map.size() == 4) {
					ContentValues values = new ContentValues();
					String[] keys = map.keySet().toArray(
							new String[values.size()]);
					for (int j = 0; j < keys.length; j++) {
						String key = keys[j];
						String val = map.get(key).toString();
						values.put(key, val);
					}

					values.put("kindid", kindid);

					r += database.insert(News.TABLE_NAME, null, values);
				} else {
					Log.d("", map.toString());
				}
			}

			database.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
			close();
		}

		return r;
	}

	// 根据ID获取新闻
	public Map<String, Object> getNewsByID(int id) {
		open();
		Cursor c = database.query("news", new String[] {  "url" },
				"_id=?", new String[] { id + "" }, null, null, null);
		Map<String, Object> map = null;

		if (c.moveToNext()) {
			map = convertToMap(c);
		}

		c.close();
		close();
		return map;
	}
	
	// 根据ID获取新闻
		public Map<String, Object> getNewsContentByURL(String url) {
			open();
			Cursor c = database.query("content", new String[] {  "content" },
					"url=?", new String[] { url }, null, null, null);
			Map<String, Object> map = null;

			if (c.moveToNext()) {
				map = convertToMap(c);
			}

			c.close();
			close();
			return map;
		}

	public List<Map<String, Object>> getNewsByKindID(int limit, int skip,
			Integer kindid) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		open();

		LinkedList<String> selectionArgs = new LinkedList<String>();
		StringBuffer selection = new StringBuffer();
		selection.append("1=1 ");
		if (kindid != null) {
			selection.append("and " + News.KINDID + "=? ");
			selectionArgs.add(kindid + "");
		}

		Cursor c = database.rawQuery("Select * From " + News.TABLE_NAME
				+ " Where " + selection.toString() + " Order by _id asc"
				+ " Limit " + limit + " Offset " + skip,
				selectionArgs.toArray(new String[selectionArgs.size()]));
		list = convertToList(c);
		c.close();
		close();
		return list;
	}

	public void deleteAll() {
		open();
		database.delete(News.TABLE_NAME, null, null);
		close();
	}

	public void deleteByKind(int kind) {
		open();
		database.delete(News.TABLE_NAME, News.KINDID + "=? ",
				new String[] { kind + "" });
		close();
	}

	public int getNewsCountByParentKindID(int parentkindid) {
		open();
		Cursor c = database.rawQuery("select count(*) from " + News.TABLE_NAME
				+ " where " + News.PARENTKINDID + "=?",
				new String[] { parentkindid + "" });

		int count = 0;
		if (c.moveToNext()) {
			count = c.getInt(0);
		}
		c.close();
		close();
		return count;
	}

	public void deleteNews(int newsid) {
		open();
		database.delete(News.TABLE_NAME, News.NEWSID + "=?",
				new String[] { newsid + "" });
		close();
	}

}
