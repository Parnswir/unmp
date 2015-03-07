package com.parnswir.unmp;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.core.ProjectResources.FragmentProperties;

import java.util.ArrayList;

public class DrawerState {
	
	public ArrayList<String> drawerItems = new ArrayList<String>();
	public DrawerLayout mDrawerLayout = null;
	public RelativeLayout mDrawer = null;
	public ListView mDrawerList = null;
	public ActionBarDrawerToggle mDrawerToggle = null;
	
	public boolean isInitialized = false;
	
	public DrawerState() {
		for (FragmentProperties fragment : C.FRAGMENTS) {
			drawerItems.add(fragment.title);
		}
	}
	
}
