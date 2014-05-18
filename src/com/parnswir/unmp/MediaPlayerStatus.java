package com.parnswir.unmp;

import java.io.Serializable;

public class MediaPlayerStatus implements Serializable {
	
	private static final long serialVersionUID = -3086458695042317582L;
	
	public boolean 
		stopped = true,
		paused = false, 
		shuffled = false, 
		repeated = false;
	
	public int
		length = 0,
		position = 0;

	public MediaPlayerStatus() {
		super();
	}
	
}