package com.parnswir.unmp.playlist;

import java.io.Serializable;

import android.os.Bundle;

public abstract class PlaylistElement implements Serializable {

	private static final long serialVersionUID = -864462965839794668L;
	
	protected boolean repeating;
	protected boolean shuffled;
	
	public abstract String getCurrentFile();
	public abstract void next();

	public boolean isRepeating() {
		return repeating;
	}

	public void setRepeating(boolean repeating) {
		this.repeating = repeating;
	}

	public boolean isShuffled() {
		return shuffled;
	}

	public void setShuffled(boolean shuffled) {
		this.shuffled = shuffled;
	}
	
	public Bundle getBundled(String key) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(key, this);
		return bundle;
	}
	
}
