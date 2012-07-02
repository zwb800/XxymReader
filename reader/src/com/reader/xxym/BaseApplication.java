package com.reader.xxym;

import android.app.Application;

public class BaseApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler ch = CrashHandler.getInstance();
		ch.init(getApplicationContext());
	}

}
