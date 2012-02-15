package org.zkoss.fiddle.component.renderer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.fiddle.model.Resource;


public class SourceTabRendererFactory {
	
	private static final Map<Integer,ISourceTabRenderer> renderer; 
	static{
		Map<Integer,ISourceTabRenderer> map = new HashMap<Integer,ISourceTabRenderer>(); 
		map.put(Resource.TYPE_JAVA, new JavaSourceTabRenderer());
		map.put(Resource.TYPE_IMAGE, new ImageSourceTabRenderer());
		
		renderer = Collections.unmodifiableMap(map);
		
	}
	private static final ISourceTabRenderer defRenderer = new SourceTabRenderer();
	
	public static ISourceTabRenderer getRenderer(int type){
		if(renderer.containsKey(type))
			return renderer.get(type);
		else 
			return defRenderer;
	}
	
}
