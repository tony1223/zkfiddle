package org.zkoss.fiddle.admin.composer;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import org.zkoss.fiddle.core.utils.UrlUtil;
import org.zkoss.fiddle.manager.FiddleSandboxManager;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.json.JSONObject;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SandboxListComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = -8939520006607012735L;

	private Listbox sampleboxs;

	public void updateList(){
		FiddleSandboxManager sandboxManager = (FiddleSandboxManager) SpringUtil
		.getBean("sandboxManager");
		Collection<FiddleSandbox> sandboxs = sandboxManager
				.listFiddleInstances().values();
		sampleboxs.setModel(new ListModelList(sandboxs));
	}

	public void onClick$clearAll(Event e){
		FiddleSandboxManager sandboxManager = (FiddleSandboxManager) SpringUtil
		.getBean("sandboxManager");
		sandboxManager.clear();
		updateList();
	}
	
	public void onClick$ping(Event e){
		
		@SuppressWarnings("unchecked")
		Set<Listitem> items = (Set<Listitem>) sampleboxs.getSelectedItems();
		
		for(Listitem item:items){
			FiddleSandbox sandbox = (FiddleSandbox) item.getValue();
			try {
				JSONObject obj = UrlUtil.fetchJSON(new URL(sandbox.getPath()+"pong"));
				if(obj.containsKey("work") && Boolean.TRUE.equals(obj.get("work"))){
					sandbox.setStatus(FiddleSandbox.Status.pong);
				}
			} catch (Exception e1) {
				sandbox.setStatus(FiddleSandbox.Status.lost);
			}
			
		}
		sampleboxs.setModel(sampleboxs.getModel());
		
	}
	
	public void onClick$refresh(Event e){
		updateList();
	}
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		updateList();

		sampleboxs.setItemRenderer(new ListitemRenderer() {
			@SuppressWarnings("deprecation")
			public void render(Listitem item, Object data) throws Exception {
				final FiddleSandbox sandbox = (FiddleSandbox) data;
				item.setValue(data);
				int rowIndex = item.getParent().getChildren().indexOf(item);

				item.appendChild(new Listcell(""+rowIndex));

				item.appendChild(new Listcell(sandbox.getZKVersion()));
				item.appendChild(new Listcell(sandbox.getPath()));


				item.appendChild(new Listcell(sandbox.getLastUpdate()
						.toLocaleString()));

				item.appendChild(new Listcell(sandbox.getStatus().toString()));
				
				{
					Button remove = new Button("Remove");
					remove.addEventListener(Events.ON_CLICK,
							new EventListener() {
								public void onEvent(Event event)
										throws Exception {
									FiddleSandboxManager sandboxManager = (FiddleSandboxManager) SpringUtil
											.getBean("sandboxManager");
									sandboxManager.removeSandbox(sandbox
											.getHash());
									updateList();
								}
							});

					Listcell cell = new Listcell();
					cell.appendChild(remove);
					item.appendChild(cell);
				}

			}
		});

	}

}
