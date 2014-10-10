package utils;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

public class Utils 
{

	public final static int GRID_IMAGE_VIEW_SIZE_DP = 100;
	public final static int GRID_VIEW_HOR_SPACING = 1;
	public final static int GRID_VIEW_VER_SPACING = 1;
	public static ArrayList<String> list;
	
	public static Bitmap getBitmapFromFile(String filePath)
	{
//		final BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		//BitmapFactory.decodeResource(res, resId, options);
//		BitmapFactory.decodeFile(photoPath, options);
//
//		// Calculate inSampleSize
//		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		//options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath);

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

	public static Bitmap getGridBitmapFromFile(String filePath, Context context)
	{
		//Bitmap gridBitmap = null;
		//int targetW = imgView.getWidth();
		//int targetH = imgView.getHeight();
//		int reqWidthdp = getResources().getInteger(R.dimen.grid_view_width);
//		int reqHeightdp = getResources().getInteger(R.dimen.grid_view_height);
		int reqWidthdp = 150;
		int reqHeightdp = 150;
		int reqWidth = convertDpToPixel(reqWidthdp, context);
		int reqHeight = convertDpToPixel(reqHeightdp, context);
//		// Get the dimensions of the bitmap
//		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//		//bmOptions.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(photoPath, bmOptions);
//		int photoW = bmOptions.outWidth;
//		int photoH = bmOptions.outHeight;
//
//		// Determine how much to scale down the image
//		int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//		// Decode the image file into a Bitmap sized to fill the View
//		bmOptions.inJustDecodeBounds = false;
//		bmOptions.inSampleSize = scaleFactor;
//		bmOptions.inPurgeable = true;
//
//		Bitmap imgBitmap = BitmapFactory.decodeFile(photoPath, bmOptions);

		//First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		//BitmapFactory.decodeResource(res, resId, options);
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);

	}
	
	public static Bitmap getBitmapFromByteArray(byte[] array, Context context, int reqWidth, int reqHeight)
	{
		//Bitmap gridBitmap = null;
		//int targetW = imgView.getWidth();
		//int targetH = imgView.getHeight();
//		int reqWidthdp = getResources().getInteger(R.dimen.grid_view_width);
//		int reqHeightdp = getResources().getInteger(R.dimen.grid_view_height);
		int reqWidthdp = 150;
		int reqHeightdp = 150;
		//int reqWidth = convertDpToPixel(reqWidthdp, context);
		//int reqHeight = convertDpToPixel(reqHeightdp, context);
//		// Get the dimensions of the bitmap
//		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//		//bmOptions.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(photoPath, bmOptions);
//		int photoW = bmOptions.outWidth;
//		int photoH = bmOptions.outHeight;
//
//		// Determine how much to scale down the image
//		int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//		// Decode the image file into a Bitmap sized to fill the View
//		bmOptions.inJustDecodeBounds = false;
//		bmOptions.inSampleSize = scaleFactor;
//		bmOptions.inPurgeable = true;
//
//		Bitmap imgBitmap = BitmapFactory.decodeFile(photoPath, bmOptions);

		//First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		//BitmapFactory.decodeResource(res, resId, options);
		BitmapFactory.decodeByteArray(array,0,array.length, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(array,0,array.length, options);

	}

}
