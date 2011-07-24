package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.zk.ui.event.Event;

public class ShowResultEvent extends Event {

	/**
	 *
	 */
	private static final long serialVersionUID = 8416621801805204156L;

	private ICase $case;

	private FiddleSandbox sandbox;

	public ShowResultEvent(ICase _case,FiddleSandbox inst) {
		super(FiddleEvents.ON_SHOW_RESULT);
		this.sandbox = inst;
		this.$case= _case;
	}


	public FiddleSandbox getSandbox() {
		return sandbox;
	}

	public void setSandbox(FiddleSandbox pSandbox) {
		this.sandbox = pSandbox;
	}

	public ICase getCase() {
		return $case;
	}


	public void setCase(ICase $case) {
		this.$case = $case;
	}
}
