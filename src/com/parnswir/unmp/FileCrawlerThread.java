package com.parnswir.unmp;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class FileCrawlerThread extends Thread {
	
	private SQLiteDatabase db;
	private String folder;
	
	public StringObservable callback = new StringObservable();
	
	
	public FileCrawlerThread(SQLiteDatabase db, String folder) {
		this.folder = folder;
		this.db = db;
	}
	
	public void run() {
		File root = new File(folder);
		traverse(root);
	}
	
	protected void traverse(File file) {
		if (file.canRead()) {
			if (file.isDirectory()) {
				callback.change(file.getAbsolutePath());
				File[] fileList = file.listFiles();
				for (int i = 0; i < fileList.length; i++) {
					File aFile = fileList[i];
					traverse(aFile);
				}
			} else {
				if (file != null) {
					String path = file.getAbsolutePath();
					if (path != null)
						if (path.endsWith(".mp3")) 
							if (! DatabaseUtils.fileAlreadyInDatabase(path, db)) { 
								saveToDatabase(file);
							} else {
								updateDatabaseEntryFor(file);
							}
				}
			}
		}
	}

	private void updateDatabaseEntryFor(File file) {
				
	}

	private void saveToDatabase(File file) {
		MP3File f = readAudioFile(file);
		MediaInformation info = new MediaInformation(f);
		insertTitle(info);
		upsertAttributes(info);
		insertRelations(info);
		insertAlbumArt(info);
	}

	private void insertTitle(MediaInformation info) {
		ContentValues cv = info.toContentValuesForTable(C.TAB_TITLES);
		cv.put(C.COL_ID, DatabaseUtils.getNextIDForTable(C.TAB_TITLES, db));
		cv.put(C.COL_LAST_PLAYED, "0");
		cv.put(C.COL_PLAY_COUNT, "0");
		db.insert(C.TAB_TITLES, null, cv);		
	}
	
	private void upsertAttributes(MediaInformation info) {
		for (int i = 0; i < C.TABLENAMES.length; i++) {
			String tableName = C.TABLENAMES[i];
			ContentValues cv = info.toContentValuesForTable(tableName);
			if (DatabaseUtils.tupleAlreadyInTable(tableName, cv, db)) {
				db.update(tableName, cv, C.COL_ID + " = ?", new String[] {DatabaseUtils.getIDForTuple(tableName, cv, db)});
			} else {
				DatabaseUtils.insertWithNewID(tableName, cv, db);
			}
		}
	}
	
	private void insertRelations(MediaInformation info) {
		ContentValues titleCV = info.toContentValuesForTable(C.TAB_TITLES);
		for (int i = 0; i < C.TABLENAMES.length; i++) {
			String tableName = C.TABLENAMES[i];
			String relationName = C.getRelationNameFor(tableName);
			ContentValues relationCV = generateRelationTuple(info, titleCV, tableName);
			DatabaseUtils.insertRelationWithNewID(relationName, relationCV, db);
		}		
	}

	private ContentValues generateRelationTuple(MediaInformation info, ContentValues titleCV, String tableName) {
		ContentValues cv = info.toContentValuesForTable(tableName);
		ContentValues relationCV = new ContentValues();
		relationCV.put(C.idNameFrom(tableName) + C.COL__ID, DatabaseUtils.getIDForTableEntryByName(tableName, cv.getAsString(C.idNameFrom(tableName)), db));
		relationCV.put(C.COL_TITLE_ID, DatabaseUtils.getIDForTuple(C.TAB_TITLES, titleCV, db));
		return relationCV;
	}
	
	private void insertAlbumArt(MediaInformation info) {
		ContentValues titleCV = info.toContentValuesForTable(C.TAB_TITLES);
		String titleID = DatabaseUtils.getIDForTuple(C.TAB_TITLES, titleCV, db);
		
		ContentValues cv = info.toContentValuesForTable(C.TAB_IMAGES);
		if (cv.get(C.COL_IMAGE_TYPE) == C.ALBUM_ART_BLOB) {
			cv = info.toContentValuesForTable(C.TAB_IMAGE_DATA);
			cv.remove("image_blob");
			if (! DatabaseUtils.tupleAlreadyInTable(C.TAB_IMAGE_DATA, cv, db)) {
				cv = info.toContentValuesForTable(C.TAB_IMAGE_DATA);
				DatabaseUtils.insertWithNewID(C.TAB_IMAGE_DATA, cv, db);
			}
		}
		
		cv = info.toContentValuesForTable(C.TAB_IMAGES);
		cv.put(C.COL_TITLE_ID, titleID);
		DatabaseUtils.insertWithNewID(C.TAB_IMAGES, cv, db);
	}

	private MP3File readAudioFile(File file) {
		MP3File f = null;
		try {
			f = (MP3File)AudioFileIO.read(file);
		} catch (CannotReadException e) {
			
		} catch (IOException e) {
			
		} catch (TagException e) {
			
		} catch (ReadOnlyFileException e) {
			
		} catch (InvalidAudioFrameException e) {
			
		}
		return f;
	}

}
