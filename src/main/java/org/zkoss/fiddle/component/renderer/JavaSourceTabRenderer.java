package org.zkoss.fiddle.component.renderer;

import java.util.regex.Pattern;

import org.zkoss.fiddle.composer.event.FiddleEventQueues;
import org.zkoss.fiddle.composer.event.SourceChangedEvent;
import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;

public class JavaSourceTabRenderer extends SourceTabRenderer {

	public static final String PACKAGE_TOKEN = "$pkg$";
	public static final String PACKAGE_TOKEN_ESCAPE = "\\$pkg\\$";
	public static final String PACKAGE_PREFIX = "fiddle.";
	
	private Pattern packageRule = Pattern.compile("^[a-zA-Z_\\$][\\w\\$]*(?:\\.[a-zA-Z_\\$][\\w\\$]*)*$");

	protected Tabpanel renderTabpanel(final IResource resource) {

		Tabpanel sourcepanel = new Tabpanel();

		Hlayout hlayout = new Hlayout();
		Label label = new Label("package");
		label.setSclass("c-like-keyword");
		hlayout.appendChild(label);
		 
		Label label2 = new Label(PACKAGE_PREFIX + PACKAGE_TOKEN);
		label2.setTooltiptext("since we have to prevent package conflict for every version ,"
				+ " so we use org.$$fiddle$$ as your class package by default.");
		hlayout.appendChild(label2);
		
		//we use desktop scope , so it's ok to lookup every time.
		final EventQueue sourceQueue = EventQueues.lookup(FiddleEventQueues.SOURCE);
		
		Textbox txtPkg = new Textbox();
		txtPkg.setInplace(true);
		txtPkg.setValue(resource.getPkg());
		txtPkg.setTooltiptext("(This is optional) you could set a package name "
				+ " and it should start with a dot ,"
				+ " but you don't have to modify the java filename , we will handle this.");

		txtPkg.setConstraint(new Constraint() {
			public void validate(Component comp, Object value) throws WrongValueException {
				if (!packageRule.matcher( PACKAGE_PREFIX + PACKAGE_TOKEN + (String) value).matches()) {
					throw new WrongValueException("Not a valid package name");
				}
			}
		});
		txtPkg.addEventListener(Events.ON_CHANGE, new EventListener() {
			public void onEvent(Event event) throws Exception {
				resource.setPkg(((InputEvent) event).getValue());
				sourceQueue.publish(new SourceChangedEvent(null,resource));
			}
		});

		hlayout.appendChild(txtPkg);
		
		hlayout.appendChild(new Label(";"));
		
		sourcepanel.appendChild(hlayout);
		sourcepanel.appendChild(prepareCodeEditor(resource));

		return sourcepanel;
	}

}
