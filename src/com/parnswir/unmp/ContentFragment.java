package com.parnswir.unmp;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.parnswir.unmp.R;

public class ContentFragment extends Fragment {
    public static final String ARG_FRAGMENT_NUMBER = "fragment_number";
    private MainActivity mainActivity;
    
    public ContentFragment() {
    	
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mainActivity = (MainActivity) getActivity();
    	
    	int layoutID = R.layout.activity_main;
    	Boolean showList = false;
    	int i = getArguments().getInt(ARG_FRAGMENT_NUMBER);
    	
    	switch (i) {
    		case 0: showTitle("Now Playing"); break;
    		case 1: showTitle("Playlist"); break;
	    	case 5: showTitle("Playlists"); break;
	    	case 6: mainActivity.startActivityNamed(LibraryActivity.class); break;
	    	case 7: mainActivity.startActivityNamed(SettingsActivity.class); break;
	    	default: layoutID = R.layout.list_fragment; showList = true; break;
    	}
    	
    	showActionBar();
    	
    	View rootView = inflater.inflate(layoutID, container, false);
    	
    	if (showList)
    		showListFragmentFor(i, rootView);
    	
        return rootView; 
    }
    
    private void showActionBar() {
    	ActionBar actionBar = mainActivity.getActionBar();
    	actionBar.show();
    }
    
    private void showListFragmentFor(int index, View layout) {
    	showTitle(C.FRAGMENTS[index]);
    	mainActivity.onChangeToListFragment(index, layout);
    }
    
    private void showTitle(String title) {
    	ActionBar actionBar = mainActivity.getActionBar();
    	actionBar.setTitle(title);
    }
}