package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;


public class SourceRemoveEvent extends Event{
	IResource resource ;
	
	public SourceRemoveEvent(String name,Component target,IResource ir){
		super(name,target,null);
		resource =ir;
	}
	
	public IResource getResource() {
		return resource;
	}


	
}
