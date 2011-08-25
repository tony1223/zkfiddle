package org.zkoss.fiddle.composer;

import java.util.List;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.composer.TopNavigationComposer.State;
import org.zkoss.fiddle.composer.event.FiddleEvents;
import org.zkoss.fiddle.composer.event.URLChangeEvent;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventListener;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventQueues;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleBrowserStateEventQueue;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleTopNavigationEventQueue;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.util.BrowserState;
import org.zkoss.fiddle.util.CaseUtil;
import org.zkoss.fiddle.util.SEOUtils;
import org.zkoss.fiddle.visualmodel.UserVO;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
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

public class UserListComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = 8763856551822579585L;

	private Grid userCaseList;

	private Paging userCasePaging;

	private static final int pageSize = 10;

	private String userName;

	private Boolean isGuest;

	private Caption userCaption;

	//TODO add page index to URL
	private List<Case> updatePage(int pageIndex, int pageSize) {
		ICaseDao caseDao = (ICaseDao) SpringUtil.getBean("caseDao");
		
		List<Case> cases = caseDao.findByAuthor(userName, isGuest, pageIndex, pageSize);
		userCaseList.setModel(new ListModelList(cases));
		userCaseList.setAttribute("pagestart", (pageIndex - 1) * pageSize);
		userCasePaging.setActivePage(pageIndex - 1);
		userCasePaging.setPageSize(pageSize);
		userCasePaging.setTotalSize(caseDao.countByAuthor(userName, isGuest));
		
		return cases;
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		updateTopNavigation();
		userName = (String) Executions.getCurrent().getAttribute(FiddleConstant.REQUEST_ATTR_USERNAME);
		isGuest = (Boolean) Executions.getCurrent().getAttribute(FiddleConstant.REQUEST_ATTR_GUEST);
		if (userName == null) {
			Execution exec =Executions.getCurrent();
			exec.sendRedirect(exec.getContextPath());
			return;
		}
		initUserList();
		initEventQueue();
		updateUser(true);

	}

	private void updateUser(boolean initSEO){
		List<Case> cases = updatePage(1, pageSize);
		if(initSEO){
			for(Case cas:cases){
				SEOUtils.render(desktop, cas);
			}
		}
		
		userCaption.setLabel("User: "+ userName );
	}

	private void initUserList() {
		userCaseList.setRowRenderer(new RowRenderer() {

			public void render(Row row, Object data) throws Exception {
				final Case theCase = (Case) data;

				{
					int index = row.getGrid().getRows().getChildren().indexOf(row) + 1;
					int pageStart = (Integer) row.getGrid().getAttribute("pagestart");
					Cell cell = new Cell();
					cell.setSclass("zkfiddle-index");
					Label lbl = new Label(String.valueOf(pageStart + index));
					cell.appendChild(lbl);
					row.appendChild(cell);
				}

				{
					Div titlecont = new Div();
					Hyperlink titlelink = new Hyperlink(CaseUtil.getPublicTitle(theCase));
					titlelink.setHref(CaseUtil.getSampleURL(theCase));

					titlelink.addEventListener("onClick", new EventListener() {
						public void onEvent(Event event) throws Exception {
							//TODO clean all the title string and generating them in a util methd.
							//"ZK Fiddle - "
							BrowserState.go(CaseUtil.getSampleURL(theCase),
									"ZK Fiddle - " + CaseUtil.getPublicTitle(theCase), theCase);
						}
					});
					titlecont.appendChild(titlelink);

					String token = theCase.getToken() + "[" + theCase.getVersion() + "]";
					Label lbl = new Label(token);
					lbl.setSclass("token");
					titlecont.appendChild(lbl);

					row.appendChild(titlecont);
				}
				{
					row.appendChild(new Label(theCase.getCreateDate().toString()));
				}

				//TODO review and clean this.
				// {
				// // implements tag cloud
				// Div tagcont = new Div();
				// tagcont.setSclass("tag-container");
				// TagCloudVO tcvo = new TagCloudVO(tclvo.getTags());
				//
				// for (int i = 0, size = tclvo.getTags().size(); i < size; ++i)
				// {
				// final Tag tag = tclvo.getTags().get(i);
				// Hyperlink taglink = new Hyperlink(tag.getName() +
				// (tag.getAmount() > 1 ? "(" + tag.getAmount() + ") " :"") );
				// if(currentTag.equals(tag)){
				// taglink.setSclass("tag-cloud tag-cloud-sel tag-cloud"+tcvo.getLevel(tag.getAmount().intValue()));
				// }else{
				// taglink.setSclass("tag-cloud tag-cloud"+tcvo.getLevel(tag.getAmount().intValue()));
				// }
				//
				// final String url = TagUtil.getViewURL(tag);
				// taglink.setHref(url);
				// taglink.addEventListener("onClick", new EventListener() {
				// public void onEvent(Event event) throws Exception {
				// BrowserState.go(url, "ZK Fiddle - Tag - "+ tag.getName() ,
				// tag);
				// }
				// });
				// tagcont.appendChild(taglink);
				// }
				// row.appendChild(tagcont);
				// }

			}
		});
		userCasePaging.addEventListener(ZulEvents.ON_PAGING, new EventListener() {

			public void onEvent(Event event) throws Exception {
				PagingEvent pagingEvt = (PagingEvent) event;
				updatePage(pagingEvt.getActivePage() + 1, pageSize);
			}
		});
	}

	private void initEventQueue() {
		/**
		 * browser state , for chrome and firefox only
		 */
		FiddleBrowserStateEventQueue queue = FiddleBrowserStateEventQueue.lookup();
		queue.subscribe(new FiddleEventListener<URLChangeEvent>(URLChangeEvent.class, self) {

			public void onFiddleEvent(URLChangeEvent evt) throws Exception {
				// only work when updated to a user view.
				if(evt.getData() instanceof UserVO){
					UserVO user = (UserVO) evt.getData();
					userName = user.getUserName();
					isGuest = user.isGuest();

					updateUser(false);
					updateTopNavigation();

					EventQueues.lookup(FiddleEventQueues.Tag).publish(new Event(FiddleEvents.ON_TAG_UPDATE, null, null));

					EventQueue queue = EventQueues.lookup(FiddleEventQueues.LeftRefresh);
					queue.publish(new Event(FiddleEvents.ON_LEFT_REFRESH, null));
				}
			}
		});
	}

	private void updateTopNavigation() {
		FiddleTopNavigationEventQueue.lookup().fireStateChange(State.User);
	}
}
