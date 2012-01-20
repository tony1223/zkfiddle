package org.zkoss.fiddle.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;

@Entity
@Table(name = "referlog")
public class ReferLog {
	private Integer id;
	private String refer;
	private String account;
	private String fromIP;
	private Date createDate;
	private Long caseId;
	private Integer type;
	
	public boolean equals(final Object other) {
		if (!(other instanceof ReferLog))
			return false;
		ReferLog castOther = (ReferLog) other;
		return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", id)
				.append("refer", refer).append("account", account)
				.append("fromIP", fromIP).append("createDate", createDate)
				.append("type", type).toString();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	@Column(length=300)
	public String getRefer() {
		return refer;
	}
	
	public void setRefer(String refer) {
		this.refer = refer;
	}
	
	@Column(length=40)
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	@Column(length=20)
	public String getFromIP() {
		return fromIP;
	}
	
	public void setFromIP(String fromIP) {
		this.fromIP = fromIP;
	}
	
	@Column
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@Column
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column	
	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}

}
