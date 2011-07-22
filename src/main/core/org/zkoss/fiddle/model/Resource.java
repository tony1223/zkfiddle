package org.zkoss.fiddle.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
@Table(name = "resources")
public class Resource implements  Cloneable, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -4759386406027188251L;

	public static final int TYPE_ZUL = 0;

	public static final int TYPE_JAVA = 1; // actually it's beanshell

	public static final int TYPE_JS = 2;

	public static final int TYPE_HTML = 3;

	public static final int TYPE_CSS = 4;
	
	public static final int TYPE_MEDIA = 5;
	

	public static final String PACKAGE_TOKEN = "pkg$";

	public static final String PACKAGE_TOKEN_ESCAPE = "pkg\\$";

	public static final String PACKAGE_PREFIX = "";
	
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

	@Lob
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
		case TYPE_MEDIA:
			return "media";
		default:
			return "unknown";
		}
	}

	@Lob
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

	@Transient
	public String getFullPackage(){
		return Resource.PACKAGE_PREFIX + Resource.PACKAGE_TOKEN + this.getPkg();
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

	@Column
	public boolean isCanDelete() {
		return canDelete;
	}

	public void setFinalConetnt(Case c) {
		this.finalContent = buildFinalConetnt(c.getToken(), c.getVersion());
	}

	public String buildFinalConetnt(String replacedPackage) {

		if (replacedPackage == null) {
			throw new IllegalArgumentException("token is null ");
		}
		if (content == null) {
			throw new IllegalStateException("content is null ");
		}

		String finalcontent;

		if (type == TYPE_JAVA) {
			finalcontent = "package " + Resource.PACKAGE_PREFIX + Resource.PACKAGE_TOKEN + pkg + ";\n\n"
					+ this.content;
			return finalcontent.replaceAll(Resource.PACKAGE_TOKEN_ESCAPE, replacedPackage);
		} else if (type == TYPE_ZUL) {
			finalcontent = this.content;
			return finalcontent.replaceAll(Resource.PACKAGE_TOKEN_ESCAPE, replacedPackage);

		} else {
			return this.content;
		}

	}

	public String buildFinalConetnt(String token, int version) {

		if (token == null) {
			throw new IllegalArgumentException("token is null ");
		}
		if (content == null) {
			throw new IllegalStateException("content is null ");
		}

		String finalcontent;

		String replacedtoken = "j" + token + "\\$v" + version;

		if (type == TYPE_JAVA) {
			finalcontent = "package " + Resource.PACKAGE_PREFIX + Resource.PACKAGE_TOKEN + pkg + ";\n\n"
					+ this.content;
			return finalcontent.replaceAll(Resource.PACKAGE_TOKEN_ESCAPE, replacedtoken);
		} else if (type == TYPE_ZUL) {
			finalcontent = this.content;
			return finalcontent.replaceAll(Resource.PACKAGE_TOKEN_ESCAPE, replacedtoken);

		} else {
			return this.content;
		}

	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public boolean equals(final Object other) {
		if (!(other instanceof Resource))
			return false;
		Resource castOther = (Resource) other;
		return new EqualsBuilder().append(id, castOther.id).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("id", id).append("name", name)
				.append("content", content).append("type", type).append("caseId", caseId)
				.append("createDate", createDate).append("finalContent", finalContent).append("pkg", pkg)
				.append("canDelete", canDelete).toString();
	}
}
