package com.parnswir.unmp.playlist;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Playlist extends PlaylistElement {
	
	private static final long serialVersionUID = 3196172024840309061L;
	
	private String name;
	private int current;
	
	public ArrayList<MediaFile> children = new ArrayList<MediaFile>();
	
	
	public void addItemsFromFilter(SQLiteDatabase db, String query) {
		Cursor cursor = db.rawQuery(query, null);
		boolean successful = cursor.moveToFirst();
		if (successful)
			while (! cursor.isAfterLast()) {
				children.add(new MediaFile(cursor.getString(0)));
				cursor.moveToNext();
			}
		cursor.close();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return current;
	}
	
	public void setPosition(int position) {
		current = position;
	}
	
	public void nextSource() {
		setPosition(getPosition() + 1);
	}
	
	public void previousSource() {
		setPosition(getPosition() - 1);
	}
	
	public boolean isAtEnd() {
		return getPosition() >= children.size();
	}
	
	public boolean isAtStart() {
		return getPosition() < 0;
	}
	
	public MediaFile getCurrentChild() {
		if (getPosition() < 0 || getPosition() >= children.size())
			return null;
		return children.get(getPosition());
	}

	@Override
	public String getCurrentFile() {
		if (getCurrentChild() == null) 
			return null;
		return getCurrentChild().getCurrentFile();
	}
	
	public void next() {
		if (isRepeating()) return;
		nextSource();
		if (isAtEnd() && isRepeatingAll()) 
			setPosition(0);
	}
	
	public void previous() {
		if (isRepeating()) return;
		previousSource();
		if (isAtStart() && isRepeatingAll())
			setPosition(children.size() - 1);
	}
}
