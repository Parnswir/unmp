package com.parnswir.unmp;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AlbumFragment extends AbstractFragment {

	private ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootView = super.onCreateView(inflater, container, savedInstanceState);
    	
    	inflate(R.layout.list_fragment);
    	showActionBar();
    	showTitle("Albums, yo!");
    	
    	ArrayList<String> items = new ArrayList<String>();
    	items.add("Album");
    	
    	list = (ListView) rootView.findViewById(R.id.contentList);
    	list.setAdapter(new ArrayAdapter<String>(activity, R.layout.drawer_list_item, items));
    	
    	list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(activity, "ALBUMS!", Toast.LENGTH_SHORT).show();
				
			}
		});
    	
        return rootView; 
    }
	
}
