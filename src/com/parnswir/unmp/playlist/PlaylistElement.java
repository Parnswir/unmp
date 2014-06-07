package com.parnswir.unmp.playlist;

public abstract class PlaylistElement {

	private boolean repeating;
	private boolean shuffled;
	
	public abstract String getCurrentFile();
	
	public String getNextFile() {
		if (repeating)
			return getCurrentFile();
		return null;
	}

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
	
}
