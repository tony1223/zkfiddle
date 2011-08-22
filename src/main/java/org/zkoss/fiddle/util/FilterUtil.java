package org.zkoss.fiddle.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;

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
}
