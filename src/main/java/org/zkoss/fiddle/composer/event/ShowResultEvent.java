package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.zk.ui.event.Event;

public class ShowResultEvent extends Event {

	private ICase $case;

	private FiddleSandbox instance;

	public ShowResultEvent(String evtName,ICase _case,FiddleSandbox inst) {
		super(evtName);
		this.instance = inst;
		this.$case= _case;
	}


	public FiddleSandbox getInstance() {
		return instance;
	}

	public void setInstance(FiddleSandbox instance) {
		this.instance = instance;
	}
	
	public ICase getCase() {
		return $case;
	}

	
	public void setCase(ICase $case) {
		this.$case = $case;
	}
}
