package com.parnswir.unmp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LibraryActivity extends Activity {
	
	private ListView mLibraryFolders;
	private ArrayList<String> folders = new ArrayList<String>();
	private SharedPreferences preferences;
	private int numberOfFoldersInLibrary = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library);
		setupActionBar();
		setupPreferences();
		setupFolderList();
	}
	
	private void setupFolderList() {
		folders.addAll(getLibraryFolders());
		folders.add(getString(R.string.addFolderToLibrary));
		mLibraryFolders = (ListView) findViewById(R.id.libraryFolders);
		mLibraryFolders.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, folders));
		mLibraryFolders.setOnItemClickListener(new FolderClickListener());
	}
	
	private ArrayList<String> getLibraryFolders() {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < numberOfFoldersInLibrary; i++) {
			list.add(preferences.getString(C.FOLDER + Integer.toString(i), ""));
		}
		return list;
	}
	
	private class FolderClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        TextView item = (TextView) view;
	        String selectedText = item.getText().toString();
	        String addFolder = getString(R.string.addFolderToLibrary);
	        
	        if (selectedText.equals(addFolder)) {
	        	showDirectorySelector();
	        }
	    	return;
	    }
	}
	
	private void showDirectorySelector() {
		DirectoryChooserDialog directoryChooserDialog = new DirectoryChooserDialog(
			LibraryActivity.this, 
	        new DirectoryChooserDialog.ChosenDirectoryListener() {
	            @Override
	            public void onChosenDir(String chosenDir) {
	            	@SuppressWarnings("unchecked") // ArrayAdapter cast
					ArrayAdapter<String> adapter = ((ArrayAdapter<String>) mLibraryFolders.getAdapter());
	                if (adapter.getPosition(chosenDir) == -1) {
	                	folders.add(0, chosenDir);
		                adapter.notifyDataSetChanged();
		                numberOfFoldersInLibrary += 1;
		                savePreferences();
	                }
	            }
		});
		directoryChooserDialog.setNewFolderEnabled(false);
		directoryChooserDialog.chooseDirectory();
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private void setupPreferences() {
		preferences = getPreferences(MODE_PRIVATE);
		numberOfFoldersInLibrary = preferences.getInt(C.NUMBEROFFOLDERS, 0);
	}
	
	private void savePreferences() {
		@SuppressWarnings("unchecked") // ArrayAdapter cast
		ArrayAdapter<String> adapter = ((ArrayAdapter<String>) mLibraryFolders.getAdapter());
		SharedPreferences.Editor editor = preferences.edit();
		
		editor.putInt(C.NUMBEROFFOLDERS, numberOfFoldersInLibrary);
		for (int i = 0; i < numberOfFoldersInLibrary; i++) {
			editor.putString(C.FOLDER + Integer.toString(i), adapter.getItem(i));
		}
		editor.apply();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.library, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
