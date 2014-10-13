package com.example.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.photo.Photo;

public class RemoteRemovePhotoTask extends AsyncTask<String, Void, String>//Progress, Result>
{
	String TAG = "RemoteRemovePhotoTask";

	private Activity activity;
	//private Photo photo = null;;

	private Callback<String> callbackOnTaskFinished;
	public void setCallbackOnTaskFinished(Callback<String> callbackFromActivity)
	{
		if( callbackFromActivity != null)
		{
			this.callbackOnTaskFinished = callbackFromActivity;
		}
		
	}
	public RemoteRemovePhotoTask(Activity act)
	{
		this.activity = act;
	}
	
	
	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
		
	}
	@Override
	protected String doInBackground(String... photoIds) {

		URL url;
		String photoId = photoIds[0];
		Photo photo = null;
		
		HttpURLConnection httpConn;
		try 
		{
			url = new URL("http://" + ServerManager.SERVER_URL_BASE + ServerManager.REMOVE_PHOTO_PATH);
			try 
			{
				httpConn = (HttpURLConnection) url.openConnection();
				//httpConn.setDoOutput(true);
				httpConn.setDoInput(false);
				httpConn.setUseCaches(false);
				httpConn.setRequestMethod("GET");
				//httpConn.setChunkedStreamingMode(0);
				//httpConn.addRequestProperty("Content-Type","application/json");
				//httpConn.setRequestProperty("Content-Length", "" + 
			    //          jsonMsgAsStr.length());
				
				//photo to be removed
				httpConn.addRequestProperty(Photo.PHOTO_ID, photoId);
				//----------------<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<disable gzip compression
				//httpConn.setRequestProperty("Accept-Encoding", "identity");
				//Log.v("UploadPhotoTask", "Json Message Length:" + jsonMsgAsStr.length());
				
				httpConn.connect();
				
				
				//Log.v(TAG, "");

				
				//Response
				
				//Read response header
				int responseCode = httpConn.getResponseCode();
				if ( responseCode == HttpURLConnection.HTTP_ACCEPTED )
				{
					//Toast.makeText(context, "Photo Saved on Server.", Toast.LENGTH_SHORT).show();
					return "OK";
				}
				else
				{
					//handle errors <<<<<<<<<<<<<-------------------
					//Toast.makeText(context, "Unable to Save on Server.", Toast.LENGTH_SHORT).show();
					return "error";
				}
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		
		} 
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//return photo;
		return "OK";
	}
	
     protected void onPostExecute(String result) 
     {
    	 if( this.callbackOnTaskFinished != null )
    	 {
    		 this.callbackOnTaskFinished.OnTaskFinished(result);
    	 }
     }
	

}
