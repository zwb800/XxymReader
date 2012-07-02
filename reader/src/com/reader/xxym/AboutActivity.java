package com.reader.xxym;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AboutActivity extends BaseActivity {
	String inittext = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		final EditText text = (EditText) findViewById(R.id.editText1);
		inittext = text.getText().toString();
		text.setOnTouchListener(new OnTouchListener(){
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					
					if(text.getText().toString().equals(inittext))
					{
						text.setText(null);
						return true;
					}
					
				}
				return false;
			}});
		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				String t = text.getText().toString().trim();
				
				if(t!=""&&t!=inittext)
				{
					final ProgressDialog mProgressDialog = new ProgressDialog(AboutActivity.this);
					mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					mProgressDialog.setMessage("发送中...");
					mProgressDialog.show();
					new AsyncTask<String, Integer, String>() {

						@Override
						protected String doInBackground(String... arg0) {
							Mail.sendMail("雅虎心香一脉 反馈意见", arg0[0]);
							mProgressDialog.dismiss();
							return null;
						}

						@Override
						protected void onPostExecute(String result) {
							super.onPostExecute(result);
							Toast.makeText(AboutActivity.this,"发送成功", Toast.LENGTH_SHORT).show();
						}}.execute(text.getText().toString());
					
					
				}
			}
			
		});
	}

}
