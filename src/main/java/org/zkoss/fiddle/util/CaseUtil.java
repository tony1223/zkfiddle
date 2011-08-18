package org.zkoss.fiddle.util;

import org.zkoss.fiddle.model.api.IRenderCase;

public class CaseUtil {

	public static String getDownloadURL(IRenderCase pCase){
		return "/download/" + pCase.getToken() + "/" + pCase.getVersion();
	}

	public static String getViewURL(IRenderCase pCase) {
		return getURL("/view/", pCase);
	}

	public static String getViewURL(IRenderCase pCase, String zkver) {
		return getURL("/view/",pCase, zkver);
	}

	public static String getSampleURL(IRenderCase pCase) {
		return getURL( "/sample/",pCase);
	}

	private static String getURL(String prefix,IRenderCase pCase) {
		return prefix + pCase.getCaseUrl();
	}

	private static String getURL(String prefix, IRenderCase pCase, String zkver) {
		return prefix + pCase.getCaseUrl(zkver);
	}

	public static String getPublicTitle(IRenderCase pCase) {
		if (pCase == null) {
			throw new IllegalArgumentException("case shouldn't be null");
		}
		boolean emptytitle = (pCase.getTitle() == null || "".equals((pCase.getTitle().trim())));
		return emptytitle ? "Untitled" : pCase.getTitle();
	}
}
