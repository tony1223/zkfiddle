package org.zkoss.fiddle.composer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.codemirror.CodeEditor;
import org.zkoss.fiddle.component.Texttab;
import org.zkoss.fiddle.composer.event.SaveEvent;
import org.zkoss.fiddle.composer.event.SourceInsertEvent;
import org.zkoss.fiddle.dao.CaseDaoImpl;
import org.zkoss.fiddle.dao.ResourceDaoImpl;
import org.zkoss.fiddle.dao.api.ICaseDao;
import org.zkoss.fiddle.dao.api.IResourceDao;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.fiddle.util.CRCCaseIDEncoder;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;


public class SourceCodeEditorComposer extends GenericForwardComposer{
	
	public List<Resource> resources;
	
	private Tabs sourcetabs;
	private Tabpanels sourcetabpanels;
	private Combobox type;
	private Textbox fileName;
	private ICase c = null ; 
	private Button saveBtn;
	
	/**
	 * we use desktop level event queue.
	 */
	private EventQueue queue = EventQueues.lookup("source",true);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		resources = new ArrayList<Resource>();
		
		c = null ; //new Case();
		String caseToken = (String) requestScope.get("token");
		String version = (String) requestScope.get("ver");
		if(caseToken != null){
			ICaseDao caseDao = new CaseDaoImpl();
			
			try{
				c = caseDao.findCaseByToken(caseToken,version == null ? null : Integer.parseInt(version));
				if(c == null){
					Executions.sendRedirect("/");
				}
				saveBtn.setLabel("Update");
			}catch(IllegalArgumentException e){ //means caseId is not a valid string 
				//TODO wrote a logger here.
				c = null;
			}
		}
		
		if( c == null || c.getId() == null){ // new case!
			resources.addAll(getDefaultResources());
		}else{ 
			IResourceDao dao = new ResourceDaoImpl(); 
			List<Resource> dbResources =  dao.listByCase(c.getId());
			for(Resource r:dbResources){
				//we clone it , since we will create a new resource instead 
				// of updating old one..
				Resource resource = r.clone();
				resource.setId(null);
				resource.setCaseId(null);
				resource.setCreateDate(new Date());
				resources.add(resource);
			}
		}
	
		applyResources(resources);
		
		queue.subscribe(new EventListener() {
			public void onEvent(Event event) throws Exception {
				if( event instanceof SourceInsertEvent){
					SourceInsertEvent insertEvent = (SourceInsertEvent) event;
					Resource ir = new Resource(insertEvent.getType(),insertEvent.getFileName(),null);
					resources.add(ir);
					insertResource(ir);
				}else if(event instanceof SaveEvent){
					SaveEvent saveEvt = (SaveEvent) event;
					saveCase(resources,saveEvt.isFork());
				}
			}
		});
		
	}
	
	private void saveCase(List<Resource> resources , boolean fork){
		Case nc= new Case();
		nc.setCreateDate(new Date());
		
		ICaseDao caseDao = new CaseDaoImpl();
		if( c == null || fork) { // Create a brand new case
			nc.setVersion(1);
			nc.setToken(CRCCaseIDEncoder.getInstance().encode(new Date().getTime()));
			
			if( c != null){ // fork
				nc.setFromId( c.getId() );
			}
			
		}else{ 
			nc.setToken(c.getToken());
			nc.setThread( c.getThread());
			nc.setVersion(caseDao.getLastVersionByToken(c.getToken()) + 1);
		}
		
		/* TODO: review this and use transcation to speed up and 
		 * 		be sure it's built as a unit. 
		 */
		
		caseDao.saveOrUdate(nc);
		
		if( c == null || fork){ // A brand new case
			// TonyQ:
			// we have to set the thread information after we get the id.
			// TODO:check if we could use trigger or something 
			//		to handle this in DB. currently we have to live with it.
			nc.setThread(nc.getId());
			caseDao.saveOrUdate(nc);
		}
		
		IResourceDao resourceDao= new ResourceDaoImpl();
		for(Resource resource : resources){
			resource.setId(null);
			resource.setCaseId(nc.getId());
			resourceDao.saveOrUdate(resource);
		}
		Executions.getCurrent().sendRedirect("/"+nc.getToken()+ ( nc.getVersion() != 1 ? "/"+nc.getVersion():""));
	}
	
	private List<Resource> getDefaultResources(){
		List resources = new ArrayList<IResource>();
		resources.add(new Resource(Resource.TYPE_ZUL,"index.zul","<zk>\n  <window>hello world1 </window>\n</zk>"));
//		resources.add(new Resource(Resource.TYPE_JS,"index.js","function hello(){alert('hello');}"));
//		resources.add(new Resource(Resource.TYPE_CSS,"index.css",".hello{ \n color:red; \n }"));
//		resources.add(new Resource(Resource.TYPE_JAVA,"org/zkoss/fiddle/Index.java",
//				"package org.zkoss.fiddle; \n public class Index{ \n \n} "));
//		resources.add(new Resource(Resource.TYPE_HTML,"index_files/index.html",
//			"<html>\n  <head>\n    <title>Hello</title>\n  </head>\n  <body>\n    hello\n  </body>\n</html>"));
		
		return resources;
	}
	
	public void onClick$insert(Event e){
		int typeVal = type.getSelectedIndex();
		String fileNameVal = fileName.getValue();

		queue.publish(new SourceInsertEvent(null, null, fileNameVal, typeVal));
	}
	
	
	private void insertResource(final IResource resource){
		if(sourcetabs == null || sourcetabpanels == null){
			throw new IllegalStateException("sourcetabpanels/sourcetabs is not ready !!\n"+
					" do you call this after doAfterCompose "+
					"(and be sure you called super.doAfterCompose()) or using in wrong page? ");
		}
		
		//TODO using swifttab to replace this if possible
		/* creating tab */
		Texttab texttab = new Texttab(resource.getTypeName());
		texttab.setAttribute("model", resource);
		
		final Textbox box = new Textbox(resource.getName());
		box.setSclass("tab-textbox");
		box.setConstraint("no empty");
		box.setInplace(true);
		texttab.appendChild(box);
		texttab.setClosable(true);
		
		box.addEventListener("onChange",new EventListener() {
			public void onEvent(Event event) throws Exception {
				resource.setName(box.getValue());
			}
		});
		
		texttab.addEventListener("onClose",new EventListener() {
			public void onEvent(Event event) throws Exception {
				Texttab tab = (Texttab)event.getTarget();
				IResource ir = (IResource) tab.getAttribute("model");

				
				//remove the resource
				int k = -1;
				for(int i = 0 ; i < resources.size();++i){
					if(resources.get(i) == ir){
						k = i;
						break;
					}
				}
				if(k != -1)
					resources.remove(k);

			}
		});
		
		sourcetabs.appendChild(texttab);
		
		
		/* creating Tabpanel */
		Tabpanel sourcepanel = new Tabpanel();
		
		CodeEditor ce = new CodeEditor();
		ce.setMode(resource.getTypeMode());
		ce.setValue(resource.getContent());
		ce.setHeight("400px");
		ce.setWidth("auto");
		ce.setAttribute("model", resource);
		
		ce.addEventListener("onChange",new EventListener() {
			public void onEvent(Event event) throws Exception {
				InputEvent inpEvt = (InputEvent) event;
				CodeEditor ce = (CodeEditor) event.getTarget();
				Resource r = (Resource) ce.getAttribute("model");
				r.setContent(inpEvt.getValue());
			}
		});
		
		sourcepanel.appendChild(ce);
		
		sourcetabpanels.appendChild(sourcepanel);
	}
	private void applyResources(List<Resource> resources){
		for(IResource resource:resources){
			insertResource(resource);
		}
	}
}
