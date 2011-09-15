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
import org.zkoss.fiddle.composer.viewmodel.URLData;
import org.zkoss.fiddle.dao.api.ICaseTagDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.util.BrowserStateUtil;
import org.zkoss.fiddle.util.CaseUtil;
import org.zkoss.fiddle.util.SEOUtils;
import org.zkoss.fiddle.util.TagUtil;
import org.zkoss.fiddle.util.UserUtil;
import org.zkoss.fiddle.visualmodel.TagCaseListVO;
import org.zkoss.fiddle.visualmodel.TagCloudVO;
import org.zkoss.fiddle.visualmodel.UserVO;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
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

	// TODO add page index to URL
	private List<TagCaseListVO> updatePage(int pageIndex, int pageSize) {
		ICaseTagDao caseTagDao = (ICaseTagDao) SpringUtil.getBean("caseTagDao");

		List<TagCaseListVO> list = caseTagDao.findCaseListsBy(currentTag,
				pageIndex, pageSize, true);
		tagCaseList.setModel(new ListModelList(list));
		tagCaseList.setAttribute("pagestart", (pageIndex - 1) * pageSize);
		tagCasePaging.setActivePage(pageIndex - 1);
		tagCasePaging.setPageSize(pageSize);
		tagCasePaging.setTotalSize(caseTagDao.countCaseRecordsBy(currentTag)
				.intValue());

		return list;
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		updateTopNavigation();
		currentTag = (Tag) Executions.getCurrent().getAttribute(
				FiddleConstant.REQUEST_ATTR_TAG);
		if (currentTag == null) {
			Executions.getCurrent().sendRedirect("/");
			return;
		}

		final int pageSize = 20;
		List<TagCaseListVO> cases = updatePage(1, pageSize);

		initSEOHandler(currentTag, cases, desktop);

		tagCaption.setLabel("Tag: " + currentTag.getName());

		initTagListRenderer();

		tagCasePaging.addEventListener(ZulEvents.ON_PAGING,
				new EventListener() {

					public void onEvent(Event event) throws Exception {
						PagingEvent pagingEvt = (PagingEvent) event;
						updatePage(pagingEvt.getActivePage() + 1, pageSize);
					}
				});
		initEventListenter();

	}

	private void initTagListRenderer() {
		tagCaseList.setRowRenderer(new RowRenderer() {

			public void render(Row row, Object data) throws Exception {
				final TagCaseListVO tagListVO = (TagCaseListVO) data;

				{
					int index = row.getGrid().getRows().getChildren()
							.indexOf(row) + 1;
					int pageStart = (Integer) row.getGrid().getAttribute(
							"pagestart");
					Cell cell = new Cell();
					cell.setSclass("zkfiddle-index");
					Label lbl = new Label(String.valueOf((pageStart + index)));
					cell.appendChild(lbl);
					row.appendChild(cell);
				}

				{
					Div titlecont = new Div();
					Hyperlink titlelink = new Hyperlink(CaseUtil
							.getPublicTitle(tagListVO.getCase()));
					titlelink.setHref(CaseUtil.getSampleURL(tagListVO.getCase()));

					titlelink.addEventListener("onClick", new EventListener() {
						public void onEvent(Event event) throws Exception {
							Case theCase = tagListVO.getCase();
							BrowserStateUtil.go(theCase);
						}
					});
					titlecont.appendChild(titlelink);

					String token = tagListVO.getCase().getToken() + "["
							+ tagListVO.getCase().getVersion() + "]";
					Label lbl = new Label(token);
					lbl.setSclass("token");
					titlecont.appendChild(lbl);

					row.appendChild(titlecont);
				}
				{
					// implements tag cloud
					Div tagcont = new Div();
					tagcont.setSclass("tag-container");
					TagCloudVO tcvo = new TagCloudVO(tagListVO.getTags());

					// FIXME: The tag is blocked , fix this later
					for (int i = 0, size = tagListVO.getTags().size(); i < size; ++i) {
						final Tag tag = tagListVO.getTags().get(i);
						Hyperlink taglink = new Hyperlink(tag.getName()
								+ (tag.getAmount() > 1 ? "(" + tag.getAmount()
										+ ") " : ""));
						if (currentTag.equals(tag)) {
							taglink.setSclass("tag-cloud tag-cloud-sel tag-cloud"
									+ tcvo.getLevel(tag.getAmount().intValue()));
						} else {
							taglink.setSclass("tag-cloud tag-cloud"
									+ tcvo.getLevel(tag.getAmount().intValue()));
						}

						final String url = TagUtil.getViewURL(tag);
						taglink.setHref(url);
						taglink.addEventListener(Events.ON_CLICK,
								new EventListener() {
									public void onEvent(Event event)
											throws Exception {
										BrowserStateUtil.go(tag);
									}
								});
						tagcont.appendChild(taglink);
					}
					row.appendChild(tagcont);
				}
				// TODO replace all "onClick" to Events.ON_CLICK
				{

					Hyperlink link = new Hyperlink(tagListVO.getCase()
							.getAuthorName());
					final UserVO userVO = new UserVO(tagListVO.getCase());
					link.setHref(UserUtil.getUserView(userVO));
					link.addEventListener(Events.ON_CLICK, new EventListener() {
						public void onEvent(Event event) throws Exception {
							BrowserStateUtil.go(userVO);
						}
					});
					if (userVO.isGuest()) {
						link.setSclass("guest-user");
					}
					row.appendChild(link);
				}
				{
					row.appendChild(new Label(tagListVO.getCase()
							.getCreateDate().toString()));
				}
			}
		});

	}

	private void initEventListenter() {
		/**
		 * browser state , for chrome and firefox only
		 */
		FiddleBrowserStateEventQueue queue = FiddleBrowserStateEventQueue
				.lookup();
		queue.subscribe(new FiddleEventListener<URLChangeEvent>(
				URLChangeEvent.class, self) {

			public void onFiddleEvent(URLChangeEvent evt) throws Exception {
				// only work when updated to a case view.
				URLData data = (URLData) evt.getData();

				if (data == null ){
					throw new IllegalStateException("not expected type");
				}else if(FiddleConstant.URL_DATA_TAG_VIEW.equals(data.getType())) {
					Tag _case = (Tag) data.getData();
					currentTag = _case;
					updatePage(1, pageSize);
					tagCaption.setLabel("Tag: " + currentTag.getName());

					updateTopNavigation();

					EventQueues.lookup(FiddleEventQueues.Tag).publish(
							new Event(FiddleEvents.ON_TAG_UPDATE, null,
									currentTag.getName()));

					EventQueue queue = EventQueues
							.lookup(FiddleEventQueues.LeftRefresh);
					queue.publish(new Event(FiddleEvents.ON_LEFT_REFRESH, null));
				}
			}
		});
	}

	private static void initSEOHandler(Tag tag, List<TagCaseListVO> caseList,
			Desktop desktop) {
		SEOUtils.render(desktop, tag);

		for (TagCaseListVO tagCase : caseList) {
			SEOUtils.render(desktop, tagCase.getCase());
		}

	}

	private void updateTopNavigation() {
		FiddleTopNavigationEventQueue.lookup().fireStateChange(State.Tag);
	}
}
