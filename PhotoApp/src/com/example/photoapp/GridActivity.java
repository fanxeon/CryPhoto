package com.example.photoapp;

import android.app.Activity;
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
	protected static int[] images;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set the layout properties for this activity as in the xml file
		setContentView(R.layout.activity_main);
		
		//this will set up an array with references to images which will be used by the adapter later
		initarray();
		
		//set up drop-down menu
		//create an array adapter which will supply views for the drop down menu
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
		        R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
		
		//sets up the up button on the action bar for the user to navigate backwards
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//get the gridview as defined in the associate xml file
		GridView gridview = (GridView) findViewById(R.id.gridview);
		
		//set the adapter for the grid view
	    gridview.setAdapter(new ImageAdapter(this));
	    
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
		   int[] images = getResources().getIntArray(R.array.ImgRef);
		   images = new int[] { R.drawable.img1, R.drawable.img2,
				      R.drawable.img3, R.drawable.img1,
				      R.drawable.img2, R.drawable.img3,
				      R.drawable.img1, R.drawable.img2,
				      R.drawable.img3, R.drawable.img1,
				      R.drawable.img2, R.drawable.img3,
				      R.drawable.img1, R.drawable.img2,
				      R.drawable.img3, R.drawable.img1,
				      R.drawable.img2, R.drawable.img3,
				      R.drawable.img1, R.drawable.img2,
				      R.drawable.img3, R.drawable.img1,
				      R.drawable.img2, R.drawable.img3,
				   };
		   this.images = images;
	   }
	
	//public final static String EXTRA_MESSAGE = "com.example.photoapp.MESSAGE";
}