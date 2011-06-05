package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.model.FiddleInstance;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.zk.ui.event.Event;

public class ShowResultEvent extends Event {

	private ICase $case;

	private FiddleInstance instance;

	public ShowResultEvent(String evtName,ICase _case,FiddleInstance inst) {
		super(evtName);
		this.instance = inst;
		this.$case= _case;
	}


	public FiddleInstance getInstance() {
		return instance;
	}

	public void setInstance(FiddleInstance instance) {
		this.instance = instance;
	}
	
	public ICase getCase() {
		return $case;
	}

	
	public void setCase(ICase $case) {
		this.$case = $case;
	}
}
