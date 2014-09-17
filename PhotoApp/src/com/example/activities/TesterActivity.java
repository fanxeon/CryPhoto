package com.example.activities;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.database.DatabaseManager;
import com.example.photo.Photo;
import com.example.photo.PhotoManager;
import com.example.photoapp.R;
import com.example.photoapp.R.id;
import com.example.photoapp.R.layout;
import com.example.photoapp.R.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TesterActivity extends Activity {

	private Button btnTakePhoto = null;
	private ImageView imgView = null;
	private TextView txtViewDescription = null;
	
	private Button btnDB = null;
	private ImageView imgViewDB = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tester);
		
		//Initialize camera button and imageview 
		imgView = (ImageView) findViewById(R.id.imageViewPicTaken);		
		btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
		txtViewDescription = (TextView) findViewById(R.id.txtViewDesc);
		
		btnTakePhoto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				capturePhotoAndSaveIt();
				//or
				//capturePhoto();				
			}
		});

		//Initialize test DB button and imageview 
		imgViewDB = (ImageView) findViewById(R.id.imgViewDB);		
		btnDB = (Button) findViewById(R.id.btnDB);
		btnDB.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				testRetrievePhotoFromBD();
			}
		});
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tester, menu);
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
	

	
	
	String photoPath = null; 
	static final int REQUEST_IMAGE_CAPTURE = 1;
	private void capturePhotoAndSaveIt()
	{
		Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(takePicIntent.resolveActivity(getPackageManager()) != null)
		{
			//Get id for a new photo and use it as a file name for the photo.
			String photoID = PhotoManager.getInstance(this).getCurrentTimeStampAsString();
			//<<<<<<<<<<<<-------------For test
			tempPhotoIDTest = photoID;
			//<<<------------end test
			File fileDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			
			File photoFile = null;
			try
			{
				photoFile = File.createTempFile(photoID, ".jpg", fileDirectory);
				photoPath = photoFile.getAbsolutePath();//"file:" + image.getAbsolutePath();<<<<<<-------

			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
			
			if( photoFile != null)
			{
				takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, 
						Uri.fromFile(photoFile));
				
				startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
				
			}
				
		}
	}

	private String descriptionStr = "";
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{

		AlertDialog.Builder descriptionDialog = new AlertDialog.Builder(this);
		descriptionDialog.setTitle("Enter a decription:");
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		descriptionDialog.setView(input);
		
		descriptionDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				descriptionStr = input.getText().toString();
				Toast.makeText(TesterActivity.this, "Photo saved.", Toast.LENGTH_SHORT).show();
				//setContentView(imgView);
				// Get the dimensions of the View
			   int targetW = imgView.getWidth();
			   int targetH = imgView.getHeight();

			    // Get the dimensions of the bitmap
			    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			    //bmOptions.inJustDecodeBounds = true;
			    BitmapFactory.decodeFile(photoPath, bmOptions);
			    int photoW = bmOptions.outWidth;
			    int photoH = bmOptions.outHeight;

			    // Determine how much to scale down the image
			    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

			    // Decode the image file into a Bitmap sized to fill the View
			    bmOptions.inJustDecodeBounds = false;
			    bmOptions.inSampleSize = scaleFactor;
			    bmOptions.inPurgeable = true;
				
				Bitmap imgBitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
				imgView.setImageBitmap(imgBitmap);
				
				
				//<<<<<<<<<<<----------------- for test
				DatabaseManager.getInstance(TesterActivity.this).addPhoto(
						new Photo(tempPhotoIDTest , descriptionStr, imgBitmap,"My album",false));
				//testRetrievePhotoFromBD();				
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

	
	
	String tempPhotoIDTest = null;
	private void testRetrievePhotoFromBD()
	{
		Photo testPhoto = DatabaseManager.getInstance(this).getPhoto(tempPhotoIDTest);
		
		imgViewDB.setImageBitmap(testPhoto.getBitmap());
		txtViewDescription.setText("Description: " + testPhoto.getDescription());
		
		
	}

}
