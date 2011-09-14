package org.zkoss.fiddle.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * currently we are designing this with Admin
 *
 * @author tony
 *
 */
@Entity
@Table(name = "user_remember_tokens")
public class UserRememberToken implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2762675670224976168L;

	/**
	 *
	 */
	private Long id;

	private String name;

	private String token;

	private Date createDate;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 30)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length = 60)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(length = 60)
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int compareTo(final UserRememberToken other) {
		UserRememberToken castOther = (UserRememberToken) other;
		return new CompareToBuilder().append(getToken(), castOther.getToken())
				.toComparison();
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("id", id).append("name", name).append("token", token)
				.toString();
	}
}
