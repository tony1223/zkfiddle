package org.zkoss.fiddle.composer;

import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.InsertResourceEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;

public class SourceCodeEditorInsertComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = -316305359195204898L;

	private Combobox type;

	private Textbox fileName;

	/**
	 * we use desktop level event queue.
	 */
	private EventQueue sourceQueue = EventQueues.lookup(FiddleEventQueues.SOURCE, true);

	public void onClick$insert(Event e) {
		int typeVal = type.getSelectedIndex();

		String selected = type.getSelectedItem().getLabel();
		String fileNameVal = fileName.getValue() + ("javascript".equals(selected) ? ".js" : "." +selected);

		sourceQueue.publish(new InsertResourceEvent(null, null, fileNameVal, typeVal));

		type.setSelectedIndex(0);
		fileName.setText("");
	}

}
