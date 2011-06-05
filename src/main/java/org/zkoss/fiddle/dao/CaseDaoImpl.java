package org.zkoss.fiddle.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.zkplus.hibernate.HibernateUtil;

public class CaseDaoImpl implements ICaseDao {

	private Session current = null;
	
	public CaseDaoImpl() {

	}
	public CaseDaoImpl(Session s) {
		current = s;
	}
	
	protected Session getCurrentSession() {
		return current == null ? HibernateUtil.currentSession() : current;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.ICaseDao#list()
	 */
	public List<Case> list() {

		return getCurrentSession().createCriteria(Case.class).list();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zkoss.usergroup.dao.ICaseDao#saveOrUdate(org.zkoss.usergroup.model
	 * .Case)
	 */
	public void saveOrUdate(Case m) {
		getCurrentSession().saveOrUpdate(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.ICaseDao#get(java.lang.Long)
	 */
	public Case get(Long id) {
		return (Case) getCurrentSession().get(Case.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.ICaseDao#remove(org.zkoss.usergroup.model.
	 * Case)
	 */
	public void remove(Case m) {
		getCurrentSession().delete(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.ICaseDao#remove(java.lang.Long)
	 */
	public void remove(Long id) {

		getCurrentSession().createQuery("delete from Case where id = :id").setLong("id", id).executeUpdate();
	}

	public Case findCaseByToken(String token, Integer version) {
		Criteria crit = getCurrentSession().createCriteria(Case.class);

		crit.add(Restrictions.eq("token", token));
		crit.add(Restrictions.eq("version", version == null ? 1 : version));

		return (Case) crit.uniqueResult();
	}

	public Integer getLastVersionByToken(String token) {
		return (Integer) getCurrentSession().createQuery("select max(version) from Case where token = :token ")
				.setString("token", token).uniqueResult();
	}

}
