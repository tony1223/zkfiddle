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
import org.zkoss.fiddle.core.utils.UrlUtil;
import org.zkoss.fiddle.model.api.ICase;

@Entity
@Table(name = "cases")
public class Case implements ICase, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8589837203581484371L;

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

	private String posterIP;
	
	private String authorName;
	
	private Boolean guest;

	/**
	 * A title for SEO friendly and data collection
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

	@Column
	public String getPosterIP() {
		return posterIP;
	}

	public void setPosterIP(String posterIP) {
		this.posterIP = posterIP;
	}

	@Transient
	public String getURLFriendlyTitle() {
		return UrlUtil.getURLFriendlyTitle(this.title);
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

	@Column
	public String getAuthorName() {
		return authorName;
	}

	
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	@Column
	public Boolean isGuest() {
		return guest;
	}

	public void setGuest(Boolean guest) {
		this.guest = guest;
	}

}
