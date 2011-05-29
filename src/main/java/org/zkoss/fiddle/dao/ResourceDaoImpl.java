package org.zkoss.fiddle.dao;

import java.util.List;

import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.zkplus.hibernate.HibernateUtil;

public class ResourceDaoImpl implements IResourceDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.IResourceDao#list()
	 */
	public List<Resource> list() {

		return HibernateUtil.currentSession().createCriteria(Resource.class).list();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zkoss.usergroup.dao.IResourceDao#saveOrUdate(org.zkoss.usergroup.model
	 * .Resource)
	 */
	public void saveOrUdate(Resource m) {
		HibernateUtil.currentSession().saveOrUpdate(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.IResourceDao#get(java.lang.Long)
	 */
	public Resource get(Long id) {
		return (Resource) HibernateUtil.currentSession().get(Resource.class, id);
	}

	/**
	 * 
	 * Unsupported Operation
	 * As current design , all resource should be readonly.
	 * 
	 * @see
	 * org.zkoss.usergroup.dao.IResourceDao#remove(org.zkoss.usergroup.model.
	 * Resource)
	 * 
	 */
	public void remove(Resource m) {
		
		throw new UnsupportedOperationException("All resource are readonly.");
		/*
		HibernateUtil.currentSession().delete(m);
		*/
	}

	/**
	 * Unsupported Operation
	 * As current design , all resource should be readonly.
	 * 
	 * @see org.zkoss.usergroup.dao.IResourceDao#remove(java.lang.Long)
	 */
	public void remove(Long id) {
		throw new UnsupportedOperationException("All resource are readonly.");
		/*
		HibernateUtil.currentSession().
			createQuery("delete from Resource where id = :id").
			setLong("id", id).
			executeUpdate();
		*/
	}

	public List<Resource> listByCase(Long caseId) {
		return HibernateUtil.currentSession()
			.createQuery("from Resource where caseId = :ccaseId")
			.setLong("caseId",caseId)
			.list();
	}
}
