package org.zkoss.fiddle.model;

import java.util.Date;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zkoss.fiddle.component.renderer.JavaTabRenderer;
import org.zkoss.fiddle.model.api.IResource;

@Entity
@Table(name = "resources")
public class Resource implements IResource {

	private Long id;

	private String name;

	private String content;

	private Integer type;

	private Long caseId;

	private Date createDate;

	/**
	 * for java or something need to eval
	 */
	private String finalContent;

	/**
	 * for java
	 */
	private String pkg = "";

	/**
	 * for the default index.zul
	 */
	private boolean canDelete = true;

	public Resource() {

	}

	public Resource(int pType) {
		this(pType, null, null);
	}

	public Resource(int pType, String pName, String pContent) {
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
		switch (type) {
		case TYPE_ZUL:
			return "zul";
		case TYPE_JAVA:
			return "java";
		case TYPE_JS:
			return "javascript";
		case TYPE_HTML:
			return "html";
		case TYPE_CSS:
			return "css";
		default:
			return "unknown";
		}
	}

	@Column
	public String getFinalContent() {
		return finalContent;
	}

	public void setFinalContent(String rawContent) {
		this.finalContent = rawContent;
	}

	@Column
	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
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
		switch (type) {
		case TYPE_ZUL:
			return "xml";
		case TYPE_JAVA:
			return "java";
		case TYPE_JS:
			return "javascript";
		case TYPE_HTML:
			return "html";
		case TYPE_CSS:
			return "css";
		default:
			return "unknown";
		}
	}

	public Resource clone() {
		Resource resource = new Resource();
		resource.setCaseId(this.caseId);
		resource.setContent(this.content);
		resource.setName(this.name);
		resource.setType(this.type);
		resource.setId(this.id);
		resource.setCanDelete(canDelete);
		resource.setPkg(this.pkg);
		return resource;
	}

	@Transient
	public boolean isCanDelete() {
		return canDelete;
	}

	public void buildFinalConetnt(String token, int version) {

		if (token == null) {
			throw new IllegalArgumentException("token is null ");
		}
		if (content == null) {
			throw new IllegalStateException("content is null ");
		}

		String finalcontent, cont = (this.content == null ? this.content : "");
		String escape = Pattern.quote(JavaTabRenderer.PACKAGE_TOKEN);

		String replacedtoken = token + "$$" + version;

		if (type == TYPE_JAVA) {
			finalcontent = "package " + JavaTabRenderer.PACKAGE_PREFIX + pkg + ";\n\n" + cont;
			this.finalContent = finalcontent.replaceAll(escape, replacedtoken);
		} else if (type == TYPE_ZUL) {
			finalcontent = cont;
			this.finalContent = cont.replaceAll(escape, replacedtoken);
		} else {
			this.finalContent = this.content;
		}

	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

}
