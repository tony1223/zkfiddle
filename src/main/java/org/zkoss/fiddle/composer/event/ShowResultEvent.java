package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.model.Instance;
import org.zkoss.zk.ui.event.Event;

public class ShowResultEvent extends Event {

	private String token;

	private String version;

	private Instance instance;

	public ShowResultEvent(String name, String token, String version, Instance inst) {
		super(name);
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

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public String getToken() {
		return token;
	}
}
