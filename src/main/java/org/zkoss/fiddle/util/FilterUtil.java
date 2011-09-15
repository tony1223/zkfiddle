package org.zkoss.fiddle.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.composer.viewmodel.CookieUser;
import org.zkoss.fiddle.dao.api.IUserRememberTokenDao;
import org.zkoss.fiddle.model.UserRememberToken;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.spring.SpringUtil;

public class FilterUtil {
	public static String getContextPath(ServletRequest request2) {
		return ((HttpServletRequest) request2).getContextPath();
	}

	public static String getPath(ServletRequest request2) {
		HttpServletRequest request = ((HttpServletRequest) request2);

		String uri = request.getRequestURI();
		String context = request.getContextPath();
		String path = uri.replaceFirst(context, "");
		return path;
	}

	public static String buildJSONP(ServletRequest request, JSONObject obj) {
		String jsonp = request.getParameter("jsonp");
		if (jsonp == null) {
			return obj.toJSONString();
		} else {
			return (jsonp + "(" + obj.toJSONString() + ")");
		}
	}

	public static String buildJSONP(ServletRequest request, JSONArray obj) {
		String jsonp = request.getParameter("jsonp");
		if (jsonp == null) {
			return obj.toJSONString();
		} else {
			return (jsonp + "(" + obj.toJSONString() + ")");
		}
	}

	public static void checkAndLoginIfCookieExist(HttpServletRequest request,HttpServletResponse response,HttpSession session){
		checkAndLoginIfCookieExist(request,response,session,null);
	}

	private static Object getBean(ServletContext ctx,String name){
		if(ctx != null){
			return SpringUtilz.getBean(ctx, name);
		}else{
			return SpringUtil.getBean(name);
		}
	}

	public static void checkAndLoginIfCookieExist(HttpServletRequest request,HttpServletResponse response,HttpSession session,ServletContext ctx){
		if (!UserUtil.isLogin(session)) {
			String cookieToken = CookieUtil.getCookie(request,FiddleConstant.COOKIE_ATTR_USER_TOKEN);
			if (cookieToken != null) {
				IUserRememberTokenDao userRememberTokenDao = (IUserRememberTokenDao) getBean(ctx, "userRememberTokenDao");

				UserRememberToken userToken = userRememberTokenDao
						.findToken(cookieToken);

				if (userToken != null) {
					UserUtil.login(session, new CookieUser(userToken.getName()),
							userToken);
				}else{
					CookieUtil.setCookie(response,FiddleConstant.COOKIE_ATTR_USER_TOKEN, "",0);
				}
			}
		}
	}
}
