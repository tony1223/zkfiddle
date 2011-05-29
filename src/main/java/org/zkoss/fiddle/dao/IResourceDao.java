package org.zkoss.fiddle.dao;

import java.util.List;

import org.zkoss.fiddle.model.Resource;

public interface IResourceDao {

	
	public abstract List<Resource> listByCase(Long caseId);

	public abstract List<Resource> list();

	public abstract void saveOrUdate(Resource m);

	public abstract Resource get(Long id);

	public abstract void remove(Resource m);

	public abstract void remove(Long id);

}
