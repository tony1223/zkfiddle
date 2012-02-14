package org.zkoss.fiddle.visualmodel;

import java.util.Date;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.zkoss.fiddle.core.utils.CRCCaseIDEncoder;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.visualmodel.comparator.VersionComparator;

public class FiddleSandbox implements Comparable<Object> {

	public enum Theme implements Comparable<Theme> {
		breeze, silvertail, sapphire, classicblue
	}
	
	public enum Status implements Comparable<Status> {
		unknown,pong,lost
	}

	private String hash;

	private String name;

	private Theme theme;

	private String path;

	private Date lastUpdate;

	private String version;

	private Status status;
	
	public FiddleSandbox() {
		status = Status.unknown;
	}
	
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

	public String getSrc(ICase _case) {
		return getSrc(_case.getToken(), _case.getVersion());
	}

	public String getSrc(String token, Integer ver) {
		return getPath() + token + "/" + ver;
	}

	public int compareVersion(FiddleSandbox other) {

		return new VersionComparator().compare(version, other.version);

	}

	public int compareTo(final Object other) {
		if (other != null && other instanceof FiddleSandbox) {
			FiddleSandbox castOther = (FiddleSandbox) other;

			if (version != null && castOther.version != null) {
				if (version.indexOf("FL") != -1 && (castOther.version.indexOf("FL") == -1)) {
					return 1;
				} else if ((version.indexOf("FL") == -1) && (castOther.version.indexOf("FL") != -1)) {
					return -1;
				}

				int ver = compareVersion(castOther);
				if (ver != 0)
					return ver;
			}

			return new CompareToBuilder()
			.append(theme, castOther.theme)
			.append(castOther.lastUpdate, lastUpdate).toComparison();
		} else {
			throw new IllegalArgumentException("FiddleSandbox can't compare to " + other == null ? "null" : other
					.getClass().getName());
		}
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
}
