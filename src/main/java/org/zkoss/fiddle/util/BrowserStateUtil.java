package org.zkoss.fiddle.util;

import org.zkoss.fiddle.model.Tag;


public class BrowserStateUtil {
	
	public static void go(Tag tag){
		BrowserState.go(TagUtil.getViewURL(tag), "ZK Fiddle - Tag - " + tag.getName(), tag);
	}
}
