package org.zkoss.fiddle.composer.eventqueue;

import org.zkoss.fiddle.composer.event.InsertResourceEvent;
import org.zkoss.fiddle.composer.event.PreparingShowResultEvent;
import org.zkoss.fiddle.composer.event.ResourceChangedEvent;
import org.zkoss.fiddle.composer.event.SaveCaseEvent;
import org.zkoss.fiddle.composer.event.ShowResultEvent;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.zk.ui.event.Event;

public class FiddleSourceEventQueue {

	private FiddleEventQueue queue ;
	private static final String SOURCE = "source";

	public static FiddleSourceEventQueue lookup() {
		return new FiddleSourceEventQueue();
	}

	public FiddleSourceEventQueue() {
		queue = new FiddleEventQueue(SOURCE);
	}

	public void subscribe(FiddleEventListener<? extends Event> evtListener) {
		queue.subscribe(evtListener);
	}

	public void subscribeShowResult(FiddleEventListener<? extends Event> evtListener) {
		queue.subscribe(evtListener);
	}

	public void fireResourceInsert(String fileName, int type,Resource resource) {
		queue.publish(new InsertResourceEvent(resource));
	}

	// TODO review if we need Resource remove trigger.
	public void fireResourceChanged(Resource resource, ResourceChangedEvent.Type type) {
		queue.publish(new ResourceChangedEvent(null, resource, type));
	}

	public void subscribeResourceChanged(FiddleEventListener<? extends Event> evtListener) {
		queue.subscribe(evtListener);
	}

	public void subscribeResourceCreated(FiddleEventListener<? extends Event> evtListener) {
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


	public void subscribeResourceSaved(FiddleEventListener<? extends Event> evtListener) {
		queue.subscribe(evtListener);
	}
}
