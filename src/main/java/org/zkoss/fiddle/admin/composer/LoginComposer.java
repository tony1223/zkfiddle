package org.zkoss.fiddle.admin.composer;

import org.zkoss.fiddle.dao.api.IUserDao;
import org.zkoss.fiddle.model.User;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Textbox;

public class LoginComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = -8939520006607012735L;

	private Textbox account;

	private Textbox password;

	public void onOK() {

		IUserDao userDao = (IUserDao) SpringUtil.getBean("userDao");

		User u = userDao.getUser(account.getValue(), password.getValue());

		if (u != null) {
			session.setAttribute("login", true);
			session.setAttribute("account", u.getAccount());
		}

		//force reload
		Clients.evalJavaScript("self.location.href= self.location.href;");

	}

}
