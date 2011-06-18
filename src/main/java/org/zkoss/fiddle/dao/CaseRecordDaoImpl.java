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
		if (logger.isDebugEnabled()) {
			logger.debug("list() - start");
		}

		List<CaseRecord> returnList = getHibernateTemplate().find("from CaseRecord");
		if (logger.isDebugEnabled()) {
			logger.debug("list() - end");
		}
		return returnList;
	}

	public void saveOrUdate(CaseRecord m) {
		if (logger.isDebugEnabled()) {
			logger.debug("saveOrUdate(CaseRecord) - start");
		}

		super.saveOrUdateObject(m);

		if (logger.isDebugEnabled()) {
			logger.debug("saveOrUdate(CaseRecord) - end");
		}
	}

	public CaseRecord get(Long id) {
		if (logger.isDebugEnabled()) {
			logger.debug("get(Long) - start");
		}

		CaseRecord returnCaseRecord = (CaseRecord) getHibernateTemplate().get(CaseRecord.class, id);
		if (logger.isDebugEnabled()) {
			logger.debug("get(Long) - end");
		}
		return returnCaseRecord;
	}

	public void remove(CaseRecord m) {
		if (logger.isDebugEnabled()) {
			logger.debug("remove(CaseRecord) - start");
		}

		getHibernateTemplate().delete(m);

		if (logger.isDebugEnabled()) {
			logger.debug("remove(CaseRecord) - end");
		}
	}

	public void remove(final Long id) {
		if (logger.isDebugEnabled()) {
			logger.debug("remove(Long) - start");
		}

		getTxTemplate().execute(new HibernateTransacationCallback<Void>(getHibernateTemplate()) {

			public Void doInHibernate(Session session) throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("doInHibernate(Session) - start");
				}

				session.createQuery("delete from CaseRecord where id = :id").setLong("id", id).executeUpdate();

				if (logger.isDebugEnabled()) {
					logger.debug("doInHibernate(Session) - end");
				}
				return null;
			}
		});

		if (logger.isDebugEnabled()) {
			logger.debug("remove(Long) - end");
		}
	}

	public boolean increase(final CaseRecord.Type type, final ICase _case) {
		if (logger.isInfoEnabled()) {
			logger.info("increase(CaseRecord.Type, ICase) - start :" + type+":"+_case.getCaseUrl());
		}


		if (type == CaseRecord.Type.Like) {
			if (logger.isDebugEnabled()) {
				logger.debug("increase(Integer, Long) - clean top10likeRecord cache");
			}

			CacheFactory.getTop10LikedRecord().removeAll();
		}
		boolean returnboolean = getTxTemplate().execute(
				new HibernateTransacationCallback<Boolean>(getHibernateTemplate()) {

					public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
						if (logger.isDebugEnabled()) {
							logger.debug("doInHibernate(Session) - start");
						}

						int update = session
								.createQuery(
										"update CaseRecord set amount = amount + 1 where type = :type and caseId = :caseId")
								.setLong("type", type.value()).setLong("caseId", _case.getId()).executeUpdate();

						if (update == 0) {
							CaseRecord view = createCaseRecord(_case, type.value(), 1L);
							session.save(view);
						}

						if (logger.isDebugEnabled()) {
							logger.debug("doInHibernate(Session) - end");
						}
						return true;
					}
				});
		if (logger.isDebugEnabled()) {
			logger.debug("increase(CaseRecord.Type, ICase) - end");
		}
		return returnboolean;
	}

	/**
	 * Note that you can't make it negative, we will block all decreasing for
	 * amount <= 0.
	 */
	public boolean decrease(final CaseRecord.Type type, final Long caseId) {
		if (logger.isDebugEnabled()) {
			logger.debug("decrease(CaseRecord.Type, Long) - start");
		}

		if (type == CaseRecord.Type.Like) {
			if (logger.isDebugEnabled()) {
				logger.debug("increase(Integer, Long) - clean top10likeRecord cache");
			}
			CacheFactory.getTop10LikedRecord().removeAll();
		}
		boolean returnboolean = getTxTemplate().execute(
				new HibernateTransacationCallback<Boolean>(getHibernateTemplate()) {

					public Boolean doInHibernate(Session session) throws HibernateException, SQLException {
						if (logger.isDebugEnabled()) {
							logger.debug("doInHibernate(Session) - start");
						}

						int update = session
								.createQuery(
										"update CaseRecord set amount = amount - 1 where type = :type and caseId = :caseId and amount > 0")
								.setLong("type", type.value()).setLong("caseId", caseId).executeUpdate();

						Boolean returnBoolean = update != 0;
						if (logger.isDebugEnabled()) {
							logger.debug("doInHibernate(Session) - end");
						}
						return returnBoolean;
					}
				});
		if (logger.isDebugEnabled()) {
			logger.debug("decrease(CaseRecord.Type, Long) - end");
		}
		return returnboolean;
	}

	public List<CaseRecord> listByType(final CaseRecord.Type type, final boolean excludeEmpty, final int pageIndex,
			final int pageSize) {
		if (logger.isDebugEnabled()) {
			logger.debug("listByType(CaseRecord.Type, boolean, int, int) - start");
		}

		List<CaseRecord> returnList = getHibernateTemplate().execute(new HibernateCallback<List<CaseRecord>>() {

			public List<CaseRecord> doInHibernate(Session session) throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("doInHibernate(Session) - start");
				}

				String rule = "";
				if (excludeEmpty) {
					rule = " and amount <> 0 ";
				}

				Query query = session.createQuery(
						"from CaseRecord " + " where type = :type " + rule + " order by amount desc").setLong("type",
						type.value());

				query.setFirstResult((pageIndex - 1) * pageSize);
				query.setMaxResults(pageSize);
				List<CaseRecord> returnList2 = query.list();
				if (logger.isDebugEnabled()) {
					logger.debug("doInHibernate(Session) - end");
				}
				return returnList2;
			}
		});
		if (logger.isDebugEnabled()) {
			logger.debug("listByType(CaseRecord.Type, boolean, int, int) - end");
		}
		return returnList;
	}

	public Long countByType(final CaseRecord.Type type, final boolean excludeEmpty) {
		if (logger.isDebugEnabled()) {
			logger.debug("countByType(CaseRecord.Type, boolean) - start");
		}

		Long returnLong = getHibernateTemplate().execute(new HibernateCallback<Long>() {

			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("doInHibernate(Session) - start");
				}

				String rule = "";
				if (excludeEmpty) {
					rule = " and amount <> 0 ";
				}

				Query query = session.createQuery("select count(id) from CaseRecord " + " where type = :type " + rule
						+ " order by amount");
				query.setLong("type", type.value());
				Long returnLong2 = (Long) query.uniqueResult();
				if (logger.isDebugEnabled()) {
					logger.debug("doInHibernate(Session) - end");
				}
				return returnLong2;
			}
		});
		if (logger.isDebugEnabled()) {
			logger.debug("countByType(CaseRecord.Type, boolean) - end");
		}
		return returnLong;

	}

	public CaseRecord get(final Integer type, final Long caseId) {
		if (logger.isDebugEnabled()) {
			logger.debug("get(Integer, Long) - start");
		}

		CaseRecord returnCaseRecord = getHibernateTemplate().execute(new HibernateCallback<CaseRecord>() {

			public CaseRecord doInHibernate(Session session) throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("doInHibernate(Session) - start");
				}

				Query query = session.createQuery("from CaseRecord where type = :type and caseId = :caseId");
				query.setLong("type", type).setLong("caseId", caseId);
				CaseRecord returnCaseRecord2 = (CaseRecord) query.uniqueResult();
				if (logger.isDebugEnabled()) {
					logger.debug("doInHibernate(Session) - end");
				}
				return returnCaseRecord2;
			}
		});
		if (logger.isDebugEnabled()) {
			logger.debug("get(Integer, Long) - end");
		}
		return returnCaseRecord;

	}

	private CaseRecord createCaseRecord(ICase cas, int type, long amount) {
		if (logger.isDebugEnabled()) {
			logger.debug("createCaseRecord(ICase, int, long) - start");
		}

		CaseRecord view = new CaseRecord();
		view.setType(type);
		view.setCaseId(cas.getId());
		view.setAmount(amount);
		view.setTitle(cas.getTitle());
		view.setVersion(cas.getVersion());
		view.setToken(cas.getToken());

		if (logger.isDebugEnabled()) {
			logger.debug("createCaseRecord(ICase, int, long) - end");
		}
		return view;
	}
}
