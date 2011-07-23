package org.zkoss.fiddle.dao.api;

import java.util.List;

import org.zkoss.fiddle.model.Tag;


public interface ITagDao extends IDao<Tag> {

	public Tag getTag(String name);
	
	
	public List<Tag> searchTag(String name);
	
	public List<Tag> searchTag(String name,int amount);
	
	/**
	 * return the tag list that in given tag array
	 * @param tags
	 * @return
	 */
	public List<Tag> prepareTags(String[] tags);
	
	public List<Tag> findPopularTags(int amount);
}
