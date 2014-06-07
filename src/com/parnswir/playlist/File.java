package com.parnswir.playlist;

public class File extends PlaylistElement {

	private String path;
	
	
	public File(String path) {
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

}
