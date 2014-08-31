package com.parnswir.unmp;

import java.util.Observable;
import java.util.Observer;

import org.jaudiotagger.tag.TagOptionSingleton;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parnswir.unmp.core.DatabaseUtils;
import com.parnswir.unmp.core.ProjectResources;
import com.parnswir.unmp.media.MediaPlayerStatus;

public class MainActivity extends DrawerActivity implements Observer {
	
	public static SQLiteDatabase DB;
	public boolean libraryShown = false;
	public MediaPlayerStatus playerStatus = new MediaPlayerStatus();
	
    private boolean rootView = true;
	private BroadcastReceiver statusBroadcastReceiver;
	private boolean receiving = false;
	
	
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        
        DB = DatabaseUtils.getDB(this);
		
		TagOptionSingleton.getInstance().setAndroid(true);
    }
	
	
	@Override
	protected void onStart() {
		super.onStart();
		showPlayerHome();
		setupIntentReceiver();
		setPlayerServiceState(PlayerService.STATUS, null);
	}
	
	
	@Override
	protected void onPause() {
		stopReceiving();
		if (playerStatus.playing) {
			setPlayerServiceState(PlayerService.START, null);
		} else {
			setPlayerServiceState(PlayerService.STOP, null);
		}
		super.onPause();
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
		stopReceiving();
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
	
	
	public void onClickOnListItem(View item) {
		TextView label = (TextView) item.findViewById(R.id.label);
		String text = (String) label.getText();
		selectItem(0);
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}
	
	
	private void setPlayerServiceState(int state, Bundle bundle) {
		Intent intent = new Intent(this, PlayerService.class);
		intent.putExtra(PlayerService.EXTRA_ID, state);
		if (bundle == null) bundle = new Bundle();
		intent.putExtra(PlayerService.SERVICE_INTENT_BUNDLE, bundle);
		startService(intent);
	}
	
	
	private void playFile(String fileName) {
		Bundle bundle = new Bundle();
		bundle.putString(PlayerService.FILE_NAME, fileName);
		setPlayerServiceState(PlayerService.PLAY, bundle);
	}
	
	
	private void play() {
		playFile("file:///storage/sdcard0/Music/MIOIOIN/MOON EP/03 Hydrogen.mp3");
		//setPlayerServiceState(PlayerService.PLAY, null);
	}
	
	
	private void pause() {
		setPlayerServiceState(PlayerService.PAUSE, null);
	}
	
	
	private void setupIntentReceiver() {
		if (!receiving) {
			statusBroadcastReceiver = new StatusIntentReceiver();
			IntentFilter statusFilter = new IntentFilter(PlayerService.STATUS_INTENT);
		    registerReceiver(statusBroadcastReceiver, statusFilter);
		    receiving = true;
		}
	}
	
	
	private class StatusIntentReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	if (PlayerService.STATUS_INTENT.equals(intent.getAction()) && currentFragment.getFragmentClass().equals("PlayerFragment")) {
	    		playerStatus = (MediaPlayerStatus) intent.getSerializableExtra(PlayerService.EXTRA_STATUS);
	    	    ((PlayerFragment) currentFragment).updatePlayerStatus();
	      	}
	    }
		
	}
	
	
	private void stopReceiving() {
		if (statusBroadcastReceiver != null && receiving) {
			unregisterReceiver(statusBroadcastReceiver);
			receiving = false;
		}
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
