package org.zkoss.fiddle.composer.event;

import org.zkoss.fiddle.core.utils.ResourceFactory;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;

public class FiddleSourceEventQueue {

	private FiddleEventQueue queue ; 
	private static final String SOURCE = "source";

	public static FiddleSourceEventQueue lookup() {
		return new FiddleSourceEventQueue();
	}

	public FiddleSourceEventQueue() {
		queue = new FiddleEventQueue(SOURCE);
	}

	public void subscribe(FiddleEventListener evtListener) {
		queue.subscribe(evtListener);
	}

	public void subscribeShowResult(FiddleEventListener evtListener) {
		queue.subscribe(evtListener);
	}

	public void fireResourceInsert(String fileName, int type) {
		queue.publish(new InsertResourceEvent(ResourceFactory.getDefaultResource(type, fileName)));
	}

	// TODO review if we need Resource remove trigger.
	public void fireResourceChanged(Resource resource, ResourceChangedEvent.Type type) {
		queue.publish(new ResourceChangedEvent(null, resource, type));
	}

	public void subscribeResourceChanged(FiddleEventListener evtListener) {
		queue.subscribe(evtListener);
	}
	
	public void subscribeResourceCreated(FiddleEventListener evtListener) {
		queue.subscribe(evtListener);
	}

	public void fireResourceSaved(boolean folk) {
		queue.publish(new SaveCaseEvent(folk));
	}
	
	public void firePreparingShowResult(FiddleSandbox sandbox) {
		queue.publish(new PreparingShowResultEvent(sandbox));
	}
	
	public void fireShowResult(ICase cas, FiddleSandbox sandbox) {
		queue.publish(new ShowResultEvent(cas, sandbox));
	}


	public void subscribeResourceSaved(FiddleEventListener evtListener) {
		queue.subscribe(evtListener);
	}
}
