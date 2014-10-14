package com.example.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import utils.Utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.location.GpsStatus.NmeaListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.database.DatabaseManager;
import com.example.photo.Photo;
import com.example.photoapp.R;

public class DownloadPhotoTask extends AsyncTask<String, Integer, Photo>//Progress, Result>
{

	private Activity activity;
	//private Photo photo = null;;

	private Notification.Builder nBuilder = null;
	private NotificationManager mNotificationManager = null;
	//Id can be change for each download or just one.<<<<<<<<<<<----------
//	int notifyID = 1;
	int notifyID = Utils.generateNotifyId();

	private Callback<Photo> callbackOnTaskFinished;
	public void setCallbackOnTaskFinished(Callback<Photo> callbackFromActivity)
	{
		if( callbackFromActivity != null)
		{
			this.callbackOnTaskFinished = callbackFromActivity;
		}
		
	}
	
	public DownloadPhotoTask(Activity act)
	{
		this.activity = act;
	}
	

	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
		
		
		nBuilder = new Notification.Builder(activity.getApplicationContext())
			.setContentTitle("Photo Download")
			.setContentText("Download in progress")
			.setSmallIcon(R.drawable.ic_action_download);
		
	    mNotificationManager =
			    (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(notifyID, nBuilder.build());
		nBuilder.setProgress(100, 0, false);
				
	}
	@Override
	protected Photo doInBackground(String... photoIds) {

		URL url;
		long contenLength = 0;
		String photoId = photoIds[0];
		Photo photo = null;
		
		//JSONObject jsonMsg = ServerManager.getInstance(context).getPhotoAsJsonMessage(photo[0]);
		//String jsonMsgAsStr = jsonMsg.toString();
		HttpURLConnection httpConn;
		try 
		{
			url = new URL("http://" + ServerManager.SERVER_URL_BASE + ServerManager.DOWNLOAD_PHOTO_PATH);
			try 
			{
				httpConn = (HttpURLConnection) url.openConnection();
				//httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				httpConn.setUseCaches(false);
				httpConn.setRequestMethod("GET");
				//httpConn.setChunkedStreamingMode(0);
				httpConn.addRequestProperty("Content-Type","application/json");
				//httpConn.setRequestProperty("Content-Length", "" + 
			    //          jsonMsgAsStr.length());
				httpConn.addRequestProperty(Photo.PHOTO_ID, photoId);
				//----------------<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<disable gzip compression
				//httpConn.setRequestProperty("Accept-Encoding", "identity");
				
				//Log.v("UploadPhotoTask", "Json Message Length:" + jsonMsgAsStr.length());
				
				httpConn.connect();
				
				
				Log.v("DownloadPhotoTask", "Input stream ready.");
				contenLength = httpConn.getContentLength();

				//get response
				//Read response header
				
				//InputStream fromServer = httpConn.getInputStream();

				int responseCode = httpConn.getResponseCode();
				if ( responseCode == HttpURLConnection.HTTP_ACCEPTED )
				{
				}
				else
				{
				}
				//Deal with the response body
				
				InputStream fromServer = httpConn.getInputStream();
				int c;
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				int numOfBytes = 0;
				int counter = 0;
				while( (c = fromServer.read()) != -1)
				{
					baos.write(c);
					numOfBytes++;
					if( ++counter == 5000 )
					{
						publishProgress( (int)((numOfBytes*100)/contenLength) );
						counter = 0;
					}					
				}
				
				fromServer.close();
				
				httpConn.disconnect();
				
				Log.v("DownloadPhotoTask", "Bytes read: " + baos.size());
			
				//-------------------------------->>>>>>>>>>>>>>post execute
				String jsonMsg = baos.toString();
				baos.close();
				//Log.v("Json msg:" , jsonMsg);
			    photo = ServerManager.getInstance(activity.getApplicationContext()).getPhotoFromJsonMsg(jsonMsg);
		    	Log.v("Task bitmap" , photo.getPhotoID());
		    	//DatabaseManager.getInstance(activity.getApplicationContext()).addPhoto(photo, 80);
				
			} 
			catch (IOException e) 
			{
				return null;
				//e.printStackTrace();
			}
		
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return photo;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) 
	{
		super.onProgressUpdate(values);
		if(values[0] < 100 )
		{
			nBuilder.setProgress(100, values[0], false);
		}
		else
		{
			nBuilder.setProgress(0, 0, true);
		}
		mNotificationManager.notify(notifyID, nBuilder.build());		
	}

     protected void onPostExecute(Photo photo) 
     {
    	 if(photo == null)
    	 {
    		 ServerManager.getInstance(activity.getApplicationContext()).showUnavailbeServerMsg();
    		 nBuilder.setContentText("Download failed").setProgress(0, 0, false);
	   	 	 mNotificationManager.notify(notifyID, nBuilder.build());
    	 }
    	 else
    	 {
    		 nBuilder.setContentText("Download complete").setProgress(0, 0, false);
    		 mNotificationManager.notify(notifyID, nBuilder.build());
    		 
    	 }
    	 if( this.callbackOnTaskFinished != null )
    	 {
    		 this.callbackOnTaskFinished.OnTaskFinished(photo);
    	 }
     }
     @Override
    protected void onCancelled() 
     {
    	super.onCancelled();
   	 	nBuilder.setContentText("Download failed").setProgress(0, 0, false);
   	 	mNotificationManager.notify(notifyID, nBuilder.build());
    	
    }
	

}
