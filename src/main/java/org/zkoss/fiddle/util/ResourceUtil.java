package org.zkoss.fiddle.util;

import org.zkoss.fiddle.model.Resource;

public class ResourceUtil {

	public static String getTypeMode(Resource resource) {
		switch (resource.getType()) {
		case Resource.TYPE_ZUL:
			return "xml";
		case Resource.TYPE_JAVA:
			return "java";
		case Resource.TYPE_JS:
			return "javascript";
		case Resource.TYPE_HTML:
			return "html";
		case Resource.TYPE_CSS:
			return "css";
		default:
			return "unknown";
		}
	}

	public static String getFinalContent(Resource resource) {
		if (resource.getFinalContent() == null) {
			return resource.getContent();
		} else {
			return resource.getFinalContent();
		}
	}

	public static String getTypeName(Resource resource) {
		switch (resource.getType()) {
		case Resource.TYPE_ZUL:
			return "zul";
		case Resource.TYPE_JAVA:
			return "java";
		case Resource.TYPE_JS:
			return "javascript";
		case Resource.TYPE_HTML:
			return "html";
		case Resource.TYPE_CSS:
			return "css";
		default:
			return "unknown";
		}
	}

}
