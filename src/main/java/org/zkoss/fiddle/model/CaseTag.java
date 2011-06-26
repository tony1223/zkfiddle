package org.zkoss.fiddle.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * it's a relation table between case and tag
 * @author tony
 *
 */
@Entity
@Table(name = "r_casetags")
public class CaseTag implements Serializable {

	private Long caseId;

	private Long tagId;

	@Id
	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	@Id
	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}
}
