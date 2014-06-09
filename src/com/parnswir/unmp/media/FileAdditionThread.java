package com.parnswir.unmp.media;

import java.io.File;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.parnswir.unmp.core.DatabaseUtils;

public class FileAdditionThread extends FileCrawlerThread {

	public FileAdditionThread(SQLiteDatabase db, List<String> folders) {
		super(db, folders);
	}
	
	public FileAdditionThread(SQLiteDatabase db, String folder) {
		super(db, folder);
	}

	protected void handleFile(File file) {
		if (stop) return;
		FileHandler fileHandler = getFileHandlerFor(file);
		if (! DatabaseUtils.fileAlreadyInDatabase(file.getAbsolutePath(), db)) { 
			fileHandler.saveToDatabase(file);
		} else {
			fileHandler.updateDatabaseEntryFor(file);
		}
	}


}
