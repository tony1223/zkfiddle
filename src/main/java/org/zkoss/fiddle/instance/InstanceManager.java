package org.zkoss.fiddle.instance;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.fiddle.model.Instance;

/**
 * we store this data in system wild and not persisting.
 * 
 * @author tony
 * 
 */
public class InstanceManager {

	private Map<String, Instance> instances = new HashMap<String, Instance>();

	private static InstanceManager _instance;

	private InstanceManager() {

	}

	/**
	 * @throws IllegalArgumentException
	 *             instance and instance path can't be null
	 * @param instance
	 */

	public void addInstance(Instance instance) {
		if (instance == null || instance.getPath() == null) {
			throw new IllegalArgumentException("instance and instance path can't be null ");
		}

		instances.put(instance.getHash(), instance);

		Date d = new Date();
		long checkTime = 1000 * 60 * 5;

		for (String hash : instances.keySet()) {

			long diff = instances.get(hash).getLastUpdate().getTime() - d.getTime();
			if (diff > checkTime) {
				instances.remove(hash);
			}
		}

	}

	/**
	 * @return
	 */
	public Map<String, Instance> listInstances() {
		return Collections.unmodifiableMap(instances);
	}

	public static InstanceManager getInstance() {
		if (_instance == null)
			_instance = new InstanceManager();

		return _instance;
	}

}
