package com.parnswir.unmp;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parnswir.unmp.core.DatabaseUtils;

public class PlaylistsFragment extends AbstractFragment {

	private ListView mPlaylists;
	private ArrayList<String> playlists = new ArrayList<String>();
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootView = super.onCreateView(inflater, container, savedInstanceState);
    	
    	inflate(R.layout.playlists_fragment);
    	showActionBar();
    	showTitle("Playlists");
    	
    	initPlaylistList();
    	
        return rootView; 
    }
	
	
	public void initPlaylistList() {
		playlists = DatabaseUtils.getAllPlaylists(DB);
		playlists.add(getString(R.string.addPlaylist));
		mPlaylists = (ListView) rootView.findViewById(R.id.playlists);
		mPlaylists.setAdapter(new ArrayAdapter<String>(activity, R.layout.drawer_list_item, playlists));
		mPlaylists.setOnItemClickListener(new PlaylistClickListener());
		mPlaylists.setOnItemLongClickListener(new PlaylistClickListener());
	}
	
	
	private class PlaylistClickListener implements ListView.OnItemClickListener, ListView.OnItemLongClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        handleAddPlaylistClick(view);
	    	return;
	    }

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			handleAddPlaylistClick(view);
			if (! clickedAddPlaylist(view)) {
				deletePlaylist(position);
			}
			return true;
		}
		
		private void handleAddPlaylistClick(View view) {
			if (clickedAddPlaylist(view)) {
				addPlaylist();
			}
		}
		
		private boolean clickedAddPlaylist(View view) {
			TextView item = (TextView) view;
	        String selectedText = item.getText().toString();
	        String addPlaylist = getString(R.string.addPlaylist);
	        
	        return selectedText.equals(addPlaylist);
		}
	}
	
	
	private void deletePlaylist(int position) {
		
	}
	
	
	private void addPlaylist() {
		
	}
	
}
