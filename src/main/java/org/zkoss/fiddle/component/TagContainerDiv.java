package org.zkoss.fiddle.component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.util.BrowserStateUtil;
import org.zkoss.fiddle.util.TagUtil;
import org.zkoss.fiddle.visualmodel.TagCloudVO;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;

import ork.zkoss.fiddle.hyperlink.Hyperlink;

public class TagContainerDiv extends Div implements IdSpace {

	/**
	 *
	 */
	private static final long serialVersionUID = -8582217171343586614L;

	public void setTags(List<Tag> list, String current) {
		this.getChildren().clear();

		// if there's no tag , need not to do this.
		if (list.size() == 0)
			return;

		TagCloudVO tcvo = new TagCloudVO(list);

		Collections.sort(list, new Comparator<Tag>() {

			public int compare(Tag o1, Tag o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});

		for (int i = 0, size = list.size(); i < size; ++i) {
			final Tag tag = list.get(i);
			Hyperlink taglink = new Hyperlink(tag.getName()
					+ (tag.getAmount() > 1 ? "(" + tag.getAmount() + ") " : " "));

			String tagN = current;
			if (tagN != null && tagN.equals(tag.getName())) {
				taglink.setSclass("tag-cloud tag-cloud-sel tag-cloud" + tcvo.getLevel(tag.getAmount().intValue()));
			} else {
				taglink.setSclass("tag-cloud tag-cloud" + tcvo.getLevel(tag.getAmount().intValue()));
			}

			final String url = TagUtil.getViewURL(tag);
			taglink.setHref(url);
			taglink.addEventListener("onClick", new EventListener() {

				public void onEvent(Event event) throws Exception {
					BrowserStateUtil.go(tag);
				}
			});

			this.appendChild(taglink);
		}
	}
}
