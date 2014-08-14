package com.parnswir.unmp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public abstract class DrawerActivity extends Activity {

	public DrawerActivity() {
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		initializeDrawer();
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getState().mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    switch (keyCode) {
		    case KeyEvent.KEYCODE_MENU:
		        if (getState().mDrawerLayout.isDrawerOpen(getState().mDrawer)) {
		        	getState().mDrawerLayout.closeDrawer(getState().mDrawer);
		        } else {
		        	getState().mDrawerLayout.openDrawer(getState().mDrawer);
		        }
		        return true;
	    }
	    return super.onKeyUp(keyCode, event);
	}
	
	
	protected void initializeDrawer() {		
		int position = 0;
		
		if (getState().isInitialized) {
			position = getState().mDrawerList.getCheckedItemPosition();
		}
		
		getState().mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		getState().mDrawer = (RelativeLayout) findViewById(R.id.left_drawer);
		getState().mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
		
		getState().mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, getState().drawerItems));
		getState().mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	        
		if (getState().isInitialized) {
			getState().mDrawerList.setItemChecked(position, true);
		}
		
		getState().mDrawerToggle = new ActionBarDrawerToggle(this, getState().mDrawerLayout, R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        getState().mDrawerLayout.setDrawerListener(getState().mDrawerToggle);
        getState().isInitialized = true;
	        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getState().mDrawerToggle.syncState();
	}
	
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}


	protected void selectItem(int position) {
		getState().mDrawerList.setItemChecked(position, true);
		
		if (position == 4)
	    	startActivity(new Intent(this, DebugActivity.class));

	    //closeDrawerDelayedBy(100);
	}
	
	
	private void closeDrawerDelayedBy(int milliseconds) {
		new Handler().postDelayed(new Runnable() {
	        @Override
	        public void run() {
	        	getState().mDrawerLayout.closeDrawer(getState().mDrawer);
	        }
	    }, milliseconds);
	}
	
	
	protected DrawerState getState() {
		return DrawerStateHelper.getDrawerState();
	}

}
