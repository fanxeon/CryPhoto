package com.example.photoapp;

//import com.example.android.displayingbitmaps.util.ImageCache;
//import com.example.android.displayingbitmaps.util.ImageCache.ImageCacheParams;
//import com.example.android.displayingbitmaps.util.ImageCache.RetainFragment;

import java.io.ByteArrayInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
//import android.graphics.Bitmap.CompressFormat;
//import android.graphics.Bitmap.Config;

public class ImageCache {

	private static ImageCache imageCache;
	private LruCache<String,byte[]> memCache;
	private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 2; // 2MB
	private static final int TO_KILO_BYTE = 1024; //divide byte count by this number to convert to KB
	private static final int TO_MEGA_BYTE = 1048576; //divide byte count by this number to get MB

	private ImageCache()
	{
		imageCache = null;
		emptyinit();
		//no-arg constructor
	}

	private ImageCache(int size)
	{
		imageCache = null;
		//size should be in kilobytes
		arginit(size);
	}

	public static ImageCache getInstance()
	{
		// No existing ImageCache, create one and store it in RetainFragment
		if (imageCache == null) {
			imageCache = new ImageCache();
		}
		return imageCache;
	}

	private void emptyinit()
	{
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / TO_KILO_BYTE);
		System.out.println("Max memory available is "+maxMemory);
		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory/8;
		
		System.out.println("Allocating "+ cacheSize+ " for the cache from the memory");
		//long memory = Runtime.getRuntime().maxMemory();
		
		memCache = new LruCache<String, byte[]>(cacheSize) {
			@Override
			protected int sizeOf(String key, byte[] array) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return array.length / 1024;
			}
		};
	}

	private void arginit(int size)
	{
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		//final int maxMemory = size;

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = size;

		memCache = new LruCache<String, byte[]>(cacheSize) {
			@Override
			protected int sizeOf(String key, byte[] array) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return array.length / 1024;
			}
		};
	}

	private boolean isSpaceAvail(byte[] array)
	{
		int count = (array.length/1024);
		//int bmcount = bm.getByteCount() / 1024;
		int max = memCache.maxSize();
		int currsize = memCache.size();
		System.out.println("The maximum size for this cache is "+max);
		System.out.println("The current size of the cache is "+currsize);
		System.out.println("The size of the bitmap to add is "+count);
		if ((max - currsize) > count)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean addBitmapToMemoryCache(String key, byte[] array) 
	{
		if (isSpaceAvail(array))
		{
			System.out.println("Space is available for this bitmap, can add it !");
			if (getBitmapFromMemCache(key) == null) {
				System.out.println("About to add bitmap to the cache with key "+key);
				memCache.put(key, array);
			}
			return true;
		}
		else
		{
			System.out.println("Space is unavailable for this bitmap, cannot add it !");
			return false;
		}
	}

	public Bitmap getBitmapFromMemCache(String key)
	{
		byte[] array = memCache.get(key);
		Bitmap decoded = null;
		if (array != null)
		{
			decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(array));
		}
		return decoded;
	}
}