package org.zkoss.fiddle.util;

import org.zkoss.fiddle.model.api.IRenderCase;


public class CaseUtil {

	public static String getSampleURL(IRenderCase saved){
		return	"/sample/" + saved.getToken() + "/" + saved.getVersion() + saved.getURLFriendlyTitle();
	}
}
