package org.zkoss.fiddle.util;

import org.zkoss.fiddle.model.api.IRenderCase;

public class CaseUtil {

	public static String getDownloadURL(IRenderCase pCase){
		return "/download/" + pCase.getToken() + "/" + pCase.getVersion();
	}
	
	public static String getViewURL(IRenderCase pCase) {
		return getURL(pCase, "/view/");
	}

	public static String getViewURL(IRenderCase pCase, String zkver) {
		return getURL(pCase, "/view/", zkver);
	}

	public static String getSampleURL(IRenderCase pCase) {
		return getURL(pCase, "/sample/");
	}

	private static String getURL(IRenderCase pCase, String prefix) {
		return prefix + pCase.getCaseUrl();
	}

	private static String getURL(IRenderCase pCase, String prefix, String zkver) {
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
