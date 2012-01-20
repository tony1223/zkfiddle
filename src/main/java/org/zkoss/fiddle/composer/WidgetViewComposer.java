package org.zkoss.fiddle.composer;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.codemirror.CodeEditor;
import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventListener;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventQueues;
import org.zkoss.fiddle.composer.viewmodel.CaseModel;
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.util.BookmarkUtil;
import org.zkoss.fiddle.util.CaseUtil;
import org.zkoss.fiddle.util.FiddleConfig;
import org.zkoss.fiddle.util.GAUtil;
import org.zkoss.fiddle.util.ResourceUtil;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.BookmarkEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

public class WidgetViewComposer extends GenericForwardComposer {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(WidgetViewComposer.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 7445220094351755044L;

	private CaseModel caseModel;

	private Tabs resourceTabs;

	private Tabpanels resourceTabpanels;

	private Window self;

	private Button goBtn;

	private Button runBtn;

	private Button downloadBtn;


	
	public void onBookmarkChange(BookmarkEvent event) throws Exception {
		EventQueues.lookup(FiddleEventQueues.Bookmark,true).publish(event);		
	}

	
	public void doAfterCompose(final Component comp) throws Exception {
		super.doAfterCompose(comp);

		int height = -1;
		try {
			String pheight = (String) Executions.getCurrent().getParameter("height");
			if (pheight != null)
				height = Integer.parseInt(pheight);
		} catch (Exception ex) {
			logger.error(ex);
		}


		Case $case = ((Case) requestScope.get(FiddleConstant.REQUEST_ATTR_CASE));
		caseModel = new CaseModel($case, false, null);
		self.setTitle("Sample Code:" + CaseUtil.getPublicTitle(caseModel.getCurrentCase()));

		ICaseRecordDao manager = (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
		if (logger.isDebugEnabled()) {
			logger.debug($case.getToken() + ":" + $case.getVersion() + ":view");
		}
		manager.increase(CaseRecord.Type.Widget, $case);

		renderTabAndTabpanels(caseModel.getResources(), height);


		String hostName = FiddleConfig.getHostName();

		String zkVer = (String) Executions.getCurrent().getParameter("zkVersion");
		runBtn.setHref(hostName + CaseUtil.getViewURL($case, zkVer)+"#"+desktop.getBookmark());
		goBtn.setHref(hostName + CaseUtil.getSampleURL($case)+"#"+desktop.getBookmark());
		downloadBtn.setHref(hostName + CaseUtil.getDownloadURL($case));
		
		initEventQueueListenter();
	}

	private void initEventQueueListenter(){
		EventQueues.lookup(FiddleEventQueues.Bookmark,true).subscribe(
				new	FiddleEventListener<BookmarkEvent>(BookmarkEvent.class,self) {
				public void onFiddleEvent(BookmarkEvent evt) throws Exception {
					int index = BookmarkUtil.getSourceIndex(evt.getBookmark());
					if(index != -1){
						@SuppressWarnings("unchecked")
						List<Tab> tabs = resourceTabs.getChildren();
						if(index >= 0 && index < tabs.size() ) {
							tabs.get(index).setSelected(true);
						}
					}
				}
			});		
	}
	public void onClick$downloadBtn(Event evt){
		GAUtil.logAction(FiddleConstant.GA_CATEGORY_SOURCE, "widget-download", caseModel.getCurrentCase().getCaseUrl());
	}

	public void onClick$goBtn(Event evt){
		GAUtil.logAction(FiddleConstant.GA_CATEGORY_SOURCE, "widget-go", caseModel.getCurrentCase().getCaseUrl());
	}

	public void onClick$runBtn(Event evt){
		GAUtil.logAction(FiddleConstant.GA_CATEGORY_SOURCE, "widget-run", caseModel.getCurrentCase().getCaseUrl());
	}
	
	private Tab preparedTab(String title,String type){
		final Tab tab = new Tab(title,"/img/types/"+type+".png");
		tab.addEventListener(Events.ON_SELECT, new EventListener() {
			public void onEvent(Event event) throws Exception {
				int ind = resourceTabs.getChildren().indexOf(tab);
				if(ind != -1 ){
					Executions.getCurrent().getDesktop().setBookmark(BookmarkUtil.SOURCE_PREFIX + (ind+1) );
				}
			}
		});
		return tab;
	}

	private void renderTabAndTabpanels(List<Resource> list, int height) {
		for (Resource resource : list) {
			{
				String type = ResourceUtil.getTypeName(resource);
				String tabTitle =  " " + resource.getName();
				resourceTabs.appendChild(preparedTab(tabTitle,"/img/types/"+type+".png"));
			}
			{
				Tabpanel tabpanel = new Tabpanel();
				{
					CodeEditor editor = new CodeEditor(ResourceUtil.getTypeMode(resource));
					editor.setReadOnly(true);
					editor.setTheme("eclipse");
					editor.setValue(resource.getFullContent());

					if (height != -1) {
						editor.setStyle("overflow:auto;height:" + (height - 50) + "px");
					}
					tabpanel.appendChild(editor);
				}
				resourceTabpanels.appendChild(tabpanel);
			}
		}
	}

}
