package com.example.database;

import java.io.ByteArrayOutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

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
	
	private DatabaseManager()
	{
		context = PhotoViewerApplication.getPhotoViewerAppContext();
		dbHelper = new PhotoViewerDatabaseOpenHelper();
	}
	
	public static DatabaseManager getInstance()
	{
		if(instance == null)
		{
			instance = new DatabaseManager();
		}
		return instance;
	}
	
	public long insertPhoto(Photo newPhoto)
	{
	    ContentValues values = new ContentValues();
	    values.put(PhotoViewerDatabaseOpenHelper.COLUMN_ID, newPhoto.getPhotoID());
	    values.put(PhotoViewerDatabaseOpenHelper.COLUMN_NAME, newPhoto.getName());
	    values.put(PhotoViewerDatabaseOpenHelper.COLUMN_DESCRIPTION, newPhoto.getDescription());
	    values.put(PhotoViewerDatabaseOpenHelper.COLUMN_ALBUM, newPhoto.getAlbum());
	    values.put(PhotoViewerDatabaseOpenHelper.COLUMN_IS_UPLOADED_TO_SERVER, newPhoto.isUploadedToServerAsYesNO());
	    
	    //Compress the image data for the photo <<<<<<<<<---------Should we handle different format
	    //Is JPEG a good idea? since it is its compression reduces the data size?????
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    newPhoto.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
	    values.put(PhotoViewerDatabaseOpenHelper.COLUMN_BITMAP, outputStream.toByteArray());

	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    long rowID = db.replace(PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME, null, values);
	    db.close();
	    
	    return rowID;
		
	}
	
	public Photo retrievePhoto(String photoID)
	{
		//This where statement for select command
		String whereStr = PhotoViewerDatabaseOpenHelper.COLUMN_ID + " = '" + photoID + "'";
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor cursor = db.query(true, PhotoViewerDatabaseOpenHelper.PHOTOS_TABLE_NAME,
				PhotoViewerDatabaseOpenHelper.ALL_COLUMNS_PHOTO_TABLE, whereStr, null,
				null, null, null, null, null);
		
		Photo photo = new Photo();
		
		return photo;
	}
	
	
}
