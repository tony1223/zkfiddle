package org.zkoss.fiddle.dao.api;

import org.zkoss.fiddle.model.User;

public interface IUserDao extends IDao<User> {

	public User getUser(String account, String password);
}
