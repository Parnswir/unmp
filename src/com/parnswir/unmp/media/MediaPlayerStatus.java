package com.parnswir.unmp.media;

import java.io.Serializable;

public class MediaPlayerStatus implements Serializable {
	
	private static final long serialVersionUID = -3086458695042317582L;
	
	public static final int DO_NOT_REPEAT = 0, REPEAT_ALL = 1, REPEAT_ONE = 2;
	
	public boolean 
		stopped = true,
		playing = false,
		paused = false, 
		shuffled = true;
	
	public int
		length = 0,
		position = 0,
		repeatMode = REPEAT_ALL,
		rating = 0;
	
	public String 
		file = "",
		title = "",
		album = "",
		artist = "",
		publisher = "",
		composer = "",
		year = "";
	
	public byte[] cover = null;

	public MediaPlayerStatus() {
		super();
	}
	
}
