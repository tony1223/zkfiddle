package org.zkoss.fiddle.composer;

import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.InsertResourceEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;

public class SourceCodeEditorInsertComposer extends GenericForwardComposer {

	private Combobox type;
	private Label extension;
	private Textbox fileName;
	private static final String[] DATA = new String[] { "zul", "java", "javascript", "html", "css", "media" };
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		type.setModel(new ListModelList(DATA));
	}

	/**
	 * we use desktop level event queue.
	 */
	private EventQueue sourceQueue = EventQueues.lookup(FiddleEventQueues.SOURCE, true);

	public void onClick$insert(Event e) {
		String fileNameStr = fileName.getValue();
		if("".equals(fileNameStr)){
			alert("file name cannot be empty!");
			return;
		}
		int typeVal = type.getSelectedIndex();
		
		String selected = type.getSelectedItem().getLabel();
		String fileNameVal = fileNameStr + getTypeExtension(selected);
		
		sourceQueue.publish(new InsertResourceEvent(null, fileNameVal, typeVal));
		
		type.setSelectedIndex(0);
		fileName.setText("");
	}
	public void onCreate(){
		type.setSelectedIndex(0);
	}
	
	public void onSelect$type(){
		String tpStr = type.getValue();
		extension.setValue(getTypeExtension(tpStr));
	}
	
	private static String getTypeExtension(String tpStr){
		String extension = null;
		if("javascript".equals(tpStr)){
			extension = ".js";
		}else if("media".equals(tpStr)){
			extension = "";
		}else{
			extension = "."+tpStr;
		}
		return extension;
	}
}
