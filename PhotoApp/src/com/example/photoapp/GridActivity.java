package com.example.photoapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import utils.Utils;

import com.example.activities.TesterActivity;
import com.example.database.DatabaseManager;
import com.example.photoapp.R;
import com.example.photoapp.R.id;
import com.example.photoapp.R.layout;
import com.example.photoapp.R.menu;
import com.example.database.SpinnerNavItem;
import com.example.photo.Photo;
import com.example.photo.PhotoManager;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.EditText;

public class GridActivity extends Activity implements OnNavigationListener {

	public final static String EXTRA_MESSAGE = "com.example.photoapp.MESSAGE";
	public final static String STRING_LIST = "string_id_list";
	public final static String STRING_ID = "string_id";
	private static ArrayList<String> list;
	protected static int[] images;
	//-- NEW CONSTRUCTION: Contextual Action Bar Declaration--//
	private TextView mHelloTextView;
	private ActionMode mActionMode;
	//-- END --//
	
	//-- ACTION BAR IMPLMENTATION DECLARATION @ Fan --//
	private ActionBar actionBar;
	// Title navigation Spinner data
	private ArrayList<SpinnerNavItem> navSpinner;
	// Navigation adapter
	private TitleNavigationAdapter adapter;
	// Refresh menu item
	private MenuItem refreshMenuItem;
	//-- ACTION BAR END --//
	
	
	public static void getActivityManager(Context context)
    {
      ActivityManager result = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
      if (result == null)
      {
        throw new UnsupportedOperationException("Could not retrieve ActivityManager");
      }
      System.out.println("The maximum memory for this application is "+result.getLargeMemoryClass());
      //return result;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("Hit the oncreate of grid activity");
		try
		{
			getActivityManager(getApplicationContext());
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		//set the layout properties for this activity as in the xml file
		setContentView(R.layout.activity_main);
		System.out.println("Just set the layout for the activity");
		
		//initialize cache and fragment
		ImageCache cache = getImageCache(this.getFragmentManager());
		
		//this will set up an array with references to images which will be used by the adapter later
		initarray(); //this will retrieve string IDs from the database manager
		System.out.println("Just initialized the array of string ids");
		
		//-- ACTION BAR IMPLMENTATION DECLARATION @ Fan --//
		actionBar = getActionBar();
		// Hide the action bar title
		actionBar.setDisplayShowTitleEnabled(false);
		// Enabling Spinner dropdown navigation
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// Spinner title navigation data
		navSpinner = new ArrayList<SpinnerNavItem>();
		navSpinner.add(new SpinnerNavItem("Sort by", R.drawable.ic_action_sort_by_size));
		navSpinner.add(new SpinnerNavItem("Album", R.drawable.ic_action_collection));
		navSpinner.add(new SpinnerNavItem("Date", R.drawable.ic_action_time));
		navSpinner.add(new SpinnerNavItem("Name", R.drawable.ic_action_view_as_grid));
		// title drop down adapter
		adapter = new TitleNavigationAdapter(getApplicationContext(),
				navSpinner);
		// assigning the spinner navigation
		actionBar.setListNavigationCallbacks(adapter, this);
		// Changing the action bar icon
		// actionBar.setIcon(R.drawable.ico_actionbar);
		//-- ACTION BAR END --//
		
		//set up drop-down menu
		//create an array adapter which will supply views for the drop down menu
		//SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
		//R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
		
		//sets up the up button on the action bar for the user to navigate backwards
		getActionBar().setDisplayHomeAsUpEnabled(true);
		System.out.println("About to get the grid view by finding it using the id name");
		
		//get the gridview as defined in the associate xml file
		GridView gridview = (GridView) findViewById(R.id.gridview);
		
		//set the adapter for the grid view
	    gridview.setAdapter(new ImageAdapter(this,cache));
	    
	    //set on item click listener
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(GridActivity.this, "" + position+", "+v.getHeight(), Toast.LENGTH_SHORT).show();
	            Indiview(v,position);
	        }
	    });
	    
	    /** NEW CONSTRUCTION: Contextual Action Bar Method @ Fan **/
	    /*mHelloTextView = (TextView) findViewById (R.id.action_test);
	    mHelloTextView.setOnLongClickListener(new OnLongClickListener(){
	    	@Override
	    	public boolean onLongClick(View v){
	    		MyActionModeCallback callback = new MyActionModeCallback();
	    		mActionMode = startActionMode (callback);
	    		mActionMode.setTitle(R.string.menu_context_title);
	    		return true;
			}
	    });*/
	}

	private ImageCache getImageCache(FragmentManager fragmentManager) {
		
		return ImageCache.getInstance(fragmentManager);
	}

	class MyActionModeCallback implements ActionMode.Callback{

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.context, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			float textSize = mHelloTextView.getTextSize();
			switch (item.getItemId()){
				//Should be Share and discard,
				//but now using change text font size
				//for test purpose
				case R.id.action_share:
					mHelloTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
							textSize + 2);
				case R.id.action_discard:
					mHelloTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
							textSize - 2);
			}
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// TODO Auto-generated method stub
			
		}
		
	}
    /** END OF CAB  **/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		 // Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_action_bar, menu);
		
		// ACTION BAR IMPLMENTATION
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		// ACTION BAR END
	    return super.onCreateOptionsMenu(menu);
	}

	/**
	 * On selecting action bar icons 
	 * ACTION BAR @ Fan
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
			case R.id.action_search:
				openSearch();
				return true;
			case R.id.action_photo:
				// Take photo
				capturePhotoAndSaveIt();
//				// TEST : Yasser test
//				Intent intent2 = new Intent(this, TesterActivity.class);
//				startActivity(intent2);
				return true;
			case R.id.action_help:
				// help action
				return true;
			case R.id.action_restore:
				// restore action
				return true;
	        case R.id.action_settings:
	            openSettings();
	    		Intent intent = new Intent(this, TesterActivity.class);
	    		//EditText editText = (EditText) findViewById(R.id.edit_message);
	    		//String message = editText.getText().toString();
	    		startActivity(intent);
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
//	private void takePhoto() {
//		// TODO Auto-generated method stub
//		
//	}

	private void openSettings() {
		// TODO Auto-generated method stub
		Context context = getApplicationContext();
		Toast.makeText(context, "Test Settings Toast !!!", Toast.LENGTH_SHORT).show();		
	}

	private void openSearch() 
	{
		// TODO Auto-generated method stub
		Context context = getApplicationContext();
		Toast.makeText(context, "Test Search Toast !!!", Toast.LENGTH_SHORT).show();	
	}
	/**
	 * Async task to load the data from server
	 * **/
	private class SyncData extends AsyncTask<String, Void, String> {
		@SuppressLint("NewApi") @Override
		protected void onPreExecute() {
			// set the progress bar view
			refreshMenuItem.setActionView(R.layout.action_progressbar);
			refreshMenuItem.expandActionView();
		}

		@Override
		protected String doInBackground(String... params) {
			// not making real request in this demo
			// for now we use a timer to wait for sometime
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@SuppressLint("NewApi") @Override
		protected void onPostExecute(String result) {
			refreshMenuItem.collapseActionView();
			// remove the progress bar view
			refreshMenuItem.setActionView(null);
		}
	};
	// ACTION BAR END
	

	/** Called when the user clicks the Send button */
	public void Indiview(View view, int position) {
	    // Do something in response to button
		Intent intent = new Intent(this, IndividualActivity.class);
		//EditText editText = (EditText) findViewById(R.id.edit_message);
		//String message = editText.getText().toString();
		intent.putExtra(STRING_ID, list.get(position));
		intent.putStringArrayListExtra(STRING_LIST, list);
		startActivity(intent);
	}
	
	private void initarray()
	{
		DatabaseManager db = DatabaseManager.getInstance(this.getApplicationContext());
		setList(db.getPhotoIDs());		
//		   this.images = getResources().getIntArray(R.array.ImgRef);
//		   this.images = new int[] { R.drawable.img1, R.drawable.img2,
//				      R.drawable.img3, R.drawable.img1,
//				      R.drawable.img2, R.drawable.img3,
//				      R.drawable.img1, R.drawable.img2,
//				      R.drawable.img3, R.drawable.img1,
//				      R.drawable.img2, R.drawable.img3,
//				      R.drawable.img1, R.drawable.img2,
//				      R.drawable.img3, R.drawable.img1,
//				      R.drawable.img2, R.drawable.img3,
//				      R.drawable.img1, R.drawable.img2,
//				      R.drawable.img3, R.drawable.img1,
//				      R.drawable.img2, R.drawable.img3,
//				   };
		   //this.images = images;
	   }
	//public final static String EXTRA_MESSAGE = "com.example.photoapp.MESSAGE";

	public static ArrayList<String> getList() {
		return list;
	}

	public static void setList(ArrayList<String> list) {
		GridActivity.list = list;
	}
	
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}
	

	//START part for taking photos from camera app
	String photoPath = null; 
	String newPhotoID = null;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	private void capturePhotoAndSaveIt()
	{
		Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(takePicIntent.resolveActivity(getPackageManager()) != null)
		{
			//Get id for a new photo and use it as a file name for the photo.
			newPhotoID = PhotoManager.getInstance(this).getCurrentTimeStampAsString();

			File fileDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

			File photoFile = null;
			try
			{
				photoFile = File.createTempFile(newPhotoID, ".jpg", fileDirectory);
				photoPath = photoFile.getAbsolutePath();

			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}

			if( photoFile != null)
			{
				takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, 
						Uri.fromFile(photoFile));
				
				//takePicIntent.putExtra(Photo.PHOTO_ID, photoID);
				startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);

			}

		}
	}

	
	private String descriptionStr = null;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if( resultCode == RESULT_OK )
		{
			AlertDialog.Builder descriptionDialog = new AlertDialog.Builder(this);
		
			descriptionDialog.setTitle("Enter a description:");
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			descriptionDialog.setView(input);
	
			//<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			//Deal with albume option ???????????????
			//>>>>>>>>>>>>>>>>>>>>>>>>>>>>
			descriptionDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					descriptionStr = input.getText().toString();
					
					Bitmap gridBitmap = 
							Utils.getGridBitmapFromFile(photoPath, getApplicationContext());
					
					if( gridBitmap != null)
					{
					
	 
						Bitmap individualBitmap = Utils.getBitmapFromFile(photoPath);
	
						
								//PhotoManager.getInstance(getApplicationContext()).getCurrentTimeStampAsString();
						DatabaseManager.getInstance(getApplicationContext()).addPhoto(
								new Photo(newPhotoID , descriptionStr, individualBitmap , 
										gridBitmap,"My album",false));
						
						//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
						//Add photoId, individualBitmap to Cache and adapter ???????????????????
						//>>>>>>>>>>>>>>>>>>>>>>>>
					}
					Toast.makeText(getApplicationContext(), "Photo saved.", Toast.LENGTH_SHORT).show();
				}
			});
			descriptionDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			descriptionDialog.show();
		}
		else if( resultCode == RESULT_CANCELED)
		{
			Toast.makeText(this, "Unable to take photo. Try later.", Toast.LENGTH_LONG ).show();
		}
	}	
	//END part for taking photos from camera app
}
