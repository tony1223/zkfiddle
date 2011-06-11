package org.zkoss.fiddle.composer;

import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.util.CacheFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
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

	private Listbox recentlys;

	private Window aboutContent;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		ICaseRecordDao caseRecordDao =  (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
		
		Cache cache = CacheFactory.getTop10LikedRecord();
		
		List<CaseRecord> list = null;
		String key = CaseRecord.TYPE_LIKE+":"+ true+":"+ 1+":"+ 50;
		if(cache.isKeyInCache(key)){
			if (logger.isDebugEnabled()) {
				logger.debug("doAfterCompose(Component) - Hit cache top 10 like");
			}
			list = (List<CaseRecord>) cache.get(key).getValue();
		}else{
			list = caseRecordDao.listByType(CaseRecord.TYPE_LIKE, true, 1, 50);	
			cache.put(new Element(key,list));
		}
		
		likes.setModel(new ListModelList(list));

		likes.setItemRenderer(new ListitemRenderer() {

			public void render(Listitem item, Object data) throws Exception {
				if (data instanceof CaseRecord) {
					CaseRecord cr = (CaseRecord) data;
					String title = (cr.getTitle() == null || "".equals(cr.getTitle())) ? cr.getToken() : cr.getTitle();
					item.appendChild(new Listcell(String.valueOf((item.getIndex() + 1))));
					item.appendChild(new Listcell(String.valueOf(title)));

					Listcell list = new Listcell(String.valueOf(cr.getAmount()));
					list.setSclass("amount");
					item.appendChild(list);
					item.setValue(cr);
				} else {
					throw new IllegalArgumentException("data should be CaseRecord!" + data);
				}

			}
		});

		ICaseDao caseDao = (ICaseDao) SpringUtil.getBean("caseDao");

		List<Case> recentlyList = caseDao.getRecentlyCase(10);
		recentlys.setModel(new ListModelList(recentlyList));

		recentlys.setItemRenderer(new ListitemRenderer() {

			public void render(Listitem item, Object data) throws Exception {
				if (data instanceof Case) {
					Case cr = (Case) data;
					String title = (cr.getTitle() == null || "".equals(cr.getTitle())) ? cr.getToken() : cr.getTitle();
					item.appendChild(new Listcell(String.valueOf((item.getIndex() + 1))));
					item.appendChild(new Listcell(String.valueOf(title)));
					
					Listcell list = new Listcell(String.valueOf(cr.getVersion()));
					list.setSclass("version");
					item.appendChild(list);
					item.setValue(cr);
				} else {
					throw new IllegalArgumentException("data should be Case!" + data);
				}

			}
		});

	}

	public void onSelect$recentlys(Event e) {
		Case cr = (Case) recentlys.getSelectedItem().getValue();
		Executions.sendRedirect("/sample/" + cr.getCaseUrl());
	}

	public void onSelect$likes(Event e) {
		CaseRecord cr = (CaseRecord) likes.getSelectedItem().getValue();
		Executions.sendRedirect("/sample/" + cr.getCaseUrl());
	}

	public void onClick$whyfiddle(Event e) {
		try {
			aboutContent.doModal();
		} catch (SuspendNotAllowedException e1) {
			if (logger.isEnabledFor(Level.ERROR))
				logger.error("onClick$abouttab(Event)", e1);
		} catch (InterruptedException e1) {
			if (logger.isEnabledFor(Level.ERROR))
				logger.error("onClick$abouttab(Event)", e1);
		}
	}
}
