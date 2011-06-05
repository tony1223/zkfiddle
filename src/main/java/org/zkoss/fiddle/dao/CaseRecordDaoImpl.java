package org.zkoss.fiddle.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.zkplus.hibernate.HibernateUtil;

public class CaseRecordDaoImpl implements ICaseRecordDao {

	private Session current = null;

	public CaseRecordDaoImpl() {

	}

	public CaseRecordDaoImpl(Session s) {
		current = s;
	}

	protected Session getCurrentSession() {
		return current == null ? HibernateUtil.currentSession() : current;
	}

	public List<CaseRecord> list() {
		return getCurrentSession().createCriteria(CaseRecord.class).list();
	}

	public void saveOrUdate(CaseRecord m) {
		getCurrentSession().saveOrUpdate(m);
	}

	public CaseRecord get(Long id) {
		return (CaseRecord) getCurrentSession().get(CaseRecord.class, id);
	}

	public void remove(CaseRecord m) {
		getCurrentSession().delete(m);
	}

	public void remove(Long id) {

		getCurrentSession().createQuery("delete from CaseRecord where id = :id").setLong("id", id).executeUpdate();
	}

	public boolean increase(Integer type, Long caseId) {
		int update = getCurrentSession()
				.createSQLQuery("update CaseRecord set amount = amount + 1 where type = :type and caseId = :caseId")
				.setLong("type", type).setLong("caseId", caseId).executeUpdate();

		return update != 0;
	}

	public List<CaseRecord> listByType(Integer type,Long caseId,int pageIndex, int pageSize){
		Query query = getCurrentSession().createQuery("from CaseRecord where type = :type and caseId = :caseId order by amount")
			.setLong("type", type)
			.setLong("caseId", caseId);
	
		query.setFirstResult((pageIndex-1)*pageSize);
		query.setMaxResults(pageSize);
		
		return query.list();
	}

	public Long countByType(Integer type, Long caseId) {
		return (Long) getCurrentSession()
				.createQuery("select count(id) from CaseRecord where type = :type and caseId = :caseId order by amount")
				.setLong("type", type).setLong("caseId", caseId).uniqueResult();

	}
	
	public CaseRecord get(Integer type,Long caseId ){
		return (CaseRecord) getCurrentSession().createQuery("from CaseRecord where type = :type and caseId = :caseId")
			.setLong("type", type)
			.setLong("caseId", caseId).uniqueResult();
	}
}
