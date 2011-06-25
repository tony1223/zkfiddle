package org.zkoss.fiddle.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Executions;

/**
 * A util to help user setting the cookies;
 * @author tony
 *
 */
public class CookieUtil {

	/**
	 * 1 day
	 */
	public static int AGE_ONE_DAY = 60 * 60 * 24;

	/**
	 * 30 days
	 */
	public static int AGE_ONE_MONTH = AGE_ONE_DAY * 30;

	/**
	 * 12 * 30 days
	 */
	public static int AGE_ONE_YEAR = AGE_ONE_MONTH * 12;

	private static HttpServletResponse getExecutionResponse() {
		return ((HttpServletResponse) Executions.getCurrent().getNativeResponse());
	}

	private static HttpServletRequest getExecutionRequest() {
		return ((HttpServletRequest) Executions.getCurrent().getNativeRequest());
	}

	public static void setCookie(HttpServletResponse response, String name, String value) {
		setCookie(response, name, value, -1);
	}

	public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	public static String getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public static void setCookie(String name, String value, int maxage) {
		setCookie(getExecutionResponse(), name, value, maxage);
	}
	
	public static void setCookie(String name, String value) {
		setCookie(getExecutionResponse(), name, value);
	}

	public static String getCookie(String name) {
		return getCookie(getExecutionRequest(), name);
	}
}
