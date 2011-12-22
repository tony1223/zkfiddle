package org.zkoss.fiddle.util;

public class BookmarkUtil {

	public static final String SOURCE_PREFIX = "source-";

	public static int getSourceIndex(String bookmark) {
		if (bookmark == null) {
			throw new IllegalArgumentException("bookmark shouldn't be null");
		}
		if (bookmark.startsWith(SOURCE_PREFIX)) {
			int index = Integer.parseInt(bookmark.substring(SOURCE_PREFIX.length())) - 1;

			return index;
		}

		return -1;
	}
}
