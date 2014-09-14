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
	
	public void previousSource() {
		current = current - 1;
	}
	
	public boolean isFinished() {
		return current >= children.size() || current < 0;
	}

	@Override
	public String getCurrentFile() {
		if (getCurrentChild() == null || isFinished())
			return null;
		return getCurrentChild().getCurrentFile();
	}
	
	@Override
	public void next() {
		if (getCurrentChild() != null && getCurrentChild().hasContent()) {
			getCurrentChild().next();
			if (getCurrentChild().getCurrentFile() == null)
				nextSource();
		} else {
			nextSource();
			if (getCurrentChild().getCurrentFile() == null)
				getCurrentChild().next();
		}
	}
	
	@Override
	public void previous() {
		if (getCurrentChild() != null && getCurrentChild().hasContent()) {
			getCurrentChild().previous();
			if (getCurrentChild().getCurrentFile() == null)
				previousSource();
		} else {
			previousSource();
			if (getCurrentChild().getCurrentFile() == null)
				getCurrentChild().previous();
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

	@Override
	public boolean hasContent() {
		return true;
	}

	@Override
	public void reset() {
		current = 0;
		for (PlaylistElement child : children) {
			child.reset();
		}
	}
	
}
