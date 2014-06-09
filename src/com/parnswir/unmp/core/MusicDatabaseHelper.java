package com.parnswir.unmp.core;

import java.util.Locale;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDatabaseHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "Music";
	
	private static final String CREATETABLESTATEMENT = "CREATE TABLE %s (%s);";
    private static final String CREATEBODYSTATEMENT = String.format(Locale.US, "%s INTEGER NOT NULL, %%s VARCHAR(255), PRIMARY KEY (%s)", C.COL_ID, C.COL_ID);
    private static final String CREATERELATIONSHIPBODY = String.format(Locale.US, "%s INTEGER NOT NULL, %%s%s INTEGER REFERENCES %%s(%s), %s REFERENCES %s(%s), PRIMARY KEY (%s)", C.COL_ID, C.COL__ID, C.COL_ID, C.COL_TITLE_ID, C.TAB_TITLES, C.COL_ID, C.COL_ID);
    

    public MusicDatabaseHelper(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
    	super.onOpen(db);
    	db.execSQL("PRAGMA foreign_keys=ON;");
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTitlesTable(db);
        createImageTables(db);
        createOtherTables(db);
    }

    
	private void createTitlesTable(SQLiteDatabase db) {
    	db.execSQL("CREATE TABLE " + C.TAB_TITLES +
				"(" + C.COL_ID + " INTEGER NOT NULL," +
				C.COL_FILE + " VARCHAR(1024) NOT NULL," +
				C.COL_TITLE + " VARCHAR(255)," +
				C.COL_LENGTH + " INTEGER NOT NULL," + 
				C.COL_RATING + " INTEGER DEFAULT 0," + 
				C.COL_YEAR + " INTEGER," + 
				C.COL_TRACK_NUMBER + " INTEGER," +
				C.COL_BIT_RATE + " INTEGER," + 
				C.COL_PLAY_COUNT + " INTEGER DEFAULT 0 NOT NULL," + 
				C.COL_LAST_PLAYED + " DOUBLE," + 
			"PRIMARY KEY (" + C.COL_ID + "));");		
	}
	
	private void createImageTables(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + C.TAB_IMAGES +
				"(" + C.COL_ID + " INTEGER NOT NULL," +
				C.COL_TITLE_ID + " INTEGER REFERENCES " + C.TAB_TITLES + "(" + C.COL_ID + ")," +
				C.COL_IMAGE_TYPE + " VARCHAR(6) DEFAULT " + C.ALBUM_ART_NONE + " NOT NULL," +
				C.COL_IMAGE_HASH + " VARCHAR(25) REFERENCES " + C.TAB_IMAGE_DATA + "(" + C.COL_IMAGE_HASH + ")," +
			"PRIMARY KEY (" + C.COL_ID + "));");
		
		db.execSQL("CREATE TABLE " + C.TAB_IMAGE_DATA +
				"(" + C.COL_ID + " INTEGER NOT NULL," +
				C.COL_IMAGE_HASH + " INTEGER NOT NULL," + 
				C.COL_IMAGE_BLOB + " BLOB," +				
			"PRIMARY KEY (" + C.COL_IMAGE_HASH + "));");
	}
    
    private void createOtherTables(SQLiteDatabase db) {     
        for (int index = 0; index < C.TABLENAMES.length; index++) {
        	String tableName = C.TABLENAMES[index];
        	createTableCalled(db, tableName);
        	createRelationFromTitleTo(db, tableName);
        }		
	}
    
    private void createTableCalled(SQLiteDatabase db, String tableName) {
		Object[] tableArguments = {tableName, String.format(CREATEBODYSTATEMENT, C.getNameColumnFor(tableName))};
    	String createStatement = String.format(CREATETABLESTATEMENT, tableArguments);
    	db.execSQL(createStatement);
	}

	private void createRelationFromTitleTo(SQLiteDatabase db, String tableName) {
		Object[] bodyArguments = {C.idNameFrom(tableName), tableName, C.idNameFrom(tableName)};
    	Object[] relationArguments = {C.getRelationNameFor(tableName), String.format(CREATERELATIONSHIPBODY, bodyArguments)};
    	String createStatement = String.format(CREATETABLESTATEMENT, relationArguments);
    	db.execSQL(createStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

}
