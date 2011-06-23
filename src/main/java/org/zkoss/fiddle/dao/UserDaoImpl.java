package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.zkoss.fiddle.dao.api.IUserDao;
import org.zkoss.fiddle.model.User;


public class UserDaoImpl extends AbstractDao implements IUserDao<User> {

	public UserDaoImpl() {

	}
	
	public List<User> list() {
		return getHibernateTemplate().find("from User");
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
				Query q = session.createQuery("from User where account = :account and password = :password");
				q.setString("account", account);
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
