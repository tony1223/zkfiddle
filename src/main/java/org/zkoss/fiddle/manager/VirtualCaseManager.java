package org.zkoss.fiddle.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.fiddle.visualmodel.VirtualCase;

/**
 * This case dao is only for virtual case , which is temporary used and will be
 * removed one it's used .
 *
 * (We use it in memory layer.)
 *
 * To prevent memory leak , we make this queue contains only 100 items. (we
 * assume concurrent is less then 50 in this case.)
 *
 * @author tony
 *
 */
public class VirtualCaseManager {

	/* Test function
	public static void main(String[] args) {
		final VirtualCaseDaoImpl imp = new VirtualCaseDaoImpl();

		Thread sl = new Thread(){
			@Override
			public void run() {
				for (int i = 0; i < 300; ++i) {

					Case c = new Case();
					c.setToken("token1" + i);
					imp.save(new VirtualCase(c, null));
					if(i %2 == 0 ){
						imp.find("token1"+i).getCase();
					}
					try {
						Thread.sleep(200);
					} catch (Exception ex) {

					}
				}
			}
		};
		sl.start();
		sl = new Thread(){
			@Override
			public void run() {
				for (int i = 0; i < 300; ++i) {

					Case c = new Case();
					c.setToken("token2" + i);
					imp.save(new VirtualCase(c, null));
					if(i %2 == 0 ){
						imp.find("token2"+i).getCase();
					}
					try {
						Thread.sleep(500);
					} catch (Exception ex) {

					}
				}
			}
		};
		sl.start();
		sl = new Thread(){
			@Override
			public void run() {
				for (int i = 0; i < 1000; ++i) {

					Case c = new Case();
					c.setToken("token3" + i);
					imp.save(new VirtualCase(c, null));
					if(i %2 == 0 ){
						imp.find("token3"+i).getCase();
					}
					try {
						Thread.sleep(10);
					} catch (Exception ex) {

					}
				}
			}
		};
		sl.start();
	}
	 */

	private static Map<String, VirtualCase> casemap = new HashMap<String, VirtualCase>();
	private static VirtualCaseManager _instance;

	private VirtualCaseManager() {
	}

	public void save(VirtualCase m) {

		casemap.put(m.getCase().getToken(), m);
		if (casemap.size() > 100) {
			clean(m.getCreateDate());
		}
	}

	private void clean(Date d) {
		Collection<VirtualCase> vcs = new ArrayList<VirtualCase>(casemap.values());
		for (VirtualCase vc : vcs) {
			if (vc.getCreateDate().getTime() < d.getTime()) {
				casemap.remove(vc.getCase().getToken());
			}
		}

	}

	public boolean contains(String token) {
		return casemap.containsKey(token);
	}
	public VirtualCase find(String token) {
		VirtualCase vc = casemap.get(token);
		casemap.remove(token);
		return vc;
	}

	public static VirtualCaseManager getInstance(){
		if(_instance == null ) _instance= new VirtualCaseManager();
		return _instance;
	}

}
