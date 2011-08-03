package org.zkoss.fiddle.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class FilterUtil {

	public static String getPath(ServletRequest request2) {
		HttpServletRequest request = ((HttpServletRequest) request2);

		String uri = request.getRequestURI();
		String context = request.getContextPath();
		String path = uri.replaceFirst(context, "");
		return path;
	}
}
