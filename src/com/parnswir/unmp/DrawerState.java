package com.parnswir.unmp;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.parnswir.unmp.core.C;

public class DrawerState {
	
	public String[] drawerItems = C.FRAGMENTS;
	public DrawerLayout mDrawerLayout = null;
	public RelativeLayout mDrawer = null;
	public ListView mDrawerList = null;
	public ActionBarDrawerToggle mDrawerToggle = null;
	
	public boolean isInitialized = false;
	
}
