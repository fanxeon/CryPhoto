package app;

import android.app.Application;
import android.content.Context;

/**
 * This class is used to access global application info from outside any activity context. * 
 * This class has to be registered by its name in AndroidManifest.xml's <application> tag ,i.e., 
 * <application android:name="com.example.app.PhotoViewerApplication">
 * @author yaldwyan
 *
 */
public class PhotoViewerApplication extends Application
{
	private static Context context = null;
	
	public void onCreat()
	{
		super.onCreate();
		PhotoViewerApplication.context = getApplicationContext();
	}
	
	public static Context getPhotoViewerAppContext()
	{
		return context;
	}
}
