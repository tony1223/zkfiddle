package org.zkoss.fiddle.dao.api;

import java.util.List;

import org.zkoss.fiddle.model.CaseTag;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.visualmodel.TagCaseListVO;

public interface ICaseTagDao extends IDao<CaseTag> {

	/**
	 * Save a new CaseTag,
	 * if the case tag exist , will throw CaseTagExistException
	 * @param c
	 * @param t
	 */
	public void save(ICase c, Tag t);
	
	/**
	 * Remove all old tags and insert all tags in list to the case
	 * @param c
	 * @param list
	 */
	public void replaceTags(ICase c, List<Tag> list);

	public List<Tag> findTagsBy(ICase c, int pageIndex, int pageSize);

	public List<Tag> findTagsBy(ICase c);
	
	
	public List<TagCaseListVO> findCaseRecordsBy(Tag c, int pageIndex, int pageSize);
	
	public Long countCaseRecordsBy(Tag tag);
	
}
