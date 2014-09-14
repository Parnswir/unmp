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
			return null;
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
		if (getCurrentChild() == null)
			return null;
		return getCurrentChild().getCurrentFile();
	}
	
	@Override
	public void next() {
		String nextFile = null;
		PlaylistElement currentChild = getCurrentChild();
		while (! isFinished() && nextFile == null) {
			if (currentChild != null) {
				currentChild.next();
				nextFile = currentChild.getCurrentFile();
			}
			if (nextFile == null) {
				nextSource();
				if (! isFinished() && getCurrentChild() != null)
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
