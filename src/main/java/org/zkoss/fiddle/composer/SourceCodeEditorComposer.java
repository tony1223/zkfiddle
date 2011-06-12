package org.zkoss.fiddle.composer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.fiddle.component.renderer.SourceTabRendererFactory;
import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.FiddleEvents;
import org.zkoss.fiddle.composer.event.SaveEvent;
import org.zkoss.fiddle.composer.event.ShowResultEvent;
import org.zkoss.fiddle.composer.event.SourceChangedEvent;
import org.zkoss.fiddle.composer.event.SourceInsertEvent;
import org.zkoss.fiddle.composer.event.SourceRemoveEvent;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.fiddletabs.Fiddletabs;
import org.zkoss.fiddle.manager.CaseRecordManager;
import org.zkoss.fiddle.manager.VirtualCaseManager;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.model.FiddleSandbox;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.ViewRequest;
import org.zkoss.fiddle.model.VirtualCase;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.fiddle.seo.SEOContainer;
import org.zkoss.fiddle.seo.handle.SEOTokenHandlerAdpter;
import org.zkoss.fiddle.seo.model.SEOToken;
import org.zkoss.fiddle.util.CRCCaseIDEncoder;
import org.zkoss.fiddle.util.ResourceFactory;
import org.zkoss.social.facebook.event.LikeEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class SourceCodeEditorComposer extends GenericForwardComposer {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SourceCodeEditorComposer.class);

	public List<Resource> resources;

	private Fiddletabs sourcetabs;

	private Tabpanels sourcetabpanels;

	private ICase $case = null;

	private Button saveBtn;

	private Textbox caseTitle;

	private Window insertWin;

	/**
	 * a state for if content is changed.
	 * 
	 * Note: For implementation , If user modify the content and then modify it
	 * back , we think taht's a source changed state ,too.
	 */
	private boolean sourceChange = false;

	/**
	 * we use desktop level event queue.
	 */
	private EventQueue sourceQueue = EventQueues.lookup(FiddleEventQueues.SOURCE, true);

	private void initSEOHandler(ICase $case, List<Resource> resources) {

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

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		resources = new ArrayList<Resource>();

		$case = (ICase) requestScope.get("__case"); // new Case();

		if ($case != null) {
			saveBtn.setLabel("Update");
		}

		boolean newCase = ($case == null || $case.getId() == null);
		if (newCase) { // new case!
			resources.addAll(getDefaultResources());
			initSEOHandler($case, resources);
		} else {
			CaseRecordManager manager = new CaseRecordManager();
			manager.increaseRecord($case.getId(), CaseRecord.TYPE_VIEW);
			if (logger.isDebugEnabled()) {
				logger.debug("counting:" + $case.getToken() + ":" + $case.getVersion() + ":view");
			}
			IResourceDao dao = (IResourceDao) SpringUtil.getBean("resourceDao");
			List<Resource> dbResources = dao.listByCase($case.getId());
			for (IResource r : dbResources) {
				// we clone it , since we will create a new resource instead of
				// updating old one..
				Resource resource = r.clone();
				resource.setId(null);
				resource.setCaseId(null);
				resource.setCreateDate(new Date());
				resources.add(resource);
			}

			caseTitle.setValue($case.getTitle());
			initSEOHandler($case, dbResources);
		}

		for (IResource resource : resources) {
			SourceTabRendererFactory.getRenderer(resource.getType()).appendSourceTab(sourcetabs, sourcetabpanels,
					resource);
			if (newCase) {
				// Notify content to do some processing,since we use desktop
				// scope eventQueue,it will not be a performance issue.
				sourceQueue.publish(new SourceChangedEvent(null, resource));
			}
		}

		sourceQueue.subscribe(new EventListener() {

			public void onEvent(Event event) throws Exception {

				if (event instanceof SourceChangedEvent) {
					sourceChange = true;
				} else if (event instanceof ShowResultEvent) {
					ShowResultEvent result = (ShowResultEvent) event;

					CaseRecordManager manager = new CaseRecordManager();
					if (sourceChange) {
						if ($case != null && $case.getId() != null) {
							manager.increaseRecord($case.getId(), CaseRecord.TYPE_RUN_TEMP);
							if (logger.isDebugEnabled()) {
								logger.debug("counting:" + $case.getToken() + ":" + $case.getVersion() + ":run-temp");
							}
						}
						Case tmpcase = new Case();
						CRCCaseIDEncoder encoder = CRCCaseIDEncoder.getInstance();
						String token = encoder.encode(new Date().getTime());
						tmpcase.setToken(token);
						tmpcase.setVersion(0);

						List<IResource> newlist = new ArrayList<IResource>();
						for (IResource current : resources) {
							IResource cloneResource = current.clone();
							cloneResource.buildFinalConetnt(tmpcase);
							newlist.add(cloneResource);
						}
						VirtualCase virtualCase = new VirtualCase(tmpcase, newlist);
						VirtualCaseManager.getInstance().save(virtualCase);
						result.setCase(tmpcase);
					} else {
						result.setCase($case);
						manager.increaseRecord($case.getId(), CaseRecord.TYPE_RUN);
						if (logger.isDebugEnabled()) {
							logger.debug($case.getToken() + ":" + $case.getVersion() + ":run");
						}
					}

					// TODO review if we really need to build this for such
					// complicated, I am still thinking
					// forward to another queue
					EventQueues.lookup(FiddleEventQueues.SHOW_RESULT, true).publish(result);

				} else if (event instanceof SourceInsertEvent) {
					SourceInsertEvent insertEvent = (SourceInsertEvent) event;
					Resource ir = ResourceFactory.getDefaultResource(insertEvent.getType(), insertEvent.getFileName());
					resources.add(ir);
					SourceTabRendererFactory.getRenderer(ir.getType()).appendSourceTab(sourcetabs, sourcetabpanels, ir);
					insertWin.setVisible(false);
				} else if (event instanceof SaveEvent) {
					SaveEvent saveEvt = (SaveEvent) event;
					Case saved = saveCase($case, resources, caseTitle.getValue(), saveEvt.isFork());
					if (saved != null) {
						Executions.getCurrent().sendRedirect(
								"/sample/" + saved.getToken() + "/" + saved.getVersion() + saved.getURLFriendlyTitle());
					}
				} else if (event instanceof SourceRemoveEvent) {
					SourceRemoveEvent sourceRmEvt = (SourceRemoveEvent) event;
					if (sourceRmEvt.getResource() == null) {
						throw new IllegalStateException("removing null resource ");
					}
					removeResource(sourceRmEvt.getResource());
				}
			}
		});
		// @see FiddleDispatcherFilter for those use this directly
		ViewRequest vr = (ViewRequest) requestScope.get("runview");

		if (vr != null) {

			FiddleSandbox inst = vr.getFiddleInstance();

			if (inst != null) { // inst can't be null
				// use echo event to find a good timing
				ShowResultEvent sv = new ShowResultEvent(FiddleEvents.ON_SHOW_RESULT, $case, vr.getFiddleInstance());
				Events.echoEvent(new Event(FiddleEvents.ON_SHOW_RESULT, self, sv));
			} else {
				alert("Can't find sandbox from specific version ");
			}
		}
	}

	public void onLike$fblike(LikeEvent evt) {
		CaseRecordManager crm = new CaseRecordManager();
		if (evt.isLiked()) {
			if (logger.isDebugEnabled()) {
				logger.debug($case.getToken() + ":" + $case.getVersion() + ":like");
			}
			crm.increaseRecord($case.getId(), CaseRecord.TYPE_LIKE);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug($case.getToken() + ":" + $case.getVersion() + ":unlike");
			}
			crm.decreaseRecord($case.getId(), CaseRecord.TYPE_LIKE);
		}
	}

	public void onShowResult(Event e) {
		EventQueues.lookup(FiddleEventQueues.SHOW_RESULT, true).publish((ShowResultEvent) e.getData());
	}

	private void removeResource(IResource ir) {
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

	/*
	 * TODO move this to a case manager.
	 */
	private Case saveCase(ICase _case, List<Resource> resources, String title, boolean fork) {
		Case newCase = new Case();
		newCase.setCreateDate(new Date());

		ICaseDao caseDao = (ICaseDao) SpringUtil.getBean("caseDao");
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

		/*
		 * TODO: review this and use transcation to speed up and be sure it's
		 * built as a unit.
		 */

		caseDao.saveOrUdate(newCase);

		if (_case == null || fork) { // A brand new case
			// TonyQ:
			// we have to set the thread information after we get the id.
			// TODO:check if we could use trigger or something
			// to handle this in DB. currently we have to live with it.
			newCase.setThread(newCase.getId());
			caseDao.saveOrUdate(newCase);
		}

		IResourceDao resourceDao = (IResourceDao) SpringUtil.getBean("resourceDao");
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


	private List<Resource> getDefaultResources() {
		List resources = new ArrayList<IResource>();
		resources.add(ResourceFactory.getDefaultResource(IResource.TYPE_ZUL));
		// resources.add(getDefaultResource(IResource.TYPE_JS));
		// resources.add(getDefaultResource(IResource.TYPE_CSS));
		// resources.add(getDefaultResource(IResource.TYPE_HTML));
		resources.add(ResourceFactory.getDefaultResource(IResource.TYPE_JAVA));

		return resources;
	}

	public void onAdd$sourcetabs(Event e) {
		try {
			insertWin.doModal();
		} catch (Exception e1) {
			logger.error("onAdd$sourcetabs(Event) - e=" + e, e1);
		}
	}

}
