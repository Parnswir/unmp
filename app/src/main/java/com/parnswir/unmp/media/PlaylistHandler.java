package com.parnswir.unmp.media;

import android.content.ContentValues;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.core.DatabaseUtils;

import java.io.File;

public class PlaylistHandler extends FileHandler {

	@Override
	public void saveToDatabase(File file) {
		ContentValues cv = new ContentValues();
		cv.put(C.COL_ID, DatabaseUtils.getNextIDForTable(C.TAB_PLAYLISTS, getDb()));
		cv.put(C.COL_FILE, file.getAbsolutePath());
		getDb().insert(C.TAB_PLAYLISTS, null, cv);
	}

	@Override
	public void updateDatabaseEntryFor(File file) {
		return;
	}

	@Override
	public void deleteFromDatabase(File file) {
		getDb().delete(C.TAB_PLAYLISTS, C.COL_FILE + " = ?", new String[] {DatabaseUtils.normalize(file.getAbsolutePath())});
	}

}
