package com.example.photoapp;

import java.util.ArrayList;

import com.example.activities.TesterActivity;
import com.example.database.DatabaseManager;
import com.example.database.SpinnerNavItem;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
//import android.widget.EditText;

public class GridActivity extends Activity implements OnNavigationListener {

	public final static String EXTRA_MESSAGE = "com.example.photoapp.MESSAGE";
	protected static ArrayList<String> list;
	protected static int[] images;


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
		
		//this will set up an array with references to images which will be used by the adapter later
		initarray(); //this will retrieve string IDs from the database manager
		System.out.println("Just initialized the array of integer ids");
		
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
//		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
//		        R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
		
		//sets up the up button on the action bar for the user to navigate backwards
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		System.out.println("About to get the grid view by finding it using the id name");
		//get the gridview as defined in the associate xml file
		GridView gridview = (GridView) findViewById(R.id.gridview);
		
		//set the adapter for the grid view
	    gridview.setAdapter(new ImageAdapter(this));
	    
	    //set on item click listener
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(GridActivity.this, "" + position+", "+v.getHeight(), Toast.LENGTH_SHORT).show();
	            Indiview(v,position);
	        }
	    });
	}

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
				takePhoto();
				// TEST : Yasser test
				Intent intent2 = new Intent(this, TesterActivity.class);
				startActivity(intent2);
				return true;
			case R.id.action_help:
				// help action
				return true;
			case R.id.action_restore:
				// check for updates action
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
	
	private void takePhoto() {
		// TODO Auto-generated method stub
		
	}

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
		intent.putExtra("id", position);
		startActivity(intent);
	}
	
	private void initarray()
	{
		DatabaseManager db = DatabaseManager.getInstance(this.getApplicationContext());
		list = db.getPhotoIDs();
		
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

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}
}