package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.util.CacheFactory;

public class CaseRecordDaoImpl extends AbstractDao implements ICaseRecordDao {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(CaseRecordDaoImpl.class);

	public CaseRecordDaoImpl() {

	}

	public List<CaseRecord> list() {
		return getHibernateTemplate().find("from CaseRecord");
	}

	public void saveOrUdate(CaseRecord m) {
		super.saveOrUdateObject(m);
	}

	public CaseRecord get(Long id) {
		return (CaseRecord) getHibernateTemplate().get(CaseRecord.class, id);
	}

	public void remove(CaseRecord m) {
		getHibernateTemplate().delete(m);
	}

	public void remove(final Long id) {

		getTxTemplate().execute(new HibernateTransacationCallback<Void>(getHibernateTemplate()) {

			public Void doInHibernate(Session session) throws HibernateException, SQLException {
				session.createQuery("delete from CaseRecord where id = :id").setLong("id", id).executeUpdate();
				return null;
			}
		});

	}

	public boolean increase(final Integer type, final Long caseId) {
		if (type == CaseRecord.TYPE_LIKE) {
			if (logger.isDebugEnabled()) {
				logger.debug("increase(Integer, Long) - clean top10likeRecord cache");
			}

			CacheFactory.getTop10LikedRecord().removeAll();
		}
		return getTxTemplate().execute(new HibernateTransacationCallback<Boolean>(getHibernateTemplate()) {

			public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
				int update = session
						.createQuery(
								"update CaseRecord set amount = amount + 1 where type = :type and caseId = :caseId")
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
		if (type == CaseRecord.TYPE_LIKE) {
			if (logger.isDebugEnabled()) {
				logger.debug("increase(Integer, Long) - clean top10likeRecord cache");
			}
			CacheFactory.getTop10LikedRecord().removeAll();
		}
		return getTxTemplate().execute(new HibernateTransacationCallback<Boolean>(getHibernateTemplate()) {

			public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
				int update = session
						.createQuery(
								"update CaseRecord set amount = amount - 1 where type = :type and caseId = :caseId and amount > 0")
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

				Query query = session.createQuery("select count(id) from CaseRecord " + " where type = :type " + rule
						+ " order by amount");
				query.setLong("type", type);
				return (Long) query.uniqueResult();
			}
		});

	}

	public CaseRecord get(final Integer type, final Long caseId) {
		return getHibernateTemplate().execute(new HibernateCallback<CaseRecord>() {

			public CaseRecord doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("from CaseRecord where type = :type and caseId = :caseId");
				query.setLong("type", type).setLong("caseId", caseId);
				return (CaseRecord) query.uniqueResult();
			}
		});

	}

	/**
	 * create 3 type of record for a case
	 * 
	 * @param cas
	 * @param type
	 */
	public void createRecords(final ICase cas) {

		getTxTemplate().execute(new HibernateTransacationCallback<Void>(getHibernateTemplate()) {

			public Void doInHibernate(Session session) throws HibernateException, SQLException {
				CaseRecord view = createCaseRecord(cas, CaseRecord.TYPE_VIEW, 1L);
				CaseRecord like = createCaseRecord(cas, CaseRecord.TYPE_LIKE, 0L);
				CaseRecord run = createCaseRecord(cas, CaseRecord.TYPE_RUN, 0L);
				CaseRecord runtmp = createCaseRecord(cas, CaseRecord.TYPE_RUN_TEMP, 0L);
				session.save(view);
				session.save(like);
				session.save(run);
				session.save(runtmp);
				return null;
			}
		});

	}

	private CaseRecord createCaseRecord(ICase cas, int type, long amount) {
		CaseRecord view = new CaseRecord();
		view.setType(type);
		view.setCaseId(cas.getId());
		view.setAmount(amount);
		view.setTitle(cas.getTitle());
		view.setVersion(cas.getVersion());
		view.setToken(cas.getToken());
		return view;
	}
}
