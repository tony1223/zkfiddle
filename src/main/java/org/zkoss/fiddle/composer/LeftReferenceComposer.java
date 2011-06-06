package org.zkoss.fiddle.composer;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.zkoss.fiddle.dao.CaseRecordDaoImpl;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

public class LeftReferenceComposer extends GenericForwardComposer {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(LeftReferenceComposer.class);

	private Listbox likes;
	private Window aboutContent;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		CaseRecordDaoImpl caseRecordDao = new CaseRecordDaoImpl();
		List<CaseRecord> list = caseRecordDao.listByType(CaseRecord.TYPE_LIKE, true, 1, 50);
		likes.setModel(new ListModelList(list));
		
		likes.setItemRenderer( new ListitemRenderer() {
			public void render(Listitem item, Object data) throws Exception {
				if( data instanceof CaseRecord){
					CaseRecord cr = (CaseRecord) data;
					String title = (cr.getTitle() == null || "".equals(cr.getTitle())) ? cr.getToken() : cr.getToken();
					item.appendChild(new Listcell("" + (item.getIndex() +1) ));
					item.appendChild(new Listcell("" + title));
					item.appendChild(new Listcell("" + cr.getAmount()));
					item.setValue(cr);
				}else{
					throw new IllegalArgumentException("data should be CaseRecord!"+data);
				}
				
			}
		});
		
	}
	
	public void onSelect$likes(Event e){
		CaseRecord cr = (CaseRecord) likes.getSelectedItem().getValue();
		Executions.sendRedirect("/sample/" + cr.getCaseUrl());
	}
	
	public void onClick$abouttab(Event e){
		aboutContent.setVisible(true);
		try {
			aboutContent.doModal();
		} catch (SuspendNotAllowedException e1) {
			if(logger.isEnabledFor(Level.ERROR))
				logger.error("onClick$abouttab(Event)", e1);
		} catch (InterruptedException e1) {
			if(logger.isEnabledFor(Level.ERROR))
				logger.error("onClick$abouttab(Event)", e1);
		}
	}
	
	public void onClose$aboutContent(Event e){
		if(e instanceof ForwardEvent){
			e = ((ForwardEvent)e).getOrigin();
		}
		aboutContent.setVisible(false);
		e.stopPropagation();
		
	}
}
