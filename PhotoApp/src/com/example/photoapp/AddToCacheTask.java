package com.example.photoapp;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class AddToCacheTask extends AsyncTask<Integer,Void,Boolean> {

	//private final WeakReference<ImageView> imageViewReference;
	//private int data = 0;
	private Context context; 
	private ImageCache cache;
	private int[] images;

	public AddToCacheTask(Context context, ImageCache cache) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		//imageViewReference = new WeakReference<ImageView>(imageView);
		this.context = context;
		this.cache = cache;
	}

	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	// Decode image in background.
	@Override
	protected Boolean doInBackground(Integer... params) 
	{
		int position = params[0].intValue();
		images = context.getResources().getIntArray(R.array.ImgRef);
		//data = params[0];
		//return decodeSampledBitmapFromResource(params[0].getResources(), data, 100, 100);
		System.out.println("Decoding resource into bitmap");
		Bitmap bm = decodeSampledBitmapFromResource(context.getResources(),position,300,300);
		System.out.println("Scaled down the bitmap to 300, 300 and is now of size "+ (bm.getByteCount()/1024));
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		if(bm.compress(Bitmap.CompressFormat.JPEG, 100, os))
		{
			System.out.println("Compressed the bitmap down to JPEG");
			//Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), images[i]);
			String string = ""+position;
			System.out.println("Attempting to add bitmap to cache");
			byte[] array = os.toByteArray();
			System.out.println("The size of the bitmap is now "+(array.length/1024));
			synchronized(cache)
			{
				if(!cache.addBitmapToMemoryCache(string, array))
				{
					System.out.println("Failed to write bitmap with identifier "+images[position]+ " to cache");
					return false;
				}
				return true;
			}
		}
		else
		{
			System.out.println("Failed to compress the bitmap to JPEG format");
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
		}
		else
		{
			//could not add to cache
			//System.out.println("Failed to write bitmap with identifier "+images[position]+ " to cache");
		}
	}
}