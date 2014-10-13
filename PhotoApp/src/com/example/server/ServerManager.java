package com.example.server;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.database.DatabaseManager;
import com.example.database.PhotoViewerDatabaseOpenHelper;
import com.example.photo.Photo;
import com.example.photoapp.R;

/**
 * This a (singleton) server manager. It is resposible for communicating with a Http server.  
 * @author yaldwyan
 *
 */
public class ServerManager 
{
	public static final String SERVER_URL_BASE = "192.168.43.184:8199";
	public static final String UPLOAD_PHOTO_PATH = "/uploadphoto";
	public static final String DOWNLOAD_PHOTO_PATH = "/downloadphoto";
	public static final String REMOVE_PHOTO_PATH = "/removephoto";
	//public static final String RESTORE_PHOTOS = "/restorephotos";
	public static final String SYNC_PATH = "/sync";
	
	
	public static final String EMPTY = "empty";
	public static final String PHOTOIDS = "photoids";
	public static final String COUNTER = "counter";
	
	private static ServerManager instance = null;
	private Context context = null;
	
	private ServerManager(Context con)
	{
		//context = PhotoViewerApplication.getPhotoViewerAppContext();
		context = con;
	}
	
	public static ServerManager getInstance(Context con)
	{
		if(instance == null)
		{
			instance = new ServerManager(con);
		}
		return instance;
	}

    public boolean checkConnection() 
    {
        final ConnectivityManager cManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) 
        {
            Toast.makeText(context, "No Network Connection Found", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void showUnavailbeServerMsg()
    {
    	//Check first client device connection.
        final ConnectivityManager cManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) 
        {
            Toast.makeText(context, "Connection Failed", Toast.LENGTH_LONG).show();
            return;
        }
        //If connection is ok, then, the server is not availble.
        Toast.makeText(context, "Server Not Available. Try Later", Toast.LENGTH_LONG).show();
    }

    public void showUnableToSyncMsg()
    {
        Toast.makeText(context, "Unable to Sync. Try later", Toast.LENGTH_LONG).show();
    }
    
    public void showSyncMsg()
    {
        Toast.makeText(context, "Photo Viewer is synced", Toast.LENGTH_LONG).show();
    }
    
    public void showPhotoRemovedSuccesfullMsg()
    {
        Toast.makeText(context, "Photo Deleted from Server", Toast.LENGTH_LONG).show();
    }
    
    public void showPhotosRemovedSuccesfullMsg()
    {
        Toast.makeText(context, "Photos Deleted from Server", Toast.LENGTH_LONG).show();
    }    

    
    public String getPhotoAsJsonMessage(Photo photo)
	{
		JSONObject msg = new JSONObject();
		
		try 
		{
		    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		    photo.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			msg.put(Photo.BITMAP_DATA, Base64.encodeToString( outputStream.toByteArray(), Base64.DEFAULT));

			msg.put(Photo.PHOTO_ID, photo.getPhotoID());
			msg.put(Photo.DESCRIPTION, photo.getDescription());
			msg.put(Photo.ALBUM, photo.getAlbum());

			
		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return msg.toString();
	}

    //this method get photo ids from DB, put photo ids in json msg to be sent to server.
    public synchronized String getPhotoIdsAsJsonMsg()
    {
    	ArrayList<String> photoIds = DatabaseManager.getInstance(context).getPhotoIDs();
    	
    	JSONObject json = new JSONObject();
    	
    	JSONArray jsonArr = new JSONArray(photoIds);
    	try 
    	{
			json.put(ServerManager.PHOTOIDS, jsonArr);
		} 
    	catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return json.toString();
    }
    //This method converts missing photo ids json msg to an arraylist of ids as strings.
    public ArrayList<String> getPhotoIdsFromJsonMsg(String jsonMsg)
    {
    	ArrayList<String> missingPhotoIds= null;
    	JSONObject json = null;
    	JSONArray jsonArr = null;
    	if( jsonMsg != null )
    	{
    		
    		try 
    		{
				json = new JSONObject(jsonMsg);
				
				int len = json.getInt(ServerManager.COUNTER);
	    		missingPhotoIds = new ArrayList<String>();
			
				if(  len > 0)
				{

		    		jsonArr = json.getJSONArray(ServerManager.PHOTOIDS);
					
					for( int i = 0; i < len; i++)
					{
						String id = jsonArr.getString(i);
						if( id != null )
						{
							missingPhotoIds.add(id);
						}
					}
						
				}
				else
				{
					missingPhotoIds.add(new String(ServerManager.EMPTY));					
				}
				
			} 
    		catch (JSONException e) 
			{
				missingPhotoIds.add(new String(ServerManager.EMPTY));					
				e.printStackTrace();
			}
    		
    	}
    	return missingPhotoIds;
    }
    
    //Get photo details from json msg
	public Photo getPhotoFromJsonMsg(String jsonMsg)
	{
		//Log.v("DownloadPhotoTask", "Strinig: " + jsonMsg);

		Photo photo = new Photo();
		JSONObject json = null;
		try 
		{
			json = new JSONObject(jsonMsg);
			//Log.v("server manager", json.getString(Photo.BITMAP_DATA));
			byte[] data = Base64.decode(json.getString(Photo.BITMAP_DATA), Base64.DEFAULT);
		    photo.setBitmap( BitmapFactory.decodeByteArray(data, 0, data.length) );	

		    photo.setGridBitmap(Utils.getGridBitmapFromByteArray(data, context));
		    
			photo.setPhotoID(json.getString(Photo.PHOTO_ID));
			photo.setDescription((json.getString(Photo.DESCRIPTION)));
			photo.setAlbum(json.getString(Photo.ALBUM));
			photo.setUploadedToServer(true);
			

		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return photo;
	}
	

}
