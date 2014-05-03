package com.parnswir.unmp;

import java.io.Serializable;

public class MediaPlayerStatus implements Serializable {
	
	private static final long serialVersionUID = -3086458695042317582L;
	
	public boolean 
		paused = false, 
		shuffled = false, 
		repeated = false;

	public MediaPlayerStatus() {
		super();
	}
	
}
