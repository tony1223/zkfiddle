package org.zkoss.fiddle.composer;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.OptionBuilder;
import org.zkoss.fiddle.OptionRenderer;
import org.zkoss.fiddle.Selectbox;
import org.zkoss.fiddle.composer.event.ResourceChangedEvent;
import org.zkoss.fiddle.composer.event.TopStateChangeEvent;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventListener;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleSourceEventQueue;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleTopNavigationEventQueue;
import org.zkoss.fiddle.event.OptionSelectedEvent;
import org.zkoss.fiddle.hyperlink.Hyperlink;
import org.zkoss.fiddle.manager.FiddleSandboxManager;
import org.zkoss.fiddle.util.BrowserStateUtil;
import org.zkoss.fiddle.util.CookieUtil;
import org.zkoss.fiddle.util.FiddleConfig;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.fiddle.visualmodel.FiddleSandboxGroup;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.api.Button;

public class TopNavigationComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = 6098592769427716897L;

	public enum State {
		Tag, New, Saved, User
	}

	private Div views;

	private Selectbox sandboxes;

	private Hyperlink logolink;

	private Button viewBtn;

	private Button forkBtn;

	private Button saveBtn;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		CookieUtil.removeCookie(FiddleConstant.COOKIE_ATTR_SANDBOX_INDEX);
		logolink.setHref(FiddleConfig.getHostName());

		initSandbox();

		// Note we are not handling any state here,
		// it's decided from outside who send the event to here.
		FiddleTopNavigationEventQueue.lookup().subscribe(
				new FiddleEventListener<TopStateChangeEvent>(TopStateChangeEvent.class, self) {

					public void onFiddleEvent(TopStateChangeEvent evt) throws Exception {
						updateStatus(evt.getState());
					}
				});

		FiddleSourceEventQueue.lookup().subscribeResourceChanged(
				new FiddleEventListener<ResourceChangedEvent>(ResourceChangedEvent.class, viewBtn) {

					public void onFiddleEvent(ResourceChangedEvent evt) {
						viewBtn.setLabel("*Run");
					};
				});

	}

	public void onClick$logolink() {
		BrowserStateUtil.goHome();
	}

	public void updateStatus(State state) {

		if (state == State.New) {
			saveBtn.setLabel("Save");
			saveBtn.setImage("/img/database_save.png");
			forkBtn.setVisible(false);
			views.setStyle("visibility:visible");
		} else if (state == State.Saved) {
			saveBtn.setLabel("Update");
			saveBtn.setImage("/img/arrow_refresh.png");
			forkBtn.setVisible(true);
			views.setStyle("visibility:visible");
			views.setVisible(true);
		} else if (state == State.Tag || state == State.User) {
			views.setStyle("visibility:hidden");
		}
	}

	private void initSandbox() {
		FiddleSandboxManager sandboxManager = (FiddleSandboxManager) SpringUtil.getBean("sandboxManager");

		if (sandboxManager.getAmount() == 0) {
			sandboxes.setModel(new ListModelList(new String[] { "No available Sandbox now" }));
			viewBtn.setDisabled(true);
		} else {
			sandboxes.setOptionRenderer(new OptionRenderer<FiddleSandboxGroup>() {
				public void render(Selectbox box,OptionBuilder builder, FiddleSandboxGroup group) throws Exception {
					
					if(group.getName().indexOf(".FL") != -1){
						group.setName(group.getName().replaceAll("\\.FL", " Freshly"));
					}
					builder.appendOptionGroup(group.getName(), group.getSandboxs(),
						new OptionRenderer<FiddleSandbox>() {
							public void render(Selectbox box,OptionBuilder builder, FiddleSandbox sandbox) throws Exception {
								builder.appendOption(sandbox.getName() +"["+sandbox.getZKVersion()+"]" ,
										sandbox.getHash(),sandbox);
							};
						});

				}
			});

			sandboxes.setModel(new ListModelList(sandboxManager.getFiddleSandboxGroups()));
		}

		String sandboxHash = CookieUtil.getCookie(FiddleConstant.COOKIE_ATTR_SANDBOX_HASH);
		if (sandboxHash != null) {
			ListModel lm = sandboxes.getModel();
			// when no instance
			if (lm.getSize() > 0 && lm.getElementAt(0) instanceof String) {
				sandboxes.setSelectedIndex(0);
				return;
			}

			sandboxes.doSelectedValue(sandboxHash);
		} 
	}
	
	public void onSelect$sandboxes(OptionSelectedEvent evt) {
		FiddleSandbox sandbox = (FiddleSandbox) evt.getValue();
		CookieUtil.setCookie(FiddleConstant.COOKIE_ATTR_SANDBOX_HASH, sandbox.getHash(), CookieUtil.AGE_ONE_YEAR);
	}

	public void onClick$viewBtn() {
		FiddleSandbox sandbox = null;
		if(sandboxes.getOptionSize() != 0 ){
			if (sandboxes.getSelectedIndex() == -1)
				sandbox = (FiddleSandbox) sandboxes.getOptionValue(0);
			else
				sandbox = (FiddleSandbox) sandboxes.getOptionValue(sandboxes.getSelectedIndex());
	
			FiddleSourceEventQueue.lookup().firePreparingShowResult(sandbox);
		}else{
			alert("Currently no available sandboxes.");
		}
	}

	public void onClick$saveBtn() {
		FiddleSourceEventQueue.lookup().fireResourceSaved(false);
	}

	public void onClick$forkBtn() {
		FiddleSourceEventQueue.lookup().fireResourceSaved(true);
	}

}
