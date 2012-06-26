package com.reader.xxym;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ReaderActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new Handler().post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent i = new Intent(ReaderActivity.this,ListActivity.class);
		    	startActivity(i);
		    	ReaderActivity.this.finish();
			}});
        
    }
}