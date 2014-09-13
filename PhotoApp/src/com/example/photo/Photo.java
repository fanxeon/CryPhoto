/**
 * 
 */
package com.example.photo;

import android.graphics.Bitmap;

/**
 * This class contains all information for a photo.
 * @author yaldwyan
 *
 */
public class Photo 
{	

	private final String YES = "yes";
	private final String NO = "no";
	private final String NONE = "none";
	
	private String name;
	//A photo ID is the same as the timestamp.
	//A date taken for the photo in String, and the value of this attribute has to be in a unified 
	//format e.g yyyyMMdd_HHmmss. For consistency, the value has to be from the photo manager.
	private String photoID;
	
	//A photo decription
	private String description;
	
	//An image data for a photo
	private Bitmap bitmap;
	//Album name where a photo belongs to, if applicable. Otherwise, null
	private String album = NONE;
	
	private boolean isUploadedToServer = false;
	
	public Photo() {
	}	
	
	public Photo(String name, String photoID, Bitmap bitmap, boolean uploadedToServer) {
		//super();
		this.name = name;
		this.photoID = photoID;
		this.bitmap = bitmap;
		this.isUploadedToServer = uploadedToServer;
	}

	public Photo(String name, String photoID, Bitmap bitmap, String album,
			boolean uploadedToServer) {
		//super();
		this.name = name;
		this.photoID = photoID;
		this.bitmap = bitmap;
		this.album = album;
		this.isUploadedToServer = uploadedToServer;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhotoID() {
		return photoID;
	}
	public void setPhotoID(String photoID) {
		this.photoID = photoID;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public boolean isUploadedToServer() {
		return isUploadedToServer;
	}
	public String isUploadedToServerAsYesNO()
	{
		if(isUploadedToServer)
			return YES;
		
		return NO;
	}
	public void setUploadedToServer(boolean isUploadedToServer) {
		this.isUploadedToServer = isUploadedToServer;
	}
	
	public void setUploadedToServer(String isUploadedToServerStr) {
		if(isUploadedToServerStr.equalsIgnoreCase(YES))
		{
			this.isUploadedToServer = true;
		}
		else
		{
			this.isUploadedToServer = false;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
