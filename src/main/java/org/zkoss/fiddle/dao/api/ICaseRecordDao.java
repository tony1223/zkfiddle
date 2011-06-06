package org.zkoss.fiddle.dao.api;

import org.zkoss.fiddle.model.CaseRecord;

public interface ICaseRecordDao extends IDao<CaseRecord> {
	
	public boolean increase(Integer type, Long caseId);
	
	public boolean decrease(Integer type, Long caseId);
}
