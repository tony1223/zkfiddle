package org.zkoss.fiddle.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.zkoss.fiddle.core.utils.CRCCaseIDEncoder;
import org.zkoss.fiddle.dao.api.IUserRememberTokenDao;
import org.zkoss.fiddle.model.UserRememberToken;

/**
 * Note the one is only used in local development, not production. we use forum
 * service on production.
 *
 * @author TonyQ
 *
 */
public class UserRememberTokenDaoImpl extends AbstractDao implements
		IUserRememberTokenDao {

	public List<UserRememberToken> list() {
		throw new UnsupportedOperationException("not expecting to be called");
	}

	public void saveOrUdate(UserRememberToken m) {
		super.saveOrUdateObject(m);
	}

	public UserRememberToken get(Long id) {
		return (UserRememberToken) getHibernateTemplate().get(
				UserRememberToken.class, id);
	}

	public void remove(UserRememberToken m) {
		getHibernateTemplate().delete(m);
	}

	public UserRememberToken findToken(final String token) {
		return getHibernateTemplate().execute(new HibernateCallback<UserRememberToken>() {
			public UserRememberToken doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query q = session
						.createQuery("from UserRememberToken where token = :token");
				q.setString("token", token);
				return (UserRememberToken) q.uniqueResult();
			}
		});

	}

	public void remove(final Long id) {
		getTxTemplate().execute(
			new HibernateTransacationCallback<Void>(
					getHibernateTemplate()) {
				public Void doInHibernate(Session session)
						throws HibernateException, SQLException {
					session.createQuery(
							"delete from UserRememberToken where id = :id")
							.setLong("id", id).executeUpdate();
					return null;
				}
			});
	}

	public UserRememberToken genereate(String userName) {
		String tokens = CRCCaseIDEncoder.getInstance().encode(
				new Date().getTime());

		String newtoken = mixTwoString(tokens,userName);

		UserRememberToken urt = new UserRememberToken();
		urt.setName(userName);
		urt.setToken(newtoken);
		urt.setCreateDate(new Date());
		saveOrUdate(urt);
		return urt;
	}

	/**
	 * if input for 1234 and abcdefg , it should come out 1a2b3c4defg
	 * @param str1
	 * @param str2
	 * @return
	 */
	private static String mixTwoString(String str1, String str2) {
		int ind = 0;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str1.length(); ++i) {
			sb.append(str1.charAt(i));

			if (str2.length() > ind) {
				sb.append(str2.charAt(i));
			}
			ind++;
		}

		if (str2.length() > ind) {
			sb.append(str2.substring(ind));
		}

		return sb.toString();
	}

}
