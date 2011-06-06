package org.zkoss.fiddle.model;

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
@Table(name="caserecord")
public class CaseRecord {
	public static final Integer TYPE_VIEW = 0 ;
	public static final Integer TYPE_LIKE = 1 ;
	public static final Integer TYPE_RUN_TEMP = 2 ;
	public static final Integer TYPE_RUN = 3 ;
	
	private Long id;
	
	private Long caseId;
	
	private String token;
	
	private Integer version;
	
	private String title;
	
	private Long amount;
	
	private Integer type;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	/*
	 * About the index ,  we assume that we will usually find by type first, then find case second. (if need)
	 */
	@Column
	@Index(name = "cRecIdx", columnNames = {"type", "caseId"})
	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

	@Column
	public String getToken() {
		return token;
	}

	
	public void setToken(String token) {
		this.token = token;
	}

	@Column
	public String getTitle() {
		return title;
	}

	
	public void setTitle(String title) {
		this.title = title;
	}

	@Column
	public Long getAmount() {
		return amount;
	}
	
	public void setAmount(Long amount) {
		this.amount = amount;
	}

	@Column
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column
	@Index(name = "cRecIdx", columnNames = {"type","caseId" })
	public Integer getType() {
		return type;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}

	public boolean equals(final Object other) {
		if (!(other instanceof CaseRecord))
			return false;
		CaseRecord castOther = (CaseRecord) other;
		return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

}
