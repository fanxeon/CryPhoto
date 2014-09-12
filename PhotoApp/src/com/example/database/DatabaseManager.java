package com.example.database;

import android.content.Context;

import com.example.app.PhotoViewerApplication;

/**
 * This a (singleton) database manager class.  
 * @author yaldwyan
 *
 */
public class DatabaseManager 
{

	
	private static DatabaseManager instance = null;
	private Context context = null;
	
	
	private DatabaseManager()
	{
		context = PhotoViewerApplication.getPhotoViewerAppContext();
	}
	
	public static DatabaseManager getInstance()
	{
		if(instance == null)
		{
			instance = new DatabaseManager();
		}
		return instance;
	}
	
	
	
}
