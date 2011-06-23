package org.zkoss.fiddle.util;


public class UrlUtil {
	public static String getURLFriendlyTitle(String title) {
		if (title == null || "".equals(title.trim()))
			return "";

		StringBuffer sb = new StringBuffer();
		String[] tokens = title.split("[^a-zA-Z0-9]+");
		for (String str : tokens) {
			if(!"".equals(str))
				sb.append("-" + str);
		}
		return sb.toString();
	}
}
