package com.example.photoapp;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import utils.Utils;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{

	private Context mContext;
	//private int[] images;
	private ArrayList<String> list;
	private ImageCache cache;
	private int reqWidth = 300;
	private int reqHeight = 300;

	// Constructor
	public ImageAdapter(Context c, ImageCache cache) {
		mContext = c;
		//images = GridActivity.images;
		list = GridActivity.getList();
		//initarray();
		this.cache = cache;
		System.out.println("Just got an instance of the Image Cache");
		putincache();
	}

	//	public static int calculateInSampleSize(
	//			BitmapFactory.Options options, int reqWidth, int reqHeight) {
	//		// Raw height and width of image
	//		final int height = options.outHeight;
	//		final int width = options.outWidth;
	//		int inSampleSize = 1;
	//
	//		if (height > reqHeight || width > reqWidth) {
	//
	//			final int halfHeight = height / 2;
	//			final int halfWidth = width / 2;
	//
	//			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
	//			// height and width larger than the requested height and width.
	//			while ((halfHeight / inSampleSize) > reqHeight
	//					&& (halfWidth / inSampleSize) > reqWidth) {
	//				inSampleSize *= 2;
	//			}
	//		}
	//
	//		return inSampleSize;
	//	}
	//	
	//	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	//			int reqWidth, int reqHeight) {
	//
	//		// First decode with inJustDecodeBounds=true to check dimensions
	//		final BitmapFactory.Options options = new BitmapFactory.Options();
	//		options.inJustDecodeBounds = true;
	//		BitmapFactory.decodeResource(res, resId, options);
	//
	//		// Calculate inSampleSize
	//		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	//
	//		// Decode bitmap with inSampleSize set
	//		options.inJustDecodeBounds = false;
	//		return BitmapFactory.decodeResource(res, resId, options);
	//	}

	//System.out.println("Decoding resource into bitmap");
	//	Bitmap bm = decodeSampledBitmapFromResource(mContext.getResources(),images[i],300,300);
	//	System.out.println("Scaled down the bitmap to 300, 300 and is now of size "+ (bm.getByteCount()/1024));
	//	ByteArrayOutputStream os = new ByteArrayOutputStream();
	//	if(bm.compress(Bitmap.CompressFormat.JPEG, 100, os))
	//	{
	//		System.out.println("Compressed the bitmap down to JPEG");
	//		//Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), images[i]);
	//		String string = ""+images[i];
	//		System.out.println("Attempting to add bitmap to cache");
	//		byte[] array = os.toByteArray();
	//		System.out.println("The size of the bitmap is now "+(array.length/1024));
	//		if(!cache.addBitmapToMemoryCache(string, array))
	//		{
	//			System.out.println("Failed to write bitmap with identifier "+images[i]+ " to cache");
	//		}
	//	}
	//	else
	//	{
	//		System.out.println("Failed to compress the bitmap to JPEG format");
	//	}

	//working properly
	private void putincache()
	{
		System.out.println("Attempting to add stuff to cache");
		int i;
		for(i=0; i < list.size(); i++)
		{
			//Integer pos = Integer.valueOf(list.get(i));
			//FetchFromCacheTask fttask = new FetchFromCacheTask();
			AddToCacheTask task = new AddToCacheTask(mContext,cache,reqWidth,reqHeight);
			task.execute(list.get(i));
			boolean done = false;
			//boolean interrupted = false;
//			while(!done)
//			{
//				try
//				{
//					boolean b = task.get();
//					if(b)
//					{
//						done = true;
//					}
//					else
//					{
//						done = true;
//					}
//				}
//				catch(InterruptedException in)
//				{
//					//interrupted = false;
//					done = false;
//				}
//				catch(ExecutionException e)
//				{
//					done = true;
//					System.out.println("Encountered an executopn exception");
//				}
//			}
		}
	}

		//	   private void initarray()
		//	   {
		//		   int[] images = mContext.getResources().getIntArray(R.array.ImageRef);
		//		   images = new int[] { R.drawable.img1, R.drawable.img2,
		//				      R.drawable.img3, R.drawable.img1,
		//				      R.drawable.img2, R.drawable.img3,
		//				      R.drawable.img1, R.drawable.img2,
		//				      R.drawable.img3, R.drawable.img1,
		//				      R.drawable.img2, R.drawable.img3,
		//				      R.drawable.img1, R.drawable.img2,
		//				      R.drawable.img3, R.drawable.img1,
		//				      R.drawable.img2, R.drawable.img3,
		//				      R.drawable.img1, R.drawable.img2,
		//				      R.drawable.img3, R.drawable.img1,
		//				      R.drawable.img2, R.drawable.img3,
		//				   };
		//		   this.images = images;
		//	   }

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public static int convertDpToPixel(int dp, Context context)
		{
			Resources resources = context.getResources();
			DisplayMetrics metrics = resources.getDisplayMetrics();
			double dpx = dp * (metrics.densityDpi / 160.0);
			Double d = Double.valueOf(dpx);
			int px = d.intValue();
			return px;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;

			if (convertView == null) 
			{
				//set properties of the image view
				imageView = new ImageView(mContext);
				int px = convertDpToPixel(mContext.getResources().getInteger(R.dimen.grid_img_view_height),mContext);
				System.out.println("The value of view width and height is now " + px + "," + px);
				imageView.setLayoutParams(new GridView.LayoutParams(px,px));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}

			//set the image resource for the image view
			//first check whether available in the cache
			FetchFromCacheTask fetchtask = new FetchFromCacheTask(imageView, mContext, cache);
			fetchtask.execute(list.get(position));
//			boolean done = false;
//			while(!done)
//			{
//				try
//				{
//					Bitmap b = fetchtask.get();
//					done = true;
//				}
//				catch(InterruptedException in)
//				{
//					//interrupted = false;
//					done = false;
//				}
//				catch(ExecutionException e)
//				{
//					done = true;
//					System.out.println("Encountered an executopn exception");
//				}
//				catch(Exception e)
//				{
//					done = true;
//					System.out.println("Encountered an exception");
//				}
//			}
//			Bitmap bm = cache.getBitmapFromMemCache(""+images[position]);
//			if (bm != null)
//			{
//				imageView.setImageBitmap(bm);
//			}
//			else
//			{
//				//			not available in the cache
//				imageView.setImageResource(images[position]);
//			}

			return imageView;
		}

		//	   // Keep all Images in array
		//	   public Integer[] mThumbIds = {
		//	      R.drawable.img1, R.drawable.img2,
		//	      R.drawable.img3, R.drawable.img1,
		//	      R.drawable.img2, R.drawable.img3,
		//	      R.drawable.img1, R.drawable.img2,
		//	      R.drawable.img3, R.drawable.img1,
		//	      R.drawable.img2, R.drawable.img3,
		//	      R.drawable.img1, R.drawable.img2,
		//	      R.drawable.img3, R.drawable.img1,
		//	      R.drawable.img2, R.drawable.img3,
		//	      R.drawable.img1, R.drawable.img2,
		//	      R.drawable.img3, R.drawable.img1,
		//	      R.drawable.img2, R.drawable.img3,
		//	   };
	}
