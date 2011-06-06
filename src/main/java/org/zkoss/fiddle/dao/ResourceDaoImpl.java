package org.zkoss.fiddle.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.zkplus.hibernate.HibernateUtil;

public class ResourceDaoImpl implements IResourceDao {

	Session current = null;

	public ResourceDaoImpl() {

	}

	public ResourceDaoImpl(Session s) {
		current = s;
	}

	public Session getCurrentSession() {
		return current == null ? HibernateUtil.currentSession() : current;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.IResourceDao#list()
	 */
	public List<Resource> list() {

		return getCurrentSession().createCriteria(Resource.class).list();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zkoss.usergroup.dao.IResourceDao#saveOrUdate(org.zkoss.usergroup.
	 * model .Resource)
	 */
	public void saveOrUdate(Resource m) {
		getCurrentSession().saveOrUpdate(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.IResourceDao#get(java.lang.Long)
	 */
	public Resource get(Long id) {
		return (Resource) getCurrentSession().get(Resource.class, id);
	}

	/**
	 * 
	 * Unsupported Operation As current design , all resource should be
	 * readonly.
	 * 
	 * @see org.zkoss.usergroup.dao.IResourceDao#remove(org.zkoss.usergroup.model.
	 *      Resource)
	 * 
	 */
	public void remove(Resource m) {

		throw new UnsupportedOperationException("All resource are readonly.");
		/*
		 * getCurrentSession().delete(m);
		 */
	}

	/**
	 * Unsupported Operation As current design , all resource should be
	 * readonly.
	 * 
	 * @see org.zkoss.usergroup.dao.IResourceDao#remove(java.lang.Long)
	 */
	public void remove(Long id) {
		throw new UnsupportedOperationException("All resource are readonly.");
		/*
		 * getCurrentSession().
		 * createQuery("delete from Resource where id = :id"). setLong("id",
		 * id). executeUpdate();
		 */
	}

	public List<Resource> listByCase(Long caseId) {
		Query query = getCurrentSession().createQuery("from Resource where caseId = :caseId");
		return query.setLong("caseId", caseId).setCacheable(true).list();
	}
}
