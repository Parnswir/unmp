package com.parnswir.unmp.playlist;

import android.os.Bundle;

import java.io.Serializable;

public abstract class PlaylistElement implements Serializable {

	private static final long serialVersionUID = -864462965839794668L;
	
	protected boolean repeating;
	protected boolean repeatingAll;
	protected boolean shuffled;
	
	public abstract String getCurrentFile();

	public boolean isRepeating() {
		return repeating;
	}

	public void setRepeating(boolean repeating) {
		this.repeating = repeating;
	}
	
	public boolean isRepeatingAll() {
		return repeatingAll;
	}

	public void setRepeatingAll(boolean repeatingAll) {
		this.repeatingAll = repeatingAll;
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
