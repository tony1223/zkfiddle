package org.zkoss.fiddle.visualmodel;

import java.util.List;

import org.zkoss.fiddle.model.CaseRecord;
import org.zkoss.fiddle.model.Tag;


public class TagCaseListVO {
	
	private CaseRecord _caserecord;
	
	private List<Tag> tags;

	
	public CaseRecord getCaseRecord() {
		return _caserecord;
	}

	
	public void setCaseRecord(CaseRecord _case) {
		this._caserecord = _case;
	}

	
	public List<Tag> getTags() {
		return tags;
	}

	
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
}
