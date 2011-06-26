package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.zkoss.fiddle.dao.api.ICaseTagDao;
import org.zkoss.fiddle.model.CaseTag;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.model.api.ICase;

public class CaseTagDaoImpl extends AbstractDao implements ICaseTagDao<CaseTag> {

	public List<CaseTag> list() {
		return getHibernateTemplate().find("from CaseTag");
	}

	public void save(ICase c, Tag t) {
		CaseTag ct = new CaseTag();
		ct.setCaseId(c.getId());
		ct.setTagId(t.getId());
		super.saveOrUdateObject(ct);
	}
	
	public void replaceTags(final ICase _case,final List<Tag> list) {
		getTxTemplate().execute(new HibernateTransacationCallback<Void>(getHibernateTemplate()) {
			public Void doInHibernate(Session session) throws HibernateException, SQLException {
				
				//decrease the amount
				Query query = session.createQuery("update Tag set amount = amount -1 "+
						" where id in (select tagId from CaseTag where caseId = :caseId)");
				query.setLong("caseId",_case.getId());
				query.executeUpdate();
				
				//removeing the tag
				Query query2 = session.createQuery("delete from CaseTag where caseId = :caseId");
				query2.setLong("caseId",_case.getId());
				query2.executeUpdate();
				
				for(Tag tag:list){
					CaseTag caseTag = new CaseTag();
					caseTag.setCaseId(_case.getId());
					caseTag.setTagId(tag.getId());
					session.save(caseTag);
				}
				
				//add the new amount 
				Query query3 = session.createQuery("update Tag set amount = amount +1  "+
				" where id in (select tagId from CaseTag where caseId = :caseId)");
				query3.setLong("caseId",_case.getId());
				query3.executeUpdate();
				
				
				return null;
			}
		});
		
	}

	

	public List<Tag> findTagsBy(final ICase c) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Tag>>() {

			public List<Tag> doInHibernate(Session session) throws HibernateException, SQLException {
				Query qu = session.createQuery("select t.* from Tag t,TagCase tc "
						+ " where t.id = tc.tagId and tc.caseId = :caseId ");
				qu.setLong("caseId", c.getId());
				return qu.list();
			}
		});
	}

	public List<Tag> findTagsBy(final ICase c, final int pageIndex, final int pageSize) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Tag>>() {

			public List<Tag> doInHibernate(Session session) throws HibernateException, SQLException {
				Query qu = session.createQuery("select t from Tag t,CaseTag tc "
						+ " where t.id = tc.tagId and tc.caseId = :caseId ");
				qu.setLong("caseId", c.getId());
				qu.setMaxResults(pageSize);
				qu.setFirstResult((pageIndex - 1) * pageSize);
				return qu.list();
			}
		});
	}

	public void saveOrUdate(CaseTag m) {
		super.saveOrUdateObject(m);
	}

	public CaseTag get(Long id) {
		throw new UnsupportedOperationException("unsupported");
	}

	public void remove(CaseTag m) {
		getHibernateTemplate().delete(m);
	}

	public void remove(final Long id) {
		throw new UnsupportedOperationException("unsupported");
	}

}
