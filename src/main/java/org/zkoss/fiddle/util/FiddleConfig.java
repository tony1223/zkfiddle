package org.zkoss.fiddle.util;

import org.zkoss.lang.Library;

public class FiddleConfig {
	public static final String ATTR_HOSTNAME  ="org.zkoss.fiddle.hostname" ;

	public static String getHostName() {
		return Library.getProperty(ATTR_HOSTNAME,"http://localhost");
	}
}
