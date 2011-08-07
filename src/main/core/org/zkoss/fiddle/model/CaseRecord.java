package org.zkoss.fiddle.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Index;
import org.zkoss.fiddle.model.api.IRenderCase;


/*
 *	For performance reason we didn't want to do "Normal Form" for DB at this part.
 */
@Entity
@Table(name="caserecord")
public class CaseRecord implements Serializable,IRenderCase{

	/**
	 *
	 */
	private static final long serialVersionUID = -2427044850941659111L;

	public enum Type {
		View(0), Like(1), RunTemp(2), Run(3), Download(4),Widget(5);
		private Integer type;
		Type(int type) {
			this.type = type;
		}
		public Integer value() {
			return type;
		}

	}

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

	@Transient
	public String getURLFriendlyTitle() {
		if (this.title == null || "".equals(this.title.trim()))
			return "";

		StringBuffer sb = new StringBuffer();
		String[] tokens = this.title.split("[^a-zA-Z0-9]+");
		for (String str : tokens) {
			sb.append("-" + str);
		}
		return sb.toString();
	}

	@Transient
	public String getCaseUrl() {
		return getToken() + "/" + getVersion() + getURLFriendlyTitle();
	}

	@Transient
	public String getCaseUrl(String ver) {
		if(ver == null) return getCaseUrl();
		return getToken() + "/" + getVersion() + "/v" + ver + getURLFriendlyTitle();
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

	//TODO review this ,see if we need to provide this information in case recoord.
	@Transient
	public Date getCreateDate() {
		return null;
	}

}
