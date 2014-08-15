package com.parnswir.unmp;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbstractFragment extends Fragment {
    protected DrawerActivity activity;
    protected View rootView;
    protected LayoutInflater inflater;
    protected ViewGroup container;
    
    
    public AbstractFragment() {
    	
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	activity = (DrawerActivity) getActivity();
    	this.inflater = inflater;
    	this.container = container;
    	
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