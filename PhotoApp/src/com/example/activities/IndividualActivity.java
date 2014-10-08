package com.example.activities;

import java.util.ArrayList;

import com.example.photoapp.FetchFromCacheTask;
import com.example.photoapp.R;
import com.example.photoapp.R.id;
import com.example.photoapp.R.layout;
import com.example.photoapp.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class IndividualActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_display_message);
		setContentView(R.layout.activity_display_message);
		Intent intent = getIntent();
		String position = intent.getExtras().getString(GridActivity.STRING_ID);
		ArrayList<String> list = intent.getStringArrayListExtra(GridActivity.STRING_LIST);
		
		//GridActivity.getList();
		 //ImageAdapter imageAdapter = new ImageAdapter(this);
	     ImageView imageView = (ImageView) findViewById(R.id.SingleView);
	     
	     //check the cache for the image
	     FetchFromCacheTask fetchtask = new FetchFromCacheTask(imageView, this.getApplicationContext(), this.getFragmentManager());
		 fetchtask.execute(position);
	     //imageView.setImageResource(GridActivity.images[position]);
		
//		// Create the text view
//	    TextView textView = new TextView(this);
//	    textView.setTextSize(40);
//	    textView.setText(message);

	    // Set the text view as the activity layout
	   
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}