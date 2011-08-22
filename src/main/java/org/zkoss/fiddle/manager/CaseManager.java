package org.zkoss.fiddle.manager;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.zkoss.fiddle.core.utils.CRCCaseIDEncoder;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.dao.api.ICaseRatingdDao;
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.dao.api.ICaseTagDao;
import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.CaseRating;
import org.zkoss.fiddle.model.CaseRecord.Type;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.visualmodel.RatingAmount;

public class CaseManager extends AbstractManager {

	private ICaseDao caseDao;

	private IResourceDao resourceDao;

	private ICaseRecordDao caseRecordDao;

	private ICaseRatingdDao caseRatingDao;

	private ICaseTagDao caseTagDao;

	public Case saveCase(final Case _case, final List<Resource> resources,
			final String title, final String authorName,final Boolean isGuest,
			final boolean fork,final String posterIP,final boolean keepTag) {

		return getTxTemplate().execute(new TransactionCallback<Case>() {
			public Case doInTransaction(TransactionStatus status) {

				Case newCase = new Case();
				newCase.setCreateDate(new Date());
				List<Tag> tags = null;
				if (_case == null || fork) { // Create a brand new case
					newCase.setVersion(1);
					newCase.setToken(CRCCaseIDEncoder.getInstance().encode(new Date().getTime()));
				} else {
					newCase.setToken(_case.getToken());
					newCase.setThread(_case.getThread());
					newCase.setVersion(caseDao.getLastVersionByToken(_case.getToken()) + 1);

					if(keepTag){
						tags = caseTagDao.findTagsBy(_case);
					}
				}
				if (_case != null) { // from a existing case
					newCase.setFromId(_case.getId());
				}
				newCase.setGuest(isGuest);
				newCase.setAuthorName(authorName);
				newCase.setTitle(title);
				newCase.setPosterIP(posterIP);

				caseDao.saveOrUdate(newCase);

				if (_case == null || fork) { // A brand new case
					// TonyQ: we have to set the thread information after we get the id.
					newCase.setThread(newCase.getId());
					caseDao.saveOrUdate(newCase);
				}

				for (Resource resource : resources) {
					resource.setId(null);
					resource.setCaseId(newCase.getId());
					resource.setFinalConetnt(newCase);
					resourceDao.saveOrUdate(resource);
				}

				if(tags != null){ //keep tag!
					caseTagDao.replaceTags(newCase, tags);
				}

				return newCase;
			}

		});

	}

	public RatingAmount rankCase(final Case cas,final String userName,final long amount){
		return (RatingAmount) getTxTemplate().execute(new TransactionCallback<RatingAmount>() {
			public RatingAmount doInTransaction(TransactionStatus status) {

				CaseRating rating = caseRatingDao.findBy(cas, userName);
				if(rating == null){
					rating = new CaseRating();
					rating.setCaseId(cas.getId());
					rating.setUserName(userName);
				}
				rating.setAmount(amount);
				caseRatingDao.saveOrUdate(rating);

				RatingAmount ratingAmount = caseRatingDao.countAverage(cas);
				caseRecordDao.updateAmount(Type.Rating, cas, ratingAmount.getAmount());

				return ratingAmount;
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

	public void setCaseRecordDao(ICaseRecordDao caseRecordDao) {
		this.caseRecordDao = caseRecordDao;
	}

	public void setCaseRatingDao(ICaseRatingdDao caseRatingdDao) {
		this.caseRatingDao = caseRatingdDao;
	}
}
