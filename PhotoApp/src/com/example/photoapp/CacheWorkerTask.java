package com.example.photoapp;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import com.example.database.DatabaseManager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class CacheWorkerTask extends AsyncTask<String,Void,Boolean> {

	//private final WeakReference<ImageView> imageViewReference;
	private String position = "";
	private Context context; 
	private ImageCache cache;
	//private int[] images;
	
	int reqWidth;
	int reqHeight;

	public CacheWorkerTask(Context context, ImageCache cache) 
	{
		// Use a WeakReference to ensure the ImageView can be garbage collected
		//imageViewReference = new WeakReference<ImageView>(imageView);
		//this.reqWidth = reqWidth;
		//this.reqHeight = reqHeight;
		this.context = context;
		this.cache = cache;
	}
	
	public CacheWorkerTask(Context context, ImageCache cache, int reqWidth, int reqHeight) 
	{
		// Use a WeakReference to ensure the ImageView can be garbage collected
		//imageViewReference = new WeakReference<ImageView>(imageView);
		this.reqWidth = reqWidth;
		this.reqHeight = reqHeight;
		this.context = context;
		this.cache = cache;
	}
	
	// Decode image in background.
	@Override
	protected Boolean doInBackground(String... params) 
	{
		position = params[0];
		ArrayList<String> list = GridActivity.getList();
		Iterator<String> itr = list.iterator();
		
		while(itr.hasNext())
		{
			String strid = itr.next();
			ImageView imageView = null;
			FetchFromCacheTask task = new FetchFromCacheTask(imageView, context, cache);
			task.execute(strid);
		}
		
		//images = context.getResources().getIntArray(R.array.ImgRef);
		//data = params[0];
		//return decodeSampledBitmapFromResource(params[0].getResources(), data, 100, 100);
		//System.out.println("Decoding resource into bitmap");
		//Bitmap bm = decodeSampledBitmapFromResource(context.getResources(),position,300,300);
		//System.out.println("Scaled down the bitmap to 300, 300 and is now of size "+ (bm.getByteCount()/1024));
		//		ByteArrayOutputStream os = new ByteArrayOutputStream();
		//		if(bm.compress(Bitmap.CompressFormat.JPEG, 100, os))
		//		{
		//			System.out.println("Compressed the bitmap down to JPEG");
		//			//Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), images[i]);
		//			String string = ""+position;
		//			System.out.println("Attempting to add bitmap to cache");
		//			byte[] array = os.toByteArray();
		//			System.out.println("The size of the bitmap is now "+(array.length/1024));
		byte[] array = DatabaseManager.getInstance(context).getGridBitmapAsBytes(position);
		if (array != null)
		{
			System.out.println("Attempting to add bitmap to cache");
			synchronized(cache)
			{
				if(!cache.addBitmapToMemoryCache(position, array))
				{
					System.out.println("Failed to write bitmap with identifier "+position+ " to cache");
					return false;
				}
				return true;
			}
		}
		else
		{
			System.out.println("Grid Bitmap Bytes returned from database is null");
			return false;
		}
	}

	//	    if (imageViewReference != null && bitmap != null) {
	//            final ImageView imageView = imageViewReference.get();
	//            if (imageView != null) {
	//                imageView.setImageBitmap(bitmap);
	//            }
	//        }
	//	     Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Boolean addedTocache) 
	{
		if (addedTocache)
		{
			//added to cache, so do something
			System.out.println("Successfully added to cache");
		}
		else
		{
			//could not add to cache
			System.out.println("Failed to write object with key "+position+" to cache");
			//System.out.println("Failed to write bitmap with identifier "+images[position]+ " to cache");
		}
	}
}