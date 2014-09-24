package com.parnswir.unmp;

import java.util.ArrayList;

import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.core.CoverList;
import com.parnswir.unmp.core.DatabaseUtils;
import com.parnswir.unmp.core.IconicAdapter;
import com.parnswir.unmp.playlist.Playlist;

public class ListFragment extends AbstractFragment {
	
	public static final String SCROLL_POSITION = "scroll_position";

	protected ListView contentList;
	protected CoverList currentContent = new CoverList();
	protected ArrayList<String> itemList = new ArrayList<String>();
	protected Button playAllButton;
	
	private String suffix = "", tableName, nameColumn, join, currentID;
	private boolean isInDetailedState = false;


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootView = super.onCreateView(inflater, container, savedInstanceState);
    	inflate(R.layout.list_fragment);
    	showActionBar();
    	init();
        return rootView; 
    }
	
	@Override
	public void onStart() {
		super.onStart();
		contentList.setSelection(preferences.getInt(SCROLL_POSITION + suffix, 0));
	}
	
	@Override
	public void onStop() {
		int position = contentList.getFirstVisiblePosition();
		Editor editor = preferences.edit();
		editor.putInt(SCROLL_POSITION + suffix, position);
		editor.apply();
		super.onStop();
	}
	
	public void init() {
		tableName = C.LIST_FRAGMENT_TABLENAMES[activity.selectedItem - 2];
		nameColumn = C.getNameColumnFor(tableName);
		suffix = tableName;
		join = DatabaseUtils.getJoinForTables(new String[] {tableName});
		 
        contentList = (ListView) rootView.findViewById(R.id.contentList);
        contentList.setOnItemClickListener(new ListItemClickListener());
		
		playAllButton = (Button) rootView.findViewById(R.id.playAllButton);
		playAllButton.setOnClickListener(new PlayAllClickListener());
		
		displayContentFor(null);
	}
	
	private void displayContentFor(String id) {
		clearLists();
		Cursor cursor;
		if (id == null) {
			contentList.setAdapter(new IconicAdapter(activity, currentContent));
			cursor = DB.query(tableName, new String[] {C.COL_ID, nameColumn}, null, null, null, null, nameColumn);
		} else {
			contentList.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, currentContent.names));
			cursor = DB.query(join, 
					new String[] {C.COL_FILE, C.COL_TITLE}, 
					tableName + "." + C.COL_ID + " = ?", 
					new String[] {id}, 
					null, null, 
					C.COL_TRACK_NUMBER);
		}
		cursor.moveToFirst();
		while (! cursor.isAfterLast()) {
			itemList.add(cursor.getString(0));
			currentContent.names.add(cursor.getString(1));
			cursor.moveToNext();
		} 
		cursor.close();
		currentID = id;
	}

	private void clearLists() {
		currentContent.clear();
		itemList.clear();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isInDetailedState) {
				displayContentFor(null);
				isInDetailedState = false;
				return true;
			}
		}
		return false;
	}
	
	public class ListItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> adapter, View item, int position, long id) {
			if (isInDetailedState) {
				playFile(itemList.get(position));
				activity.selectItem(0);
			} else {
				showTitle(currentContent.names.get(position));
				displayContentFor(itemList.get(position));
				isInDetailedState = true;
			}
		}
    } 
	
	public class PlayAllClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			Playlist playlist = new Playlist();
			String query = String.format("SELECT %s FROM %s", C.COL_FILE, C.TAB_TITLES);
			if (isInDetailedState) {
				query = String.format("SELECT %s FROM %s WHERE %s = %s", C.COL_FILE, join, tableName + "." + C.COL_ID, currentID);
			}
			playlist.addItemsFromFilter(DB, query);
			playPlaylist(playlist);
		}
	}
	
}
