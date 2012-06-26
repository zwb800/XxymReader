package com.reader.xxym;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public abstract class NewsClient  {
	protected static final int INITITEM = 10;
	private Context context;
	public NewsClient(Context context) {
		this.context = context;
	}
	
	private NewsDao ndao;
	private Downloader downloader;
	private SharedPreferences settings;
	private int next;
	
	
	public NewsClient(Activity activity,Integer currentKindID) {
		this.context = activity;
		settings = activity.getPreferences(Context.MODE_PRIVATE);
		ndao = new NewsDao(context);
		this.currentKindID = currentKindID;
		next = getNext();
		
		downloader = new Downloader(context){

			@Override
			protected void onPostExecute(String result) {
				
				Pattern p = Pattern.compile("<a href=\"(http://soul.cn.yahoo.com/ypen/\\d+/\\d+.html)",Pattern.MULTILINE);
				
				Matcher m = p.matcher(result);
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				while(m.find())
				{
					MatchResult mr = m.toMatchResult();
					Map<String,Object> map = new HashMap<String,Object>();
		        	map.put("url", mr.group(1));
		        	list.add(map);
				}
				
				MatchField(">([^<]+?)</a></h[2-3]>",result,"title",list);
				MatchField("(http://x.limgs.cn(/\\w+)+.\\w+)",result,"cover",list);
				MatchField("<p>(.+?)</p>",result,"description",list);
				
				if(next>1)
				{
					list.remove(0);
				}
				
				p = Pattern.compile("(\\d+)\">下一页",Pattern.MULTILINE);
				
				m = p.matcher(result);
				if(m.find())
				{
					MatchResult mr = m.toMatchResult();
					next = Integer.parseInt( mr.group(1));
				}
				else
				{
					next = -1;
				}
				
				ndao.addAllNews(list,NewsClient.this.currentKindID);
				loadfromlocal();
			}
		};
		
	}
	
	private void MatchField(String regex,String html,String field,List<Map<String, Object>> list)
	{
		Pattern p = Pattern.compile(regex,
				Pattern.MULTILINE);
		
		Matcher m = p.matcher(html);
		
		for(int i=0;m.find();i++)
		{
			MatchResult mr = m.toMatchResult();
			Map<String,Object> map = list.get(i);
			map.put(field, mr.group(1));
		}
	}

	private int skip;
	private Integer currentKindID;
	private int inititem;
	private Boolean loading = false;
	private boolean iscache;
	
	public static final String[] urls = new String[]{"ai","ren","mei","dao","de","zhi","xin","zhen","chengji","jiushi","bian","yuedu"};
	public static final String[] kindname = new String[]{"爱","人","美","道","德","智","心","真","城纪","旧记忆","彼岸生活","悦读"};
	public void loadfromlocal()
	{
		List<Map<String, Object>> newslist = ndao.getNewsByKindID(inititem,skip,currentKindID);
		 
        if(newslist.size()<inititem&&next>0)
		{	
        	loadfromnetwork();
		}
        else
        {
        	fillResult(newslist, 0,iscache);
        	setNext(next);
        	loading = false;
        }
	}
	
	public int getNext() {
		return settings.getInt("list"+currentKindID, 1);
	}

	public void setNext(int next) {
		settings.edit().putInt("list"+currentKindID, next).commit();
	}
	
	public void loadfromnetwork()
	{
		String url = "http://soul.cn.yahoo.com/"+urls[currentKindID]+"/index.html?page="+next;
    	downloader.execute(url,false);
	}
	
	public void load(int skip, int inititem,boolean cache)
	{
		if(loading)
			return;
		loading = true;
		this.skip = skip;
		this.inititem = inititem;
		
		this.iscache = cache;
		
		if(cache)
		{
			//加载库中的资讯
			loadfromlocal();
		}
		else
		{
			next = 1;
			//清除库中的资讯
			ndao.deleteByKind(currentKindID);
			//加载最新的资讯
			loadfromnetwork();
		}
	}
	
	public abstract void fillResult(List<Map<String, Object>> list,int next,Boolean cache);
}
