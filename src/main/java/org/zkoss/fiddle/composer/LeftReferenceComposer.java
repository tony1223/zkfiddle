package org.zkoss.fiddle.composer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.FiddleEvents;
import org.zkoss.fiddle.core.utils.CacheHandler;
import org.zkoss.fiddle.core.utils.FiddleCache;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.dao.api.ITagDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.util.SEOUtils;
import org.zkoss.fiddle.visualmodel.TagCloudVO;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

public class LeftReferenceComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1691313428073974036L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(LeftReferenceComposer.class);

	private Listbox likes;

	private Listbox recentlys;

	private Window popupContent;

	private Div tagContainer;

	private EventQueue tag = EventQueues.lookup(FiddleEventQueues.Tag,true);

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		List<CaseRecord> list = (List<CaseRecord>) FiddleCache.Top10liked.execute(new CacheHandler<List<CaseRecord>>(){
			protected List<CaseRecord> execute() {
				ICaseRecordDao caseRecordDao = (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
				return caseRecordDao.listByType(CaseRecord.Type.Like, true, 1, 50);
			}
			protected String getKey() {
				return CaseRecord.Type.Like + ":" + true + ":" + 1 + ":" + 50;
			}
		});
		
		if(list.size() != 0){
			SEOUtils.render(desktop, "Top 10 Favorites :" , list);
		}


		initTags();
		tag.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(FiddleEvents.ON_TAG_UPDATE.equals(event.getName())){
					initTags();
				}
			}
		});

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
		List<Case> caseList = caseDao.getRecentlyCase(10);
		if(caseList.size() != 0){
			SEOUtils.render(desktop, "Latest 10 Fiddles :" , caseList);
		}
		recentlys.setModel(new ListModelList(caseList));

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

	private void initTags(){
		tagContainer.getChildren().clear();
		tagContainer.setSclass("tag-container");

		ITagDao tagDao = (ITagDao) SpringUtil.getBean("tagDao");
		List<Tag> list = tagDao.findPopularTags(20);

		//if there's no tag , need not to do this.
		if( list.size() == 0 ) return ;

		TagCloudVO tcvo = new TagCloudVO(list);


		Collections.sort(list, new Comparator<Tag>() {
			public int compare(Tag o1, Tag o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});

		for (int i = 0, size = list.size(); i < size; ++i) {
			Tag tag = list.get(i);
			try {
				A taglink = new A(tag.getName() + (tag.getAmount() > 1 ? "(" + tag.getAmount() + ") " :""));

				String tagN = (String) requestScope.get("tag");
				if (tagN != null && tagN.equals(tag.getName())) {
					taglink.setSclass("tag-cloud tag-cloud-sel tag-cloud" + tcvo.getLevel(tag.getAmount().intValue()));
				} else {
					taglink.setSclass("tag-cloud tag-cloud" + tcvo.getLevel(tag.getAmount().intValue()));
				}

				taglink.setHref("/tag/" + URLEncoder.encode(tag.getName(), "UTF-8"));
				tagContainer.appendChild(taglink);
			} catch (UnsupportedEncodingException e) {
			}
		}
	}

	public void onSelect$recentlys(Event e) {
		Case cr = (Case) recentlys.getSelectedItem().getValue();
		Executions.sendRedirect("/sample/" + cr.getCaseUrl());
	}

	public void onSelect$likes(Event e) {
		CaseRecord cr = (CaseRecord) likes.getSelectedItem().getValue();
		Executions.sendRedirect("/sample/" + cr.getCaseUrl());
	}

	public void onClick$news(Event e) {
		try {

			popupContent.setTitle("Maintenance Log");
			popupContent.doOverlapped();
			((Include)popupContent.getFellow("popupInclude")).setSrc("/html/maintain.html");
		} catch (SuspendNotAllowedException e1) {
			if (logger.isEnabledFor(Level.ERROR))
				logger.error("onClick$newsContent(Event)", e1);
		}
	}

	public void onClick$whyfiddle(Event e) {
		try {
			popupContent.setTitle("About");
			popupContent.doOverlapped();
			((Include)popupContent.getFellow("popupInclude")).setSrc("/html/about.html");
		} catch (SuspendNotAllowedException e1) {
			if (logger.isEnabledFor(Level.ERROR))
				logger.error("onClick$whyfiddle(Event)", e1);
		}
	}
}
