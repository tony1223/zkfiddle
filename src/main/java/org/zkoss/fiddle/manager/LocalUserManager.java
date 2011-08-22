package org.zkoss.fiddle.manager;

import org.zkoss.fiddle.dao.api.IUserDao;
import org.zkoss.service.login.IReadonlyLoginService;
import org.zkoss.service.login.IUser;


/**
 * Note the one is only used in local development,
 * not production. we use forum service on production.
 * @author tony
 *
 */

public class LocalUserManager implements IReadonlyLoginService {
	
	private IUserDao userDao;
	
	public IUser getUser(String name) {
		throw new UnsupportedOperationException("not supported");
	}

	public IUser verifyUser(String account, String password) {
		return userDao.getUser(account, password);
	}

	public void setUserDao(IUserDao userDao) {
		this.userDao = userDao;
	}

}
