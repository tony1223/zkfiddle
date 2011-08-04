package org.zkoss.fiddle.component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.visualmodel.TagCloudVO;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zul.A;
import org.zkoss.zul.Div;

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
			Tag tag = list.get(i);
			try {
				A taglink = new A(tag.getName() + (tag.getAmount() > 1 ? "(" + tag.getAmount() + ") " : " "));

				String tagN = current;
				if (tagN != null && tagN.equals(tag.getName())) {
					taglink.setSclass("tag-cloud tag-cloud-sel tag-cloud" + tcvo.getLevel(tag.getAmount().intValue()));
				} else {
					taglink.setSclass("tag-cloud tag-cloud" + tcvo.getLevel(tag.getAmount().intValue()));
				}

				taglink.setHref("/tag/" + URLEncoder.encode(tag.getName(), "UTF-8"));
				this.appendChild(taglink);
			} catch (UnsupportedEncodingException e) {
			}
		}
	}
}
