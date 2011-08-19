package org.zkoss.fiddle.composer;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Include;

public class ContentComposer extends GenericForwardComposer {

	private Include contentInclude;

	private String currentState;

	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
		currentState = (String) Executions.getCurrent().getAttribute(FiddleConstant.REQUEST_ATTR_CONTENT_PAGE);
		return super.doBeforeCompose(page, parent, compInfo);
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}

	public String getContentURL() {
		return FiddleConstant.REQUEST_VALUE_PAGE_TYPE_TAG.equals(currentState) ? "tag.zul" : "sourceedtior.zul";
	}
}
