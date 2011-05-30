package org.zkoss.fiddle.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Index;
import org.zkoss.fiddle.model.api.ICase;

@Entity
@Table(name = "cases")
public class Case implements ICase {

	private Long id;

	/**
	 * if it's a updated version , the thread should be first one's ID.
	 */
	private Long thread;
	
	/**
	 * fork from
	 */
	private Long from;

	private String token;

	/**
	 * version start with zero
	 */
	private Integer version;

	private Date createDate;
	
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
	public Long getFrom() {
		return from;
	}
	
	public void setFrom(Long from) {
		this.from = from;
	}
	
	@Column
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
