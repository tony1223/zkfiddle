package org.zkoss.fiddle.composer;

import java.util.Collection;
import java.util.TreeSet;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.composer.event.FiddleEventListener;
import org.zkoss.fiddle.composer.event.FiddleSourceEventQueue;
import org.zkoss.fiddle.composer.event.ResourceChangedEvent;
import org.zkoss.fiddle.manager.FiddleSandboxManager;
import org.zkoss.fiddle.util.CookieUtil;
import org.zkoss.fiddle.util.FiddleConfig;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.api.Button;
import org.zkoss.zul.event.ZulEvents;

public class TopNavigationComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = 6098592769427716897L;


	private Combobox sandboxes = null;

	private Button viewBtn;

	private Button forkBtn;

	private Button saveBtn;


	public String getHostName(){
		return FiddleConfig.getHostName();
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		boolean newcase = requestScope.get(FiddleConstant.REQUEST_ATTR_CASE) == null;
		if (!newcase) { //existing case
			saveBtn.setLabel("Update");
			saveBtn.setImage("/img/arrow_refresh.png");
		}else{
			forkBtn.setVisible(false);
		}

		initSandbox();

		FiddleSourceEventQueue.lookup().subscribeResourceChanged(
			new FiddleEventListener<ResourceChangedEvent>(ResourceChangedEvent.class) {
				public void onFiddleEvent(ResourceChangedEvent evt) {
					viewBtn.setLabel("*Run");
				};
			}
		);

	}

	private void initSandbox(){
		FiddleSandboxManager sandboxManager = (FiddleSandboxManager) SpringUtil.getBean("sandboxManager");
		Collection<FiddleSandbox> acounts = sandboxManager.listFiddleInstances().values();

		if (acounts.size() == 0) {
			sandboxes.setModel(new ListModelList(new String[] { "No available Sandbox now" }));
			viewBtn.setDisabled(true);
		} else {
			sandboxes.setItemRenderer(new ComboitemRenderer() {
				public void render(Comboitem comboItem, Object data) throws Exception {
					FiddleSandbox inst = (FiddleSandbox) data;
					comboItem.setImage("/img/server-network.png");
					comboItem.setLabel(inst.getName() + " ["+inst.getZKVersion()+"]");
					comboItem.setValue(data);
				}
			});

			TreeSet<FiddleSandbox> tree = new TreeSet<FiddleSandbox>(sandboxManager.listFiddleInstances().values());
			sandboxes.setModel(new ListModelList(tree));
		}
		sandboxes.addEventListener(ZulEvents.ON_AFTER_RENDER, new EventListener() {

			public void onEvent(Event event) throws Exception {
				String inst = CookieUtil.getCookie("inst");
				if (inst != null) {
					ListModel lm = sandboxes.getModel();
					//when no instance
					if(lm.getSize() > 0 && lm.getElementAt(0) instanceof String){
						sandboxes.setSelectedIndex(0);
						return;
					}
					{// check last index first to speed it up
						String indstr = CookieUtil.getCookie("ind");
						int ind = indstr == null ? -1 : Integer.parseInt(indstr);
						if (ind != -1) {
							if(ind < lm.getSize() ){
								FiddleSandbox sandbox = (FiddleSandbox) lm.getElementAt(ind);
								if (inst.equals(sandbox.getHash())) {
									sandboxes.setSelectedIndex(ind);
									return;
								}
							}
						}
					}

					for (int i = 0; i < lm.getSize(); ++i) {
						FiddleSandbox sandbox = (FiddleSandbox) lm.getElementAt(i);
						if (inst.equals(sandbox.getHash())) {
							// update index
							CookieUtil.setCookie("ind", String.valueOf(i), CookieUtil.AGE_ONE_YEAR);
							sandboxes.setSelectedIndex(i);
							return;
						}
					}
					sandboxes.setSelectedIndex(0);
				} else {
					sandboxes.setSelectedIndex(0);
				}
			}
		});
	}

	public void onChange$instances(){
		FiddleSandbox inst = (FiddleSandbox) sandboxes.getSelectedItem().getValue();
		CookieUtil.setCookie("inst",inst.getHash(),CookieUtil.AGE_ONE_YEAR);
		CookieUtil.setCookie("ind",String.valueOf(sandboxes.getSelectedIndex()),CookieUtil.AGE_ONE_YEAR);
	}

	public void onClick$viewBtn() {
		FiddleSandbox inst = null;
		if (sandboxes.getSelectedIndex() == -1)
			inst = (FiddleSandbox) sandboxes.getItemAtIndex(0).getValue();
		else
			inst = (FiddleSandbox) sandboxes.getSelectedItem().getValue();
		
		FiddleSourceEventQueue.lookup().firePreparingShowResult(inst);
	}

	public void onClick$saveBtn() {
		FiddleSourceEventQueue.lookup().fireResourceSaved(false);
	}

	public void onClick$forkBtn() {
		FiddleSourceEventQueue.lookup().fireResourceSaved(true);
	}
}
