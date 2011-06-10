package org.zkoss.fiddle.dao.api;

import java.util.List;

import org.zkoss.fiddle.model.Case;


public interface ICaseDao extends IDao<Case> {

	public Case findCaseByToken(String token,Integer version);
	
	public Integer getLastVersionByToken(String token);
	
	
	public List<Case> getRecentlyCase(Integer amount);
	
}
