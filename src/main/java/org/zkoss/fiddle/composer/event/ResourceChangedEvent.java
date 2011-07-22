package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.model.Resource;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

public class ResourceChangedEvent extends Event {

	/**
	 *
	 */
	private static final long serialVersionUID = 8569590747949787104L;

	private Resource resource;

	public enum Type{
		Created,Modified,Removed
	}
	
	private Type type;
	
	public ResourceChangedEvent(String name, Component target, Resource resource,Type type) {
		super(name, target, null);
		this.resource = resource;
		this.type = type;
	}

	public ResourceChangedEvent(Component target, Resource resource,Type type) {
		this(FiddleEvents.ON_RESOURCE_CHANGE, target, resource,type);
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	
	public Type getType() {
		return type;
	}

	
	public void setType(Type type) {
		this.type = type;
	}

}
