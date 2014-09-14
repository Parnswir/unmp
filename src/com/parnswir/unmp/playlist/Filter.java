package com.parnswir.unmp.playlist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Filter extends Playlist {

	private static final long serialVersionUID = -4786047903938579938L;
	
	public Filter(SQLiteDatabase db, String query) {
		Cursor cursor = db.rawQuery(query, null);
		boolean successful = cursor.moveToFirst();
		if (successful)
			while (! cursor.isAfterLast()) {
				children.add(new MediaFile(cursor.getString(0)));
				cursor.moveToNext();
			}
		if (cursor != null) {
			cursor.close();
		}
		reset();
	}

}
