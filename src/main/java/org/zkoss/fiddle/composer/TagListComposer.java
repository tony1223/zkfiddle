package org.zkoss.fiddle.composer;

import java.net.URLEncoder;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.composer.TopNavigationComposer.State;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleTopNavigationEventQueue;
import org.zkoss.fiddle.dao.api.ICaseTagDao;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.util.CaseUtil;
import org.zkoss.fiddle.visualmodel.TagCaseListVO;
import org.zkoss.fiddle.visualmodel.TagCloudVO;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.A;
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

	private void setPage(Tag t, int pageIndex, int pageSize) {
		ICaseTagDao caseTagDao = (ICaseTagDao) SpringUtil.getBean("caseTagDao");
		tagCaseList.setModel(new ListModelList(caseTagDao.findCaseRecordsBy(t, pageIndex, pageSize)));
		tagCasePaging.setActivePage(pageIndex - 1);
		tagCasePaging.setPageSize(pageSize);
		tagCasePaging.setTotalSize(caseTagDao.countCaseRecordsBy(t).intValue());
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		updateTopNavigation();
		final Tag t = (Tag) Executions.getCurrent().getAttribute(FiddleConstant.REQUEST_ATTR_TAG);
		if( t == null ){
			Executions.getCurrent().sendRedirect("/");
			return ;
		}

		final int pageIndex = 1, pageSize = 20;

		setPage(t, pageIndex, pageSize);

		tagCasePaging.addEventListener(ZulEvents.ON_PAGING, new EventListener() {

			public void onEvent(Event event) throws Exception {
				PagingEvent pagingEvt = (PagingEvent) event;
				setPage(t, pagingEvt.getActivePage() + 1, pageSize);
			}
		});

		tagCaseList.setRowRenderer(new RowRenderer() {

			public void render(Row row, Object data) throws Exception {
				TagCaseListVO tclvo = (TagCaseListVO) data;

				{
					int index = row.getGrid().getRows().getChildren().indexOf(row) + 1;
					Cell cell = new Cell();
					cell.setSclass("zkfiddle-index");
					Label lbl = new Label(String.valueOf(index));
					cell.appendChild(lbl);
					row.appendChild(cell);
				}

				{
					Div titlecont = new Div();
					Hyperlink titlelink = new Hyperlink(CaseUtil.getPublicTitle(tclvo.getCaseRecord()));
					titlelink.setHref(CaseUtil.getSampleURL(tclvo.getCaseRecord()));
					titlelink.setDisableHref(true);
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
						Tag tag = tclvo.getTags().get(i);
						A taglink = new A(tag.getName() +
								(tag.getAmount() > 1 ? "(" + tag.getAmount() + ")" :"") );
						if(t.equals(tag)){
							taglink.setSclass("tag-cloud tag-cloud-sel tag-cloud"+tcvo.getLevel(tag.getAmount().intValue()));
						}else{
							taglink.setSclass("tag-cloud tag-cloud"+tcvo.getLevel(tag.getAmount().intValue()));
						}

						taglink.setHref("/tag/" + URLEncoder.encode(tag.getName(), "UTF-8"));
						tagcont.appendChild(taglink);
					}
					row.appendChild(tagcont);
				}

			}
		});

	}
	
	private void updateTopNavigation(){
		FiddleTopNavigationEventQueue.lookup().fireStateChange(State.Tag);
	}
}
