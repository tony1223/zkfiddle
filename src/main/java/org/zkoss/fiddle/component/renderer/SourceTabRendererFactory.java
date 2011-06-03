package org.zkoss.fiddle.component.renderer;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.fiddle.model.api.IResource;


public class SourceTabRendererFactory {
	
	private static Map<Integer,ISourceTabRenderer> renderer = 
		new HashMap<Integer,ISourceTabRenderer>(); 
	private static ISourceTabRenderer defRenderer = null;
	
	static{
		renderer.put(IResource.TYPE_JAVA,new JavaTabRenderer());
		defRenderer = new SourceTabRenderer();
	}
	
	
	public static ISourceTabRenderer getRenderer(int type){
		if(renderer.containsKey(type))
			return renderer.get(type);
		else 
			return defRenderer;
	}
	
}
