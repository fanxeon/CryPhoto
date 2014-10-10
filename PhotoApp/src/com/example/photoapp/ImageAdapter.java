package com.example.photoapp;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import utils.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
		//putincache();
	}

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
				//int px = convertDpToPixel(mContext.getResources().getDimensionPixelSize(R.dimen.grid_img_view_height),mContext);
				int px = mContext.getResources().getDimensionPixelSize(R.dimen.grid_img_view_height);
				System.out.println("The value of view width and height is now " + px + "," + px);
				imageView.setLayoutParams(new GridView.LayoutParams(px,px));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setBackgroundColor(Color.WHITE);
				int pdx = convertDpToPixel(5,mContext);
				imageView.setPadding(pdx,pdx ,pdx ,pdx );
//				FetchFromCacheTask fetchtask = new FetchFromCacheTask(imageView, mContext, cache);
//				fetchtask.execute(list.get(position));
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
	}
