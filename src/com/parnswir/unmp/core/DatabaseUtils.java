package com.parnswir.unmp.core;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class DatabaseUtils {
	
	public static final String IDNOTINTABLE = "";
	
	private static SQLiteDatabase DB = null;
	
	public static SQLiteDatabase getDB(Context context) {
		if (DB == null) {
			MusicDatabaseHelper dbHelper = new MusicDatabaseHelper(context);
			DB = dbHelper.getWritableDatabase();
		}
		return DB;
	}
	
	public static int getCurrentIDForTable(String tableName, SQLiteDatabase db) {
		Cursor c = db.query(tableName, new String[] {C.COL_ID}, null, null, null, null, C.COL_ID + " DESC");
		boolean successful = c.moveToFirst();					
		int result = 0;
		if (successful) {
			result = c.getInt(0);
		}
		c.close();
		return result;
	}

	public static int getNextIDForTable(String tableName, SQLiteDatabase db) {
		return getCurrentIDForTable(tableName, db) + 1;
	}
	
	public static String getIDForTableEntry(String tableName, String entrySelection, String[] entrySelectionArguments, SQLiteDatabase db) {
		Cursor c = db.query(tableName, new String[] {C.COL_ID}, entrySelection, entrySelectionArguments, null, null, null);
		boolean successful = c.moveToFirst();					
		String result = IDNOTINTABLE;
		if (successful) {
			result = c.getString(0);
		}
		c.close();
		return result;
	}
	
	public static String getIDForTableEntryByName(String tableName, String entryName, SQLiteDatabase db) {
		return getIDForTableEntry(tableName, C.getNameColumnFor(tableName) + " = ?", new String[] {entryName}, db);
	}
	
	public static String getIDForTuple(String tableName, ContentValues cv, SQLiteDatabase db) {
		return getIDForTableEntryByName(tableName, cv.getAsString(C.idNameFrom(tableName)), db);
	}
	
	public static boolean fileAlreadyInDatabase(String absolutePath, SQLiteDatabase db) {
		boolean result = 
			(! getIDForTableEntry(C.TAB_TITLES, C.COL_FILE + " = ?", new String[] {normalize(absolutePath)}, db).equals(IDNOTINTABLE)) ||
			(! getIDForTableEntry(C.TAB_PLAYLISTS, C.COL_FILE + " = ?", new String[] {normalize(absolutePath)}, db).equals(IDNOTINTABLE));
		return result;
	}
	
	public static boolean tupleAlreadyInTable(String tableName, ContentValues cv, SQLiteDatabase db) {
		String[] whereClauseItems = new String[cv.size()];
		for (int i = 0; i < cv.size(); i++) {
			String key = (String) cv.keySet().toArray()[i];
			whereClauseItems[i] = key + " = " + android.database.DatabaseUtils.sqlEscapeString(cv.get(key).toString());
		}
		String whereClause = TextUtils.join(" AND ", whereClauseItems);
		Cursor c = db.query(tableName, null, whereClause, null, null, null, null);
		return c.moveToFirst();
	}

	public static boolean relationAlreadyInDatabase(String tableName, ContentValues cv, SQLiteDatabase db) {
		String firstColumn = C.COLUMNS.get(tableName)[0];
		String secondColumn = C.COLUMNS.get(tableName)[1];
		return (! getIDForTableEntry(tableName, "? = ? AND ? = ?", new String[] {firstColumn, cv.getAsString(firstColumn), secondColumn, cv.getAsString(secondColumn)}, db).equals(IDNOTINTABLE));
	}

	public static void insertWithNewID(String tableName, ContentValues cv, SQLiteDatabase db) {
		cv.put(C.COL_ID, DatabaseUtils.getNextIDForTable(tableName, db));
		db.insert(tableName, null, cv);
	}
	
	public static void insertRelationWithNewID(String relationName, ContentValues cv, SQLiteDatabase db) {
		if (! DatabaseUtils.relationAlreadyInDatabase(relationName, cv, db)) {
			insertWithNewID(relationName, cv, db);
		}		
	}
	
	public static byte[] getAlbumArtFor(String albumName, SQLiteDatabase db) {
		String id = getIDForTableEntryByName(C.TAB_ALBUMS, albumName, db);
		String innerSelection = String.format("SELECT %s.%s FROM %s, %s%s AS ta WHERE %s.%s=ta.%s AND ta.%s = %s LIMIT 1", C.TAB_TITLES, C.COL_ID, C.TAB_TITLES, C.TAB_TITLES, C.TAB_ALBUMS, C.TAB_TITLES, C.COL_ID, C.COL_TITLE_ID, C.COL_ALBUM + C.COL__ID, id);
		String selection = String.format("SELECT %s FROM %s JOIN %s ON %s.%s = %s.%s WHERE %s.%s = (%s)", C.COL_IMAGE_BLOB, C.TAB_IMAGE_DATA, C.TAB_IMAGES, C.TAB_IMAGE_DATA, C.COL_IMAGE_HASH, C.TAB_IMAGES, C.COL_IMAGE_HASH, C.TAB_IMAGES, C.COL_TITLE_ID, innerSelection);
		Cursor c = db.rawQuery(selection, null);
		boolean successful = c.moveToFirst();					
		byte[] result = null;
		if (successful) {
			byte[] binaryData = c.getBlob(0).clone();
			result = binaryData;
		}
		c.close();
		return result;
	}
	
	public static String getGiantJoin() {
		String join = C.TAB_TITLES + " ";
		for (String tableName : C.TABLENAMES) {
			String relation = C.getRelationNameFor(tableName);
			String idColumn = C.idNameFrom(tableName) + C.COL__ID;
			join += String.format("JOIN %s ON %s.%s = %s.%s ", relation, C.TAB_TITLES, C.COL_ID, relation, C.COL_TITLE_ID);
			join += String.format("JOIN %s ON %s.%s = %s.%s ", tableName, relation, idColumn, tableName, C.COL_ID);
		}
		return join;
	}

	public static ArrayList<String> getAllPlaylists(SQLiteDatabase db) {
		Cursor c = db.query(C.TAB_PLAYLISTS, new String[] {C.COL_FILE}, null, null, null, null, null);
		c.moveToFirst();
		ArrayList<String> result = new ArrayList<String>();
		while (! c.isAfterLast()) {
			result.add(c.getString(0));
			c.moveToNext();
		}
		c.close();
		return result;
	}
	
	public static String normalize(String path) {
		String result = path;
		try {
			result = new File(result).getCanonicalPath();
		} catch (IOException e) {
		}
		return result;
	}
	
}
