package org.zkoss.fiddle.dao.api;

import java.util.List;


public interface ITagDao<Tag> extends IDao<Tag> {

	public Tag getTag(String name);
	
	public List<Tag> searchTag(String name);
	
	/**
	 * return the tag list that in given tag array
	 * @param tags
	 * @return
	 */
	public List<Tag> prepareTags(String[] tags);
	
}
