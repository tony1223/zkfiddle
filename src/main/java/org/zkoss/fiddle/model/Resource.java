package org.zkoss.fiddle.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zkoss.fiddle.model.api.IResource;


@Entity
@Table(name = "resources")
public class Resource implements IResource {
	private Long id;
	private String name;
	private String content;
	private Integer type ;
	private Integer caseId;
	
	public Integer getCaseId() {
		return caseId;
	}

	
	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}

	public Resource(int pType){
		this(pType,null,null);
	}
	
	public Resource(int pType,String pName,String pContent){
		type = pType;
		name = pName;
		content = pContent;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Column
	public String getName() {
		return name;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column
	public String getContent() {
		return this.content;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}

	@Column
	public Integer getType() {
		return type;
	}
	
	@Transient
	public String getTypeName() {
		switch(type){
			case TYPE_ZUL: return "zul";
			case TYPE_JAVA: return "java";
			case TYPE_JS: return "javascript";
			case TYPE_HTML: return "html";
			case TYPE_CSS: return "css";
			default: return "unknown";
		}
	}
	
	
	/**
	 * @Transient means it's not a db field
	 */
	@Transient
	public String getTypeMode() {
		switch(type){
			case TYPE_ZUL: return "xml";
			case TYPE_JAVA: return "java";
			case TYPE_JS: return "javascript";
			case TYPE_HTML: return "html";
			case TYPE_CSS: return "css";
			default: return "unknown";
		}
	}

}
