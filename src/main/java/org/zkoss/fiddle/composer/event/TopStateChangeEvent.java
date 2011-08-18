package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.composer.TopNavigationComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

public class TopStateChangeEvent extends Event {

	/**
	 *
	 */
	private static final long serialVersionUID = -305405869025740418L;

	private TopNavigationComposer.State state;

	public TopStateChangeEvent(TopNavigationComposer.State state) {
		super(FiddleEvents.ON_STATE_CHANGE, (Component) null, null);
		this.state = state;
	}

	public TopNavigationComposer.State getState() {
		return this.state;
	}
}
