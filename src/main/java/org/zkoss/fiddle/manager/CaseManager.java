package org.zkoss.fiddle.manager;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.util.CRCCaseIDEncoder;

public class CaseManager extends AbstractManager {

	private ICaseDao caseDao;

	private IResourceDao resourceDao;

	public Case saveCase(final ICase _case, final List<Resource> resources, final String title, final boolean fork) {

		return getTxTemplate().execute(new TransactionCallback<Case>() {

			public Case doInTransaction(TransactionStatus status) {
				Case newCase = new Case();
				newCase.setCreateDate(new Date());

				if (_case == null || fork) { // Create a brand new case
					newCase.setVersion(1);
					newCase.setToken(CRCCaseIDEncoder.getInstance().encode(new Date().getTime()));

					if (_case != null) { // fork
						newCase.setFromId(_case.getId());
					}

				} else {
					newCase.setToken(_case.getToken());
					newCase.setThread(_case.getThread());
					newCase.setVersion(caseDao.getLastVersionByToken(_case.getToken()) + 1);
				}
				newCase.setTitle(title);

				caseDao.saveOrUdate(newCase);

				if (_case == null || fork) { // A brand new case
					// TonyQ:
					// we have to set the thread information after we get the
					// id.
					// TODO:check if we could use trigger or something
					// to handle this in DB. currently we have to live with it.
					newCase.setThread(newCase.getId());
					caseDao.saveOrUdate(newCase);
				}

				for (Resource resource : resources) {
					resource.setId(null);
					resource.setCaseId(newCase.getId());
					resource.buildFinalConetnt(newCase);
					resourceDao.saveOrUdate(resource);
				}
				CaseRecordManager caseRecordManager = new CaseRecordManager();
				caseRecordManager.initRecord(newCase);
				return newCase;
			}

		});

	}

	public void setCaseDao(ICaseDao caseDao) {
		this.caseDao = caseDao;
	}

	public void setResourceDao(IResourceDao resourceDao) {
		this.resourceDao = resourceDao;
	}

}
