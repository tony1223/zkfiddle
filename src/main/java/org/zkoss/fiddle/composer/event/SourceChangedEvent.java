package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;


public class SourceChangedEvent extends Event{

	private IResource resource;
	public SourceChangedEvent(String name,Component target,IResource resource){
		super(name,target,null);
		this.resource = resource;
	}

	public SourceChangedEvent(Component target,IResource resource){
		this(FiddleEvents.ON_SOURCE_CHANGE,target,resource);
	}

	
	public IResource getResource() {
		return resource;
	}

	
	public void setResource(IResource resource) {
		this.resource = resource;
	}
	
}
