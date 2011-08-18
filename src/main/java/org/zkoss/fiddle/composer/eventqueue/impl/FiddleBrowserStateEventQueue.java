package org.zkoss.fiddle.composer.eventqueue.impl;

import org.zkoss.fiddle.composer.event.URLChangeEvent;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventQueue;

public class FiddleBrowserStateEventQueue extends FiddleEventQueue{


	private static final String QUEUE_NAME = "e_browserState";

	public static FiddleBrowserStateEventQueue lookup() {
		return new FiddleBrowserStateEventQueue();
	}

	public FiddleBrowserStateEventQueue() {
		super(QUEUE_NAME);
	}

	public void fireStateChange(String url, Object data) {
		publish(new URLChangeEvent(url, data));
	}
}
