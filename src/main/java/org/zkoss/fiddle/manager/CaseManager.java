package org.zkoss.fiddle.manager;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.zkoss.fiddle.core.utils.CRCCaseIDEncoder;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.dao.api.ICaseTagDao;
import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.model.api.IResource;

public class CaseManager extends AbstractManager {

	private ICaseDao caseDao;

	private IResourceDao resourceDao;
	
	private ICaseTagDao caseTagDao;
	
	public Case saveCase(final ICase _case, final List<IResource> resources, final String title, 
			final boolean fork,final String posterIP,final boolean keepTag) {

		return getTxTemplate().execute(new TransactionCallback<Case>() {
			public Case doInTransaction(TransactionStatus status) {
				
				Case newCase = new Case();
				newCase.setCreateDate(new Date());
				List<Tag> tags = null;
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
					
					if(keepTag){
						tags = caseTagDao.findTagsBy(_case);
					}
				}
				
				newCase.setTitle(title);
				newCase.setPosterIP(posterIP);

				caseDao.saveOrUdate(newCase);

				if (_case == null || fork) { // A brand new case
					// TonyQ: we have to set the thread information after we get the id.
					newCase.setThread(newCase.getId());
					caseDao.saveOrUdate(newCase);
				}

				for (IResource resource : resources) {
					resource.setId(null);
					resource.setCaseId(newCase.getId());
					resource.setFinalConetnt(newCase);
					resourceDao.saveOrUdate((Resource)resource);//IAN: no solution if the View keep using resource entity bean!!!
				}
				
				if(tags != null){ //keep tag!
					caseTagDao.replaceTags(newCase, tags);
				}
				
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

	public void setCaseTagDao(ICaseTagDao caseTagDao) {
		this.caseTagDao = caseTagDao;
	}
	
}
