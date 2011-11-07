package org.zkoss.fiddle.admin.composer;

import java.util.Collection;

import org.zkoss.fiddle.manager.FiddleSandboxManager;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

public class SandboxListComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = -8939520006607012735L;

	private Grid sampleboxs;

	public void updateList(){
		FiddleSandboxManager sandboxManager = (FiddleSandboxManager) SpringUtil
		.getBean("sandboxManager");
		Collection<FiddleSandbox> acounts = sandboxManager
				.listFiddleInstances().values();
		sampleboxs.setModel(new ListModelList(acounts));
	}

	public void onClick$clearAll(Event e){
		FiddleSandboxManager sandboxManager = (FiddleSandboxManager) SpringUtil
		.getBean("sandboxManager");
		sandboxManager.clear();
		updateList();
	}
	
	public void onClick$refresh(Event e){
		updateList();
	}
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		updateList();

		sampleboxs.setRowRenderer(new RowRenderer() {

			@SuppressWarnings("deprecation")

			public void render(Row row, Object data) throws Exception {
				final FiddleSandbox sandbox = (FiddleSandbox) data;

				int rowIndex = row.getParent().getChildren().indexOf(row);

				row.appendChild(new Label(""+rowIndex));

				row.appendChild(new Label(sandbox.getZKVersion()));
				row.appendChild(new Label(sandbox.getPath()));


				row.appendChild(new Label(sandbox.getLastUpdate()
						.toLocaleString()));

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

					row.appendChild(remove);
				}

			}
		});

	}

}
