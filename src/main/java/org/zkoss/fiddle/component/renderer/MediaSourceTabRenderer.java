/**MediaSourceTabRenderer.java
 * 2011/7/15
 * 
 */
package org.zkoss.fiddle.component.renderer;

import org.zkoss.fiddle.model.Media;
import org.zkoss.fiddle.model.api.IResource;
import org.zkoss.image.Image;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.api.Tabpanels;
import org.zkoss.zul.api.Tabs;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class MediaSourceTabRenderer  extends SourceTabRenderer {

	private static final int HEIGHT = 400;
	private static final String STR_HEIGHT = HEIGHT + "px";
	
	protected Tabpanel renderTabpanel(final IResource resource) {
		Tabpanel tabpanel = new Tabpanel();
		final Hlayout hl = new Hlayout();
		hl.setHeight(STR_HEIGHT);
		Button uploadBtn = new Button("upload a media");
		uploadBtn.setUpload("true");
		hl.appendChild(uploadBtn);
		uploadBtn.addEventListener("onUpload", new EventListener() {
			org.zkoss.zul.Image image;
			public void onEvent(Event arg0) throws Exception {
				UploadEvent event = (UploadEvent) arg0;
				org.zkoss.util.media.Media media = event.getMedia();
				
				if(image!=null)image.detach();
				if (media instanceof org.zkoss.image.Image) {
					org.zkoss.image.Image imgSrc = (Image) media;
					imgSrc.getWidth();
					image = new org.zkoss.zul.Image();
					int height = imgSrc.getHeight();
					int width = imgSrc.getWidth();
					
					if(height>HEIGHT){
						width = (width * HEIGHT)/height;
						height = HEIGHT;
					}
					image.setHeight(height + "px");
					image.setWidth(width + "px");
					image.setContent((Image) media);
					image.setParent(hl);
				} 
				Media mediaBean = new Media();
				mediaBean.setContent(media.getByteData());
				resource.setMedia(mediaBean);
			}
		});
		tabpanel.appendChild(hl);
		return tabpanel;
	}
	

}
