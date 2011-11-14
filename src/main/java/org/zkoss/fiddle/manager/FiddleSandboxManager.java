package org.zkoss.fiddle.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.fiddle.visualmodel.FiddleSandboxGroup;
import org.zkoss.fiddle.visualmodel.comparator.VersionComparator;

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
			
			
			createFakeSandbox("Breeze",FiddleSandbox.Theme.breeze,"5.0.8","http://localhost/test1");
			createFakeSandbox("Breeze",FiddleSandbox.Theme.breeze,"5.0.7.1","http://localhost/test2");
			createFakeSandbox("Breeze",FiddleSandbox.Theme.breeze,"5.0.10","http://localhost/test3");
			createFakeSandbox("Breeze",FiddleSandbox.Theme.breeze,"5.0.10.FL","http://localhost/test4");
			createFakeSandbox("Breeze",FiddleSandbox.Theme.breeze,"6.0.0.FL","http://localhost/test5");
			createFakeSandbox("Sapphire",FiddleSandbox.Theme.sapphire,"6.0.0.FL","http://localhost/test51");
			createFakeSandbox("Sapphire",FiddleSandbox.Theme.sapphire,"6.0.0.FL","http://localhost/test52");
			createFakeSandbox("Silvertail",FiddleSandbox.Theme.silvertail,"6.0.0.FL","http://localhost/test53");
			createFakeSandbox("Breeze",FiddleSandbox.Theme.breeze,"6.0.0","http://localhost/test6");
			createFakeSandbox("Breeze",FiddleSandbox.Theme.breeze,"3.6.4","http://localhost/test7");
		}
	}
	
	private void createFakeSandbox(String name,FiddleSandbox.Theme theme,String version,String path){
		FiddleSandbox sandbox = new FiddleSandbox() {

			public String getSrc(String token, Integer ver) {
				return "https://www.google.com";
			}
		};
		sandbox.setLastUpdate(new Date());
		sandbox.setName(name);
		sandbox.setTheme(theme);
		sandbox.setVersion(version);
		sandbox.setPath(path);
		this.addFiddleSandbox(sandbox);
	}

	public FiddleSandbox getFiddleSandbox(String hash) {
		return checkDate(sandboxesByHash.get(hash));

	}
	
	public int getAmount(){
		return sandboxesByHash.size();
	}

	/**
	 * organize by version
	 * @return
	 */
	public List<FiddleSandboxGroup> getFiddleSandboxGroups(){
		
	
		Set<String> keyset = new TreeSet(new VersionComparator());
		keyset.addAll(sandboxesByVersion.keySet());

		List<FiddleSandboxGroup> groups = new ArrayList<FiddleSandboxGroup>();
		
		for(String key:keyset){
			List<FiddleSandbox> sandboxs = new ArrayList<FiddleSandbox>(sandboxesByVersion.get(key));
			
			Collections.sort(sandboxs);
			
			groups.add(new FiddleSandboxGroup(key, sandboxs));
		}
		
		return groups;
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
	
	public void clear(){
		sandboxesByHash.clear();
		sandboxesByVersion.clear();
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

		if(!sandboxesByHash.containsKey(sandbox.getHash())){
			getVersionList(sandbox.getZKVersion()).add(sandbox);
		}

		sandboxesByHash.put(sandbox.getHash(), sandbox);
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
