package com.example.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.RuleBasedCollator;

import org.json.JSONObject;

import com.example.photo.Photo;
import com.example.photoapp.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class UploadPhotoTask extends AsyncTask<Photo, Integer, Boolean>//Progress, Result>
{

	private Activity activity;

	private Notification.Builder nBuilder = null;
	private NotificationManager mNotificationManager = null;
	//Id can be change for each download or just one.<<<<<<<<<<<----------
	int notifyID = 2;
	
	//boolean isOk = true;
	
	public UploadPhotoTask(Activity act)
	{
		this.activity = act;
	}
	
	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();

		nBuilder = new Notification.Builder(activity.getApplicationContext())
		.setContentTitle("Photo Upload")
		.setContentText("Upload in progress")
		.setSmallIcon(R.drawable.ic_action_upload);
	
		mNotificationManager =
				(NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(notifyID, nBuilder.build());
		nBuilder.setProgress(100, 0, false);
	}
	@Override
	protected Boolean doInBackground(Photo... photo) {

		URL url;
		int uploadFileSize = 0;
		String photoId = photo[0].getPhotoID();
		
		String jsonMsg = ServerManager.getInstance(activity.getApplicationContext()).getPhotoAsJsonMessage(photo[0]);
		HttpURLConnection httpConn;
		try {
			url = new URL("http://" + ServerManager.SERVER_URL_BASE + ServerManager.UPLOAD_PHOTO_PATH);
			try 
			{
				httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				httpConn.setUseCaches(false);
				httpConn.setRequestMethod("POST");
				httpConn.setChunkedStreamingMode(0);
				httpConn.addRequestProperty("Content-Type","application/json");
				//httpConn.setRequestProperty("Content-Length", "" + 
			    //          jsonMsgAsStr.length());
				httpConn.addRequestProperty(Photo.PHOTO_ID, photoId);
				httpConn.addRequestProperty("hello", "yasser");
				//Disable gzip compression
				//httpConn.setRequestProperty("Accept-Encoding", "identity");
				Log.v("UploadPhotoTask", "Json Message Length:" + jsonMsg.length());
				httpConn.connect();
				
				//uploadFileSize = httpConn.getContentLength();
				
				OutputStream toServer = httpConn.getOutputStream();
				Log.v("UploadPhotoTask", "Output stream ready.");
				
				byte[] bytes = jsonMsg.getBytes();
				int numberOfBytes = bytes.length;
				int c;
				int counter = 0;
				int progressCounter = 0;
    			while( counter < numberOfBytes )
    			{
    				c = bytes[counter];
    				toServer.write(c);
    				
    				counter++;
    				
					if( ++progressCounter == 200 )
					{
						publishProgress( (int)((counter*100)/numberOfBytes) );
						progressCounter = 0;
					}					
    				//Sleep for only testing
//    				try {
//    					Thread.sleep(100);
//   					} catch (InterruptedException e) {
//   						// TODO Auto-generated catch block
//   						e.printStackTrace();
//   					}
    			}

    			
//				OutputStreamWriter osw = new OutputStreamWriter(toServer);
//				osw.write(jsonMsg);
//				osw.close();

				//get response
				//InputStream fromServer = httpConn.getInputStream();

				int responseCode = httpConn.getResponseCode();
				if ( responseCode == HttpURLConnection.HTTP_ACCEPTED )
				{
				}
				else
				{
					//handle errors

				}
				//fromServer.close();
				
				httpConn.disconnect();
				
				

			} 
			catch (IOException e) 
			{
				return false;
				//e.printStackTrace();
			}
	
		
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
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
	
	@Override
	protected void onPostExecute(Boolean result) 
	{
		super.onPostExecute(result);
	   	 if(result == false)
    	 {
    		 ServerManager.getInstance(activity.getApplicationContext()).showUnavailbeServerMsg();
    		 nBuilder.setContentText("Upload failed").setProgress(0, 0, false);
	   	 	 mNotificationManager.notify(notifyID, nBuilder.build());
    	 }
    	 else
    	 {
    		 nBuilder.setContentText("Upload complete").setProgress(0, 0, false);
    		 mNotificationManager.notify(notifyID, nBuilder.build());
    	 }
	}
	
    @Override
    protected void onCancelled() 
    {
    	super.onCancelled();
  	 	nBuilder.setContentText("Upload failed").setProgress(0, 0, false);
  	 	mNotificationManager.notify(notifyID, nBuilder.build());   	
   }
}
