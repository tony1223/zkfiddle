package org.zkoss.fiddle.component.renderer;

import org.zkoss.codemirror.CodeEditor;
import org.zkoss.fiddle.component.Texttab;
import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.FiddleEvents;
import org.zkoss.fiddle.composer.event.ResourceChangedEvent;
import org.zkoss.fiddle.composer.event.SourceRemoveEvent;
import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.api.Tab;
import org.zkoss.zul.api.Tabpanels;
import org.zkoss.zul.api.Tabs;

public class SourceTabRenderer implements ISourceTabRenderer {

	protected CodeEditor prepareCodeEditor(final IResource resource) {
		CodeEditor ce = new CodeEditor();
		ce.setMode(resource.getTypeMode());
		ce.setValue(resource.getContent());
		ce.setHeight("400px");
		ce.setWidth("auto");

		//we use desktop scope , so it's ok to lookup every time.
		final EventQueue sourceQueue = EventQueues.lookup(FiddleEventQueues.SOURCE);
		
		ce.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event event) throws Exception {

				if(event instanceof InputEvent){
					InputEvent inpEvt = (InputEvent) event;
					resource.setContent(inpEvt.getValue());
					sourceQueue.publish(new ResourceChangedEvent(null,resource));
				}
			}
		});
		return ce;
	}

	protected Tab renderTab(final IResource resource) {
		final Texttab texttab = new Texttab(resource.getTypeName());
		texttab.setAttribute("model", resource);

		final Textbox box = new Textbox(resource.getName());
		box.setSclass("tab-textbox");
		box.setConstraint("no empty");
		box.setInplace(true);
		if(!resource.isCanDelete()){
			box.setReadonly(true);
			box.setTooltiptext("you can't edit this file name since it's a must have.");
		}

		texttab.appendChild(box);
		texttab.setClosable(resource.isCanDelete());

		final EventQueue eventQueue = EventQueues.lookup(FiddleEventQueues.SOURCE);
		
		eventQueue.subscribe(new EventListener() {

			public void onEvent(Event event) throws Exception {
				if (event instanceof ResourceChangedEvent) {
					if (((ResourceChangedEvent) event).getResource() == resource) {
						texttab.setLabel("*" + resource.getTypeName());
					}
				}
			}
		});
		
		box.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event event) throws Exception {
				resource.setName(box.getValue());
				eventQueue.publish(new ResourceChangedEvent(null,resource));
			}
		});
		
		texttab.addEventListener(Events.ON_CLOSE, new EventListener() {
			public void onEvent(Event event) throws Exception {
				eventQueue.publish(new ResourceChangedEvent(null,resource));
				eventQueue.publish(new SourceRemoveEvent(FiddleEvents.ON_SOURCE_REMOVE, null,resource ));
			}
		});

		return texttab;
	}

	protected Tabpanel renderTabpanel(IResource resource) {
		/* creating Tabpanel */
		Tabpanel sourcepanel = new Tabpanel();
		sourcepanel.appendChild(prepareCodeEditor(resource));
		return sourcepanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zkoss.fiddle.component.renderer.ISourceTabRenderer#appendSourceTab
	 * (org.zkoss.zul.api.Tabs, org.zkoss.zul.api.Tabpanels,
	 * org.zkoss.fiddle.model.api.IResource)
	 */
	public void appendSourceTab(Tabs sourcetabs, Tabpanels sourcetabpanels, final IResource resource) {
		if (sourcetabs == null || sourcetabpanels == null) {
			throw new IllegalStateException("sourcetabpanels/sourcetabs is not ready !!\n"
					+ " do you call this after doAfterCompose "
					+ "(and be sure you called super.doAfterCompose()) or using in wrong page? ");
		}
		sourcetabs.appendChild(renderTab(resource));
		sourcetabpanels.appendChild(renderTabpanel(resource));
	}
}
