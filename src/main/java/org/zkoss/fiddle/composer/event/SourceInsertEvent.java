package org.zkoss.fiddle.composer.event;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;


public class SourceInsertEvent extends Event{

	private String folder ;
	private String fileName ;
	private int type;
	
	public SourceInsertEvent(String name,Component target,String pfolder,String pfileName,int ptype){
		super(name,target,null);
		type = ptype;
		folder = pfolder;
		fileName = pfileName;
	}
	
	public SourceInsertEvent(Component target,String pfolder,String pfileName,int ptype){
		this(FiddleEvents.ON_SOURCE_INSERT,target,pfolder,pfileName,ptype);
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
