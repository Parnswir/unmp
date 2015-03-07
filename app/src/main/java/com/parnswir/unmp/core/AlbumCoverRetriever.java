package com.parnswir.unmp.core;

import android.database.sqlite.SQLiteDatabase;

public class AlbumCoverRetriever implements ImageRetriever {

	@Override
	public byte[] getBitmap(String name, SQLiteDatabase DB) {
		return DatabaseUtils.getAlbumArtFor(name, DB);
	}

}
