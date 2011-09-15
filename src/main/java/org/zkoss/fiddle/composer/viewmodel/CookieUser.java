package org.zkoss.fiddle.composer.viewmodel;

import org.zkoss.service.login.IUser;
import org.zkoss.service.login.impl.User;

/**
 * This is just a temp solution before we implemented the getUser and API from
 * Zorum.
 *
 * @author cwang4
 *
 */
public class CookieUser implements IUser {

	private String name;
	private Boolean active = true;

	public CookieUser(String name) {
		super();
		this.name = name;
	}

	public int compareTo(IUser o) {
		return 0;
	}

	public String getDisplayName() {
		return name;
	}

	public String getEmailAddress() {
		return null;
	}

	public String getName() {
		return name;
	}

	public Integer getRole() {
		return User.ROLE_USER;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean arg0) {
		active = arg0;
	}

	public void setDisplayName(String arg0) {
		this.name = arg0;
	}

	public void setEmailAddress(String arg0) {
	}

	public void setName(String arg0) {
		this.name = arg0;
	}

	public void setRole(Integer arg0) {

	}

}
