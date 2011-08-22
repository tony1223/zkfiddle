/**WorkbenchContext.java
 * 2011/7/16
 *
 */
package org.zkoss.fiddle.composer.viewmodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.fiddle.composer.event.PreparingShowResultEvent;
import org.zkoss.fiddle.composer.event.ResourceChangedEvent;
import org.zkoss.fiddle.composer.event.ResourceChangedEvent.Type;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventListener;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleSourceEventQueue;
import org.zkoss.fiddle.composer.event.ShowResultEvent;
import org.zkoss.fiddle.core.utils.CRCCaseIDEncoder;
import org.zkoss.fiddle.core.utils.ResourceFactory;
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.manager.VirtualCaseManager;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.util.CaseUtil;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.fiddle.visualmodel.VirtualCase;
import org.zkoss.zkplus.spring.SpringUtil;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class CaseModel {

	private static final Logger logger = Logger.getLogger(CaseModel.class);

	/**
	 * a state for if content is changed.
	 *
	 * Note: For implementation , If user modify the content and then modify it
	 * back , we think that's a source changed state ,too.
	 */
	private boolean sourceChange;

	private Case _case;

	private boolean newCase;

	private List<Resource> resources;

	public CaseModel(Case pCase, boolean tryCase,String zulData) {
		resources = new ArrayList<Resource>();

		_case = pCase;
		newCase = (_case == null || _case.getId() == null);
		if (newCase) { // new case!
			sourceChange = true;
			if(tryCase){
				resources.add(getIndexZulWithZulData(zulData));
			}else{
				resources.addAll(getDefaultResources());
			}
		} else {
			ICaseRecordDao manager = (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
			manager.increase(CaseRecord.Type.View, _case);

			if (logger.isDebugEnabled()) {
				logger.debug("counting:" + _case.getToken() + ":" + _case.getVersion() + ":view");
			}
			IResourceDao dao = (IResourceDao) SpringUtil.getBean("resourceDao");
			List<Resource> dbResources = new ArrayList<Resource>(dao.listByCase(_case.getId()));
			for (Resource r : dbResources) {
				// we clone it , since we will create a new resource instead of
				// updating old one.
				Resource resource = r.clone();
				resource.setId(null);
				resource.setCaseId(null);
				resource.setCreateDate(new Date());
				resources.add(resource);
			}
		}

		FiddleSourceEventQueue.lookup().subscribeResourceChanged(
				new FiddleEventListener<ResourceChangedEvent>(ResourceChangedEvent.class) {
			public void onFiddleEvent(ResourceChangedEvent event) throws Exception {
				if(event.getType() == Type.Removed){
					removeResource(event.getResource());
				}
				sourceChange = true;
			}
		});
		FiddleSourceEventQueue.lookup().subscribeShowResult(new FiddleEventListener<PreparingShowResultEvent>(
				PreparingShowResultEvent.class) {

			public void onFiddleEvent(PreparingShowResultEvent evt) throws Exception {
				ShowResult(evt.getSandbox());
			}
		});
	}

	public void setCase(Case pCase){
		resources.clear();

		_case = pCase;
		newCase = (_case == null || _case.getId() == null);
		if (newCase) { // new case!
			sourceChange = true;
			resources.addAll(getDefaultResources());
		} else {
			ICaseRecordDao manager = (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
			manager.increase(CaseRecord.Type.View, _case);

			if (logger.isDebugEnabled()) {
				logger.debug("counting:" + _case.getToken() + ":" + _case.getVersion() + ":view");
			}
			IResourceDao dao = (IResourceDao) SpringUtil.getBean("resourceDao");
			List<Resource> dbResources = new ArrayList<Resource>(dao.listByCase(_case.getId()));
			for (Resource r : dbResources) {
				// we clone it , since we will create a new resource instead of
				// updating old one.
				Resource resource = r.clone();
				resource.setId(null);
				resource.setCaseId(null);
				resource.setCreateDate(new Date());
				resources.add(resource);
			}
		}
	}

	public boolean isSourceChange() {
		return sourceChange;
	}

	public void setSourceChange(boolean sourceChange) {
		this.sourceChange = sourceChange;
	}

	public void addResource(int type, String fileName) {
		resources.add(ResourceFactory.getDefaultResource(type, fileName));
	}

	public void addResource(Resource resource) {
		resources.add(resource);
	}


	public String getDownloadLink(){
		return CaseUtil.getDownloadURL(getCurrentCase());
	}

	public void removeResource(Resource ir) {
		if (ir == null) {
			throw new IllegalStateException("removing null resource ");
		}
		int k = -1;
		for (int i = 0; i < resources.size(); ++i) {
			if (resources.get(i) == ir) {
				k = i;
				break;
			}
		}
		if (k != -1)
			resources.remove(k);
	}

	public boolean isStartWithNewCase() {
		return newCase;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setCurrentCase(Case pCase) {
		_case = pCase;
	}

	public Case getCurrentCase() {
		return _case;
	}

	public void ShowResult(FiddleSandbox sandbox) {
		ShowResultEvent result = new ShowResultEvent(null, sandbox);

		ICase rcase = null;
		if (sourceChange) {
			rcase = prepareVirtualCase();
		} else {
			result.setCase(_case);
			ICaseRecordDao manager = (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
			manager.increase(CaseRecord.Type.Run, _case);
			if (logger.isDebugEnabled()) {
				logger.debug(_case.getToken() + ":" + _case.getVersion() + ":run");
			}
			rcase = _case;
		}
		FiddleSourceEventQueue.lookup().fireShowResult(rcase, sandbox);

	}

	private ICase prepareVirtualCase(){
		if (_case != null && _case.getId() != null) {
			ICaseRecordDao manager = (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
			manager.increase(CaseRecord.Type.RunTemp, _case);
			if (logger.isDebugEnabled()) {
				logger.debug("counting:" + _case.getToken() + ":" + _case.getVersion() + ":run-temp");
			}
		}
		Case tmpcase = new Case();
		CRCCaseIDEncoder encoder = CRCCaseIDEncoder.getInstance();
		String token = encoder.encode(new Date().getTime());
		tmpcase.setToken(token);
		tmpcase.setVersion(0);

		List<Resource> newlist = new ArrayList<Resource>();
		for (Resource current : resources) {
			Resource cloneResource = current.clone();
			cloneResource.setFinalConetnt(tmpcase);
			newlist.add(cloneResource);
		}
		VirtualCase virtualCase = new VirtualCase(tmpcase, newlist);
		VirtualCaseManager.getInstance().save(virtualCase);
		return tmpcase;
	}

	private static List<Resource> getDefaultResources() {
		List<Resource> resources = new ArrayList<Resource>();
		resources.add(ResourceFactory.getDefaultResource(Resource.TYPE_ZUL));
		// resources.add(getDefaultResource(Resource.TYPE_JS));
		// resources.add(getDefaultResource(Resource.TYPE_CSS));
		// resources.add(getDefaultResource(Resource.TYPE_HTML));
		resources.add(ResourceFactory.getDefaultResource(Resource.TYPE_JAVA));

		return resources;
	}
	private static Resource getIndexZulWithZulData(String zulData) {
		Resource res = ResourceFactory.getDefaultResource(Resource.TYPE_ZUL);
		res.setContent(zulData);
		return res;
	}


}
