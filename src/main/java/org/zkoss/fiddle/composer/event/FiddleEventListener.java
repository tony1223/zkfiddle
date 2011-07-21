package org.zkoss.fiddle.composer.event;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

public abstract class FiddleEventListener<T extends Event> implements EventListener {

	private Class accpeted;

	/**
	 * Class inp must match with T .
	 * 
	 * @param inp
	 */
	public FiddleEventListener(Class inp) {
		accpeted = inp;
	}

	public void onEvent(Event event) throws Exception {
		if (accpeted.isInstance(event)) {
			onFiddleEvent((T) event);
		}
	}

	public abstract void onFiddleEvent (T evt) throws Exception ;
}
