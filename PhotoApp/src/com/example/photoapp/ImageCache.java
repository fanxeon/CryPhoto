package com.example.photoapp;

//import com.example.android.displayingbitmaps.util.ImageCache;
//import com.example.android.displayingbitmaps.util.ImageCache.ImageCacheParams;
//import com.example.android.displayingbitmaps.util.ImageCache.RetainFragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;
import android.graphics.Bitmap;
//import android.graphics.Bitmap.CompressFormat;
//import android.graphics.Bitmap.Config;

public class ImageCache {

	private static ImageCache bitmapCache;
	private LruCache<String,Bitmap> memCache;
	
	private ImageCache()
	{
		bitmapCache = null;
		emptyinit();
		//no-arg constructor
	}
	
	private ImageCache(int size)
	{
		bitmapCache = null;
		arginit(size);
	}
	
	public static ImageCache getInstance()
	{
        // No existing ImageCache, create one and store it in RetainFragment
        if (bitmapCache == null) {
            bitmapCache = new ImageCache();
        }
        
        return bitmapCache;
    }
	
	private void emptyinit()
	{
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;

	    memCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount() / 1024;
	        }
	    };
	}
	
	private void arginit(int size)
	{
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = size;

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;

	    memCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount() / 1024;
	        }
	    };
	}
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        memCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(String key) {
	    return memCache.get(key);
	}
}