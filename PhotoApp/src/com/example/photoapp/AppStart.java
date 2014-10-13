package com.example.photoapp;

import com.example.activities.TesterActivity;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class AppStart extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        final View view = View.inflate(this, R.layout.activity_app_start, null);
	        setContentView(view);
	        ActionBar actionBar = getActionBar();
	        actionBar.hide(); 
	        AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
	        aa.setDuration(3000);
	        view.startAnimation(aa);
	        aa.setAnimationListener(new AnimationListener()
	        {
	            @Override
	            public void onAnimationEnd(Animation arg0) {
	            	Intent intent = new Intent(AppStart.this, GridActivity.class);
	            	startActivity(intent);
	            	
	            }
	            @Override
	            public void onAnimationRepeat(Animation animation) {}
	            @Override
	            public void onAnimationStart(Animation animation) {}
	                                                                          
	        });
	        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app_start, menu);
		
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
