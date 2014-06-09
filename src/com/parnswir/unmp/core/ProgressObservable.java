package com.parnswir.unmp.core;

import java.util.Observable;


public class ProgressObservable extends Observable {

	public void change(ProjectResources.ProgressItem arg) {
		this.setChanged();
		this.notifyObservers(arg);
	}
	
}
