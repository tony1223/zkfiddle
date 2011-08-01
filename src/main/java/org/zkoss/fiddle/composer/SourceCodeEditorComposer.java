package org.zkoss.fiddle.composer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.component.renderer.SourceTabRendererFactory;
import org.zkoss.fiddle.composer.event.FiddleEventListener;
import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.FiddleEvents;
import org.zkoss.fiddle.composer.event.FiddleSourceEventQueue;
import org.zkoss.fiddle.composer.event.InsertResourceEvent;
import org.zkoss.fiddle.composer.event.PreparingShowResultEvent;
import org.zkoss.fiddle.composer.event.ResourceChangedEvent.Type;
import org.zkoss.fiddle.composer.event.SaveCaseEvent;
import org.zkoss.fiddle.composer.viewmodel.CaseModel;
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.dao.api.ICaseTagDao;
import org.zkoss.fiddle.dao.api.ITagDao;
import org.zkoss.fiddle.fiddletabs.Fiddletabs;
import org.zkoss.fiddle.manager.CaseManager;
import org.zkoss.fiddle.manager.FiddleSandboxManager;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.util.CaseUtil;
import org.zkoss.fiddle.util.SEOUtils;
import org.zkoss.fiddle.visualmodel.CaseRequest;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.social.facebook.event.LikeEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class SourceCodeEditorComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = -5940380002871513285L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SourceCodeEditorComposer.class);

	/**
	 * case management model.
	 */
	private CaseModel caseModel;

	private Fiddletabs sourcetabs;

	private Tabpanels sourcetabpanels;

	private Textbox caseTitle;

	private Window insertWin;

	private Div caseToolbar;

	private A download;

	/* for tags */
	private Hlayout tagContainer;

	private Label tagEmpty;

	private Textbox tagInput;

	private Hlayout editTag;

	private Hlayout viewTag;

	private String lastVal;

	private Checkbox cbSaveTag;

	private CaseRequest viewRequestParam;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		ICase $case = (ICase) requestScope.get(FiddleConstant.REQUEST_ATTR_CASE);

		final FiddleSourceEventQueue sourceQueue = FiddleSourceEventQueue.lookup();

		Boolean tryCase = (Boolean) requestScope.get(FiddleConstant.REQUEST_ATTR_TRY_CASE);
		String zulData = (String) Executions.getCurrent().getParameter("zulData");
		caseModel = new CaseModel($case, tryCase!=null && tryCase ,zulData);

		if(tryCase){
			Events.echoEvent(new Event("onShowTryCase",self, null));
		}

		boolean newCase = caseModel.isStartWithNewCase();

		if (!newCase) {
			$case = caseModel.getCurrentCase();
			caseTitle.setValue($case.getTitle());
			download.setHref(caseModel.getDownloadLink());
			caseToolbar.setVisible(true);
			initTagEditor($case);
		}

		sourceQueue.subscribeShowResult(new FiddleEventListener<PreparingShowResultEvent>(PreparingShowResultEvent.class) {
			public void onFiddleEvent(PreparingShowResultEvent evt) throws Exception {
				caseModel.ShowResult(evt.getSandbox());
			}
		});
		sourceQueue.subscribeResourceCreated(new FiddleEventListener<InsertResourceEvent>(InsertResourceEvent.class) {

			public void onFiddleEvent(InsertResourceEvent event) throws Exception {
				InsertResourceEvent insertEvent = (InsertResourceEvent) event;
				Resource ir = insertEvent.getResource();
				SourceTabRendererFactory.getRenderer(ir.getType()).appendSourceTab(sourcetabs, sourcetabpanels, ir);
			}
		});
		sourceQueue.subscribeResourceSaved(new FiddleEventListener<SaveCaseEvent>(SaveCaseEvent.class) {

			public void onFiddleEvent(SaveCaseEvent event) throws Exception {
				SaveCaseEvent saveEvt = (SaveCaseEvent) event;
				CaseManager caseManager = (CaseManager) SpringUtil.getBean("caseManager");

				String title = caseTitle.getValue().trim();
				String ip = Executions.getCurrent().getRemoteAddr();
				ICase saved = caseManager.saveCase(caseModel.getCurrentCase(), caseModel.getResources(),
						title, saveEvt.isFork(), ip, cbSaveTag.isChecked());
				if (saved != null) {
					Executions.getCurrent().sendRedirect(CaseUtil.getSampleURL(saved));
				}
			}
		});

		for (Resource resource : caseModel.getResources()) {
			SourceTabRendererFactory.getRenderer(resource.getType()).appendSourceTab(sourcetabs, sourcetabpanels,
					resource);
			if (newCase) {
				// Notify content to do some processing,since we use desktop
				// scope eventQueue,it will not be a performance issue.
				sourceQueue.fireResourceChanged(resource, Type.Created);
			}
		}
		initSEOHandler(caseModel,desktop);

		// @see FiddleDispatcherFilter for those use this directly
		viewRequestParam = (CaseRequest) requestScope.get(FiddleConstant.REQUEST_ATTR_RUN_VIEW);
		if (viewRequestParam != null) {
			runDirectlyView(viewRequestParam);
		}
	}

	private void initTagEditor(final ICase pCase) {
		ICaseTagDao caseTagDao = (ICaseTagDao) SpringUtil.getBean("caseTagDao");
		//TODO check if we need to cache this.
		updateTags(caseTagDao.findTagsBy(pCase));

		EventListener handler = new EventListener() {

			public void onEvent(Event event) throws Exception {
				performUpdateTag();
			}
		};

		tagInput.addEventListener("onOK", handler);
		tagInput.addEventListener("onCancel", new EventListener() {
			public void onEvent(Event event) throws Exception {
				tagInput.setValue(lastVal);
				setTagEditable(false);
				event.stopPropagation();
			}
		});
	}

	private void setTagEditable(boolean bool) {

		// 2011/6/27:TonyQ
		// set visible twice for forcing smart update
		// sicne we set visible in client , so the visible state didn't sync
		// with server,
		// we need to make sure the server will really send the smartUpdate
		// messages. ;)
		editTag.setVisible(!bool);
		editTag.setVisible(bool); // actually we want editTag visible false

		viewTag.setVisible(bool);
		viewTag.setVisible(!bool); // actually we want viewTag visible true
	}

	private void performUpdateTag() {

		String val = tagInput.getValue();

		boolean valueChange = (lastVal == null || !val.equals(lastVal));
		// Do nothing if it didn't change
		if (valueChange) {
			ITagDao tagDao = (ITagDao) SpringUtil.getBean("tagDao");

			List<Tag> list = "".equals(val.trim()) ? new ArrayList<Tag>() : tagDao.prepareTags(val.split("[ ]*,[ ]*"));
			ICaseTagDao caseTagDao = (ICaseTagDao) SpringUtil.getBean("caseTagDao");
			caseTagDao.replaceTags(caseModel.getCurrentCase(), list);

			EventQueues.lookup(FiddleEventQueues.Tag).publish(new Event(FiddleEvents.ON_TAG_UPDATE, null));

			updateTags(list);
		}

		setTagEditable(false);
	}

	private void updateTags(List<Tag> list) {
		tagContainer.getChildren().clear();
		if (list.size() == 0) {
			tagEmpty.setVisible(true);
			cbSaveTag.setVisible(false);
		} else {
			StringBuffer sb = new StringBuffer();
			for (Tag tag : list) {
				A lbl = new A(tag.getName());
				lbl.setHref("/tag/" + tag.getName());
				lbl.setSclass("case-tag");
				sb.append(tag.getName() + ",");
				tagContainer.appendChild(lbl);
			}
			if (sb.length() != 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			tagInput.setValue(sb.toString());
			lastVal = sb.toString();
			tagEmpty.setVisible(false);
			cbSaveTag.setVisible(true);
		}
	}

	private void runDirectlyView(CaseRequest viewRequestParam) {

		FiddleSandbox sandbox = viewRequestParam.getFiddleSandbox();
		if (sandbox != null) { // inst can't be null
			// use echo event to find a good timing
			Events.echoEvent(new Event(FiddleEvents.ON_SHOW_RESULT, self, null));
		} else {
			alert("Can't find sandbox from specific version ");
		}
	}

	public void onLike$fblike(LikeEvent evt) {
		ICaseRecordDao manager = (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
		ICase $case = caseModel.getCurrentCase();
		if (evt.isLiked()) {
			if (logger.isDebugEnabled()) {
				logger.debug($case.getToken() + ":" + $case.getVersion() + ":like");
			}
			manager.increase(CaseRecord.Type.Like, $case);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug($case.getToken() + ":" + $case.getVersion() + ":unlike");
			}
			manager.decrease(CaseRecord.Type.Like, $case.getId());
		}
	}

	public void onShowResult(Event e) {
		if(viewRequestParam != null){
			FiddleSourceEventQueue.lookup().fireShowResult(caseModel.getCurrentCase(),
				viewRequestParam.getFiddleSandbox());
		}
	}

	public void onShowTryCase(Event e){
		FiddleSandboxManager manager = (FiddleSandboxManager) SpringUtil.getBean("sandboxManager");

		FiddleSandbox sandbox = manager.getFiddleSandboxForLastestVersion();
		if(sandbox == null){
			alert("Currently no any avaiable sandbox.");
			return ;
		}

		caseModel.ShowResult(sandbox);
	}

	public void onAdd$sourcetabs(Event e) {
		try {
			insertWin.doOverlapped();
		} catch (Exception e1) {
			logger.error("onAdd$sourcetabs(Event) - e=" + e, e1);
		}
	}

	private static void initSEOHandler(CaseModel model, Desktop desktop) {
		SEOUtils.render(desktop, model.getCurrentCase());
		SEOUtils.render(desktop,  model.getResources());
	}
}
