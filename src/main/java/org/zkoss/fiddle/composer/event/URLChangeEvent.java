package org.zkoss.fiddle.composer.event;

import org.zkoss.zk.ui.event.Event;

public class URLChangeEvent extends Event {

	private String url;

	public URLChangeEvent(String url) {
		super(FiddleEvents.ON_URL_CHANGE, null, null);
		this.url = url;
	}

	public URLChangeEvent(String url, Object data) {
		super(FiddleEvents.ON_URL_CHANGE, null, data);
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
