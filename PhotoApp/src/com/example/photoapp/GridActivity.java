package com.example.photoapp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import utils.Utils;

import com.example.activities.TesterActivity;
import com.example.database.DatabaseManager;
import com.example.photoapp.R;
import com.example.database.SpinnerNavItem;
import com.example.photo.Photo;
import com.example.photo.PhotoManager;
import com.example.server.Callback;
import com.example.server.DownloadPhotoTask;
import com.example.server.ServerManager;
import com.example.server.SyncPhotosTask;
import com.example.server.UploadPhotoTask;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TimePicker;
import android.widget.Toast;
//import android.widget.EditText;

public class GridActivity extends Activity implements OnNavigationListener, OnClickListener {

	public final static String EXTRA_MESSAGE = "com.example.photoapp.MESSAGE";
	public final static String STRING_LIST = "string_id_list";
	public final static String STRING_ID = "string_id";
	GridView gridview;
	protected ImageAdapter imgadapter;
	//private static ArrayList<String> list;
	protected static int[] images;
	//-- NEW CONSTRUCTION: Contextual Action Bar Declaration--//
	private ActionMode mActionMode;
	//-- END --//
	Menu mMenu;
	//-- ACTION BAR IMPLMENTATION DECLARATION @ Fan --//
	private ActionBar actionBar;
	// Title navigation Spinner data
	private ArrayList<SpinnerNavItem> navSpinner;
	// Navigation adapter
	private TitleNavigationAdapter adapter;
	// Refresh menu item
	private MenuItem refreshMenuItem;
	private String nn = null;
	private List<Integer> Animlists = new ArrayList<Integer>();
	//-- ACTION BAR END --//
	// On Actitvity Result declaration
	private String descriptionStr = null;
	int indx = 0;
	Bitmap gridBitmap = null;
	ArrayList<String> albumList ;
	AlertDialog.Builder albumDialog;
	Bitmap individualBitmap = null;
	// Date picker
	private static int mStartYear, mEndYear;
	private static int mStartMonth, mEndMonth;
	private static int mStartDay, mEndDay;
	private static String mStartingDate = "";
	private static String mEndDate = "";
	// Search String
	private static String mSearchQuery = null;
	
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
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);

		processExtraData();

	}
	private void processExtraData()
	{
		Intent intent = getIntent();
		//do something
		if( (intent.getStringExtra(Utils.PHOTO_DELETED) != null) & (getList() != null) )
		{
			getList().remove(intent.getStringExtra(Utils.PHOTO_DELETED));
			imgadapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first
		//adapter.notifyDataSetChanged();
		//initarray(); //this will retrieve string IDs from the database manager
		//Toast.makeText(GridActivity.this, "On Resume Called", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		gridview.smoothScrollToPosition(0);
		if(Utils.isIndividualPhotoDeleted())
		{
			imgadapter.notifyDataSetChanged();
			Utils.setIndividualPhotoDeleted(false);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();  // Always call the superclass method first

		// Save the note's current draft, because the activity is stopping
		// and we want to be sure the current note progress isn't lost.

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

		//processExtraData();

		//initialize cache and fragment
		ImageCache cache = getImageCache(this.getFragmentManager());

		//this will set up an array with references to images which will be used by the adapter later
		initarray(); //this will retrieve string IDs from the database manager
		System.out.println("Just initialized the array of string ids");

		//-- ACTION BAR IMPLMENTATION DECLARATION @ Fan --//
		actionBar = getActionBar();
		// Hide the action bar title
		actionBar.setDisplayShowTitleEnabled(false);
		// Enabling Spinner dropdown navigation
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// Spinner title navigation data
		navSpinner = new ArrayList<SpinnerNavItem>();
		navSpinner.add(new SpinnerNavItem("All", R.drawable.ic_action_view_as_grid));
		navSpinner.add(new SpinnerNavItem("Albums", R.drawable.ic_action_collection));
		navSpinner.add(new SpinnerNavItem("Date Range", R.drawable.ic_action_time));
		navSpinner.add(new SpinnerNavItem("Last Week", R.drawable.ic_action_data_usage));
		navSpinner.add(new SpinnerNavItem("Last month", R.drawable.ic_action_data_usage2));
		// title drop down adapter
		adapter = new TitleNavigationAdapter(getApplicationContext(),
				navSpinner);
		// assigning the spinner navigation
		actionBar.setListNavigationCallbacks(adapter, this);
		setOverflowShowingAlways(); 
		handleIntent(getIntent());
		// -- new --//
		// -- end --//
		// Changing the action bar icon
		// actionBar.setIcon(R.drawable.ico_actionbar);
		//-- ACTION BAR END --//

		//set up drop-down menu
		//create an array adapter which will supply views for the drop down menu
		//SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
		//R.array.action_list, android.R.layout.simple_spinner_dropdown_item);

		//sets up the up button on the action bar for the user to navigate backwards
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		System.out.println("About to get the grid view by finding it using the id name");

		//get the gridview as defined in the associate xml file
		gridview = (GridView) findViewById(R.id.gridview);
		//gridview.setBackgroundColor(Color.BLACK);
		gridview.setDrawSelectorOnTop(true);
		//gridview.setSelector(R.drawable.grid_color_selector);
		//set the adapter for the grid view
		imgadapter = new ImageAdapter(this,cache);
		gridview.setAdapter(imgadapter);
		//setSelection(setSelected, true);
		// gridview.setAdapter(new ImageAdapter(this,cache));

		//set on item click listener
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				//Toast.makeText(GridActivity.this, "" + position+", "+v.getHeight(), Toast.LENGTH_SHORT).show();
				gridview.setSelection(position);
				Indiview(v,position);
			}
		});

		/** NEW CONSTRUCTION: Contextual Action Bar Method @ Fan **/
		gridview.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				gridview.setSelection(position);
				MyActionModeCallback callback = new MyActionModeCallback();
				mActionMode = startActionMode (callback);
				mActionMode.setTitle("1" + R.string.menu_context_title);

				return true;
			}
		});

		gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
		//gridview.setOnItemSelectedListener()
		gridview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

//			private int numOfItemsSelected = 0;
//			@Override
//			public void onItemCheckedStateChanged(ActionMode mode, int position,
//					long id, boolean checked) {
//				// Here you can do something when items are selected/de-selected,
//				// such as update the title in the CAB
//				if(checked)
//				{
//					numOfItemsSelected++;
//					//change the color
//					//imgadapter.getView(position, null, get);
//
//
//				}	
//				else
//				{
//					numOfItemsSelected--;
//				}
//				if( numOfItemsSelected <= 1)
//				{
//					mode.setTitle(numOfItemsSelected + " Item Selected");
//				}
//				else
//				{
//					mode.setTitle(numOfItemsSelected + " Items Selected");
//				}
//
//
//
//			}
//
//			@Override
//			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//				// Respond to clicks on the actions in the CAB
//				switch (item.getItemId()){
//				//Should be Share and discard,
//				case R.id.action_share:
//
//					return true;
//				case R.id.action_discard:
//					//Developing
//					int IDD = item.getItemId();
//					//String IDDDD = String.valueOf(IDD);
//					//discard(IDDDD);
//					return true;
//				default:
//					return false;
//				}
//			}
//
//			@Override
//			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//				// Inflate the menu for the CAB
//				MenuInflater inflater = mode.getMenuInflater();
//				inflater.inflate(R.menu.context, menu);
//				return true;
//			}
//
//			@Override
//			public void onDestroyActionMode(ActionMode mode) {
//				// Here you can make any necessary updates to the activity when
//				// the CAB is removed. By default, selected items are deselected/unchecked.
//			}
//
//			@Override
//			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//				// Here you can perform updates to the CAB due to
//				// an invalidate() request
//				return false;
//			}
//		});
		

			
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub

        	MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context, menu);
//			return true;
            mode.setTitle("Select Items");
            mode.setSubtitle("One item selected");
            return true;

        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub

        	//actionList.clear();
        	 int selectCount = gridview.getCheckedItemCount();
        	 //gridview.getItemAtPosition(gridview.getCheckedItemPosition());
             switch (selectCount) {
             case 1:
                 mode.setSubtitle("One item selected");

                 break;
             default:
                 mode.setSubtitle("" + selectCount + " items selected");

                 break;
             }
             
//            SparseBooleanArray sparseBooleanArray = gridview.getCheckedItemPositions();
//            for (int i = 0;  i < sparseBooleanArray.size(); i++)
//            {
//            	if( sparseBooleanArray.get(i) == true)
//            	{
//            		String id = getList().get(i);
//            		actionList.add(id);
//            	}
//            }
       	   //Toast.makeText(getApplicationContext(), "action list after = " + actionList.get(0), Toast.LENGTH_SHORT).show();
        	switch (item.getItemId()){
			//Should be Share and discard,
			case R.id.action_share:
				uploadPhotos();
				mode.finish();
				return true;
			case R.id.action_discard:
				discardPhotos();
				mode.finish();
				return true;
			default:
				return false;
			}
        	
        }
        private void uploadPhotos()
        {
           	int numOfPhotos = actionList.size();
        	if( numOfPhotos > 0 )
        	{
        		
        		UploadPhotoMultipleSelectionWorker uploadMultiWorker = 
        				new UploadPhotoMultipleSelectionWorker( (ArrayList<String>)actionList.clone() );
        		
        		uploadMultiWorker.executeOnExecutor(Utils.getThreadPoolExecutorInstance(), null);
//        		if ( numOfPhotos == 1)
//        	       	   Toast.makeText(getApplicationContext(), "1 photo deleted.", Toast.LENGTH_SHORT).show();
//        		else
//     	       	   Toast.makeText(getApplicationContext(), numOfPhotos + " photos deleted.", Toast.LENGTH_SHORT).show();
        		actionList.clear();


        	
        		//imgadapter.notifyDataSetChanged();
        	}
        	
        	
        }
        private void discardPhotos()
        {
        	int numOfPhotos = actionList.size();
        	if( numOfPhotos > 0 )
        	{
        		getList().removeAll(actionList);
        		DatabaseManager.getInstance(getApplicationContext()).deletePhotos(actionList);
        		actionList.clear();
        		if ( numOfPhotos == 1)
        	       	   Toast.makeText(getApplicationContext(), "1 photo deleted.", Toast.LENGTH_SHORT).show();
        		else
     	       	   Toast.makeText(getApplicationContext(), numOfPhotos + " photos deleted.", Toast.LENGTH_SHORT).show();


        	
        		//imgadapter.notifyDataSetChanged();
        	}
        
        }
//        ImageView im ;
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position,
                long id, boolean checked) {
            // TODO Auto-generated method stub


       	    //Toast.makeText(getApplicationContext(), position + "checked: " + checked, Toast.LENGTH_SHORT).show();
//   	    	im = (ImageView) gridview.getChildAt(position); 
       	    if(checked)
       	    {
       	    	actionList.add(getList().get(position));
                //Toast.makeText(getApplicationContext(), "Added = " + actionList.get(actionList.size()-1), Toast.LENGTH_SHORT).show();

       	    }
       	    else
       	    {
       	    	actionList.remove(getList().get(position));
                //Toast.makeText(getApplicationContext(), "removed = " + actionList.size(), Toast.LENGTH_SHORT).show();
      	    }       	    	
       	    
       	    
            int selectCount = gridview.getCheckedItemCount();
            switch (selectCount) {
            case 1:
                mode.setSubtitle("One item selected");
                break;
            default:
                mode.setSubtitle("" + selectCount + " items selected");
                break;
            }

        }
    });


	}
	//For SEARCH SEARCH SEARCH
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			mSearchQuery = intent.getStringExtra(SearchManager.QUERY);
			Toast.makeText(getApplicationContext(),"User search '" + mSearchQuery + "'", Toast.LENGTH_LONG).show();
			// Reset : temp for test
			search();
			finish();
			//mSearchQuery = null;
		}
	}
	private ArrayList<String> idsForSearchResults = new ArrayList<String>();
    private void search()
    {
    	idsForSearchResults.clear();
    	ArrayList<Photo> photos = DatabaseManager
    			.getInstance(getApplicationContext()).getPhotosDescriptions();
    	String keywords = mSearchQuery;
    	
        for (Photo photo : photos)
        {
            if (!keywords.equals("") && photo.getDescription()!=null
                && (photo.getDescription().toLowerCase().contains(keywords.toLowerCase()))
                ||(keywords.toLowerCase().contains(photo.getDescription().toLowerCase())))
            {
            	idsForSearchResults.add( photo.getPhotoID() ); 
            } 
        }
        imgadapter.replaceList(idsForSearchResults);
        update();

    }
	
	// UNUSED ANY MORE
	@SuppressLint("NewApi")
	@Override
	public boolean onSearchRequested() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			MenuItem mi = mMenu.findItem(R.id.action_search);
			if(mi.isActionViewExpanded()){
				mi.collapseActionView();
			} else{
				mi.expandActionView();
			}
		} else{
			//onOptionsItemSelected(mMenu.findItem(R.id.search));
		}
		return super.onSearchRequested();
	}
	// END 

	// END
	private ImageCache getImageCache(FragmentManager fragmentManager) {

		return ImageCache.getInstance(fragmentManager);
	}

	ArrayList<String> actionList = new ArrayList<String>();
	
	class MyActionModeCallback implements ActionMode.Callback{
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.context, menu);
			return true;
		}
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			//float textSize = mHelloTextView.getTextSize();
			switch (item.getItemId()){
			//Should be Share and discard,
			case R.id.action_share:
				//Share method
				return true;
			case R.id.action_discard:
				//Developing
				int IDD = item.getItemId();
				String IDDDD = String.valueOf(IDD);
				discard(IDDDD);
				return true;
			}
			return false;
		}
		// --------- PROBLEMS -----------//
		private void discard(String IDDDD) {
			int n = DatabaseManager.getInstance(getApplicationContext()).deletePhoto(IDDDD);

			if( n > 0 )
				Toast.makeText(getApplicationContext(), "Photo deleted.", Toast.LENGTH_LONG).show();
			Log.v("grid delete: ", n + "");
		}
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// TODO Auto-generated method stub
		}
	}
	/** END OF CAB  **/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// Inflate the menu items for use in the action bar
		mMenu = menu;
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
			//onSearchRequested();
			return true;
		case R.id.action_photo:
			// Take photo
			capturePhotoAndSaveIt();
			return true;
		case R.id.action_sync:
			openSync();
			//sync action
			return true;
		case R.id.action_add_album:
			add_album();
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
	// Add an new album //
	private void add_album() {
		LayoutInflater li = LayoutInflater.from(this);  
		View view = li.inflate(R.layout.prompt_view, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);  
		builder.setTitle("Enter an Album name");  

		builder.setView(view);  
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				if(which == Dialog.BUTTON_POSITIVE){  

					AlertDialog ad = (AlertDialog) dialog;  
					EditText t = (EditText) ad.findViewById(R.id.editText_prompt);  

					String albumName =  t.getText().toString();
					DatabaseManager.getInstance(getApplicationContext()).insertAlbum(albumName);
				}
			}
		});  
		builder.setNegativeButton("Cancel", this);
		//DatabaseManager.getInstance(getApplicationContext()).insertAlbum("ss");
		builder.create().show();  
		//int n = DatabaseManager.getInstance(getApplicationContext()).insertAlbum();
	}

	private void openSync()
	{
		//Tesing donwload this should be sync <<<<<<<<<<<<<<<<<----------------
		SyncPhotosTask syncTask = new SyncPhotosTask(this);
		syncTask.setCallbackOnTaskFinished(new Callback<ArrayList<String>>() {			
			@Override
			public void OnTaskFinished(ArrayList<String> downloadedPhotoIds) {
				if(downloadedPhotoIds.size() > 0 )
				{	
					//this method will be called by downloadPhotoTask when it finish downloading
					//					AddToCacheTask task = new AddToCacheTask(getApplicationContext()
					//							,getImageCache(getFragmentManager()), downloadedPhoto.getGridBitmap());
					//					task.execute(downloadedPhoto.getPhotoID());

					//					DatabaseWorker dbWorker = new DatabaseWorker();
					//					dbWorker.execute(newPhoto);						
					//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
					//Add photoId, individualBitmap to Cache and adapter ???????????????????
					//>>>>>>>>>>>>>>>>>>>>>>>>
					getList().addAll(downloadedPhotoIds);
					imgadapter.notifyDataSetChanged();

					//		DownloadPhotoTask downloadPhotoTask = new DownloadPhotoTask(this);
					//		downloadPhotoTask.setCallbackOnTaskFinished(new Callback<Photo>() {			
					//			@Override
					//			public void OnTaskFinished(Photo downloadedPhoto) {
					//				if(downloadedPhoto != null)
					//				{	
					//					//this method will be called by downloadPhotoTask when it finish downloading
					//					AddToCacheTask task = new AddToCacheTask(getApplicationContext()
					//							,getImageCache(getFragmentManager()), downloadedPhoto.getGridBitmap());
					//					task.execute(downloadedPhoto.getPhotoID());
					//					
					////					DatabaseWorker dbWorker = new DatabaseWorker();
					////					dbWorker.execute(newPhoto);						
					//					//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
					//					//Add photoId, individualBitmap to Cache and adapter ???????????????????
					//					//>>>>>>>>>>>>>>>>>>>>>>>>
					//					getList().add(downloadedPhoto.getPhotoID());
					//					imgadapter.notifyDataSetChanged();
				}	
			}
		});
		syncTask.executeOnExecutor(Utils.getThreadPoolExecutorInstance(), null);
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
		intent.putExtra(STRING_ID, getList().get(position));
		//intent.putStringArrayListExtra(STRING_LIST, getList());
		//Animation
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		
		startActivity(intent);
		//Animation
		overridePendingTransition(R.anim.enteralpha, R.anim.exitalpha);
	}

	private void initarray()
	{
		DatabaseManager db = DatabaseManager.getInstance(this.getApplicationContext());
		setList(db.getPhotoIDs());		
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

	public static ArrayList<String> getList() {
		return Utils.list;
	}

	public static void setList(ArrayList<String> list) {
		Utils.list = list;
	}
	
	public void update()
	{
		imgadapter.notifyDataSetChanged();
	}
	
	// Dealing with Samsung phones menu problem
	private void setOverflowShowingAlways() {  
		try {  
			ViewConfiguration config = ViewConfiguration.get(this);  
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");  
			menuKeyField.setAccessible(true);  
			menuKeyField.setBoolean(config, false);  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	}  
	/**=======================
	 * Drop down menu listener
	 * =======================
	 */
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		AlertDialog.Builder albumListDialog = new AlertDialog.Builder(this);
		AlertDialog.Builder timesDialog = new AlertDialog.Builder(this);
		if (itemPosition == 0){ // All

			return true;

		}
		else if (itemPosition == 1){ //Albums
			albumList = DatabaseManager.getInstance(getApplicationContext()).getAlbumNames();
			albumListDialog.setTitle("Albums")
			.setIcon(R.drawable.ic_action_collection2)
			.setSingleChoiceItems(albumList.toArray(new String[albumList.size()]), -1 ,  
					new DialogInterface.OnClickListener() {  
				@Override 
				public void onClick(DialogInterface dialog, int which) {
					/**>>> Add Methods to respond selecting albums <<<**/

					Toast.makeText(getApplicationContext(), "User select an '" + albumList.get(which)+ "' Album", Toast.LENGTH_SHORT).show();
					update();//Delete it when methods add

					/**>>> END <<<**/
					dialog.dismiss();
				}  
			});
			albumListDialog.show();
			return true;
		}
		else if (itemPosition == 2){ // Times(Custom date range)
			Toast.makeText(getApplicationContext(), "Times", Toast.LENGTH_SHORT).show();
			DialogFragment endDate = new DatePickerFragment2();
			endDate.show(getFragmentManager(), "datePicker");

			DialogFragment startDate = new DatePickerFragment();
			startDate.show(getFragmentManager(), "datePicker");

			/**>>> Add Methods to respond Times(Custom date range)<<<**/
			update();//Delete it when methods add

			/**>>> END <<<**/

		}
		else if (itemPosition == 3){ // A week
			/**>>> Add Methods to respond Times(Custom date range)<<<**/
			update();//Delete it when methods add
			Toast.makeText(getApplicationContext(), "User select 'Within a week'", Toast.LENGTH_SHORT).show();
			/**>>> END <<<**/
		}
		else if (itemPosition == 4){ // A month
			/**>>> Add Methods to respond Times(Custom date range)<<<**/
			update();//Delete it when methods add
			Toast.makeText(getApplicationContext(), "User select 'Within a month'", Toast.LENGTH_SHORT).show();
			/**>>> END <<<**/
		}
		return true;
	}
	/**===========================
	 * Drop down menu listener END
	 * ===========================
	 */
	//START part for taking photos from camera app
	String photoPath = null; 
	String newPhotoID = null;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	private void capturePhotoAndSaveIt()
	{
		Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(takePicIntent.resolveActivity(getPackageManager()) != null)
		{
			//Get id for a new photo and use it as a file name for the photo.
			newPhotoID = PhotoManager.getInstance(this).getCurrentTimeStampAsString();

			File fileDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

			File photoFile = null;
			try
			{
				photoFile = File.createTempFile(newPhotoID, ".jpg", fileDirectory);
				photoPath = photoFile.getAbsolutePath();

			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}

			if( photoFile != null)
			{
				takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, 
						Uri.fromFile(photoFile));

				//takePicIntent.putExtra(Photo.PHOTO_ID, photoID);
				startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);

			}

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{

		if( resultCode == RESULT_OK )
		{
			//GridView gridview = (GridView) findViewById(R.id.gridview);
			AlertDialog.Builder descriptionDialog = new AlertDialog.Builder(this);
			descriptionDialog.setTitle("Enter description");
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_TEXT);

			descriptionDialog.setView(input);			
			//<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			//Deal with albume option ???????????????
			//>>>>>>>>>>>>>>>>>>>>>>>>>>>>
			descriptionDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					descriptionStr = input.getText().toString();

					gridBitmap = 
							Utils.getGridBitmapFromFile(photoPath, getApplicationContext());

					if( gridBitmap != null)
					{

						//						Display display = getWindowManager().getDefaultDisplay();
						//						Point size = new Point();
						//						display.getSize(size);
						//						int width = size.x;
						//						int height = size.y;
						//Ignore bitmap colonm since we are using gridBitmap for both<<<<<<------
						individualBitmap = Utils.getBitmapFromFile(photoPath);
						//Bitmap individualBitmap = null;


						albumDialog.show();
					}
				}
			});
			descriptionDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			descriptionDialog.show();

			// -- Get Album list on Capture view-- //
			albumList = DatabaseManager.getInstance(getApplicationContext()).getAlbumNames();

			albumDialog = new AlertDialog.Builder(this);
			Log.v("Album before", indx + "");

			albumDialog.setTitle("Select an Album")
			.setIcon(R.drawable.ic_action_collection2)
			.setSingleChoiceItems(albumList.toArray(new String[albumList.size()]), 0 ,  
					new DialogInterface.OnClickListener() {  
				@Override 
				public void onClick(DialogInterface dialog, int which) {
					//albumSelection = albumArray[which];
					indx = which;

					AddToCacheTask cacheTask = new AddToCacheTask(getApplicationContext()
							,getImageCache(getFragmentManager()), gridBitmap);
					cacheTask.executeOnExecutor(Utils.getThreadPoolExecutorInstance(),newPhotoID);						
					Photo newPhoto = new Photo(newPhotoID , descriptionStr, individualBitmap , 
							gridBitmap, albumList.get(indx),false);
					indx = 0;
					getList().add(newPhotoID);

					DatabaseWorker dbWorker = new DatabaseWorker();
					dbWorker.executeOnExecutor(Utils.getThreadPoolExecutorInstance(),newPhoto);						

//					boolean done = false;
//					while(!done)
//					{
//						try
//						{
//							Boolean b = cacheTask.get();
//							done = true;
//						}
//						catch(InterruptedException in)
//						{
//							//interrupted = false;
//							done = false;
//						}
//						catch(ExecutionException e)
//						{
//							done = true;
//							System.out.println("Encountered an executopn exception");
//						}
//						catch(Exception e)
//						{
//							done = true;
//							System.out.println("Encountered an exception");
//						}
//					}

//					imgadapter.notifyDataSetChanged();


					//            						//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
					//Add photoId, individualBitmap to Cache and adapter ???????????????????
					//>>>>>>>>>>>>>>>>>>>>>>>>
		            						
					try {						
						Thread.sleep(1000);
						} catch (InterruptedException e) {
								e.printStackTrace();
						}
					dialog.dismiss();
				}  
			});


			// -- END -- //



		}
		//Dialog new options

		//
		else if( resultCode == RESULT_CANCELED)
		{
			Toast.makeText(this, "Unable to take photo. Try later.", Toast.LENGTH_LONG ).show();
		}
	}	
	//END part for taking photos from camera app
	//NEW CONSTRUCTION

	//END

	private class UploadPhotoMultipleSelectionWorker extends AsyncTask<Void, Void, Void>
	{
		private ArrayList<String> photoIds = new ArrayList<String>();
		//private boolean isSomePhotosAlreadySaved = false;
		private int numPhotoAlreadySavedOnServer = 0;
		public UploadPhotoMultipleSelectionWorker(ArrayList<String> photoIdsToUpload) 
		{
			photoIds = photoIdsToUpload;
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			for(String photoId : photoIds )
			{
				if( photoId != null )
				{
					if( !DatabaseManager.getInstance(getApplicationContext()).isSavedOnServer(photoId))
					{
							UploadPhotoTask uploadPhotoTask = new UploadPhotoTask(GridActivity.this);
							uploadPhotoTask.executeOnExecutor(Utils.getThreadPoolExecutorInstance(),
									DatabaseManager.getInstance(getApplicationContext()).getPhoto(photoId));
					}
					else
					{
						//isSomePhotosAlreadySaved = true;
						numPhotoAlreadySavedOnServer++;
					}
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(numPhotoAlreadySavedOnServer > 0)
			{
				if(numPhotoAlreadySavedOnServer == 1)
				{
					Toast.makeText(getApplicationContext(), "Photo already saved on server."
							, Toast.LENGTH_SHORT).show();
				}			
				else if(numPhotoAlreadySavedOnServer == photoIds.size())
					Toast.makeText(getApplicationContext(), "Photos already saved on server."
						, Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getApplicationContext(), "Some photos already saved on server."
							, Toast.LENGTH_SHORT).show();
			}

		}
		
	}

	private class DatabaseWorker extends AsyncTask<Photo, Void , Void>
	{
		public DatabaseWorker() {
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Void doInBackground(Photo... newPhoto) 
		{
			//super(newPhoto);
			DatabaseManager.getInstance(getApplicationContext()).addPhoto( newPhoto[0], 50);


			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//imgadapter.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), "Photo saved.", Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}
	// NEW CONSTRUCTION - date Picker
	public static class DatePickerFragment extends DialogFragment
	implements DatePickerDialog.OnDateSetListener {
		DatePickerDialog datePicker;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();

			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			datePicker = new DatePickerDialog(getActivity(),this,year,month,day);
			datePicker.setTitle("Start date");
			// Create a new instance of DatePickerDialog and return it
			return datePicker;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
	      	mStartYear = year * 10000;
            mStartMonth = (month + 1) * 100;
            mStartDay = day;
            
            mStartingDate = mStartYear + mStartMonth + mStartDay + "_0000" ;
            Toast.makeText(getActivity(),"Starting dates: " + mStartingDate, Toast.LENGTH_LONG).show();
		}
	}
	public static class DatePickerFragment2 extends DialogFragment
	implements DatePickerDialog.OnDateSetListener {
		DatePickerDialog datePicker;
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();

			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			datePicker = new DatePickerDialog(getActivity(),this,year,month,day);
			datePicker.setTitle("End Date");
			// Create a new instance of DatePickerDialog and return it
			return datePicker;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
	      	mEndYear = year * 10000;
	        mEndMonth = (month + 1) * 100;
	        mEndDay = day;
	        mEndDate = mEndYear + mEndMonth + mEndDay + "_0000";
	        Toast.makeText(getActivity(),"End dates: " + mEndDate, Toast.LENGTH_LONG).show();
		}
	}
	// END

}
