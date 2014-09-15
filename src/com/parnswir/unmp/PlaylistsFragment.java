package com.parnswir.unmp;

import java.io.File;
import java.io.IOException;
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
import com.parnswir.unmp.playlist.Playlist;
import com.parnswir.unmp.playlist.parser.WPLParser;
import com.parnswir.unmp.playlist.parser.WPLParser.WPLParserException;

public class PlaylistsFragment extends AbstractFragment {

	private ListView mPlaylists;
	private ArrayList<String> playlists = new ArrayList<String>();
	private ArrayList<String> playlistNames = new ArrayList<String>();
	
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
		initLists();
		mPlaylists = (ListView) rootView.findViewById(R.id.playlists);
		mPlaylists.setAdapter(new ArrayAdapter<String>(activity, R.layout.drawer_list_item, playlistNames));
		mPlaylists.setOnItemClickListener(new PlaylistClickListener());
		mPlaylists.setOnItemLongClickListener(new PlaylistClickListener());
	}
	
	
	private void initLists() {
		for (String playlistPath : playlists) { 
			WPLParser parser = new WPLParser(new File(playlistPath), DB);
			playlistNames.add(parser.getName());
		}
		playlistNames.add(getString(R.string.addPlaylist));
	}
	
	
	private class PlaylistClickListener implements ListView.OnItemClickListener, ListView.OnItemLongClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        handleAddPlaylistClick(view);
	        if (! clickedAddPlaylist(view)) {
				playPlaylist(position);
			}
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
	
	
	private void playPlaylist(int position) {
		String path = playlists.get(position);
		WPLParser parser = new WPLParser(new File(path), DB);
		Playlist playlist = new Playlist();
		try {
			playlist = parser.buildPlaylist();
			playPlaylist(playlist);
		} catch (IOException e) {
		} catch (WPLParserException e) {
		}
	}
	
	
	private void addPlaylist() {
		
	}    
	
}
