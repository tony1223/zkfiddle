package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.zkoss.fiddle.dao.api.ICaseTagDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.CaseTag;
import org.zkoss.fiddle.model.Tag;

public class CaseTagDaoImpl extends AbstractDao implements ICaseTagDao<CaseTag> {

	public List<CaseTag> list() {
		return getHibernateTemplate().find("from CaseTag");
	}

	public void saveOrUdate(Case c, Tag t) {
		CaseTag ct = new CaseTag();
		ct.setCaseId(c.getId());
		ct.setTagId(t.getId());
		super.saveOrUdateObject(ct);
	}

	public List<Tag> findTagsBy(final Case c) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Tag>>() {

			public List<Tag> doInHibernate(Session session) throws HibernateException, SQLException {
				Query qu = session.createQuery("select t.* from Tag t,TagCase tc "
						+ " where t.id = tc.tagId and tc.caseId = :caseId ");
				qu.setLong("caseId", c.getId());
				return qu.list();
			}
		});
	}

	public List<Tag> findTagsBy(final Case c, final int pageIndex, final int pageSize) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Tag>>() {

			public List<Tag> doInHibernate(Session session) throws HibernateException, SQLException {
				Query qu = session.createQuery("select t.* from Tag t,TagCase tc "
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
