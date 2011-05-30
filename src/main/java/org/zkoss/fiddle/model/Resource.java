package org.zkoss.fiddle.model;

import java.util.Date;

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
public class Resource implements IResource,Cloneable {
	private Long id;
	private String name;
	private String content;
	private Integer type ;
	private Long caseId;
	private Date createDate;
	
	public Resource(){
		
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	
	@Column
	public Long getCaseId() {
		return caseId;
	}

	
	public void setCaseId(Long caseId) {
		this.caseId = caseId;
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
	
	
	@Column
	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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
	
	public Resource clone(){
		Resource r = new Resource();
		r.setCaseId(this.caseId);
		r.setContent(this.content);
		r.setName(this.name);
		r.setType(this.type);
		r.setId(this.id);
		
		return r;
	}
	
}
