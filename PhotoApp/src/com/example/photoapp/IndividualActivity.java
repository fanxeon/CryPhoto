package com.example.photoapp;

import java.util.ArrayList;

import com.example.photo.Photo;
import com.example.photoapp.R;
import com.example.photoapp.R.id;
import com.example.photoapp.R.layout;
import com.example.photoapp.R.menu;
import com.example.activities.TesterActivity;
import com.example.database.DatabaseManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class IndividualActivity extends Activity {

	private Photo photoDetails = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_display_message);
		setContentView(R.layout.activity_display_message);
		Intent intent = getIntent();
		String position = intent.getExtras().getString(GridActivity.STRING_ID);
		//ArrayList<String> list = intent.getStringArrayListExtra(GridActivity.STRING_LIST);
		
		//GridActivity.getList();
		 //ImageAdapter imageAdapter = new ImageAdapter(this);
	     ImageView imageView = (ImageView) findViewById(R.id.SingleView);
	     imageView.setBackgroundColor(Color.BLACK);
//	     int height = imageView.getHeight();
//	     int width = imageView.getWidth();
//	     Bitmap bm = DatabaseManager.getInstance(getApplicationContext()).getBitmap(position, width, height);
//	     imageView.setImageBitmap(bm);
	     //check the cache for the image	     
//	     FetchFromCacheTask fetchtask = new FetchFromCacheTask(imageView, this.getApplicationContext(), this.getFragmentManager());
//		 fetchtask.execute(position);
		 
//		 photoDetails = DatabaseManager.getInstance(getApplicationContext())
//				 					.getPhotoWithoutBitmaps(position);
//		 Toast.makeText(getApplicationContext(), photoDetails.getDescription(), Toast.LENGTH_LONG).show();
	        final DisplayMetrics displayMetrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	        final int height = displayMetrics.heightPixels;
	        final int width = displayMetrics.widthPixels;

	        final int longest = (height > width ? height : width) / 2;
		 photoDetails = DatabaseManager.getInstance(getApplicationContext())
					.getPhoto(position, longest, longest);
		 
		 imageView.setImageBitmap(photoDetails.getBitmap());
		 Toast.makeText(getApplicationContext(), photoDetails.getDescription(), Toast.LENGTH_LONG).show();
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
		MenuInflater inflater = getMenuInflater();
		getMenuInflater().inflate(R.menu.display_message, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		//-- ACTION BAR IMPLEMNTATION @ FAN --//
		int id = item.getItemId();
		switch (item.getItemId()) {
			case R.id.action_share:
				// Share current image 
				openShare();
				return true;
			case R.id.action_discard:
				// Delete current image
				discard();
				return true;
			case R.id.action_sync:
				// sync action
				return true;
			case R.id.action_restore:
				// check for updates action
				openRestore();
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
		//-- ACTION BAR END --//
	}

	private void openRestore() {
		// TODO Auto-generated method stub
		
	}

	private void openSettings() {
		// TODO Auto-generated method stub
		
	}

	private void openShare() {
		// TODO Auto-generated method stub
		
	}

	private void discard() {
		// TODO Auto-generated method stub
		
	}
}
