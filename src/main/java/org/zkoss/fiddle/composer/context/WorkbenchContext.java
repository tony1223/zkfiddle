/**WorkbenchContext.java
 * 2011/7/16
 * 
 */
package org.zkoss.fiddle.composer.context;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.FiddleEvents;
import org.zkoss.fiddle.composer.event.InsertResourceEvent;
import org.zkoss.fiddle.composer.event.ResourceChangedEvent;
import org.zkoss.fiddle.composer.event.SaveCaseEvent;
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
import org.zkoss.fiddle.seo.SEOContainer;
import org.zkoss.fiddle.seo.handle.SEOTokenHandlerAdpter;
import org.zkoss.fiddle.seo.model.SEOToken;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.fiddle.visualmodel.VirtualCase;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zkplus.spring.SpringUtil;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class WorkbenchContext {
	private static final Logger logger = Logger.getLogger(WorkbenchContext.class);
	private static final String WORKBENCH_CONTEXT_KEY = "WORKBENCH_CONTEXT_KEY";
	private static final String SOURCE = "source";
	
	
	public static WorkbenchContext getInstance() {
		Execution exec = Executions.getCurrent();
		Desktop desktop = exec.getDesktop();
		
		WorkbenchContext wbCtxt = 
			(WorkbenchContext) desktop.getAttribute(WORKBENCH_CONTEXT_KEY);
		if(wbCtxt==null){
			desktop.setAttribute(WORKBENCH_CONTEXT_KEY, 
					wbCtxt = new WorkbenchContext(exec));
		}
		return wbCtxt;
	}
	private EventQueue sourceQueue;
	/**
	 * a state for if content is changed.
	 * 
	 * Note: For implementation , If user modify the content and then modify it
	 * back , we think that's a source changed state ,too.
	 */
	private boolean sourceChange;
	private ICase $case;
	private boolean newCase;
	private List<Resource> resources;
	
	protected WorkbenchContext(final Execution exec){
		resources = new ArrayList<Resource>();
		$case = (ICase) ((HttpServletRequest)exec.getNativeRequest())
			.getAttribute("__case"); // new Case();

		newCase = ($case == null || $case.getId() == null);
		if (newCase) { // new case!
			resources.addAll(getDefaultResources());
			initSEOHandler($case, resources, exec.getDesktop());
		} else {
			ICaseRecordDao manager = (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
			manager.increase(CaseRecord.Type.View, $case);
			
			if (logger.isDebugEnabled()) {
				logger.debug("counting:" + $case.getToken() + ":" + $case.getVersion() + ":view");
			}
			IResourceDao dao = (IResourceDao) SpringUtil.getBean("resourceDao");
			List<Resource> dbResources = 
				new ArrayList<Resource>(dao.listByCase($case.getId()));
			for (Resource r : dbResources) {
				// we clone it , since we will create a new resource instead of
				// updating old one.
				Resource resource = r.clone();
				resource.setId(null);
				resource.setCaseId(null);
				resource.setCreateDate(new Date());
				resources.add(resource);
			}
			initSEOHandler($case, dbResources, exec.getDesktop());
		}
		sourceQueue = EventQueues.lookup(SOURCE, true);
		sourceQueue.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if (event instanceof ResourceChangedEvent) {
					sourceChange = true;
				} 
			}
		});	
	}

	public void addResource(int type, String fileName){
		Resource ir = ResourceFactory.getDefaultResource( type, fileName);
		resources.add(ir);
		sourceQueue.publish(new InsertResourceEvent( ir));
	}

	public void removeResource(Resource ir) {
		if (ir == null){
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
	public boolean isStartWithNewCase(){
		return newCase;
	}
	
	public List<Resource> getResources(){
		return resources;
	}
	
	public void setCurrentCase(ICase _case){
		$case = _case;
	}
	public ICase getCurrentCase(){
		return $case;
	}
	
	public void ShowResult(FiddleSandbox inst) {
		ShowResultEvent result = new ShowResultEvent(
				FiddleEvents.ON_TEMP_SHOW_RESULT , null, inst);

		if (sourceChange) {
			if ($case != null && $case.getId() != null) {
				ICaseRecordDao manager = (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
				manager.increase(CaseRecord.Type.RunTemp, $case);
				if (logger.isDebugEnabled()) {
					logger.debug("counting:" + $case.getToken() + ":" + $case.getVersion() + ":run-temp");
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
			result.setCase(tmpcase);
		} else {
			result.setCase($case);
			ICaseRecordDao manager = (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
			manager.increase(CaseRecord.Type.Run, $case);
			if (logger.isDebugEnabled()) {
				logger.debug($case.getToken() + ":" + $case.getVersion() + ":run");
			}
		}
		EventQueues.lookup(FiddleEventQueues.SHOW_RESULT, true).publish(result);
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

	private static void initSEOHandler(ICase $case, List<Resource> resources, Desktop desktop) {

		SEOContainer seo = SEOContainer.getInstance(desktop);

		if ($case != null)
			seo.addToken(new SEOToken<ICase>("case", $case));
		if (resources != null)
			seo.addToken(new SEOToken<List<Resource>>("resources", resources));

		seo.addHandler(new SEOTokenHandlerAdpter<ICase>() {

			public boolean accept(String type) {
				return "case".equals(type);
			}

			public void resolve(Writer out, String type, ICase model) throws IOException {

				appendTagStart(out, "div", "case");

				appendTitle(out, 2, model.getTitle());
				appendText(out, "version", model.getVersion());
				appendText(out, "token", model.getToken());
				appendText(out, "create date", model.getCreateDate());

				appendTagEnd(out, "div");
			}
		});

		seo.addHandler(new SEOTokenHandlerAdpter<List<Resource>>() {

			public boolean accept(String type) {
				return "resources".equals(type);
			}

			public void resolve(Writer out, String type, List<Resource> model) throws IOException {

				appendTagStart(out, "div", "resoruces");
				appendTitle(out, 3, "resources");

				for (Resource r : model) {
					appendText(out, "fileName", r.getName());
					appendText(out, "fileType", r.getTypeName());
					// r.getFinalContent() == null only when default resources
					appendText(out, "fileContent", r.getFinalContent() == null ? r.getContent() : r.getFinalContent());
				}
				appendTagEnd(out, "div");
			}
		});
	}
	
	public void subscribeShowResult(EventListener evtListener){
		sourceQueue.subscribe(evtListener);
	}
	
	public void fireResourceChanged(Resource resource){
		sourceQueue.publish(new ResourceChangedEvent(null, resource));
	}
	public void subscribeResourceChanged(EventListener evtListener){
		sourceQueue.subscribe(evtListener);
	}
	
	public void subscribeResourceCreated(EventListener evtListener){
		sourceQueue.subscribe(evtListener);
	}
	
	
	public void fireResourceSaved(boolean folk){
		sourceQueue.publish(new SaveCaseEvent( folk));
	}
	public void subscribeResourceSaved(EventListener evtListener){
		sourceQueue.subscribe(evtListener);
	}
}
