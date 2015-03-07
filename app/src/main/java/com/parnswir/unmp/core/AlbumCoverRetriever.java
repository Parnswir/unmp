package com.parnswir.unmp.core;

import android.database.sqlite.SQLiteDatabase;

public class AlbumCoverRetriever implements ImageRetriever {

	@Override
	public byte[] getBitmap(int albumID, SQLiteDatabase DB) {
		return DatabaseUtils.getAlbumArtFor(albumID, DB);
	}

}
