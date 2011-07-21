package org.zkoss.fiddle.component.renderer;

import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.zul.api.Tabpanels;
import org.zkoss.zul.api.Tabs;


/**
 * A tab renderer means it have to render and handle resource data as well including remove , content ...etc
 * they are factory instances , don't save any information on member field!
 * @author tony
 *
 */
public interface ISourceTabRenderer {

	/**
	 * @param sourcetabs
	 * @param sourcetabpanels
	 * @param resource
	 */
	public void appendSourceTab(Tabs sourcetabs, Tabpanels sourcetabpanels, final Resource resource);

}