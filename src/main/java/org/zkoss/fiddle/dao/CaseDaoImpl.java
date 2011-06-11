package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.model.Case;


public class CaseDaoImpl extends AbstractDao implements ICaseDao {

	
	public CaseDaoImpl() {

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.ICaseDao#list()
	 */
	public List<Case> list() {
		return getHibernateTemplate().find("from Case");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zkoss.usergroup.dao.ICaseDao#saveOrUdate(org.zkoss.usergroup.model
	 * .Case)
	 */
	public void saveOrUdate(Case m) {
		getHibernateTemplate().saveOrUpdate(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.ICaseDao#get(java.lang.Long)
	 */
	public Case get(Long id) {
		return (Case) getHibernateTemplate().get(Case.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.ICaseDao#remove(org.zkoss.usergroup.model.
	 * Case)
	 */
	public void remove(Case m) {
		throw new UnsupportedOperationException("Can't delete cases .");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.ICaseDao#remove(java.lang.Long)
	 */
	public void remove(final Long id) {
		throw new UnsupportedOperationException("Can't delete cases.");
	}

	public Case findCaseByToken(final String token,final Integer version) {
		return getHibernateTemplate().execute(new HibernateCallback<Case>() {
			public Case doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria crit = session.createCriteria(Case.class);
				
				crit.add(Restrictions.eq("token", token));
				crit.add(Restrictions.eq("version", version == null ? 1 : version));
		
				return (Case) crit.uniqueResult();		
			}

		});
	}

	public Integer getLastVersionByToken(final String token) {
		return getHibernateTemplate().execute(new HibernateCallback<Integer>() {
			public Integer doInHibernate(Session session) throws HibernateException, SQLException {
				return (Integer)  session.createQuery("select max(version) from Case where token = :token ")
				.setString("token", token).uniqueResult();
			}
		});
	}

	public List<Case> getRecentlyCase(final Integer amount) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Case>>() {
			public List<Case> doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("from Case order by id desc");
				query.setMaxResults(amount);
				return query.list();
			}
		});
	}

}
