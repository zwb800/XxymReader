package com.reader.xxym;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kuguo.ad.PushAdsManager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ListActivity extends BaseActivity implements OnItemClickListener {
    private ViewPager vp;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker.trackPageView("/List");
        setContentView(R.layout.list);
        loadAD();
        vp = (ViewPager)findViewById(R.id.viewpager);
        PagerAdapter pa = new ViewPagerAdapter(NewsClient.kindname);
        vp.setAdapter(pa);
    }
    
   
    

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean r = super.onOptionsItemSelected(item);
		switch(item.getItemId())
		{
			case R.id.menu_refersh:
				ViewPagerAdapter adapter = (ViewPagerAdapter) vp.getAdapter();
				adapter.reload(vp.getCurrentItem());
				break;
			case R.id.menu_exit:finish();break;
		}
		return r;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.bottom, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Map<String,String> item = (Map<String,String>)parent.getItemAtPosition(position);
		// TODO Auto-generated method stub
		Intent i = new Intent(this,ContentActivity.class);
		int ids = Integer.parseInt(item.get("_id"));
		i.putExtra("id", new Integer(ids));
		
		startActivity(i);
	}
	
	private void MatchField(String regex,String html,String field,List<Map<String,String>> list)
	{
		Pattern p = Pattern.compile(regex,
				Pattern.MULTILINE);
		
		Matcher m = p.matcher(html);
		
		for(int i=0;m.find();i++)
		{
			MatchResult mr = m.toMatchResult();
			Map<String,String> map = list.get(i);
			map.put(field, mr.group(1));
		}
	}
	
	class ViewPagerAdapter extends PagerAdapter
	{
		private Map<Integer,NewsClient> clients = new HashMap<Integer,NewsClient>();
		private String[] title = null;
		public ViewPagerAdapter(String[] title) {
			super();
			this.title = title;
		}
		
		public void reload(int position)
		{
			clients.get(position).load(0, NewsClient.INITITEM, false);
		}

		@Override
		public Object instantiateItem(View container, final int position) {
			// TODO Auto-generated method stub
			final ListView listview = (ListView) View.inflate(container.getContext(), R.layout.listview, null);
			final List<Map<String, Object>> newslist = new ArrayList<Map<String, Object>>();
			final NewsListAdapter adapter = new NewsListAdapter(ListActivity.this,newslist,R.layout.list_item,
	        		new String[]{"title","description","cover"},
	        		new int[]{R.id.txtTitle,R.id.txtDescription,R.id.imageView1});
			listview.setAdapter(adapter);
		    listview.setOnItemClickListener(ListActivity.this);
		    
			final NewsClient newsclient = new NewsClient(ListActivity.this,position){
				@Override
				public void fillResult(List<Map<String, Object>> list, int next,Boolean cache) {
					if(!cache)
					{
						newslist.clear();
					}
					 for(int i=0;i<list.size();i++)
					 {
						 newslist.add(list.get(i));
					 }
				
					adapter.notifyDataSetChanged();
				}
			};
			
			clients.put(position,newsclient);
			newsclient.load(0, NewsClient.INITITEM, true);
				
			OnScrollListener onScrollListener = new OnScrollListener(){

				protected int currentItem;

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if(OnScrollListener.SCROLL_STATE_IDLE==scrollState)
					{
						ListAdapter adapter = view.getAdapter();
						int loadeditem = adapter.getCount();
						if(currentItem>=loadeditem)
						{
							newsclient.load(loadeditem, 10, true);
						}
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					currentItem = firstVisibleItem+visibleItemCount;
				}};
				
			listview.setOnScrollListener(onScrollListener);

	        ViewPager vp = (ViewPager)container;
	        vp.addView(listview);
			return listview;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			ViewPager vp = (ViewPager)container;
			View v  = (View)object;
			vp.removeView(v);
			clients.remove(position);
		}



		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return title.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return title[position];
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==arg1;
		}
	}
	
	public class NewsListAdapter extends SimpleAdapter {

		Context context;
		public NewsListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
			this.context = context;
		}
		String filename = null;
		@Override
		public void setViewImage(ImageView v, String value) {
			new Downloader(this.context).Download(v,value);
		}

	}


}