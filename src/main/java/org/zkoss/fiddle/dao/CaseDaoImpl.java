package org.zkoss.fiddle.dao;

import java.util.List;

import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.zkplus.hibernate.HibernateUtil;

public class CaseDaoImpl implements ICaseDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.ICaseDao#list()
	 */
	public List<Case> list() {

		return HibernateUtil.currentSession().createCriteria(Case.class).list();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zkoss.usergroup.dao.ICaseDao#saveOrUdate(org.zkoss.usergroup.model
	 * .Case)
	 */
	public void saveOrUdate(Case m) {
		HibernateUtil.currentSession().saveOrUpdate(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.ICaseDao#get(java.lang.Long)
	 */
	public Case get(Long id) {
		return (Case) HibernateUtil.currentSession().get(Case.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zkoss.usergroup.dao.ICaseDao#remove(org.zkoss.usergroup.model.
	 * Case)
	 */
	public void remove(Case m) {
		HibernateUtil.currentSession().delete(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.ICaseDao#remove(java.lang.Long)
	 */
	public void remove(Long id) {

		HibernateUtil.currentSession().
			createQuery("delete from Case where id = :id").
			setLong("id", id).
			executeUpdate();
	}

}
