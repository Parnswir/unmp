package com.parnswir.unmp;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LibraryActivity extends Activity implements Observer {
	
	private ListView mLibraryFolders;
	private ArrayList<String> folders = new ArrayList<String>();
	private SharedPreferences preferences;
	private int numberOfFoldersInLibrary = -1;
	private FileCrawlerThread fileCrawlerThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library);
		setupActionBar();
	}
	
	protected void onStart() {
		super.onStart();
		setupPreferences();
		setupFolderList();
	}
	
	protected void onPause() {
		savePreferences();
		scanFolders();
		super.onPause();
	}
	
	private void setupFolderList() {
		folders.clear();
		folders.addAll(getLibraryFolders());
		folders.add(getString(R.string.addFolderToLibrary));
		mLibraryFolders = (ListView) findViewById(R.id.libraryFolders);
		mLibraryFolders.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, folders));
		mLibraryFolders.setOnItemClickListener(new FolderClickListener());
		mLibraryFolders.setOnItemLongClickListener(new FolderClickListener());
	}
	
	private ArrayList<String> getLibraryFolders() {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < numberOfFoldersInLibrary; i++) {
			list.add(preferences.getString(C.FOLDER + Integer.toString(i), ""));
		}
		return list;
	}
	
	private class FolderClickListener implements ListView.OnItemClickListener, ListView.OnItemLongClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        handleAddFolderClick(view);
	    	return;
	    }

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			handleAddFolderClick(view);
			if (! clickedAddFolder(view)) {
				deleteFolderFromLibrary(position);
			}
			return true;
		}
		
		private void handleAddFolderClick(View view) {
			if (clickedAddFolder(view)) {
				showDirectorySelector();
			}
		}
		
		private boolean clickedAddFolder(View view) {
			TextView item = (TextView) view;
	        String selectedText = item.getText().toString();
	        String addFolder = getString(R.string.addFolderToLibrary);
	        
	        return selectedText.equals(addFolder);
		}
	}
	
	private void showDirectorySelector() {
		DirectoryChooserDialog directoryChooserDialog = new DirectoryChooserDialog(
			LibraryActivity.this, 
	        new DirectoryChooserDialog.ChosenDirectoryListener() {
	            @Override
	            public void onChosenDir(String chosenDir) {
					ArrayAdapter<String> adapter = getListAdapter();
	                if (adapter.getPosition(chosenDir) == -1) {
	                	folders.add(0, chosenDir);
		                adapter.notifyDataSetChanged();
		                numberOfFoldersInLibrary += 1;
	                }
	            }
		});
		directoryChooserDialog.setNewFolderEnabled(false);
		directoryChooserDialog.chooseDirectory();
	}
	
	private void deleteFolderFromLibrary(final int position) {
		new AlertDialog.Builder(this)
        .setMessage("Do you want to remove this folder from your library?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
				ArrayAdapter<String> adapter = getListAdapter();
            	folders.remove(position);
                adapter.notifyDataSetChanged();
                numberOfFoldersInLibrary -= 1;
            }
        })
        .setNegativeButton("No", null)
        .show();
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private void setupPreferences() {
		preferences = getPreferences(MODE_PRIVATE);
		numberOfFoldersInLibrary = preferences.getInt(C.NUMBEROFFOLDERS, 0);
	}
	
	private void savePreferences() {
		ArrayAdapter<String> adapter = getListAdapter();
		SharedPreferences.Editor editor = preferences.edit();
		
		editor.putInt(C.NUMBEROFFOLDERS, numberOfFoldersInLibrary);
		for (int i = 0; i < numberOfFoldersInLibrary; i++) {
			editor.putString(C.FOLDER + Integer.toString(i), adapter.getItem(i));
		}
		editor.apply();
	}
	
	@SuppressWarnings("unchecked")
	private ArrayAdapter<String> getListAdapter() {
		return ((ArrayAdapter<String>) mLibraryFolders.getAdapter());
	}
	
	private void scanFolders() {
		if (fileCrawlerThread == null) {
			fileCrawlerThread = new FileCrawlerThread(MainActivity.DB, folders);
			fileCrawlerThread.callback.addObserver(this);
		}
		if (! fileCrawlerThread.isAlive()) {
			fileCrawlerThread.start();
		}
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

	@Override
	public void update(Observable observable, final Object data) {
		runOnUiThread(new Runnable() {
	        public void run()
	        {
	        	TextView tv = (TextView) findViewById(R.id.tvCurrentFolder);
	    		tv.setText((String) data);
	        }
	    });
	}

}
