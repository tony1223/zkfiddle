package org.zkoss.fiddle.composer;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.composer.TopNavigationComposer.State;
import org.zkoss.fiddle.composer.event.FiddleEvents;
import org.zkoss.fiddle.composer.event.URLChangeEvent;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventListener;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventQueues;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleBrowserStateEventQueue;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleTopNavigationEventQueue;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.dao.api.ICaseTagDao;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.util.BrowserState;
import org.zkoss.fiddle.util.CaseUtil;
import org.zkoss.fiddle.util.TagUtil;
import org.zkoss.fiddle.visualmodel.TagCaseListVO;
import org.zkoss.fiddle.visualmodel.TagCloudVO;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.event.ZulEvents;

import ork.zkoss.fiddle.hyperlink.Hyperlink;

public class TagListComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = 8763856551822579585L;

	private Grid tagCaseList;

	private Paging tagCasePaging;
	private static final int pageSize = 20;;
	private Tag currentTag;

	private Caption tagCaption;
	
	private void setPage(int pageIndex, int pageSize) {
		ICaseTagDao caseTagDao = (ICaseTagDao) SpringUtil.getBean("caseTagDao");
		tagCaseList.setModel(new ListModelList(caseTagDao.findCaseRecordsBy(currentTag, pageIndex, pageSize)));
		tagCaseList.setAttribute("pagestart", (pageIndex-1) * pageSize );
		tagCasePaging.setActivePage(pageIndex - 1);
		tagCasePaging.setPageSize(pageSize);
		tagCasePaging.setTotalSize(caseTagDao.countCaseRecordsBy(currentTag).intValue());
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		updateTopNavigation();
		currentTag = (Tag) Executions.getCurrent().getAttribute(FiddleConstant.REQUEST_ATTR_TAG);
		if( currentTag == null ){
			Executions.getCurrent().sendRedirect("/");
			return ;
		}

		final int pageSize = 20;
		setPage(1, pageSize);
		tagCaption.setLabel("Tag: " + currentTag.getName());
		

		initTagListRenderer();
		
		tagCasePaging.addEventListener(ZulEvents.ON_PAGING, new EventListener() {

			public void onEvent(Event event) throws Exception {
				PagingEvent pagingEvt = (PagingEvent) event;
				setPage(pagingEvt.getActivePage() + 1, pageSize);
			}
		});
		initEventListenter();

	}
	private void initTagListRenderer(){
		tagCaseList.setRowRenderer(new RowRenderer() {

			public void render(Row row, Object data) throws Exception {
				final TagCaseListVO tclvo = (TagCaseListVO) data;

				{
					int index = row.getGrid().getRows().getChildren().indexOf(row) + 1;
					int pageStart = (Integer) row.getGrid().getAttribute("pagestart");
					Cell cell = new Cell();
					cell.setSclass("zkfiddle-index");
					Label lbl = new Label(String.valueOf((pageStart+index)));
					cell.appendChild(lbl);
					row.appendChild(cell);
				}

				{
					Div titlecont = new Div();
					Hyperlink titlelink = new Hyperlink(CaseUtil.getPublicTitle(tclvo.getCaseRecord()));
					titlelink.setHref(CaseUtil.getSampleURL(tclvo.getCaseRecord()));
					
					titlelink.addEventListener("onClick", new EventListener() {
						public void onEvent(Event event) throws Exception {
							CaseRecord record = tclvo.getCaseRecord();
							ICaseDao caseDao = (ICaseDao) SpringUtil.getBean("caseDao");
							ICase theCase = caseDao.get(record.getCaseId());
							BrowserState.go(CaseUtil.getSampleURL(theCase),
									"ZK Fiddle - "+ CaseUtil.getPublicTitle(record), theCase);
						}
					});
					titlecont.appendChild(titlelink);

					String token = tclvo.getCaseRecord().getToken() + "[" + tclvo.getCaseRecord().getVersion() + "]";
					Label lbl = new Label(token);
					lbl.setSclass("token");
					titlecont.appendChild(lbl);

					row.appendChild(titlecont);
				}
				{
					// implements tag cloud
					Div tagcont = new Div();
					tagcont.setSclass("tag-container");
					TagCloudVO tcvo = new TagCloudVO(tclvo.getTags());

					for (int i = 0, size = tclvo.getTags().size(); i < size; ++i) {
						final Tag tag = tclvo.getTags().get(i);
						Hyperlink taglink = new Hyperlink(tag.getName() +
								(tag.getAmount() > 1 ? "(" + tag.getAmount() + ") " :"") );
						if(currentTag.equals(tag)){
							taglink.setSclass("tag-cloud tag-cloud-sel tag-cloud"+tcvo.getLevel(tag.getAmount().intValue()));
						}else{
							taglink.setSclass("tag-cloud tag-cloud"+tcvo.getLevel(tag.getAmount().intValue()));
						}

						final String url = TagUtil.getViewURL(tag);
						taglink.setHref(url);
						taglink.addEventListener("onClick", new EventListener() {
							public void onEvent(Event event) throws Exception {
								BrowserState.go(url, "ZK Fiddle - Tag - "+ tag.getName() , tag);	
							}
						});
						tagcont.appendChild(taglink);
					}
					row.appendChild(tagcont);
				}

			}
		});

	}
	
	private void initEventListenter(){
		/**
		 * browser state , for chrome and firefox only
		 */
		FiddleBrowserStateEventQueue queue = FiddleBrowserStateEventQueue.lookup();
		queue.subscribe(new FiddleEventListener<URLChangeEvent>(
				URLChangeEvent.class,self) {

			public void onFiddleEvent(URLChangeEvent evt) throws Exception {
				// only work when updated to a case view.
				if (evt.getData() != null && evt.getData() instanceof Tag) {
					Tag _case = (Tag) evt.getData();
					currentTag = _case;
					setPage(1, pageSize);
					tagCaption.setLabel("Tag: " + currentTag.getName());
					
					updateTopNavigation();

					EventQueues.lookup(FiddleEventQueues.Tag).publish(
							new Event(FiddleEvents.ON_TAG_UPDATE, null, currentTag.getName()));
					
					EventQueue queue = EventQueues.lookup(FiddleEventQueues.LeftRefresh);
					queue.publish(new Event(FiddleEvents.ON_LEFT_REFRESH, null));					
				}
			}
		});
	}
	
	private void updateTopNavigation(){
		FiddleTopNavigationEventQueue.lookup().fireStateChange(State.Tag);
	}
}
