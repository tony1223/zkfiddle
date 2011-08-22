package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.BasicTransformerAdapter;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.zkoss.fiddle.dao.api.ICaseRatingdDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.CaseRating;
import org.zkoss.fiddle.visualmodel.RatingAmount;

public class CaseRatingDaoImpl extends AbstractDao implements ICaseRatingdDao {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(CaseRatingDaoImpl.class);

	@SuppressWarnings("unchecked")
	public List<CaseRating> list() {
		return getHibernateTemplate().find("from CaseRating");
	}

	public CaseRating findBy(final Case theCase, final String userName) {
		if (theCase == null || theCase.getId() == null) {
			throw new IllegalArgumentException("missing case information ");
		}
		if (userName == null) {
			throw new IllegalArgumentException("missing user name information ");
		}

		return (CaseRating) getHibernateTemplate().execute(
				new HibernateCallback<CaseRating>() {
					public CaseRating doInHibernate(Session session)
							throws HibernateException, SQLException {

						Query query = session
								.createQuery("from CaseRating where caseId = :caseId and userName = :userName");

						query.setLong("caseId", theCase.getId());
						query.setString("userName", userName);

						return (CaseRating) query.uniqueResult();
					}
				});
	}

	public void saveOrUdate(CaseRating m) {
		super.saveOrUdateObject(m);

		if (logger.isInfoEnabled()) {
			logger.info("saveOrUdate(CaseRating) - Clear Recently caches");
		}
	}

	public CaseRating get(Long id) {
		return (CaseRating) getHibernateTemplate().get(CaseRating.class, id);
	}

	public void remove(CaseRating m) {
		// TODO implemenats this
		throw new UnsupportedOperationException("Can't delete CaseRating .");
	}

	public void remove(final Long id) {
		throw new UnsupportedOperationException("Can't delete cases.");
	}

	public RatingAmount countAverage(final Case theCase) {
		return (RatingAmount) getHibernateTemplate().execute(
				new HibernateCallback<RatingAmount>() {
					public RatingAmount doInHibernate(Session session)
							throws HibernateException, SQLException {

						Query query = session
								.createQuery("select count(userName) and avg(amount) from CaseRating where caseId = :caseId ");

						query.setLong("caseId", theCase.getId());
						query.setResultTransformer(new BasicTransformerAdapter() {
							private static final long serialVersionUID = 2558727927107253017L;

							public Object transformTuple(Object[] tuple, String[] aliases) {
								RatingAmount ra = new RatingAmount();
								ra.setRatingUserAmount((Long)tuple[0]);
								ra.setAmount((Long)tuple[1]);
								// TODO Auto-generated method stub
								return ra;
							}
						});
						return (RatingAmount) query.uniqueResult();
					}
				});
	}

}
