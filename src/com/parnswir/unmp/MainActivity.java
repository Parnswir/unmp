package com.parnswir.unmp;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import org.jaudiotagger.tag.TagOptionSingleton;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Observer {
	
	public static SQLiteDatabase DB;
	public boolean libraryShown = false;
	
	private String[] drawerItems = C.FRAGMENTS;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawer;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean rootView = true;
    
    private Hashtable<Integer, Fragment> fragmentCache = new Hashtable<Integer, Fragment>();
    
    private ListView contentList;
    private IconicAdapter adapter;
    private CoverList currentContent = new CoverList();
    
    private ListView mLibraryFolders;
	private ArrayList<String> folders = new ArrayList<String>();
	private SharedPreferences preferences;
	private int numberOfFoldersInLibrary = -1;
	private List<FileCrawlerThread> fileCrawlers;
	
	private MediaPlayerStatus playerStatus = new MediaPlayerStatus();
	
	public View currentLayout;
	private ArrayList<ImageButton> playerControls = new ArrayList<ImageButton>();
	private static int BTN_REPEAT = 0, BTN_PREV = 1, BTN_PLAY = 2, BTN_NEXT = 3, BTN_SHUFFLE = 4;
	private ProgressBar currentTitleProgress;
	private ArrayList<TextView> playerLabels = new ArrayList<TextView>();
	private static int LAB_POSITION = 0, LAB_LENGTH = 1, LAB_TITLE = 2, LAB_ARTIST = 3, LAB_ALBUM = 4;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        initializeDrawer();
	}
	
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        
        MusicDatabaseHelper dbHelper = new MusicDatabaseHelper(this);
		DB = dbHelper.getWritableDatabase();
		fileCrawlers = new ArrayList<FileCrawlerThread>();
		
		adapter = new IconicAdapter(this, currentContent); 
		
		TagOptionSingleton.getInstance().setAndroid(true);
        
        mDrawerToggle.syncState();
        
        setupIntentReceiver();
    }
	
	
	@Override
	protected void onStart() {
		super.onStart();
		setPlayerServiceState(PlayerService.STATUS);
		showPlayerHome();
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		if (playerStatus.playing) {
			setPlayerServiceState(PlayerService.STOP);
		} else {
			setPlayerServiceState(PlayerService.START);
		}
	}

	
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        menu.findItem(R.id.action_scan).setVisible(libraryShown && !drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
	
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    switch (keyCode) {
		    case KeyEvent.KEYCODE_MENU:
		        if (mDrawerLayout.isDrawerOpen(mDrawer)) {
		        	mDrawerLayout.closeDrawer(mDrawer);
		        } else {
		        	mDrawerLayout.openDrawer(mDrawer);
		        }
		        return true;
		    case KeyEvent.KEYCODE_BACK:
		    	if (!rootView) {
		    		showPlayerHome();
		    		return true;
		    	} 
	    }
	    return super.onKeyUp(keyCode, event);
	}
	
	
	private void initializeDrawer() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer = (RelativeLayout) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	
	private void showPlayerHome() {
		selectItem(0);
	}
	
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}


	private void selectItem(int position) {
		rootView = (position == 0);
	    showFragment(position);
	    mDrawerList.setItemChecked(position, true);
	    closeDrawerDelayedBy(100);
	}
	
	
	private void closeDrawerDelayedBy(int milliseconds) {
		new Handler().postDelayed(new Runnable() {
	        @Override
	        public void run() {
	        	mDrawerLayout.closeDrawer(mDrawer);
	        }
	    }, milliseconds);
	}
	
	
	private void showFragment(int position) {
		Fragment fragment = getFragment(position);
	    FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, fragment)
	                   .commit();
	}
	
	
	private Fragment getFragment(int position) {
		Fragment fragment;
		if (fragmentCache.contains(position)) {
			fragment = fragmentCache.get(position);
		} else {
			fragment = new ContentFragment();
			Bundle args = new Bundle();
		    args.putInt(ContentFragment.ARG_FRAGMENT_NUMBER, position);
		    fragment.setArguments(args);
			fragmentCache.put(position, fragment);
		}
		return fragment;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
	          return true;
	        }		
		switch (item.getItemId()) {
			case R.id.action_search: startActivityNamed(DebugActivity.class); return true;
			case R.id.action_scan: scanFolders(); return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	public void startActivityNamed(Class<?> className) {
		Intent intent = new Intent(this, className);
		startActivity(intent);
	}
	
	
	public void onClickCover(View view) {
		ActionBar actionBar = getActionBar();
		setOverlayVisibilityTo(!actionBar.isShowing());	
	}
	
	
	public void onClickPlay(View view) {
		if (playerStatus.stopped) {
			play();
		} else {
			pause();
		}
	}
	
	
	public void showToast(final String toast)
	{
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	
	
	public void setOverlayVisibilityTo(Boolean v) {
		ActionBar actionBar = getActionBar();
		RelativeLayout overlay = (RelativeLayout) findViewById(R.id.coverOverlay);
		if (!v) {
			actionBar.hide();
			overlay.setVisibility(View.INVISIBLE);
		} else  {
			actionBar.show();
			overlay.setVisibility(View.VISIBLE);
		}
	}


	public void onChangeToListFragment(int i, View layout) {
		String tableName = C.LIST_FRAGMENT_TABLENAMES[i-2];
		String nameColumn = C.getNameColumnFor(tableName);
		
        contentList = (ListView) layout.findViewById(R.id.contentList);
        contentList.setAdapter(adapter);
        currentContent.clear();
		
		Cursor cursor = DB.query(tableName, new String[] {nameColumn}, null, null, null, null, nameColumn);
		cursor.moveToFirst();
		while (! cursor.isAfterLast()) {
			currentContent.names.add(cursor.getString(0));
			cursor.moveToNext();
		} 
		cursor.close();
	}
	
	
	public void onShowLibrary(View layout) {
		setupPreferences();
		setupFolderList(layout);
		libraryShown = true;
	}
	
	
	private void setupFolderList(View layout) {
		folders.clear();
		folders.addAll(getLibraryFolders());
		folders.add(getString(R.string.addFolderToLibrary));
		mLibraryFolders = (ListView) layout.findViewById(R.id.libraryFolders);
		mLibraryFolders.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, folders));
		mLibraryFolders.setOnItemClickListener(new FolderClickListener());
		mLibraryFolders.setOnItemLongClickListener(new FolderClickListener());
	}
	
	
	private ArrayList<String> getLibraryFolders() {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < numberOfFoldersInLibrary; i++) {
			list.add(preferences.getString(C.FOLDER + Integer.toString(i), ""));
		}
		return list;
	}
	
	
	private class FolderClickListener implements ListView.OnItemClickListener, ListView.OnItemLongClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        handleAddFolderClick(view);
	    	return;
	    }

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			handleAddFolderClick(view);
			if (! clickedAddFolder(view)) {
				deleteFolderFromLibrary(position);
			}
			return true;
		}
		
		private void handleAddFolderClick(View view) {
			if (clickedAddFolder(view)) {
				showDirectorySelector();
			}
		}
		
		private boolean clickedAddFolder(View view) {
			TextView item = (TextView) view;
	        String selectedText = item.getText().toString();
	        String addFolder = getString(R.string.addFolderToLibrary);
	        
	        return selectedText.equals(addFolder);
		}
	}
	
	
	private void showDirectorySelector() {
		DirectoryChooserDialog directoryChooserDialog = new DirectoryChooserDialog(
			this, 
	        new DirectoryChooserDialog.ChosenDirectoryListener() {
	            @Override
	            public void onChosenDir(String chosenDir) {
					ArrayAdapter<String> adapter = getFolderListAdapter();
	                if (adapter.getPosition(chosenDir) == -1) {
	                	addFolderToLibrary(chosenDir);
	                	folders.add(0, chosenDir);
		                adapter.notifyDataSetChanged();
		                numberOfFoldersInLibrary += 1;
		                savePreferences();
	                }
	            }
		});
		directoryChooserDialog.setNewFolderEnabled(false);
		directoryChooserDialog.chooseDirectory();
	}
	
	
	private void deleteFolderFromLibrary(final int position) {
		new AlertDialog.Builder(this)
        .setMessage("Do you want to remove this folder from your library?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
				ArrayAdapter<String> adapter = getFolderListAdapter();
				removeFolderFromLibrary(folders.get(position));
				folders.remove(position);
                adapter.notifyDataSetChanged();
                numberOfFoldersInLibrary -= 1;
                savePreferences();
            }
        })
        .setNegativeButton("No", null)
        .show();
	}

	
	private void setupPreferences() {
		preferences = getPreferences(MODE_PRIVATE);
		numberOfFoldersInLibrary = preferences.getInt(C.NUMBEROFFOLDERS, 0);
	}
	
	
	private void savePreferences() {
		ArrayAdapter<String> adapter = getFolderListAdapter();
		SharedPreferences.Editor editor = preferences.edit();
		
		editor.putInt(C.NUMBEROFFOLDERS, numberOfFoldersInLibrary);
		for (int i = 0; i < numberOfFoldersInLibrary; i++) {
			editor.putString(C.FOLDER + Integer.toString(i), adapter.getItem(i));
		}
		editor.apply();
	}
	
	
	@SuppressWarnings("unchecked")
	private ArrayAdapter<String> getFolderListAdapter() {
		return ((ArrayAdapter<String>) mLibraryFolders.getAdapter());
	}
	
	
	private void addFolderToLibrary(String folder) {
		stopAll(FileAdditionThread.class);
		FileCrawlerThread crawler = new FileAdditionThread(DB, folder);
		addFileCrawler(crawler);
	}
	
	
	private void removeFolderFromLibrary(String folder) {
		stopAll(FileRemovalThread.class);
		FileRemovalThread crawler = new FileRemovalThread(DB, folder);
		addFileCrawler(crawler);
	}
	
	
	private void stopAll(Class<?> threadType) {
		int i = 0;
		while (i < fileCrawlers.size()) {
			if (fileCrawlers.get(i).getClass() == threadType) {
				fileCrawlers.get(i).kill();
				fileCrawlers.remove(i);
			} else {
				i += 1;
			}
		}
	}
	
	
	private void addFileCrawler(FileCrawlerThread thread) {
		thread.callback.addObserver(this);
		fileCrawlers.add(thread);
		thread.start();
	}
	
	
	private void scanFolders() {
		FileAdditionThread crawler = new FileAdditionThread(DB, folders);
		addFileCrawler(crawler);
	}
	
	
	@Override
	public void update(Observable observable, final Object data) {
		runOnUiThread(new Runnable() {
	        public void run()
	        {
	        	View libraryProgress = findViewById(R.id.library_progress);
	        	View drawerProgress = findViewById(R.id.drawer_progress);
	        	
	        	ProjectResources.ProgressItem cast = (ProjectResources.ProgressItem) data;
	        	TextView tv = (TextView) findViewById(R.id.tvCurrentFolder);
	    		if (tv != null) {
	    			tv.setText(cast.text);
	    			libraryProgress.setVisibility(View.VISIBLE);
	    		}
	    		
	    		int percentDone = 100;
	    		if (cast.count > 0) {
	    			percentDone = Math.round(100 / cast.count * cast.value);
	    		}
	    		
	    		ProgressBar pb = (ProgressBar) findViewById(R.id.pbCurrentFolder);
	    		pb.setIndeterminate(cast.count == -1);
	    		pb.setMax(100);
	    		pb.setProgress(percentDone);
	    		
	    		tv = (TextView) findViewById(R.id.tvUpdatingLibrary);
	    		tv.setText(String.format(getString(R.string.updatingLibrary) + " (%d%%)", percentDone));
	    		drawerProgress.setVisibility(View.VISIBLE);
	    		
	    		if (cast.value == cast.count) hideProgress();
	        }
	    });
	}
	
	
	private void hideProgress() {
		final View drawerProgress = findViewById(R.id.drawer_progress);
		final View libraryProgress = findViewById(R.id.library_progress);
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		    @Override
		    public void run() {
		        drawerProgress.setVisibility(View.INVISIBLE);
		        if (libraryProgress != null) libraryProgress.setVisibility(View.INVISIBLE);
		    }
		}, 5000);
	}
	
	
	public void onClickOnListItem(View item) {
		TextView label = (TextView) item.findViewById(R.id.label);
		String text = (String) label.getText();
		selectItem(0);
		showToast(text);
	}
	
	
	private void setPlayerServiceState(int state) {
		Intent intent = new Intent(this, PlayerService.class);
		intent.putExtra(PlayerService.EXTRA_ID, state);
		startService(intent);
	}
	
	
	private void play() {
		setPlayerServiceState(PlayerService.PLAY);
	}
	
	
	private void pause() {
		setPlayerServiceState(PlayerService.PAUSE);
	}
	
	
	private void setupIntentReceiver() {
		IntentFilter statusFilter = new IntentFilter(PlayerService.STATUS_INTENT);
	    registerReceiver(new StatusIntentReceiver(), statusFilter);
	}
	
	
	public void setupPlayerControls() {
		playerControls.clear();
		int[] buttons = {R.id.btnRepeat, R.id.btnPrevious, R.id.btnPlay, R.id.btnNext, R.id.btnShuffle};
		for (int button : buttons) {
			playerControls.add((ImageButton) currentLayout.findViewById(button));
		}
		
		playerLabels.clear();
		int[] labels = {R.id.tvTime, R.id.tvTimeLeft, R.id.tvTitle, R.id.tvArtist, R.id.tvAlbum};
		for (int label : labels) {
			playerLabels.add((TextView) currentLayout.findViewById(label));
		}
		
		currentTitleProgress = (ProgressBar) currentLayout.findViewById(R.id.seekBar);
	}
	
	
	private class StatusIntentReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	if (PlayerService.STATUS_INTENT.equals(intent.getAction())) {
	    		playerStatus = (MediaPlayerStatus) intent.getSerializableExtra(PlayerService.EXTRA_STATUS);
	    		updatePlayerStatus();
	      	}
	    }
		
	}
	
	
	public void updatePlayerStatus() {
		setPlayIconTo(playerStatus.paused || playerStatus.stopped);	    		
		showTitleDuration();
		showCurrentPosition();
	}
	
	
	private void setPlayIconTo(boolean shown) {
		Drawable icon;
		Resources res = getResources();
		if (shown) {
			icon = res.getDrawable(R.drawable.ic_action_play);
		} else {
			icon = res.getDrawable(R.drawable.ic_action_pause);
		}
		playerControls.get(BTN_PLAY).setImageDrawable(icon);
	}
	
	
	private void showCurrentPosition() {
		currentTitleProgress.setProgress(playerStatus.position);
		playerLabels.get(LAB_POSITION).setText(formatPosition(playerStatus.position));
	}
	
	
	private void showTitleDuration() {
		currentTitleProgress.setMax(playerStatus.length);
		playerLabels.get(LAB_LENGTH).setText(formatPosition(playerStatus.length));
	}
	
	
	private String formatPosition(int position) {
		int seconds = position / 1000;
		return String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
	}
}
