package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.zkoss.fiddle.dao.api.ITagDao;
import org.zkoss.fiddle.model.Tag;


public class TagDaoImpl extends AbstractDao implements ITagDao<Tag> {
	
	public List<Tag> list() {
		return getHibernateTemplate().find("from Tag");
	}


	public void saveOrUdate(Tag m) {
		super.saveOrUdateObject(m);
	}

	public Tag get(Long id) {
		return (Tag) getHibernateTemplate().get(Tag.class, id);
	}

	public void remove(Tag m) {
		getHibernateTemplate().delete(m);
	}
	
	public void remove(final Long id) {

		getTxTemplate().execute(new HibernateTransacationCallback<Void>(getHibernateTemplate()) {
			public Void doInHibernate(Session session) throws HibernateException, SQLException {
				session.createQuery("delete from Tag where id = :id").setLong("id", id).executeUpdate();
				return null;
			}
		});
	}

	public Tag getTag(final String name) {
		return getHibernateTemplate().execute(new HibernateCallback<Tag>() {
			public Tag doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("from Tag where name = :name");
				query.setString("name",name);
				return (Tag) query.uniqueResult();
			}
		});
	}

	public List<Tag> searchTag(final String name) {
		return getHibernateTemplate().execute(new HibernateCallback<List<Tag>>() {
			public List<Tag> doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery("from Tag where name like :name");
				query.setString("name",name+"%");
				return (List<Tag>) query.list();
			}
		});
	}


}
