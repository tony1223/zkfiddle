package org.zkoss.fiddle.admin.composer;

import java.util.List;

import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.util.CaseUtil;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.A;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.event.PagingEvent;

public class CaseListComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = -8939520006607012735L;

	private Grid cases;
	private Paging casesPaging;
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		ICaseDao caseDao = (ICaseDao) SpringUtil.getBean("caseDao");
		List<Case> list = caseDao.list(0,30);
		cases.setModel(new ListModelList(list));
		
		cases.setRowRenderer(new RowRenderer() {
			public void render(Row row, Object data) throws Exception {
				Case aCase = (Case) data;
				
				int rowIndex = row.getParent().getChildren().indexOf(row);
				row.appendChild(new Label(String.valueOf(rowIndex)));
				
				A link = new A(CaseUtil.getPublicTitle(aCase));
				link.setHref(CaseUtil.getSampleURL(aCase));
				row.appendChild(link);
				row.appendChild(new Label(String.valueOf(aCase.getVersion())));
				
				row.appendChild(new Label(aCase.getCreateDate().toString()));
			}
		});
		
		casesPaging.setTotalSize(caseDao.size());
		
	}
	
	public void onPaging$casesPaging(PagingEvent e){
		ICaseDao caseDao = (ICaseDao) SpringUtil.getBean("caseDao");
		List<Case> list = caseDao.list(e.getActivePage(),30);
		cases.setModel(new ListModelList(list));
	}
	
}
