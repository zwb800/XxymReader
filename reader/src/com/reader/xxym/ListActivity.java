package com.reader.xxym;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
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
		vp = (ViewPager) findViewById(R.id.viewpager);
		PagerAdapter pa = new ViewPagerAdapter(NewsClient.kindname);
		vp.setAdapter(pa);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			showDialog(EXITDIALOG);
		} else {
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean r = super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_refersh:
			ViewPagerAdapter adapter = (ViewPagerAdapter) vp.getAdapter();
			adapter.reload(vp.getCurrentItem());
			break;
		case R.id.menu_exit:
			finish();
			break;
		case R.id.menu_about:
			Intent i = new Intent(this, AboutActivity.class);
			startActivity(i);
		}
		return r;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bottom, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		@SuppressWarnings("unchecked")
		Map<String, String> item = (Map<String, String>) parent
				.getItemAtPosition(position);
		tracker.trackPageView(item.get("title"));
		Intent i = new Intent(this, ContentActivity.class);
		i.putExtra("url", item.get("url"));
	
		startActivity(i);
	}


	class ViewPagerAdapter extends PagerAdapter {
		private Map<Integer, NewsClient> clients = new HashMap<Integer, NewsClient>();
		private String[] title = null;

		public ViewPagerAdapter(String[] title) {
			super();
			this.title = title;
		}

		public void reload(int position) {
			clients.get(position).load(0, NewsClient.INITITEM, false);
		}

		@Override
		public Object instantiateItem(View container, final int position) {
			final ListView listview = (ListView) View.inflate(
					container.getContext(), R.layout.listview, null);
			final List<Map<String, Object>> newslist = new ArrayList<Map<String, Object>>();
			final NewsListAdapter adapter = new NewsListAdapter(
					ListActivity.this, newslist, R.layout.list_item,
					new String[] { "title", "description", "cover" },
					new int[] { R.id.txtTitle, R.id.txtDescription,
							R.id.imageView1 });
			final View footer = View.inflate(container.getContext(),
					R.layout.loadbutton, null);
			final Button btnLoad = (Button) footer.findViewById(R.id.button1);
			final NewsClient newsclient = new NewsClient(ListActivity.this,
					position) {
				@Override
				public void fillResult(List<Map<String, Object>> list,
						int next, Boolean cache) {
					if (!cache) {
						newslist.clear();
					}

					if (next < 0) {
						listview.removeFooterView(footer);
					} else {

						btnLoad.setText(R.string.loadmore);
						btnLoad.setClickable(true);
					}

					for (int i = 0; i < list.size(); i++) {
						newslist.add(list.get(i));
					}

					adapter.notifyDataSetChanged();
				}
			};

			btnLoad.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View view) {
					Button btn = (Button) view;
					btn.setText(R.string.loading);
					newsclient.load(newslist.size(), 10, true);
					btn.setClickable(false);
				}
			});
			listview.addFooterView(footer);
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(ListActivity.this);

			clients.put(position, newsclient);
			newsclient.load(0, NewsClient.INITITEM, true);

//			OnScrollListener onScrollListener = new OnScrollListener() {
//
//				protected int currentItem;
//
//				@Override
//				public void onScrollStateChanged(AbsListView view,
//						int scrollState) {
//					if (OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
//						ListAdapter adapter = view.getAdapter();
//						int loadeditem = adapter.getCount();
//						if (currentItem >= loadeditem) {
//							newsclient.load(loadeditem, 10, true);
//						}
//					}
//				}
//
//				@Override
//				public void onScroll(AbsListView view, int firstVisibleItem,
//						int visibleItemCount, int totalItemCount) {
//					currentItem = firstVisibleItem + visibleItemCount;
//				}
//			};

			// listview.setOnScrollListener(onScrollListener);

			ViewPager vp = (ViewPager) container;
			vp.addView(listview);
			return listview;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			ViewPager vp = (ViewPager) container;
			View v = (View) object;
			vp.removeView(v);
			clients.remove(position);
		}

		@Override
		public int getCount() {
			return title.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return title[position];
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	public class NewsListAdapter extends SimpleAdapter {

		Context context;

		public NewsListAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			this.context = context;
		}

		String filename = null;

		@Override
		public void setViewImage(ImageView v, String value) {
			new Downloader(this.context).Download(v, value);
		}

	}

}