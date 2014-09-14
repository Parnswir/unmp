package com.parnswir.unmp.playlist;

public class MediaFile extends PlaylistElement {

	private static final long serialVersionUID = 1142236402765430040L;
	
	private String path;
	private String currentFile;
	
	
	public MediaFile(String path) {
		super();
		this.path = path;
		currentFile = path;
	}

	@Override
	public String getCurrentFile() {
		return currentFile;
	}
	
	public boolean equals(MediaFile e) {
		return e.getCurrentFile() == path;
	}
	
	@Override
	public void next() {
		if (currentFile == null) {
			reset();
		} else if (! repeating)
			currentFile = null;
	}

	@Override
	public void previous() {
		next();
	}

	@Override
	public boolean hasContent() {
		return false;
	}

	@Override
	public void reset() {
		currentFile = path;
	}

}
