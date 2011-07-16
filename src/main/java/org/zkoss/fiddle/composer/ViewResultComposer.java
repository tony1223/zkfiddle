package org.zkoss.fiddle.composer;

import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.ShowResultEvent;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.api.Window;

public class ViewResultComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = 7445220094351755044L;

	private Textbox directUrl;

	private Iframe content;

	private Window viewEditor;

	private Label directDesc;
	private Button openNewWindow;


	/**
	 * we use desktop level event queue.
	 */
	private EventQueue queue = EventQueues.lookup(FiddleEventQueues.SHOW_RESULT, true);

	private String hostpath;

	public void doAfterCompose(final Component comp) throws Exception {
		super.doAfterCompose(comp);

		//save the host name in member field. (it's coming from FiddleDispatcherFilter )
		hostpath = (String) requestScope.get("hostName");

		queue.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if (event instanceof ShowResultEvent) {
					ShowResultEvent evt = (ShowResultEvent) event;
					FiddleSandbox inst = evt.getInstance();
					viewEditor.setTitle("Running Sandbox : " + inst.getName() + " @ ZK " + inst.getZKVersion() );


					if(evt.getCase().getVersion() != 0){
						String tokenpath = evt.getCase().getCaseUrl( inst.getZKVersion());
						directUrl.setText( hostpath + "direct/" + tokenpath	+ "?run=" + inst.getHash());
						openNewWindow.setHref( hostpath + "direct/" + tokenpath	+ "?run=" + inst.getHash());
						setDirectVisible(true);
					}else{
						setDirectVisible(false);
					}

					content.setSrc(inst.getSrc(evt.getCase()));
					viewEditor.doModal();
				}
			}
		});
	}

	private void setDirectVisible(boolean show){

		directDesc.setVisible(show);
		openNewWindow.setVisible(show);
		directUrl.setVisible(show);

	}

	public void onClick$closeWindow(ForwardEvent e) {
		viewEditor.setVisible(false);
		content.setSrc("/img/viewresult-loading.gif");
	}
}
