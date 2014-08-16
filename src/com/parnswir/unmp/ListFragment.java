package com.parnswir.unmp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListFragment extends AbstractFragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootView = super.onCreateView(inflater, container, savedInstanceState);
    	
    	inflate(R.layout.list_fragment);
    	showActionBar();
    	
        return rootView; 
    }
	
}
