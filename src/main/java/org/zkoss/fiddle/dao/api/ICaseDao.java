package org.zkoss.fiddle.dao.api;

import org.zkoss.fiddle.model.Case;


public interface ICaseDao extends IDao<Case> {
	public Case findCaseByToken(String token,Integer version);
	
}
