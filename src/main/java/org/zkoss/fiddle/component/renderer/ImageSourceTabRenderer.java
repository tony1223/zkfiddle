package org.zkoss.fiddle.component.renderer;

import org.zkoss.fiddle.composer.event.ResourceChangedEvent.Type;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleSourceEventQueue;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;

public class ImageSourceTabRenderer extends SourceTabRenderer {

	protected Tabpanel renderTabpanel(final Resource resource) {

		Tabpanel sourcepanel = new Tabpanel();

		Hlayout hlayout = new Hlayout();
		hlayout.setStyle("margin-top:20px");
		final Label label = new Label("Image url:");
		hlayout.appendChild(label);
		
		final Textbox txtURL = new Textbox();
		txtURL.setValue(resource.getContent());
		txtURL.setWidth("500px");
		txtURL.setConstraint("no empty");
		hlayout.appendChild(txtURL);
		txtURL.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(txtURL.isValid()){
					if(event instanceof InputEvent){
						String value = ((InputEvent) event).getValue();
						resource.setContent(value);
						FiddleSourceEventQueue.lookup().
							fireResourceChanged(resource,Type.Modified);
					}
				}
			}
		});
		sourcepanel.appendChild(hlayout);
		
		{
			sourcepanel.appendChild(new Separator());
		}
		{
			Label lbl = new Label("Enter a image url ,and you have to make sure you have the right to use the image.");
			sourcepanel.appendChild(lbl);
		}
		
		return sourcepanel;		
	}

}
