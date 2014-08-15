package com.parnswir.unmp;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parnswir.unmp.core.C;

public class ContentFragment extends Fragment {
    public static final String ARG_FRAGMENT_NUMBER = "fragment_number";

    private DrawerActivity activity;
    private View rootView;
    private LayoutInflater inflater;
    private ViewGroup container;
    
    public ContentFragment() {
    	
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	activity = (DrawerActivity) getActivity();
    	this.inflater = inflater;
    	this.container = container;
    	
    	int index = getArguments().getInt(ARG_FRAGMENT_NUMBER);
    	inflate(C.FRAGMENTS[index].layout);
    	
//    	switch (index) {
//    		case 2: 
//    		case 3: 
//	    	case 4: inflate(R.layout.list_fragment); break;//showListFragmentFor(index, rootView); break;
//	    	case 5: inflate(R.layout.playlists_fragment); break; //activity.onShowPlaylistList(rootView); break;
//	    	case 6: inflate(R.layout.library_fragment); break; // activity.onShowLibrary(rootView); break;
//	    	//case 7: activity.startActivityNamed(SettingsActivity.class); break;
//	    	default: inflate(R.layout.activity_main); //activity.setupPlayerControls(); mainActivity.updatePlayerStatus();
//    	}
    	
    	showActionBar();
    	showTitle(C.FRAGMENTS[index].title);
    	
        return rootView; 
    }
    
    private void inflate(int layoutID) {
    	rootView = inflater.inflate(layoutID, container, false);
    	activity.currentLayout = rootView;
    }
    
    private void showActionBar() {
    	ActionBar actionBar = activity.getActionBar();
    	actionBar.show();
    }
    
    private void showTitle(String title) {
    	ActionBar actionBar = activity.getActionBar();
    	actionBar.setTitle(title);
    }
    
}