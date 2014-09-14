package com.example.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.RuleBasedCollator;

import com.example.photo.Photo;

import android.os.AsyncTask;

public class uploadPhotoTask extends AsyncTask<Photo, Void, Void>//Progress, Result>
{

	private String urlStr;
	
	public uploadPhotoTask(String urlS)
	{
		this.urlStr = urlS;
	}
	@Override
	protected Void doInBackground(Photo... photo) {

		URL url;
		HttpURLConnection uploadHttpConn;
		try {
			url = new URL(urlStr);
			try 
			{
				uploadHttpConn = (HttpURLConnection) url.openConnection();
				uploadHttpConn.setDoInput(true);

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
		
		return null;
	}
	

}
