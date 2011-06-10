package info.pppc.pcom.system.container.internal.component;

import info.pppc.base.system.Invocation;
import info.pppc.base.system.InvocationException;
import info.pppc.base.system.Proxy;
import info.pppc.base.system.Result;
import info.pppc.pcom.system.model.component.IEventEmitter;

/**
 * The event emitter provides the default implementation of 
 * event emitters for instances.
 * 
 * @author Mac
 */
public class EventEmitter extends Proxy implements IEventEmitter {

	/**
	 * Creates a new event emitter that is not connected with
	 * any instance.
	 */
	public EventEmitter() {
		super();
	}

	/**
	 * Emitts the specified event to the connected instance in
	 * a synchronous fashion.
	 * 
	 * @param type The type of the event that should be emitted.
	 * @param data The data object of the event.
	 * @throws InvocationException Thrown by the 
	 */
	public void emitEvent(String type, Object data) throws InvocationException {
		Object[] params = new Object[] { type, data };
		Invocation invocation = proxyCreateSynchronous(null, params);
		Result result = proxyInvokeSynchronous(invocation);
		Throwable t = result.getException();
		if (t != null) {
			if (t instanceof InvocationException) {
				throw (InvocationException)t;
			} else if (t instanceof RuntimeException) {
				throw (RuntimeException)t;
			} else {
				throw new InvocationException("Received an unknown exception " + t.getClass() + ".");
			}
		}
	}

}
