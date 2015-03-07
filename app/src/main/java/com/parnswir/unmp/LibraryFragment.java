package com.parnswir.unmp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.media.FileAdditionThread;
import com.parnswir.unmp.media.FileCrawlerThread;
import com.parnswir.unmp.media.FileRemovalThread;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends AbstractFragment {
   
    private ListView libraryFolders;
    private ArrayList<String> folders = new ArrayList<String>();
	private int numberOfFoldersInLibrary = -1;
	private List<FileCrawlerThread> fileCrawlers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootView = super.onCreateView(inflater, container, savedInstanceState);
    	
    	inflate(R.layout.library_fragment);
    	showActionBar();
    	showTitle("Manage your library");
    	
    	libraryFolders = (ListView) rootView.findViewById(R.id.libraryFolders);
    	onShowLibrary();
    	
        return rootView; 
    }
    
    
    public void onShowLibrary() {
    	fileCrawlers = new ArrayList<FileCrawlerThread>();
    	
		setupPreferences();
		setupFolderList();
	}
	
	
	private void setupFolderList() {
		folders.clear();
		folders.addAll(getLibraryFolders());
		folders.add(getString(R.string.addFolderToLibrary));
		libraryFolders = (ListView) rootView.findViewById(R.id.libraryFolders);
		libraryFolders.setAdapter(new ArrayAdapter<String>(activity, R.layout.drawer_list_item, folders));
		libraryFolders.setOnItemClickListener(new FolderClickListener());
		libraryFolders.setOnItemLongClickListener(new FolderClickListener());
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
			activity, 
	        new DirectoryChooserDialog.ChosenDirectoryListener() {
	            @Override
	            public void onChosenDir(String chosenDir) {
					ArrayAdapter<String> adapter = getFolderListAdapter();
	                if (adapter.getPosition(chosenDir) == -1) {
	                	addFolderToLibrary(chosenDir);
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
	
	
	private void deleteFolderFromLibrary(final int position) {
		new AlertDialog.Builder(activity)
        .setMessage("Do you want to remove this folder from your library?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
				ArrayAdapter<String> adapter = getFolderListAdapter();
				removeFolderFromLibrary(folders.get(position));
				folders.remove(position);
                adapter.notifyDataSetChanged();
                numberOfFoldersInLibrary -= 1;
                savePreferences();
            }
        })
        .setNegativeButton("No", null)
        .show();
	}

	
	private void setupPreferences() {
		numberOfFoldersInLibrary = preferences.getInt(C.NUMBEROFFOLDERS, 0);
	}
	
	
	private void savePreferences() {
		ArrayAdapter<String> adapter = getFolderListAdapter();
		SharedPreferences.Editor editor = preferences.edit();
		
		editor.putInt(C.NUMBEROFFOLDERS, numberOfFoldersInLibrary);
		for (int i = 0; i < numberOfFoldersInLibrary; i++) {
			editor.putString(C.FOLDER + Integer.toString(i), adapter.getItem(i));
		}
		editor.apply();
	}
	
	
	@SuppressWarnings("unchecked")
	private ArrayAdapter<String> getFolderListAdapter() {
		return ((ArrayAdapter<String>) libraryFolders.getAdapter());
	}
	
	
	private void addFolderToLibrary(String folder) {
		stopAll(FileAdditionThread.class);
		FileCrawlerThread crawler = new FileAdditionThread(DB, folder);
		addFileCrawler(crawler);
	}
	
	
	private void removeFolderFromLibrary(String folder) {
		stopAll(FileRemovalThread.class);
		FileRemovalThread crawler = new FileRemovalThread(DB, folder);
		addFileCrawler(crawler);
	}
	
	
	private void stopAll(Class<?> threadType) {
		int i = 0;
		while (i < fileCrawlers.size()) {
			if (fileCrawlers.get(i).getClass() == threadType) {
				fileCrawlers.get(i).kill();
				fileCrawlers.remove(i);
			} else {
				i += 1;
			}
		}
	}
	
	
	private void addFileCrawler(FileCrawlerThread thread) {
		thread.callback.addObserver(activity);
		fileCrawlers.add(thread);
		thread.start();
	}
    
}