package org.zkoss.fiddle.manager;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class AbstractManager {

	private TransactionTemplate txTemplate;

	public void setTransactionManager(PlatformTransactionManager txManager) {
		this.txTemplate = new TransactionTemplate(txManager);
		this.txTemplate.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
	}

	/* package */TransactionTemplate getTxTemplate() {
		return txTemplate;
	}

}
