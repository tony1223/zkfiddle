package org.zkoss.fiddle.dao.api;

import java.util.List;


public interface ITagDao<Tag> extends IDao<Tag> {

	public Tag getTag(String name);
	
	public List<Tag> searchTag(String name);
}
