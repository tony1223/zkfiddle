package org.zkoss.fiddle.dao.api;

import org.zkoss.fiddle.model.User;

/**
 * Note the one is only used in local development,
 * not production. we use forum service on production.
 * @author tony
 *
 */
public interface IUserDao extends IDao<User> {

	public User getUser(String account, String password);
}
