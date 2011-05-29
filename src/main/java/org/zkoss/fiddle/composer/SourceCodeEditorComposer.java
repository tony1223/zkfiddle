package org.zkoss.fiddle.composer;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.codemirror.CodeEditor;
import org.zkoss.fiddle.component.Texttab;
import org.zkoss.fiddle.composer.event.SourceInsertEvent;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;


public class SourceCodeEditorComposer extends GenericForwardComposer{
	
	public List<IResource> resources;
	
	private Tabs sourcetabs;
	private Tabpanels sourcetabpanels;
	private Combobox type;
	private Textbox fileName;
	
	/**
	 * we use desktop level event queue.
	 */
	private EventQueue queue = EventQueues.lookup("source",true);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		resources = new ArrayList<IResource>();
		resources.addAll(getDefaultResources());
	
		applyResources(resources);
		
		queue.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if( event instanceof SourceInsertEvent){
					SourceInsertEvent insertEvent = (SourceInsertEvent) event;
					IResource ir = new Resource(insertEvent.getType(),insertEvent.getFileName(),null);
					resources.add(ir);
					insertResource(ir);
				}
			}
		});
		
	}
	
	private List<Resource> getDefaultResources(){
		List resources = new ArrayList<IResource>();
		resources.add(new Resource(Resource.TYPE_ZUL,"index.zul","<zk>\n  <window>hello world1 </window>\n</zk>"));
		resources.add(new Resource(Resource.TYPE_JS,"index.js","function hello(){alert('hello');}"));
		resources.add(new Resource(Resource.TYPE_CSS,"index.css",".hello{ \n color:red; \n }"));
		resources.add(new Resource(Resource.TYPE_JAVA,"org/zkoss/fiddle/Index.java",
				"package org.zkoss.fiddle; \n public class Index{ \n \n} "));

		resources.add(new Resource(Resource.TYPE_HTML,"index_files/index.html",
			"<html>\n  <head>\n    <title>Hello</title>\n  </head>\n  <body>\n    hello\n  </body>\n</html>"));
		
		return resources;
	}
	
	public void onClick$insert(Event e){
		int typeVal = type.getSelectedIndex();
		String fileNameVal = fileName.getValue();

		queue.publish(new SourceInsertEvent(null, null, fileNameVal, typeVal));
	}
	
	
	private void insertResource(IResource resource){
		if(sourcetabs == null || sourcetabpanels == null){
			throw new IllegalStateException("sourcetabpanels/sourcetabs is not ready !!\n"+
					" do you call this after doAfterCompose "+
					"(and be sure you called super.doAfterCompose()) or using in wrong page? ");
		}
		
		/* creating tab */
		Texttab texttab = new Texttab(resource.getTypeName());
		texttab.setAttribute("model", resource);
		
		Textbox box = new Textbox(resource.getName());
		box.setWidth("200px");
		box.setConstraint("no empty");
		texttab.appendChild(box);
		
		sourcetabs.appendChild(texttab);
		
		
		/* creating Tabpanel */
		Tabpanel sourcepanel = new Tabpanel();
		
		CodeEditor ce = new CodeEditor();
		ce.setMode(resource.getTypeMode());
		ce.setValue(resource.getContent());
		ce.setHeight("400px");
		ce.setWidth("auto");
		ce.setAttribute("model", resource);
		sourcepanel.appendChild(ce);
		
		sourcetabpanels.appendChild(sourcepanel);
	}
	private void applyResources(List<IResource> resources){
		for(IResource resource:resources){
			insertResource(resource);
		}
	}
}
