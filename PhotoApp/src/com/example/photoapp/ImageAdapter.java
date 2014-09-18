package com.example.photoapp;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{

	private Context mContext;
	int[] images;

	   // Constructor
	   public ImageAdapter(Context c) {
	      mContext = c;
	      images = GridActivity.images;
	    		  //initarray();
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
	      return images.length;
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
	      if (convertView == null) {
	      imageView = new ImageView(mContext);
	      int px = convertDpToPixel(150,mContext);
	      System.out.println("The value of view width and height is now " + px + "," + px);
	      imageView.setLayoutParams(new GridView.LayoutParams(px,px));
	      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	      imageView.setPadding(8, 8, 8, 8);
	      } else {
	      imageView = (ImageView) convertView;
	      }

	      imageView.setImageResource(images[position]);
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