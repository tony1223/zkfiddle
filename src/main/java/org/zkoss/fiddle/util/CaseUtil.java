package org.zkoss.fiddle.util;

import org.zkoss.fiddle.model.api.ICase;


public class CaseUtil {

	public static String getSampleURL(ICase saved){
		return	"/sample/" + saved.getToken() + "/" + saved.getVersion() + saved.getURLFriendlyTitle();
	}
}
