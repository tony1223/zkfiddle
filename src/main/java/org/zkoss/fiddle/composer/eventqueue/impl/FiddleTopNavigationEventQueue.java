package org.zkoss.fiddle.composer.eventqueue.impl;

import org.zkoss.fiddle.composer.TopNavigationComposer;
import org.zkoss.fiddle.composer.event.TopStateChangeEvent;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventQueue;

public class FiddleTopNavigationEventQueue extends FiddleEventQueue{


	private static final String QUEUE_NAME = "e_topnav";

	public static FiddleTopNavigationEventQueue lookup() {
		return new FiddleTopNavigationEventQueue();
	}

	public FiddleTopNavigationEventQueue() {
		super(QUEUE_NAME);
	}

	public void fireStateChange(TopNavigationComposer.State state) {
		publish(new TopStateChangeEvent(state));
	}
}
