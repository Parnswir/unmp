package com.parnswir.unmp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlayerFragment extends AbstractFragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootView = super.onCreateView(inflater, container, savedInstanceState);
    	
    	inflate(R.layout.activity_main);
    	showActionBar();
    	
    	((MainActivity) activity).setupPlayerControls(rootView);
    	
        return rootView; 
    }
	
}
