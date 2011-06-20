package org.zkoss.fiddle.composer;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.ShowResultEvent;
import org.zkoss.fiddle.model.FiddleSandbox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.A;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.api.Window;

public class ViewResultComposer extends GenericForwardComposer {

	private Textbox directly;

	private Iframe content;

	private Window viewEditor;

	private Label zkver;
	
	private Label msg;
	
	private A directlyLink;
	
	private Div directLinkContainer;

	/**
	 * we use desktop level event queue.
	 */
	private EventQueue queue = EventQueues.lookup(FiddleEventQueues.SHOW_RESULT, true);

	private String hostpath;

	private String getHostpath(){
		if (hostpath == null) {
			HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
			StringBuffer hostName = new StringBuffer(request.getServerName());
			if (request.getLocalPort() != 80) {
				hostName.append(":" + request.getLocalPort());
			}
			if ("".equals(request.getContextPath())) {
				hostName.append("/" + request.getContextPath());
			} else {
				hostName.append("/");
			}
			hostpath = "http://" + hostName.toString() ;
		}
		
		return hostpath;
	}
	
	public void doAfterCompose(final Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		//save the host name in member field. (it's coming from FiddleDispatcherFilter )
		hostpath = (String) requestScope.get("hostName");
		
		queue.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if (event instanceof ShowResultEvent) {
					ShowResultEvent evt = (ShowResultEvent) event;
					FiddleSandbox inst = evt.getInstance();

					zkver.setValue(inst.getZKVersion());
					viewEditor.setTitle("Running sandbox:" + inst.getName());

					
					if(evt.getCase().getVersion() != 0){
						String tokenpath = evt.getCase().getCaseUrl( inst.getZKVersion());
						directly.setText( getHostpath() + "view/" + tokenpath	+ "?run=" + inst.getHash());
						directlyLink.setHref( getHostpath() + "direct/" + tokenpath	+ "?run=" + inst.getHash());
						directLinkContainer.setVisible(true);
					}else{
						directLinkContainer.setVisible(false);
					}
					
					msg.setValue(""); //force it doing the update job 
					msg.setValue("loading...");
					content.setSrc(inst.getPath() + evt.getCase().getToken() + "/" + evt.getCase().getVersion());
					viewEditor.doModal();
				}
			}
		});
	}
	
	public void onClose$viewEditor(ForwardEvent e) {
		content.setSrc("");
	}
}
