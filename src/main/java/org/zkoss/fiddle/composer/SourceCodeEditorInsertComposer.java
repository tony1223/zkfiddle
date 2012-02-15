package org.zkoss.fiddle.composer;

import org.zkoss.fiddle.composer.eventqueue.impl.FiddleSourceEventQueue;
import org.zkoss.fiddle.core.utils.ResourceFactory;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;

public class SourceCodeEditorInsertComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = -316305359195204898L;

	private Combobox type;
	private Label extension;
	private Textbox fileName;
	private static final String[] DATA = new String[] { "zul", "java", "html",
			"js", "js.dsp", "css", "css.dsp", "image" };

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		type.setModel(new ListModelList(DATA));
	}

	/**
	 * we use desktop level event queue.
	 */
	// private EventQueue sourceQueue =
	// EventQueues.lookup(FiddleEventQueues.SOURCE, true);

	public void onClick$insert(Event e) {
		String fileNameStr = fileName.getValue();
		if ("".equals(fileNameStr)) {
			alert("file name cannot be empty!");
			return;
		}
		String selected = type.getSelectedItem().getLabel();
		int typeVal = getType(selected);
		String fileNameVal = fileNameStr + getTypeExtension(selected);

		Resource resource = ResourceFactory.getDefaultResource(typeVal,
				fileNameVal);
		FiddleSourceEventQueue.lookup().fireResourceInsert(fileNameVal,
				typeVal, resource);

		type.setSelectedIndex(0);
		extension.setValue(getTypeExtension(type.getValue()));
		fileName.setText("test");
		self.setVisible(false);
	}

	public void onCreate() {
		type.setSelectedIndex(0);
	}

	public void onSelect$type() {
		String tpStr = type.getValue();
		extension.setValue(getTypeExtension(tpStr));
	}
	private static int getType(String tpStr) {
		
		if("zul".equals(tpStr)){
			return Resource.TYPE_ZUL;
		}else if("java".equals(tpStr)){
			return Resource.TYPE_JAVA;
		}else if("html".equals(tpStr)){
			return Resource.TYPE_HTML;
		}else if("js".equals(tpStr) || "js.dsp".equals(tpStr)){
			return Resource.TYPE_JS;
		}else if("css".equals(tpStr) || "css.dsp".equals(tpStr)){
			return Resource.TYPE_CSS;
		}else if("image".equals(tpStr)){
			return Resource.TYPE_IMAGE;
		}

		return Resource.TYPE_HTML;
	}
	private static String getTypeExtension(String tpStr) {
		if("image".equals(tpStr)){
			return ".jpg";
		}
		return "." + tpStr;
	}
}
