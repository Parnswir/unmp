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
    private View rootView;
    private LayoutInflater inflater;
    private ViewGroup container;
    
    public ContentFragment() {
    	
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mainActivity = (MainActivity) getActivity();
    	this.inflater = inflater;
    	this.container = container;
    	
    	mainActivity.libraryShown = false;
    	
    	int index = getArguments().getInt(ARG_FRAGMENT_NUMBER);
    	
    	switch (index) {
    		case 2: 
    		case 3: 
	    	case 4: inflate(R.layout.list_fragment); showListFragmentFor(index, rootView); break;
	    	case 6: inflate(R.layout.library_fragment); mainActivity.onShowLibrary(rootView); break;
	    	case 7: mainActivity.startActivityNamed(SettingsActivity.class); break;
	    	default: inflate(R.layout.activity_main);
    	}
    	
    	showActionBar();
    	showTitle(C.FRAGMENTS[index]);
    	
        return rootView; 
    }
    
    private void inflate(int layoutID) {
    	rootView = inflater.inflate(layoutID, container, false);
    }
    
    private void showActionBar() {
    	ActionBar actionBar = mainActivity.getActionBar();
    	actionBar.show();
    }
    
    private void showListFragmentFor(int index, View layout) {
    	mainActivity.onChangeToListFragment(index, layout);
    }
    
    private void showTitle(String title) {
    	ActionBar actionBar = mainActivity.getActionBar();
    	actionBar.setTitle(title);
    }
    
}