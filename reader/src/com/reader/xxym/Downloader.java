package com.reader.xxym;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.widget.ImageView;

/**
 * 下载图片
 * 
 * @author zwb
 * 
 */
public class Downloader {

	public Downloader(Context context) {
		this.context = context;
	}

	protected Context context;

	/**
	 * 异步下载图片
	 * 
	 * @param v
	 *            想要设置图片的对象
	 * @param value
	 *            图片url
	 */
	public void Download(ImageView v, String value) {
		v.setImageBitmap(null);
		String filename = getFileName(value);
		File file = getFile(filename);

		if (!file.exists()) {
			// 从网络下载图片 显示并保存到sdcard
			new AsyncTask<Object, Integer, String>() {

				private Handler handler = new Handler();

				@Override
				protected String doInBackground(Object... arg0) {
					final ImageView v = (ImageView) arg0[0];
					final String url = (String) arg0[1];

					HttpClient client = new DefaultHttpClient();
					try {
						HttpResponse response = client
								.execute(new HttpGet(url));
						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							final byte[] result = EntityUtils
									.toByteArray(response.getEntity());
							final Bitmap bitmap = ThumbnailUtils
									.extractThumbnail(result, v.getWidth());
							handler.post(new Runnable() {
								@Override
								public void run() {
									v.setImageBitmap(bitmap);
									writeFile(bitmap, getFileName(url));
								}
							});
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}
			}.execute(v, value);
		} else {
			// 从sdcard读取图片
			try {
				FileInputStream stream = getFileInputStream(filename);
				Bitmap bitmap = BitmapFactory.decodeStream(stream);
				v.setImageBitmap(bitmap);
				stream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getFileName(String value) {
		String filename = null;
		try {
			filename = new URL(value).getFile();
			// filename = filename.substring(filename.lastIndexOf("/")+1);
			filename = filename.replace("/", "_");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		return filename;
	}

	public void execute(String value) {
		execute(value, true);
	}

	public void execute(String value, boolean cache) {
		final String filename = getFileName(value);

		File file = getFile(filename);

		if (cache && file.exists()) {
			try {
				FileInputStream stream = getFileInputStream(filename);
				byte[] bArr = new byte[stream.available()];
				stream.read(bArr);
				String s = new String(bArr, "utf-8");
				onPostExecute(s);
				stream.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			new AsyncTask<Object, Integer, String>() {

				@Override
				protected String doInBackground(Object... arg0) {

					final String url = (String) arg0[0];

					HttpClient client = new DefaultHttpClient();
					String result = null;
					try {
						HttpResponse response = client
								.execute(new HttpGet(url));
						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							result = EntityUtils.toString(response.getEntity(),
									"utf-8");
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

					return result;
				}

				@Override
				protected void onPostExecute(String result) {
					if (result == null) {
						Downloader.this.onPostFaild();
					} else {
						Downloader.this.onPostExecute(result);

						// try
						// {
						// writeFile(result.getBytes("utf-8"),filename);
						// }
						// catch (UnsupportedEncodingException e) {
						// e.printStackTrace();
						// }
					}
				}
			}.execute(value);
		}
	}

	protected void onPostExecute(String result) {

	}

//	private void writeFile(byte[] result, String filename) {
//		try {
//			// 保存到sdcard
//			FileOutputStream stream = null;
//			stream = getFileOutpuStream(filename);
//			stream.write(result);
//			stream.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	private void writeFile(Bitmap bm, String filename) {
		try {
			// 保存到sdcard
			FileOutputStream stream = null;
			stream = getFileOutpuStream(filename);
			bm.compress(CompressFormat.JPEG, 20, stream);
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File getFile(String filename) {
		File file = null;

		File dir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			dir = Environment.getExternalStorageDirectory();
			dir = new File(dir.getAbsolutePath() + "/Android/data/"
					+ context.getPackageName() + "/cache/");
		} else {
			dir = context.getFilesDir();
		}

		if (!dir.exists()) {
			dir.mkdirs();
		}
		file = new File(dir, filename);

		return file;
	}

	private FileOutputStream getFileOutpuStream(String filename)
			throws FileNotFoundException {

		FileOutputStream stream = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File file = getFile(filename);
			stream = new FileOutputStream(file);
		} else {
			stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
		}

		return stream;
	}

	private FileInputStream getFileInputStream(String filename)
			throws FileNotFoundException {
		FileInputStream stream = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File file = getFile(filename);
			stream = new FileInputStream(file);
		} else {
			stream = context.openFileInput(filename);
		}
		return stream;
	}

	// public static List<Map<String, Object>> convertToList(JSONArray arr)
	// {
	// List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	// try {
	// for(int i=0;i<arr.length();i++)
	// {
	// list.add(convertToMap(arr.getJSONObject(i)));
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return list;
	// }

	// public static Map<String, Object> convertToMap(JSONObject jo)
	// {
	// Map<String, Object> map = new HashMap<String, Object>();
	//
	// try {
	// Iterator keys = jo.keys();
	//
	// while(keys.hasNext())
	// {
	// String key = keys.next().toString();
	// map.put(key, jo.get(key));
	// }
	// map.put("emptycover",0);
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return map;
	// }
	//
	protected void onPostFaild() {

	}
}
