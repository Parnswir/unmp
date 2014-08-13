package com.parnswir.unmp;


public class DrawerStateHelper {
	
	private static DrawerState state = null;
	
	public static DrawerState getDrawerState() {
		if (state == null)
			state = new DrawerState();
		return state;
	}
	
}
