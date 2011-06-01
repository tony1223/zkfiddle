package org.zkoss.fiddle.composer;

import java.util.Collection;

import org.zkoss.fiddle.composer.event.SaveEvent;
import org.zkoss.fiddle.instance.InstanceManager;
import org.zkoss.fiddle.model.Instance;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.api.Button;

public class TopNavigationComposer extends GenericForwardComposer {

	/**
	 * we use desktop level event queue.
	 */
	private EventQueue queue = EventQueues.lookup("source", true);

	private Combobox instances;

	private Button viewBtn;

	private String token;
	private String ver;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		token = (String) requestScope.get("token");
		ver = (String) requestScope.get("ver");
		
		InstanceManager instanceManager = InstanceManager.getInstance();

		Collection<Instance> acounts = instanceManager.listInstances().values();

		if (acounts.size() == 0) {
			instances.setModel(new ListModelList(new String[] { "No available Instance now" }));
			instances.setDisabled(true);
			viewBtn.setDisabled(true);
		} else {
			
			instances.setModel(new ListModelList(instanceManager.listInstances().values()));
			instances.setItemRenderer(new ComboitemRenderer() {

				public void render(Comboitem item, Object data) throws Exception {
					Instance inst = (Instance) data;
					item.setLabel(inst.getName());
					item.setValue(data);
				}
			});
		}

	}

	public void onClick$viewBtn() {
		Instance inst = null;
		if (instances.getSelectedIndex() == -1)
			inst = (Instance) instances.getItemAtIndex(0).getValue();
		else
			inst = (Instance) instances.getSelectedItem().getValue();
		
	 	Clients.evalJavaScript("window.open('"+inst.getPath()+token+"/" + ver+ "')");
	}

	public void onClick$saveBtn() {
		queue.publish(new SaveEvent());
	}

	public void onClick$forkBtn() {
		queue.publish(new SaveEvent(true));
	}
}
