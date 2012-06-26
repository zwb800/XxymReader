package com.reader.xxym;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.kuguo.ad.PushAdsManager;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

	 protected GoogleAnalyticsTracker tracker;

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		tracker.dispatch();
	}

	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    tracker = GoogleAnalyticsTracker.getInstance();

	    // Start the tracker in manual dispatch mode...
	    tracker.startNewSession("UA-15153331-5", this);
	    // ...alternatively, the tracker can be started with a dispatch interval (in seconds).
	    //tracker.startNewSession("UA-YOUR-ACCOUNT-HERE", 20, this);
	  }

	  @Override
	  protected void onDestroy() {
	    super.onDestroy();
	    // Stop the tracker when it is no longer needed.
	    tracker.stopSession();
	  }
	  
	  protected void loadAD()
	  {
    	/*
		 ＊获取PushAdsManager的唯一实例
    	 */
		PushAdsManager paManager = PushAdsManager.getInstance();
		
		/*
		 * 广告接口 receivePushMessage(Context ctx, boolean isTiming)
		 * @param ctx 
		 * @param isTiming  值为true，在调用广告接口时起，定时两小时请求一次；值为false，只在调用广告接口时请求广告
		 */
		paManager.receivePushMessage(this, false);
	  }
}
