package org.zkoss.fiddle.model;

import java.util.Date;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.zkoss.fiddle.core.utils.CRCCaseIDEncoder;

public class FiddleSandbox implements Comparable {

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
		if (!(other instanceof FiddleSandbox))
			return false;
		FiddleSandbox castOther = (FiddleSandbox) other;
		return new EqualsBuilder().append(hash, castOther.hash).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(hash).toHashCode();
	}

	public int compareTo(final Object other) {
		if (other != null && other instanceof FiddleSandbox) {
			FiddleSandbox castOther = (FiddleSandbox) other;

//			if (version != null && castOther.version != null) {
//				if ("freshly".equals(version) && (!"freshly".equals(castOther.version))) {
//					return 1;
//				} else if ((!"freshly".equals(version)) && "freshly".equals(castOther.version)) {
//					return -1;
//				}
//			}

			return new CompareToBuilder().append(castOther.version,version).
				append(castOther.lastUpdate,lastUpdate).toComparison();
		} else {
			throw new IllegalArgumentException("FiddleSandbox can't compare to " + other == null ? "null" : other
					.getClass().getName());
		}
	}
}
