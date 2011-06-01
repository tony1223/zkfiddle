package org.zkoss.fiddle.composer.event;

import org.zkoss.zk.ui.event.Event;


public class SaveEvent extends Event{

	private boolean fork = false;
	
	public SaveEvent(String name,boolean fork) {
		super(name);
		this.fork = fork;
	}

	public SaveEvent(boolean fork) {
		this(FiddleEvents.ON_SAVE_CASE,fork);
	}
	
	public SaveEvent() {
		this(false);
	}
	
	public boolean isFork() {
		return fork;
	}

	public void setFork(boolean fork) {
		this.fork = fork;
	}
}
