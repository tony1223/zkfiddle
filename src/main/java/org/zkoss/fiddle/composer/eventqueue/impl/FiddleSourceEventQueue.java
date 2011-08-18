package org.zkoss.fiddle.composer.eventqueue.impl;

import org.zkoss.fiddle.composer.event.InsertResourceEvent;
import org.zkoss.fiddle.composer.event.PreparingShowResultEvent;
import org.zkoss.fiddle.composer.event.ResourceChangedEvent;
import org.zkoss.fiddle.composer.event.SaveCaseEvent;
import org.zkoss.fiddle.composer.event.ShowResultEvent;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventListener;
import org.zkoss.fiddle.composer.eventqueue.FiddleEventQueue;
import org.zkoss.fiddle.model.Resource;
import org.zkoss.fiddle.model.api.ICase;
import org.zkoss.fiddle.visualmodel.FiddleSandbox;
import org.zkoss.zk.ui.event.Event;

public class FiddleSourceEventQueue extends FiddleEventQueue{

	private static final String SOURCE = "source";

	public static FiddleSourceEventQueue lookup() {
		return new FiddleSourceEventQueue();
	}

	public FiddleSourceEventQueue() {
		super(SOURCE);
	}

	public void subscribeShowResult(FiddleEventListener<? extends Event> evtListener) {
		subscribe(evtListener);
	}

	public void fireResourceInsert(String fileName, int type,Resource resource) {
		publish(new InsertResourceEvent(resource));
	}

	// TODO review if we need Resource remove trigger.
	public void fireResourceChanged(Resource resource, ResourceChangedEvent.Type type) {
		publish(new ResourceChangedEvent(null, resource, type));
	}

	public void subscribeResourceChanged(FiddleEventListener<? extends Event> evtListener) {
		subscribe(evtListener);
	}

	public void subscribeResourceCreated(FiddleEventListener<? extends Event> evtListener) {
		subscribe(evtListener);
	}

	public void fireResourceSaved(boolean folk) {
		publish(new SaveCaseEvent(folk));
	}

	public void firePreparingShowResult(FiddleSandbox sandbox) {
		publish(new PreparingShowResultEvent(sandbox));
	}

	public void fireShowResult(ICase cas, FiddleSandbox sandbox) {
		publish(new ShowResultEvent(cas, sandbox));
	}


	public void subscribeResourceSaved(FiddleEventListener<? extends Event> evtListener) {
		subscribe(evtListener);
	}
}
