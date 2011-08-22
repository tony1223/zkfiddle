package org.zkoss.fiddle.visualmodel;

import java.util.List;

import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.Tag;


public class TagCaseListVO {

	private Case _case;

	private List<Tag> tags;


	public Case getCase() {
		return _case;
	}


	public void setCase(Case _case) {
		this._case = _case;
	}


	public List<Tag> getTags() {
		return tags;
	}


	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

}
