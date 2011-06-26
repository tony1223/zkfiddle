package org.zkoss.fiddle.dao.api;

import java.util.List;

import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.Tag;

public interface ICaseTagDao<CaseTag> extends IDao<CaseTag> {

	public List<Tag> findTagsBy(Case c, int pageIndex, int pageSize);

	public List<Tag> findTagsBy(Case c);
}
