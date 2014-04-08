package com.parnswir.unmp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class DatabaseUtils {
	
	public static final String IDNOTINTABLE = "";
	
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
		return (! getIDForTableEntry(C.TAB_TITLES, C.COL_FILE + " = ?", new String[] {absolutePath}, db).equals(IDNOTINTABLE));
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
	
}
