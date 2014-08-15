package com.parnswir.unmp;

import com.parnswir.unmp.core.DatabaseUtils;

import android.app.ActionBar;
import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbstractFragment extends Fragment {
	
    protected DrawerActivity activity;
    protected View rootView;
    protected LayoutInflater inflater;
    protected ViewGroup container;
    protected SQLiteDatabase DB;
    
    public AbstractFragment() {
    	
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	activity = (DrawerActivity) getActivity();
    	this.inflater = inflater;
    	this.container = container;
    	
    	DB = DatabaseUtils.getDB(activity);
    	
        return null; 
    }
    
    protected void inflate(int layoutID) {
    	rootView = inflater.inflate(layoutID, container, false);
    	activity.currentLayout = rootView;
    }
    
    protected void showActionBar() {
    	ActionBar actionBar = activity.getActionBar();
    	actionBar.show();
    }
    
    protected void showTitle(String title) {
    	ActionBar actionBar = activity.getActionBar();
    	actionBar.setTitle(title);
    }
    
}