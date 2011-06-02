package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.model.FiddleInstance;
import org.zkoss.zk.ui.event.Event;

public class ShowResultEvent extends Event {

	private String token;

	private String version;

	private FiddleInstance instance;

	public ShowResultEvent(String evtName, String token, String version, FiddleInstance inst) {
		super(evtName);
		this.token = token;
		this.version = version;
		this.instance = inst;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public FiddleInstance getInstance() {
		return instance;
	}

	public void setInstance(FiddleInstance instance) {
		this.instance = instance;
	}

	public String getToken() {
		return token;
	}
}
