package com.parnswir.unmp.media;

import android.content.ContentValues;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.core.DatabaseUtils;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaInformation {
	
	private HashMap<String, Object> extractedTags;
	private static Pattern genrePattern = Pattern.compile("(\\d+)");
	
	public MediaInformation(MP3File f) {
		Tag tag = f.getTag();
		extractedTags = new HashMap<String, Object>();
		
		extractBasicInfo(tag);		
		extractArtwork(tag);
		extractFileInfo(f);
	}

	private void extractBasicInfo(Tag tag) {
		for (int i = 0; i < C.FIELDS.length; i++) { 
			String name = C.FIELDNAMES[i];
			FieldKey key = C.FIELDS[i];
			
			String extractedValue = tag.getFirst(key);
			extractedTags.put(name, extractedValue);
		}
		if (extractedTags.containsKey(C.COL_GENRE))
			correctGenre();
		if (extractedTags.containsKey(C.COL_RATING))
			correctRating();
	}

	private void extractArtwork(Tag tag) {
		Artwork artwork = tag.getFirstArtwork();
		String artType = C.ALBUM_ART_FOLDER;
		if (artwork != null) {
			artType = C.ALBUM_ART_BLOB;
			extractedTags.put(C.COL_IMAGE_BLOB, artwork.getBinaryData());
			extractedTags.put(C.COL_IMAGE_HASH, Integer.toString(new BigInteger(artwork.getBinaryData()).hashCode()));
		}
		extractedTags.put(C.COL_IMAGE_TYPE, artType);
	}
	
	private void extractFileInfo(MP3File f) {
		extractedTags.put(C.COL_LENGTH, Integer.toString(f.getAudioHeader().getTrackLength()));
		extractedTags.put(C.COL_BIT_RATE, f.getAudioHeader().getBitRate());
		extractedTags.put(C.COL_FILE, DatabaseUtils.normalize(f.getFile().getAbsolutePath()));
	}
	
	private void correctGenre() {
		Matcher m = genrePattern.matcher((String) extractedTags.get(C.COL_GENRE));
		if (m.find()) {
			extractedTags.put(C.COL_GENRE, C.GENRE_IDS.get(m.group(1)));
		}
	}
	
	private void correctRating() {
		int rating = 0;
		try {
			rating = Integer.decode((String) extractedTags.get(C.COL_RATING));
			if (rating > 0)
				rating = (rating + 1) / 32 + 2; // 255 = 5 stars, 192 = 4 stars, ...
			
		} catch (NumberFormatException e) {
		} finally {
			extractedTags.put(C.COL_RATING, Integer.toString(rating));
		}
	}

	public ContentValues toContentValuesForTable(String tableName) {
		ContentValues result = new ContentValues();
		String[] columnNames = C.COLUMNS.get(tableName);
		for (int i = 0; i < columnNames.length; i++) {
			String name = columnNames[i];
			Object tag = extractedTags.get(name);
			if (name.equals(C.COL_IMAGE_BLOB)) {
				result.put(name, (byte[]) tag);
			} else {
				result.put(name, (String) tag);
			}
		}
		return result;
	}

}
