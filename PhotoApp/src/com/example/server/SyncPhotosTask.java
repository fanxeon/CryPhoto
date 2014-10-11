package com.example.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.database.DatabaseManager;
import com.example.photo.Photo;
import com.example.photoapp.R;

public class SyncPhotosTask extends AsyncTask<Void, Void, ArrayList<String>>//Progress, Result>
{

	private String TAG = "SyncPhotosTask";
	private Activity activity;
	private ArrayList<String> downloadedPhotoIds = new ArrayList<String>();

//	private Notification.Builder nBuilder = null;
//	private NotificationManager mNotificationManager = null;
//	//Id can be change for each download or just one.<<<<<<<<<<<----------
//	int notifyID = 2;
	
	//boolean isOk = true;
	private Callback<ArrayList<String>> callbackOnTaskFinished;
	public void setCallbackOnTaskFinished(Callback<ArrayList<String>> callbackFromActivity)
	{
		if( callbackFromActivity != null)
		{
			this.callbackOnTaskFinished = callbackFromActivity;
		}
		
	}
	
	public SyncPhotosTask(Activity act)
	{
		this.activity = act;
	}
	
	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();

//		nBuilder = new Notification.Builder(activity.getApplicationContext())
//		.setContentTitle("Photo Upload")
//		.setContentText("Upload in progress")
//		.setSmallIcon(R.drawable.ic_action_upload);
//	
//		mNotificationManager =
//				(NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
//		// mId allows you to update the notification later on.
//		mNotificationManager.notify(notifyID, nBuilder.build());
//		nBuilder.setProgress(100, 0, false);
	}
	@Override
	protected ArrayList<String> doInBackground(Void...Void) {

		URL url;
//		int uploadFileSize = 0;
//		String photoId = photo[0].getPhotoID();
		ArrayList<String> photoIds = null;
		
		String jsonMsg = ServerManager.getInstance(activity.getApplicationContext()).getPhotoIdsAsJsonMsg();
		Log.v(TAG, "Available ids at DB:" + jsonMsg);
		HttpURLConnection httpConn;
		try {
			url = new URL("http://" + ServerManager.SERVER_URL_BASE + ServerManager.SYNC_PATH);
			try 
			{
				httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				httpConn.setUseCaches(false);
				httpConn.setRequestMethod("POST");
				httpConn.setChunkedStreamingMode(0);
				httpConn.addRequestProperty("Content-Type","application/json");
				httpConn.setConnectTimeout(10000);
				//httpConn.setRequestProperty("Content-Length", "" + 
			    //          jsonMsgAsStr.length());
				//Disable gzip compression
				//httpConn.setRequestProperty("Accept-Encoding", "identity");
				Log.v(TAG, "Json Message Length:" + jsonMsg.length());
				httpConn.connect();
				
				//uploadFileSize = httpConn.getContentLength();
				
				OutputStream toServer = httpConn.getOutputStream();
				Log.v("RestorePhotos", "Output stream ready.");
				
				byte[] bytes = jsonMsg.getBytes();
				int numberOfBytes = bytes.length;
				int c;
				int counter = 0;
//				int progressCounter = 0;
    			while( counter < numberOfBytes )
    			{
    				c = bytes[counter];
    				toServer.write(c);
    				
    				counter++;
     			}
    			toServer.close();

				//get response
				int responseCode = httpConn.getResponseCode();
				if ( responseCode == HttpURLConnection.HTTP_ACCEPTED )
				{
				}
				else
				{
					//handle errors
				}

				//Get json message containing photo ids that are not avaiable in client side.
				InputStream fromServer = httpConn.getInputStream();
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
//				int numOfBytes = 0;
//				int counter = 0;
				while( (c = fromServer.read()) != -1)
				{
					baos.write(c);
//					numOfBytes++;
//					if( ++counter == 500 )
//					{
//						publishProgress( (int)((numOfBytes*100)/contenLength) );
//						counter = 0;
//					}			get(0).equalsIgnoreCase(ServerManager.EMPTY) 		
				}
				
				fromServer.close();												
				//fromServer.close();				
				httpConn.disconnect();
		
				//Get json message from server
				String jsonMsgFromServer = baos.toString();
				baos.close();
				
				
				//Should this be handled here or in grid view activity process <<<<<<<<<--------------------
				//The question is how to 
				photoIds = ServerManager.getInstance(activity.getApplicationContext())
						.getPhotoIdsFromJsonMsg(jsonMsgFromServer);
				
				this.numberOfExpectedPhotos = photoIds.size();
				
				if( !photoIds.get(0).equalsIgnoreCase( ServerManager.EMPTY ) )
				{
					for (String photoId : photoIds )
					{
						DownloadPhotoTask downloadPhotoTask = new DownloadPhotoTask(activity);
						downloadPhotoTask.execute(photoId);
						downloadPhotoTask.setCallbackOnTaskFinished(new Callback<Photo>() {			
							@Override
							public void OnTaskFinished(Photo photo) {
								if(photo != null)
								{	
									
									//this method will be called by downloadPhotoTask when it finish downloading
						    		//Should we add the downloaded photo to DB here???????????<<<<<<<<<<<<<<<<<<<--------
						    		DatabaseManager.getInstance(activity.getApplicationContext()).addPhoto(photo,80);
						    		//incrNumOfSuccessfulDownloadPhotos(photo.getPhotoID());
						    		downloadedPhotoIds.add(photo.getPhotoID());		
						    		Log.v("OnTaskFinished sync", photo.getPhotoID());
						    		//<<<<<<<<<<<<<<<<<<<<<----------------------- HAS to be complete
						    		//add new downloaded photo id to grid view array each time a photo downloaded
						    		//this method should be called since each download task for each photo will call it. 
						    		// e.g. refreshAdapter ( photo.getPhotoID() );
						    		//then the adapter will retrieve the photo object from db, and show it in the grid.
								}	
								else
								{
									
								}
								incrementFinishedDownLoadTasks();
								if( getFinishDownloadTasks() == numberOfExpectedPhotos )
								{
									done = true;
								   	if( callbackOnTaskFinished != null )
								   	{
								   		//Log.v("end", downloadedPhotoIds.get(0));
								   		callbackOnTaskFinished.OnTaskFinished(downloadedPhotoIds);
								   	}
									
								}
								
							}
						});
					}
				}
				else
				{
					return photoIds;
				}
				
				

			} 
			catch (SocketTimeoutException stoe)
			{
				return null;
				
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
	
		
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		return photoIds;
	}
	
	boolean done = false;
	
	int numberOfExpectedPhotos = 0;
    int  finishedDownloadsTasks = 0;
    public synchronized void incrementFinishedDownLoadTasks()
    {
    	finishedDownloadsTasks++;
    }
    public synchronized int getFinishDownloadTasks()
    {
    	return finishedDownloadsTasks;
    }
	
	//private int numOfSuccessDownloadPhotos = 0;
//	private  int incrNumOfSuccessfulDownloadPhotos(String newPhotoID)
//	{
//		numOfSuccessDownloadPhotos++;
//		//downloadedPhotoIds.add(newPhotoID);		
//		return numOfSuccessDownloadPhotos;
//	}
//	
//	private  int getNumOfSuccessfulDownloadPhotos()
//	{		
//		return numOfSuccessDownloadPhotos;
//	}

	@Override
	protected void onPostExecute(ArrayList<String> photoIds) 
	{
		super.onPostExecute(photoIds);
	   	if( photoIds == null)
    	{
	   		ServerManager.getInstance(activity.getApplicationContext()).showUnavailbeServerMsg();
    	}
    	else if ( (!photoIds.get(0).equalsIgnoreCase(ServerManager.EMPTY)) && (photoIds.size() > downloadedPhotoIds.size()) )
    	{
	   		//ServerManager.getInstance(activity.getApplicationContext()).showUnableToSyncMsg();
    		
    	}
    	else
    	{
	   		ServerManager.getInstance(activity.getApplicationContext()).showSyncMsg();		
    	}
//	   	if( this.callbackOnTaskFinished != null )
//	   	{
//	   		//Log.v("end", downloadedPhotoIds.get(0));
//	   		this.callbackOnTaskFinished.OnTaskFinished(downloadedPhotoIds);
//	   	}
	
	}
	
    @Override
    protected void onCancelled() 
    {
    	super.onCancelled();

   }
}
