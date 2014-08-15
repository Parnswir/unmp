package com.parnswir.unmp.core;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jaudiotagger.tag.FieldKey;

import com.parnswir.unmp.R;
import com.parnswir.unmp.core.ProjectResources.FragmentProperties;

public class C {

	public static final String TAB_TITLES = "Titles";
	public static final String TAB_IMAGES = "Images";
	public static final String TAB_IMAGE_DATA = "ImageData";
	public static final String TAB_ALBUMS = "Albums";
	public static final String TAB_ARTISTS = "Artists";
	public static final String TAB_GENRES = "Genres";
	public static final String TAB_COMPOSERS = "Composers";
	public static final String TAB_PUBLISHERS = "Publishers";
	public static final String TAB_PLAYLISTS = "Playlists";
	
	public static final String[] TABLENAMES = {TAB_GENRES, TAB_PUBLISHERS, TAB_ARTISTS, TAB_COMPOSERS, TAB_ALBUMS};
	
	public static final String COL_ID = "ID";
	public static final String COL_FILE = "file";
	public static final String COL_TITLE = "title";
	public static final String COL_LENGTH = "length";
	public static final String COL_RATING = "rating";
	public static final String COL_YEAR = "year";
	public static final String COL_TRACK_NUMBER = "track_number";
	public static final String COL_BIT_RATE = "bit_rate";
	public static final String COL_PLAY_COUNT = "play_count";
	public static final String COL_LAST_PLAYED = "last_played";
	public static final String COL_ALBUM = "album";
	public static final String COL_ARTIST = "artist";
	public static final String COL_GENRE = "genre";
	public static final String COL_COMPOSER = "composer";
	public static final String COL_PUBLISHER = "publisher";
	public static final String COL_IMAGE_TYPE = "image_type";
	public static final String COL_IMAGE_BLOB = "image_blob";
	public static final String COL_IMAGE_HASH = "image_hash";
	
	public static final String COL__ID = "_id";
	public static final String COL_TITLE_ID = COL_TITLE + COL__ID; 
	
	public static final String[] FIELDNAMES = {COL_TITLE, COL_RATING, COL_YEAR, COL_TRACK_NUMBER, COL_ALBUM, COL_ARTIST, COL_COMPOSER, COL_PUBLISHER, COL_GENRE, COL_LENGTH, COL_FILE, COL_BIT_RATE};
	public static final FieldKey[] FIELDS = {FieldKey.TITLE, FieldKey.RATING, FieldKey.YEAR, FieldKey.TRACK, FieldKey.ALBUM, FieldKey.ARTIST, FieldKey.COMPOSER, FieldKey.RECORD_LABEL, FieldKey.GENRE}; 
	
	public static final String ALBUM_ART_NONE = "NONE";
	public static final String ALBUM_ART_BLOB = "BLOB";
	public static final String ALBUM_ART_FOLDER = "FOLDER";
	
	public static final String NUMBEROFFOLDERS = "_nofil";
	public static final String FOLDER = "_f";
	
	public static final String NUMBEROFPLAYLISTS = "_nop";
	public static final String PLAYLIST = "_p";
	
	public static final FragmentProperties[] FRAGMENTS = {
		new FragmentProperties("Now Playing", R.layout.activity_main),
		new FragmentProperties("Playlist"),
		new FragmentProperties("Artists"),
		new FragmentProperties("Albums"),
		new FragmentProperties("Genres"),
		new FragmentProperties("Playlists", R.layout.drawer_layout),
		new FragmentProperties("Manage Library...", R.layout.library_fragment),
		new FragmentProperties("Settings")
	};
	
	public static final String[] LIST_FRAGMENT_TABLENAMES = {TAB_ARTISTS, TAB_ALBUMS, TAB_GENRES};
	
	public static final Map<String, String[]> COLUMNS = new HashMap<String, String[]>();
	static{
		 COLUMNS.put(TAB_TITLES, new String[] {COL_TITLE, COL_RATING, COL_YEAR, COL_TRACK_NUMBER, COL_LENGTH, COL_FILE, COL_BIT_RATE});
		 COLUMNS.put(TAB_ALBUMS, new String[] {COL_ALBUM});
		 COLUMNS.put(TAB_ARTISTS, new String[] {COL_ARTIST});
		 COLUMNS.put(TAB_GENRES, new String[] {COL_GENRE});
		 COLUMNS.put(TAB_COMPOSERS, new String[] {COL_COMPOSER});
		 COLUMNS.put(TAB_PUBLISHERS, new String[] {COL_PUBLISHER});

		 COLUMNS.put(TAB_IMAGES, new String[] {COL_IMAGE_TYPE, COL_IMAGE_HASH});
		 COLUMNS.put(TAB_IMAGE_DATA, new String[] {COL_IMAGE_HASH, COL_IMAGE_BLOB});
		 
		 for (int i = 0; i < TABLENAMES.length; i++) {
			String relationName = getRelationNameFor(TABLENAMES[i]);
			COLUMNS.put(relationName, new String[] {COL_TITLE_ID, idNameFrom(TABLENAMES[i]) + COL__ID});
		}
	}

	public static final Map<String, String> GENRE_IDS = new HashMap<String, String>();
	static{
		GENRE_IDS.put("0", "Blues");
		GENRE_IDS.put("1", "Classic Rock");
		GENRE_IDS.put("2", "Country");
		GENRE_IDS.put("3", "Dance");
		GENRE_IDS.put("4", "Disco");
		GENRE_IDS.put("5", "Funk");
		GENRE_IDS.put("6", "Grunge");
		GENRE_IDS.put("7", "Hip-Hop");
		GENRE_IDS.put("8", "Jazz");
		GENRE_IDS.put("9", "Metal");
		GENRE_IDS.put("10", "New Age");
		GENRE_IDS.put("11", "Oldies");
		GENRE_IDS.put("12", "Other");
		GENRE_IDS.put("13", "Pop");
		GENRE_IDS.put("14", "R&B");
		GENRE_IDS.put("15", "Rap");
		GENRE_IDS.put("16", "Reggae");
		GENRE_IDS.put("17", "Rock");
		GENRE_IDS.put("18", "Techno");
		GENRE_IDS.put("19", "Industrial");
		GENRE_IDS.put("20", "Alternative");
		GENRE_IDS.put("21", "Ska");
		GENRE_IDS.put("22", "Death Metal");
		GENRE_IDS.put("23", "Pranks");
		GENRE_IDS.put("24", "Soundtrack");
		GENRE_IDS.put("25", "Euro-Techno");
		GENRE_IDS.put("26", "Ambient");
		GENRE_IDS.put("27", "Trip-Hop");
		GENRE_IDS.put("28", "Vocal");
		GENRE_IDS.put("29", "Jazz+Funk");
		GENRE_IDS.put("30", "Fusion");
		GENRE_IDS.put("31", "Trance");
		GENRE_IDS.put("32", "Classical");
		GENRE_IDS.put("33", "Instrumental");
		GENRE_IDS.put("34", "Acid");
		GENRE_IDS.put("35", "House");
		GENRE_IDS.put("36", "Game");
		GENRE_IDS.put("37", "Sound Clip");
		GENRE_IDS.put("38", "Gospel");
		GENRE_IDS.put("39", "Noise");
		GENRE_IDS.put("40", "Alternative Rock");
		GENRE_IDS.put("41", "Bass");
		GENRE_IDS.put("42", "Soul");
		GENRE_IDS.put("43", "Punk");
		GENRE_IDS.put("44", "Space");
		GENRE_IDS.put("45", "Meditative");
		GENRE_IDS.put("46", "Instrumental Pop");
		GENRE_IDS.put("47", "Instrumental Rock");
		GENRE_IDS.put("48", "Ethnic");
		GENRE_IDS.put("49", "Gothic");
		GENRE_IDS.put("50", "Darkwave");
		GENRE_IDS.put("51", "Techno-Industrial");
		GENRE_IDS.put("52", "Electronic");
		GENRE_IDS.put("53", "Pop-Folk");
		GENRE_IDS.put("54", "Eurodance");
		GENRE_IDS.put("55", "Dream");
		GENRE_IDS.put("56", "Southern Rock");
		GENRE_IDS.put("57", "Comedy");
		GENRE_IDS.put("58", "Cult");
		GENRE_IDS.put("59", "Gangsta");
		GENRE_IDS.put("60", "Top4");
		GENRE_IDS.put("61", "Christian Rap");
		GENRE_IDS.put("62", "Pop/Funk");
		GENRE_IDS.put("63", "Jungle");
		GENRE_IDS.put("64", "Native US");
		GENRE_IDS.put("65", "Cabaret");
		GENRE_IDS.put("66", "New Wave");
		GENRE_IDS.put("67", "Psychadelic");
		GENRE_IDS.put("68", "Rave");
		GENRE_IDS.put("69", "Showtunes");
		GENRE_IDS.put("70", "Trailer");
		GENRE_IDS.put("71", "Lo-Fi");
		GENRE_IDS.put("72", "Tribal");
		GENRE_IDS.put("73", "Acid Punk");
		GENRE_IDS.put("74", "Acid Jazz");
		GENRE_IDS.put("75", "Polka");
		GENRE_IDS.put("76", "Retro");
		GENRE_IDS.put("77", "Musical");
		GENRE_IDS.put("78", "Rock & Roll");
		GENRE_IDS.put("79", "Hard Rock");
		GENRE_IDS.put("80", "Folk");
		GENRE_IDS.put("81", "Folk-Rock");
		GENRE_IDS.put("82", "National Folk");
		GENRE_IDS.put("83", "Swing");
		GENRE_IDS.put("84", "Fast Fusion");
		GENRE_IDS.put("85", "Bebob");
		GENRE_IDS.put("86", "Latin");
		GENRE_IDS.put("87", "Revival");
		GENRE_IDS.put("88", "Celtic");
		GENRE_IDS.put("89", "Bluegrass");
		GENRE_IDS.put("90", "Avantgarde");
		GENRE_IDS.put("91", "Gothic Rock");
		GENRE_IDS.put("92", "Progressive Rock");
		GENRE_IDS.put("93", "Psychedelic Rock");
		GENRE_IDS.put("94", "Symphonic Rock");
		GENRE_IDS.put("95", "Slow Rock");
		GENRE_IDS.put("96", "Big Band");
		GENRE_IDS.put("97", "Chorus");
		GENRE_IDS.put("98", "Easy Listening");
		GENRE_IDS.put("99", "Acoustic");
		GENRE_IDS.put("100", "Humour");
		GENRE_IDS.put("101", "Speech");
		GENRE_IDS.put("102", "Chanson");
		GENRE_IDS.put("103", "Opera");
		GENRE_IDS.put("104", "Chamber Music");
		GENRE_IDS.put("105", "Sonata");
		GENRE_IDS.put("106", "Symphony");
		GENRE_IDS.put("107", "Booty Bass");
		GENRE_IDS.put("108", "Primus");
		GENRE_IDS.put("109", "Porn Groove");
		GENRE_IDS.put("110", "Satire");
		GENRE_IDS.put("111", "Slow Jam");
		GENRE_IDS.put("112", "Club");
		GENRE_IDS.put("113", "Tango");
		GENRE_IDS.put("114", "Samba");
		GENRE_IDS.put("115", "Folklore");
		GENRE_IDS.put("116", "Ballad");
		GENRE_IDS.put("117", "Power Ballad");
		GENRE_IDS.put("118", "Rhythmic Soul");
		GENRE_IDS.put("119", "Freestyle");
		GENRE_IDS.put("120", "Duet");
		GENRE_IDS.put("121", "Punk Rock");
		GENRE_IDS.put("122", "Drum Solo");
		GENRE_IDS.put("123", "Acapella");
		GENRE_IDS.put("124", "Euro-House");
		GENRE_IDS.put("125", "Dance Hall");
		GENRE_IDS.put("126", "Goa");
		GENRE_IDS.put("127", "Drum & Bass");
		GENRE_IDS.put("128", "Clu House");
		GENRE_IDS.put("129", "Hardcore");
		GENRE_IDS.put("130", "Terror");
		GENRE_IDS.put("131", "Indie");
		GENRE_IDS.put("132", "BritPop");
		GENRE_IDS.put("133", "Negerpunk");
		GENRE_IDS.put("134", "Polsk Punk");
		GENRE_IDS.put("135", "Beat");
		GENRE_IDS.put("136", "Christian Gangsta Rap");
		GENRE_IDS.put("137", "Heavy Metal");
		GENRE_IDS.put("138", "Black Metal");
		GENRE_IDS.put("139", "Crossover");
		GENRE_IDS.put("140", "Contemporary Christian");
		GENRE_IDS.put("141", "Christian Rock");
		GENRE_IDS.put("142", "Merengue");
		GENRE_IDS.put("143", "Salsa");
		GENRE_IDS.put("144", "Thrash Metal");
		GENRE_IDS.put("145", "Anime");
		GENRE_IDS.put("146", "JPop");
		GENRE_IDS.put("147", "Synthpop");
		GENRE_IDS.put("148", "Unknown");
	}
	
	
	public static String getNameColumnFor(String tableName) {
		return COLUMNS.get(tableName)[0];
	}
	
	public static String getRelationNameFor(String tableName) {
		return TAB_TITLES + tableName;
	}
	
	public static String idNameFrom(String tableName) {
		String result = tableName.toLowerCase(Locale.US);
		return result.substring(0, result.length() - 1);
	}
}
