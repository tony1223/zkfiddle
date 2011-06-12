package org.zkoss.fiddle.dao;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;


public abstract class HibernateTransacationCallback<T> implements HibernateCallback<T> ,TransactionCallback<T>{

	HibernateTemplate template ;
	public HibernateTransacationCallback(HibernateTemplate tmp){
		this.template = tmp;
	}

	public T doInTransaction(TransactionStatus status) {
		return this.template.execute(this);
	}

}
