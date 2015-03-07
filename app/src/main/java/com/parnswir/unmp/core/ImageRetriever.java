package com.parnswir.unmp.core;

import android.database.sqlite.SQLiteDatabase;


public interface ImageRetriever {

	public byte[] getBitmap(int ID, SQLiteDatabase DB);
	
}
