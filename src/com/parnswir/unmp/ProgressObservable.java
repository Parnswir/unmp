package com.parnswir.unmp;

import java.util.Observable;

public class ProgressObservable extends Observable {

	public void change(ProjectResources.ProgressItem arg) {
		this.setChanged();
		this.notifyObservers(arg);
	}
	
}
