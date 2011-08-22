package org.zkoss.fiddle.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Index;

/*
 *	For performance reason we didn't want to do "Normal Form" for DB at this part.
 */
@Entity
@Table(name = "rating")
public class CaseRating implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2427044850941659111L;

	private Long id;

	private Long caseId;

	private Long amount;

	private String userName;

	private Date createDate;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/*
	 * About the index , we assume that we will usually find by type first, then
	 * find case second. (if need)
	 */
	@Column
	@Index(name = "cRatingIdx", columnNames = { "caseId", "userName" })
	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	@Column
	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	@Column
	@Index(name = "cRatingIdx", columnNames = { "caseId", "userName" })
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public boolean equals(final Object other) {
		if (!(other instanceof CaseRating))
			return false;
		CaseRating castOther = (CaseRating) other;
		return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

}
