package com.example.photoapp;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class FetchFromCacheTask extends AsyncTask<Integer,Void,Bitmap> {

	//private final WeakReference<ImageView> imageViewReference;
	ImageView imageView;
	private int position;
	private Context context; 
	private ImageCache cache;

	public FetchFromCacheTask(ImageView imageView, Context context, ImageCache cache) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		//imageViewReference = new WeakReference<ImageView>(imageView);
		this.imageView = imageView;
		this.context = context;
		this.cache = cache;
		position = 0;
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(Integer... params)
	{
		position = params[0];
		synchronized(cache)
		{
			Bitmap bm = cache.getBitmapFromMemCache(""+position);
			return bm;
		}
	}

	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		//		if (imageViewReference != null && bitmap != null) {
		//			final ImageView imageView = imageViewReference.get();
		if (bitmap != null)
		{
			imageView.setImageBitmap(bitmap);
		}else
		{
			//load from other source
			imageView.setImageResource(position);
		}
		//		     Once complete, see if ImageView is still around and set bitmap
	}
}