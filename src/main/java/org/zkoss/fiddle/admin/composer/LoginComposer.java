package org.zkoss.fiddle.admin.composer;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.dao.api.IUserRememberTokenDao;
import org.zkoss.fiddle.model.UserRememberToken;
import org.zkoss.fiddle.util.CookieUtil;
import org.zkoss.fiddle.util.UserUtil;
import org.zkoss.service.login.IReadonlyLoginService;
import org.zkoss.service.login.IUser;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;

public class LoginComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = -8939520006607012735L;

	private Textbox account;

	private Textbox password;

	private Checkbox rembember;

	public void onOK() {

		IReadonlyLoginService loginService = (IReadonlyLoginService) SpringUtil.getBean("loginManager");

		IUser user = loginService.verifyUser(account.getValue(), password.getValue());

		if (user != null) {

			//FIXME implements login cookie here.
			if( user.getRole() != FiddleConstant.ROLE_ADMIN){
				alert("Permission denied! You don't have admin right!");
			}else{
				UserRememberToken token = null;
				if(rembember.isChecked()){
					IUserRememberTokenDao userRememberTokenDao = (IUserRememberTokenDao) SpringUtil.getBean("userRememberTokenDao");
					token = userRememberTokenDao.genereate(user.getName());
					CookieUtil.setCookie(FiddleConstant.COOKIE_ATTR_USER_TOKEN, token.getToken(), CookieUtil.AGE_ONE_YEAR);
				}
				UserUtil.login(Sessions.getCurrent(), user, token);
				//force reload
				Clients.evalJavaScript("self.location.href= self.location.href;");
			}
		}


	}

}
