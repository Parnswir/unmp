package com.parnswir.unmp;

import java.util.Observable;

public class StringObservable extends Observable {

	public void change(String arg) {
		this.setChanged();
		this.notifyObservers(arg);
	}
	
}
