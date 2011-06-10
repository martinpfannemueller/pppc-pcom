package info.pppc.pcom.system.container.internal.component;

import info.pppc.base.system.IInvocationHandler;
import info.pppc.base.system.ISession;
import info.pppc.base.system.Invocation;
import info.pppc.base.system.InvocationBroker;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.ObjectID;
import info.pppc.base.system.ObjectRegistry;
import info.pppc.pcom.system.model.component.IEvent;
import info.pppc.pcom.system.model.component.IEventCollector;
import info.pppc.pcom.system.model.component.IEventListener;

import java.util.Vector;

/**
 * The event collector implements the default event collector
 * for instances.
 * 
 * @author Mac
 */
public class EventCollector implements IEventCollector, IInvocationHandler {

	/**
	 * The identifier under which the event collector is registered.
	 */
	private ObjectID identifier;
	
	/**
	 * The listeners that are registered at the event collector.
	 */
	private Vector listeners = new Vector();
	
	/**
	 * Creates a new event collector and registers it at 
	 * the local broker.
	 */
	public EventCollector() {
		InvocationBroker broker = InvocationBroker.getInstance();
		ObjectRegistry registry = broker.getObjectRegistry();
		identifier = registry.registerObject(this);
	}
	
	/**
	 * Returns the identifier under which the collector is registered 
	 * or null if the collector is not registered at all. 
	 * 
	 * @return The id of the collector at the local object registry.
	 */
	public ObjectID getIdentifier() {
		return identifier;
	}

	/**
	 * Adds an event listener to the set of listeners registered for
	 * events. If the listener is null, the call will be ignored 
	 * silently.
	 * 
	 * @param listener The listener that should be registered.
	 */
	public synchronized void addEventListener(IEventListener listener) {
		if (listener != null) {
			listeners.addElement(listener);
		}
	}

	/**
	 * Removes a previously registered event listener from the
	 * set of event listeners and returns whether the listener
	 * has been found. If the listener is null, the request will
	 * be ignored.
	 * 
	 * @param listener The event listener to remove.
	 * @return True, if the listener has been removed. False if
	 * 	the listener was not present and thus, could not be
	 * 	removed. 
	 */
	public synchronized boolean removeEventListener(IEventListener listener) {
		if (listener != null) {
			return listeners.removeElement(listener);
		} else {
			return false;
		}
	}

	/**
	 * Returns the event listeners that have been registered so far.
	 * 
	 * @return An array of event listeners that have been registered
	 * 	at this collector.
	 */
	public synchronized IEventListener[] getEventListeners() {
		IEventListener[] result = new IEventListener[listeners.size()];
		for (int i = listeners.size() - 1; i >= 0; i--) {
			result[i] = (IEventListener)listeners.elementAt(i);
		}
		return result;
	}
	
	/**
	 * Called whenever an invocation is incoming.
	 * 
	 * @param invocation The invocation that has been received.
	 * @param session The session that has been received.
	 */
	public synchronized void invoke(Invocation invocation, ISession session) {
		final Object[] params = invocation.getArguments();
		// perform compliance checks
		if (params == null || params.length != 2 
				|| params[0] == null || !(params[0] instanceof String)) {
			// throw remote exception and ignore event
			invocation.setException(new InvocationException("Illegal event structure."));
		} else {
			// deliver event to registered listeners
			IEvent event = new IEvent() {
				public Object getData() { return params[1]; };
				public IEventCollector getSource() { return EventCollector.this; };
				public String getType() { return (String)params[0]; };
			};
			for (int i = 0; i < listeners.size(); i++) {
				IEventListener listener = (IEventListener)listeners.elementAt(i);
				listener.receivedEvent(event);
			}
		}
	}
	
	/**
	 * Removes the event collector from the object registry and
	 * unsets the identifier of the collector. It is safe to
	 * call this method multiple times sequentially.
	 */
	public void release() {
		if (identifier != null) {
			InvocationBroker broker = InvocationBroker.getInstance();
			ObjectRegistry registry = broker.getObjectRegistry();
			registry.removeObject(identifier);
			identifier = null;
		}	
	}

}
