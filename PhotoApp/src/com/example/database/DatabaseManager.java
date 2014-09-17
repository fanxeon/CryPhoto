package com.example.database;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.app.PhotoViewerApplication;
import com.example.photo.Photo;

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
	
	public long addPhoto(Photo newPhoto)
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
	    newPhoto.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
	    values.put(PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP, outputStream.toByteArray());

	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    long rowID = db.insert(PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME, null, values);
	    db.close();
	    
	    return rowID;
		
	}
	
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
	
		    byte[] data = cursor.getBlob(cursor
		    		.getColumnIndex(PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP));
		    photo.setBitmap( BitmapFactory.decodeByteArray(data, 0, data.length) );	
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
	
	
}
