package com.example.photoapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.database.DatabaseManager;
import com.example.photo.Photo;

public class FetchFromCacheTask extends AsyncTask<String,Void,Bitmap> {

	//private final WeakReference<ImageView> imageViewReference;
	ImageView imageView;
	private String position;
	private Context context;
	private ImageCache cache;

	public FetchFromCacheTask(ImageView imageView, Context context, ImageCache cache) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		//imageViewReference = new WeakReference<ImageView>(imageView);
		this.imageView = imageView;
		this.context = context;
		this.cache = cache;
		position = "";
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(String... params)
	{
		position = params[0];
		synchronized(cache)
		{
			Bitmap bm = cache.getBitmapFromMemCache(position);
			return bm;
		}
	}

	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		//		if (imageViewReference != null && bitmap != null) {
		//final ImageView imageView = imageViewReference.get();
		if (bitmap != null)
		{
			imageView.setImageBitmap(bitmap);
		}else
		{
			//load from other source
			//imageView.setImageResource(position);
			//load direct from database here
			DatabaseManager db = DatabaseManager.getInstance(context);
			Photo photo = db.getPhoto(position);
			imageView.setImageBitmap(photo.getBitmap());
		}
		//		     Once complete, see if ImageView is still around and set bitmap
	}
}