package com.parnswir.unmp;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import org.jaudiotagger.tag.TagOptionSingleton;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.core.CoverList;
import com.parnswir.unmp.core.DatabaseUtils;
import com.parnswir.unmp.core.IconicAdapter;
import com.parnswir.unmp.core.ProjectResources;
import com.parnswir.unmp.media.MediaPlayerStatus;

public class MainActivity extends DrawerActivity implements Observer {
	
	public static SQLiteDatabase DB;
	public boolean libraryShown = false;
	
    private boolean rootView = true;
    
    private ListView contentList;
    private IconicAdapter adapter;
    private CoverList currentContent = new CoverList();
	
	private MediaPlayerStatus playerStatus = new MediaPlayerStatus();
	private String currentTitle = "";
	private BroadcastReceiver statusBroadcastReceiver;
	
	public View currentLayout;
	private ArrayList<ImageButton> playerControls = new ArrayList<ImageButton>();
	private static int BTN_REPEAT = 0, BTN_PREV = 1, BTN_PLAY = 2, BTN_NEXT = 3, BTN_SHUFFLE = 4;
	private ProgressBar currentTitleProgress;
	private ArrayList<TextView> playerLabels = new ArrayList<TextView>();
	private static int LAB_POSITION = 0, LAB_LENGTH = 1, LAB_TITLE = 2, LAB_ARTIST = 3, LAB_ALBUM = 4;
	
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        
        DB = DatabaseUtils.getDB(this);
		
		adapter = new IconicAdapter(this, currentContent); 
		
		TagOptionSingleton.getInstance().setAndroid(true);
    }
	
	
	@Override
	protected void onStart() {
		super.onStart();
		showPlayerHome();
		setupIntentReceiver();
		//setPlayerServiceState(PlayerService.STATUS);
	}
	
	
	@Override
	protected void onPause() {
		unregisterReceiver(statusBroadcastReceiver);
		if (playerStatus.playing) {
			setPlayerServiceState(PlayerService.START);
		} else {
			setPlayerServiceState(PlayerService.STOP);
		}
		super.onPause();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = state.mDrawerLayout.isDrawerOpen(state.mDrawer);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        menu.findItem(R.id.action_scan).setVisible(libraryShown && !drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
	
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    switch (keyCode) {
		    case KeyEvent.KEYCODE_BACK:
		    	if (!rootView) {
		    		showPlayerHome();
		    		return true;
		    	} 
	    }
	    return super.onKeyUp(keyCode, event);
	}
	
	
	private void showPlayerHome() {
		selectItem(0);
	}


	protected void selectItem(int position) {
		rootView = (position == 0);
		super.selectItem(position);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (state.mDrawerToggle.onOptionsItemSelected(item)) {
	          return true;
	        }		
		switch (item.getItemId()) {
			case R.id.action_search: startActivityNamed(DebugActivity.class); return true;
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
	
	
	public void onClickOnListItem(View item) {
		TextView label = (TextView) item.findViewById(R.id.label);
		String text = (String) label.getText();
		selectItem(0);
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
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
		statusBroadcastReceiver = new StatusIntentReceiver();
		IntentFilter statusFilter = new IntentFilter(PlayerService.STATUS_INTENT);
	    registerReceiver(statusBroadcastReceiver, statusFilter);
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
		updateTitleInfo();
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
	
	
	private void updateTitleInfo() {
		if (! playerStatus.currentTitle.equals(currentTitle))
			setTitleInfo();	
	}
	
	
	private void setTitleInfo() {
		Cursor cursor = DB.query(C.TAB_TITLES, new String[] {C.COL_ID, C.COL_TITLE, C.COL_YEAR}, C.COL_FILE + " = \"" + playerStatus.currentTitle + "\"", null, null, null, null);
		cursor.moveToFirst();
		while (! cursor.isAfterLast()) {
			Toast.makeText(getApplicationContext(), cursor.getString(1), Toast.LENGTH_SHORT).show();
			cursor.moveToNext();
		}
		cursor.close();
		currentTitle = playerStatus.currentTitle;
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
	
}
