package photoapp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.example.photoapp.R;

import database.DatabaseManager;import photoapp.FetchFromCacheTask;import photoapp.ImageCache;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter
{

	private Context mContext;
	//private int[] images;
	private ArrayList<String> list;
	private ArrayList<String> templist;
	private ImageCache cache;
	boolean replaced = false;
	
//	private int reqWidth = 300;
//	private int reqHeight = 300;

	// Constructor
	public ImageAdapter(Context c, ImageCache cache) {
		mContext = c;
		//images = GridActivity.images;
		//list = GridActivity.getList();
		//initarray();
		this.cache = cache;
		System.out.println("Just got an instance of the Image Cache");
		//putincache();
	}
	
	public ArrayList<String> getList()
	{
		return list;
	}
	
	public void setList(ArrayList<String> array)
	{
		list = array;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public void setListToAll()
	{
		list =  DatabaseManager.getInstance(mContext).getPhotoIDs();
	}
	
	public void getAllList()
	{
		if (replaced)
		{
			list = templist;
		}
	}
	
	public void UpdateGridView()
	{
		this.notifyDataSetChanged();
	}

	public void replaceList(ArrayList<String> positions)
	{
		//templist = (ArrayList<String>) list.clone(); //save the values in the array list
		synchronized(list)
		{
			list.clear();
			list = positions;
			//list.addAll(positions);
		}
		replaced = true;
	}

	public void addSingleToList(String id)
	{
		synchronized(list)
		{
			list.add(id);
		}
	}

	public void addManyToList(ArrayList<String> positions)
	{
		synchronized(list)
		{
			list.addAll(list.size() -1, positions);
		}
	}

	public void removeManyFromList(int[] positions)
	{
		int i;
		for(i=0; i < positions.length; i++)
		{
			synchronized(list)
			{
				list.remove(positions[i]);
			}
		}
	}

	public void removeSingle(int position)
	{
		//int i;
		synchronized(list)
		{
			list.remove(position);
		}
	}

	static class AsyncDrawable extends BitmapDrawable 
	{
		private final WeakReference<FetchFromCacheTask> FetchTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap, FetchFromCacheTask fetchtask) 
		{
			super(res, bitmap);
			FetchTaskReference = new WeakReference<FetchFromCacheTask>(fetchtask);
		}

		public FetchFromCacheTask getFetchTask() 
		{
			return FetchTaskReference.get();
		}
	}

	private static FetchFromCacheTask getBitmapWorkerTask(ImageView imageView) {

		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getFetchTask();
			}
		}
		return null;
	}

	public boolean cancelPotentialWork(String position, ImageView imageView) 
	{
		final FetchFromCacheTask fetchTask = getBitmapWorkerTask(imageView);

		if (fetchTask != null) {

			final String bitmapData = fetchTask.getPosition();
			// If bitmapData is not yet set or it differs from the new data
			if (bitmapData.equals("") || !(bitmapData.equals(position))) {
				// Cancel previous task
				fetchTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was cancelled
		return true;
	}

	public void loadBitmap(String position, ImageView imageView) 
	{
		if (cancelPotentialWork(position, imageView)) 
		{
			final FetchFromCacheTask task = new FetchFromCacheTask(imageView,mContext,cache);
			BitmapDrawable drw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.empty_photo);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(),drw.getBitmap(),task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute(position);
		}
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
			//imageView.setBackgroundColor(Color.WHITE);
			int pdx = convertDpToPixel(5,mContext);
			imageView.setPadding(pdx,pdx ,pdx ,pdx );
		} 
		else 
		{
			imageView = (ImageView) convertView;
		}

		//set the image resource for the image view
		//first check whether available in the cache
		loadBitmap(list.get(position),imageView);
		//		FetchFromCacheTask fetchtask = new FetchFromCacheTask(imageView, mContext, cache);
		//		fetchtask.execute(list.get(position));
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
