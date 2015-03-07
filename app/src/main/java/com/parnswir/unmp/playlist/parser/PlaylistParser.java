package com.parnswir.unmp.playlist.parser;

import android.database.sqlite.SQLiteDatabase;

import com.parnswir.unmp.core.Utils;
import com.parnswir.unmp.playlist.Playlist;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PlaylistParser {
	
	private static final Map<String, Class<?>> FILE_HANDLERS = new HashMap<String, Class<?>>();
	static{
		FILE_HANDLERS.put("wpl", WPLParser.class);
	}
	
	protected String fileName;
	protected String directory;
	protected Playlist playlist;
	protected SQLiteDatabase database;
	
	private static PlaylistParser getPlaylistHandlerFor(File file, SQLiteDatabase db) {
		PlaylistParser result = new PlaylistParser();
		String extension = Utils.getFileExt(file.getName());
		if (FILE_HANDLERS.containsKey(extension)) {
			try {
				result = (PlaylistParser) FILE_HANDLERS.get(extension).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static Playlist parse(String path, SQLiteDatabase db) throws PlaylistParserException {
		PlaylistParser parser = getPlaylistHandlerFor(new File(path), db);
		parser.init(path, db);
		return parser.buildPlaylist();
	}
	
	public void init(String path, SQLiteDatabase db)  {
		File file = new File(path);
		database = db;
		fileName = file.getAbsolutePath();
		directory = file.getParentFile().getAbsolutePath() + "/";
		playlist = new Playlist();
		playlist.setName(file.getName().replace("" + Utils.getFileExt(fileName), ""));
	}
	
	public Playlist buildPlaylist() throws PlaylistParserException {
		return new Playlist();
	}
	
    public String getName() {
    	return playlist.getName();
    }

	public static class PlaylistParserException extends Exception {

		private static final long serialVersionUID = 4328611493780253444L;

		public PlaylistParserException(String message) {
			super(message);
		}
		
	}	
}
