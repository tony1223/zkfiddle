package org.zkoss.fiddle.dao.api;

import org.zkoss.fiddle.model.UserRememberToken;

/**
 * Note the one is only used in local development,
 * not production. we use forum service on production.
 * @author tony
 *
 */
public interface IUserRememberTokenDao extends IDao<UserRememberToken> {

	/**
	 * create a new token and save into the database.
	 * @param userName
	 * @return
	 */
	public UserRememberToken genereate(String userName);

	/**
	 * lookup the user from token
	 * @param token
	 * @return
	 */
	public UserRememberToken findToken(final String token) ;
}
