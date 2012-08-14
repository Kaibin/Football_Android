package com.orange.score.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageNetworkRequest extends AsyncTask<ImageView, Void, Bitmap>{

	ImageView imageView = null;
	public static final String TAG = ImageNetworkRequest.class.getName();
	@Override
	protected Bitmap doInBackground(ImageView... imageViews) {
		
		this.imageView = imageViews[0];
		Bitmap bitmap = downloadImage((String)imageView.getTag());
		return bitmap;
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		if (result != null) {
			imageView.setImageBitmap(result);
		}
	}


	private Bitmap downloadImage(String srcUrl) {
		Bitmap bm = null;
	    try {
	        URL url = new URL(srcUrl);
	        URLConnection conn = url.openConnection();
	        conn.connect();
	        InputStream is = conn.getInputStream();
	        BufferedInputStream bis = new BufferedInputStream(is);
	        bm = BitmapFactory.decodeStream(bis);
	        bis.close();
	        is.close();
	    } catch (IOException e) {
	        Log.w(TAG,"Error getting the image from server : " + e.getMessage().toString());
	        return null;
	    } 
	    return bm;
	}

}
