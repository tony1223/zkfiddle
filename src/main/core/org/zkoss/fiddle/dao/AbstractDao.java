package org.zkoss.fiddle.dao;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class AbstractDao extends HibernateDaoSupport {

	private TransactionTemplate txTemplate;

	public void setTransactionManager(PlatformTransactionManager txManager) {
		this.txTemplate = new TransactionTemplate(txManager);
		this.txTemplate.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
	}


	/*package*/ TransactionTemplate getTxTemplate() {
		return txTemplate;
	}

	protected void saveOrUdateObject(final Object m) {
		getTxTemplate().execute(new HibernateTransacationCallback<Void>(getHibernateTemplate()) {
			public Void doInHibernate(Session session) throws HibernateException, SQLException {
				session.saveOrUpdate(m);
				return null;
			}
		});
	}
	
	protected static void setPage(Query qu,int pageIndex,int pageSize){
		qu.setMaxResults(pageSize);
		qu.setFirstResult((pageIndex - 1) * pageSize);
		
	}
}
