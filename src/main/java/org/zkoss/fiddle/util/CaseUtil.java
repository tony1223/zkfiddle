package org.zkoss.fiddle.util;

import org.zkoss.fiddle.model.api.IRenderCase;

public class CaseUtil {

	public static String getViewURL(IRenderCase saved) {
		return getURL(saved, "/view/");
	}

	public static String getViewURL(IRenderCase saved, String zkver) {
		return getURL(saved, "/view/", zkver);
	}

	public static String getSampleURL(IRenderCase saved) {
		return getURL(saved, "/sample/");
	}

	private static String getURL(IRenderCase saved, String prefix) {
		return prefix + saved.getCaseUrl();
	}

	private static String getURL(IRenderCase saved, String prefix, String zkver) {
		return prefix + saved.getCaseUrl(zkver);
	}

	public static String getPublicTitle(IRenderCase pcase) {
		if (pcase == null) {
			throw new IllegalArgumentException("case shouldn't be null");
		}
		boolean emptytitle = (pcase.getTitle() == null || "".equals((pcase.getTitle().trim())));
		return emptytitle ? "Untitled" : pcase.getTitle();
	}
}
