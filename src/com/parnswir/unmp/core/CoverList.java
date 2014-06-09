package com.parnswir.unmp.core;

import java.util.ArrayList;

public class CoverList {

	public ArrayList<String> names = new ArrayList<String>();
	public ArrayList<byte[]> images = new ArrayList<byte[]>();
	
	public void clear() {
		names.clear();
		images.clear();
	}
	
}
