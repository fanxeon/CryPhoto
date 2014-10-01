/**
 * 
 */
package com.example.photo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.app.PhotoViewerApplication;

import android.content.Context;

/**
 * This a (singleton) photo manager class, all  
 * @author yaldwyan
 *
 */
public class PhotoManager 
{
	public final String TIME_STAMP_FORMAT = "yyyyMMdd_HHmmss";
	private final String AUTONUMBER_PHOTO_FILE_NAME = "auto_num_photo_file";
	private final String AUTONUMBER_ALBUM_FILE_NAME = "auto_num_album_file";
	
	private static PhotoManager instance = null;
	private Context context = null;
	
	private long autoPhotoNum = 1;
	private long autoAlbumNum = 1;
	
	private PhotoManager(Context con)
	{
		context = con;
		//context = PhotoViewerApplication.getPhotoViewerAppContext();
		initializePhotoAlbumAutoNumber();//<<<<<<<<<<<<<--------------
	}
	
	public static PhotoManager getInstance(Context con)
	{
		if(instance == null)
		{
			instance = new PhotoManager(con);
		}
		return instance;
	}
	
	//Get the last auto number for photo and album as well from a persistent file, if applicable
	private void initializePhotoAlbumAutoNumber() 
	{
		FileInputStream photoFIS = null;
		try
		{
			photoFIS = context.openFileInput(AUTONUMBER_PHOTO_FILE_NAME);		
            BufferedReader reader = new BufferedReader(new InputStreamReader(photoFIS));
            String line = null, inputStr = "";
            
            try 
            {
				while( (line = reader.readLine()) != null )
				{ inputStr += line; }
				this.autoPhotoNum = Long.parseLong(inputStr);
			} 
            catch (IOException e) 
            {
				e.printStackTrace();
			}
		}
		//If file does not exist, i.e, first time running the application
		catch(FileNotFoundException fnfe)
		{
			writeAutoNumberPhotoToPersistantFile();
		}
	
		
		FileInputStream albumFIS = null;
		try
		{
			photoFIS = context.openFileInput(AUTONUMBER_ALBUM_FILE_NAME);		
            BufferedReader reader = new BufferedReader(new InputStreamReader(photoFIS));
            String line = null, inputStr = "";
            
            try 
            {
				while( (line = reader.readLine()) != null )
				{ inputStr += line; }
				this.autoAlbumNum = Long.parseLong(inputStr);
			} 
            catch (IOException e) 
            {
				e.printStackTrace();
			}
		}
		//If file does not exist, i.e, first time running the application
		catch(FileNotFoundException fnfe)
		{
			writeAutoNumberAlbumToPersistantFile();
		}
		
		//read current auto photo number and  
	}
	
	//Write a current auto photo number to a file, this method has to be called whenever generating
	//a new auto photo number.
	private void writeAutoNumberPhotoToPersistantFile()
	{
		try 
		{
			FileOutputStream photoFOS = context.openFileOutput(AUTONUMBER_PHOTO_FILE_NAME, Context.MODE_PRIVATE);
			String autoNumPhotoStr = Long.toString(autoPhotoNum);
			photoFOS.write(autoNumPhotoStr.getBytes());
			photoFOS.close();
		}
		catch (FileNotFoundException fnfe2) 
		{
			fnfe2.printStackTrace();
		}
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
		}
		
	}
	
	//Write a current auto album number to a file, this method has to be called whenever generating
	//a new auto album number.	
	private void writeAutoNumberAlbumToPersistantFile()
	{
		try 
		{
			FileOutputStream photoFOS = context.openFileOutput(AUTONUMBER_ALBUM_FILE_NAME, Context.MODE_PRIVATE);
			String autoNumPhotoStr = Long.toString(autoAlbumNum);
			photoFOS.write(autoNumPhotoStr.getBytes());
			photoFOS.close();
		}
		catch (FileNotFoundException fnfe2) 
		{
			fnfe2.printStackTrace();
		}
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
		}
		
	}
	//This methods return a new photo ID which is the current time stamp taken for a photo object 
	// e.g a new photo taken. This value has to be unique.
	public synchronized String getNewPhotoID()
	{
		return new SimpleDateFormat(TIME_STAMP_FORMAT).format(new Date());
	}
	

	//This method returns the time stamp as a date object. We are less likely going to use it.<<<-----
	public synchronized Date getCurrentTimeStampAsDate()
	{
		return new Date();
	}
	
	//This method returns the time stamp as a String.
	public synchronized String getCurrentTimeStampAsString()
	{
		return new SimpleDateFormat(TIME_STAMP_FORMAT).format(new Date());
	}

	public Date getTimeStampAsDate(String photoID)
	{
		Date timeStamp = null;
		try {
			timeStamp = new SimpleDateFormat(TIME_STAMP_FORMAT).parse(photoID);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeStamp;
	}
	
	public String getTimeStampAsString(Date timeStamp)
	{
		return new SimpleDateFormat(TIME_STAMP_FORMAT).format(timeStamp);
	}
	
//	public synchronized long getCurrentPhotoNumber()
//	{
//		return 1;
//	}
	
	//This generate a name for a new photo unless the user change when filling the name and
	//the description e.g photo_(3) 
	public synchronized String generatePhotoName()
	{
		return "photo_(" + generatePhotoNumber() + ")";
	}
	
	private long generatePhotoNumber()
	{
		autoPhotoNum++;
		//Write the new generated photo number to a file as soon as possible to avoid 
		//inconsistency, when the application crashes
		writeAutoNumberAlbumToPersistantFile();
		return autoPhotoNum;
	}	
	
	
	//This generate a name for a new album unless the user change when filling the name of the
	//the new album e.g album(8) 
	public synchronized String generateAlbumName()
	{
		return "album(" + generatePhotoNumber() + ")";
	}
	
	private long generateAlbumNumber()
	{
		autoAlbumNum++;
		//Write the new generated photo number to a file as soon as possible to avoid 
		//inconsistency, when the application crashes
		writeAutoNumberAlbumToPersistantFile();
		return autoAlbumNum;
	}
}
