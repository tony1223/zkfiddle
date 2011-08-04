package org.zkoss.fiddle.composer.event;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;

@SuppressWarnings("rawtypes")
public abstract class FiddleEventListener<T extends Event> implements
		EventListener {

	private Class accpeted;

	private Component target;

	private EventQueue queue;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(FiddleEventListener.class);

	/**
	 * Class inp must match with T .
	 *
	 * @param inp
	 */
	public FiddleEventListener(Class inp) {
		accpeted = inp;
	}

	public EventQueue getQueue() {
		return queue;
	}

	public void setQueue(EventQueue queue) {
		this.queue = queue;
	}

	public FiddleEventListener(Class inp, Component compScope) {
		accpeted = inp;
		target = compScope;
	}

	@SuppressWarnings("unchecked")
	public void onEvent(Event event) throws Exception {
		if (target != null && target.getDesktop() == null) {
			if (queue == null) {
				if (logger.isEnabledFor(Level.WARN)) {
					logger.warn("FiddleEventListener:"
							+ target.getUuid()
							+ ":"
							+ target.getId()
							+ ":"
							+ event.getName()
							+ ": can't find queue to unsubcribe when the component is detached.");
				}
			}

			if (queue != null) {
				queue.unsubscribe(this);
			}
			return;
		}
		if (accpeted.isInstance(event)) {
			onFiddleEvent((T) event);
		}
	}

	public abstract void onFiddleEvent(T evt) throws Exception;
}
