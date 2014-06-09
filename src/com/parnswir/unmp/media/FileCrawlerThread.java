package com.parnswir.unmp.media;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

import com.parnswir.unmp.core.ProgressObservable;
import com.parnswir.unmp.core.ProjectResources;

public abstract class FileCrawlerThread extends Thread {
	
	public static final Map<String, Class<?>> FILE_HANDLERS = new HashMap<String, Class<?>>();
	static{
		FILE_HANDLERS.put("mp3", MP3Handler.class);
		FILE_HANDLERS.put("wpl", WPLHandler.class);
	}
	
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
	
	public FileHandler getFileHandlerFor(File file) {
		FileHandler result = new DefaultFileHandler();
		String extension = getFileExt(file.getName());
		if (FILE_HANDLERS.containsKey(extension)) {
			try {
				result = (FileHandler) FILE_HANDLERS.get(extension).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		result.setDb(db);
		return result;
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
		setProgress("Looking for files in " + file.getAbsolutePath(), -1, -1);
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
		String extension = getFileExt(file.getName());
		return (FILE_HANDLERS.containsKey(extension)) && !stop;
	}
	
	private static String getFileExt(String fileName) {       
	     return fileName.toLowerCase(Locale.ENGLISH).substring((fileName.lastIndexOf(".") + 1), fileName.length());
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
