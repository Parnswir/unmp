package com.parnswir.unmp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private List<String> folders;
	private List<String> files;
	private boolean stop = false;
	
	public ProgressObservable callback = new ProgressObservable();
	
	
	public FileCrawlerThread(SQLiteDatabase db, String folder) {
		ArrayList<String> wrapper = new ArrayList<String>();
		wrapper.add(folder);
		init(db, wrapper);
	}
	
	public FileCrawlerThread(SQLiteDatabase db, List<String> folders) {
		init(db, folders);
	}
	
	private void init(SQLiteDatabase db, List<String> folders) {
		this.folders = folders;
		this.db = db;
		files = new ArrayList<String>();
	}
	
	public void run() {
		files.clear();
		for (String folderRoot : folders) {
			File root = new File(folderRoot);
			searchForFilesIn(root);
		}
		processFiles();
	}
	
	private void searchForFilesIn(File file) {
		if (stop) return;
		if (file.canRead()) {
			if (file.isDirectory()) {
				searchForFilesInDirectory(file);
			} else {
				addToFileListIfSuitable(file);
			}
		}
	}
	
	private void searchForFilesInDirectory(File file) {
		setProgress("Looking for files in " + file.getAbsolutePath(), 0, 1);
		for (File child : file.listFiles()) {
			searchForFilesIn(child);
		}
	}
	
	private void addToFileListIfSuitable(File file) {
		if (isSuitable(file)) {
			files.add(file.getAbsolutePath());
		}
	}
	
	private boolean isSuitable(File file) {
		return file.getAbsolutePath().endsWith(".mp3") && !stop;
	}
	
	private void setProgress(String text, float value, float count) {
		callback.change(new Resources.ProgressItem(text, value, count));
	}
	
	private void processFiles() {
		for (String filePath : files) {
			File current = new File(filePath);
			if (current != null) {
				setProgress(filePath, files.indexOf(filePath), files.size());
				handleFile(current);
			}
		}
		setProgress("Done.", files.size(), files.size());
	}
	
	private void handleFile(File file) {
		if (stop) return;
		if (! DatabaseUtils.fileAlreadyInDatabase(file.getAbsolutePath(), db)) { 
			saveToDatabase(file);
		} else {
			updateDatabaseEntryFor(file);
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

	public void kill() {
		stop = true;		
	}

}
