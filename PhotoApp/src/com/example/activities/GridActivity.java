package com.example.activities;

import java.util.ArrayList;

import com.example.database.DatabaseManager;
import com.example.photoapp.ImageAdapter;
import com.example.photoapp.R;
import com.example.photoapp.R.id;
import com.example.photoapp.R.layout;
import com.example.photoapp.R.menu;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
//import android.widget.EditText;

public class GridActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.photoapp.MESSAGE";
	protected static ArrayList<String> list;
	protected static int[] images;

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
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		   // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_search:
	            openSearch();
	            return true;
	        case R.id.action_settings:
	            openSettings();
	    		Intent intent = new Intent(this, TesterActivity.class);
	    		//EditText editText = (EditText) findViewById(R.id.edit_message);
	    		//String message = editText.getText().toString();
	    		startActivity(intent);

	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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
}