package org.zkoss.fiddle.component;

import org.zkoss.zul.Window;

public class ClosableWindow extends Window{

	public void onClose() {
		this.setVisible(false);
	}
}