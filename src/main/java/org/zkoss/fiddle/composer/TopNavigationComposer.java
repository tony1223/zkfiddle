package org.zkoss.fiddle.composer;

import org.zkoss.fiddle.composer.event.SaveEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;


public class TopNavigationComposer extends GenericForwardComposer{
	

	/**
	 * we use desktop level event queue.
	 */
	private EventQueue queue = EventQueues.lookup("source",true);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
	}
	
	public void onClick$saveBtn(){
		queue.publish(new SaveEvent());
	}

	public void onClick$forkBtn(){
		queue.publish(new SaveEvent(true));
	}
}
