package com.parnswir.unmp.playlist;

public class MediaFile extends PlaylistElement {

	private String path;
	
	
	public MediaFile(String path) {
		super();
		this.path = path;
	}
	
	@Override
	public String getNextFile() {
		return super.getNextFile();
	}

	@Override
	public String getCurrentFile() {
		return path;
	}
	
	public boolean equals(MediaFile e) {
		return e.getCurrentFile() == path;
	}

}
