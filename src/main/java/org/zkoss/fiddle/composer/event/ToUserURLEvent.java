package org.zkoss.fiddle.composer.event;


public class ToUserURLEvent extends URLChangeEvent {

	/**
	 *
	 */
	private static final long serialVersionUID = 1401670411326498604L;

	private String userName;

	private boolean guest;

	public ToUserURLEvent(String url) {
		super(url);
	}

	public ToUserURLEvent(String url, String userName, boolean isGuest) {
		super(url, null);
		this.userName = userName;
		guest = isGuest;

	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isGuest() {
		return guest;
	}

	public void setGuest(boolean guest) {
		this.guest = guest;
	}
}
