package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.model.Resource;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;


public class InsertResourceEvent extends Event{

	public InsertResourceEvent( Resource resource){		
		super(FiddleEvents.ON_RESOURCE_INSERT, (Component)null, resource);
	}
	
	public Resource getResource(){
		return (Resource) this.getData();
	}
}
