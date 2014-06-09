package com.parnswir.unmp.media;

import java.io.File;
import java.util.List;

import com.parnswir.unmp.core.DatabaseUtils;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FileRemovalThread extends FileCrawlerThread {

	public FileRemovalThread(SQLiteDatabase db, List<String> folders) {
		super(db, folders);
	}
	
	public FileRemovalThread(SQLiteDatabase db, String folder) {
		super(db, folder);
	}

	protected void handleFile(File file) {
		if (stop) return;
		if (DatabaseUtils.fileAlreadyInDatabase(file.getAbsolutePath(), db)) { 
			deleteFromDatabase(file);
		}
	}
	
	private void deleteFromDatabase(File file) {
		Log.d("HHHHHHHHHH", "delete " + file.getAbsolutePath());
	}
}
