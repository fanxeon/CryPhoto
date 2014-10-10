package com.example.photoapp;

import java.util.ArrayList;
import java.util.List;

import com.example.database.DatabaseManager;
import com.example.database.SpinnerNavItem;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class AlbumActivity extends Activity implements OnNavigationListener, OnClickListener {
	private ActionBar actionBar;
	private ArrayList<SpinnerNavItem> navSpinner;
	// Navigation adapter
	private TitleNavigationAdapter adapter;
	// Refresh menu item
	private MenuItem refreshMenuItem;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		//-- ACTION BAR IMPLMENTATION DECLARATION @ Fan --//
		actionBar = getActionBar();
		// Hide the action bar title
		actionBar.setDisplayShowTitleEnabled(false);
		// Enabling Spinner dropdown navigation
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// Spinner title navigation data
		navSpinner = new ArrayList<SpinnerNavItem>();
		navSpinner.add(new SpinnerNavItem("Albums", R.drawable.ic_action_collection));
		navSpinner.add(new SpinnerNavItem("Grid View", R.drawable.ic_action_view_as_grid));
		navSpinner.add(new SpinnerNavItem("Recents", R.drawable.ic_action_time));
		navSpinner.add(new SpinnerNavItem("Particular dates", R.drawable.ic_action_data_usage));
		// title drop down adapter
		adapter = new TitleNavigationAdapter(getApplicationContext(),
				navSpinner);
		// assigning the spinner navigation
		actionBar.setListNavigationCallbacks(adapter, this);
		// -- DISPLAY IN LIST : CONSTRUCTING -- //
		//DatabaseManager.getInstance(getApplicationContext()).insertAlbum("ss");
		ArrayList<String> list = DatabaseManager.getInstance(getApplicationContext()).getAlbumNames();
		
		listView =(ListView)findViewById(R.id.listView1);

		//list.add("test albums");
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1,
                list );

		listView.setAdapter(arrayAdapter); 

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.album, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
			switch (item.getItemId()){
			//Should be Share and discard,
			case R.id.action_add_album:
				add_album();
				return true;
			case R.id.action_sync:
				//sync
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void add_album() {
		LayoutInflater li = LayoutInflater.from(this);  
		View view = li.inflate(R.layout.prompt_view, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);  
	    builder.setTitle("Enter an Album name");  
	    
	    builder.setView(view);  
	    builder.setPositiveButton("Save", this);  
	    builder.setNegativeButton("Cancel", this);
	    builder.create().show();  
	    //int n = DatabaseManager.getInstance(getApplicationContext()).insertAlbum();
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (itemPosition == 0){ // Album
			Toast.makeText(getApplicationContext(), "Album", Toast.LENGTH_SHORT).show();

		}
		else if (itemPosition == 1){ // Grid
			Toast.makeText(getApplicationContext(), "Grid", Toast.LENGTH_SHORT).show();
    		Intent intent2 = new Intent(this, GridActivity.class);
    		startActivity(intent2);
		}
		else if (itemPosition == 2){ // recents dates
			Toast.makeText(getApplicationContext(), "recents dates", Toast.LENGTH_SHORT).show();
		}
		else if (itemPosition == 3){ // Particular date
			Toast.makeText(getApplicationContext(), "Particular date", Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}
}
