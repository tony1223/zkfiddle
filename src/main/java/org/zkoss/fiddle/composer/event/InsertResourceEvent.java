package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;


public class InsertResourceEvent extends Event{

	public InsertResourceEvent( IResource resource){		
		super(FiddleEvents.ON_RESOURCE_INSERT, (Component)null, resource);
	}
	
	public IResource getResource(){
		return (IResource) this.getData();
	}
}
