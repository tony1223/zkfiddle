package org.zkoss.fiddle.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.zkoss.service.login.IUser;

/**
 * currently we are designing this with Admin
 * 
 * @author tony
 * 
 */
@Entity
@Table(name = "users")
public class User implements Serializable, IUser {

	/**
	 *
	 */
	private static final long serialVersionUID = 7758063965784902553L;

	private Long id;

	private String name;

	private String password;

	private String emailAddress;
	
	private Integer role;
	
	private Boolean active;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 50)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean equals(final Object other) {
		if (!(other instanceof User))
			return false;
		User castOther = (User) other;
		return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(name).toHashCode();
	}


	@Transient
	public String getDisplayName() {
		return name;
	}

	@Column
	public String getEmailAddress() {
		return emailAddress;
	}

	@Column(length = 50)
	public String getName() {
		return name;
	}

	@Column
	public Integer getRole() {
		return role;
	}

	@Column
	public Boolean isActive() {
		return active != null ? active : false;
	 }

	@Transient
	public void setActive(Boolean arg0) {
		active = arg0;
	}

	public void setDisplayName(String arg0) {
		throw new UnsupportedOperationException(
			"you can't set a display name , set the account instead.");
	}

	public void setEmailAddress(String arg0) {
		emailAddress = arg0;
	}

	public void setName(String arg0) {
		this.name = arg0;
	}

	public void setRole(Integer arg0) {
		this.role = arg0;
	}

	public int compareTo(final IUser other) {
		IUser castOther = (IUser) other;
		return new CompareToBuilder().append(getName(), castOther.getName()).toComparison();
	}

	public String toString() {
			return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("id", id)
					.append("name", name)
					.append("password", password).append("emailAddress", emailAddress).append("role", role)
					.append("active", active).toString();
		}

}
