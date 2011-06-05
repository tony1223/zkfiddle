package org.zkoss.fiddle.model;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.zkoss.fiddle.util.CRCCaseIDEncoder;

public class FiddleInstance {

	private String hash;

	private String name;

	private String path;

	private Date lastUpdate;

	private String version;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		this.hash = CRCCaseIDEncoder.getInstance().encode(path);
	}

	public String getZKVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getHash() {
		return hash;
	}

	public boolean equals(final Object other) {
		if (!(other instanceof FiddleInstance))
			return false;
		FiddleInstance castOther = (FiddleInstance) other;
		return new EqualsBuilder().append(name, castOther.name).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(name).toHashCode();
	}
}
