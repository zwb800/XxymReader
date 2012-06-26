package com.reader.xxym;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class ContentActivity extends BaseActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker.trackPageView("/Content");
        setContentView(R.layout.content);
        int id = getIntent().getIntExtra("id", 0);
        final ViewPager vp = (ViewPager)findViewById(R.id.viewpager);
        
        new NewsContentClient(this){
			@Override
			public void fillResult(String content) {
				PagerAdapter pa = new ViewPagerAdapter(content);
		        vp.setAdapter(pa);
			}
        }.load(id, true);
    }
    
    
    class ViewPagerAdapter extends PagerAdapter
	{
		
		private String content = null;
		public ViewPagerAdapter(String content) {
			super();

			this.content = content;
		}
		private int th;
		@Override
		public Object instantiateItem(View container, final int position) {
			// TODO Auto-generated method stub
			final View layout = getLayoutInflater().inflate(R.layout.text, null);
			final TextView textview = (TextView)layout.findViewById(R.id.textView1);
			layout.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					return false;
				}});
			textview.setText(content);
			final ViewPager vp = (ViewPager)container;

			vp.addView(layout);
			
			new Handler().post(new Runnable(){
				@Override
				public void run() {
					//第一次进入计算每页高度
					if(th==0)
					{
						int vh = vp.getMeasuredHeight();
						int line = textview.getLineCount();//所有行
						int sth = textview.getMeasuredHeight()/line;//每行的高度
						int linecount = vh/sth;//每页的行数
						th = linecount * sth;//每页的高度
						count = (int) Math.ceil( line / (float)linecount);
						ViewPagerAdapter.this.notifyDataSetChanged();
					}
				
					int scrollx = th * position;
					layout.scrollTo(0, scrollx);
					textview.setHeight(th * (position+1));
				}});
			
			return layout;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			ViewPager vp = (ViewPager)container;
			View v  = (View)object;
			vp.removeView(v);
		}
		int count = 3;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return count;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==arg1;
		}
	}

}