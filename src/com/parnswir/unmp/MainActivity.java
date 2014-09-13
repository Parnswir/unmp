package com.parnswir.unmp;

import java.util.Observable;
import java.util.Observer;

import org.jaudiotagger.tag.TagOptionSingleton;

import android.app.ActionBar;
import android.content.Intent;
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

public class MainActivity extends DrawerActivity implements Observer {
	
	public static SQLiteDatabase DB;
	
	
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
	}
	
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = state.mDrawerLayout.isDrawerOpen(state.mDrawer);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
	
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    switch (keyCode) {
		    case KeyEvent.KEYCODE_BACK:
		    	if (selectedItem != 0) {
		    		showPlayerHome();
		    		return true;
		    	} 
	    }
	    return super.onKeyUp(keyCode, event);
	}
	
	
	private void showPlayerHome() {
		selectItem(0);
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
