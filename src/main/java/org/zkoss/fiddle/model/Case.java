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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Index;
import org.zkoss.fiddle.model.api.ICase;

@Entity
@Table(name = "cases")
public class Case implements ICase, Serializable {

	private Long id;

	/**
	 * if it's a updated version , the thread should be first one's ID.
	 */
	private Long thread;

	/**
	 * fork from
	 */
	private Long fromId;

	private String token;

	/**
	 * version start with zero
	 */
	private Integer version;

	private Date createDate;

	/**
	 * A title for seo friendly and data collection
	 */
	private String title;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column
	public Long getThread() {
		return thread;
	}

	public void setThread(Long thread) {
		this.thread = thread;
	}

	@Index(name = "caseIdx", columnNames = { "token", "version" })
	@Column
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Index(name = "caseIdx", columnNames = { "token", "version" })
	@Column
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Column
	public Long getFromId() {
		return fromId;
	}

	public void setFromId(Long from) {
		this.fromId = from;
	}

	@Column
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(length = 80)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Transient
	public String getURLFriendlyTitle() {
		if (this.title == null || "".equals(this.title.trim()))
			return "";

		StringBuffer sb = new StringBuffer();
		String[] tokens = this.title.split("[^a-zA-Z0-9]+");
		for (String str : tokens) {
			if(!"".equals(str))
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
		return getToken() + "/" + getVersion() + "/v" + ver + getURLFriendlyTitle();
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("id", id).append("thread", thread)
				.append("fromId", fromId).append("token", token).append("version", version)
				.append("createDate", createDate).toString();
	}

	public boolean equals(final Object other) {
		if (!(other instanceof Case))
			return false;
		Case castOther = (Case) other;
		return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

}
