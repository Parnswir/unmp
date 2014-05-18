package com.parnswir.unmp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public abstract class FileCrawlerThread extends Thread {
	
	protected SQLiteDatabase db;
	private List<String> folders;
	private List<String> files;
	protected boolean stop = false;
	
	public ProgressObservable callback = new ProgressObservable();
	
	
	public FileCrawlerThread(SQLiteDatabase db, String folder) {
		ArrayList<String> wrapper = new ArrayList<String>();
		wrapper.add(folder);
		init(db, wrapper);
	}
	
	public FileCrawlerThread(SQLiteDatabase db, List<String> folders) {
		init(db, folders);
	}
	
	private void init(SQLiteDatabase db, List<String> folders) {
		this.folders = folders;
		this.db = db;
		files = new ArrayList<String>();
	}
	
	public void run() {
		files.clear();
		for (String folderRoot : folders) {
			File root = new File(folderRoot);
			searchForFilesIn(root);
		}
		processFiles();
	}
	
	private void searchForFilesIn(File file) {
		if (stop) return;
		if (file.canRead()) {
			if (file.isDirectory()) {
				searchForFilesInDirectory(file);
			} else {
				addToFileListIfSuitable(file);
			}
		}
	}
	
	private void searchForFilesInDirectory(File file) {
		setProgress("Looking for files in " + file.getAbsolutePath(), 0, 1);
		for (File child : file.listFiles()) {
			searchForFilesIn(child);
		}
	}
	
	private void addToFileListIfSuitable(File file) {
		if (isSuitable(file)) {
			files.add(file.getAbsolutePath());
		}
	}
	
	private boolean isSuitable(File file) {
		return file.getAbsolutePath().endsWith(".mp3") && !stop;
	}
	
	private void setProgress(String text, float value, float count) {
		callback.change(new ProjectResources.ProgressItem(text, value, count));
	}
	
	private void processFiles() {
		for (String filePath : files) {
			File current = new File(filePath);
			if (current != null) {
				setProgress(filePath, files.indexOf(filePath), files.size());
				handleFile(current);
			}
		}
		setProgress("Done.", files.size(), files.size());
	}
	
	abstract void handleFile(File file);

	public void kill() {
		stop = true;		
	}

}
