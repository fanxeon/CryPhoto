package photo;

public class Album {
	
	//This is a name and id as well, so it has to be unique, and whenever create an album  object,
	//it has to be added to database or a persistant file<<<<<<<<<<<<<<<<???????.
	private String albumName = null;
	
	public Album()
	{
		//Get a name from  a generator if 
	}
	
	public Album(String albumName)
	{
		this.albumName = albumName;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

}
