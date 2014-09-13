package com.parnswir.unmp.playlist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Filter extends PlaylistElement {

	private static final long serialVersionUID = -4786047903938579938L;
	
	private SQLiteDatabase db = null;
	private Cursor cursor = null;
	private String sql;
	private Playlist playlist;
	
	public Filter(SQLiteDatabase database, String query) {
		db = database;
		sql = query;
		initPlaylist();
		fillPlaylist();
	}
	
	private void initPlaylist() {
		playlist = new Playlist();
		playlist.setRepeating(isRepeating());
		playlist.setShuffled(isShuffled());
	}
	
	private void fillPlaylist() {
		cursor = db.rawQuery(sql, null);
		boolean successful = cursor.moveToFirst();
		if (successful)
			while (! cursor.isAfterLast()) {
				playlist.children.add(new MediaFile(cursor.getString(0)));
				cursor.moveToNext();
			}
		freeCursor();
		playlist.start();
	}
	
	private void freeCursor() {
		if (cursor != null) {
			cursor.close();
		}
	}

	@Override
	public String getCurrentFile() {
		return playlist.getCurrentFile();
	}
	
	@Override
	public String getNextFile() {
		return playlist.getNextFile();
	}

}
