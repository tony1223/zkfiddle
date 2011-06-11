package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.model.CaseRecord;

public class CaseRecordDaoImpl extends AbstractDao implements ICaseRecordDao {

	public CaseRecordDaoImpl() {

	}

	public List<CaseRecord> list() {
		return getHibernateTemplate().find("from CaseRecord");
	}

	public void saveOrUdate(CaseRecord m) {
		getHibernateTemplate().saveOrUpdate(m);
	}

	public CaseRecord get(Long id) {
		return (CaseRecord) getHibernateTemplate().get(CaseRecord.class, id);
	}

	public void remove(CaseRecord m) {
		getHibernateTemplate().delete(m);
	}

	public void remove(final Long id) {
		getHibernateTemplate().execute(new HibernateCallback<Void>() {

			public Void doInHibernate(Session session) throws HibernateException, SQLException {
				session.createQuery("delete from CaseRecord where id = :id").setLong("id", id).executeUpdate();
				return null;
			}
		});
		;

	}

	public boolean increase(final Integer type, final Long caseId) {
		return getHibernateTemplate().execute(new HibernateCallback<Boolean>() {

			public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
				int update = session
						.createSQLQuery(
								"update caserecord set amount = amount + 1 where type = :type and caseId = :caseId")
						.setLong("type", type).setLong("caseId", caseId).executeUpdate();

				return update != 0;
			}
		});

	}

	/**
	 * Note that you can't make it negative, we will block all decreasing for
	 * amount <= 0.
	 */
	public boolean decrease(final Integer type, final Long caseId) {
		return getHibernateTemplate().execute(new HibernateCallback<Boolean>() {

			public Boolean doInHibernate(Session session) throws HibernateException, SQLException {

				int update = session
						.createSQLQuery(
								"update caserecord set amount = amount - 1 where type = :type and caseId = :caseId and amount > 0")
						.setLong("type", type).setLong("caseId", caseId).executeUpdate();

				return update != 0;
			}
		});
	}

	public List<CaseRecord> listByType(final Integer type, final boolean excludeEmpty, final int pageIndex,
			final int pageSize) {

		return getHibernateTemplate().execute(new HibernateCallback<List<CaseRecord>>() {

			public List<CaseRecord> doInHibernate(Session session) throws HibernateException, SQLException {
				String rule = "";
				if (excludeEmpty) {
					rule = " and amount <> 0 ";
				}

				Query query = session.createQuery(
						"from CaseRecord " + " where type = :type " + rule + " order by amount desc").setLong("type",
						type);

				query.setFirstResult((pageIndex - 1) * pageSize);
				query.setMaxResults(pageSize);

				return query.list();
			}
		});
	}

	public Long countByType(final Integer type, final boolean excludeEmpty) {
		return getHibernateTemplate().execute(new HibernateCallback<Long>() {

			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				String rule = "";
				if (excludeEmpty) {
					rule = " and amount <> 0 ";
				}
				return (Long) session
						.createQuery(
								"select count(id) from CaseRecord " + " where type = :type " + rule
										+ " order by amount").setLong("type", type).uniqueResult();
			}
		});

	}

	public CaseRecord get(final Integer type, final Long caseId) {
		return getHibernateTemplate().execute(new HibernateCallback<CaseRecord>() {

			public CaseRecord doInHibernate(Session session) throws HibernateException, SQLException {
				return (CaseRecord) session.createQuery("from CaseRecord where type = :type and caseId = :caseId")
						.setLong("type", type).setLong("caseId", caseId).uniqueResult();
			}
		});

	}
}
