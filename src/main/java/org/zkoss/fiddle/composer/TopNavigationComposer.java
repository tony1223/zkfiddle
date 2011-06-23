package org.zkoss.fiddle.composer;

import java.util.Collection;
import java.util.TreeSet;

import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.FiddleEvents;
import org.zkoss.fiddle.composer.event.SaveEvent;
import org.zkoss.fiddle.composer.event.ShowResultEvent;
import org.zkoss.fiddle.composer.event.SourceChangedEvent;
import org.zkoss.fiddle.manager.FiddleSandboxManager;
import org.zkoss.fiddle.model.FiddleSandbox;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.api.Button;
import org.zkoss.zul.event.ZulEvents;

public class TopNavigationComposer extends GenericForwardComposer {

	/**
	 * we use desktop level event queue.
	 */
	private EventQueue sourceQueue = EventQueues.lookup(FiddleEventQueues.SOURCE, true);

	private Combobox instances = null;

	private Button viewBtn;

	private Button forkBtn;

	private Button saveBtn;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);


		boolean newcase = requestScope.get("__case") == null; 
		if (!newcase) { //existing case
			saveBtn.setLabel("Update");
			saveBtn.setImage("/img/arrow_refresh.png");
		}else{
			forkBtn.setVisible(false);
		}

		
		FiddleSandboxManager sandboxManager = (FiddleSandboxManager) SpringUtil.getBean("sandboxManager");
		Collection<FiddleSandbox> acounts = sandboxManager.listFiddleInstances().values();

		if (acounts.size() == 0) {
			instances.setModel(new ListModelList(new String[] { "No available Sandbox now" }));
			viewBtn.setDisabled(true);
		} else {
			instances.setItemRenderer(new ComboitemRenderer() {
				public void render(Comboitem item, Object data) throws Exception {
					FiddleSandbox inst = (FiddleSandbox) data;
					item.setLabel(inst.getName() + "["+inst.getZKVersion()+"]");
					item.setValue(data);
				}
			});
			
			TreeSet tree = new TreeSet(sandboxManager.listFiddleInstances().values());
			instances.setModel(new ListModelList(tree));
		}
		instances.addEventListener(ZulEvents.ON_AFTER_RENDER, new EventListener() {
			public void onEvent(Event event) throws Exception {
				instances.setSelectedIndex(0);
			}
		});
		
		sourceQueue.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event instanceof SourceChangedEvent){
					viewBtn.setLabel("*Run");
				}
			}
		});
		
	}
	
	public void onClick$viewBtn() {
		FiddleSandbox inst = null;
		if (instances.getSelectedIndex() == -1)
			inst = (FiddleSandbox) instances.getItemAtIndex(0).getValue();
		else
			inst = (FiddleSandbox) instances.getSelectedItem().getValue();

		sourceQueue.publish(new ShowResultEvent(FiddleEvents.ON_SOURCE_SHOW_RESULT , null, inst));
	}

	public void onClick$saveBtn() {
		sourceQueue.publish(new SaveEvent());
	}

	public void onClick$forkBtn() {
		sourceQueue.publish(new SaveEvent(true));
	}
}
