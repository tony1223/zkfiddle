package org.zkoss.fiddle.composer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.zkoss.fiddle.component.renderer.SourceTabRendererFactory;
import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.FiddleEvents;
import org.zkoss.fiddle.composer.event.SaveEvent;
import org.zkoss.fiddle.composer.event.ShowResultEvent;
import org.zkoss.fiddle.composer.event.SourceInsertEvent;
import org.zkoss.fiddle.composer.event.SourceRemoveEvent;
import org.zkoss.fiddle.dao.CaseDaoImpl;
import org.zkoss.fiddle.dao.ResourceDaoImpl;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.FiddleInstance;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.ViewRequest;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.fiddle.util.CRCCaseIDEncoder;
import org.zkoss.fiddle.util.FileUtil;
import org.zkoss.fiddle.util.StringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;

public class SourceCodeEditorComposer extends GenericForwardComposer {

	public List<Resource> resources;

	private Tabs sourcetabs;

	private Tabpanels sourcetabpanels;

	private Combobox type;

	private Textbox fileName;

	private ICase c = null;

	private Button saveBtn;

	/**
	 * we use desktop level event queue.
	 */
	private EventQueue queue = EventQueues.lookup(FiddleEventQueues.SOURCE, true);

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		// FIXME add logger for counting
		// TODO ask user to have index.zul as a must have.

		resources = new ArrayList<Resource>();

		c = null; // new Case();
		String caseToken = (String) requestScope.get("token");
		String version = (String) requestScope.get("ver");
		if (caseToken != null) {
			ICaseDao caseDao = new CaseDaoImpl();

			try {
				c = caseDao.findCaseByToken(caseToken, version == null ? null : Integer.parseInt(version));
				if (c == null) {
					Executions.sendRedirect("/");
				}
				saveBtn.setLabel("Update");
			} catch (IllegalArgumentException e) { // means caseId is not a
													// valid string
				// TODO wrote a logger here.
				c = null;
			}
		}

		if (c == null || c.getId() == null) { // new case!
			resources.addAll(getDefaultResources());
		} else {
			IResourceDao dao = new ResourceDaoImpl();
			List<Resource> dbResources = dao.listByCase(c.getId());
			for (IResource r : dbResources) {
				// we clone it , since we will create a new resource instead
				// of updating old one..
				Resource resource = r.clone();
				resource.setId(null);
				resource.setCaseId(null);
				resource.setCreateDate(new Date());
				resources.add(resource);
			}
		}

		for (IResource resource : resources) {
			SourceTabRendererFactory.getRenderer(resource.getType()).appendSourceTab(sourcetabs, sourcetabpanels,
					resource);
		}

		queue.subscribe(new EventListener() {

			public void onEvent(Event event) throws Exception {
				if (event instanceof SourceInsertEvent) {
					SourceInsertEvent insertEvent = (SourceInsertEvent) event;
					Resource ir = getDefaultResource(insertEvent.getType(), insertEvent.getFileName());
					resources.add(ir);
					SourceTabRendererFactory.getRenderer(ir.getType()).appendSourceTab(sourcetabs, sourcetabpanels, ir);
				} else if (event instanceof SaveEvent) {
					SaveEvent saveEvt = (SaveEvent) event;
					saveCase(resources, saveEvt.isFork());
				} else if (event instanceof SourceRemoveEvent) {
					SourceRemoveEvent sourceRmEvt = (SourceRemoveEvent) event;
					if (sourceRmEvt.getResource() == null) {
						throw new IllegalStateException("removing null resource ");
					}
					int k = -1;
					for (int i = 0; i < resources.size(); ++i) {
						if (resources.get(i) == sourceRmEvt.getResource()) {
							k = i;
							break;
						}
					}
					if (k != -1)
						resources.remove(k);
				}
			}
		});
		// @see FiddleDispatcherFilter for those use this directly
		ViewRequest vr = (ViewRequest) requestScope.get("runview");

		if (vr != null) {

			FiddleInstance inst = vr.getFiddleInstance();

			if (inst != null) { // inst can't be null
				// use echo event to find a good timing
				ShowResultEvent sv = new ShowResultEvent(FiddleEvents.ON_SHOW_RESULT, vr.getToken(),
						vr.getTokenVersion(), vr.getFiddleInstance());
				Events.echoEvent(new Event(FiddleEvents.ON_SHOW_RESULT, self, sv));
			} else {
				alert("Can't find sandbox from specific version ");
			}
		}
	}

	public void onShowResult(Event e) {
		EventQueues.lookup(FiddleEventQueues.SHOW_RESULT, true).publish((ShowResultEvent) e.getData());
	}

	private void saveCase(List<Resource> resources, boolean fork) {
		Case nc = new Case();
		nc.setCreateDate(new Date());

		ICaseDao caseDao = new CaseDaoImpl();
		if (c == null || fork) { // Create a brand new case
			nc.setVersion(1);
			nc.setToken(CRCCaseIDEncoder.getInstance().encode(new Date().getTime()));

			if (c != null) { // fork
				nc.setFromId(c.getId());
			}

		} else {
			nc.setToken(c.getToken());
			nc.setThread(c.getThread());
			nc.setVersion(caseDao.getLastVersionByToken(c.getToken()) + 1);
		}

		/*
		 * TODO: review this and use transcation to speed up and be sure it's
		 * built as a unit.
		 */

		caseDao.saveOrUdate(nc);

		if (c == null || fork) { // A brand new case
			// TonyQ:
			// we have to set the thread information after we get the id.
			// TODO:check if we could use trigger or something
			// to handle this in DB. currently we have to live with it.
			nc.setThread(nc.getId());
			caseDao.saveOrUdate(nc);
		}

		IResourceDao resourceDao = new ResourceDaoImpl();
		for (Resource resource : resources) {
			resource.setId(null);
			resource.setCaseId(nc.getId());
			resource.buildFinalConetnt(nc.getToken(), nc.getVersion());
			resourceDao.saveOrUdate(resource);
		}
		Executions.getCurrent().sendRedirect("/code/" + nc.getToken() + ("/" + nc.getVersion()));
	}

	/**
	 * TODO move it to resource
	 * 
	 * @param type
	 * @param name
	 * @return
	 */
	private Resource getDefaultResource(int type, String name) {
		
		ServletContext req = (ServletContext) Executions.getCurrent().getDesktop().getWebApp().getNativeContext();
		
		if (IResource.TYPE_ZUL == type) {
			return new Resource(IResource.TYPE_ZUL, name,FileUtil.readIfExist(req.getRealPath("/WEB-INF/_templates/index.zul")));
		} else if (IResource.TYPE_JS == type) {
			return new Resource(IResource.TYPE_JS, name, "function hello(){alert('hello');}");
		} else if (IResource.TYPE_CSS == type) {
			return new Resource(IResource.TYPE_CSS, name, ".hello{ \n color:red; \n }");
		} else if (IResource.TYPE_HTML == type) {
			return (new Resource(IResource.TYPE_HTML, name, StringUtil.concatln(
					"<html>\n  <head>\n    <title>Hello</title>\n  </head>\n", "<body>\n    hello\n  </body>\n</html>")));
		} else if (IResource.TYPE_JAVA == type) {
			
			return new Resource(IResource.TYPE_JAVA, name, 
				FileUtil.readIfExist(req.getRealPath("/WEB-INF/_templates/TestComposer.java"))
			);
		} else
			return null;
	}

	/**
	 * TODO move it to resource
	 * 
	 * @param type
	 * @return
	 */
	private IResource getDefaultResource(int type) {
		if (IResource.TYPE_ZUL == type) {
			IResource r = getDefaultResource(type, "index.zul");
			r.setCanDelete(false);
			return r;
		} else if (IResource.TYPE_JS == type)
			return getDefaultResource(type, "index.js");
		else if (IResource.TYPE_CSS == type)
			return getDefaultResource(type, "index.css");
		else if (IResource.TYPE_HTML == type)
			return getDefaultResource(type, "index.html");
		else if (IResource.TYPE_JAVA == type)
			return getDefaultResource(type, "TestComposer.java");
		else
			return null;
	}

	private List<Resource> getDefaultResources() {
		List resources = new ArrayList<IResource>();
		resources.add(getDefaultResource(IResource.TYPE_ZUL));
		// resources.add(getDefaultResource(IResource.TYPE_JS));
		// resources.add(getDefaultResource(IResource.TYPE_CSS));
		// resources.add(getDefaultResource(IResource.TYPE_HTML));
		resources.add(getDefaultResource(IResource.TYPE_JAVA));

		return resources;
	}

	public void onClick$insert(Event e) {
		int typeVal = type.getSelectedIndex();
		String fileNameVal = fileName.getValue();

		queue.publish(new SourceInsertEvent(null, null, fileNameVal, typeVal));
	}

}
