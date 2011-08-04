package org.zkoss.fiddle.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Index;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.CompareToBuilder;

@SuppressWarnings("rawtypes")
@Entity
@Table(name = "tags")
public class Tag implements Comparable, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -4082360764952895125L;

	private Long id;

	/**
	 * 2011/6/26 TonyQ:
	 *
	 * Note that I decide tag name is case-senstive, it make more sense in
	 * generic way .
	 *
	 * Although in some case it didn't make all sense ,like "grid" vs "Grid" ,
	 * but comparing to make it all lower-case or upper-case, we have to live
	 * with it .
	 *
	 * Yes , we could do some "smart" things , but do we really need that? ;) I
	 * don't think so.
	 *
	 */
	private String name;

	private Long amount;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 50)
	@Index(name = "tagIdx")
	public String getName() {
		return name;
	}

	/**
	 * Don't enter the word that more then 25 chars.
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Column
	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public boolean equals(final Object other) {
		if (!(other instanceof Tag))
			return false;
		Tag castOther = (Tag) other;
		return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	public int compareTo(final Object other) {
		Tag castOther = (Tag) other;
		return new CompareToBuilder().append(amount, castOther.amount).toComparison();
	}

}
