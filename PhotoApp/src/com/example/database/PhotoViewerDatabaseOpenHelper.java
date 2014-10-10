package com.example.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.app.PhotoViewerApplication;
import com.example.photo.Photo;

public class PhotoViewerDatabaseOpenHelper extends SQLiteOpenHelper
{
	//Database details
	private static final String DATABASE_NAME = "photoviewerdb";
	//NOT SURE what version should we use <<<<<<<<<<<<<<<<<<<<<<<<----------------
	private static final int DATABASE_VERSION = 4;
	
	//Information needed for photos table.
	protected static final String PHOTOS_TABLE_NAME = "photos";
	//Columns details, we can add or remove columns starting from here <<<<<<<<----------
	protected static final String COLUMN_ID = "id";
	protected static final String COLUMN_NAME = "name";
	protected static final String COLUMN_DESCRIPTION = "description";
	protected static final String COLUMN_ALBUM = "album";
	protected static final String COLUMN_BITMAP = "bitmap";
	protected static final String COLUMN_GRID_BITMAP = "gridbitmap";
	//The value here is either yes or no.
	protected static final String COLUMN_IS_UPLOADED_TO_SERVER = "uploadedtoserver";
	protected static final String[] ALL_COLUMNS_PHOTO_TABLE = { COLUMN_ID, 
		COLUMN_DESCRIPTION,COLUMN_ALBUM, COLUMN_BITMAP,COLUMN_GRID_BITMAP, COLUMN_IS_UPLOADED_TO_SERVER };

	protected static final String[] ALL_COLUMNS_PHOTO_TABLE_NO_BITMAPS = { COLUMN_ID, 
		COLUMN_DESCRIPTION,COLUMN_ALBUM, COLUMN_IS_UPLOADED_TO_SERVER };	//NOT SURE We may need it, may be local url or remote url???<<<<<<<<<<<<<<<<<<<---------------
	//private static final String COLUMN_URL = "url";

	//SQL command to create photos table.
	protected static final String PHOTOS_TABLE_CREATE =
    "CREATE TABLE " + PHOTOS_TABLE_NAME + " (" + COLUMN_ID 
    	+ " TEXT PRIMARY KEY," + COLUMN_DESCRIPTION + " TEXT," 
    	+ COLUMN_ALBUM + " TEXT," + COLUMN_BITMAP + " BLOB," 
    	+  COLUMN_GRID_BITMAP + " BLOB," + COLUMN_IS_UPLOADED_TO_SERVER 
    	+ " TEXT);";

    //Information needed for album table.
	protected static final String ALBUM_TABLE_NAME = "albums";
	//This column keeps the number of photos belongs to an album
	protected static final String COLUMN_PHOTOS_NUM = "photonums";
	protected static final String[] ALL_COLUMNS_ALBUM_TABLE = { COLUMN_NAME, COLUMN_PHOTOS_NUM };

	//SQL command to create album table.
    private static final String ALBUM_TABLE_CREATE =
    "CREATE TABLE " + ALBUM_TABLE_NAME + " (" + COLUMN_NAME 
    	+ " TEXT PRIMARY KEY," + COLUMN_PHOTOS_NUM + " INTEGER);";

    PhotoViewerDatabaseOpenHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	    
	    
    //This methods will be called when the database first time created
    @Override 
    public void onCreate(SQLiteDatabase db)
    {
    	//Create tables once
    	db.execSQL(PHOTOS_TABLE_CREATE);
    	db.execSQL(ALBUM_TABLE_CREATE);
    	initial(db);
	}
    private void initial(SQLiteDatabase db)
    {
		ContentValues values = new ContentValues();
		values.put(PhotoViewerDatabaseOpenHelper.COLUMN_NAME, Photo.NONE);
		db.insert( PhotoViewerDatabaseOpenHelper.ALBUM_TABLE_NAME, null, values);

    }
    //This methods will be called whenever the database is open
    @Override 
    public void onOpen(SQLiteDatabase db){
    	//Do something ever time database opens.
    	//NOT SURE we can put photos on the memory cache immediately.??? 
    	//to improve the response <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<,<-----------
	}
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) 
    {  
    	 db.execSQL("DROP TABLE IF EXISTS " + PHOTOS_TABLE_NAME);
    	 db.execSQL("DROP TABLE IF EXISTS " + ALBUM_TABLE_NAME);
         onCreate(db);    	
    }

}
