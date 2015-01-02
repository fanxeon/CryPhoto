package database;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import photo.Photo;
import photo.PhotoManager;

import utils.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import app.PhotoViewerApplication;


/**
 * This a (singleton) database manager class.  
 * @author yaldwyan
 *
 */
public class DatabaseManager 
{

	private static DatabaseManager instance = null;
	private Context context = null;
	//This object will create/open a database, and has a SQLiteDatabase object, which can do all
	//sql commnads. From this helper you can get writable/readable database.
	private PhotoViewerDatabaseOpenHelper dbHelper = null;

	private DatabaseManager(Context con)
	{
		//context = PhotoViewerApplication.getPhotoViewerAppContext();
		context = con;
		dbHelper = new PhotoViewerDatabaseOpenHelper(con);
	}

	public static DatabaseManager getInstance(Context con)
	{
		if(instance == null)
		{
			instance = new DatabaseManager(con);
		}
		return instance;
	}

//	public long addPhoto(Photo newPhoto)
//	{
//		ContentValues values = new ContentValues();
//		values.put(PhotoViewerDatabaseOpenHelper.COLUMN_ID, newPhoto.getPhotoID());
//		//values.put(PhotoViewerDatabaseOpenHelper.COLUMN_NAME, newPhoto.getName());
//		values.put(PhotoViewerDatabaseOpenHelper.COLUMN_DESCRIPTION, newPhoto.getDescription());
//		values.put(PhotoViewerDatabaseOpenHelper.COLUMN_ALBUM, newPhoto.getAlbum());
//		values.put(PhotoViewerDatabaseOpenHelper.COLUMN_IS_UPLOADED_TO_SERVER, newPhoto.isUploadedToServerAsYesNO());
//
//		//Compress the image data for the photo <<<<<<<<<---------Should we handle different format
//		//Is JPEG a good idea? since it is its compression reduces the data size?????
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		newPhoto.getBitmap().compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
//		values.put(PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP, outputStream.toByteArray());
//
//		if(newPhoto.getGridBitmap() != null )
//		{
//			ByteArrayOutputStream os = new ByteArrayOutputStream();
//			newPhoto.getGridBitmap().compress(Bitmap.CompressFormat.JPEG, 80, os);
//			values.put(PhotoViewerDatabaseOpenHelper.COLUMN_GRID_BITMAP, os.toByteArray());
//		}
//		else
//		{
//
//		}
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//		long rowID = db.insert(PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME, null, values);
//		db.close();
//
//		return rowID;
//
//	}
	public synchronized long addPhoto(Photo newPhoto, int quality)
	{
		ContentValues values = new ContentValues();
		values.put(PhotoViewerDatabaseOpenHelper.COLUMN_ID, newPhoto.getPhotoID());
		//values.put(PhotoViewerDatabaseOpenHelper.COLUMN_NAME, newPhoto.getName());
		values.put(PhotoViewerDatabaseOpenHelper.COLUMN_DESCRIPTION, newPhoto.getDescription());
		values.put(PhotoViewerDatabaseOpenHelper.COLUMN_ALBUM, newPhoto.getAlbum());
		values.put(PhotoViewerDatabaseOpenHelper.COLUMN_IS_UPLOADED_TO_SERVER, newPhoto.isUploadedToServerAsYesNO());

		//Compress the image data for the photo <<<<<<<<<---------Should we handle different format
		//Is JPEG a good idea? since it is its compression reduces the data size?????
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		newPhoto.getBitmap().compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
		values.put(PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP, outputStream.toByteArray());

		if(newPhoto.getGridBitmap() != null )
		{
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			newPhoto.getGridBitmap().compress(Bitmap.CompressFormat.JPEG, quality, os);
			values.put(PhotoViewerDatabaseOpenHelper.COLUMN_GRID_BITMAP, os.toByteArray());
		}
		else
		{
//			byte[] data = outputStream.toByteArray();
//			Bitmap bm = Utils.decodeSampledBitmapFromByteArray(data, 0, data.length, Utils.widthAtDp, Utils.hieghtAtDp);
			

		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowID = db.insert(PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME, null, values);
		db.close();

		return rowID;

	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) 
	{
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
            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }

		return inSampleSize;
	}

//	public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, int offset,
//			int length, int reqWidth, int reqHeight) {
//
//		// First decode with inJustDecodeBounds=true to check dimensions
//		final BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeByteArray(data, offset, length, options);
//
//		// Calculate inSampleSize
//		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//		// Decode bitmap with inSampleSize set
//		options.inJustDecodeBounds = false;
//		return BitmapFactory.decodeByteArray(data, offset, length, options);
//	}


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


	//compress to jpeg format
	private byte[] compressTojpeg(Bitmap bm)
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] array = null;

		if(bm.compress(Bitmap.CompressFormat.JPEG, 100, os))
		{
			System.out.println("Compressed the bitmap down to JPEG");
			//Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), images[i]);
			//String string = ""+position;
			array = os.toByteArray();
			System.out.println("The size of the bitmap is now "+(array.length/1024));
		}

		return array;
	}

//	//This method will be called by cache object.
//	public byte[] getCompressedBitmap(String photoID, int reqWidth, int reqHeight)
//	{
//		Bitmap photoBitmap = null;
//		byte[] array = null;
//		//This where statement for select command
//		String whereStr = PhotoViewerDatabaseOpenHelper.COLUMN_ID + " = '" + photoID + "'";
//
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		String[] projection = { PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP};
//
//		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
//				projection, whereStr, null,
//				null, null, null, null, null);
//
//		if ( cursor.moveToFirst() )
//		{	
//
//			byte[] data = cursor.getBlob(cursor
//					.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP));
//			//BitmapFactory.Options has to be added to scale down the resolution for gird view
//			photoBitmap =  decodeSampledBitmapFromByteArray(data, 0, data.length, reqWidth, reqHeight);
//			System.out.println("Scaled down the bitmap to 300, 300 and is now of size "+ (photoBitmap.getByteCount()/1024));
//			array = compressTojpeg(photoBitmap);
//		}
//
//		cursor.close();
//
//		return array;
//	}

	//This method should be called by individual mode since we need all information about the photo <<<<<<<<<<-----------
	//Retrieve all information about a photo for an individual view, resolution 100%
	public Photo getPhoto(String photoID)
	{
		Photo photo = null;
		//This where statement for select command
		String whereStr = PhotoViewerDatabaseOpenHelper.COLUMN_ID + " = '" + photoID + "'";

		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				PhotoViewerDatabaseOpenHelper.ALL_COLUMNS_PHOTO_TABLE, whereStr, null,
				null, null, null, null, null);

		if ( cursor.moveToFirst() )
		{	
			photo = new Photo();
			photo.setPhotoID(photoID);
			photo.setAlbum( cursor.getString(cursor
					.getColumnIndex( PhotoViewerDatabaseOpenHelper.COLUMN_ALBUM) ) );
			photo.setDescription( cursor.getString(cursor
					.getColumnIndex( PhotoViewerDatabaseOpenHelper.COLUMN_DESCRIPTION) ) );
			//photo.setName( cursor.getString(cursor
			//		.getColumnIndex( PhotoViewerDatabaseOpenHelper.COLUMN_NAME) ) );
			photo.setUploadedToServer( cursor.getString(cursor
					.getColumnIndex( PhotoViewerDatabaseOpenHelper.COLUMN_IS_UPLOADED_TO_SERVER) ) );

			//Ignore bitmap variable in photo object since we may not need it, use gridbitmap
			//for now.
			byte[] data = cursor.getBlob(cursor
					.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP));
			photo.setBitmap( BitmapFactory.decodeByteArray(data, 0, data.length) );	
//			byte[] data = cursor.getBlob(cursor
//			.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_GRID_BITMAP));
//			photo.setGridBitmap( BitmapFactory.decodeByteArray(data, 0, data.length) );	
		}

		cursor.close();
		return photo;
	}
	public Photo getPhoto(String photoID, int reqWidth, int reqHieght)
	{
		Photo photo = null;
		//This where statement for select command
		String whereStr = PhotoViewerDatabaseOpenHelper.COLUMN_ID + " = '" + photoID + "'";

		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				PhotoViewerDatabaseOpenHelper.ALL_COLUMNS_PHOTO_TABLE, whereStr, null,
				null, null, null, null, null);

		if ( cursor.moveToFirst() )
		{	
			photo = new Photo();
			photo.setPhotoID(photoID);
			photo.setAlbum( cursor.getString(cursor
					.getColumnIndex( PhotoViewerDatabaseOpenHelper.COLUMN_ALBUM) ) );
			photo.setDescription( cursor.getString(cursor
					.getColumnIndex( PhotoViewerDatabaseOpenHelper.COLUMN_DESCRIPTION) ) );
			//photo.setName( cursor.getString(cursor
			//		.getColumnIndex( PhotoViewerDatabaseOpenHelper.COLUMN_NAME) ) );
			photo.setUploadedToServer( cursor.getString(cursor
					.getColumnIndex( PhotoViewerDatabaseOpenHelper.COLUMN_IS_UPLOADED_TO_SERVER) ) );

			//Ignore bitmap variable in photo object since we may not need it, use gridbitmap
			//for now.
			byte[] data = cursor.getBlob(cursor
					.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP));
			photo.setBitmap( Utils.decodeSampledBitmapFromByteArray(data, 0, data.length, reqWidth, reqHieght) );	
//			byte[] data = cursor.getBlob(cursor
//			.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_GRID_BITMAP));
//			photo.setGridBitmap( BitmapFactory.decodeByteArray(data, 0, data.length) );	
		}

		cursor.close();
		return photo;
	}

	public Photo getPhotoWithoutBitmaps(String photoID)
	{
		Photo photo = null;
		//This where statement for select command
		String whereStr = PhotoViewerDatabaseOpenHelper.COLUMN_ID + " = '" + photoID + "'";

		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				PhotoViewerDatabaseOpenHelper.ALL_COLUMNS_PHOTO_TABLE_NO_BITMAPS, whereStr, null,
				null, null, null, null, null);

		if ( cursor.moveToFirst() )
		{	
			photo = new Photo();
			photo.setPhotoID(photoID);
			photo.setAlbum( cursor.getString(cursor
					.getColumnIndex( PhotoViewerDatabaseOpenHelper.COLUMN_ALBUM) ) );
			photo.setDescription( cursor.getString(cursor
					.getColumnIndex( PhotoViewerDatabaseOpenHelper.COLUMN_DESCRIPTION) ) );
			//photo.setName( cursor.getString(cursor
			//		.getColumnIndex( PhotoViewerDatabaseOpenHelper.COLUMN_NAME) ) );
			photo.setUploadedToServer( cursor.getString(cursor
					.getColumnIndex( PhotoViewerDatabaseOpenHelper.COLUMN_IS_UPLOADED_TO_SERVER) ) );

			//Ignore bitmap variable in photo object since we may not need it, use gridbitmap
			//for now.
//			byte[] data = cursor.getBlob(cursor
//					.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP));
//			photo.setBitmap( BitmapFactory.decodeByteArray(data, 0, data.length) );	
//			byte[] data = cursor.getBlob(cursor
//			.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_GRID_BITMAP));
//			photo.setGridBitmap( BitmapFactory.decodeByteArray(data, 0, data.length) );	
		}

		cursor.close();
		return photo;
	}

	//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<---------------------------
	//This method will be called when grid view activity created. Then the activity will pass the arraylist of ids to its adapdter.
	//The adapter will ask the cache object to get bitmaps for photos from DB asynchronously
	//This will improve the performance of the application
	//This method returns photos' ids
	public ArrayList<String> getPhotoIDs()
	{
		ArrayList<String> photoIDs = new ArrayList<String>();

		SQLiteDatabase db = dbHelper.getReadableDatabase();	
		String[] projection = { PhotoViewerDatabaseOpenHelper.COLUMN_ID};

		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				projection, null, null,
				null, null, null, null, null);

		if ( cursor.moveToFirst() )
		{
			do
			{
				photoIDs.add( cursor.getString( cursor
						.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_ID) ) );
			}
			while(cursor.moveToNext());

		}

		return photoIDs;
	}
	public ArrayList<Photo> getPhotosDescriptions()
	{
		ArrayList<Photo> photosDescriptions = new ArrayList<Photo>();

		SQLiteDatabase db = dbHelper.getReadableDatabase();	
		String[] projection = { PhotoViewerDatabaseOpenHelper.COLUMN_ID
				, PhotoViewerDatabaseOpenHelper.COLUMN_DESCRIPTION};

		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				projection, null, null,
				null, null, null, null, null);

		if ( cursor.moveToFirst() )
		{
			do
			{
				Photo photo = new Photo();
				photo.setPhotoID(cursor.getString( cursor
						.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_ID)));
				photo.setDescription(cursor.getString( cursor
						.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_DESCRIPTION)));
				
				photosDescriptions.add( photo );
			}
			while(cursor.moveToNext());

		}

		return photosDescriptions;
	}

	//This method will be called by cache object.
	public Bitmap getBitmap(String photoID, int reqWidth, int reqHeight)
	{
		Bitmap photoBitmap = null;
		//This where statement for select command
		String whereStr = PhotoViewerDatabaseOpenHelper.COLUMN_ID + " = '" + photoID + "'";

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] projection = { PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP};

		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				projection, whereStr, null,
				null, null, null, null, null);

		if ( cursor.moveToFirst() )
		{	
			
			byte[] data = cursor.getBlob(cursor
					.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP));
			//BitmapFactory.Options has to be added to scale down the resolution for gird view
			photoBitmap = Utils.getBitmapFromByteArray(data, context, reqWidth, reqHeight);
			
			//photoBitmap =  BitmapFactory.decodeByteArray(data, 0, data.length);
		}

		cursor.close();


		return photoBitmap;
	}

	//This method will be called by cache object.
	public Bitmap getBitmap(String photoID)
	{
		Bitmap photoBitmap = null;
		//This where statement for select command
		String whereStr = PhotoViewerDatabaseOpenHelper.COLUMN_ID + " = '" + photoID + "'";

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] projection = { PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP};

		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				projection, whereStr, null,
				null, null, null, null, null);

		if ( cursor.moveToFirst() )
		{	

			byte[] data = cursor.getBlob(cursor
					.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP));
			//BitmapFactory.Options has to be added to scale down the resolution for gird view
			photoBitmap =  BitmapFactory.decodeByteArray(data, 0, data.length);
		}

		cursor.close();


		return photoBitmap;
	}
	
	public boolean isSavedOnServer(String photoID)
	{
		boolean saved = false;
		String saveOnServerStr = Photo.NO;
		//This where statement for select command
		String whereStr = PhotoViewerDatabaseOpenHelper.COLUMN_ID + " = '" + photoID + "'";

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] projection = { PhotoViewerDatabaseOpenHelper.COLUMN_IS_UPLOADED_TO_SERVER};

		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				projection, whereStr, null,
				null, null, null, null, null);

		if ( cursor.moveToFirst() )
		{	
			saveOnServerStr = 
					cursor.getString(cursor
							.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_IS_UPLOADED_TO_SERVER));

		}

		if( saveOnServerStr.equalsIgnoreCase(Photo.YES))
			saved = true;
		
		cursor.close();

		return saved;
	}

	public Bitmap getGridBitmap(String photoID)
	{
		Bitmap photoBitmap = null;
		//This where statement for select command
		String whereStr = PhotoViewerDatabaseOpenHelper.COLUMN_ID + " = '" + photoID + "'";

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] projection = { PhotoViewerDatabaseOpenHelper.COLUMN_GRID_BITMAP};

		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				projection, whereStr, null,
				null, null, null, null, null);

		if ( cursor.moveToFirst() )
		{	

			byte[] data = cursor.getBlob(cursor
					.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_GRID_BITMAP));
			//BitmapFactory.Options has to be added to scale down the resolution for gird view
			photoBitmap =  BitmapFactory.decodeByteArray(data, 0, data.length);	
		}

		cursor.close();


		return photoBitmap;
	}

	public byte[] getGridBitmapAsBytes(String photoID)
	{
		//Bitmap photoBitmap = null;
		byte[] data = null;
		//This where statement for select command
		String whereStr = PhotoViewerDatabaseOpenHelper.COLUMN_ID + " = '" + photoID + "'";

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] projection = { PhotoViewerDatabaseOpenHelper.COLUMN_GRID_BITMAP};

		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				projection, whereStr, null,
				null, null, null, null, null);

		if ( cursor.moveToFirst() )
		{	

			data = cursor.getBlob(cursor
					.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_GRID_BITMAP));
		}

		cursor.close();


		return data;
	}

	//<<<<<<<<<----------CAlling this method may affect the performance
	//This method should return ID and bitmap data for all photos in db to be used by gridview
	//Also, the bitmap has be scaled to improve performance.
	public ArrayList<Photo> getAllPhotos()
	{
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				PhotoViewerDatabaseOpenHelper.ALL_COLUMNS_PHOTO_TABLE, null, null,
				null, null, null, null, null);

		//This method not completed yet. Do we really need it ???????<<<<<<<<<------------


		return new ArrayList<>();
	}

	

	public ArrayList<String> getAlbumNames()
	{
		ArrayList<String> albumNames = new ArrayList<String>();

		SQLiteDatabase db = dbHelper.getReadableDatabase();	
		String[] projection = { PhotoViewerDatabaseOpenHelper.COLUMN_NAME};

		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.ALBUM_TABLE_NAME,
				projection, null, null,
				null, null, null, null, null);

		if ( cursor.moveToFirst() )
		{
			do
			{
				albumNames.add( cursor.getString( cursor
						.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_NAME) ) );
			}
			while(cursor.moveToNext());

		}

		return albumNames;
		
	}
	
	public boolean insertAlbum(String newAlbum)
	{
		if(newAlbum != null)
		{
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(PhotoViewerDatabaseOpenHelper.COLUMN_NAME, newAlbum);

		
			long rowId = db.insert( PhotoViewerDatabaseOpenHelper.ALBUM_TABLE_NAME, null, values);
		
			db.close();
		
			if(rowId == -1)
				return false;
		}
		else
			return false;
		
		return true;

	}
	public int deleteAlbum(String albumName)
	{
		int numRows = 0;
		if( albumName != null)
		{
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			String selection = PhotoViewerDatabaseOpenHelper.COLUMN_NAME + " = ?";
			String[] selectionArgs = {albumName};
		    numRows = 
					db.delete(PhotoViewerDatabaseOpenHelper.ALBUM_TABLE_NAME, selection, selectionArgs);
		}
		else
		{
			return 0;
		}
		return numRows;
	}
	
	public int deletePhoto(String photoId)
	{
		int numRows = 0;
		if( photoId != null)
		{
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			String selection = PhotoViewerDatabaseOpenHelper.COLUMN_ID + " = ?";
			String[] selectionArgs = {photoId};
		    numRows = 
					db.delete(PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME, selection, selectionArgs);
		}
		else
		{
			return 0;
		}
		return numRows;	
	}

	public int deletePhotos(ArrayList<String> photoIds)
	{
		int n = 0;
		for( String photoID : photoIds )
		{
			if( deletePhoto(photoID) != 0)
				n++;
		}
		return n;
	}

	public int updateSavedOnServer(String photoId, boolean isSaved)
	{
		int c = 0;
		if( photoId != null)
		{
			String savedStr = null;
			
			if(isSaved)
				savedStr = Photo.YES;
			else
				savedStr = Photo.NO;
			
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			String selection = PhotoViewerDatabaseOpenHelper.COLUMN_ID + " = ?";
			String[] selectionArgs = {photoId};
			
			ContentValues values = new ContentValues();
			values.put(PhotoViewerDatabaseOpenHelper.COLUMN_IS_UPLOADED_TO_SERVER, savedStr);
			
			c = db.update(PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME, values, selection, selectionArgs);
			
			db.close();
		}
		else
			return 0;
		return c;
		
	}
	
	public ArrayList<String> getPhotoIdsForAlbum(String albumName)
	{
		ArrayList<String> photoIDs = new ArrayList<>();
		if( albumName != null)
		{
			//This where statement for select command
		
			String whereStr = PhotoViewerDatabaseOpenHelper.COLUMN_ALBUM + " = '" + albumName + "'";

			SQLiteDatabase db = dbHelper.getReadableDatabase();

			Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				new String[]{PhotoViewerDatabaseOpenHelper.COLUMN_ID}, whereStr, null,
				null, null, null, null, null);

			if ( cursor.moveToFirst() )
			{	
				//photoIDs = new ArrayList<String>();
				do
				{
					photoIDs.add( cursor.getString( cursor
							.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_ID) ) );
				}
				while(cursor.moveToNext());

			}
	
			cursor.close();
			db.close();
		}
		return photoIDs;
	}


}
