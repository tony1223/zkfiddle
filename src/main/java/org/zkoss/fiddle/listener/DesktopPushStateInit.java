package org.zkoss.fiddle.listener;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopInit;

public class DesktopPushStateInit implements DesktopInit {

	public void init(final Desktop desktop, Object request) throws Exception {
		desktop.addListener(new AuService() {

			public boolean service(AuRequest request, boolean everError) {

				String cmd = request.getCommand();
				if (FiddleConstant.AU_PUSH_STATE_INIT.equals(cmd)) {
					desktop.setAttribute(FiddleConstant.DESKTOP_ATTR_PUSH_STATE, Boolean.TRUE);
					return true;
				}/* TODO check this later.
				 else if (FiddleConstant.AU_PUSH_STATE_CHANGE.equals(cmd)) {
					String url = (String) request.getData().get("url") ;
					
					if(url == null) throw new IllegalStateException("url shouldn't be null;");

					String deskurl = (String) desktop.getAttribute(FiddleConstant.DESKTOP_ATTR_CURRENT_STATE_URI);
					
					if(deskurl == null || !url.equals(deskurl)){
						desktop.setAttribute(FiddleConstant.DESKTOP_ATTR_CURRENT_STATE_URI,url);
					}
					
					return true;
				}
				*/
				return false;
			}
		});
	}

}
