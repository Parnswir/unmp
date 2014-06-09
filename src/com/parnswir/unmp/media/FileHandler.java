package com.parnswir.unmp.media;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;

public abstract class FileHandler {
	
	private SQLiteDatabase db;

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

	public abstract void saveToDatabase(File file);
	public abstract void updateDatabaseEntryFor(File file);
	public abstract void deleteFromDatabase(File file);

}
