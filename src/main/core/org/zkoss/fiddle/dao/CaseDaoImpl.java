package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.zkoss.fiddle.core.utils.CacheHandler;
import org.zkoss.fiddle.core.utils.FiddleCache;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.model.Case;


public class CaseDaoImpl extends AbstractDao implements ICaseDao {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(CaseDaoImpl.class);

	@SuppressWarnings("unchecked")
	public List<Case> list() {
		return getHibernateTemplate().find("from Case");
	}

	public void saveOrUdate(Case m) {
		super.saveOrUdateObject(m);

		if (logger.isInfoEnabled()) {
			logger.info("saveOrUdate(Case) - Clear Recently caches");
		}
		FiddleCache.RecentlyCases.removeAll();
	}


	public Case get(Long id) {
		return (Case) getHibernateTemplate().get(Case.class, id);
	}

	public void remove(Case m) {
		throw new UnsupportedOperationException("Can't delete cases .");
	}

	public void remove(final Long id) {
		throw new UnsupportedOperationException("Can't delete cases.");
	}

	public Case findCaseByToken(final String token,final Integer version) {
		return (Case) FiddleCache.CaseByToken.execute(new CacheHandler<Case>() {
			protected Case execute() {
				return getHibernateTemplate().execute(new HibernateCallback<Case>() {
					public Case doInHibernate(Session session) throws HibernateException, SQLException {
						Criteria crit = session.createCriteria(Case.class);

						crit.add(Restrictions.eq("token", token));
						crit.add(Restrictions.eq("version", version == null ? 1 : version));
						return (Case) crit.uniqueResult();
					}

				});
			}
			protected String getKey() {
				return token+"::"+version;
			}
		});

	}

	public Integer getLastVersionByToken(final String token) {
		return getHibernateTemplate().execute(new HibernateCallback<Integer>() {

			public Integer doInHibernate(Session session) throws HibernateException, SQLException {
				return (Integer) session.createQuery("select max(version) from Case where token = :token ")
						.setString("token", token).uniqueResult();
			}
		});
	}

	/**
	 * We cache this and update it when save a new case,
	 * yes , it's a often updated field , but it's on the index page , so it's worth to cache it here.
	 */
	public List<Case> getRecentlyCase(final Integer amount) {
		//TODO review , I think we should remove out the cache code to app.
		return (List<Case>) FiddleCache.RecentlyCases.execute(new CacheHandler<List<Case>>() {

			protected List<Case> execute() {
				return getHibernateTemplate().execute(new HibernateCallback<List<Case>>() {

					@SuppressWarnings("unchecked")
					public List<Case> doInHibernate(Session session) throws HibernateException, SQLException {
						Query query = session.createQuery("from Case order by id desc");
						query.setMaxResults(amount);
						return query.list();
					}
				});
			}

			protected String getKey() {
				return "recently:" + amount;
			}
		});

	}

	public List<Case> list(final int pageIndex,final  int pageSize) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Case>>() {

			@SuppressWarnings("unchecked")
			public List<Case> doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("from Case order by id desc");
				setPage(query,pageIndex,pageSize);
				return query.list();
			}
		});
	}

	public Integer size() {
		return ((Long) getHibernateTemplate().execute(new HibernateCallback<Long>() {

			public Long doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("select count(id) from Case");
				return (Long) query.uniqueResult();
			}
		})).intValue();
	}

}
