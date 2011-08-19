package org.zkoss.fiddle.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.zkoss.fiddle.model.Tag;

public class TagUtil {

	public static String getViewURL(Tag tag) {
		try {
			return "/tag/" + URLEncoder.encode(tag.getName(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("wrong tag:" + tag, e);
		}
	}

}
