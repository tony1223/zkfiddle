package org.zkoss.fiddle.composer;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.composer.TopNavigationComposer.State;
import org.zkoss.fiddle.composer.event.FiddleEvents;
import org.zkoss.fiddle.composer.event.ToUserURLEvent;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventListener;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventQueues;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleBrowserStateEventQueue;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleTopNavigationEventQueue;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.util.BrowserState;
import org.zkoss.fiddle.util.CaseUtil;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
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

	private static final int pageSize = 20;;

	private String userName;

	private Boolean isGuest;

	private void setPage(int pageIndex, int pageSize) {
		ICaseDao caseDao = (ICaseDao) SpringUtil.getBean("caseDao");
		userCaseList.setModel(new ListModelList(caseDao.findByAuthor(userName, isGuest, pageIndex, pageSize)));
		userCaseList.setAttribute("pagestart", (pageIndex - 1) * pageSize);
		userCasePaging.setActivePage(pageIndex - 1);
		userCasePaging.setPageSize(pageSize);
		userCasePaging.setTotalSize(caseDao.countByAuthor(userName, isGuest));
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		updateTopNavigation();
		userName = (String) Executions.getCurrent().getAttribute(FiddleConstant.REQUEST_ATTR_USERNAME);
		isGuest = (Boolean) Executions.getCurrent().getAttribute(FiddleConstant.REQUEST_ATTR_GUEST);

		if (userName == null) {
			Executions.getCurrent().sendRedirect("/");
			return;
		}

		final int pageSize = 20;
		setPage(1, pageSize);

		initUserListRenderer();

		userCasePaging.addEventListener(ZulEvents.ON_PAGING, new EventListener() {

			public void onEvent(Event event) throws Exception {
				PagingEvent pagingEvt = (PagingEvent) event;
				setPage(pagingEvt.getActivePage() + 1, pageSize);
			}
		});
		initEventListenter();

	}

	private void initUserListRenderer() {
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

	}

	private void initEventListenter() {
		/**
		 * browser state , for chrome and firefox only
		 */
		FiddleBrowserStateEventQueue queue = FiddleBrowserStateEventQueue.lookup();
		queue.subscribe(new FiddleEventListener<ToUserURLEvent>(ToUserURLEvent.class, self) {

			public void onFiddleEvent(ToUserURLEvent evt) throws Exception {
				// only work when updated to a case view.
				userName = evt.getUserName();
				isGuest = evt.isGuest();

				setPage(1, pageSize);
				updateTopNavigation();

				EventQueues.lookup(FiddleEventQueues.Tag).publish(new Event(FiddleEvents.ON_TAG_UPDATE, null, null));

				EventQueue queue = EventQueues.lookup(FiddleEventQueues.LeftRefresh);
				queue.publish(new Event(FiddleEvents.ON_LEFT_REFRESH, null));
			}
		});
	}

	private void updateTopNavigation() {
		FiddleTopNavigationEventQueue.lookup().fireStateChange(State.User);
	}
}
