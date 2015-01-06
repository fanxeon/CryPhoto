package photoapp;

import com.example.photoapp.R;


import activities.TesterActivity;
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
import android.widget.TextView;

public class AppStart extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        final View view = View.inflate(this, R.layout.activity_app_start, null);
	        setContentView(view);
	        ActionBar actionBar = getActionBar();
	        actionBar.hide(); 
	        AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
	        aa.setDuration(1500);
	        view.startAnimation(aa);
	        aa.setAnimationListener(new AnimationListener()
	        {
	            @Override
	            public void onAnimationEnd(Animation arg0) {
	            	Intent intent = new Intent(AppStart.this, GridActivity.class);
	            	intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	            	startActivity(intent);
	            	overridePendingTransition(R.anim.enteralpha, R.anim.exitalpha);
	            	finish();
	            }
	            @Override
	            public void onAnimationRepeat(Animation animation) {}
	            @Override
	            public void onAnimationStart(Animation animation) {}
	                                                                          
	        });
	        TextView textView = (TextView) findViewById(R.id.welcomeText);
	        textView.setOnClickListener(new TextView.OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(AppStart.this, GridActivity.class);
	            	intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	            	startActivity(intent);
	            	overridePendingTransition(R.anim.enteralpha, R.anim.exitalpha);
				}
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
