package com.parnswir.unmp.media;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

import android.content.ContentValues;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.core.DatabaseUtils;

public class MP3Handler extends FileHandler {
	
	private long titleID;
	private long[] tableIDs = new long[C.TABLENAMES.length];
	
	@Override
	public void saveToDatabase(File file) {
		MP3File f = readAudioFile(file);
		MediaInformation info = new MediaInformation(f);
		insertTitle(info);
		upsertAttributes(info);
		insertRelations(info);
		insertAlbumArt(info);
	}
	
	@Override
	public void updateDatabaseEntryFor(File file) {

	}

	@Override
	public void deleteFromDatabase(File file) {

	}

	private void insertTitle(MediaInformation info) {
		ContentValues cv = info.toContentValuesForTable(C.TAB_TITLES);
		cv.put(C.COL_LAST_PLAYED, "0");
		cv.put(C.COL_PLAY_COUNT, "0");
		titleID = getDb().insert(C.TAB_TITLES, null, cv);		
	}
	
	private void upsertAttributes(MediaInformation info) {
		for (int i = 0; i < C.TABLENAMES.length; i++) {
			String tableName = C.TABLENAMES[i];
			ContentValues cv = info.toContentValuesForTable(tableName);
			if (DatabaseUtils.tupleAlreadyInTable(tableName, cv, getDb())) {
				getDb().update(tableName, cv, C.COL_ID + " = ?", new String[] {DatabaseUtils.getIDForTuple(tableName, cv, getDb())});
				tableIDs[i] = Integer.valueOf(DatabaseUtils.getIDForTuple(tableName, cv, getDb()));
			} else {
				tableIDs[i] = getDb().insert(tableName, null, cv);
			}
		}
	}
	
	private void insertRelations(MediaInformation info) {
		for (int i = 0; i < C.TABLENAMES.length; i++) {
			String tableName = C.TABLENAMES[i];
			String relationName = C.getRelationNameFor(tableName);
			ContentValues relationCV = generateRelationTuple(i);
			getDb().insert(relationName, null, relationCV);
		}		
	}

	private ContentValues generateRelationTuple(int table) {
		ContentValues relationCV = new ContentValues();
		relationCV.put(C.idNameFrom(C.TABLENAMES[table]) + C.COL__ID, tableIDs[table]);
		relationCV.put(C.COL_TITLE_ID, titleID);
		return relationCV;
	}
	
	private void insertAlbumArt(MediaInformation info) {
		ContentValues titleCV = info.toContentValuesForTable(C.TAB_TITLES);
		String titleID = DatabaseUtils.getIDForTuple(C.TAB_TITLES, titleCV, getDb());
		
		ContentValues cv = info.toContentValuesForTable(C.TAB_IMAGES);
		if (cv.get(C.COL_IMAGE_TYPE) == C.ALBUM_ART_BLOB) {
			cv = info.toContentValuesForTable(C.TAB_IMAGE_DATA);
			cv.remove("image_blob");
			if (! DatabaseUtils.tupleAlreadyInTable(C.TAB_IMAGE_DATA, cv, getDb())) {
				cv = info.toContentValuesForTable(C.TAB_IMAGE_DATA);
				DatabaseUtils.insertWithNewID(C.TAB_IMAGE_DATA, cv, getDb());
			}
		}
		
		cv = info.toContentValuesForTable(C.TAB_IMAGES);
		cv.put(C.COL_TITLE_ID, titleID);
		DatabaseUtils.insertWithNewID(C.TAB_IMAGES, cv, getDb());
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
