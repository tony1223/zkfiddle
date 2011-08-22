package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.zkoss.fiddle.dao.api.IUserDao;
import org.zkoss.fiddle.model.User;

/**
 * Note the one is only used in local development,
 * not production. we use forum service on production.
 * @author tony
 *
 */
public class UserDaoImpl extends AbstractDao implements IUserDao {

	@SuppressWarnings("unchecked")
	public List<User> list() {
		return (List<User>) getHibernateTemplate().find("from User");
	}


	public void saveOrUdate(User m) {
		super.saveOrUdateObject(m);
	}

	public User get(Long id) {
		return (User) getHibernateTemplate().get(User.class, id);
	}

	public void remove(User m) {
		getHibernateTemplate().delete(m);
	}

	public User getUser(final String account, final String password) {

		return getHibernateTemplate().execute(new HibernateCallback<User>() {

			public User doInHibernate(Session session) throws HibernateException, SQLException {
				Query q = session.createQuery("from User where name = :name and password = :password");
				q.setString("name", account);
				q.setString("password", password);
				return (User) q.uniqueResult();
			}
		});

	}

	public void remove(final Long id) {
		getTxTemplate().execute(new HibernateTransacationCallback<Void>(getHibernateTemplate()) {
			public Void doInHibernate(Session session) throws HibernateException, SQLException {
				session.createQuery("delete from User where id = :id").setLong("id", id).executeUpdate();
				return null;
			}
		});
	}


}
