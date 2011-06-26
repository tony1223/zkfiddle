package org.zkoss.fiddle.dao.api;

import org.zkoss.fiddle.model.User;

public interface IUserDao<T> extends IDao<T> {

	public User getUser(String account, String password);
}
