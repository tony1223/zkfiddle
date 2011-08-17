package org.zkoss.fiddle.composer.eventqueue;

import org.zkoss.fiddle.composer.event.URLChangeEvent;
import org.zkoss.zk.ui.event.Event;

public class FiddleBrowserStateEventQueue {

	private FiddleEventQueue queue;

	private static final String QUEUE_NAME = "e_browserState";

	public static FiddleBrowserStateEventQueue lookup() {
		return new FiddleBrowserStateEventQueue();
	}

	public FiddleBrowserStateEventQueue() {
		queue = new FiddleEventQueue(QUEUE_NAME);
	}

	public void subscribe(FiddleEventListener<? extends Event> evtListener) {
		queue.subscribe(evtListener);
	}

	public void fireStateChange(String url, Object data) {
		queue.publish(new URLChangeEvent(url, data));
	}
}
