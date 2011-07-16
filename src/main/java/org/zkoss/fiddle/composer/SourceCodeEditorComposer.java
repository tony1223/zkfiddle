package org.zkoss.fiddle.composer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.fiddle.component.renderer.SourceTabRendererFactory;
import org.zkoss.fiddle.composer.context.WorkbenchContext;
import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.FiddleEvents;
import org.zkoss.fiddle.composer.event.InsertResourceEvent;
import org.zkoss.fiddle.composer.event.SaveCaseEvent;
import org.zkoss.fiddle.composer.event.ShowResultEvent;
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.dao.api.ICaseTagDao;
import org.zkoss.fiddle.dao.api.ITagDao;
import org.zkoss.fiddle.fiddletabs.Fiddletabs;
import org.zkoss.fiddle.manager.CaseManager;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.fiddle.visualmodel.ViewRequest;
import org.zkoss.social.facebook.event.LikeEvent;
import org.zkoss.zk.ui.Component;
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
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SourceCodeEditorComposer.class);

//	public List<Resource> resources;

	private Fiddletabs sourcetabs;

	private Tabpanels sourcetabpanels;

//	private ICase $case;

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
	


	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		final WorkbenchContext wbCtxt = WorkbenchContext.getInstance();
		boolean newCase = wbCtxt.isStartWithNewCase();
		
		if (!newCase) {
			ICase $case = wbCtxt.getCurrentCase();
			caseTitle.setValue($case.getTitle());
			download.setHref("/download/"+$case.getToken() + "/" + $case.getVersion());
			caseToolbar.setVisible(true);
			initTagEditor($case);
		}
		wbCtxt.subscribeResourceCreated(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if (event instanceof InsertResourceEvent) {
					InsertResourceEvent insertEvent = (InsertResourceEvent) event;
					IResource ir = insertEvent.getResource();
					SourceTabRendererFactory.getRenderer(ir.getType()).appendSourceTab(
							sourcetabs, 
							sourcetabpanels, 
							ir);
				} 
			}
		});		
		wbCtxt.subscribeResourceSaved(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if (event instanceof SaveCaseEvent) {
					SaveCaseEvent saveEvt = (SaveCaseEvent) event;
					CaseManager caseManager = (CaseManager) SpringUtil.getBean("caseManager");
					String ip = Executions.getCurrent().getRemoteAddr();
					ICase saved = caseManager.saveCase(wbCtxt.getCurrentCase(), 
							wbCtxt.getResources(), 
							caseTitle.getValue(), 
							saveEvt.isFork(), 
							ip,
							cbSaveTag.isChecked());
					if (saved != null) {
						Executions.getCurrent().sendRedirect(
								"/sample/" + saved.getToken() + "/" + saved.getVersion() + saved.getURLFriendlyTitle());
					}
				} 
			}
		});
		
		
		for (IResource resource : wbCtxt.getResources()) {
			SourceTabRendererFactory.getRenderer(resource.getType()).
				appendSourceTab(sourcetabs, sourcetabpanels,resource);
			if (newCase) {
				// Notify content to do some processing,since we use desktop
				// scope eventQueue,it will not be a performance issue.
				wbCtxt.fireResourceChanged(resource);
			}
		}
		// @see FiddleDispatcherFilter for those use this directly
		ViewRequest viewRequestParam = (ViewRequest) requestScope.get("runview");
		if (viewRequestParam != null) {
			runDirectlyView(viewRequestParam);
		}
	}
	
	private void initTagEditor(final ICase $case){
		ICaseTagDao caseTagDao = (ICaseTagDao) SpringUtil.getBean("caseTagDao");
		List<Tag> list = caseTagDao.findTagsBy($case, 1, 30);
		updateTags(list);
		
		EventListener handler = new EventListener() {
			public void onEvent(Event event) throws Exception {
				performUpdateTag();
			}
		};
		
		tagInput.addEventListener("onOK",handler);
		tagInput.addEventListener("onCancel",new EventListener() {
			public void onEvent(Event event) throws Exception {
				tagInput.setValue(lastVal);
				setTagEditable(false);
				event.stopPropagation();
			}
		});
	}
	
	private void setTagEditable(boolean bool){
		
		//2011/6/27:TonyQ 
		//set visible twice for forcing smart update
		//sicne we set visible in client , so the visible state didn't sync with server,
		//we need to make sure the server will really send the smartUpdate messages. ;)		
		editTag.setVisible(!bool);
		editTag.setVisible(bool); //actually we want editTag visible false
		
		viewTag.setVisible(bool);
		viewTag.setVisible(!bool); //actually we want viewTag visible true
	}
	
	private void performUpdateTag(){
		
		String val = tagInput.getValue();
		
		boolean valueChange = ( lastVal == null || !val.equals(lastVal));
		//Do nothing if it didn't change
		if(valueChange){
			ITagDao tagDao = (ITagDao) SpringUtil.getBean("tagDao");
			
			List<Tag> list = "".equals(val.trim()) ? new ArrayList<Tag>() : 
					tagDao.prepareTags(val.split("[ ]*,[ ]*"));
			ICaseTagDao caseTagDao = (ICaseTagDao) SpringUtil.getBean("caseTagDao");
			caseTagDao.replaceTags(
					WorkbenchContext.getInstance().getCurrentCase(), list);
			
			EventQueues.lookup(FiddleEventQueues.Tag).
				publish(new Event(FiddleEvents.ON_TAG_UPDATE,null));
			
			updateTags(list);
		}
		
		setTagEditable(false);
	}
	
	private void updateTags(List<Tag> list){
		tagContainer.getChildren().clear();		
		if(list.size() == 0){
			tagEmpty.setVisible(true);
			cbSaveTag.setVisible(false);
		}else{
			StringBuffer sb = new StringBuffer();
			for(Tag tag:list){
				A lbl = new A(tag.getName());
				lbl.setHref("/tag/"+tag.getName());
				lbl.setSclass("case-tag");
				sb.append(tag.getName()+",");
				tagContainer.appendChild(lbl);
			}
			if(sb.length()!=0){
				sb.deleteCharAt(sb.length()-1);
			}
			tagInput.setValue(sb.toString());
			lastVal = sb.toString();
			tagEmpty.setVisible(false);
			cbSaveTag.setVisible(true);
		}
	}
	
	private void runDirectlyView(ViewRequest viewRequestParam){

		FiddleSandbox inst = viewRequestParam.getFiddleInstance();
		if (inst != null) { // inst can't be null
			// use echo event to find a good timing
			ShowResultEvent sv = new ShowResultEvent(FiddleEvents.ON_SHOW_RESULT, 
				WorkbenchContext.getInstance().getCurrentCase(), 
				viewRequestParam.getFiddleInstance());
			
			Events.echoEvent(new Event(FiddleEvents.ON_SHOW_RESULT, self, sv));
		} else {
			alert("Can't find sandbox from specific version ");
		}
	}
	
	public void onLike$fblike(LikeEvent evt) {
		ICaseRecordDao manager = (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
		ICase $case = WorkbenchContext.getInstance().getCurrentCase();
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
		EventQueues.lookup(FiddleEventQueues.SHOW_RESULT, true).publish((ShowResultEvent) e.getData());
	}


	
	public void onAdd$sourcetabs(Event e) {
		try {
			insertWin.doOverlapped();		
		} catch (Exception e1) {
			logger.error("onAdd$sourcetabs(Event) - e=" + e, e1);
		}
	}

}
