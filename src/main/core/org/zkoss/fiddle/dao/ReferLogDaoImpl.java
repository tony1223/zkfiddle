package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.zkoss.fiddle.dao.api.IReferLogDao;
import org.zkoss.fiddle.model.ReferLog;


public class ReferLogDaoImpl extends AbstractDao implements IReferLogDao {

	@SuppressWarnings("unchecked")
	public List<ReferLog> list() {
		return (List<ReferLog>) getHibernateTemplate().find("from ReferLog");
	}

	public void saveOrUdate(ReferLog m) {
		super.saveOrUdateObject(m);
	}

	public ReferLog get(Long id) {
		return getHibernateTemplate().load(ReferLog.class, id);
	}

	public void remove(ReferLog m) {
		getHibernateTemplate().delete(m);
	}

	public void remove(final Long id) {
		getTxTemplate().execute(new HibernateTransacationCallback<Void>(getHibernateTemplate()) {

			public Void doInHibernate(Session session) throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("doInHibernate(Session) - start");
				}

				session.createQuery("delete from ReferLog where id = :id").setLong("id", id).executeUpdate();

				if (logger.isDebugEnabled()) {
					logger.debug("doInHibernate(Session) - end");
				}
				return null;
			}
		});
	}

}
