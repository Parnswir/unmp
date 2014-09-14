package com.parnswir.unmp.playlist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Filter extends PlaylistElement {

	private static final long serialVersionUID = -4786047903938579938L;

	private Playlist playlist;
	
	public Filter(SQLiteDatabase database, String query) {
		initPlaylist();
		fillPlaylist(database, query);
	}
	
	private void initPlaylist() {
		playlist = new Playlist();
		playlist.setRepeating(isRepeating());
		playlist.setShuffled(isShuffled());
	}
	
	private void fillPlaylist(SQLiteDatabase db, String sql) {
		Cursor cursor = db.rawQuery(sql, null);
		boolean successful = cursor.moveToFirst();
		if (successful)
			while (! cursor.isAfterLast()) {
				playlist.children.add(new MediaFile(cursor.getString(0)));
				cursor.moveToNext();
			}
		if (cursor != null) {
			cursor.close();
		}
		playlist.start();
	}
	
	@Override
	public String getCurrentFile() {
		return playlist.getCurrentFile();
	}

	@Override
	public void next() {
		playlist.next();		
	}

}
