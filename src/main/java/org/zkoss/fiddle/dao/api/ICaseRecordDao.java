package org.zkoss.fiddle.dao.api;

import java.util.List;

import org.zkoss.fiddle.model.CaseRecord;

public interface ICaseRecordDao extends IDao<CaseRecord> {
	
	public boolean increase(Integer type, Long caseId);
	
	public boolean decrease(Integer type, Long caseId);
	
	public Long countByType(final Integer type, final boolean excludeEmpty);
	
	public List<CaseRecord> listByType(final Integer type, final boolean excludeEmpty, final int pageIndex,
			final int pageSize) ;
}
