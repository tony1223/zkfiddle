package org.zkoss.fiddle.dao.api;

import java.util.List;

import org.zkoss.fiddle.model.Resource;

public interface IResourceDao extends IDao<Resource> {
	
	public List<Resource> listByCase(Long caseId);
	

}
