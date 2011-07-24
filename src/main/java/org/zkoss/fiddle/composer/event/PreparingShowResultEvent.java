package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.zk.ui.event.Event;

public class PreparingShowResultEvent extends Event {

	/**
	 *
	 */
	private static final long serialVersionUID = 8416621801805204156L;

	private FiddleSandbox sandbox;

	public PreparingShowResultEvent(FiddleSandbox inst) {
		super(FiddleEvents.ON_PREPARING_SHOW_RESULT);
		this.sandbox = inst;
	}

	public FiddleSandbox getSandbox() {
		return sandbox;
	}

	public void setSandbox(FiddleSandbox sandbox) {
		this.sandbox = sandbox;
	}

}
