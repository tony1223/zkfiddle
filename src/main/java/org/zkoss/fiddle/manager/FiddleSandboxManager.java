package org.zkoss.fiddle.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.fiddle.model.FiddleSandbox;

/**
 * We store this data in system wild and not persisting,
 * this should be used as a singleton
 * 
 * @author tony
 * 
 */
public class FiddleSandboxManager {

	private Map<String, FiddleSandbox> instancesByHash = new HashMap<String, FiddleSandbox>();

	private Map<String, List<FiddleSandbox>> instancesByVersion = new HashMap<String, List<FiddleSandbox>>();

	private String latest = "5.0.7.1";

	private long checkTime = 1000 * 60 * 5;
	
	/**
	 * @throws IllegalArgumentException
	 *             instance and instance path can't be null
	 * @param instance
	 */

	public FiddleSandbox getFiddleInstance(String hash) {
		return checkDate(instancesByHash.get(hash));

	}

	public FiddleSandbox getFiddleInstanceForLastestVersion() {
		return getFiddleInstanceByVersion(latest);
	}
	
	public FiddleSandbox getFiddleInstanceByVersion(String version) {

		for (FiddleSandbox fi : getVersionList(version)) {
			FiddleSandbox inst = checkDate(fi);
			if (inst != null)
				return inst;
		}
		return null;

	}


	private List<FiddleSandbox> getVersionList(String ver) {
		if (instancesByVersion.containsKey(ver)) {
			return instancesByVersion.get(ver);
		} else {
			List<FiddleSandbox> list = new ArrayList<FiddleSandbox>();
			instancesByVersion.put(ver, list);
			return list;
		}
	}

	private FiddleSandbox checkDate(FiddleSandbox os) {
		if (os == null)
			return os;

		Date d = new Date();
		long diff = os.getLastUpdate().getTime() - d.getTime();
		if (diff > checkTime) {
			removeInstacne(os.getHash());
			return null;
		}
		return os;
	}

	private void removeInstacne(String hash){
		
		FiddleSandbox ins = instancesByHash.get(hash);
		instancesByHash.remove(hash);
		
		getVersionList(ins.getZKVersion()).remove(ins);
		
	}

	public void addFiddleInstance(FiddleSandbox instance) {
		if (instance == null || instance.getPath() == null) {
			throw new IllegalArgumentException("instance and instance path can't be null ");
		}

		instancesByHash.put(instance.getHash(), instance);

		getVersionList(instance.getZKVersion()).add(instance);

		Date d = new Date();
		long checkTime = 1000 * 60 * 5;

		for (String hash : instancesByHash.keySet()) {

			long diff = instancesByHash.get(hash).getLastUpdate().getTime() - d.getTime();
			if (diff > checkTime) {
				removeInstacne(hash);
			}
		}

	}

	/**
	 * @return
	 */
	public Map<String, FiddleSandbox> listFiddleInstances() {
		return Collections.unmodifiableMap(instancesByHash);
	}


}
