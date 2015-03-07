package com.parnswir.unmp.media;

import android.database.sqlite.SQLiteDatabase;

import com.parnswir.unmp.core.DatabaseUtils;

import java.io.File;
import java.util.List;

public class FileRemovalThread extends FileCrawlerThread {

	public FileRemovalThread(SQLiteDatabase db, List<String> folders) {
		super(db, folders);
	}
	
	public FileRemovalThread(SQLiteDatabase db, String folder) {
		super(db, folder);
	}

	protected void handleFile(File file) {
		if (stop) return;
		FileHandler fileHandler = getFileHandlerFor(file);
		if (DatabaseUtils.fileAlreadyInDatabase(file.getAbsolutePath(), db)) { 
			fileHandler.deleteFromDatabase(file);
		}
	}
	
}
