package com.parnswir.unmp.playlist;

import java.util.ArrayList;


public class Playlist extends PlaylistElement {
	
	private static final long serialVersionUID = 3196172024840309061L;
	
	private String name;
	private int current;
	public ArrayList<PlaylistElement> children = new ArrayList<PlaylistElement>();
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
	public void next() {
		String nextFile = null;
		while (! isFinished() && nextFile == null) {
			getCurrentChild().next();
			nextFile = getCurrentChild().getCurrentFile();
			if (nextFile == null) {
				nextSource();
				if (! isFinished())
					nextFile = getCurrentChild().getCurrentFile();
			}
		}
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
