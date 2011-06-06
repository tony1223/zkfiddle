package org.zkoss.fiddle.manager;

import org.zkoss.fiddle.dao.CaseRecordDaoImpl;
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.model.api.ICase;


public class CaseRecordManager {

	private ICaseRecordDao dao ;
	
	public CaseRecordManager(){
		dao = new CaseRecordDaoImpl(); 
	}
	
	/**
	 * create 3 type of record for a case
	 * @param cas
	 * @param type
	 */
	public void initRecord(ICase cas){
		
		CaseRecord view = createCaseRecord(cas, CaseRecord.TYPE_VIEW , 1L);
		CaseRecord like = createCaseRecord(cas, CaseRecord.TYPE_LIKE , 0L);
		CaseRecord run = createCaseRecord(cas, CaseRecord.TYPE_RUN , 0L);
		CaseRecord runtmp = createCaseRecord(cas, CaseRecord.TYPE_RUN_TEMP , 0L);
		
		dao.saveOrUdate(view);
		dao.saveOrUdate(like);
		dao.saveOrUdate(run);
		dao.saveOrUdate(runtmp);
	}
	
	private CaseRecord createCaseRecord(ICase cas,int type ,long amount){
		CaseRecord view = new CaseRecord();
		view.setType(type);
		view.setCaseId(cas.getId());
		view.setAmount(amount);
		view.setTitle(cas.getTitle());
		view.setVersion(cas.getVersion());
		view.setToken(cas.getToken());
		return view;
	}
	
	public void increaseRecord(Long casid,int type){
		dao.increase(type, casid);
	}
	
	public void decreaseRecord(Long casid,int type){
		dao.decrease(type, casid);
	}
	
}
