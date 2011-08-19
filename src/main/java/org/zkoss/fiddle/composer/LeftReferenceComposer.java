package org.zkoss.fiddle.composer;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.component.TagContainerDiv;
import org.zkoss.fiddle.composer.event.FiddleEvents;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventQueues;
import org.zkoss.fiddle.core.utils.CacheHandler;
import org.zkoss.fiddle.core.utils.FiddleCache;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.dao.api.ICaseRecordDao;
import org.zkoss.fiddle.dao.api.ITagDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.util.BrowserState;
import org.zkoss.fiddle.util.CaseUtil;
import org.zkoss.fiddle.util.SEOUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ork.zkoss.fiddle.hyperlink.Hyperlink;

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

	private TagContainerDiv tagContainer;

	private Textbox tagFilter;

	private String currentTag;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		
		Tag tag = ((Tag) requestScope.get(FiddleConstant.REQUEST_ATTR_TAG));
		if(tag != null){
			currentTag = tag.getName();
		}

		updateTags();
		EventQueues.lookup(FiddleEventQueues.Tag, true).subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if (FiddleEvents.ON_TAG_UPDATE.equals(event.getName())) {
					if(event.getData() != null){
						currentTag = (String) event.getData();
					}
					updateTags();
				}
			}
		});

		updateLikeModel();

		likes.setItemRenderer(new ListitemRenderer() {

			public void render(Listitem item, Object data) throws Exception {
				if (data instanceof CaseRecord) {
					CaseRecord cr = (CaseRecord) data;
					String title = (cr.getTitle() == null || "".equals(cr.getTitle().trim())) ? cr.getToken() : cr
							.getTitle();
					item.appendChild(new Listcell(String.valueOf((item.getIndex() + 1))));

					{
						Listcell list = new Listcell();
						Hyperlink titleItem = new Hyperlink(String.valueOf(title));
						titleItem.setHref(CaseUtil.getSampleURL(cr));

						//set disable to prevent default href behavior,
						//since we will handle the url in onSelect event.
						titleItem.setDisableHref(true);
						list.appendChild(titleItem);
						item.appendChild(list);
					}

					Listcell list = new Listcell(String.valueOf(cr.getAmount()));
					list.setSclass("amount");
					item.appendChild(list);
					item.setValue(cr);
				} else {
					throw new IllegalArgumentException("data should be CaseRecord!" + data);
				}

			}
		});

		updateRecentlyModel();
		EventQueues.lookup(FiddleEventQueues.LeftRefresh, true).subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				updateRecentlyModel();
				updateLikeModel();
			}
		});

		recentlys.setItemRenderer(new ListitemRenderer() {

			public void render(Listitem item, Object data) throws Exception {
				if (data instanceof Case) {
					Case cr = (Case) data;
					String title = (cr.getTitle() == null || "".equals(cr.getTitle())) ? cr.getToken() : cr.getTitle();
					item.appendChild(new Listcell(String.valueOf((item.getIndex() + 1))));
					{
						Listcell list = new Listcell();
						Hyperlink titleItem = new Hyperlink(String.valueOf(title));
						titleItem.setHref(CaseUtil.getSampleURL(cr));

						//set disable to prevent default href behavior,
						//since we will handle the url in onSelect event.
						titleItem.setDisableHref(true);
						list.appendChild(titleItem);
						item.appendChild(list);
					}

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

	private void updateRecentlyModel() {

		ICaseDao caseDao = (ICaseDao) SpringUtil.getBean("caseDao");
		List<Case> caseList = caseDao.getRecentlyCase(10);
		if (caseList.size() != 0) {
			SEOUtils.render(desktop, "Latest 10 Fiddles :", caseList);
		}
		recentlys.setModel(new ListModelList(caseList));
	}

	private void updateLikeModel() {

		List<CaseRecord> list = (List<CaseRecord>) FiddleCache.Top10liked.execute(new CacheHandler<List<CaseRecord>>() {

			protected List<CaseRecord> execute() {
				ICaseRecordDao caseRecordDao = (ICaseRecordDao) SpringUtil.getBean("caseRecordDao");
				return caseRecordDao.listByType(CaseRecord.Type.Like, true, 1, 50);
			}

			protected String getKey() {
				return CaseRecord.Type.Like + ":" + true + ":" + 1 + ":" + 50;
			}
		});

		if (list.size() != 0) {
			SEOUtils.render(desktop, "Top 10 Favorites :", list);
		}
		likes.setModel(new ListModelList(list));
	}

	private void updateTags() {
		if (!"".equals(tagFilter.getValue().trim())) {
			searchTags(tagFilter.getValue().trim());
			return;
		}
		ITagDao tagDao = (ITagDao) SpringUtil.getBean("tagDao");
		List<Tag> list = tagDao.findPopularTags(20);
		//FIXME update current tag after push state.
		tagContainer.setTags(list, currentTag);
	}

	private void searchTags(String keyword) {
		ITagDao tagDao = (ITagDao) SpringUtil.getBean("tagDao");
		List<Tag> list = tagDao.searchTag(keyword.trim(), false, 30);
		tagContainer.setTags(list, currentTag);
		logger.info("LeftReferenceComposer:search TAG[" + keyword + "]");
	}

	public void onChanging$tagFilter(InputEvent e) {
		searchTags(e.getValue());
	}

	public void onSelect$recentlys(Event e) {
		Listitem item = recentlys.getSelectedItem();
		//Note that we will set the model of listbox after every time if pushState enabled,
		//if you select it very quickly , it might be null for selectedItem.
		if(item != null){ 
			Case cr = (Case) item.getValue();
			String newtitle = "ZK Fiddle - "+CaseUtil.getPublicTitle(cr);
			BrowserState.go(CaseUtil.getSampleURL(cr), newtitle, cr);
		}
	}

	public void onSelect$likes(Event e) {
		Listitem item = likes.getSelectedItem();
		if(item != null){
			CaseRecord cr = (CaseRecord) item.getValue();
			Executions.sendRedirect(CaseUtil.getSampleURL(cr));
		}
	}

	public void onClick$news(Event e) {
		try {

			popupContent.setTitle("Maintenance Log");
			popupContent.doOverlapped();
			((Include) popupContent.getFellow("popupInclude")).setSrc("/html/maintain.html");
		} catch (SuspendNotAllowedException e1) {
			if (logger.isEnabledFor(Level.ERROR))
				logger.error("onClick$newsContent(Event)", e1);
		}
	}

	public void onClick$whyfiddle(Event e) {
		try {
			popupContent.setTitle("About");
			popupContent.doOverlapped();
			((Include) popupContent.getFellow("popupInclude")).setSrc("/html/about.html");
		} catch (SuspendNotAllowedException e1) {
			if (logger.isEnabledFor(Level.ERROR))
				logger.error("onClick$whyfiddle(Event)", e1);
		}
	}
}
