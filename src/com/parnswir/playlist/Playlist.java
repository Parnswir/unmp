package com.parnswir.playlist;

import java.util.ArrayList;


public class Playlist extends PlaylistElement {
	
	private int current;
	public ArrayList<PlaylistElement> children = new ArrayList<PlaylistElement>();
	
	
	public int getPosition() {
		return current;
	}
	
	public void setPosition(int position) {
		if (position < children.size() && position > -1)
			current = position;
	}

	public PlaylistElement getCurrentChild() {
		if (isFinished())
			throw new IndexOutOfBoundsException("Playlist is finished.");
		
		return children.get(current);
	}
	
	public void nextSource() {
		current = current + 1;
	}
	
	public boolean isFinished() {
		return current >= children.size();
	}
	
	public void start() {
		current = 0;
	}

	@Override
	public String getCurrentFile() {
		return getCurrentChild().getCurrentFile();
	}
	
	@Override
	public String getNextFile() {
		String nextFile = super.getNextFile();
		while (! isFinished() && nextFile == null) {
			nextFile = getCurrentChild().getNextFile();
			if (nextFile == null)
				nextSource();
		}
		return nextFile;
	}

	@Override
	public void setRepeating(boolean repeating) {
		super.setRepeating(repeating);
		for (PlaylistElement child : children) {
			child.setRepeating(repeating);
		}
	}
	
	@Override
	public void setShuffled(boolean shuffled) {
		super.setShuffled(shuffled);
		for (PlaylistElement child : children) {
			child.setShuffled(shuffled);
		}
	}
	
}
