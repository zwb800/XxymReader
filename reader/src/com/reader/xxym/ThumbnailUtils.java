package com.reader.xxym;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ThumbnailUtils {
	public static Bitmap extractThumbnail(byte[] data, int width)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	     
	     // 获取这个图片的宽和高
	     BitmapFactory.decodeByteArray(data, 0, data.length, options); //此时返回bm为空
	     options.inJustDecodeBounds = false;
	     int be = (int) Math.floor(options.outWidth / (float)width);
	     if (be <= 0)
	      be = 1;
	     options.inSampleSize = be;
	     Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, options);
	     return bm;
	}
}
