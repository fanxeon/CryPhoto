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

	private String name;
	//A photo ID is the same as the timestamp.
	//A date taken for the photo in String, and the value of this attribute has to be in a unified 
	//format e.g yyyyMMdd_HHmmss. For consistency, the value has to be from the photo manager.
	private String photoID;
	
	//An image data for a photo
	private Bitmap bitmap;
	//Album name where a photo belongs to, if applicable. Otherwise, null
	private String album;
	
	
	public Photo(String name, String photoID, Bitmap bitmap) {
		//super();
		this.name = name;
		this.photoID = photoID;
		this.bitmap = bitmap;
	}

	public Photo(String name, String photoID, Bitmap bitmap, String album) {
		//super();
		this.name = name;
		this.photoID = photoID;
		this.bitmap = bitmap;
		this.album = album;
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

}
