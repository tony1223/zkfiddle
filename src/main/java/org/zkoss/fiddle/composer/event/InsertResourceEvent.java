package org.zkoss.fiddle.composer.event;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;


public class InsertResourceEvent extends Event{

	private String folder ;
	private String fileName ;
	private int type;
	
	public InsertResourceEvent(String name,Component target,String pfolder,String pfileName,int ptype){
		super(name,target,null);
		type = ptype;
		folder = pfolder;
		fileName = pfileName;
	}
	
	public InsertResourceEvent(Component target,String pfolder,String pfileName,int ptype){
		this(FiddleEvents.ON_RESOURCE_INSERT,target,pfolder,pfileName,ptype);
	}

	public String getFolder() {
		return folder;
	}

	
	public void setFolder(String folder) {
		this.folder = folder;
	}

	
	public String getFileName() {
		return fileName;
	}

	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
	public int getType() {
		return type;
	}

	
	public void setType(int type) {
		this.type = type;
	}
	
}
