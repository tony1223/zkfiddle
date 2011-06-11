package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.model.Resource;

public class ResourceDaoImpl extends AbstractDao implements IResourceDao {

	public ResourceDaoImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.IResourceDao#list()
	 */
	public List<Resource> list() {
		return getHibernateTemplate().find("from Resource");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zkoss.usergroup.dao.IResourceDao#saveOrUdate(org.zkoss.usergroup.
	 * model .Resource)
	 */
	public void saveOrUdate(Resource m) {
		getHibernateTemplate().saveOrUpdate(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.usergroup.dao.IResourceDao#get(java.lang.Long)
	 */
	public Resource get(Long id) {
		return (Resource) getHibernateTemplate().get(Resource.class, id);
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

		throw new UnsupportedOperationException("Can't delete resources.");
		/*
		 * getHibernateTemplate().delete(m);
		 */
	}

	/**
	 * Unsupported Operation As current design , all resource should be
	 * readonly.
	 * 
	 * @see org.zkoss.usergroup.dao.IResourceDao#remove(java.lang.Long)
	 */
	public void remove(Long id) {
		throw new UnsupportedOperationException("Can't delete resources.");
	}

	public List<Resource> listByCase(final Long caseId) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Resource>>() {

			public List<Resource> doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from Resource where caseId = :caseId").setLong("caseId", caseId).list();
			}
		});

	}
}
