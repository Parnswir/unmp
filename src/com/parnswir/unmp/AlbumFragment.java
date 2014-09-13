package com.parnswir.unmp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class AlbumFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootView = super.onCreateView(inflater, container, savedInstanceState);
    	
    	playAllButton.setOnClickListener(new PlayAllClickListener());
    	contentList.setOnItemClickListener(new ListItemClickListener());
    	
        return rootView; 
    }
   
    public class PlayAllClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			playFile("file:///storage/sdcard0/Music/Andreas Waldetoft/Europa Universalis III Soundtrack/04 Conquistador - Main Theme.mp3");
			activity.selectItem(0);
		}
    }
    
    public class ListItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> adapter, View item, int arg2, long arg3) {
			TextView label = (TextView) item.findViewById(R.id.label);
			String albumName = (String) label.getText();
			Toast.makeText(activity, "Album: " + albumName, Toast.LENGTH_SHORT).show();
			activity.selectItem(0);
			
		}
    }
	
}
