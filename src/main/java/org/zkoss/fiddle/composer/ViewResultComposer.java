package org.zkoss.fiddle.composer;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.ShowResultEvent;
import org.zkoss.fiddle.model.Instance;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.api.Window;

public class ViewResultComposer extends GenericForwardComposer {

	private Textbox directly;

	private Iframe content;

	private Window viewEditor;
	private Label zkver;
	/**
	 * we use desktop level event queue.
	 */
	private EventQueue queue = EventQueues.lookup(FiddleEventQueues.SHOW_RESULT, true);

	@Override
	public void doAfterCompose(final Component comp) throws Exception {
		super.doAfterCompose(comp);

		queue.subscribe(new EventListener() {

			public void onEvent(Event event) throws Exception {
				if (event instanceof ShowResultEvent) {
					ShowResultEvent evt = (ShowResultEvent) event;
					viewEditor.setVisible(true);

					HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
					StringBuffer hostName = new StringBuffer(request.getServerName());
					if(request.getLocalPort() != 80){
						hostName.append(":"+request.getLocalPort());
					}
					if("".equals(request.getContextPath())){
						hostName.append("/"+request.getContextPath());
					}else{
						hostName.append("/");
					}
					
					Instance inst = evt.getInstance();
					
					zkver.setValue(inst.getVersion());
					
					viewEditor.setTitle("Runnign sandbox:"+inst.getName());
					directly.setText("http://"+hostName.toString()+"view/" + evt.getToken() + "/" + 
							evt.getVersion()+"/"+inst.getHash());
					
					content.setSrc(inst.getPath()+evt.getToken()+"/"+evt.getVersion());
//					content.setSrc("http://localhost:8080/");
//					content.setSrc(inst.getPath()+"dbn96j/7");
					
					
					viewEditor.doModal();
//					 Clients.evalJavaScript("window.open('" +inst.getPath()+"dbn96j/7"+ "')");
				}
			}
		});

	}
	
	public void onClose$viewEditor(Event e){
		viewEditor.setVisible(false);
		e.stopPropagation();
	}
}
