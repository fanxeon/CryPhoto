package com.example.photoapp;

import java.util.ArrayList;

import com.example.database.DatabaseManager;
import com.example.photo.Photo;
import com.example.photoapp.GridActivity.AdapterListFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
public class SearchResultsActivity extends Activity {
	private ArrayList<String> idsForSearchResults = new ArrayList<String>();
	private String q = null ;
	protected ImageAdapter imgadapter;
	GridView gridview;
	private static final String TAG = "ListTag";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ImageCache cache = getImageCache(this.getFragmentManager());
        handleIntent(getIntent());
        gridview = (GridView) findViewById(R.id.gridview);
        imgadapter = new ImageAdapter(this,cache);		
		setList(idsForSearchResults);
		gridview.setAdapter(imgadapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Indiview(v,position);
			}
		});
        
    }
 
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
         
        handleIntent(intent);
    }
 
    private void handleIntent(Intent intent) {
 
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
            idsForSearchResults.clear();
        	ArrayList<Photo> photos = DatabaseManager
        			.getInstance(getApplicationContext()).getPhotosDescriptions();
        	String keywords = query;
        	
            for (Photo photo : photos)
            {
                if (!keywords.equals("") && photo.getDescription()!=null
                    && (photo.getDescription().toLowerCase().contains(keywords.toLowerCase()))
                    ||(keywords.toLowerCase().contains(photo.getDescription().toLowerCase())))
                {
                	idsForSearchResults.add( photo.getPhotoID() ); 
                	//Toast.makeText(getApplicationContext(), photo.getDescription(), Toast.LENGTH_SHORT).show();
                } 
            }

        }
    }
 
    private void showResults(String query) {
	     TextView textView = (TextView) findViewById(R.id.txtQuery);
	     textView.setText(query);
    }
    private void search()
    {
    	idsForSearchResults.clear();
    	ArrayList<Photo> photos = DatabaseManager
    			.getInstance(getApplicationContext()).getPhotosDescriptions();
    	String keywords = q;
    	
        for (Photo photo : photos)
        {
            if (!keywords.equals("") && photo.getDescription()!=null
                && (photo.getDescription().toLowerCase().contains(keywords.toLowerCase()))
                ||(keywords.toLowerCase().contains(photo.getDescription().toLowerCase())))
            {
            	idsForSearchResults.add( photo.getPhotoID() ); 
            	//Toast.makeText(getApplicationContext(), photo.getDescription(), Toast.LENGTH_SHORT).show();
            } 
        }
        imgadapter.replaceList(idsForSearchResults);
        update();
    }
	public void update()
	{
		imgadapter.notifyDataSetChanged();
	}
	private ImageCache getImageCache(FragmentManager fragmentManager) {

		return ImageCache.getInstance(fragmentManager);
	}
	public void setList(ArrayList<String> list) {
		imgadapter.setList(list);;
	}
	ArrayList<String> getList(FragmentManager fragmentManager)
	{
		// Search for, or create an instance of the non-UI RetainFragment
		final AdapterListFragment mFragment = findOrCreateListFragment(fragmentManager);

		// See if we already have an ImageCache stored in RetainFragment
		ArrayList<String> list = mFragment.getList();

		// No existing ImageCache, create one and store it in RetainFragment
		if (list == null) {
			list = initarray();
		}
		
		return list;
	}
	private static AdapterListFragment findOrCreateListFragment(
			FragmentManager fm) {
		// TODO Auto-generated method stub
		//BEGIN_INCLUDE(find_create_retain_fragment)
		// Check to see if we have retained the worker fragment.
		AdapterListFragment mFragment = (AdapterListFragment) fm.findFragmentByTag(TAG);

		// If not retained (or first time running), we need to create and add it.
		if (mFragment == null) {
			mFragment = new AdapterListFragment();
			fm.beginTransaction().add(mFragment, TAG).commitAllowingStateLoss();
		}
		//END_INCLUDE(find_create_retain_fragment)
		return mFragment;
	}
	private ArrayList<String> initarray()
	{
		DatabaseManager db = DatabaseManager.getInstance(this.getApplicationContext());
		return db.getPhotoIDs();		

	}
	public void Indiview(View view, int position) {
		Intent intent = new Intent(this, IndividualActivity.class);
		startActivity(intent);
	}
}