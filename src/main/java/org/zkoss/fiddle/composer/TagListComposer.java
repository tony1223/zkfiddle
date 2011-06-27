package org.zkoss.fiddle.composer;

import java.net.URLEncoder;

import org.zkoss.fiddle.dao.api.ICaseTagDao;
import org.zkoss.fiddle.dao.api.ITagDao;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.visualmodel.TagCaseListVO;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
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


public class TagListComposer extends GenericForwardComposer{

	private Grid tagCaseList;
	private Paging tagCasePaging;
	
	private void setPage(Tag t,int pageIndex,int pageSize){
		ICaseTagDao caseTagDao = (ICaseTagDao) SpringUtil.getBean("caseTagDao");
		tagCaseList.setModel(new ListModelList(caseTagDao.findCaseRecordsBy(t, pageIndex, pageSize)));
		tagCasePaging.setActivePage(pageIndex - 1);
		tagCasePaging.setPageSize(pageSize);
		tagCasePaging.setTotalSize(caseTagDao.countCaseRecordsBy(t).intValue());
	}
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		ITagDao tagDao = (ITagDao) SpringUtil.getBean("tagDao");
		final Tag t = tagDao.getTag((String)requestScope.get("tag"));
		
		final int pageIndex = 1 , pageSize = 20 ;
		
		setPage(t,pageIndex,pageSize);
		
		tagCasePaging.addEventListener(ZulEvents.ON_PAGING,new EventListener() {
			public void onEvent(Event event) throws Exception {
				PagingEvent pagingEvt = (PagingEvent) event;
				setPage(t, pagingEvt.getActivePage() +1 , pageSize);
			}
		});
		
		tagCaseList.setRowRenderer(new RowRenderer() {
			
			public void render(Row row, Object data) throws Exception {
				TagCaseListVO tclvo = (TagCaseListVO) data;
				
				{
					int index = row.getGrid().getRows().getChildren().indexOf(row) +1 ;
					Cell cell = new Cell();
					cell.setSclass("zkfiddle-index");
					Label lbl = new Label(String.valueOf(index));
					cell.appendChild(lbl);
					row.appendChild(cell);
				}

				{
					Div titlecont= new Div();
					String title = tclvo.getCaseRecord().getTitle();
					title = (title == null || "".equals(title.trim()) ? "unnamed" : title);					
					A titlelink = new A(title);
					titlelink.setHref("/sample/" + tclvo.getCaseRecord().getCaseUrl());
					titlecont.appendChild(titlelink);
					
					String token = tclvo.getCaseRecord().getToken()+"[" + tclvo.getCaseRecord().getVersion()+"]";
					Label lbl = new Label(token);
					lbl.setSclass("token");
					titlecont.appendChild(lbl);
					
					row.appendChild(titlecont);
				}
				{
					Div tagcont= new Div();
					tagcont.setSclass("tag-container");
					for (int i = 0 , size = tclvo.getTags().size() ; i < size;++i) {
						Tag tag = tclvo.getTags().get(i);
						A taglink = new A(tag.getName());
						taglink.setHref("/tag/"+URLEncoder.encode(tag.getName(),"UTF-8"));
						tagcont.appendChild(taglink);
						if(i != size -1){
							tagcont.appendChild(new Label(","));
						}
					}
					row.appendChild(tagcont);
				}

			}
		});
		
	}
}
