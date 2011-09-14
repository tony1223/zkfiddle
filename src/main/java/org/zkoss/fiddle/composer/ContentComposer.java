package org.zkoss.fiddle.composer;

import org.zkoss.fiddle.FiddleConstant;
import org.zkoss.fiddle.composer.event.URLChangeEvent;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventListener;
import org.zkoss.fiddle.composer.eventqueue.impl.FiddleBrowserStateEventQueue;
import org.zkoss.fiddle.composer.viewmodel.URLData;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.Tag;
import org.zkoss.fiddle.visualmodel.UserVO;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Include;

public class ContentComposer extends GenericForwardComposer {

	/**
	 *
	 */
	private static final long serialVersionUID = 6526163707470169730L;

	private Include contentInclude;

	private String currentState;

	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
		currentState = (String) Executions.getCurrent().getAttribute(FiddleConstant.REQUEST_ATTR_CONTENT_PAGE);
		return super.doBeforeCompose(page, parent, compInfo);
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		FiddleBrowserStateEventQueue queue = FiddleBrowserStateEventQueue.lookup();
		queue.subscribe(new FiddleEventListener<URLChangeEvent>(
				URLChangeEvent.class,self) {

			public void onFiddleEvent(URLChangeEvent evt) throws Exception {
				// only work when updated to a case view.

				URLData data = (URLData) evt.getData();

				if (data == null ){
					throw new IllegalStateException("not expected type");
				}else if(FiddleConstant.URL_DATA_CASE_VIEW.equals(data.getType())) {
					currentState = FiddleConstant.REQUEST_VALUE_PAGE_TYPE_SOURCE;
					Executions.getCurrent().setAttribute(FiddleConstant.REQUEST_ATTR_CASE, data.getData());
				}else if(FiddleConstant.URL_DATA_TAG_VIEW.equals(data.getType())){
					currentState = FiddleConstant.REQUEST_VALUE_PAGE_TYPE_TAG;
					Executions.getCurrent().setAttribute(FiddleConstant.REQUEST_ATTR_TAG, data.getData());
				}else if(FiddleConstant.URL_DATA_USER_VIEW.equals(data.getType())){
					UserVO userVO = (UserVO) data.getData();
					currentState = FiddleConstant.REQUEST_VALUE_PAGE_TYPE_USER;
					Executions.getCurrent().setAttribute(FiddleConstant.REQUEST_ATTR_GUEST, userVO.isGuest());
					Executions.getCurrent().setAttribute(FiddleConstant.REQUEST_ATTR_USERNAME, userVO.getUserName());
				}else{
					throw new IllegalStateException("not expected type");
				}

				contentInclude.setSrc(getContentURL());
			}
		});
	}

	public String getContentURL() {

		if(FiddleConstant.REQUEST_VALUE_PAGE_TYPE_TAG.equals(currentState)){
			return "tag.zul";
		}else if(FiddleConstant.REQUEST_VALUE_PAGE_TYPE_SOURCE.equals(currentState)){
			return "sourceedtior.zul";
		}else{
			return "user.zul";
		}
	}
}
