package com.parnswir.unmp;

import java.util.Hashtable;
import java.util.Observer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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

import com.parnswir.unmp.core.C;

public abstract class DrawerActivity extends Activity implements Observer {

	public static Hashtable<Integer, Fragment> fragmentCache = new Hashtable<Integer, Fragment>();
	
	protected DrawerState state;
	
	public View currentLayout;
	public int selectedItem = 0;
	
	
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
		state = new DrawerState();
		showFragment(selectedItem);
		initializeDrawer();
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        state.mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    switch (keyCode) {
		    case KeyEvent.KEYCODE_MENU:
		        if (state.mDrawerLayout.isDrawerOpen(state.mDrawer)) {
		        	state.mDrawerLayout.closeDrawer(state.mDrawer);
		        } else {
		        	state.mDrawerLayout.openDrawer(state.mDrawer);
		        }
		        return true;
	    }
	    return super.onKeyUp(keyCode, event);
	}
	
	
	protected void initializeDrawer() {		
		state.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		state.mDrawer = (RelativeLayout) findViewById(R.id.left_drawer);
		state.mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
		
		state.mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, state.drawerItems));
		state.mDrawerList.setOnItemClickListener(new DrawerItemClickListener());    
		state.mDrawerList.setItemChecked(selectedItem, true);
		
		state.mDrawerToggle = new ActionBarDrawerToggle(this, state.mDrawerLayout, R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        state.mDrawerLayout.setDrawerListener(state.mDrawerToggle);
        state.isInitialized = true;
	        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        state.mDrawerToggle.syncState();
	}
	
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}


	protected void selectItem(int position) {
		selectedItem = position;
		state.mDrawerList.setItemChecked(position, true);
		showFragment(position);
		closeDrawerDelayedBy(100);
	}
	
	
	private void closeDrawerDelayedBy(int milliseconds) {
		new Handler().postDelayed(new Runnable() {
	        @Override
	        public void run() {
	        	state.mDrawerLayout.closeDrawer(state.mDrawer);
	        }
	    }, milliseconds);
	}
	
	protected void showFragment(int position) {
		Fragment fragment = getFragment(position);
	    FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, fragment)
	                   .commit();
	}
	
	
	private Fragment getFragment(int position) {
		Fragment fragment = null;
		if (fragmentCache.contains(position)) {
			fragment = fragmentCache.get(position);
		} else {
			Class<?> fragmentClass = C.FRAGMENTS[position].handler;
			fragment = getFragmentInstance(fragmentClass);
			fragmentCache.put(position, fragment);
		}
		return fragment;
	}
	
	
	private Fragment getFragmentInstance(Class<?> fragmentClass) {
		try {
			return (Fragment) fragmentClass.newInstance();
		} catch (InstantiationException e) {
			
		} catch (IllegalAccessException e) {
			
		}
		return null;
	}

}
