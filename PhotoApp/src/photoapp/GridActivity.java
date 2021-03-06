/*
   Developed by Fan Xuan , Yasser , Vidnoh and Hosam
    From University of Melbourne
    Continue developing by FanX
 */

package photoapp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import photo.Photo;
import photo.PhotoManager;

import server.Callback;
import server.SyncPhotosTask;
import server.UploadPhotoTask;
import utils.Utils;
import com.example.photoapp.R;

import database.DatabaseManager;
import database.SpinnerNavItem;


import activities.TesterActivity;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
//import android.widget.EditText;

public class GridActivity extends Activity implements OnNavigationListener, OnClickListener {

	public final static String EXTRA_MESSAGE = "com.example.photoapp.MESSAGE";
	public final static String STRING_LIST = "string_id_list";
	public final static String STRING_ID = "string_id";
	public final static String LIST_NAME = "AdapterList";
	private static final String TAG = "ListTag";
	private static final String NAVIGATION_LIST_POSITION_KEY = "navkey";
	private static final String SAVED_INSTANCE_STATE = "YesSavedIt";
    private long exitTime;
	private static int navigation_list_position;
	private boolean savedstate;
	
	GridView gridview;
	protected ImageAdapter imgadapter;
	private ArrayList<String> list;
	//-- NEW CONSTRUCTION: Contextual Action Bar Declaration--//
	private ActionMode mActionMode;
	//-- END --//
	Menu mMenu;
	private ActionBar actionBar;
	// Title navigation Spinner data
	private ArrayList<SpinnerNavItem> navSpinner;
	// Navigation adapter
	private TitleNavigationAdapter adapter;
	// Refresh menu item
	private MenuItem refreshMenuItem;
	private String nn = null;
	private List<Integer> Animlists = new ArrayList<Integer>();
	// On Actitvity Result declaration
	private String descriptionStr = null;
	int indx = 0;
	Bitmap gridBitmap = null;
	ArrayList<String> albumList = null;
	String[] albumStringList = null;
	AlertDialog.Builder albumDialog;
	Bitmap individualBitmap = null;
	// Date picker
	private static int mStartYear, mEndYear;
	private static int mStartMonth, mEndMonth;
	private static int mStartDay, mEndDay;
	private String mStartingDate = "";
	private String mEndDate = "";
	// Search String
	private static String mSearchQuery = null;
    // ----------------------- Utils and support ----------------------------//
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK  
                && event.getAction() == KeyEvent.ACTION_DOWN) {  
   
            if ((System.currentTimeMillis() - exitTime) > 2000)   
            {  
                Toast.makeText(getApplicationContext(), "再次按下退出 CryPhoto",
                        Toast.LENGTH_SHORT).show();  
                exitTime = System.currentTimeMillis();
            } else {  
                finish();  
                System.exit(0);  
            }  
            return true;  
        }  
        return super.dispatchKeyEvent(event);  
    }  

	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		gridview.smoothScrollToPosition(0);
		if(Utils.isIndividualPhotoDeleted())
		{
			getList().remove(Utils.getDeletedPhotoID());
			imgadapter.notifyDataSetChanged();
			Utils.setIndividualPhotoDeleted(false);
		}
	}

	@Override
	public void onPause() {
		super.onPause();  // Always call the superclass method first

		// Release the Camera because we don't need it when paused
		// and other activities might need to use it.
		System.out.println("On Pause Called");
		//Toast.makeText(GridActivity.this, "On Pause Called", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onStop() {
		super.onStop();  // Always call the superclass method first
		// Save the note's current draft, because the activity is stopping
		// and we want to be sure the current note progress isn't lost.
	}


	private void setMembers(Bundle state) {
		// TODO Auto-generated method stub
		if (state != null)
		{
			list = state.getStringArrayList(LIST_NAME);
			System.out.println("Size of list being returned is "+list.size());
			navigation_list_position = state.getInt(NAVIGATION_LIST_POSITION_KEY);
			savedstate = true;
		}
		else
		{
			navigation_list_position = 0;
			savedstate = false;
			DatabaseManager db = DatabaseManager.getInstance(this.getApplicationContext());
			list = db.getPhotoIDs();
		}
	}
    // --------------------- Utils and support ends -------------------- //

    // ----------------------- Main implementation --------------------- //
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the user's current game state
		//public final static String LIST_NAME = "AdapterList";
		savedInstanceState.putStringArrayList(LIST_NAME, imgadapter.getList());
		System.out.println("Length of list being saved to state is "+list.size());
		savedInstanceState.putInt(NAVIGATION_LIST_POSITION_KEY, navigation_list_position);
		savedInstanceState.putBoolean(SAVED_INSTANCE_STATE, true);

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("Hit the oncreate of grid activity");
		setMembers(savedInstanceState);
		try	{
			getActivityManager(getApplicationContext());
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		//set the layout properties for this activity as in the xml file
		setContentView(R.layout.activity_main);
		System.out.println("Just set the layout for the activity");
		ImageCache cache = getImageCache(this.getFragmentManager());
		System.out.println("Just initialized the array of string ids");
		System.out.println("About to get the grid view by finding it using the id name");
		//get the gridview as defined in the associate xml file
		gridview = (GridView) findViewById(R.id.gridview);
		//gridview.setBackgroundColor(Color.BLACK);
		gridview.setDrawSelectorOnTop(true);
		//gridview.setSelector(R.drawable.grid_color_selector);
		//set the adapter for the grid view
		imgadapter = new ImageAdapter(this,cache);
		imgadapter.setList(list);
		//setList(getList(this.getFragmentManager()));
		gridview.setAdapter(imgadapter);

		//-- ACTION BAR IMPLMENTATION DECLARATION @ Fan --//
		actionBar = getActionBar();
		// Hide the action bar title
		actionBar.setDisplayShowTitleEnabled(false);
		// Enabling Spinner dropdown navigation
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// Spinner title navigation data
		navSpinner = new ArrayList<SpinnerNavItem>();
		navSpinner.add(new SpinnerNavItem("全部", R.drawable.ic_action_view_as_grid));
		navSpinner.add(new SpinnerNavItem("相册", R.drawable.ic_action_collection));
		navSpinner.add(new SpinnerNavItem("指定日期", R.drawable.ic_action_time));
		navSpinner.add(new SpinnerNavItem("一周以前", R.drawable.ic_action_data_usage));
		navSpinner.add(new SpinnerNavItem("一个月之前", R.drawable.ic_action_data_usage));

		// title drop down adapter
		adapter = new TitleNavigationAdapter(getApplicationContext(),
				navSpinner);
		// assigning the spinner navigation
		actionBar.setListNavigationCallbacks(adapter, this);
		setOverflowShowingAlways();
		actionBar.setSelectedNavigationItem(navigation_list_position);

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
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
		gridview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context, menu);
                mode.setTitle("选择照片");
                mode.setSubtitle("一张照片已选择");
                return true;

        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
             int selectCount = gridview.getCheckedItemCount();
             switch (selectCount) {
                 case 1:
                     mode.setSubtitle("一张照片已选择");
                     break;
                 default:
                     mode.setSubtitle("" + selectCount + " 张照片已选择");
                     break;
             }
             switch (item.getItemId()){
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
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    // --------------------------- Main ENDS --------------------------------- //


    // --------------------------- Function area ----------------------------- //
            private void uploadPhotos()
        {
           	int numOfPhotos = actionList.size();
        	if( numOfPhotos > 0 )
        	{
        		
        		UploadPhotoMultipleSelectionWorker uploadMultiWorker = 
        				new UploadPhotoMultipleSelectionWorker( (ArrayList<String>)actionList.clone() );

        		uploadMultiWorker.executeOnExecutor(Utils.getThreadPoolExecutorInstance(), null);
        		actionList.clear();
        	}
        }
         
        @Override
			
        public void onDestroyActionMode(ActionMode mode) {
			}

        private void discardPhotos()
        {
            int numOfPhotos = actionList.size();
            if( numOfPhotos > 0 ){
                getList().removeAll(actionList);
                DatabaseManager.getInstance(getApplicationContext()).deletePhotos(actionList);
                actionList.clear();
                if ( numOfPhotos == 1) {
                    Toast.makeText(getApplicationContext(),
                            "1 张照片已删除", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), numOfPhotos +
                            " 照片已删除", Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position,
                long id, boolean checked) {
                if(checked) {
                    actionList.add(getList().get(position));
                }
                else {
                    actionList.remove(getList().get(position));
                }
                int selectCount = gridview.getCheckedItemCount();
                switch (selectCount) {
                    case 1:
                        mode.setSubtitle("一张照片已选择");
                        break;
                    default:
                        mode.setSubtitle("" + selectCount + " 照片已选择");
                        break;
                }
            }
        });

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

	//For SEARCH SEARCH SEARCH
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			mSearchQuery = intent.getStringExtra(SearchManager.QUERY);
			search();
			finish();
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
            	//Toast.makeText(getApplicationContext(), photo.getDescription(), Toast.LENGTH_SHORT).show();
            } 
        }
        if (idsForSearchResults.size() == 0){
        	Toast.makeText(getApplicationContext(),"未找到结果", Toast.LENGTH_LONG).show();
        	onRestart();
        } else {
	        imgadapter.replaceList(idsForSearchResults);
	        update();
        }
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
			openSearch();
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
		builder.setTitle("输入新相册名");

		builder.setView(view);  
		builder.setPositiveButton("保存", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				if(which == Dialog.BUTTON_POSITIVE){  

					AlertDialog ad = (AlertDialog) dialog;  
					EditText t = (EditText) ad.findViewById(R.id.editText_prompt);  

					String albumName =  t.getText().toString();
					DatabaseManager.getInstance(getApplicationContext()).insertAlbum(albumName);
				}
			}
		});  
		builder.setNegativeButton("取消", this);
		//DatabaseManager.getInstance(getApplicationContext()).insertAlbum("ss");
		builder.create().show();  
		//int n = DatabaseManager.getInstance(getApplicationContext()).insertAlbum();
	}
	//Get Album List
	private void get_album(){
		albumList = DatabaseManager.getInstance(getApplicationContext()).getAlbumNames();
		albumStringList = albumList.toArray(new String[albumList.size()]);
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

					//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
					//Add photoId, individualBitmap to Cache and adapter ???????????????????
					//>>>>>>>>>>>>>>>>>>>>>>>>
					getList().addAll(downloadedPhotoIds);
					imgadapter.notifyDataSetChanged();

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
			AlertDialog.Builder builder = new AlertDialog.Builder(this);  
			builder.setTitle("键入描述搜索照片")
                 .setIcon(R.drawable.ic_action_search);
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			builder.setView(input);	
			builder.setPositiveButton("搜索", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					if(which == Dialog.BUTTON_POSITIVE){  
						mSearchQuery = input.getText().toString();
						search();
					}
				}
			});  
			builder.setNegativeButton("取消", this);
			
			builder.show();
	
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
		startActivity(intent);
		//Animation
	}

	private ArrayList<String> initarray(Bundle savedInstanceState)
	{
		// Check whether we're recreating a previously destroyed instance
		if (savedInstanceState != null) {
			// Restore value of members from saved state
			//list = savedInstanceState.getStringArrayList(LIST_NAME);
		} else {
			// Probably initialize members with default values for a new instance
			DatabaseManager db = DatabaseManager.getInstance(this.getApplicationContext());
			list = db.getPhotoIDs();
		}
		return list;
	}

	//public final static String EXTRA_MESSAGE = "com.example.photoapp.MESSAGE";

	public ArrayList<String> getList() {
		return imgadapter.getList();
	}

	//	public static ArrayList<String> staticGetList()
	//	{
	//		return list;
	//	}

	public void setList(ArrayList<String> list) {
		imgadapter.setList(list);;
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
			//imgadapter.getAllList();
			imgadapter.setListToAll();
			imgadapter.UpdateGridView();
			//Toast.makeText(GridActivity.this, "item Position is 0", Toast.LENGTH_SHORT).show();
			navigation_list_position = 0;
			return true;
		}

		else if (itemPosition == 1){ //Albums
			
			//Toast.makeText(GridActivity.this, "item Position is 1", Toast.LENGTH_SHORT).show();
			albumList = DatabaseManager.getInstance(getApplicationContext()).getAlbumNames();
			albumListDialog.setTitle("相册")
			.setIcon(R.drawable.ic_action_collection)
			.setSingleChoiceItems(albumList.toArray(new String[albumList.size()]), -1 ,  
					new DialogInterface.OnClickListener() {  
				@Override 
				public void onClick(DialogInterface dialog, int which) {
					/**>>> Add Methods to respond selecting albums <<<**/

					Toast.makeText(getApplicationContext(), "User select an '" + albumList.get(which)+ "' Album", Toast.LENGTH_SHORT).show();
					imgadapter.replaceList(DatabaseManager.getInstance(getApplicationContext()).getPhotoIdsForAlbum(albumList.get(which)));
					imgadapter.UpdateGridView();//Delete it when methods add

					/**>>> END <<<**/
					dialog.dismiss();
				}
			});
			
			if(savedstate == false)
			{
				albumListDialog.show();
			}
			else
			{
				//savedstate = true; i.e. an orientation was performed
				if(navigation_list_position == 1)
				{
					//savedstate = true and position = 1
					//need to remember the album
					//imgadapter.UpdateGridView();
				}
				else
				{
					//savedstate = true and position != 1
					albumListDialog.show();
				}
			}
			navigation_list_position = 1;
			return true;
		}
		else if (itemPosition == 2){ // Times(Custom date range)
			
			//Toast.makeText(GridActivity.this, "item Position is 2", Toast.LENGTH_SHORT).show();
			//Toast.makeText(getApplicationContext(), "Times", Toast.LENGTH_SHORT).show();
			if(savedstate == false)
			{

				DialogFragment endDate = new DatePickerFragment2();
				endDate.show(getFragmentManager(), "datePicker");
				
				DialogFragment startDate = new DatePickerFragment();
				startDate.show(getFragmentManager(), "datePicker");
			}
			else
			{
				//savedstate = true; i.e. an orientation was performed
				if(navigation_list_position == 2)
				{
					//savedstate = true and position = 1
					//need to remember the album
					//imgadapter.UpdateGridView();
				}
				else
				{
					//savedstate = true and position != 1

					DialogFragment endDate = new DatePickerFragment2();
					endDate.show(getFragmentManager(), "datePicker");
					
					DialogFragment startDate = new DatePickerFragment();
					startDate.show(getFragmentManager(), "datePicker");
				}
			}
			/**>>> Add Methods to respond Times(Custom date range)<<<**/
			update();//Delete it when methods add
			//update();//Delete it when methods add
			//setting this member to remember the list item number. used when changing view orientation
			navigation_list_position = 2;
			/**>>> END <<<**/
			return true;

		}
		else if (itemPosition == 3){ // Last week
			
			try {
				listByLastWeek();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//setting this member to remember the list item number. used when changing view orientation
			//update();
			navigation_list_position = 3;
			/**>>> END <<<**/
			return true;
		}
		else if (itemPosition == 4){ // A month
			//navigation_list_position = 4;
			//Toast.makeText(GridActivity.this, "item Position is 4", Toast.LENGTH_SHORT).show();
			try {
				listByLastMonth();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/**>>> Add Methods to respond Times(Custom date range)<<<**/
			//update();//Delete it when methods add
		
			//Toast.makeText(getApplicationContext(), "User select 'Last month'", Toast.LENGTH_SHORT).show();
			//setting this member to remember the list item number. used when changing view orientation
			navigation_list_position = 4;
			return true;
			/**>>> END <<<**/
		}
		else
		{
			Toast.makeText(GridActivity.this, "item Position is unknown", Toast.LENGTH_SHORT).show();
		}
		return true;
	}
//	private final String START_DATE_TAG = "startdate";
//	private final String END_DATE_TAG = "enddate";

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
			descriptionDialog.setTitle("输入描述文字")
                           .setIcon(R.drawable.ic_action_add_to_queue);
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

			albumDialog.setTitle("选择相册")
			.setIcon(R.drawable.ic_action_collection)
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
					//            						//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
					//Add photoId, individualBitmap to Cache and adapter ???????????????????
					//>>>>>>>>>>>>>>>>>>>>>>>>

		            						
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
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
	public class DatePickerFragment extends DialogFragment
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

			mStartingDate = mStartYear + mStartMonth + mStartDay + "_000000" ;
			Toast.makeText(getActivity(),"Starting dates: " + mStartingDate, Toast.LENGTH_LONG).show();
		}
	}
	public class DatePickerFragment2 extends DialogFragment
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
			mEndDate = mEndYear + mEndMonth + mEndDay + "_000000";
			Toast.makeText(getActivity(),"End dates: " + mEndDate, Toast.LENGTH_LONG).show();
			listByCustomDatesRange();
		}
	}
	// END
	private ArrayList<String> listByLastWeekList = null;
	private void listByLastWeek() throws ParseException
	{
		listByLastWeekList = new ArrayList<String>();
	       
		Date today = new Date();
	    int dayValue ;
	    ArrayList<String> ids = DatabaseManager.getInstance(getApplicationContext()).getPhotoIDs();
	        
	    for(String photoId : ids)//getList())
	    {

	        dayValue =  daysDifferences(today,stringToDate(photoId));
	               
//	          if (dayValue<2)
//	              yesterday.add(dateToSring(o.getDate()));
	          if (dayValue<8)
	        	  listByLastWeekList.add(photoId );
//	          if (dayValue<31)
//	              lastMonth.add(dateToSring(o.getDate()));
//	          if (dayValue<91)
//	              threeMonth.add(dateToSring(o.getDate()));
//	          if (dayValue>90)
//	              older.add(dateToSring(o.getDate()));
	         
	     }
        imgadapter.replaceList(listByLastWeekList);
        update();	
	}
	
	private ArrayList<String> listByLastMonthList = null;
	private void listByLastMonth() throws ParseException
	{
		listByLastMonthList = new ArrayList<String>();
	       
		Date today = new Date();
	    int dayValue ;
	        
	    ArrayList<String> ids = DatabaseManager.getInstance(getApplicationContext()).getPhotoIDs();
	    
	    for(String photoId : ids)
	    {

	        dayValue =  daysDifferences(today,stringToDate(photoId));
	               
//	          if (dayValue<2)
//	              yesterday.add(dateToSring(o.getDate()));
//	          if (dayValue<8)
//	        	  listByLastWeekList.add(photoId );
	          if (dayValue<31)
	        	  listByLastMonthList.add(photoId);
//	          if (dayValue<91)
//	              threeMonth.add(dateToSring(o.getDate()));
//	          if (dayValue>90)
//	              older.add(dateToSring(o.getDate()));
	         
	     }
        imgadapter.replaceList(listByLastMonthList);
        update();	
	}   
	
	
	public static String dateToSring (Date inputDate)
    {
    	return new SimpleDateFormat(PhotoManager.TIME_STAMP_FORMAT).format(inputDate);
    }
    
    public static Date stringToDate (String inputString) throws ParseException
    {
       return new SimpleDateFormat(PhotoManager.TIME_STAMP_FORMAT).parse(inputString);
    }
    
    public static int daysDifferences (Date currentDate , Date lastDate)
    {
        int dayValue ;
        long diff = currentDate.getTime() - lastDate.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        
        if ((diffDays==0))
            dayValue =1;
        else if (diffDays>0 && diffDays<7)
            dayValue=7;
        else if (diffDays>=7 && diffDays<30)
            dayValue=30;
        else if (diffDays>=30 && diffDays<90)
            dayValue=90;
        else
            dayValue=365;
       return dayValue;       
    }

	private ArrayList<String> listByCustomDateList = null;
	  private  void listByCustomDatesRange () //throws ParseException
	  {
		  
		  listByCustomDateList = new ArrayList();
		  //Date startDate = PhotoManager.getInstance(getApplicationContext()).getTimeStampAsDate(mStartingDate);
			Date startDate = null;
			Date endDate = null;
			Date tempDate = null;
	
			
			try 
			{
				startDate = new SimpleDateFormat(PhotoManager.TIME_STAMP_FORMAT).parse(mStartingDate);
				endDate = new SimpleDateFormat(PhotoManager.TIME_STAMP_FORMAT).parse(mEndDate);
				if ( startDate.after(endDate))
				{
					tempDate = startDate;
					startDate = endDate;
					endDate = tempDate;
				}
				
		      for( String photoId : getList())
		      {
		    	  
		    	  Date photoDate = new SimpleDateFormat(PhotoManager.TIME_STAMP_FORMAT).parse(photoId);
		
		      
		          if (endDate.after(photoDate) && startDate.before(photoDate))
		          {
		              listByCustomDateList.add(photoId);  
		          }	          
		      }
	          imgadapter.replaceList(listByCustomDateList);
	          update();
	
			} catch (ParseException e) {
				e.printStackTrace();
			}
	
	  }

	/**
	 * A simple non-UI Fragment that stores a single Object and is retained over configuration
	 * changes. It will be used to retain the ImageCache object.
	 */
	public static class AdapterListFragment extends Fragment {

		private ArrayList<String> list;

		/**
		 * Empty constructor as per the Fragment documentation
		 */
		public AdapterListFragment() {}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Make sure this Fragment is retained over a configuration change
			setRetainInstance(true);
		}

		public void setList(ArrayList<String> list) {
			this.list = list;
		}

		/**
		 * Get the stored object.
		 *
		 * @return The stored object
		 */
		public ArrayList<String> getList() {
			return list;
		}
	}

	public ArrayList<String> getList(FragmentManager fragmentManager)
	{
		// Search for, or create an instance of the non-UI RetainFragment
		final AdapterListFragment mFragment = findOrCreateListFragment(fragmentManager);

		// See if we already have an ImageCache stored in RetainFragment
		ArrayList<String> list = mFragment.getList();

		//		// No existing ImageCache, create one and store it in RetainFragment
		//		if (list == null) {
		//			list = initarray();
		//		}

		return list;
	}
}
