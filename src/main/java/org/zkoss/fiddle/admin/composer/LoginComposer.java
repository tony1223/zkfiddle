package org.zkoss.fiddle.admin.composer;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.util.UserUtil;
import org.zkoss.service.login.IReadonlyLoginService;
import org.zkoss.service.login.IUser;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Sessions;
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

		IReadonlyLoginService loginService = (IReadonlyLoginService) SpringUtil.getBean("loginManager");

		IUser user = loginService.verifyUser(account.getValue(), password.getValue());

		if (user != null) {
			
			if( user.getRole() != FiddleConstant.ROLE_ADMIN){
				alert("Permission denied! You don't have admin right!");
			}else{
				UserUtil.login(Sessions.getCurrent(), user);
				//force reload
				Clients.evalJavaScript("self.location.href= self.location.href;");				
			}
		}


	}

}
