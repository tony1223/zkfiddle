package org.zkoss.fiddle.model;

import java.util.Date;

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

	public int hashCode() {

		if (name != null)
			return name.hashCode();
		else
			return super.hashCode();
	}

	public boolean equals(Object obj) {
		
		if (!(obj instanceof FiddleInstance)) {
			return super.equals(obj);
		}
		if (this.getName() == null) {
			throw new IllegalStateException("FiddleInstance didn't contains name information");
		}
		FiddleInstance out = (FiddleInstance) obj;
		return this.getName().equals(out.getName());
	}
}
