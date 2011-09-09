package org.zkoss.fiddle.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.zkoss.fiddle.visualmodel.FiddleSandbox;

/**
 * We store this data in system wild and not persisting, this should be used as
 * a singleton
 *
 * @author tony
 *
 */
public class FiddleSandboxManager {

	private Map<String, FiddleSandbox> sandboxesByHash = new HashMap<String, FiddleSandbox>();

	private Map<String, List<FiddleSandbox>> sandboxesByVersion = new TreeMap<String, List<FiddleSandbox>>();

	private String latest = "5.0.8";

	private long checkTime = 1000 * 60 * 5;

	public FiddleSandboxManager() {

		boolean debugMode = Boolean.getBoolean("fiddle.debug");
		if (debugMode) {
			FiddleSandbox sandbox = new FiddleSandbox() {

				public String getSrc(String token, Integer ver) {
					return "http://www.google.com";
				}
			};
			sandbox.setLastUpdate(new Date());
			sandbox.setName("localtest");
			sandbox.setVersion("5.0.8");
			sandbox.setPath("http://localhost/test1");
			this.addFiddleSandbox(sandbox);

			// open two sandboxes to help designer finetune the features.
			sandbox = new FiddleSandbox() {

				public String getSrc(String token, Integer ver) {
					return "http://www.google.com";
				}
			};
			sandbox.setLastUpdate(new Date());
			sandbox.setName("localtest2");
			sandbox.setVersion("5.0.7.1");
			sandbox.setPath("http://localhost/test2");
			this.addFiddleSandbox(sandbox);
		}
	}

	public FiddleSandbox getFiddleSandbox(String hash) {
		return checkDate(sandboxesByHash.get(hash));

	}

	public FiddleSandbox getFiddleSandboxForLastestVersion() {
		return getFiddleSandboxByVersion(latest);
	}

	public FiddleSandbox getFiddleSandboxByVersion(String version) {

		if(version == null)
			return null;

		version = version.trim();

		for (FiddleSandbox fi : getVersionList(version)) {
			FiddleSandbox sandbox = checkDate(fi);
			if (sandbox != null)
				return sandbox;
		}
		return null;

	}

	private List<FiddleSandbox> getVersionList(String ver) {
		if (sandboxesByVersion.containsKey(ver)) {
			return sandboxesByVersion.get(ver);
		} else {
			List<FiddleSandbox> list = new ArrayList<FiddleSandbox>();
			sandboxesByVersion.put(ver, list);
			return list;
		}
	}

	private FiddleSandbox checkDate(FiddleSandbox os) {
		if (os == null)
			return os;

		Date d = new Date();
		long diff = os.getLastUpdate().getTime() - d.getTime();
		if (diff > checkTime) {
			removeSandbox(os.getHash());
			return null;
		}
		return os;
	}

	public void removeSandbox(String hash) {

		FiddleSandbox ins = sandboxesByHash.get(hash);
		if(ins != null){
			sandboxesByHash.remove(hash);
			getVersionList(ins.getZKVersion()).remove(ins);
		}

	}

	public void addFiddleSandbox(FiddleSandbox sandbox) {
		if (sandbox == null || sandbox.getPath() == null) {
			throw new IllegalArgumentException("sandbox and sandbox path can't be null ");
		}

		sandboxesByHash.put(sandbox.getHash(), sandbox);

		getVersionList(sandbox.getZKVersion()).add(sandbox);

		Date d = new Date();

		for (String hash : sandboxesByHash.keySet()) {

			long diff = sandboxesByHash.get(hash).getLastUpdate().getTime() - d.getTime();
			if (diff > checkTime) {
				removeSandbox(hash);
			}
		}

	}

	/**
	 * @return
	 */
	public Map<String, FiddleSandbox> listFiddleInstances() {
		return Collections.unmodifiableMap(sandboxesByHash);
	}

	public List<String> getAvailableVersions() {
		List<String> array = new ArrayList<String>();
		synchronized (sandboxesByVersion) {
			for (String key : sandboxesByVersion.keySet()) {
				if (sandboxesByVersion.get(key).size() != 0) {
					array.add(key);
				}
			}
		}
		return array;
	}

}
