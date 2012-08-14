package com.reader.xxym;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.kuguo.ad.PushAdsManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class BaseActivity extends Activity {

	protected static final int EXITDIALOG = 1;
	protected GoogleAnalyticsTracker tracker;

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		tracker.dispatch();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case 1:
			AlertDialog d = new AlertDialog.Builder(this)
					.setMessage("确定要退出吗")
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.dismiss();

								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									BaseActivity.this.finish();
								}
							}).create();
			return d;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tracker = GoogleAnalyticsTracker.getInstance();

		// Start the tracker in manual dispatch mode...
		tracker.startNewSession("UA-15153331-5", this);
		// ...alternatively, the tracker can be started with a dispatch interval
		// (in seconds).
		// tracker.startNewSession("UA-YOUR-ACCOUNT-HERE", 20, this);
	}

	@Override
	protected void onDestroy() {
		tracker.stopSession();
		super.onDestroy();
		// Stop the tracker when it is no longer needed.
		
	}
}
