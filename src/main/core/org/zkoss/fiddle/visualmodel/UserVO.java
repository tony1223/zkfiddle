package org.zkoss.fiddle.visualmodel;

import org.zkoss.fiddle.model.Case;

public class UserVO {
	private String userName;

	private boolean guest;

	public UserVO() {

	}

	public UserVO(Case theCase){
		this(theCase.getAuthorName(),theCase.isGuest());
	}
	public UserVO(String uName, boolean guest) {
		this.userName = uName;
		this.guest = guest;
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
