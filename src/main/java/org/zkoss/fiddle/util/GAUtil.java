package org.zkoss.fiddle.util;

import org.zkoss.json.JSONArray;
import org.zkoss.zk.ui.util.Clients;

public class GAUtil {

	@SuppressWarnings("unchecked")
	public static void logAction(String category, String action, String label, int value) {
		JSONArray array = new JSONArray();
		array.add("_trackEvent");
		array.add(category);
		array.add(action);
		array.add(label);
		array.add(value);

		Clients.evalJavaScript("if(_gaq) _gaq.push("+array.toJSONString()+")");
		// _gaq.push(['_trackEvent', 'Videos', 'Play', 'Gone With the Wind']);
	}

	@SuppressWarnings("unchecked")
	public static void logAction(String category, String action, String label) {
		JSONArray array = new JSONArray();
		array.add("_trackEvent");
		array.add(category);
		array.add(action);
		array.add(label);
		Clients.evalJavaScript("if(_gaq) _gaq.push("+array.toJSONString()+")");
		// _gaq.push(['_trackEvent', 'Videos', 'Play', 'Gone With the Wind']);
	}
}
